package ch.patchcode.port_royale_3.map;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.port_royale_3.map.WorldMap.Pos;
import ch.patchcode.port_royale_3.routes.DistanceCsvData;
import ch.patchcode.port_royale_3.routes.DistanceGraph;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;
import ch.patchcode.port_royale_3.routes.PortRoyaleTriangleInequalityTest;

public class WorldMapTest {

    private DistanceGraph graph;

    @Before
    public void setup() throws IOException {
        try (InputStream is = PortRoyaleTriangleInequalityTest.class.getClassLoader()
                .getResourceAsStream("port-royale-3-distances.csv")) {
            graph = new DistanceGraph(new DistanceCsvData(is));
        }
    }

    @Test
    public void test() throws IOException {
        WorldMap<Vertex, Edge> map = new WorldMap<>(200.);
        Vertex center = graph.getCentralVertex();
        map.addFixed(center, 0, 0);

        List<Vertex> otherNodesOrderedByDistance = center.getEdges().stream()
                .sorted((a, b) -> Double.compare(a.getWeight(), b.getWeight()))
                .flatMap(it -> it.getVertices().stream())
                .filter(it -> !center.equals(it))
                .collect(Collectors.toList());

        for (Vertex node : otherNodesOrderedByDistance) {
            if (map.contains(node)) continue;
            map.add(node);
            map.rebalance(node, 0.01);
            map.rebalanceAll(0.01);
        }

        File file = new File("WorldMapTest.dot");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
            writer.println("digraph G {");
            for (Vertex node : map.vertices()) {
                Pos p = map.getPosition(node);
                writer.println(String.format("  \"%s\" [ pos=\"%d,%d!\" ]", node.getName(), (int)p.getX(), (int)p.getY()));
            }
            writer.println("}");
        }
        
    }

}
