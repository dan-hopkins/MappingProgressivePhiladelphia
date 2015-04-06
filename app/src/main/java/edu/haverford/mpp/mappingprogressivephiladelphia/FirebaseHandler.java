package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;

import com.facebook.android.Facebook;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by BrianG on 4/2/2015.
 */
public class FirebaseHandler {

    ArrayList<PhillyOrg> allOrgs;

    public FirebaseHandler(Context context){
        Firebase.setAndroidContext(context);
        Firebase myFirebaseRef = new Firebase("https://mappp.firebaseio.com/"); // open Firebase
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Iterable orgs = snapshot.getChildren(); // go through all of the children of the data snapshot

                for (int i = 0; i<snapshot.getChildrenCount(); i++){
                    Object o = orgs.iterator().next();
                    DataSnapshot org = (DataSnapshot) o; // double-cast is necessary for some reason
                    int id = Integer.parseInt(org.getKey());
                    // Updated and Timestamp
                    String name = org.child("Name").getValue().toString();
                    int facebookID = Integer.parseInt(org.child("FacebookID").getValue().toString());
                    String isDeleted = org.child("Is Deleted").getValue().toString();
                    String website = org.child("Website").getValue().toString();
                    String socialIssues = org.child("Social-Issues").getValue().toString();
                    String address = org.child("Address").getValue().toString();
                    String mission = org.child("Mission").getValue().toString();
                    String facebook = org.child("Facebook").getValue().toString();
                    String zipcode = org.child("Zipcode").getValue().toString();
                    String twitter = org.child("Twitter").getValue().toString();

                    PhillyOrg currOrg = new PhillyOrg(id, name, website, facebook, address, zipcode, // make an organization with data from snapshot
                            socialIssues, mission, twitter, (isDeleted=="yes") ? true : false);
                    allOrgs.add(currOrg);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });
    }

    public ArrayList<PhillyOrg> getAllOrgs(){
        return allOrgs;
    }

}