package woverines.sfsuapp.Maps;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 *
 * This class represents a node in the graph used for pathfinding.
 */
public class MapNode {
    private LatLng coords;
    private List<MapNode> adjacentNodes;
    private int id;

    /**
     * @param id an id number for this node
     * @param coords the longitude and latitude of this node
     */
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

    /**
     * Get the neighboring (adjacent) nodes
     * @return a list of the adjacent nodes
     */
    public List<MapNode> getAdjacentNodes() {
        return adjacentNodes;
    }

    /**
     * This method calculates the distance from this node to another
     *
     * @param node node to find the distance to
     * @return distance between the current node and the other
     */
    public float distFrom(MapNode node) {
        float [] dist = new float[1];
        Location.distanceBetween(getCoords().latitude,
                getCoords().longitude, node.getCoords().latitude,
                node.getCoords().longitude,dist);
        return dist[0];
    }
}
