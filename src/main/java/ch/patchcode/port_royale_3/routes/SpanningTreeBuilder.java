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

public class SpanningTreeBuilder {

    private final Vertex centralVertex;
    public Map<Vertex, List<Vertex>> tree;
    private TreeSet<Edge> openEdges;
    private Predicate<Edge> pred;

    public SpanningTreeBuilder(Vertex centralVertex) {
        Map<Vertex, List<Vertex>> tree = new HashMap<>();
        tree.put(centralVertex, new ArrayList<>());
        TreeSet<Edge> openEdges = new TreeSet<>(centralVertex.getEdges());
        this.centralVertex = centralVertex;
        this.tree = tree;
        this.openEdges = openEdges;
        this.pred = e -> edgeIsNotCoveredByTree(tree, e);
    }

    public Map<Vertex, List<Vertex>> getTree() {
        return tree;
    }

    public boolean hasOpenEdges() {
        return openEdges.size() > 0;
    }

    public Edge shortestOpenEdge() {
        return openEdges.iterator().next();
    }

    public void connectEdge(Edge openEdge) {
        List<Vertex> vertices = new LinkedList<>(openEdge.getVertices());
        Vertex insertionPoint = tree.entrySet().stream()
                .filter(it -> vertices.contains(it.getKey())).findFirst().get().getKey();
        vertices.remove(insertionPoint);

        Vertex newPoint = vertices.get(0);
        addSubVertex(insertionPoint, newPoint);
    }

    private void addSubVertex(Vertex insertionPoint, Vertex newPoint) {
        tree.get(insertionPoint).add(newPoint);
        tree.put(newPoint, new ArrayList<>());
        openEdges.addAll(newPoint.getEdges());
        openEdges = new TreeSet<>(openEdges.stream().filter(pred).collect(Collectors.toList()));
    }

    private static boolean edgeIsNotCoveredByTree(Map<Vertex, List<Vertex>> tree, Edge edge) {
        return edge.getVertices().stream().filter(it -> !tree.keySet().contains(it)).count() > 0;
    }

    public Vertex getRoot() {
        return centralVertex;
    }
}