package ch.patchcode.port_royale_3.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

@RunWith(Parameterized.class)
public class PortRoyaleTriangleInequalityTest {

    // distances are rounded to one digit, plus some small rounding error
    private static final double EPS = 0.1 + 0.0000001;

    @Parameters
    public static Collection<Vertex[]> data() throws IOException {
        DistanceGraph graph;
        try (InputStream is = PortRoyaleTriangleInequalityTest.class.getClassLoader()
                .getResourceAsStream("port-royale-3-distances.csv")) {
            graph = new DistanceGraph(new DistanceCsvData(is));
        }

        ArrayList<Vertex> vertices = new ArrayList<>(graph.getVertices());
        ArrayList<Vertex[]> result = new ArrayList<>(vertices.size() * (vertices.size() - 1) * (vertices.size() - 2) >> 1);
        for (int i = 0; i < vertices.size(); ++i) {
            Vertex vertex1 = vertices.get(i);
            for (int j = i + 1; j < vertices.size(); ++j) {
                Vertex vertex2 = vertices.get(j);
                for (int k = j + 1; k < vertices.size(); ++k) {
                    Vertex vertex3 = vertices.get(k);
                    result.add(new Vertex[] { vertex1, vertex2, vertex3 });
                }
            }
        }
        return result;
    }

    private final Vertex vertex1;
    private final Vertex vertex2;
    private final Vertex vertex3;
    private final Edge edge12;
    private final Edge edge13;
    private final Edge edge23;

    public PortRoyaleTriangleInequalityTest(Vertex vertex1, Vertex vertex2, Vertex vertex3) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
        this.edge12 = vertex1.getEdges().stream().filter(it -> it.getVertices().contains(vertex2)).findFirst().get();
        this.edge13 = vertex1.getEdges().stream().filter(it -> it.getVertices().contains(vertex3)).findFirst().get();
        this.edge23 = vertex2.getEdges().stream().filter(it -> it.getVertices().contains(vertex3)).findFirst().get();
    }

    @Test
    public void checkTripplet13() {
        assertThat(String.format("%s-%s-%s", vertex1.getName(), vertex2.getName(), vertex3.getName()),
                edge13.getWeight(), lessThanOrEqualTo(edge12.getWeight() + edge23.getWeight() + EPS));
    }

    @Test
    public void checkTripplet12() {
        assertThat(String.format("%s-%s-%s", vertex1.getName(), vertex3.getName(), vertex2.getName()),
                edge12.getWeight(), lessThanOrEqualTo(edge13.getWeight() + edge23.getWeight() + EPS));
    }

    @Test
    public void checkTripplet23() {
        assertThat(String.format("%s-%s-%s", vertex2.getName(), vertex1.getName(), vertex3.getName()),
                edge23.getWeight(), lessThanOrEqualTo(edge12.getWeight() + edge13.getWeight() + EPS));
    }

}
