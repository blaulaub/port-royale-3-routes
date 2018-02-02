package ch.patchcode.graphs.weighted;

import ch.patchcode.graphs.basic.Edge;

public interface WeightedEdge<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> extends Edge<V> {

    double getWeight();
}
