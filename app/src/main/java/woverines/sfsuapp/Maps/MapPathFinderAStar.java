package woverines.sfsuapp.Maps;

import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapPathFinderAStar implements MapPathfinder {
    private MapData mapData;
    private List<MapNode> currentPath;

    @Override
    public List<MapNode> getPath() {
        return null;
    }

    @Override
    public void setStart(MapNode node) {

    }

    @Override
    public void setDestination(MapNode node) {

    }

    @Override
    public void setMapData(MapData mdata) {
        mapData = mdata;
    }
}
