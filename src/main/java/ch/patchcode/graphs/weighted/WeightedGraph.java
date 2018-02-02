package ch.patchcode.graphs.weighted;

import ch.patchcode.graphs.basic.Graph;

public interface WeightedGraph<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> extends Graph<V, E> {

    default V getCentralVertex() {
        return getVertices().stream().map(NeighbourDistanceScore::new).sorted().findFirst().get().getVertex();
    }
}
