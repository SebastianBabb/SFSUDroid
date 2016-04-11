package woverines.sfsuapp.Maps;

import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public interface MapPathfinder {
    List<MapNode> getPath();
    void setStart(MapNode node);
    void setDestination(MapNode node);
    void setMapData(MapData mdata);
}
