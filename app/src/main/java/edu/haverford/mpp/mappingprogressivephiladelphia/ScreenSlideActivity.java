package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/*
Heavily edited by Dan Hopkins for use in the PAVE application.
*/

public class ScreenSlideActivity extends FragmentActivity {

    private static final int NUM_PAGES = 2;

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_screen_slide, menu);

        menu.findItem(R.id.go_orgs).setEnabled(mPager.getCurrentItem() > 0);

        if (menu.findItem(R.id.go_orgs).isEnabled()) {
            menu.findItem(R.id.go_events).setEnabled(false);
        } else {
            menu.findItem(R.id.go_events).setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.go_orgs:
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.go_events:
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter( android.support.v4.app.FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            if (position==0) {
                return OrgFrag.create(0);
            } else {
                return EventFrag.create(1);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
