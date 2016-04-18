package woverines.sfsuapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import woverines.sfsuapp.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class CampusMapFragment extends Fragment implements OnMapReadyCallback {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TEXT = "text";

    private GoogleMap gMap;

    public CampusMapFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     *
     */
    public static CampusMapFragment newInstance(int sectionNumber, String text) {

        CampusMapFragment fragment = new CampusMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_map, container, false);
        Bundle args = getArguments();
//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(args.getString(ARG_TEXT));

        FragmentManager manager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentByTag("map_fragment");

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.campus_map, mapFragment, "map_fragment");
        transaction.commit();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

    }
}