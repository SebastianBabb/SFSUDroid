package woverines.sfsuapp.Maps;

import android.content.res.Resources;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import woverines.sfsuapp.R;

/**
 * Created by Lowell Milliken on 4/11/2016.
 */
public class MapData {
    private Map<String, MapBuilding> buildingList = new HashMap<>();;
    private Map<Integer,MapNode> nodeList;

    // dummy
    public void initMap(Resources resources) {
        nodeList = new HashMap<>();
        loadMapFromFile(resources);
    }

    private void loadMapFromFile(Resources resources) {
        try {
            InputStream nInputStream = resources.openRawResource(R.raw.nodes);
            BufferedReader nReader = new BufferedReader(new InputStreamReader(nInputStream));

            String line;
            while((line = nReader.readLine())!= null) {
                String [] tokens = line.split(",");

                MapNode node = new MapNode(Integer.parseInt(tokens[0]),
                        new LatLng(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])));

                nodeList.put(node.getId(),node);
            }
            nReader.close();
            nInputStream = resources.openRawResource(R.raw.nodes);
            nReader = new BufferedReader(new InputStreamReader(nInputStream));

            while((line = nReader.readLine())!= null) {
                String [] tokens = line.split(",");

                MapNode node = nodeList.get(Integer.parseInt(tokens[0]));

                for(int i = 3; i < tokens.length; i++)
                {
                    node.addAdjacentNode(nodeList.get(Integer.parseInt(tokens[i])));
                }
            }

            nReader.close();

            InputStream inputStream = resources.openRawResource(R.raw.buildings);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while((line = reader.readLine()) != null) {
                String [] tokens = line.split(",");

                List<MapNode> nodes = new ArrayList<>();

                for(int i = 3; i < tokens.length; i++) {
                    nodes.add(nodeList.get(Integer.parseInt(tokens[i])));
                }

                MapBuilding temp = new MapBuilding(tokens[0],nodes,
                        new LatLng(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])));
                buildingList.put(temp.getName(),temp);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String [] getBuildingNames() {
        String [] buildings = buildingList.keySet().toArray(new String[0]);
        Arrays.sort(buildings);
        return buildings;
    }

    public MapBuilding getBuilding(String name) {
        return buildingList.get(name);
    }

    public MapNode getBuildingNode(String name, LatLng coords)
    {
        return buildingList.get(name).getNearestNode(coords);
    }

    public MapNode getNearestNode(LatLng coords) {
        MapNode tempNode = new MapNode(-1,coords);
        MapNode[] nodes = nodeList.values().toArray(new MapNode[0]);
        MapNode nearest = nodes[0];

        for(MapNode node : nodes) {
            float nearestDist = tempNode.distFrom(nearest);
            float curDist = tempNode.distFrom(node);
            if(curDist < nearestDist) {
                nearest = node;
            }
        }

        return nearest;
    }

    public Map<Integer,MapNode> getNodes() {
        return nodeList;
    }
}
