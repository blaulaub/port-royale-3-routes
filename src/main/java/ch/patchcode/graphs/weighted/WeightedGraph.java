package ch.patchcode.graphs.weighted;

import ch.patchcode.graphs.basic.Graph;

public interface WeightedGraph<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> extends Graph<V, E> {

    default V getCentralVertex() {
        return getVertices().stream().map(NeighbourDistanceScore::new).sorted().findFirst().get().getVertex();
    }

    default E getLongestEdge() {
        return getVertices().stream().flatMap(it -> it.getEdges().stream()).reduce((a, b) -> a.getWeight() > b.getWeight() ? a : b).get();
    }

    default double getDistance(V v1, V v2) {
        return v1.equals(v2) ? 0. : v1.getEdges().stream().filter(e -> e.getVertices().contains(v2)).findFirst().get().getWeight();
    }
}
