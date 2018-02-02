package ch.patchcode.graphs.weighted;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class NeighbourDistanceScoreTest {

    private TestGraph graph;

    @Before
    public void setup() throws IOException {
        TestGraph g = new TestGraph();
        TestGraph.Vertex a = g.createVertex("A");
        TestGraph.Vertex b = g.createVertex("B");
        TestGraph.Vertex c = g.createVertex("C");
        g.createEdge(a, b, 1.);
        g.createEdge(b, c, 2);
        g.createEdge(a, c, 3);
        this.graph = g;
    }

    @Test
    public void findMostCentral() {
        NeighbourDistanceScore<?, ?> centralVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().findFirst().get();
        assertThat(centralVertex.getVertex().getName(), equalTo("B"));
        assertThat(centralVertex.getMeanDistance(), closeTo(1.5, 0.01));
    }

    @Test
    public void findMostRemote() {
        NeighbourDistanceScore<?, ?> remoteVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().reduce((first, second) -> second).get();
        assertThat(remoteVertex.getVertex().getName(), equalTo("C"));
        assertThat(remoteVertex.getMeanDistance(), closeTo(2.5, 0.01));
    }
}
