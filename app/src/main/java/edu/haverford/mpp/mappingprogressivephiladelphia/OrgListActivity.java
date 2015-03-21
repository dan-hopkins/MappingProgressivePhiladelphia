package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;


public class OrgListActivity extends Activity {

    private OrgListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_list);

        MyDatabase db = new MyDatabase(this);
        //Array list of organizations
        ArrayList<PhillyOrg> orgList = db.getAllOrganizations();

        //create an ArrayAdaptar from the String Array
        mAdapter = new OrgListAdapter(this,
                R.layout.org_list_item, orgList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(mAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                PhillyOrg currOrg = (PhillyOrg) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + currOrg.getGroupName() + ". Subscribed is: " + Boolean.toString(currOrg.getSubscribed()),
                        Toast.LENGTH_SHORT).show();
            }
        });

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




class OrgListAdapter extends ArrayAdapter<PhillyOrg> {

    private ArrayList<PhillyOrg> orgList;
    private Context mContext;

    public OrgListAdapter(Context context, int textViewResourceId,
                          ArrayList<PhillyOrg> myOrgList) {
        super(context, textViewResourceId, myOrgList);
        this.orgList = new ArrayList<PhillyOrg>();
        this.orgList.addAll(myOrgList);
        this.mContext = context;
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.org_list_item, null);

            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);

            convertView.setTag(holder);


            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    PhillyOrg currOrg = (PhillyOrg) cb.getTag();
                    Toast.makeText(mContext,
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_SHORT).show();
                    MyDatabase db = new MyDatabase(mContext);
                    Boolean subbed = cb.isChecked();
                    currOrg.setSubscribed(subbed);
                    if (subbed)
                        db.insertSubYes(currOrg.id);
                    else
                        db.insertSubNo(currOrg.id);
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        PhillyOrg currOrg = orgList.get(position);
        holder.name.setChecked(currOrg.getSubscribed());
        holder.code.setText(" (" +  currOrg.getId() + ")");
        holder.name.setText(currOrg.getGroupName());
        holder.name.setChecked(currOrg.getSubscribed());
        holder.name.setTag(currOrg);

        return convertView;

    }

}


