package ch.patchcode.graphs.weighted;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ch.patchcode.graphs.trees.Tree;
import ch.patchcode.graphs.trees.TreeUtils;

/**
 * Traveling Salesman optimizer. Takes a spanning tree as input, starts with a
 * route that travels each edge twice, and optimizes the route by greedily
 * inserting shortcuts for nodes that are visited more than once. Greedy means
 * that, of all possible shortcuts, the one with the biggest saving is always
 * chosen first, until no further shortcuts are possible.
 */
public class TourShortcutOptimizer<V extends WeightedVertex<V, E> & Comparable<V>, E extends WeightedEdge<V, E>> {

    private Map<V, List<V>> links;

    public TourShortcutOptimizer(Tree<V> tree) {
        Map<V, List<V>> links = new HashMap<>();
        TreeUtils.visitAllEdges(tree, (a, b) -> {
            links.computeIfAbsent(a, $ -> new ArrayList<>()).addAll(Arrays.asList(b, b));
            links.computeIfAbsent(b, $ -> new ArrayList<>()).addAll(Arrays.asList(a, a));
        });
        this.links = links;
    }

    public List<V> createTour() {
        List<V> redundantVertices = computeRedundantVertices();
        while (redundantVertices.size() > 0) {
            List<ShortcutMetric<V, E>> metrics = computeAllShortcutMetrics(redundantVertices);

            Collections.sort(metrics);

            while (metrics.size() > 0) {

                ShortcutMetric<V, E> top = metrics.remove(0);

                // Problem: may (will) split the graph
                applyShortcut(top);

                // so do a coloring check
                if (isUnsplitted()) {
                    break;
                } else {
                    unapplyShortcut(top);
                }
            }

            redundantVertices = computeRedundantVertices();
        }

        List<V> visited = new ArrayList<>();
        Optional<V> node = links.entrySet().stream().findFirst().map(it -> it.getKey());
        while (node.isPresent()) {
            visited.add(node.get());
            node = links.get(node.get()).stream().filter(it -> !visited.contains(it)).findFirst();
        }
        return visited;
    }

    private List<V> computeRedundantVertices() {
        return links.entrySet().stream().filter(it -> it.getValue().size() / 2 > 1).map(it -> it.getKey())
                .collect(Collectors.toList());
    }

    private List<ShortcutMetric<V, E>> computeAllShortcutMetrics(List<V> redundantVertices) {
        List<ShortcutMetric<V, E>> metrics = new ArrayList<>();
        for (V v : redundantVertices) {

            Set<V> neighbours = links.get(v).stream().distinct().collect(Collectors.toSet());

            List<E> edges = v.getEdges().stream()
                    .filter(it -> it.getVertices().stream().anyMatch(it2 -> neighbours.contains(it2)))
                    .collect(Collectors.toList());

            for (int i = 0; i < edges.size(); ++i) {
                E edge1 = edges.get(i);
                V v1 = edge1.getVertices().stream().filter(it -> !it.equals(v)).findFirst().get();
                for (int j = i + 1; j < edges.size(); ++j) {
                    E edge2 = edges.get(j);
                    V v2 = edge2.getVertices().stream().filter(it -> !it.equals(v)).findFirst().get();
                    E edge3 = v1.getEdges().stream().filter(it -> it.getVertices().contains(v2)).findFirst().get();

                    double benefit = Math.max(0, edge1.getWeight() + edge2.getWeight() - edge3.getWeight());
                    metrics.add(new ShortcutMetric<>(v, Arrays.asList(v1, v2), -benefit));
                }
            }
        }
        return metrics;
    }

    private void applyShortcut(ShortcutMetric<V, E> top) {
        links.get(top.center).remove(top.neighbours.get(0));
        links.get(top.center).remove(top.neighbours.get(1));
        links.get(top.neighbours.get(0)).remove(top.center);
        links.get(top.neighbours.get(1)).remove(top.center);
        links.get(top.neighbours.get(0)).add(top.neighbours.get(1));
        links.get(top.neighbours.get(1)).add(top.neighbours.get(0));
    }

    private boolean isUnsplitted() {
        Set<V> colored = new HashSet<>();
        Deque<V> candidates = new ArrayDeque<>();
        candidates.add(links.keySet().iterator().next());
        while (candidates.size() > 0) {
            V v = candidates.pop();
            colored.add(v);
            links.get(v).stream().distinct().filter(it -> !colored.contains(it)).forEach(it -> candidates.add(it));
        }
        return colored.size() == links.size();
    }

    private void unapplyShortcut(ShortcutMetric<V, E> top) {
        links.get(top.center).add(top.neighbours.get(0));
        links.get(top.center).add(top.neighbours.get(1));
        links.get(top.neighbours.get(0)).add(top.center);
        links.get(top.neighbours.get(1)).add(top.center);
        links.get(top.neighbours.get(0)).remove(top.neighbours.get(1));
        links.get(top.neighbours.get(1)).remove(top.neighbours.get(0));
    }
}