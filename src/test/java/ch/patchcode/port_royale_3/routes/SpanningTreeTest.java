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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.graphs.trees.Tree;
import ch.patchcode.graphs.trees.TreeUtils;
import ch.patchcode.graphs.weighted.TopDownBisectionSpanningTree;
import ch.patchcode.graphs.weighted.WeightedEdge;
import ch.patchcode.graphs.weighted.WeightedVertex;
import ch.patchcode.graphs.weighted.GreedyMinimumDistanceSpanningTree;
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
        Tree<Vertex> tree = new GreedyMinimumDistanceSpanningTree<>(graph);

        File file = new File("GreedyMinimumDistanceSpanningTree.dot");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
            TreeUtils.writeGraphvizDotFileContent(tree, writer);
        }
    }

    @Test
    public void outputTopDownBisectionSpanningTree() throws IOException {
        Tree<Vertex> tree = new TopDownBisectionSpanningTree<>(graph);

        File file = new File("TopDownBisectionSpanningTree.dot");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
            TreeUtils.writeGraphvizDotFileContent(tree, writer);
        }
    }

    @Test
    public void outputBottomUpSpanningTree() throws IOException {
        Set<Vertex> vertices = graph.getVertices();
        List<Group<Vertex, Edge>> groups = vertices.stream().map(Collections::singleton).map(Group::new).collect(Collectors.toList());

        List<GroupConnection<Vertex, Edge>> next = new ArrayList<>();
        while (groups.size() > 1) {
            GroupConnection<Vertex, Edge> cand = allShortestIntergroupConnections(groups).values().stream().reduce((g1, g2) -> g1.edge.getWeight() > g2.edge.getWeight() ? g1 : g2).get();
            groups.removeIf(it -> it.vertices.stream().anyMatch(v -> cand.edge.getVertices().contains(v)));
            next.add(cand);
            System.out.println("connect " + cand.edge.getVertices().stream().map(it -> it.getName()).collect(Collectors.joining(" - ")) + " (" + cand.edge.getWeight() + " days)");
        }
        // TBC

        // sort descending (group with furthest nearest neighbour first) and create pairs for next round
        // if an odd group remains, just keep it for the next round
    }

    private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> Map<Group<V, E>, GroupConnection<V, E>> allShortestIntergroupConnections(List<Group<V, E>> groups) {
        return groups.stream().collect(Collectors.toMap(Function.identity(), pivot -> groups.stream().map(g -> new GroupConnection<>(pivot, g, shortestEdgeBetweenTwoGroups(pivot, g))).reduce((c1, c2) -> c1.edge.getWeight() < c2.edge.getWeight() ? c1 : c2).get()));
    }

    public static class GroupConnection<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> {
        public final Set<Group<V, E>> groups;
        public final E edge;
        public GroupConnection(Group<V, E> g1, Group<V, E> g2, E edge) {
            this.groups = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(g1, g2)));
            this.edge = edge;
        }
    }

    private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> E shortestEdgeBetweenTwoGroups(Group<V, E> pivot, Group<V, E> o) {
        return pivot.vertices.stream().flatMap(v -> v.getEdges().stream()).filter(it -> it.getVertices().stream().anyMatch(o.vertices::contains)).reduce((a, b) -> a.getWeight() < b.getWeight() ? a : b).get();
    }

    public static class Group<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> {

        public final Set<V> vertices;

        public Group(Set<V> vertices) {
            this.vertices = Collections.unmodifiableSet(new HashSet<>(vertices));
        }
    }
}
