package edu.haverford.mpp.mappingprogressivephiladelphia;

/**
 * Created by dan on 3/17/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            // Place your dialog code here to display the dialog


            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
        }
    }

    // Duration of wait
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    /** THIS IS THE PICTURE TO GO IN SPLASHY.XML
     * <ImageView android:id="@+id/splashscreen" android:layout_width="wrap_content"
     android:layout_height="fill_parent"
     android:src="@drawable/splash"
     android:layout_gravity="center"/>
     *
     */

        // Called when the activity is first created
        @Override
         public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashy);

            /* New Handler to start the Swipe Picker
             * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Create an Intent that will start the Swipe Picker Activity.
                Intent mainIntent = new Intent(Splash.this,SwipePickerActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}