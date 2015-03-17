package edu.haverford.mpp.mappingprogressivephiladelphia;

/**
 * Created by dan on 3/17/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    // Duration of wait
    private final int SPLASH_DISPLAY_LENGTH = 2000;

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