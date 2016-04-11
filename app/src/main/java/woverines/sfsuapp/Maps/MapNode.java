package woverines.sfsuapp.Maps;

import java.util.List;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapNode {
    private double x, y;
    private List<MapNode> adjacentNodes;

    public MapNode(double x, double y,List<MapNode> nodes) {
        this.x = x;
        this.y = y;
        this.adjacentNodes = nodes;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public List<MapNode> getAdjacentNodes() {
        return adjacentNodes;
    }
}
