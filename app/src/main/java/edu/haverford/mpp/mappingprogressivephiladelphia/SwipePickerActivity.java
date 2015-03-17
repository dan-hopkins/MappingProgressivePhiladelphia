package edu.haverford.mpp.mappingprogressivephiladelphia;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SwipePickerActivity extends Activity {

    private ArrayList<String> al;
    private SwipePickerAdapter arrayAdapter;
    private int i;
    private int itemPos;

    @InjectView(R.id.frame) SwipeFlingAdapterView flingContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        MyDatabase db = new MyDatabase(this);
        db.getAllSubscribedOrgIDs();

        al = db.getAllOrganizationNames();
        //al = new ArrayList<>();

        arrayAdapter = new SwipePickerAdapter(this);
        itemPos = 1; //matches the id of the first item in the stack -- Increment itemPos as we remove cards so that it remains the correct id
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                itemPos++;
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //makeToast(SwipePickerActivity.this, "Left!");
                MyDatabase db = new MyDatabase(getApplicationContext()); //for this context
                db.insertSubNo(itemPos-1); //itemPos has incremented since object has been removed so offset by -1
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                MyDatabase db = new MyDatabase(getApplicationContext());
                db.insertSubYes(itemPos-1);  //itemPos has incremented since object has been removed so offset by -1
                //makeToast(SwipePickerActivity.this, "Right!");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //MyDatabase db = new MyDatabase(getApplicationContext());
                //makeToast(SwipePickerActivity.this, Boolean.toString(db.isSubscribed(itemPos)));

                Intent intent = new Intent(getApplicationContext(), OrganizationInfoActivity.class);
                intent.putExtra("OrgID", itemPos);
                startActivity(intent);
            }
        });

    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.right)
    public void right() {

        // THIS IS JUST DAN TRYING SOMETHING NEW
        /*Intent intent = new Intent(getApplicationContext(), DBActive.class);
        startActivity(intent);*/

        //
        /**
         * Trigger the right event manually.
         */
        flingContainer.getTopCardListener().selectRight();
    }

    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        menu.getItem(0).setVisible(true);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                return (true);
            case R.id.map:
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                return (true);
            case R.id.help:
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }




}