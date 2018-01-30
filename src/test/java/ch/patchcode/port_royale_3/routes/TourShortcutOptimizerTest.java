package ch.patchcode.port_royale_3.routes;

import java.io.IOException;
import java.io.InputStream;

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

        links.createTour().stream().forEach(it -> System.out.println("visit " + it.getName()));
    }
}
