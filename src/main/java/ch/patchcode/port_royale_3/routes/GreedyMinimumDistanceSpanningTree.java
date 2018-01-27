package ch.patchcode.port_royale_3.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

/**
 * A spanning tree constructed from some {@link DistanceGraph} by first
 * selecting the node with the most connections and the minimum mean distance
 * thereof, and then successively adding the next shortest edge to some yet
 * unconnected node, until all nodes are connected by the tree.
 */
// TODO may need some superclass (Tree? Spanning Tree?)
public class GreedyMinimumDistanceSpanningTree {

    private Map<Vertex, List<Vertex>> tree;

    public GreedyMinimumDistanceSpanningTree(DistanceGraph graph) {
        Vertex centralVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().findFirst().get()
                .getVertex();

        TreeSet<Edge> edges = new TreeSet<>(centralVertex.getEdges());

        Map<Vertex, List<Vertex>> tree = new HashMap<>();
        tree.put(centralVertex, new ArrayList<>());

        Predicate<Edge> pred = e -> edgeIsNotCoveredByTree(tree, e);

        while (edges.size() > 0) {
            Edge edge = edges.iterator().next();
            List<Vertex> vertices = new LinkedList<>(edge.getVertices());

            Map.Entry<Vertex, List<Vertex>> insertionPoint = tree.entrySet().stream()
                    .filter(it -> vertices.contains(it.getKey())).findFirst().get();
            vertices.remove(insertionPoint.getKey());

            Vertex newPoint = vertices.get(0);
            insertionPoint.getValue().add(newPoint);

            tree.put(newPoint, new ArrayList<>());

            edges.addAll(newPoint.getEdges());
            edges = new TreeSet<>(edges.stream().filter(pred).collect(Collectors.toList()));
        }

        this.tree = tree;
    }

    private static boolean edgeIsNotCoveredByTree(Map<Vertex, List<Vertex>> tree, Edge edge) {
        return edge.getVertices().stream().filter(it -> !tree.keySet().contains(it)).count() > 0;
    }
}