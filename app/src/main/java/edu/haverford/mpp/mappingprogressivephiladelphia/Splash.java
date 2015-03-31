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
    }

    public void switchToSwipe() {
        Intent intent = new Intent(this, SwipePickerActivity.class);
        startActivity(intent);
    }

    public void switchToMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun) {
            new AlertDialog.Builder(this, R.style.DialogTheme) // try R.layout.splash
                    .setTitle("Welcome to Mapping Progressive Philadelphia!")
                    .setMessage("This app is made up of three parts: Swipes, Map, and List." + "\n" + "\n"
                            + "Swipes offers a Tinder-style interface for choosing organizations that " +
                            "you'd like to subscribe to and learn more about. Just swipe the cards left " +
                            "(no thanks!) or right (sign me up!)." + "\n" + "\n" + "Subscribing to an organization " +
                            "simply allows the app to notify you when there's an event going on and places " +
                            "that organization on your personal map of Progressive Philadelphia." + "\n" + "\n" +
                            "Map displays all of the organizations you're subscribed to and allows you to click" +
                            " on a point to get more information about that organization." + "\n" + "\n" + "List is " +
                            "another way to choose organizations to subscribe to, using a more conventional design " +
                            "if Swipes just isn't your style." + "\n" + "\n" + "Click 'Subscribe Now' to get started with " +
                            "Swipes or 'Subscribe Later' to head over to the Map. You can always review this information " +
                            "again by clicking on the Help button in the overflow menu.")
                    // add  + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "hello" to see that scroll works
                    .setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switchToSwipe();
                        }
                    })
                    .setNegativeButton("Subscribe Later", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switchToMap();
                        }
                    })
                    .setIcon(R.drawable.ic_launcher)
                    .show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
        } else
            switchToMap();
    }
}