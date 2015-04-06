package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by dan on 2/28/15.
 */
public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "mppdb";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        // super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
    }

    public void insertSubYes(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Subscribed", 1);
        db.update("mppdata", updatedValues, "_id=" + id, null);
    }

    public void insertSubNo(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Subscribed", 0);
        db.update("mppdata", updatedValues, "_id = " + id, null);
    }

    public boolean isSubscribed(int id){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] sqlSelect = {"_id", "Subscribed"}; // the 0 _id thing is necessary for some reason
        qb.setTables("mppdata");
        Cursor c = qb.query(db, sqlSelect, "_id = " + id, null, null, null, null);
        Log.d("Cursor", DatabaseUtils.dumpCursorToString(c));
        if (c.moveToFirst())
            return (c.getInt(1) == 1) ? true : false;
        else
            return false;
    }

 /* public Cursor getAllZipCodes() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"_id", "ZipCode"}; // the 0 _id thing is necessary for some reason
        String sqlTables = "mppdata"; // this is the table in the database that you want to work with

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        c.moveToFirst();
        return c;
    }*/

    public ArrayList<PhillyOrg> getAllOrganizations(){
        ArrayList<PhillyOrg> allOrgs = new ArrayList<PhillyOrg>();
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String sqlTables = "mppdata"; // this is the table in the database that you want to work with

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                boolean subbed = (c.getInt(12) == 1) ? true : false;

                PhillyOrg org = new PhillyOrg(
                                c.getInt(0), c.getString(1),c.getString(2),
                                c.getString(3),c.getString(4),c.getString(5),
                                c.getString(6),c.getString(7),c.getString(8), c.getString(9), c.getDouble(10), c.getDouble(11), subbed, c.getString(14));

                allOrgs.add(org);
            } while (c.moveToNext());
        }
        return allOrgs;
    }

    public PhillyOrg getOrganizationById(int id){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String sqlTables = "mppdata"; // this is the table in the database that you want to work with
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, null, "_id = " + id, null, null, null, null);
        if (c.moveToFirst()) {
                boolean subbed = (c.getInt(12) == 1) ? true : false;
                PhillyOrg org = new PhillyOrg(
                        c.getInt(0), c.getString(1),c.getString(2),
                        c.getString(3),c.getString(4),c.getString(5),
                        c.getString(6),c.getString(7),c.getString(8), c.getString(9), c.getDouble(10), c.getDouble(11), subbed, c.getString(14));
            return org;
        }
        return null;
    }

/*    public ArrayList<String> getAllOrganizationNames() {
        ArrayList<String> orgNames = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"_id", "GroupName"}; // the 0 _id thing is necessary for some reason
        String sqlTables = "mppdata"; // this the table in the database that you want to work with

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                orgNames.add(c.getString(1));
            } while (c.moveToNext());
        }
        return orgNames;
    }

    public ArrayList<Integer> getAllSubscribedOrgIDs() {
        ArrayList<Integer> subbedOrgs = new ArrayList<Integer>();
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"_id", "Subscribed"}; // the 0 _id thing is necessary for some reason
        String sqlTables = "mppdata"; // this the table in the database that you want to work with

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "Subscribed = 1", null, null, null, null);
        Log.i("SubCursor", DatabaseUtils.dumpCursorToString(c));
        if (c.moveToFirst()) {
            do {
                subbedOrgs.add(c.getInt(0));
            } while (c.moveToNext());
        }
        return subbedOrgs;
    }*/

    /*int id = Integer.parseInt(org.getKey());
                    String updated = org.child("Updated").getValue().toString();
                    String name = org.child("Name").getValue().toString(); // this works!!!!!
                    String facebookID = org.child("FacebookID").getValue().toString();
                    String isDeleted = org.child("Is Deleted").getValue().toString();
                    String website = org.child("Website").getValue().toString();
                    String socialIssues = org.child("Social-Issues").getValue().toString();
                    String address = org.child("Address").getValue().toString();
                    String mission = org.child("Mission").getValue().toString();
                    String facebook = org.child("Facebook").getValue().toString();
                    String zipcode = org.chichild("Timestamp").getValue().toString();
                    String twitter = org.child("Twitter").getValue().toString();*/

    public void updateEntry(int id, String updated, String name, String facebookID, String isDeleted,
                            String website, String socialIssues, String address, String mission, String
                            facebook, String zipcode, String timestamp, String twitter, Double latitude, Double longitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        //updatedValues.put("_id", id);
        updatedValues.put("Updated", updated);
        updatedValues.put("GroupName", name);
        updatedValues.put("FacebookID", facebookID);
        updatedValues.put("isDeleted", isDeleted);
        updatedValues.put("Website", website);
        updatedValues.put("SocialIssues", socialIssues);
        updatedValues.put("Address", address);
        updatedValues.put("Mission", mission);
        updatedValues.put("Facebook", facebook);
        updatedValues.put("ZipCode", zipcode);
        updatedValues.put("Timestamp", timestamp);
        updatedValues.put("Twitter", twitter);
        updatedValues.put("Longitude", longitude);
        updatedValues.put("Latitude", latitude);
        //db.insertWithOnConflict("mppdata", null, updatedValues, SQLiteDatabase.CONFLICT_REPLACE);
        //System.out.println(db.insertWithOnConflict("mppdata", null, updatedValues, SQLiteDatabase.CONFLICT_REPLACE));
        db.update("mppdata", updatedValues, "_id = " + id, null); //TODO make it work with insert as well as update
    }

    public void updateEntry(int id, String updated, String name, String facebookID, String isDeleted,
                            String website, String socialIssues, String address, String mission, String
            facebook, String zipcode, String timestamp, String twitter) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        //updatedValues.put("_id", id);
        updatedValues.put("Updated", updated);
        updatedValues.put("GroupName", name);
        updatedValues.put("FacebookID", facebookID);
        updatedValues.put("isDeleted", isDeleted);
        updatedValues.put("Website", website);
        updatedValues.put("SocialIssues", socialIssues);
        updatedValues.put("Address", address);
        updatedValues.put("Mission", mission);
        updatedValues.put("Facebook", facebook);
        updatedValues.put("ZipCode", zipcode);
        updatedValues.put("Timestamp", timestamp);
        updatedValues.put("Twitter", twitter);
        //db.insertWithOnConflict("mppdata", null, updatedValues, SQLiteDatabase.CONFLICT_REPLACE);
        //System.out.println(db.insertWithOnConflict("mppdata", null, updatedValues, SQLiteDatabase.CONFLICT_REPLACE));
        db.update("mppdata", updatedValues, "_id = " + id, null); //TODO make it work with insert as well as update
    }
    // public ArrayList<PhillyOrg> getAllOrgsByIDs(ArrayList<Integer> IDs){ }
}
