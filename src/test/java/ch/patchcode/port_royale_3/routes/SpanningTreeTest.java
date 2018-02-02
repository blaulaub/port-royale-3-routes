package ch.patchcode.port_royale_3.routes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.graphs.trees.Tree;
import ch.patchcode.graphs.trees.TreeUtils;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

public class SpanningTreeTest {

    private DistanceGraph graph;

    @Before
    public void setup() throws IOException {
        try (InputStream is = PortRoyaleTriangleInequalityTest.class.getClassLoader()
                .getResourceAsStream("port-royale-3-distances.csv")) {
            graph = new DistanceGraph(new DistanceCsvData(is));
        }
    }

    @Test
    public void outputGreedyMinimumDistanceSpanningTree() throws IOException {
        Tree<Vertex> tree = new GreedyMinimumDistanceSpanningTree(graph);

        File file = new File("GreedyMinimumDistanceSpanningTree.dot");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
            TreeUtils.writeGraphvizDotFileContent(tree, writer);
        }
    }

    @Test
    public void develBisection() {
        Edge edge = graph.getLongestEdge();
        List<Vertex> vPair = new ArrayList<>(edge.getVertices());
        Map<Boolean, List<Vertex>> n =  graph.getVertices().stream().collect(Collectors.groupingBy(v -> graph.getDistance(v, vPair.get(0)) < graph.getDistance(v,  vPair.get(1)) ? Boolean.TRUE : Boolean.FALSE));
        List<Vertex> g1 = n.get(Boolean.TRUE);
        List<Vertex> g2 = n.get(Boolean.FALSE);
        BisectResult r = new BisectResult(g1, g2);

        System.out.println("shortest is between " + r.connection.getVertices().stream().map(v -> v.getName()).collect(Collectors.joining(" and ")) + " and takes " + r.connection.getWeight() + " days.");
    }

    public static class BisectResult {

        public final List<List<Vertex>> groups;
        public final Edge connection;

        public BisectResult(List<Vertex> g1, List<Vertex> g2) {
            connection = g1.stream().flatMap(v -> v.getEdges().stream()).filter(e -> g2.stream().anyMatch(v -> e.getVertices().contains(v))).reduce((a, b) -> a.getWeight() < b.getWeight() ? a : b).get();
            groups = Collections.unmodifiableList(Arrays.asList(Collections.unmodifiableList(new ArrayList<>(g1)), Collections.unmodifiableList(new ArrayList<>(g1))));
        }
    }
}
