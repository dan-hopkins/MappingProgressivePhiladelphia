package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by dan on 3/17/15.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstRun();
        setContentView(R.layout.splashy);
    }

    public void switchAct() {
        Intent intent = new Intent(this, SwipePickerActivity.class);
        startActivity(intent);
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun) {
            new AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("Welcome to My Progressive Philadelphia!")
                    .setMessage("Here is a brief explanation about how to use this application." + "\n" + "Check these" +
                            "\n" + "new lines")
                    .setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switchAct();
                        }
                    })
                    .setNegativeButton("Subscribe Later", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switchAct();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            // FIRST TIME ONLY: DISPLAY DIALOG

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
        }

        else
            switchAct();
    }
}