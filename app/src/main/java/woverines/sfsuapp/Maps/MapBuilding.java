package woverines.sfsuapp.Maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapBuilding {
    private String name;
    private List<MapNode> nodeList;
    private LatLng coords;

    public MapBuilding(String name, List<MapNode> nodes, LatLng coords) {
        this.name = name;
        this.nodeList = nodes;
        this.coords = coords;
    }

    public String getName() {
        return name;
    }

    public List<MapNode> getNodes() {
        return nodeList;
    }

    public LatLng getCoords() {
        return coords;
    }

    public MapNode getNearestNode(LatLng coords) {
        MapNode tempNode = new MapNode(-1,coords);
        MapNode nearest = nodeList.get(0);

        for(MapNode node : nodeList) {
            float nearestDist = tempNode.distFrom(nearest);
            float curDist = tempNode.distFrom(node);
            if(curDist < nearestDist) {
                nearest = node;
            }
        }

        return nearest;
    }
}
