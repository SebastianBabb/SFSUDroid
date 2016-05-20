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
 * The fragment that controls the view for the campus map. The view is created from
 * fragment_campus_map.xml. This fragment will initialize the map data on creation and
 * draw paths based on user input.
 *
 * @author Lowell Milliken
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

    // SFSU location
    private static final LatLng SFSU = new LatLng(37.7219, -122.4782);
    private static final int STARTING_ZOOM = 17;

    // the map
    private GoogleMap gMap;
    // the api to the current location (not used for now)
    private GoogleApiClient mGoogleApiClient;

    // the starting location for pathfinding
    private Marker startLocation;
    // the default location
    private LatLng lastKnownLocation = SFSU;
    // the map data
    private MapData mapData;
    // prevents map calls before the map is ready
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

    /**
     * Called when the fragment is created.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_map, container, false);
        Bundle args = getArguments();

        /**
         * This section places the google map in the fragment.
         */
        FragmentManager manager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentByTag("map_fragment");

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.campus_map, mapFragment, "map_fragment");
        transaction.commit();

        // create new map data and initialize
        mapData = new MapData();
        mapData.initMap(getResources());

        // populate Spinner (drop down menu) with buildings from the map data
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

    /**
     * Called asynchronously when the map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // the map is ready
        Log.d("Map ready", "Campus map");

        // set the map to be interacted with
        gMap.setOnMapClickListener(this);
        gMap.setOnMarkerClickListener(this);

        // move the camera to SFSU
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                        new LatLngBounds(new LatLng(37.72083239241096,-122.48518355190754),
                                            new LatLng(37.72567087764266,-122.47505284845829)), 0));

        // place a default starting location
        startLocation = gMap.addMarker(new MarkerOptions().position(SFSU).title("start"));

        // when creating nodes, display all current nodes with edges
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

    /**
     * Called when touching the map
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {

        // add a node for manual transcription to data file
        if(nodeCreation) {
            gMap.addMarker(new MarkerOptions().position(latLng).title("Node #" + nodeCount));
            nodeCount++;
        }

        // creating node and building files (not working, bad directory)
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

            // remove the old starting location, add the new one
            startLocation.remove();
            startLocation = gMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
            Log.i("Location", latLng.toString());
        }
    }

    /**
     * When clicking a marker in node creation node, displays it's info in the debug window
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(nodeCreation) {
            Log.i("Node Clicked", marker.getTitle() + " " + marker.getPosition().toString());

            marker.showInfoWindow();

            return true;
        }

        return false;
    }

    /**
     * Creates and adds a line on the map based on a path of nodes
     * @param path the path to draw
     * @return
     */
    public Polyline drawPath(List<MapNode> path) {
        gMap.clear();

        // create new line
        PolylineOptions pathOptions = new PolylineOptions();

        // add marker at the end of the path
        gMap.addMarker(new MarkerOptions().position(path.get(path.size()-1).getCoords())
                                          .title("Destination"));

        // add each location in the path to the line
        pathOptions.add(lastKnownLocation);
        for(MapNode node : path) {
            pathOptions.add(node.getCoords());
        }

        return gMap.addPolyline(pathOptions);
    }

    /**
     * When selecting an item from the spinner, create a new pathfinder, set the start and goal,
     * get the path, and draw it.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
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

    /**
     * get location from google location services (not used)
     */
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