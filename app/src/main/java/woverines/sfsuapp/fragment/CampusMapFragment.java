package woverines.sfsuapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import woverines.sfsuapp.Maps.MapData;
import woverines.sfsuapp.Maps.MapNode;
import woverines.sfsuapp.Maps.MapPathFinderAStar;
import woverines.sfsuapp.Maps.MapPathfinder;
import woverines.sfsuapp.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CampusMapFragment extends Fragment implements OnMapReadyCallback, OnMapClickListener,
        OnMarkerClickListener, AdapterView.OnItemSelectedListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TEXT = "text";
    private static final LatLng SFSU = new LatLng(37.7219, -122.4782);
    private static final int STARTING_ZOOM = 17;

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

        Spinner spinner = (Spinner) rootView.findViewById(R.id.building_spinner);

        CharSequence [] buildings = {"Select a Building","Thornton Hall","Cesar Chavez Student Center"};

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, buildings);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        Log.d("Testing Map ready", "testing");

        gMap.setOnMapClickListener(this);
        gMap.setOnMarkerClickListener(this);
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                        new LatLngBounds(new LatLng(37.72083239241096,-122.48518355190754),
                                            new LatLng(37.72567087764266,-122.47505284845829)), 0));

        //gMap.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder().tilt(30).build())));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("Location", latLng.toString());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("Location", marker.getPosition().toString());
        return false;
    }

    public Polyline drawPath(List<MapNode> path) {
        PolylineOptions pathOptions = new PolylineOptions();

        gMap.addMarker(new MarkerOptions().position(new LatLng(path.get(0).getX(),path.get(0).getY()))
                                          .title("You are here"));
        for(MapNode node : path) {
            pathOptions.add(new LatLng(node.getX(), node.getY()));
        }

        return gMap.addPolyline(pathOptions);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position != 0) {
            String building = (String) parent.getItemAtPosition(position);

            MapPathfinder pathfinder = new MapPathFinderAStar();

            pathfinder.setMapData(new MapData());

            drawPath(pathfinder.getPath());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}