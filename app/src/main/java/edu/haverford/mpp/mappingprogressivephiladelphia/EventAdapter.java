package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by dan on 5/9/15.
 */
public class EventAdapter extends BaseAdapter {

    // getInt(), getCount(), getObject() given int position
    private ArrayList<OrgEvent> eventList;
    private Context mContext;

    public EventAdapter(Context context, int textViewResourceId, ArrayList<OrgEvent> myEventList) {
        this.eventList = new ArrayList<OrgEvent>();
        this.eventList.addAll(myEventList);
        this.mContext = context;
    }

    private class ViewHolder {

    }

    public int getCount() {
        return eventList.size();
    }

    public Object getItem(int arg0) {
        return null; // TODO
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        // My database?
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.org_list_item, null); // TODO not org_list_item
        }

        return null; // TODO
    }

}
