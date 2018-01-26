package ch.patchcode.port_royale_3.routes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistanceCsvData {

    private final Map<String, Map<String, Double>> distances;

    public DistanceCsvData(InputStream is) {
        this.distances = createFullDistanceMap(is);
    }

    private static Map<String, Map<String, Double>> createFullDistanceMap(InputStream is) {
        InputStreamReader ir = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(ir);

        List<String> places = new ArrayList<>();
        Map<String, Map<String, Double>> distances = new HashMap<>();

        reader.lines().forEach(line -> {
            String[] parts = line.split(",");

            String thisPlace = parts[0];
            places.add(thisPlace);

            Map<String, Double> subdist = new HashMap<>();
            distances.put(thisPlace, subdist);

            for (int i = 1; i < parts.length; ++i) {
                String otherPlace = places.get(i-1);
                Double distance = Double.parseDouble(parts[i]);
                updateBothDistances(distances, thisPlace, otherPlace, distance);
            }
        });
        return distances;
    }

    private static void updateBothDistances(Map<String, Map<String, Double>> distances, String thisPlace, String otherPlace,
            Double distance) {
        distances.get(thisPlace).put(otherPlace, distance);
        distances.get(otherPlace).put(thisPlace, distance);
    }

    public double getDistance(String fromPlace, String toPlace) {
        return distances.get(fromPlace).get(toPlace);
    }
}
