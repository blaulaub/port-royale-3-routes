package ch.patchcode.graphs.weighted;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ch.patchcode.graphs.trees.Tree;

/**
 * A spanning tree constructed from some {@link WeightedGraph} by first
 * selecting the node with the most connections and the minimum mean distance
 * thereof, and then successively adding the next shortest edge to some yet
 * unconnected node, until all nodes are connected by the tree.
 */
public class GreedyMinimumDistanceSpanningTree<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> implements Tree<V> {

    private V root;
    private Map<V, List<V>> tree;

    public GreedyMinimumDistanceSpanningTree(WeightedGraph<V, E> graph) {
        V centralVertex = graph.getCentralVertex();

        SpanningTreeBuilder<V, E> builder = new SpanningTreeBuilder<>(centralVertex);

        while (builder.hasOpenEdges()) {
            E shortestUnconnectedEdge = builder.shortestOpenEdge();
            builder.connectEdge(shortestUnconnectedEdge);
        }

        this.root = builder.getRoot();
        this.tree = builder.getTree();
    }

    @Override
    public V getRoot() {
        return root;
    }

    @Override
    public Collection<V> getChildren(V vertex) {
        return Collections.unmodifiableCollection(tree.getOrDefault(vertex, Collections.emptyList()));
    }
}