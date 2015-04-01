package edu.haverford.mpp.mappingprogressivephiladelphia;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

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

                //System.out.println(snapshot.getValue());
                //System.out.println(snapshot.getChildren());

                Iterable orgs = snapshot.getChildren();

                for (int i = 0; i<snapshot.getChildrenCount(); i++){
                    Object org = orgs.iterator().next(); // object is A-Space, for example
                    System.out.println(org.getClass());
                    System.out.println(org);
                    DataSnapshot organ = (DataSnapshot) org;
                    System.out.println(organ.getValue());
                    Object orgz = organ.getValue();
                    //This is just trolling
                    //orgs.iterator().next is returning an object, but then the type of that object
                    //is a DataSnapshot? Either way, I don't know how to access the info
                    //Check the console to see how each organization be logged
                }



            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });

        myFirebaseRef.child("Out4STEM15").addValueEventListener(new ValueEventListener() {

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


        });

        /*
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        url = new String("https://graph.facebook.com/"+id+"/picture?type=large");
        Log.d(url, "URL");

        Picasso.with(this)
                .load(url)
                .error(R.drawable.default_pic)
                .into(imageView);
        */



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }







}
