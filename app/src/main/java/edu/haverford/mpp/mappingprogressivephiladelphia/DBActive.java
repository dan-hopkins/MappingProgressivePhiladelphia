package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

/**
 * Created by dan on 2/28/15.
 */
public class DBActive extends ListActivity {
    private Cursor employees;
    private MyDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new MyDatabase(this);
        employees = db.getEmployees(); // you would not typically call this on the main thread

        ListAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, employees, new String[] {"FirstName"}, new int[] {android.R.id.text1});

        getListView().setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        employees.close();
        db.close();
    }
}
