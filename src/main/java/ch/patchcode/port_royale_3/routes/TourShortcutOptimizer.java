package ch.patchcode.port_royale_3.routes;

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
import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

/**
 * Traveling Salesman optimizer. Takes a spanning tree as input, starts with a
 * route that travels each edge twice, and optimizes the route by greedily
 * inserting shortcuts for nodes that are visited more than once. Greedy means
 * that, of all possible shortcuts, the one with the biggest saving is always
 * chosen first, until no further shortcuts are possible.
 */
public class TourShortcutOptimizer {

    private Map<Vertex, List<Vertex>> links;

    public TourShortcutOptimizer(Tree<Vertex> tree) {
        Map<Vertex, List<Vertex>> links = new HashMap<>();
        TreeUtils.visitAllEdges(tree, (a, b) -> {
            links.computeIfAbsent(a, $ -> new ArrayList<>()).addAll(Arrays.asList(b, b));
            links.computeIfAbsent(b, $ -> new ArrayList<>()).addAll(Arrays.asList(a, a));
        });
        this.links = links;
    }

    public List<Vertex> createTour() {
        List<Vertex> redundantVertices = computeRedundantVertices();
        while (redundantVertices.size() > 0) {
            List<ShortcutMetric> metrics = computeAllShortcutMetrics(redundantVertices);

            Collections.sort(metrics);

            while (metrics.size() > 0) {

                ShortcutMetric top = metrics.remove(0);

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

        List<Vertex> visited = new ArrayList<>();
        Optional<Vertex> node = links.entrySet().stream().findFirst().map(it -> it.getKey());
        while (node.isPresent()) {
            visited.add(node.get());
            node = links.get(node.get()).stream().filter(it -> !visited.contains(it)).findFirst();
        }
        return visited;
    }

    private List<Vertex> computeRedundantVertices() {
        return links.entrySet().stream().filter(it -> it.getValue().size() / 2 > 1).map(it -> it.getKey())
                .collect(Collectors.toList());
    }

    private List<ShortcutMetric> computeAllShortcutMetrics(List<Vertex> redundantVertices) {
        List<ShortcutMetric> metrics = new ArrayList<>();
        for (Vertex v : redundantVertices) {

            Set<Vertex> neighbours = links.get(v).stream().distinct().collect(Collectors.toSet());

            List<Edge> edges = v.getEdges().stream()
                    .filter(it -> it.getVertices().stream().anyMatch(it2 -> neighbours.contains(it2)))
                    .collect(Collectors.toList());

            for (int i = 0; i < edges.size(); ++i) {
                Edge edge1 = edges.get(i);
                Vertex v1 = edge1.getVertices().stream().filter(it -> !it.equals(v)).findFirst().get();
                for (int j = i + 1; j < edges.size(); ++j) {
                    Edge edge2 = edges.get(j);
                    Vertex v2 = edge2.getVertices().stream().filter(it -> !it.equals(v)).findFirst().get();
                    Edge edge3 = v1.getEdges().stream().filter(it -> it.getVertices().contains(v2)).findFirst().get();

                    double benefit = Math.max(0, edge1.getWeight() + edge2.getWeight() - edge3.getWeight());
                    metrics.add(new ShortcutMetric(v, Arrays.asList(v1, v2), -benefit));
                }
            }
        }
        return metrics;
    }

    private void applyShortcut(ShortcutMetric top) {
        links.get(top.center).remove(top.neighbours.get(0));
        links.get(top.center).remove(top.neighbours.get(1));
        links.get(top.neighbours.get(0)).remove(top.center);
        links.get(top.neighbours.get(1)).remove(top.center);
        links.get(top.neighbours.get(0)).add(top.neighbours.get(1));
        links.get(top.neighbours.get(1)).add(top.neighbours.get(0));
    }

    private boolean isUnsplitted() {
        Set<Vertex> colored = new HashSet<Vertex>();
        Deque<Vertex> candidates = new ArrayDeque<>();
        candidates.add(links.keySet().iterator().next());
        while (candidates.size() > 0) {
            Vertex v = candidates.pop();
            colored.add(v);
            links.get(v).stream().distinct().filter(it -> !colored.contains(it)).forEach(it -> candidates.add(it));
        }
        return colored.size() == links.size();
    }

    private void unapplyShortcut(ShortcutMetric top) {
        links.get(top.center).add(top.neighbours.get(0));
        links.get(top.center).add(top.neighbours.get(1));
        links.get(top.neighbours.get(0)).add(top.center);
        links.get(top.neighbours.get(1)).add(top.center);
        links.get(top.neighbours.get(0)).remove(top.neighbours.get(1));
        links.get(top.neighbours.get(1)).remove(top.neighbours.get(0));
    }
}