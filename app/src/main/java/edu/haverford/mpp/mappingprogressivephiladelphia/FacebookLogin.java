package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class FacebookLogin extends Activity {


    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Realm realm;
    public String event = "name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(this);


        //FacebookSdk.sdkInitialize(this.getApplicationContext());

        if(!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(this.getApplicationContext());
        }

        setContentView(R.layout.activity_facebook_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        loginButton.setReadPermissions(permissionNeeds);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String gp = "/61159665895/events";
                Bundle parameter = new Bundle();
                parameter.putString("fields", "id,name,link");
                realm.beginTransaction();
                GraphRequest r = GraphRequest.newGraphPathRequest(loginResult.getAccessToken(), gp, new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse graphResponse) {
                                //GraphResponse contains the data we asked for from Facebook, as well as a response code
                                JSONObject query_result = graphResponse.getJSONObject();
                                TextView view = (TextView) findViewById(R.id.login_text);
                                try {
                                    //Taking the JSONArray that contains the response code + raw data,
                                    // and getting an array of just the data
                                    JSONArray query_data = query_result.getJSONArray("data");
                                    //Querying for the first (which is the most recent) event
                                    JSONObject event = query_data.getJSONObject(0);
                                    //event is a JSON object containing (in order) name, start_time, end_time, timezone, location, id.
                                    //This ID is a separate event_ID that must be queried to get event description
                                    view.setText(event.get("name").toString());
                                    OrgEvent realm_event = realm.createObject(OrgEvent.class);
                                    realm_event.setName((String) event.get("name"));
                                    realm_event.setStartTime("start");
                                    realm.commitTransaction();


                                } catch (JSONException e) {
                                    view.setText(graphResponse.toString());
                                    view.setText("error");
                                    e.printStackTrace();
                                }

                                System.out.println(graphResponse.getJSONObject().names());

                            }
                        }
                );
                r.executeAsync();

                RealmQuery<OrgEvent> test_query = realm.where(OrgEvent.class);
                test_query.equalTo("StartTime", "Start");
                RealmResults<OrgEvent> result1 = test_query.findAll();
                //TextView view = (TextView) findViewById(R.id.login_text);
                //view.setText(result1.toString());

            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }



            @Override
            public void onError(FacebookException exception) {
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }

    public void queryFacebookEvent(String facebookid, LoginResult login){


        String graphpath = facebookid+"/events";
        Bundle parameter = new Bundle();
        parameter.putString("fields", "id,name,link");
        realm.beginTransaction();
        GraphRequest r = GraphRequest.newGraphPathRequest(login.getAccessToken(), graphpath, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        JSONObject query_result = graphResponse.getJSONObject();
                        TextView view = (TextView)findViewById(R.id.login_text);
                        try {
                            //Taking the JSONArray that contains the response code + raw data,
                            // and getting an array of just the data
                            JSONArray query_data = query_result.getJSONArray("data");
                            //Querying for the first (which is the most recent) event
                            JSONObject event = query_data.getJSONObject(0);
                            //event is a JSON object containing (in order) name, start_time, end_time, timezone, location, id.
                            //This ID is a separate event_ID that must be queried to get event description
                            view.setText(event.get("name").toString());


                        } catch (JSONException e) {
                            view.setText(graphResponse.toString());
                            view.setText("error");
                            e.printStackTrace();
                        }

                        OrgEvent event = realm.createObject(OrgEvent.class);
                        event.setName(FacebookLogin.this.event);
                        realm.commitTransaction();
                    }
                }
        );
        r.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_facebook_login, menu);
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
