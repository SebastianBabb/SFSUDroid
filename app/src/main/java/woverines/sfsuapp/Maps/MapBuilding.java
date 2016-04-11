package woverines.sfsuapp.Maps;

import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapBuilding {
    private String name;
    private List<MapNode> nodesList;
    private double x, y;

    public MapBuilding(String name, List<MapNode> nodes, double x, double y) {
        this.name = name;
        this.nodesList = nodes;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public List<MapNode> getNodes() {
        return nodesList;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
