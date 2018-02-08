package ch.patchcode.graphs.weighted;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.patchcode.graphs.trees.Tree;

public class BottomUpBipairingSpanningTree<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> implements Tree<V> {

    private final V root;
    private final Map<V, List<V>> tree;

    public BottomUpBipairingSpanningTree(WeightedGraph<V, E> graph) {
        Set<V> vertices = graph.getVertices();
        List<BottomUpBipairingSpanningTree.Group<V, E>> groups = vertices.stream().map(Collections::singleton).map(BottomUpBipairingSpanningTree.Group::new).collect(Collectors.toList());

        List<E> edges = new ArrayList<>();
        while (groups.size() > 1) {
            groups = combineGroups(groups, edges::add);
        }

        Map<V, List<V>> neighbours = new HashMap<>();
        edges.stream().forEach(e -> {
            List<V> vs = new ArrayList<>(e.getVertices());
            neighbours.computeIfAbsent(vs.get(0), $ -> new ArrayList<>()).add(vs.get(1));
            neighbours.computeIfAbsent(vs.get(1), $ -> new ArrayList<>()).add(vs.get(0));
        });

        root = graph.getCentralVertex();
        tree = new HashMap<>();

        Deque<V> nodes = new ArrayDeque<>();
        nodes.push(root);
        while (!nodes.isEmpty()) {
            V node = nodes.pop();
            neighbours.remove(node).stream().filter(it -> !tree.keySet().contains(it)).forEach(n -> {
                tree.computeIfAbsent(node, $ -> new ArrayList<>()).add(n);
                nodes.add(n);
            });
        }
    }

    @Override
    public V getRoot() {
        return root;
    }

    @Override
    public Collection<V> getChildren(V vertex) {
        return Collections.unmodifiableCollection(tree.getOrDefault(vertex, Collections.emptyList()));
    }

    private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> List<BottomUpBipairingSpanningTree.Group<V, E>> combineGroups(List<BottomUpBipairingSpanningTree.Group<V, E>> groups, Consumer<E> newEdgeConsumer) {
        List<BottomUpBipairingSpanningTree.Group<V, E>> groups1 = new ArrayList<>(groups);

        List<BottomUpBipairingSpanningTree.GroupConnection<V, E>> nextConnections1 = new ArrayList<>();
        while (groups1.size() > 1) {
            // lookup shortest intergroup-connection for all remaining groups
            Collection<BottomUpBipairingSpanningTree.GroupConnection<V, E>> values = allShortestIntergroupConnections(groups1).values();
            // pick the one group that has the longest way to other groups
            BottomUpBipairingSpanningTree.GroupConnection<V, E> cand = values.stream().reduce((g1, g2) -> g1.edge.getWeight() > g2.edge.getWeight() ? g1 : g2).get();
            newEdgeConsumer.accept(cand.edge);
            // remove connected groups from remaining groups
            groups1.removeIf(it1 -> it1.vertices.stream().anyMatch(v -> cand.edge.getVertices().contains(v)));
            // keep connection
            nextConnections1.add(cand);
        }

        List<BottomUpBipairingSpanningTree.Group<V, E>> nextGroups = new ArrayList<>();
        nextConnections1.stream().map(x -> toGroup(x)).forEach(nextGroups::add);
        nextGroups.addAll(groups1);
        return nextGroups;
    }

    private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> BottomUpBipairingSpanningTree.Group<V, E> toGroup(BottomUpBipairingSpanningTree.GroupConnection<V, E> connection) {
        return new BottomUpBipairingSpanningTree.Group<>(connection.groups.stream().flatMap(it -> it.vertices.stream()).collect(Collectors.toSet()));
    }

    private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> Map<BottomUpBipairingSpanningTree.Group<V, E>, BottomUpBipairingSpanningTree.GroupConnection<V, E>> allShortestIntergroupConnections(List<BottomUpBipairingSpanningTree.Group<V, E>> groups) {
        return groups.stream().collect(Collectors.toMap(Function.identity(), pivot -> groups.stream().filter(g -> !pivot.equals(g)).map(g -> new BottomUpBipairingSpanningTree.GroupConnection<>(pivot, g, shortestEdgeBetweenTwoGroups(pivot, g))).reduce((c1, c2) -> c1.edge.getWeight() < c2.edge.getWeight() ? c1 : c2).get()));
    }

    private static class GroupConnection<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> {
        public final Set<BottomUpBipairingSpanningTree.Group<V, E>> groups;
        public final E edge;
        public GroupConnection(BottomUpBipairingSpanningTree.Group<V, E> g1, BottomUpBipairingSpanningTree.Group<V, E> g2, E edge) {
            sanityCheck(g1, g2, edge);
            this.groups = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(g1, g2)));
            this.edge = edge;
        }
        private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> void sanityCheck(BottomUpBipairingSpanningTree.Group<V, E> g1, BottomUpBipairingSpanningTree.Group<V, E> g2, E edge) {
            int score = 0;
            for (V v : edge.getVertices()) {
                score = (score << 1) + (g1.vertices.contains(v) ? 1 : 0);
                score = (score << 1) + (g2.vertices.contains(v) ? 1 : 0);
            }
            if (score != 0b1001 && score != 0b0110) {
                throw new RuntimeException(String.format("Trying to connect %s and %s", g1, g2));
            }
        }
    }

    private static <V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> E shortestEdgeBetweenTwoGroups(BottomUpBipairingSpanningTree.Group<V, E> pivot, BottomUpBipairingSpanningTree.Group<V, E> o) {
        return pivot.vertices.stream().flatMap(v -> v.getEdges().stream()).filter(it -> it.getVertices().stream().anyMatch(o.vertices::contains)).reduce((a, b) -> a.getWeight() < b.getWeight() ? a : b).get();
    }

    private static class Group<V extends WeightedVertex<V, E>, E extends WeightedEdge<V, E>> {

        public final Set<V> vertices;

        public Group(Set<V> vertices) {
            this.vertices = Collections.unmodifiableSet(new HashSet<>(vertices));
        }

        @Override
        public String toString() {
            return String.format("%s<%s>[%d:%s]", getClass().getSimpleName(), vertices.stream().findFirst().map(it -> it.getClass().getSimpleName()).orElse("?"), vertices.size(), vertices.stream().map(it->it.getName()).collect(Collectors.joining(",")));
        }
    }
}