package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import io.realm.Realm;

/**
 * Created by BrianG on 5/8/2015.
 */
public class SplashActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR = "dialog_error"; // Unique tag for the error dialog fragment
    private boolean mResolvingError = false; // Bool to track whether the app is already resolving an error
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Realm realm;

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences("PREFERENCES",MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
        FacebookSdk.sdkInitialize(getApplicationContext());
        buildGoogleApiClient();
        setContentView(R.layout.splash);
        progressDialog = ProgressDialog.show(SplashActivity.this, "Loading...",
                "Loading PAVE database, please wait...", false, false);
        //Initialize a LoadViewTask object and call the execute() method
        //new LoadViewTask().execute();
        if (isNetworkConnected()) {
            updateDatabase();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("No internet connection detected. Organization database may be out of date. Please connect to internet and try again or continue and manually sync later.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            updateDatabase();
                        }
                    });
            alertDialog.show();
        }
    }

    public void onResume(){

        super.onResume();
    }

    public void updateDatabase() {
        if (isNetworkConnected()) {
            Log.w("updateDatabase", "Beginning DB Update");
            Firebase.setAndroidContext(this);
            Firebase myFirebaseRef = new Firebase("https://mappp.firebaseio.com/");
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Iterable orgs = snapshot.getChildren();
                    MyDatabase db = new MyDatabase(SplashActivity.this);
                    GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBDfoc1GGXv7ZYv4vCzH1cAZJxVGkmT1n0"); // TODO was AIzaSyAzZPMw_I4GNcfuT4PeDDkp16-PNqiB1YE
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
                        realm = Realm.getInstance(getApplicationContext());
                        realm.beginTransaction();
                        OrgEvent event = realm.createObject(OrgEvent.class);
                        event.setorgName(name);
                        event.setFacebookID(facebookID);
                        realm.commitTransaction();
                        double lat = 0;
                        double lng = 0;
                        try {
                            if (!zipcode.equals("0")) {
                                GeocodingResult[] results = GeocodingApi.geocode(context,
                                        address + " Philadelphia, PA " + zipcode).await();
                                Geometry myGeo = results[0].geometry;
                                lat = myGeo.location.lat;
                                lng = myGeo.location.lng;

                                if (address.equals("4722 Baltimore Ave")) {
                                    lat = lat + Math.random()*.0005;
                                    lng = lng + Math.random()*.0005;
                                }

                            } else {
                                lat = 39.952595 + Math.random()*.001; //slight perturbation to be in the center of Philly... but kinda the wrong place.
                                lng = -75.163736 + Math.random()*.001;
                            }
                            //We are calling update here
                            db.updateEntry(id, updated, name, facebookID, isDeleted, website, socialIssues, address, mission, facebook, zipcode, timestamp, twitter, lat, lng);
                        } catch (Exception e) {e.printStackTrace();
                            db.updateEntry(id, updated, name, facebookID, isDeleted, website, socialIssues, address, mission, facebook, zipcode, timestamp, twitter);
                        }
                    }
                    db.close();
                    Log.w("updateDatabase", "Completed DB Update");
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                }


                @Override
                public void onCancelled(FirebaseError error) {
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Sync failed...Try again later", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) { // There are no active networks
            return false;
        } else
            return true;
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
            return; // Already attempting to resolve an error.
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                buildGoogleApiClient(); // There was an error with the resolution intent. Try again.
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, REQUEST_RESOLVE_ERROR); // Show dialog using GooglePlayServicesUtil.getErrorDialog()
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
        progressDialog.dismiss();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}



