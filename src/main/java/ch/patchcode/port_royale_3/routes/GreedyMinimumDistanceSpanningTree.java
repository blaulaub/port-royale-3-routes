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

        TreeBuilder c = new TreeBuilder(centralVertex);

        while (c.openEdges.size() > 0) {
            Edge shortestUnconnectedEdge = c.openEdges.iterator().next();
            List<Vertex> vertices = new LinkedList<>(shortestUnconnectedEdge.getVertices());

            Vertex insertionPoint = c.tree.entrySet().stream()
                    .filter(it -> vertices.contains(it.getKey())).findFirst().get().getKey();
            vertices.remove(insertionPoint);

            Vertex newPoint = vertices.get(0);
            c.addSubVertex(insertionPoint, newPoint);

            System.out.println("Visit " + newPoint.getName() + " from " + insertionPoint.getName());

            c.openEdges.addAll(newPoint.getEdges());
            c.openEdges = new TreeSet<>(c.openEdges.stream().filter(c.pred).collect(Collectors.toList()));
        }

        this.tree = c.tree;
    }

    // TODO elaborate
    private static class TreeBuilder {
        public Map<Vertex, List<Vertex>> tree;
        public TreeSet<Edge> openEdges;
        Predicate<Edge> pred;

        public TreeBuilder(Vertex centralVertex) {
            Map<Vertex, List<Vertex>> tree = new HashMap<>();
            tree.put(centralVertex, new ArrayList<>());
            TreeSet<Edge> openEdges = new TreeSet<>(centralVertex.getEdges());
            this.tree = tree;
            this.openEdges = openEdges;
            this.pred = e -> edgeIsNotCoveredByTree(tree, e);
        }

        public void addSubVertex(Vertex insertionPoint, Vertex newPoint) {
            tree.get(insertionPoint).add(newPoint);
            tree.put(newPoint, new ArrayList<>());
        }

        private static boolean edgeIsNotCoveredByTree(Map<Vertex, List<Vertex>> tree, Edge edge) {
            return edge.getVertices().stream().filter(it -> !tree.keySet().contains(it)).count() > 0;
        }
    }
}