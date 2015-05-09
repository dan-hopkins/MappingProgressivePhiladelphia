package edu.haverford.mpp.mappingprogressivephiladelphia;

/**
 * Created by dan on 5/8/15.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dan on 5/8/15.
 */
public class OrgFrag extends Fragment {

    private OrgListAdapter mAdapter;
    private Spinner issue_spinner;

    public static final String ARG_PAGE = "page";

    private int mPageNumber;

    public static OrgFrag create(int pageNumber) {
        OrgFrag fragment = new OrgFrag();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public OrgFrag() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        issue_spinner = (Spinner)getActivity().findViewById(R.id.issue_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.issue_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issue_spinner.setAdapter(adapter);

        issue_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadActivity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    public void onResume() { // was protected
        super.onResume();
        getActivity().invalidateOptionsMenu();
        loadActivity();
    }

    protected void loadActivity() {
        MyDatabase db = new MyDatabase(getActivity());
        ArrayList<PhillyOrg> orgList = db.getAllOrganizations();

        if (issue_spinner.getSelectedItem().toString().equals("Show All")) {
            mAdapter = new OrgListAdapter(getActivity(), R.layout.org_list_item, orgList);
            ListView listView = (ListView)getActivity().findViewById(R.id.listView1);
            listView.setAdapter(mAdapter); // Assign adapter to ListView
        } else {
            ArrayList<PhillyOrg> newOrgList = new ArrayList<PhillyOrg>();

            for (PhillyOrg org: orgList) {
                String issues = org.getSocialIssues();
                if (issues.contains(issue_spinner.getSelectedItem().toString())) {
                    newOrgList.add(org);
                }
            }
            mAdapter = new OrgListAdapter(getActivity(), R.layout.org_list_item, newOrgList);
            ListView listView = (ListView)getActivity().findViewById(R.id.listView1);
            listView.setAdapter(mAdapter);
        }
        db.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.saveSubscribed();
    } // used to be protected

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.org_frag, container, false);

        // Set the title view to show the page number.
        if (mPageNumber == 0) {
            // Organizations
            ((TextView) rootView.findViewById(android.R.id.title)).setText("Organizations");
        } else {
            // Events
            ((TextView) rootView.findViewById(android.R.id.title)).setText("Events");
        }

        return rootView;
    }


    public int getPageNumber() {
        return mPageNumber;
    }
}
