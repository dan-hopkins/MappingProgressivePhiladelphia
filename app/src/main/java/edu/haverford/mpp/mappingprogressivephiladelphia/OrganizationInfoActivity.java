package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;


public class OrganizationInfoActivity extends Activity {

    // TODO: Delete this activity
    /*int currentOrgID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.organization_info);

        Intent intent = getIntent();
        currentOrgID = intent.getIntExtra("OrgID", -1);

        MyDatabase db = new MyDatabase(this);
        PhillyOrg currOrg = db.getOrganizationById(currentOrgID);

        ImageView image = (ImageView)findViewById(R.id.org_info_pic);
        Picasso.with(this)
                .load("https://graph.facebook.com/" + currOrg.getFacebookID() + "/picture?width=99999")
                .placeholder(R.drawable.default_pic)
                .into(image);

        TextView name = (TextView)findViewById(R.id.org_group_name);
        name.append(currOrg.getGroupName());

        TextView address = (TextView)findViewById(R.id.org_address);
        address.append(currOrg.getAddress());
        address.append(", " + currOrg.getZipCode());

        TextView issue = (TextView)findViewById(R.id.org_issue);
        issue.append("\n" + currOrg.getSocialIssues());

        TextView mission = (TextView)findViewById(R.id.org_mission);
        mission.append("\n" + currOrg.getMission());

        TextView subscribed = (TextView)findViewById(R.id.org_subscribed);
        if (currOrg.getSubscribed()) {
            subscribed.append("Yes");
        } else {
            subscribed.append("No");
        }

        TextView distance = (TextView)findViewById(R.id.my_distance);
        float myDist = intent.getFloatExtra("OrgDist", (float)-1.0);
        if (myDist == (float)-1.0){
            distance.append("Currently Unknown");
        }
        else{
            float p = Math.round(myDist * 10) / 10;
            distance.append(Float.toString(p) + "mi");
        }

        // Find the user's profile picture custom view
        //profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
        //profilePictureView.setCropped(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_organization_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // handles action bar item clicks
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}