package woverines.sfsuapp.Maps;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by Lowell Milliken on 4/11/2016.
 *
 * This class contains an implementation of the A* algorithm for pathfinding
 * given some graph representing a map, a starting node and a goal (dest) node.
 *
 */
public class MapPathFinderAStar implements MapPathfinder {
    private MapData mapData;
    private List<MapNode> currentPath;
    private MapNode start;
    private MapNode dest;

    /**
     * This method finds the shortest path to the destination using the A* algorithm.
     *
     * @return returns a list of MapNodes representing the shortest path in order
     */
    @Override
    public List<MapNode> getPath() {

        // a priority queue to get best node candidates in order
        PriorityQueue<MapNode> toVisit;
        toVisit = new PriorityQueue<>(20, new ToDestinationComparator(dest));

        // first candidate is the starting node
        toVisit.add(start);

        // a list of all nodes that have already been visited
        List<MapNode> visited = new ArrayList<>();

        // a mapping of node to node to keep track of predecessor nodes
        Map<MapNode, MapNode> predecessors= new HashMap<>();

        /*
        keeping track of the g score and f score of each node
        g score = current total path cost
        f score = current total path cost + remaining distance to the destination
        */
        Map<MapNode, Double> gScore = new HashMap<>();
        Map<MapNode, Double> fScore = new HashMap<>();

        // initial g score is 0
        gScore.put(start, 0.0);
        // initial f score is the straight line distance from the start to the dest
        fScore.put(start, gScore.get(start) + start.distFrom(dest));

        MapNode currentNode;

        /*
        continue as long as there are more nodes to visit
        if this end condition is reached, there is no path between start and dest
        */
        while(!toVisit.isEmpty())
        {
            // get the next node closest to dest
            currentNode = toVisit.poll();

            // if the node is dest, recreate the path and return it
            if(currentNode.equals(dest))
                return currentPath = createPath(predecessors, dest);

            // otherwise add it to already visited nodes
            visited.add(currentNode);

            // get all neighboring nodes
            List<MapNode> neighbors = currentNode.getAdjacentNodes();

            // for each neighbor
            for(MapNode neighbor : neighbors)
            {
                // get the g score and f score for the current neighbor
                double tempgScore = gScore.get(currentNode) + currentNode.distFrom(neighbor);
                double tempfScore = tempgScore + neighbor.distFrom(dest);

                // if this neighbor has already been visited and is on an equal or longer path
                // than it was before, skip it
                if(visited.contains(neighbor) && tempfScore >= fScore.get(neighbor))
                    continue;

                // if the neighbor is not already under consideration for visiting
                // or is on a potentially shorter path
                if(!toVisit.contains(neighbor) || tempfScore < fScore.get(neighbor))
                {
                    // record the predecessor
                    predecessors.put(neighbor, currentNode);

                    // store its g and f scores
                    gScore.put(neighbor, tempgScore);
                    fScore.put(neighbor, tempfScore);

                    // add it to the toVisit list if it is not already there
                    if(!toVisit.contains(neighbor))
                        toVisit.add(neighbor);
                }
            }
        }

        // return null if there is no path
        return null;
    }

    /**
     * A recursive method for retrieving the path given the last node in the path
     * and the predecessor node for each node.
     *
     * @param predecessors a mapping of nodes to nodes which indicates which nodes
     *                 preceded which. (the ordering of the path)
     * @param curNode the current node to be added to the path.
     * @return
     */
    private List<MapNode> createPath(Map<MapNode, MapNode> predecessors, MapNode curNode)
    {
        List<MapNode> path = new ArrayList<>();

        if(predecessors.containsKey(curNode))
        {
            path.addAll(createPath(predecessors, predecessors.get(curNode)));
            path.add(curNode);
        }
        else
        {
            path.add(curNode);
        }
        return path;
    }

    /**
     * This class is used in the priority queue to compare two nodes based on their
     * distance to the goal (dest) node
     */
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

