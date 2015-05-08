package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MapActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_RESOLVE_ERROR = 1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR = "dialog_error"; // Unique tag for the error dialog fragment
    private boolean mResolvingError = false; // Bool to track whether the app is already resolving an error

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    int x = 1;
    boolean y = (x==1) ? true : false;
    private Realm realm;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private HashMap<Marker, PhillyOrg> OrgMarkerHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Updated Facebook SDK from 3.7 to 4.1
        checkFirstRun();
        setContentView(R.layout.activity_map);
        buildGoogleApiClient();
        //Log.w("TAG", "Play services configured: " + Boolean.toString(isPlayServicesConfigured()));
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            //Log.e("TAG", "Failed to initialize map");
        }
        setUpMapIfNeeded();
        getActionBar().setDisplayHomeAsUpEnabled(false); // necessary to declare false
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.952595, -75.163736), 13)); // Town Center Philadelphia, zoom = 13. Bigger # = more zoomed in
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        realm = Realm.getInstance(this);
        RealmQuery<OrgEvent> query = realm.where(OrgEvent.class);
        RealmResults<OrgEvent> result1 = query.findAll();
        if (result1.size() > 0){
            //Toast.makeText(getApplicationContext(), result1.toString(),
              //      Toast.LENGTH_LONG).show();
        }


    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        if (mMap == null) { // Do a null check to confirm that we have not already instantiated the map
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)) // Try to obtain the map from the SupportMapFragment
                    .getMap();
            if (mMap != null) { // Check if we were successful in obtaining the map
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMap() {

        final Button toggle = (Button) findViewById(R.id.toggle);
        //toggle.setText("Show Subscribed");
        toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (toggle.getText().equals("Show Subscribed")) {
                    toggle.setText("Show Unsubscribed");
                    mMap.clear();
                    setUpMap();
                } else if (toggle.getText().equals("Show Unsubscribed")) {
                    toggle.setText("Show All");
                    mMap.clear();
                    setUpMap();
                } else { // "Show All"
                    toggle.setText("Show Subscribed");
                    mMap.clear();
                    setUpMap();
                }
            }
        });

        final SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MapActivity.this);

        MyDatabase db = new MyDatabase(getApplicationContext());
        PhillyOrg currentOrg = new PhillyOrg();
        ArrayList<PhillyOrg> allOrgs = db.getAllOrganizations();
        Marker currMarker;
        db.close(); // TODO check closes

        OrgMarkerHash = new HashMap<Marker, PhillyOrg>();

        for (int i = 0; i < allOrgs.size(); i++) {
            currentOrg = allOrgs.get(i);

            if (toggle.getText().equals("Show Subscribed")) { // this is at Show All
                if (currentOrg.getSubscribed()) {
                    currMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentOrg.getLatLng())
                            .title(currentOrg.getGroupName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.subscribed))); // color for subscribed markers
                } else {
                    currMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentOrg.getLatLng())
                            .title(currentOrg.getGroupName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.not_subscribed))); // color for unsubscribed markers
                }
                OrgMarkerHash.put(currMarker, currentOrg); // OrgMarkerHash is not null at this point, but it does refill every time we set up the map
            } else if (toggle.getText().equals("Show Unsubscribed")) { // this is show subscribed
                if (currentOrg.getSubscribed()) {
                    currMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentOrg.getLatLng())
                            .title(currentOrg.getGroupName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.subscribed))); // color for subscribed markers
                }
            } else { // this is show unsubscribed
                if (!currentOrg.getSubscribed()) {
                    currMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentOrg.getLatLng())
                            .title(currentOrg.getGroupName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.not_subscribed))); // color for unsubscribed markers
                }
            }
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                System.out.println(OrgMarkerHash.get(marker) == null);
                // vvv This thing may not be an issue anymore vvv //
                // TODO: THE MARKER IS CORRECT, AND AT THE MOMENT WE ONLY MAKE THE HASH ONCE BUT APPARENTLY OrgMarkerHash.get(marker) IS NULL
                System.out.println(marker.getTitle()); // correct marker
                final PhillyOrg currOrg = OrgMarkerHash.get(marker);

                float myDist;

                if (mLastLocation == null) {
                    myDist = (float)-1.0;
                } else {
                    //System.out.println(currOrg == null); // After we click on banner, then go to new org, currOrg is null
                    //System.out.println(currOrg.getLocation());
                    myDist = currOrg.getLocation().distanceTo(mLastLocation) * (float) 0.000621371;
                    // vvv This thing may not be an issue anymore vvv //
                    // TODO NullPointerException here sometimes
                }

                // custom dialog
                final Dialog dialog = new Dialog(MapActivity.this);
                dialog.setContentView(R.layout.organization_info);
                dialog.setTitle(currOrg.getGroupName());

                ImageView image = (ImageView)dialog.findViewById(R.id.org_info_pic);
                Picasso.with(MapActivity.this)
                        .load("https://graph.facebook.com/" + currOrg.getFacebookID() + "/picture?width=99999")
                        .placeholder(R.drawable.default_pic)
                        .into(image);

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

                TextView distance = (TextView)dialog.findViewById(R.id.my_distance);
                //float myDist = intent.getFloatExtra("OrgDist", (float)-1.0);
                if (myDist == (float)-1.0){
                    distance.append("Currently Unknown");
                }
                else{
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
                            currOrg.setSubscribed(true);
                            subButton.setText("Unsubscribe");
                        } else {
                            db.insertSubNo(number);
                            currOrg.setSubscribed(false);
                            subButton.setText("Subscribe");
                        }
                        db.close();
                        dialog.dismiss();
                        mMap.clear();
                        setUpMap();
                    }
                });

                dialog.show();

            }

        });
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        // map
        MenuItem map = menu.findItem(R.id.map);
        map.setEnabled(false);
        map.getIcon().setAlpha(100);

        // swipe
        MenuItem swipe = menu.findItem(R.id.swipe);
        swipe.setEnabled(true);
        swipe.getIcon().setAlpha(255);

        // list
        MenuItem list = menu.findItem(R.id.list);
        list.setEnabled(true);
        list.getIcon().setAlpha(255);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu); // Inflate the menu; this adds items to the action bar if it is present
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // handle action bar selections
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.swipe:
                Intent intent = new Intent(getApplicationContext(), SwipePickerActivity.class);
                startActivity(intent);
                break;
            case R.id.list:
                intent = new Intent(getApplicationContext(), OrgListActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                getMapHelp();
                break;
            case R.id.update_db:
                intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent);
                break;
            case R.id.Facebook:
                intent = new Intent(getApplicationContext(), FacebookLogin.class);
                startActivity(intent);

        }

        return(super.onOptionsItemSelected(item));
    }

    private boolean isPlayServicesConfigured() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapActivity.this);
        if(status == ConnectionResult.SUCCESS)
            return true;
        else {
            //Log.d("STATUS", "Error connecting with Google Play services. Code: " + String.valueOf(status));
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, MapActivity.this, status);
            dialog.show();
            return false;
        }
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            if (!isNetworkConnected()) {
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("No internet connection detected!")
                        .setMessage("Please check to make sure that your internet is turned on and try again!" + "\n" + "\n" + "Internet is needed for the app to get set up.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid()); // close application
                            }
                        })
                        .show();
            } else {
                //updateDatabase();
                System.out.println("Instantiated");
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("Welcome to Philly Activists and Volunteers Exchange (PAVE)!")
                        .setMessage(R.string.dialogMessage)
                        .setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), SwipePickerActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Subscribe Later", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                setUpMap();
                            }
                        })
                        .setIcon(R.drawable.ic_launcher)
                        .show();
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
            }
        }
    }

    public void getMapHelp() {
        new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("Welcome to Philly Activists and Volunteers Exchange (PAVE)!")
                .setMessage(R.string.dialogMessage)
                .setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), SwipePickerActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Subscribe Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setUpMap();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }

    public void updateDatabase() {
        if (!isNetworkConnected()) {
            //Toast.makeText(getApplicationContext(), "No internet connection detected. Please reconnect and try again.", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getApplicationContext(), "Fetching updated database...", Toast.LENGTH_SHORT).show();
            Firebase.setAndroidContext(this);
            Firebase myFirebaseRef = new Firebase("https://mappp.firebaseio.com/");
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Iterable orgs = snapshot.getChildren();
                    MyDatabase db = new MyDatabase(MapActivity.this);

                    for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                        Object o = orgs.iterator().next();
                        DataSnapshot org = (DataSnapshot) o;
                        int id = Integer.parseInt(org.getKey());
                        String updated = org.child("Updated").getValue().toString();
                        String name = org.child("Name").getValue().toString();
                        String facebookID = org.child("FacebookID").getValue().toString();
                        //System.out.println(org.child("FacebookID").getValue().toString()+"FBID");
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
                        String longitude = org.child("Longitude").getValue().toString();
                        double lat = Double.parseDouble(latitude);
                        double lng =  Double.parseDouble(longitude);*/

                        realm = Realm.getInstance(getApplicationContext());
                        realm.beginTransaction();
                        OrgEvent event = realm.createObject(OrgEvent.class);
                        event.setorgName(name);
                        event.setFacebookID(facebookID);
                        realm.commitTransaction();

                        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAzZPMw_I4GNcfuT4PeDDkp16-PNqiB1YE"); // TODO This is slowing down the app, should be in worker thread (100ms per org, which is not cool)
                        try {
                            GeocodingResult[] results = GeocodingApi.geocode(context,
                                    address + " Philadelphia, PA " + zipcode).await();
                            Geometry myGeo = results[0].geometry;
                            double lat = myGeo.location.lat;
                            double lng = myGeo.location.lng;
                            //We are calling update here
                            db.updateEntry(id, updated, name, facebookID, isDeleted, website, socialIssues, address, mission, facebook, zipcode, timestamp, twitter, lat, lng);
                        } catch (Exception e) {e.printStackTrace();
                            db.updateEntry(id, updated, name, facebookID, isDeleted, website, socialIssues, address, mission, facebook, zipcode, timestamp, twitter);
                        }
                    }
                    db.close();
                }

                @Override
                public void onCancelled(FirebaseError error) {
                }

            });
            //Toast.makeText(getApplicationContext(), "Sync complete", Toast.LENGTH_SHORT).show();
        }
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
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) { // There are no active networks
            return false;
        } else
            return true;
    }
}