package woverines.sfsuapp.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import woverines.sfsuapp.Maps.MapData;
import woverines.sfsuapp.Maps.MapNode;
import woverines.sfsuapp.Maps.MapPathFinderAStar;
import woverines.sfsuapp.Maps.MapPathfinder;
import woverines.sfsuapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CampusMapFragment extends Fragment implements OnMapReadyCallback, OnMapClickListener,
        OnMarkerClickListener, AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TEXT = "text";

    private static final LatLng SFSU = new LatLng(37.7219, -122.4782);
    private static final int STARTING_ZOOM = 17;

    private GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;

    private Marker startLocation;
    private LatLng lastKnownLocation = SFSU;
    private MapData mapData;
    private boolean mapReady = false;

    // map creation fields
    // will all be unused during normal use
    private final boolean nodeCreation = false;
    private String currentBuilding = "";
    private boolean buildingInit = false;
    private int nodeCount = 0;
    private boolean nodeLinking = false;

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

        FragmentManager manager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentByTag("map_fragment");

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.campus_map, mapFragment, "map_fragment");
        transaction.commit();

        mapData = new MapData();
        mapData.initMap(getResources());

        Spinner spinner = (Spinner) rootView.findViewById(R.id.building_spinner);

        CharSequence [] buildings = mapData.getBuildingNames();

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, buildings);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        Log.d("Map ready", "Campus map");

        gMap.setOnMapClickListener(this);
        gMap.setOnMarkerClickListener(this);
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                        new LatLngBounds(new LatLng(37.72083239241096,-122.48518355190754),
                                            new LatLng(37.72567087764266,-122.47505284845829)), 0));

        startLocation = gMap.addMarker(new MarkerOptions().position(SFSU).title("start"));

        if(nodeCreation) {
            for(MapNode node: mapData.getNodes().values()) {
                gMap.addMarker(new MarkerOptions().position(node.getCoords())
                        .title("Node #" + node.getId()));
                for(MapNode temp : node.getAdjacentNodes()) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.add(node.getCoords());
                    polylineOptions.add(temp.getCoords());
                    gMap.addPolyline(polylineOptions);
                }
            }
        }

        mapReady =true;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if(nodeCreation) {
            gMap.addMarker(new MarkerOptions().position(latLng).title("Node #" + nodeCount));
            nodeCount++;
        }

        buildingInit = false;
        if(buildingInit) {

            if(nodeCreation) {
                try {
                    File dir =
                            new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOCUMENTS), null);
                    File file = new File(dir, "buildingOut.txt");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    Log.i("building",currentBuilding + "\n" + latLng.latitude + "\n" + latLng.longitude);
                    writer.append(currentBuilding + "\n" + latLng.latitude + "\n" + latLng.longitude);
                    writer.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            buildingInit = false;
        } else {
            startLocation.remove();
            startLocation = gMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
            Log.i("Location", latLng.toString());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(nodeCreation) {
            Log.i("Node Clicked", marker.getTitle() + " " + marker.getPosition().toString());

            marker.showInfoWindow();

            return true;
        }

        return false;
    }

    public Polyline drawPath(List<MapNode> path) {
        gMap.clear();

        PolylineOptions pathOptions = new PolylineOptions();

        gMap.addMarker(new MarkerOptions().position(path.get(path.size()-1).getCoords())
                                          .title("Destination"));

        pathOptions.add(lastKnownLocation);
        for(MapNode node : path) {
            pathOptions.add(node.getCoords());
        }

        return gMap.addPolyline(pathOptions);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(mapReady) {
            if (nodeCreation) {
                currentBuilding = (String) parent.getItemAtPosition(position);
                buildingInit = true;
            } else {
//            updateLocation();
                lastKnownLocation = startLocation.getPosition();

                LatLng currentCoords = lastKnownLocation;
                if (position != 0) {
                    String building = (String) parent.getItemAtPosition(position);
                    MapPathfinder pathfinder = new MapPathFinderAStar();

                    pathfinder.setMapData(mapData);
                    MapNode nearest = mapData.getNearestNode(currentCoords);
                    Log.i("Nearest Node", "#" + nearest.getId());
                    pathfinder.setStart(nearest);
                    pathfinder.setDestination(mapData.getBuildingNode(building, currentCoords));
                    drawPath(pathfinder.getPath());
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void updateLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (loc != null)
                lastKnownLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
        }
    }
}