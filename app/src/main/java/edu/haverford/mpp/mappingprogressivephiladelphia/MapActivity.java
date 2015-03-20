package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

public class MapActivity extends FragmentActivity {

    int x = 1;
    boolean y = (x==1) ? true : false;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.w("TAG", "Play services configured: " + Boolean.toString(isPlayServicesConfigured()));
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            Log.e("TAG", "Failed to initialize map");
        }

        setUpMapIfNeeded();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Move the camera instantly to Philadelphia with a zoom of 11.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.952595,-75.163736), 11)); //Town Center Philadelphia

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        final SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(MapActivity.this);

        MyDatabase db = new MyDatabase(getApplicationContext());
        ArrayList<Integer> subbedOrgIDs = db.getAllSubscribedOrgIDs();
        PhillyOrg currentOrg = new PhillyOrg();

        mMap.addMarker(new MarkerOptions().position(new LatLng(40.00786,-75.306238)).title("Haverford College"));
        for (int i = 0; i < subbedOrgIDs.size(); i++) {
            currentOrg = db.getOrganizationById(subbedOrgIDs.get(i));
            mMap.addMarker(new MarkerOptions().position(new LatLng(currentOrg.getLatitude(), currentOrg.getLongitude())).title(currentOrg.getGroupName()));
        }

        //mMap.addMarker(new MarkerOptions().position(new LatLng(40.034901,-75.33735)).title("Villanova University"));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(40.034901, -75.33735)).title(mySharedPreferences.getString("Haverford", "")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        menu.getItem(0).setVisible(false);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.about:
                return(true);
            case R.id.help:
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }


    private boolean isPlayServicesConfigured() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapActivity.this);
        if(status == ConnectionResult.SUCCESS)
            return true;
        else {
            Log.d("STATUS", "Error connecting with Google Play services. Code: " + String.valueOf(status));
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, MapActivity.this, status);
            dialog.show();
            return false;
        }
    }
}