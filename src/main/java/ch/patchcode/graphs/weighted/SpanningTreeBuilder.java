package ch.patchcode.graphs.weighted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SpanningTreeBuilder<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> {

    private final V centralVertex;
    public Map<V, List<V>> tree;
    private TreeSet<E> openEdges;
    private Predicate<E> pred;

    public SpanningTreeBuilder(V centralVertex) {
        Map<V, List<V>> tree = new HashMap<>();
        tree.put(centralVertex, new ArrayList<>());
        TreeSet<E> openEdges = new TreeSet<>(centralVertex.getEdges());
        this.centralVertex = centralVertex;
        this.tree = tree;
        this.openEdges = openEdges;
        this.pred = e -> edgeIsNotCoveredByTree(tree, e);
    }

    public Map<V, List<V>> getTree() {
        return tree;
    }

    public boolean hasOpenEdges() {
        return openEdges.size() > 0;
    }

    public E shortestOpenEdge() {
        return openEdges.iterator().next();
    }

    public void connectEdge(E openEdge) {
        List<V> vertices = new LinkedList<>(openEdge.getVertices());
        V insertionPoint = tree.entrySet().stream()
                .filter(it -> vertices.contains(it.getKey())).findFirst().get().getKey();
        vertices.remove(insertionPoint);

        V newPoint = vertices.get(0);
        addSubVertex(insertionPoint, newPoint);
    }

    private void addSubVertex(V insertionPoint, V newPoint) {
        tree.get(insertionPoint).add(newPoint);
        tree.put(newPoint, new ArrayList<>());
        openEdges.addAll(newPoint.getEdges());
        openEdges = new TreeSet<>(openEdges.stream().filter(pred).collect(Collectors.toList()));
    }

    private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> boolean edgeIsNotCoveredByTree(Map<V, List<V>> tree, E edge) {
        return edge.getVertices().stream().filter(it -> !tree.keySet().contains(it)).count() > 0;
    }

    public V getRoot() {
        return centralVertex;
    }
}