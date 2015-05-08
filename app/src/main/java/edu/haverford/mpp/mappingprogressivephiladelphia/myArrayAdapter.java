package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class myArrayAdapter extends ArrayAdapter {
    Context mContext;

    public myArrayAdapter(Context context, int textViewResourceId, ArrayList<PhillyOrg> orgs) {
        super(context, textViewResourceId, /*objects*/ orgs);
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater)mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View view = convertView;
        if (view == null) {
            view = vi.inflate(R.layout.item, parent, false);
            PhillyOrg po = (PhillyOrg)getItem(position);
            TextView myText = (TextView) view.findViewById(R.id.orgname);
            myText.setText(po.getGroupName());

            ImageView myImageView = (ImageView) view.findViewById(R.id.org_pic);
            Picasso.with(mContext)
                    .load("https://graph.facebook.com/" + po.getFacebookID() + "/picture?width=99999")
                    .placeholder(R.drawable.default_pic)
                    .into(myImageView);
        }

        return view;
    }
}