package ch.patchcode.graphs.weighted;

import ch.patchcode.graphs.basic.Vertex;

public interface WeightedVertex<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> extends Vertex<E> {

    double getWeight();
}
