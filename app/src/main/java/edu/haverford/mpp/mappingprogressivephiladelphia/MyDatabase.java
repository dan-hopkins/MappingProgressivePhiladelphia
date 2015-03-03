package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

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
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

    }

    public Cursor getAllZipCodes() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"0 _id", "ZipCode"}; // the 0 _id thing is necessary for some reason
        String sqlTables = "mppdata"; // this is the table in the database that you want to work with

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        c.moveToFirst();
        return c;
    }

    public ArrayList<PhillyOrg> getAllOrganizations(){
        ArrayList<PhillyOrg> allOrgs = new ArrayList<PhillyOrg>();
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"0 _id", "ZipCode"}; // the 0 _id thing is necessary for some reason
        String sqlTables = "mppdata"; // this is the table in the database that you want to work with

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                PhillyOrg org = new PhillyOrg(
                                c.getInt(0), c.getString(1),c.getString(2),
                                c.getString(3),c.getString(4),c.getString(5),
                                c.getString(6),c.getString(7),c.getString(8), c.getString(9));

                allOrgs.add(org);
            } while (c.moveToNext());
        }
        return allOrgs;
    }

    /*
    public Cursor getEmployees() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"0 _id", "FirstName", "LastName"};
        String sqlTables = "Employees";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        c.moveToFirst();
        return c;
    } */
}
