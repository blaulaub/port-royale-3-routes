package ch.patchcode.graphs.basic;

import java.util.Set;

public interface Graph<V extends Vertex<E>, E extends Edge<V>> {

    Set<? extends V> getVertices();

    Set<? extends E> getEdges();
}
