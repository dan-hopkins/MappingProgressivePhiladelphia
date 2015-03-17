package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class OrganizationInfoActivity extends Activity {

    int currentOrgID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organization_info);

        Intent intent = getIntent();
        currentOrgID = intent.getIntExtra("OrgID", -1);

        MyDatabase db = new MyDatabase(this);
        PhillyOrg currOrg = db.getOrganizationById(currentOrgID);

        TextView name = (TextView)findViewById(R.id.org_group_name);
        name.append(currOrg.getGroupName());

        TextView address = (TextView)findViewById(R.id.org_address);
        address.append(currOrg.getAddress());

        TextView zip = (TextView)findViewById(R.id.org_zip);
        zip.append(currOrg.getZipCode());

        TextView issue = (TextView)findViewById(R.id.org_issue);
        issue.append(currOrg.getSocialIssues());

        TextView mission = (TextView)findViewById(R.id.org_mission);
        mission.append(currOrg.getMission());

        TextView subscribed = (TextView)findViewById(R.id.org_subscribed);
        subscribed.append(Boolean.toString(currOrg.getSubscribed()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_organization_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
