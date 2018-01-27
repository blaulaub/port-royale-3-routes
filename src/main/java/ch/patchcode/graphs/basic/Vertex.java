package ch.patchcode.graphs.basic;

import java.util.Set;

public interface Vertex<E extends Edge<?>> {

    Set<? extends E> getEdges();
}
