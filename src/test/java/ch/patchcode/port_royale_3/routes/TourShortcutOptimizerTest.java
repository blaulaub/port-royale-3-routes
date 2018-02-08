package ch.patchcode.port_royale_3.routes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.graphs.trees.Tree;
import ch.patchcode.graphs.weighted.TopDownBisectionSpanningTree;
import ch.patchcode.graphs.weighted.BottomUpBipairingSpanningTree;
import ch.patchcode.graphs.weighted.GreedyMinimumDistanceSpanningTree;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

public class TourShortcutOptimizerTest {

    private DistanceGraph graph;

    @Before
    public void setup() throws IOException {
        try (InputStream is = PortRoyaleTriangleInequalityTest.class.getClassLoader()
                .getResourceAsStream("port-royale-3-distances.csv")) {
            graph = new DistanceGraph(new DistanceCsvData(is));
        }
    }

    @Test
    public void optimizeGreedyMinimumDistanceSpanningTree() throws IOException {
        Tree<Vertex> tree = new GreedyMinimumDistanceSpanningTree<>(graph);
        TourShortcutOptimizer links = new TourShortcutOptimizer(tree);

        List<Vertex> tour = links.createTour();

        report(tree, tour);
    }

    @Test
    public void optimizeTopDownBisectionSpanningTree() throws IOException {
        Tree<Vertex> tree = new TopDownBisectionSpanningTree<>(graph);
        TourShortcutOptimizer links = new TourShortcutOptimizer(tree);

        List<Vertex> tour = links.createTour();

        report(tree, tour);
    }

    @Test
    public void optimizeBottomUpBipairingSpanningTree() throws IOException {
        Tree<Vertex> tree = new BottomUpBipairingSpanningTree<>(graph);
        TourShortcutOptimizer links = new TourShortcutOptimizer(tree);

        List<Vertex> tour = links.createTour();

        report(tree, tour);
    }

    private void report(Tree<Vertex> tree, List<Vertex> tour) {
        System.out.println(String.format("total duration %.1f days from %-33s visiting %s", getDistance(tour), tree.getClass().getSimpleName(), tour.stream().map(it -> it.getName()).collect(Collectors.joining(", "))));
    }

    private double getDistance(List<Vertex> tour) {
        Iterator<Vertex> iter = tour.iterator();
        double sum = 0;
        Vertex first = iter.next();
        Vertex current = first;
        Vertex previous;
        while (iter.hasNext()) {
            previous = current;
            current = iter.next();
            sum += getDistance(current, previous);
        }
        sum += getDistance(current, first);
        return sum;
    }

    private Double getDistance(Vertex a, Vertex b) {
        return a.getEdges().stream().filter(it -> it.getVertices().contains(b)).filter(it -> it.getVertices().contains(a)).findFirst().map(it -> it.getWeight()).get();
    }
}
