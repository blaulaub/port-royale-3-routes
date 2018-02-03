package ch.patchcode.port_royale_3.routes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.graphs.trees.Tree;
import ch.patchcode.graphs.trees.TreeUtils;
import ch.patchcode.graphs.weighted.GreedyMinimumDistanceSpanningTree;
import ch.patchcode.graphs.weighted.WeightedEdge;
import ch.patchcode.graphs.weighted.WeightedGraph;
import ch.patchcode.graphs.weighted.WeightedVertex;
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
    public void outputBisectionSpanningTree() throws IOException {
        Tree<Vertex> tree = new BisectionSpanningTree<>(graph);

        File file = new File("BisectionSpanningTree.dot");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
            TreeUtils.writeGraphvizDotFileContent(tree, writer);
        }
    }

    public static class BisectionSpanningTree<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> implements Tree<V> {

        private V root;
        private Map<V, List<V>> tree;

        public BisectionSpanningTree(WeightedGraph<V, E> graph) {
            List<E> edges = bisectRecursively(graph, graph.getVertices());

            System.out.println("got " + edges.size() + " edges");
            System.out.println("those are :\n" + edges.stream().map(e -> e.getVertices().stream().map(v -> v.getName()).collect(Collectors.joining(" - "))).collect(Collectors.joining("\n")));

            root = graph.getCentralVertex();
            tree = new HashMap<>();

            Deque<V> nodes = new ArrayDeque<>();
            nodes.push(root);

            while (!nodes.isEmpty()) {
                V node = nodes.pop();
                List<V> list = tree.computeIfAbsent(node, $ -> new ArrayList<>());
                Map<Boolean, List<E>> x = edges.stream().collect(Collectors.groupingBy(it -> it.getVertices().contains(node) ? Boolean.TRUE : Boolean.FALSE));
                List<E> toBeRetained = x.getOrDefault(Boolean.FALSE, Collections.emptyList());
                List<E> toBeAdded = x.getOrDefault(Boolean.TRUE, Collections.emptyList());
                toBeAdded.stream().map(it -> it.getVertices()).map(it -> it.stream().filter(v -> !node.equals(v)).findFirst().get()).forEach(n -> {
                    list.add(n);
                    nodes.push(n);
                });
                edges = toBeRetained;
            }
        }

        @Override
        public V getRoot() {
            return root;
        }

        @Override
        public Collection<V> getChildren(V vertex) {
            return Collections.unmodifiableCollection(tree.getOrDefault(vertex, Collections.emptyList()));
        }

        private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> List<E> bisectRecursively(WeightedGraph<V, E> graph, Set<V> vertices) {
            List<E> result = new ArrayList<>();
            BisectResult<V, E> r = bisectOnce(graph, vertices);
            r.groups.stream().filter(it -> it.size() > 1).map(it -> bisectRecursively(graph, it)).forEach(result::addAll);
            result.add(r.connection);
            return result;
        }

        private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> BisectResult<V, E> bisectOnce(WeightedGraph<V, E> graph, Set<V> vertices) {
            E edge = graph.getLongestEdge(vertices);
            List<V> vPair = new ArrayList<>(edge.getVertices());
            Map<Boolean, List<V>> n =  vertices.stream().collect(Collectors.groupingBy(v -> graph.getDistance(v, vPair.get(0)) < graph.getDistance(v,  vPair.get(1)) ? Boolean.TRUE : Boolean.FALSE));
            List<V> g1 = n.get(Boolean.TRUE);
            List<V> g2 = n.get(Boolean.FALSE);
            BisectResult<V, E> r = new BisectResult<V, E>(g1, g2);
            return r;
        }

        public static class BisectResult<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> {

            public final List<Set<V>> groups;
            public final E connection;

            public BisectResult(List<V> g1, List<V> g2) {
                connection = g1.stream().flatMap(v -> v.getEdges().stream()).filter(e -> g2.stream().anyMatch(v -> e.getVertices().contains(v))).reduce((a, b) -> a.getWeight() < b.getWeight() ? a : b).get();
                groups = Collections.unmodifiableList(Arrays.asList(Collections.unmodifiableSet(new HashSet<>(g1)), Collections.unmodifiableSet(new HashSet<>(g2))));
            }
        }
    }
}
