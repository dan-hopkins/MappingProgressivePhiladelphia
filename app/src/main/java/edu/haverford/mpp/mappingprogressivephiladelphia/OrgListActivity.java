package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;

import java.util.ArrayList;


public class OrgListActivity extends Activity {

    private OrgListAdapter mAdapter;
    private Spinner issue_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_list);

        issue_spinner = (Spinner)findViewById(R.id.issue_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.issue_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issue_spinner.setAdapter(adapter);

        issue_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadActivity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

    }

    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        loadActivity();
    }

    protected void loadActivity() {
        MyDatabase db = new MyDatabase(this);
        ArrayList<PhillyOrg> orgList = db.getAllOrganizations();

        if (issue_spinner.getSelectedItem().toString().equals("Show All")) {
            mAdapter = new OrgListAdapter(this, //create an ArrayAdapter from the String Array
                    R.layout.org_list_item, orgList);
            ListView listView = (ListView) findViewById(R.id.listView1);
            listView.setAdapter(mAdapter); // Assign adapter to ListView
        } else {
            ArrayList<PhillyOrg> newOrgList = new ArrayList<PhillyOrg>();

            for (PhillyOrg org: orgList) {
                String issues = org.getSocialIssues();
                if (issues.contains(issue_spinner.getSelectedItem().toString())) {
                    newOrgList.add(org);
                }
            }
            mAdapter = new OrgListAdapter(this, R.layout.org_list_item, newOrgList);
            ListView listView = (ListView) findViewById(R.id.listView1);
            listView.setAdapter(mAdapter);
        }
        db.close(); // TODO check closes
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //map
        MenuItem map = menu.findItem(R.id.map);
        map.setEnabled(true);
        map.getIcon().setAlpha(255);

        //swipe
        MenuItem swipe = menu.findItem(R.id.swipe);
        swipe.setEnabled(true);
        swipe.getIcon().setAlpha(255);

        //list
        MenuItem list = menu.findItem(R.id.list);
        list.setEnabled(false);
        list.getIcon().setAlpha(100);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // adds items to action bar
        getMenuInflater().inflate(R.menu.options, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // handle action bar items
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return (true);
            case R.id.swipe:
                Intent intent = new Intent(getApplicationContext(), SwipePickerActivity.class);
                startActivity(intent);
                break;
            case R.id.map:
                intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                getListHelp();
                break;
            case R.id.update_db:
                updateDatabase();
                break;
            case R.id.Facebook:
                intent = new Intent(getApplicationContext(), FacebookLogin.class);
                startActivity(intent);
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void getListHelp() {
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
                       dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) { // There are no active networks
            return false;
        } else
            return true;
    }

    public void updateDatabase() {
        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection detected. Please reconnect and try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Fetching updates, one moment...", Toast.LENGTH_SHORT).show();
            Firebase.setAndroidContext(this);
            Firebase myFirebaseRef = new Firebase("https://mappp.firebaseio.com/");
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Iterable orgs = snapshot.getChildren();
                    MyDatabase db = new MyDatabase(OrgListActivity.this);

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
            Toast.makeText(getApplicationContext(), "Sync complete", Toast.LENGTH_SHORT).show();
            loadActivity();
        }
    }
}

class OrgListAdapter extends ArrayAdapter<PhillyOrg> {

    private ArrayList<PhillyOrg> orgList;
    private Context mContext;

    public OrgListAdapter(Context context, int textViewResourceId,
                          ArrayList<PhillyOrg> myOrgList) {
        super(context, textViewResourceId, myOrgList);
        this.orgList = new ArrayList<PhillyOrg>();
        this.orgList.addAll(myOrgList);
        this.mContext = context;
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;

        public void CheckMeOrNot(Boolean bool){
            name.setChecked(bool);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        //Log.v("ConvertView", String.valueOf(position));
        MyDatabase db = new MyDatabase(mContext);
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.org_list_item, null);

            holder = new ViewHolder();
            // holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            PhillyOrg currOrg = orgList.get(position);
            holder.CheckMeOrNot(db.isSubscribed(currOrg.getId()));
            convertView.setTag(holder);

            /*holder.code.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    TextView tv = (TextView) v ;
                    Intent intent = new Intent(mContext, OrganizationInfoActivity.class);
                    PhillyOrg currOrg = (PhillyOrg) tv.getTag();
                    intent.putExtra("OrgID", currOrg.getId());
                    mContext.startActivity(intent);
                }
            });*/
            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    PhillyOrg currOrg = (PhillyOrg) cb.getTag();
                    Boolean subbed = cb.isChecked();
                    currOrg.setSubscribed(subbed);
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        db.close();

        PhillyOrg currOrg = orgList.get(position);
        // holder.code.setText(" (" +  currOrg.getId() + ")");
        holder.name.setText(currOrg.getGroupName());
        // holder.name.setChecked(currOrg.getSubscribed());
        holder.name.setTag(currOrg);
        // holder.code.setTag(currOrg);
        return convertView;
    }

    /**
     * Saves the state of the checklist to the database.
     * @return the final number of subscribed organizations after saving
     */

    public int saveSubscribed() {
        int counter = 0;
        MyDatabase db = new MyDatabase(mContext);
        ArrayList<PhillyOrg> listToSave = this.orgList;
        for (int i = 0; i < listToSave.size(); i++) {
            PhillyOrg currOrg = listToSave.get(i);
            if (currOrg.getSubscribed()) {
                db.insertSubYes(currOrg.getId());
                counter++;
            }
            else {
                db.insertSubNo(currOrg.getId());
            }
        }
        db.close();
        return counter;
    }
}