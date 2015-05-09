package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dan on 5/9/15.
 */
public class EventAdapter extends BaseAdapter {

    private ArrayList<OrgEvent> eventList;
    private Context mContext;

    public EventAdapter(Context context, ArrayList<OrgEvent> myEventList) {
        this.eventList = myEventList;
        this.mContext = context;
    }



    public int getCount() {
        return eventList.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return Long.parseLong(eventList.get(position).getEventID());
    }

    private class ViewHolder {
        TextView orgname;
        TextView eventname;
        TextView startTime;
        LinearLayout wholeLayout;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.event_item, null);
            holder = new ViewHolder();
            holder.orgname = (TextView) convertView.findViewById(R.id.orgname);
            holder.eventname = (TextView) convertView.findViewById(R.id.eventname);
            holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
            holder.wholeLayout = (LinearLayout) convertView.findViewById(R.id.eventitemlayout);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        final OrgEvent myEvent = eventList.get(position);
        holder.eventname.setText(myEvent.getEventName());
        holder.orgname.setText(myEvent.getOrgName());

        String startTime = myEvent.getStartTime();
        String year = startTime.substring(0, 4);
        String month = startTime.substring(5, 7);
        String day = startTime.substring(8, 10);
        String fake_hour = startTime.substring(11,13);
        String minute = startTime.substring(14, 16);
        String amorpm;
        int hour_int;
        String hour;
        if (Integer.parseInt(fake_hour) > 11) {
            amorpm = "pm";
            hour_int = Integer.parseInt(fake_hour)-12;
            hour = Integer.toString(hour_int);
        } else {
            amorpm = "am";
            hour = startTime.substring(12,13);
        }

        final String realStartTime = hour+":"+minute+amorpm+" "+month+"/"+day+"/"+year;

        holder.startTime.setText(realStartTime);

        holder.wholeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.event_info);
                dialog.setTitle(myEvent.getEventName()); // Event Name

                TextView org_dialog = (TextView)dialog.findViewById(R.id.org_dialog);
                org_dialog.append(myEvent.getOrgName()); // name of organization

                TextView start_dialog = (TextView)dialog.findViewById(R.id.start_dialog);
                start_dialog.append(realStartTime); // start time of event

                TextView event_description_dialog = (TextView)dialog.findViewById(R.id.event_description_dialog);
                event_description_dialog.append(myEvent.getEventDescription()); // event description

                Button closeButton = (Button)dialog.findViewById(R.id.closeButton);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return convertView;
    }

}
