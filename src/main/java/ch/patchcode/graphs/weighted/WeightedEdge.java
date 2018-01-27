package ch.patchcode.graphs.weighted;

import ch.patchcode.graphs.basic.Edge;

public interface WeightedEdge extends Edge<WeightedVertex> {

    double getWeight();
}
