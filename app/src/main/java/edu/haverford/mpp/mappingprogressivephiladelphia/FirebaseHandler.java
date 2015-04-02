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
        Firebase myFirebaseRef = new Firebase("https://mappp.firebaseio.com/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Iterable orgs = snapshot.getChildren();

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
                    // Updated
                    String name = org.child("Name").getValue().toString(); // this works!!!!!
                    int facebookID = Integer.parseInt(org.child("FacebookID").getValue().toString());
                    String isDeleted = org.child("Is Deleted").getValue().toString();
                    String website = org.child("Website").getValue().toString();
                    String socialIssues = org.child("Social-Issues").getValue().toString();
                    String address = org.child("Address").getValue().toString();
                    String mission = org.child("Mission").getValue().toString();
                    String facebook = org.child("Facebook").getValue().toString();
                    String zipcode = org.child("Zipcode").getValue().toString();
                    // Timestamp
                    String twitter = org.child("Twitter").getValue().toString();



                    PhillyOrg currOrg = new PhillyOrg(id, name, website, facebook, address, zipcode,
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

