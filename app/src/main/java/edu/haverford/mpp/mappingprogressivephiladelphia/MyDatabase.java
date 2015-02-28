package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by dan on 2/28/15.
 */
public class MyDatabase extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "mpp";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
