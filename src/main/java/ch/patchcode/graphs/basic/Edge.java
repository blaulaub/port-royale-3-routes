package ch.patchcode.graphs.basic;

import java.util.Set;

public interface Edge<V extends Vertex<?>> {

    Set<? extends V> getVertices();
}
