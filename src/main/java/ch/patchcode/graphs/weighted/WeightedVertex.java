package ch.patchcode.graphs.weighted;

import ch.patchcode.graphs.basic.Vertex;

public interface WeightedVertex extends Vertex<WeightedEdge> {

    double getWeight();
}
