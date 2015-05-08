package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SwipePickerActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR = "dialog_error"; // Unique tag for the error dialog fragment
    private boolean mResolvingError = false; // Bool to track whether the app is already resolving an error

    private ArrayList<PhillyOrg> allOrgs;
    private myArrayAdapter myCardAdapter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public int facebookCardCount = 0;
    int numOrgs;

    private Realm realm;


    @InjectView(R.id.frame) SwipeFlingAdapterView flingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(this) // set up google api for location
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        ButterKnife.inject(this);
        MyDatabase db = new MyDatabase(this);
        allOrgs = db.getAllUnSubscribedOrgs();
        numOrgs = allOrgs.size();
        Collections.shuffle(allOrgs); // TODO: order cards by distance from you (see below)
        db.close(); // TODO check closes

        /*
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // TODO: This is where to put Dan's work (found at bottom of class) on making the cards ordered by distance from you


         */
        myCardAdapter = new myArrayAdapter (this, R.layout.item, allOrgs);

        flingContainer.setAdapter(myCardAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                allOrgs.remove(0);
                numOrgs--;
                myCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                facebookCardCount++;
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                boolean isFirstLeft = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstLeft", true);
                boolean checkedForFB = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("checkedForFB", false);

                if (isFirstLeft) {
                    new AlertDialog.Builder(SwipePickerActivity.this, R.style.DialogTheme)
                            .setTitle("Swiped left!")
                            .setMessage("Swiping organizations to the left hides information from those organizations. The organization will appear blue on the map.\n\nYou can change these settings in the List view.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { }
                            })
                            .setIcon(R.drawable.ic_launcher)
                            .setCancelable(false)
                            .show();
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstLeft", false).apply();
                }

                if (facebookCardCount ==5 & !checkedForFB){
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("checkedForFB", true).apply();

                    new AlertDialog.Builder(SwipePickerActivity.this, R.style.DialogTheme)
                            .setTitle("Want Events?")
                            .setMessage("If you log into Facebook, PAVE will automatically include event data from your subscribed organizations. Don't worry though, we won't post anything to Facebook!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getApplicationContext(), FacebookLogin.class);
                                    startActivity(intent);
                                    }
                            })
                            .setIcon(R.drawable.ic_launcher)
                            .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setCancelable(false)
                            .show();

                }

                MyDatabase db = new MyDatabase(getApplicationContext());
                PhillyOrg currOrg = (PhillyOrg) dataObject;
                db.insertSubNo(currOrg.id);
                db.close();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                facebookCardCount++;

                boolean checkedForFB = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("checkedForFB", false);

                boolean isFirstRight = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRight", true);
                if (isFirstRight) {
                    new AlertDialog.Builder(SwipePickerActivity.this, R.style.DialogTheme)
                            .setTitle("Swiped right!")
                            .setMessage("Swiping organizations to the right subscribes you to those organizations. The organization will appear yellow on the map.\n\nYou can change these settings in the List view.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { }
                            })
                            .setIcon(R.drawable.ic_launcher)
                            .setCancelable(false)
                            .show();
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRight", false).apply();
                }

                if (facebookCardCount == 5 & !checkedForFB ){
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("checkedForFB", true).apply();

                    new AlertDialog.Builder(SwipePickerActivity.this, R.style.DialogTheme)
                            .setTitle("Want Events?")
                            .setMessage("If you log into Facebook, PAVE will automatically include event data from your subscribed organizations. Don't worry though, we won't post anything to Facebook!")
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getApplicationContext(), FacebookLogin.class);
                                    startActivity(intent);
                                }
                            })
                            .setIcon(R.drawable.ic_launcher)
                            .setPositiveButton("Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setCancelable(false)
                            .show();
                }

                MyDatabase db = new MyDatabase(getApplicationContext());
                PhillyOrg currOrg = (PhillyOrg) dataObject;
                db.insertSubYes(currOrg.id);
                db.close();
            }


            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (numOrgs == 0){
                    new AlertDialog.Builder(SwipePickerActivity.this)
                            .setTitle("You've looked through all of the organizations!")
                            .setMessage("Go to the map to see all of your organizations and get some more information about them!\n\nOr you can head over to your list of organizations and manage them in a more conventional way.")
                            .setPositiveButton("Organization List", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), ScreenSlideActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Map", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


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

                // custom dialog
                final Dialog dialog = new Dialog(SwipePickerActivity.this);
                dialog.setContentView(R.layout.organization_info);
                dialog.setTitle(currOrg.getGroupName());

                ImageView image = (ImageView)dialog.findViewById(R.id.org_info_pic);
                Picasso.with(SwipePickerActivity.this)
                        .load("https://graph.facebook.com/" + currOrg.getFacebookID() + "/picture?width=99999")
                        .placeholder(R.drawable.default_pic)
                        .into(image);
                //System.out.println(currOrg.getFacebookID()+"FacebookID");
                TextView issue = (TextView)dialog.findViewById(R.id.org_issue);
                issue.append(currOrg.getSocialIssues());

                TextView mission = (TextView)dialog.findViewById(R.id.org_mission);
                mission.append(currOrg.getMission());

                TextView subscribed = (TextView)dialog.findViewById(R.id.org_subscribed);
                if (currOrg.getSubscribed()) {
                    subscribed.append("Yes");
                } else {
                    subscribed.append("No");
                }

                TextView address = (TextView)dialog.findViewById(R.id.org_address);
                address.setText(currOrg.getAddress() + ", Philadelphia, PA " + currOrg.getZipCode());

                TextView event = (TextView)dialog.findViewById(R.id.event);
                event.append("Log into Facebook to see upcoming events");
                Realm realm = Realm.getInstance(getApplicationContext());
                RealmQuery<OrgEvent> query = realm.where(OrgEvent.class);
                query.equalTo("orgName", currOrg.getGroupName());
                RealmResults<OrgEvent> results = query.findAll();
                boolean checkLoggedIntoFB = getSharedPreferences("PREFERENCES", MODE_PRIVATE).getBoolean("isLoggedIntoFB", false);

                if (checkLoggedIntoFB == true) {
                    if (results.get(0).getEventDescription().isEmpty()){
                        event.setText("There are no upcoming events, check back later.");
                    }
                    else{
                        event.setText("Upcoming Event: " + results.get(0).getEventDescription());

                    }
                }
                else{
                    event.setText("Log into Facebook to see upcoming events.");
                }

                TextView distance = (TextView)dialog.findViewById(R.id.my_distance);
                if (myDist == (float)-1.0) {
                    distance.append("Currently Unknown");
                }
                else {
                    String sigDist = String.format("%.1f", myDist);
                    distance.setText(sigDist + " miles from current location");
                }

                Button closeButton = (Button) dialog.findViewById(R.id.closeButton);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final int number = currOrg.getId();

                final Button subButton = (Button) dialog.findViewById(R.id.subButton);
                if (currOrg.getSubscribed()) {
                    subButton.setText("Unsubscribe");
                } else {
                    subButton.setText("Subscribe");
                }
                subButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyDatabase db = new MyDatabase(getApplicationContext());
                        if (subButton.getText().equals("Subscribe")) {
                            db.insertSubYes(number);
                            db.close();
                            flingContainer.getTopCardListener().selectRight();
                        } else {
                            db.insertSubNo(number);
                            db.close();
                            flingContainer.getTopCardListener().selectLeft();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        invalidateOptionsMenu();
        if (numOrgs == 0) {
            MyDatabase db = new MyDatabase(this);
            allOrgs = db.getAllUnSubscribedOrgs();
            numOrgs = allOrgs.size();
            myCardAdapter.notifyDataSetChanged();
            Collections.shuffle(allOrgs); // TODO: order cards by distance from you (see below)
            db.close();
        }
    }

    @OnClick(R.id.right)
    public void right() {
        if (numOrgs > 0) {
            flingContainer.getTopCardListener().selectRight();
        }
    }

    @OnClick(R.id.left)
    public void left() {
        if (numOrgs > 0) {
            flingContainer.getTopCardListener().selectLeft();
        }
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

        //slider
        MenuItem slider = menu.findItem(R.id.slider);
        slider.setEnabled(true);
        slider.getIcon().setAlpha(255);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                return (true);
            case R.id.slider:
                Intent intent = new Intent(getApplicationContext(), ScreenSlideActivity.class);
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
                intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent);
                break;
            case R.id.Facebook:
                intent = new Intent(getApplicationContext(), FacebookLogin.class);
                startActivity(intent);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void getSwipeHelp() {
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Welcome to Philly Activists and Volunteers Exchange (PAVE)!")
                .setMessage(R.string.dialogMessage)
                .setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Subscribe Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
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
        if (mResolvingError) { // Already attempting to resolve an error
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) { // There was an error with the resolution intent. Try again.
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
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


}