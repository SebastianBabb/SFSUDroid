package woverines.sfsuapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import woverines.sfsuapp.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class ShuttleScheduleFragment extends Fragment implements OnMapReadyCallback{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TEXT = "text";

    private GoogleMap mMap;

    public ShuttleScheduleFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     *
     */
    public static ShuttleScheduleFragment newInstance(int sectionNumber, String text) {

        ShuttleScheduleFragment fragment = new ShuttleScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shuttle_schedule, container, false);
        Bundle args = getArguments();
        final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(args.getString(ARG_TEXT));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        FragmentManager manager = getChildFragmentManager();
        SupportMapFragment shuttleFragment = (SupportMapFragment) manager.
                findFragmentById(R.id.shuttleMapFragment);

        if (shuttleFragment == null) {
            shuttleFragment = SupportMapFragment.newInstance();
        }

        shuttleFragment.getMapAsync(this);



        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Log.d("Map Ready", "testing");

        LatLngBounds campus = new LatLngBounds(new LatLng(37.72083239241096,-122.48518355190754),
                new LatLng(37.72567087764266,-122.47505284845829));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(campus,0));
    }
}