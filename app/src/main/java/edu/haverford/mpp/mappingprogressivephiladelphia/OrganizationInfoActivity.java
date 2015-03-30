package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.w3c.dom.Text;


public class OrganizationInfoActivity extends Activity {

    int currentOrgID;
    private ProfilePictureView profilePictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        // Check for an open session
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }



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

        TextView distance = (TextView)findViewById(R.id.my_distance);
        float myDist = intent.getFloatExtra("OrgDist", (float)-1.0);
        if (myDist == (float)-1.0){
            distance.append("Currently Unknown");
        }
        else{
            float p = Math.round(myDist * 10) / 10;
            distance.append(Float.toString(p) + "mi");
        }

        /*// Find the user's profile picture custom view

        profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);*/


    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                profilePictureView.setVisibility(View.VISIBLE);
                                profilePictureView.setProfileId(user.getId());
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data.
            makeMeRequest(session);
        }
    }

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
