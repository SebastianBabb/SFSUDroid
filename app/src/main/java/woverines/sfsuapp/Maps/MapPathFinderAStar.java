package woverines.sfsuapp.Maps;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapPathFinderAStar implements MapPathfinder {
    private MapData mapData;
    private List<MapNode> currentPath;
    private MapNode start;
    private MapNode dest;

    @Override
    public List<MapNode> getPath() {

            // dummy path
//        List<MapNode> path = new ArrayList<>();
//
//        path.add(new MapNode(new LatLng(37.72347404032161, -122.47694313526152), null));
//        path.add(new MapNode(new LatLng(37.72339633833966, -122.47693005949259), null));
//        path.add(new MapNode(new LatLng(37.7233931560042, -122.47706685215235), null));
//        path.add(new MapNode(new LatLng(37.72334860329329, -122.47712317854167), null));
//        path.add(new MapNode(new LatLng(37.72311496631688, -122.47731965035202), null));
//        path.add(new MapNode(new LatLng(37.72291182619023, -122.47771427035332), null));
//        path.add(new MapNode(new LatLng(37.722680309413605,-122.47794292867184),null));
//        path.add(new MapNode(new LatLng(37.72237347564042, -122.47804015874864), null));
//        path.add(new MapNode(new LatLng(37.722165294698954, -122.47845087200405), null));

        PriorityQueue<MapNode> toVisit;
        toVisit = new PriorityQueue<>(20, new ToDestinationComparator(dest));

        toVisit.add(start);

        List<MapNode> visited = new ArrayList<>();
        Map<MapNode, MapNode> cameFrom= new HashMap<>();

        Map<MapNode, Double> gScore = new HashMap<>();
        Map<MapNode, Double> fScore = new HashMap<>();

        gScore.put(start, 0.0);
        fScore.put(start, gScore.get(start) + start.distFrom(dest));

        MapNode currentNode;

        while(!toVisit.isEmpty())
        {
            currentNode = toVisit.poll();

            if(currentNode.equals(dest))
                return currentPath = createPath(cameFrom, dest);

            visited.add(currentNode);

            List<MapNode> neighbors = currentNode.getAdjacentNodes();

            for(MapNode neighbor : neighbors)
            {
                double tempgScore = gScore.get(currentNode) + currentNode.distFrom(neighbor);
                double tempfScore = tempgScore + neighbor.distFrom(dest);

                if(visited.contains(neighbor) && tempfScore >= fScore.get(neighbor))
                    continue;

                if(!toVisit.contains(neighbor) || tempfScore < fScore.get(neighbor))
                {
                    cameFrom.put(neighbor, currentNode);
                    gScore.put(neighbor, tempgScore);
                    fScore.put(neighbor, tempfScore);

                    if(!toVisit.contains(neighbor))
                        toVisit.add(neighbor);
                }
            }
        }

        return null;
    }

    private List<MapNode> createPath(Map<MapNode, MapNode> camefrom, MapNode curNode)
    {
        List<MapNode> path = new ArrayList<>();

        if(camefrom.containsKey(curNode))
        {
            path.addAll(createPath(camefrom, camefrom.get(curNode)));
            path.add(curNode);
        }
        else
        {
            path.add(curNode);
        }
        return path;
    }

    private class ToDestinationComparator implements Comparator<MapNode>
    {
        private MapNode goal;

        public ToDestinationComparator(MapNode goal)
        {
            this.goal = goal;
        }

        @Override
        public int compare(MapNode node1, MapNode node2)
        {
            float dist1 = node1.distFrom(goal);
            float dist2 = node2.distFrom(goal);

            if(Math.abs(dist1 - dist2) < 0.000001)
                return 0;
            if(dist1 < dist2)
                return -1;
            else
                return 1;
        }

    }

    @Override
    public void setStart(MapNode node) {
        start = node;
    }

    @Override
    public void setDestination(MapNode node) {
        dest = node;
    }

    @Override
    public void setMapData(MapData mdata) {
        mapData = mdata;
    }
}

