package edu.haverford.mpp.mappingprogressivephiladelphia;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SwipePickerActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private ArrayList<String> al;
    private ArrayList<PhillyOrg> allOrgs;
    //private ArrayAdapter<String> myCardAdapter;
    private ArrayAdapter<PhillyOrg> myCardAdapter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    @InjectView(R.id.frame) SwipeFlingAdapterView flingContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //set up google api for location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        ButterKnife.inject(this);

        MyDatabase db = new MyDatabase(this);

        al = db.getAllOrganizationNames();
        //al = new ArrayList<>();
        allOrgs = db.getAllOrganizations();


        //myCardAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al );
        myCardAdapter = new ArrayAdapter<PhillyOrg> (this, R.layout.item, R.id.orgname, allOrgs);
        flingContainer.setAdapter(myCardAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                allOrgs.remove(0);
                myCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //makeToast(SwipePickerActivity.this, "Left!");
                MyDatabase db = new MyDatabase(getApplicationContext()); //for this context
                PhillyOrg currOrg = (PhillyOrg) dataObject;
                db.insertSubNo(currOrg.id);

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                MyDatabase db = new MyDatabase(getApplicationContext());
                PhillyOrg currOrg = (PhillyOrg) dataObject;
                db.insertSubYes(currOrg.id);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                new AlertDialog.Builder(SwipePickerActivity.this)
                        .setTitle("Done")
                        .setMessage("Would you like to look at the organizations again?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MyDatabase db = new MyDatabase(SwipePickerActivity.this);
                                allOrgs = db.getAllOrganizations();
                                Collections.shuffle(allOrgs); //We can have a better sort order later, but for now random seems good.
                                myCardAdapter = new ArrayAdapter<PhillyOrg> (SwipePickerActivity.this, R.layout.item, R.id.orgname, allOrgs);
                                flingContainer.setAdapter(myCardAdapter);
                                myCardAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), OrgListActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //MyDatabase db = new MyDatabase(getApplicationContext());
                PhillyOrg currOrg = (PhillyOrg) dataObject;
                float myDist;
                if (mLastLocation != null) {
                    myDist = currOrg.getLocation().distanceTo(mLastLocation) * (float) 0.000621371;
                }
                else
                    myDist = (float)-1.0;

                makeToast(SwipePickerActivity.this, Float.toString(myDist));

                Intent intent = new Intent(getApplicationContext(), OrganizationInfoActivity.class);
                intent.putExtra("OrgID", currOrg.getId());
                intent.putExtra("OrgDist", myDist);
                startActivity(intent);


            }
        });

    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


    /**
     * Trigger the right event manually.
     */
    @OnClick(R.id.right)
    public void right() {

        flingContainer.getTopCardListener().selectRight();
    }

    /**
     * Trigger the left event manually.
     */
    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //map
        MenuItem map = menu.findItem(R.id.map);
        map.setEnabled(true);
        map.getIcon().setAlpha(255);

        //swipe
        MenuItem swipe = menu.findItem(R.id.swipe);
        swipe.setEnabled(false);
        swipe.getIcon().setAlpha(100);

        //list
        MenuItem list = menu.findItem(R.id.list);
        list.setEnabled(true);
        list.getIcon().setAlpha(255);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                return (true);
            case R.id.list:
                Intent intent = new Intent(getApplicationContext(), OrgListActivity.class);
                startActivity(intent);
                break;
            case R.id.map:
                intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                intent = new Intent(getApplicationContext(), Splash.class);
                SharedPreferences.Editor editor = getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit();
                editor.putBoolean("isFirstRun", true);
                editor.apply();
                startActivity(intent);
                break;
            case R.id.facebook:
                intent = new Intent(getApplicationContext(), Facebook_Login.class);
                startActivity(intent);
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    protected synchronized void buildGoogleApiClient() {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                buildGoogleApiClient();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, REQUEST_RESOLVE_ERROR);
            mResolvingError = true;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}