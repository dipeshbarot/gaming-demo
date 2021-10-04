package com.amazon.gamingdemo.service;

import com.amazon.gamingdemo.dao.LandmarkDAO;
import com.amazon.gamingdemo.model.Node;
import com.amazon.gamingdemo.model.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class LandmarkService {

    private static final Integer INFINITE = Integer.MAX_VALUE;
    private static final Logger LOGGER = LoggerFactory.getLogger(LandmarkService.class);

    private LandmarkDAO landMarkDAO;
    private final Integer maxStops;

    @Autowired
    public LandmarkService(LandmarkDAO landMarkDAO,
                           @Value("${gaming-demo.maxStops}") Integer maxStops) {
        this.landMarkDAO = landMarkDAO;
        this.maxStops = maxStops;
    }

    /**
     * Add Landmarks to a graph
     * @param routes - set of routes as received in input
     */
    public void addLandMarks(Set<String> routes) {
        if(null == routes || routes.isEmpty()
                || routes.stream().allMatch(route -> route.length() < 3))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        for(String route : routes) {
            landMarkDAO.addLandmarks(String.valueOf(route.charAt(0)),
                    String.valueOf(route.charAt(1)),
                    Integer.parseInt(String.valueOf(route.charAt(2))));
        }
    }

    /**
     * Get the distance between landmarks via a given route
     * @param route - Route String
     * @return - Distance between landmarks or message if route is not available
     */
    public Optional<String> getDistanceBetweenLandMarks(String route) {
        if(null == route || !route.contains("-"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid route");
        String[] nodes = route.split("-");
        int distance = 0;

        for(int i=0;i<nodes.length;i++) {
            if ((i + 1) < nodes.length) {
                if(isNodeConnected(nodes[i], nodes[i+1])) {
                    Optional<Map<String, Integer>> result =
                            getMinDistance(landMarkDAO.getLandmarks(), nodes[i]);
                    if (result.isPresent()) {
                        Map<String, Integer> map = result.get();
                        distance += map.get(nodes[i + 1]);
                    }
                } else {
                    return Optional.of("Path not found");
                }
            }
        }
        return Optional.of(String.valueOf(distance));
    }

    /**
     * Checks if nodes are connected or not
     * @param source - A source node
     * @param destination - A destination node
     * @return - True if a connection exists between nodes
     */
    private Boolean isNodeConnected(String source, String destination) {
        return landMarkDAO.getLandmarks().get(source).getChildren().stream()
                .anyMatch(child -> child.getFirst()
                        .equalsIgnoreCase(destination));
    }

    /**
     * Gets a minimum distance between nodes
     * @param graph - A connected set of landmarks
     * @param source - A source node
     * @return - A minimum distance from a source node to other nodes
     */
    private Optional<Map<String, Integer>> getMinDistance(Map<String, Node> graph, String source) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, Boolean> visited = new HashMap<>();

        // Initialize with default values
        for(String key : graph.keySet()) {
            visited.putIfAbsent(key, Boolean.FALSE);
            dist.put(key, INFINITE);
        }

        // Handle First element
        dist.put(source, 0);
        String current = source;

        Set<String> nodesToBeVisited = new HashSet<>();
        while(true) {
            visited.put(current, true);
            for(int i=0;i<graph.get(current).getChildren().size();i++) {
                String v = graph.get(current).getChildren().get(i).getFirst();
                if(visited.get(v))
                    continue;
                nodesToBeVisited.add(v);
                int alt = dist.get(current) + graph.get(current).getChildren().get(i).getSecond();
                if(alt < dist.get(v)) {
                    dist.put(v, alt);
                }
            }
            nodesToBeVisited.remove(current);

            if(nodesToBeVisited.isEmpty())
                break;

            current = getNextIndex(dist, nodesToBeVisited);
        }
        return Optional.of(dist);
    }

    /**
     * Get next node to be visisted
     * @param distanceMap - Mapping between landmarks
     * @param nodesToBeVisited - All nodes which are yet to be visited
     * @return - Next node to be visited
     */
    private String getNextIndex(Map<String, Integer> distanceMap, Set<String> nodesToBeVisited) {
        int minDist = INFINITE;
        String index = "";

        for(String node : nodesToBeVisited) {
            if(distanceMap.get(node) < minDist) {
                minDist = distanceMap.get(node);
                index = node;
            }
        }
        return index;
    }

    /**
     * This method returns the number of routes starting at A and ending at C with a maximum of 2 stops
     *
     * @param source - A starting node i.e. A
     * @param dest - A destination node i.e. C
     * @return - a number of routes between A and C with a maximum of 2 stops
     */
    public Optional<Integer> getNorOfRoutesBetweenNodes(String source, String dest) {
        int maxStops = this.maxStops;
        int count = 0;
        int stopsCount = 0;
        Map<String, Boolean> visited = new HashMap<>();
        Map<String, Node> graph = landMarkDAO.getLandmarks();

        if(null == graph || graph.isEmpty() || !graph.keySet()
                .containsAll(Set.of(source, dest)))
            return Optional.empty();

        // Initialize
        for(String key : graph.keySet()) {
            visited.putIfAbsent(key, Boolean.FALSE);
        }

        while(stopsCount < maxStops) {
            visited.put(source, Boolean.TRUE);
            count += getCount(graph.get(source).getChildren(), dest);

            for(int i = 0; i<graph.get(source).getChildren().size(); i++) {
                String v = graph.get(source).getChildren().get(i).getFirst();
                if(visited.get(v))
                    continue;

                count += getCount(graph.get(v).getChildren(), dest);
            }
            stopsCount++;
        }
        return Optional.of(count);
    }

    /**
     * This method returns the count of routes which found a match with destination node i.e. C
     * @param pairs - Adjacent nodes
     * @param destination - A destination node i.e. C
     * @return - A number of matches found
     */
    private int getCount(List<Pair> pairs, String destination) {
        return (int) pairs.stream().filter(x -> x.getFirst().equalsIgnoreCase(destination)).count();
    }
}
