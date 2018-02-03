package ch.patchcode.graphs.weighted;

import java.util.Collection;

import ch.patchcode.graphs.basic.Graph;

public interface WeightedGraph<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> extends Graph<V, E> {

    default V getCentralVertex() {
        return getVertices().stream().map(NeighbourDistanceScore::new).sorted().findFirst().get().getVertex();
    }

    /**
     * @return the edge of the graph with the largest weight
     */
    default E getLongestEdge() {
        return getVertices().stream().flatMap(it -> it.getEdges().stream()).reduce((a, b) -> a.getWeight() > b.getWeight() ? a : b).get();
    }

    /**
     * @param vertices
     * @return the edge with the largest weight, taking into account all edges of the graph that connect the given vertices
     */
    default E getLongestEdge(Collection<V> vertices) {
        return vertices.stream().flatMap(it -> it.getEdges().stream()).filter(e -> e.getVertices().stream().allMatch(v -> vertices.contains(v))).reduce((a, b) -> a.getWeight() > b.getWeight() ? a : b).get();
    }

    default double getDistance(V v1, V v2) {
        return v1.equals(v2) ? 0. : v1.getEdges().stream().filter(e -> e.getVertices().contains(v2)).findFirst().get().getWeight();
    }
}
