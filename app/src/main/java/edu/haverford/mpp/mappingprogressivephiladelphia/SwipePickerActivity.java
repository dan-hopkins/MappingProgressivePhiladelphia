package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SwipePickerActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR = "dialog_error"; // Unique tag for the error dialog fragment
    private boolean mResolvingError = false; // Bool to track whether the app is already resolving an error

    private ArrayList<String> al;
    private ArrayList<PhillyOrg> allOrgs;
    // private ArrayAdapter<String> myCardAdapter;
    private myArrayAdapter myCardAdapter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public int facebookCardCount = 0;

    @InjectView(R.id.frame) SwipeFlingAdapterView flingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        mGoogleApiClient = new GoogleApiClient.Builder(this) // set up google api for location
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        ButterKnife.inject(this);

        // TODO: figure out how to prevent cards from getting reshuffled for SwipePickerActivity onCreate AND down in updateDatabase

        MyDatabase db = new MyDatabase(this);

        //al = db.getAllOrganizationNames();
        //al = new ArrayList<>();
        allOrgs = db.getAllOrganizations();
        Collections.shuffle(allOrgs);


        //myCardAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al );
        myCardAdapter = new myArrayAdapter (this, R.layout.item, allOrgs);
        flingContainer.setAdapter(myCardAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                allOrgs.remove(0);
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
                            .show();

                }

                MyDatabase db = new MyDatabase(getApplicationContext());
                PhillyOrg currOrg = (PhillyOrg) dataObject;
                db.insertSubNo(currOrg.id);
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

                            .show();
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRight", false).apply();
                }

                if (facebookCardCount ==5 & !checkedForFB ){
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
                                        }})
                            .show();

                }


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

    @OnClick(R.id.right)
    public void right() {
        boolean isFirstRight = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRight", true);
        if (isFirstRight) {
            new AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("Are you sure")
                    .setMessage("really?")
                    .setPositiveButton("Yeah I'm sure", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            flingContainer.getTopCardListener().selectRight();
                        }
                    })
                    .setNegativeButton("No actually wait", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .setIcon(R.drawable.ic_launcher)
                    .show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRight", false).apply();
        } else {
            flingContainer.getTopCardListener().selectRight();
        }
    }

    @OnClick(R.id.left)
    public void left() {
        boolean isFirstLeft = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstLeft", true);
        if (isFirstLeft) {
            new AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("Are you sure L")
                    .setMessage("really L")
                    .setPositiveButton("Yeah I'm sure", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            flingContainer.getTopCardListener().selectLeft();
                        }
                    })
                    .setNegativeButton("No actually wait", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .setIcon(R.drawable.ic_launcher)
                    .show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstLeft", false).apply();
        } else {
            flingContainer.getTopCardListener().selectLeft();
        }
    }

/*    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }*/

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
        getMenuInflater().inflate(R.menu.options, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                .setTitle("Welcome to Philly Activists and Volunteers Exchange (PAVE)!")
                .setMessage(R.string.dialogMessage)
                .setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
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
            Toast.makeText(getApplicationContext(), "No internet connection detected. Please reconnect and try again.", Toast.LENGTH_SHORT).show();
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
            MyDatabase db = new MyDatabase(SwipePickerActivity.this); // TODO remove this stuff that refills the swiper
            allOrgs = db.getAllOrganizations();
            Collections.shuffle(allOrgs); //We can have a better sort order later, but for now random seems good.
            myCardAdapter = new myArrayAdapter(SwipePickerActivity.this, R.layout.item, allOrgs);
            flingContainer.setAdapter(myCardAdapter);
            myCardAdapter.notifyDataSetChanged();
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

