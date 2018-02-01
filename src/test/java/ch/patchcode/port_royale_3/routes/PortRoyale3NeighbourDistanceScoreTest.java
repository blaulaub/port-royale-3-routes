package ch.patchcode.port_royale_3.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.graphs.weighted.NeighbourDistanceScore;

public class PortRoyale3NeighbourDistanceScoreTest {

    private DistanceGraph graph;

    @Before
    public void setup() throws IOException {
        try (InputStream is = PortRoyaleTriangleInequalityTest.class.getClassLoader()
                .getResourceAsStream("port-royale-3-distances.csv")) {
            graph = new DistanceGraph(new DistanceCsvData(is));
        }
    }

    @Test
    public void findMostCentralTortuga() {
        NeighbourDistanceScore<?, ?> centralVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().findFirst().get();
        assertThat(centralVertex.getVertex().getName(), equalTo("Tortuga"));
        assertThat(centralVertex.getMeanDistance(), closeTo(2.49, 0.01));
    }

    @Test
    public void findMostRemoteCorpusChristi() {
        NeighbourDistanceScore<?, ?> remoteVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().reduce((first, second) -> second).get();
        assertThat(remoteVertex.getVertex().getName(), equalTo("Corpus Christi"));
        assertThat(remoteVertex.getMeanDistance(), closeTo(4.75, 0.01));
    }
}
