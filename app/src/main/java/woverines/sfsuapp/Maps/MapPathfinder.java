package woverines.sfsuapp.Maps;

import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 *
 * This interface is for any graph based pathfinding algorithm.
 */
public interface MapPathfinder {
    /**
     * Finds the path from start to destination
     * @return a list of nodes representing the path. null if no path exists
     */
    List<MapNode> getPath();

    /**
     * Set the starting node.
     * @param node the starting node
     */
    void setStart(MapNode node);

    /**
     * Set the destination node.
     * @param node the destination node.
     */
    void setDestination(MapNode node);

    /**
     * Set the map data that the algorithm is working with
     * @param mdata the map data
     */
    void setMapData(MapData mdata);
}
