package ch.patchcode.port_royale_3.routes;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class GreedyMinimumDistanceSpanningTreeTest {

    private DistanceGraph graph;

    @Before
    public void setup() throws IOException {
        try (InputStream is = PortRoyaleTriangleInequalityTest.class.getClassLoader()
                .getResourceAsStream("port-royale-3-distances.csv")) {
            graph = new DistanceGraph(new DistanceCsvData(is));
        }
    }

    @Test
    public void test() {
        GreedyMinimumDistanceSpanningTree tree = new GreedyMinimumDistanceSpanningTree(graph);
    }
}
