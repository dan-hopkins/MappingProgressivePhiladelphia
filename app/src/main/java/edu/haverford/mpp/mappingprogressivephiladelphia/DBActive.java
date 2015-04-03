package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

/**
 * Created by dan on 2/28/15.
 */
public class DBActive extends ListActivity {

    /*private Cursor zipcodes;
    //private Cursor employees;
    private MyDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new MyDatabase(this);
        ArrayList<PhillyOrg> test = db.getAllOrganizations();
        Log.i("Size", Integer.toString(test.size()));
        Log.i("Test", db.getAllOrganizationNames().get(1));
        zipcodes = db.getAllZipCodes(); // you would not typically call this on the main thread

        ListAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                zipcodes,
                new String[] {"ZipCode"}, // table you want to look at
                new int[] {android.R.id.text1});

        getListView().setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zipcodes.close();
        db.close();
    }*/
}
