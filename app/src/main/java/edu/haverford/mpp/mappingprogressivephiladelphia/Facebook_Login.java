package edu.haverford.mpp.mappingprogressivephiladelphia;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by evanhamilton on 3/22/15.
 */
public class Facebook_Login extends FragmentActivity{
    TextView userInfoTextView;

    public String id;
    public String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_login);
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://mappp.firebaseio.com/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Iterable orgs = snapshot.getChildren();
                MyDatabase db = new MyDatabase(Facebook_Login.this);
                for (int i = 0; i<snapshot.getChildrenCount(); i++){
                    Object o = orgs.iterator().next(); // object is A-Space, for example, required to be an object even though it's really a DataSnapshot
                    // System.out.println(o); // o is a DataSnapshot object (can be found by doing o.getClass())
                    // DataSnapshot { key = 1. value = {Updated=2015-04-01T02:04:54.716Z, Name=A-Space (Space), FacebookID=30987050865, Is Deleted=noo, Website=TEMP, Social-Issues=Disability, Racial Justice, Gender, Immigration / Immigrant Rights, LGBTQ, Poverty, Incarceration, Funding / Grantwriting, Education, Housing, Youth, Fun things to Do!, Health, Women, Arts & Culture, Public Safety, Recreation, Food Justice, Address=4722 Baltimore Ave, Mission=A-Space is a collectively run anarchist community center and art gallery in Philadelphia. A-Space hosts a Books Through Bars program, the Philadelphia Anti-War Forum, and meetings of the National Organization for the Reform of Marijuana Laws (NORML)., Facebook=https://www.facebook.com/pages/A-Space-Anarchist-Community-Center/30987050865, Zipcode=19143, Timestamp=3/9/2014 14:46:50, Twitter=TEMP} }
                    // prints this ^^^ for each organization

                    DataSnapshot org = (DataSnapshot) o;
                    // just need to figure out how to parse
                    //System.out.println(org.getValue()); // this correctly prints the value for the current org
                    //System.out.println(org.getKey()); // this correctly prints the key (id) for the current org
                    //System.out.println(org.child("Name")); // this prints out: DataSnapshot { key = Name, value = A-Space (Space) } for each organization
                    //System.out.println(org.child("Name").getValue()); // this prints out: A-Space (Space)
                    int id = Integer.parseInt(org.getKey());
                    String updated = org.child("Updated").getValue().toString();
                    String name = org.child("Name").getValue().toString(); // this works!!!!!
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
                    db.updateEntry(id, updated, name, facebookID, isDeleted, website, socialIssues, address, mission, facebook, zipcode, timestamp, twitter);

                }



            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });

        /*myFirebaseRef.child("Out4STEM15").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("string", "string");
                //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                id = snapshot.child("FacebookID").getValue().toString();
                System.out.println(id);

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                url = new String("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.d(url, "URL");

                Picasso.with(getBaseContext())
                        .load(url)
                        .error(R.drawable.default_pic)
                        .into(imageView);

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }


        });*/

        /*ImageView imageView = (ImageView) findViewById(R.id.imageView);
        url = new String("https://graph.facebook.com/"+id+"/picture?type=large");
        Log.d(url, "URL");

        Picasso.with(this)
                .load(url)
                .error(R.drawable.default_pic)
                .into(imageView);*/



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


/*        Firebase ref = new Firebase("https://mappp.firebaseio.com/");
        // Query queryRef = ref.orderByChild("Zipcode").limitToLast(2); // returns the IDs of the orgs with the two highest zipcodes (19147 and 19143) so returns 22 and 10.
        // https://www.firebase.com/blog/2014-11-04-firebase-realtime-queries.html
        // https://www.firebase.com/docs/android/guide/retrieving-data.html
        // https://www.firebase.com/docs/web/api/query/

        // CHECK THESE ^^^ BUT NOTE THAT FIRST ONE HAS A TYPO IN THE FIRST EXAMPLE


        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                System.out.println(snapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/




}
