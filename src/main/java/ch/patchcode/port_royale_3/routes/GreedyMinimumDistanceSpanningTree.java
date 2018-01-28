package ch.patchcode.port_royale_3.routes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

/**
 * A spanning tree constructed from some {@link DistanceGraph} by first
 * selecting the node with the most connections and the minimum mean distance
 * thereof, and then successively adding the next shortest edge to some yet
 * unconnected node, until all nodes are connected by the tree.
 */
// TODO may need some superclass (Tree? Spanning Tree?)
public class GreedyMinimumDistanceSpanningTree {

    private Vertex root;
    private Map<Vertex, List<Vertex>> tree;

    public GreedyMinimumDistanceSpanningTree(DistanceGraph graph) {
        Vertex centralVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().findFirst().get()
                .getVertex();

        SpanningTreeBuilder builder = new SpanningTreeBuilder(centralVertex);

        while (builder.hasOpenEdges()) {
            Edge shortestUnconnectedEdge = builder.shortestOpenEdge();
            builder.connectEdge(shortestUnconnectedEdge);
        }

        this.root = builder.getRoot();
        this.tree = builder.getTree();
    }

    public Vertex getRoot() {
        return root;
    }

    public Collection<Vertex> getChildren(Vertex vertex) {
        return Collections.unmodifiableCollection(tree.getOrDefault(vertex, Collections.emptyList()));
    }
}