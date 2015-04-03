package edu.haverford.mpp.mappingprogressivephiladelphia;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private myArrayAdapter myCardAdapter;

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

        //al = db.getAllOrganizationNames();
        //al = new ArrayList<>();
        allOrgs = db.getAllOrganizations();


        //myCardAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al );
        myCardAdapter = new myArrayAdapter (this, R.layout.item, allOrgs);
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
                        .setTitle("We're running out of organizations to show you!")
                        .setMessage("Go to the map to see all of your organizations and get some more information about them!"
                        + "\n" + "\n" + "Or you can head over to your list of organizations and manage them in a more conventional way.")
                        .setPositiveButton("Organization List", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), OrgListActivity.class);
                                startActivity(intent);
                                /*MyDatabase db = new MyDatabase(SwipePickerActivity.this);
                                allOrgs = db.getAllOrganizations();
                                Collections.shuffle(allOrgs); //We can have a better sort order later, but for now random seems good.
                                myCardAdapter = new ArrayAdapter<PhillyOrg> (SwipePickerActivity.this, R.layout.item, R.id.orgname, allOrgs);
                                flingContainer.setAdapter(myCardAdapter);
                                myCardAdapter.notifyDataSetChanged();*/
                            }
                        })
                        .setNegativeButton("Map", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
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


                Intent intent = new Intent(getApplicationContext(), OrganizationInfoActivity.class);
                intent.putExtra("OrgID", currOrg.getId());
                intent.putExtra("OrgDist", myDist);
                startActivity(intent);


            }
        });

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
                getSwipeHelp();
                break;
            case R.id.update_db:
                updateDatabase();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    public void getSwipeHelp() {
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Welcome to Mapping Progressive Philadelphia!")
                .setMessage("This app is made up of three parts: Swipes, Map, and List." + "\n" + "\n"
                        + "Swipes offers a Tinder-style interface for choosing organizations that " +
                        "you'd like to subscribe to and learn more about. Just swipe the cards left " +
                        "(no thanks!) or right (sign me up!)." + "\n" + "\n" + "Subscribing to an organization " +
                        "simply allows the app to notify you when there's an event going on and places " +
                        "that organization on your personal map of Progressive Philadelphia." + "\n" + "\n" +
                        "Map displays all of the organizations you're subscribed to and allows you to click" +
                        " on a point to get more information about that organization." + "\n" + "\n" + "List is " +
                        "another way to choose organizations to subscribe to, using a more conventional design " +
                        "if Swipes just isn't your style." + "\n" + "\n" + "Click 'Subscribe Now' to get started with " +
                        "Swipes or 'Subscribe Later' to head over to the Map. You can always review this information " +
                        "again by clicking on the Help button in the overflow menu.")
                .setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Subscribe Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }

    public void updateDatabase() {
        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection detected. Please reconnect and try again.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Fetching updated database...", Toast.LENGTH_SHORT).show();
            Firebase.setAndroidContext(this);
            Firebase myFirebaseRef = new Firebase("https://mappp.firebaseio.com/");
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Iterable orgs = snapshot.getChildren();
                    MyDatabase db = new MyDatabase(SwipePickerActivity.this);
                    for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                        Object o = orgs.iterator().next();
                        DataSnapshot org = (DataSnapshot) o;
                        int id = Integer.parseInt(org.getKey());
                        String updated = org.child("Updated").getValue().toString();
                        String name = org.child("Name").getValue().toString();
                        String facebookID = org.child("FacebookID").getValue().toString();
                        String isDeleted = org.child("Is Deleted").getValue().toString();
                        String website = org.child("Website").getValue().toString();
                        String socialIssues = org.child("Social-Issues").getValue().toString();
                        String address = org.child("Address").getValue().toString();
                        String mission = org.child("Mission").getValue().toString();
                        String facebook = org.child("Facebook").getValue().toString();
                        String zipcode = org.child("Zipcode").getValue().toString();
                        String timestamp = org.child("Timestamp").getValue().toString();
                        String twitter = org.child("Twitter").getValue().toString();
                        /*String latitude = org.child("Latitude").getValue().toString();
                        String longitude = org.child("Longitude").getValue().toString();*//*
                        double lat = Double.parseDouble(latitude);
                        double lng =  Double.parseDouble(longitude);*/
                        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAzZPMw_I4GNcfuT4PeDDkp16-PNqiB1YE");
                        try {
                            GeocodingResult[] results = GeocodingApi.geocode(context,
                                    address + " Philadelphia, PA " + zipcode).await();
                            Geometry myGeo = results[0].geometry;
                            double lat = myGeo.location.lat;
                            double lng = myGeo.location.lng;
                            db.updateEntry(id, updated, name, facebookID, isDeleted, website, socialIssues, address, mission, facebook, zipcode, timestamp, twitter, lat, lng);
                            break;
                        } catch (Exception e){e.printStackTrace();
                            db.updateEntry(id, updated, name, facebookID, isDeleted, website, socialIssues, address, mission, facebook, zipcode, timestamp, twitter);

                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError error) {
                }

            });
            Toast.makeText(getApplicationContext(), "Sync complete", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

