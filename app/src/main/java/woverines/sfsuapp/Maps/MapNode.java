package woverines.sfsuapp.Maps;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapNode {
    private LatLng coords;
    private List<MapNode> adjacentNodes;
    private int id;

    public MapNode(int id, LatLng coords) {
        this.id = id;
        this.coords = coords;
        this.adjacentNodes = new ArrayList<>();
    }

    public void addAdjacentNode(MapNode node) {
        adjacentNodes.add(node);
    }

    public LatLng getCoords() {
        return coords;
    }

    public int getId() {
        return id;
    }

    public List<MapNode> getAdjacentNodes() {
        return adjacentNodes;
    }

    public float distFrom(MapNode node) {
        float [] dist = new float[1];
        Location.distanceBetween(getCoords().latitude,
                getCoords().longitude, node.getCoords().latitude,
                node.getCoords().longitude,dist);
        return dist[0];
    }
}
