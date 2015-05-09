package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dan on 5/8/15.
 */
class OrgListAdapter extends ArrayAdapter<PhillyOrg> {

    private ArrayList<PhillyOrg> orgList;
    private Context mContext;
    private ArrayList<Boolean> checkList = figureOutChecks();

    public ArrayList<PhillyOrg> getOrgList() {
        return orgList;
    }

    public OrgListAdapter(Context context, int textViewResourceId,
                          ArrayList<PhillyOrg> myOrgList) {
        super(context, textViewResourceId, myOrgList);
        this.orgList = new ArrayList<PhillyOrg>();
        this.orgList.addAll(myOrgList);
        this.mContext = context;
    }

    private class ViewHolder { CheckBox name; }

    public ArrayList<Boolean> figureOutChecks() {
        ArrayList<Boolean> checkList = new ArrayList<Boolean>();
        MyDatabase db = new MyDatabase(getContext());
        ArrayList<PhillyOrg> allOrgs = db.getAllOrganizations();
        for (PhillyOrg org: allOrgs) {
            if (org.getSubscribed()) {
                checkList.add(true);
            } else {
                checkList.add(false);
            }
        }
        db.close();
        return checkList;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        MyDatabase db = new MyDatabase(mContext);
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.org_list_item, null);

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.name.setChecked(checkList.get(position));

            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    PhillyOrg currOrg = (PhillyOrg) cb.getTag();
                    Boolean subbed = cb.isChecked();
                    currOrg.setSubscribed(subbed);
                    //saveSubscribed();
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        db.close();

        PhillyOrg currOrg = orgList.get(position);

        // holder.code.setText(" (" +  currOrg.getId() + ")");
        holder.name.setText(currOrg.getGroupName());
        //holder.name.setChecked(currOrg.getSubscribed());
        holder.name.setTag(currOrg);
        // holder.code.setTag(currOrg);
        return convertView;
    }

    /**
     * Saves the state of the checklist to the database.
     * @return the final number of subscribed organizations after saving
     */

    public int saveSubscribed() {
        int counter = 0;
        MyDatabase db = new MyDatabase(mContext);
        ArrayList<PhillyOrg> listToSave = this.orgList;
        for (int i = 0; i < listToSave.size(); i++) {
            PhillyOrg currOrg = listToSave.get(i);
            if (currOrg.getSubscribed()) {
                db.insertSubYes(currOrg.getId());
                counter++;
            }
            else {
                db.insertSubNo(currOrg.getId());
            }
        }
        db.close();
        return counter;
    }


}
