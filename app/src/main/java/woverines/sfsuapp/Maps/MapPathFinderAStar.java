package woverines.sfsuapp.Maps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapPathFinderAStar implements MapPathfinder {
    private MapData mapData;
    private List<MapNode> currentPath;

    @Override
    public List<MapNode> getPath() {
        // Dummy Path
        List<MapNode> path = new ArrayList<>();

        path.add(new MapNode(37.72347404032161, -122.47694313526152, null));
        path.add(new MapNode(37.72339633833966, -122.47693005949259, null));
        path.add(new MapNode(37.7233931560042, -122.47706685215235, null));
        path.add(new MapNode(37.72334860329329, -122.47712317854167, null));
        path.add(new MapNode(37.72311496631688, -122.47731965035202, null));
        path.add(new MapNode(37.72291182619023, -122.47771427035332, null));
        path.add(new MapNode(37.722680309413605,-122.47794292867184,null));
        path.add(new MapNode(37.72237347564042, -122.47804015874864, null));
        path.add(new MapNode(37.722165294698954, -122.47845087200405, null));

        return path;
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
