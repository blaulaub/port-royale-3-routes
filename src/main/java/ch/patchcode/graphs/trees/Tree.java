package ch.patchcode.graphs.trees;

import java.util.Collection;

import ch.patchcode.graphs.basic.Vertex;

// TODO I think Tree should extend Graph
public interface Tree<V extends Vertex<?>> {

    V getRoot();

    Collection<V> getChildren(V vertex);
}
