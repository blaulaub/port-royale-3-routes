package ch.patchcode.port_royale_3.routes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.graphs.trees.Tree;
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
    public void outputTree() throws IOException {
        Tree<Vertex> tree = new GreedyMinimumDistanceSpanningTree(graph);
        TourShortcutOptimizer links = new TourShortcutOptimizer(tree);

        List<Vertex> tour = links.createTour();

        tour.stream().forEach(it -> System.out.println("visit " + it.getName()));
        System.out.println(String.format("total duration: %.1f", getDistance(tour)));
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
