package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dan on 5/8/15.
 */
public class EventFrag extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */

    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */

    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */

    public static EventFrag create(int pageNumber) {
        EventFrag fragment = new EventFrag();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public EventFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.event_frag, container, false);

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

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
