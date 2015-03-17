package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by BrianG on 3/17/2015.
 */
public class SwipePickerAdapter extends BaseAdapter {

    private ArrayList<PhillyOrg> allOrgs;
    Context mContext;

    public SwipePickerAdapter(Context context){
        MyDatabase db = new MyDatabase(context);
        this.allOrgs = db.getAllOrganizations();
        mContext = context;
    }

    @Override
    public int getCount() {
        return allOrgs.size();
    }

    @Override
    public Object getItem(int position) {
        return allOrgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return allOrgs.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhillyOrg currOrg = allOrgs.get(position);
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View cell = inflater.inflate(R.layout.item, null);
        TextView myText = (TextView)cell.findViewById(R.id.helloText);
        myText.setText(currOrg.getGroupName());
        return cell;
    }
}
