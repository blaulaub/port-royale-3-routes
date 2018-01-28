package ch.patchcode.graphs.basic;

import java.util.Set;

public interface Vertex<E extends Edge<?>> {

    String getName();

    Set<? extends E> getEdges();
}
