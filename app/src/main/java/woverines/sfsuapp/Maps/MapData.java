package woverines.sfsuapp.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapData {
    Map<String, MapBuilding> buildingList;
    List<MapNode> nodeList;

    // dummy
    public void initMap() {

    }

    public MapBuilding getBuilding(String name) {
        return buildingList.get(name);
    }

    public MapNode getNearestNode(double x, double y) {
        // dummy return
        return nodeList.get(0);
    }
}
