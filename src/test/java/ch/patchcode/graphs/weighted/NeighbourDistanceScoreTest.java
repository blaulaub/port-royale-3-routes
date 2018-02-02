package ch.patchcode.graphs.weighted;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class NeighbourDistanceScoreTest {

    private TestGraph graph;

    @Before
    public void setup() throws IOException {
        TestGraph g = new TestGraph();
        TestGraph.TestVertex a = g.createVertex("A");
        TestGraph.TestVertex b = g.createVertex("B");
        TestGraph.TestVertex c = g.createVertex("C");
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
    public void findMostRemoteCorpusChristi() {
        NeighbourDistanceScore<?, ?> remoteVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().reduce((first, second) -> second).get();
        assertThat(remoteVertex.getVertex().getName(), equalTo("C"));
        assertThat(remoteVertex.getMeanDistance(), closeTo(2.5, 0.01));
    }

    private class TestGraph implements WeightedGraph<TestGraph.TestVertex, TestGraph.TestEdge> {

        public Set<TestVertex> vertices = new HashSet<>();
        public Set<TestEdge> edges = new HashSet<>();

        public TestVertex createVertex(String name) {
            TestVertex v = new TestVertex(name);
            vertices.add(v);
            return v;
        }

        public TestEdge createEdge(TestVertex a, TestVertex b, double weight) {
            TestEdge edge = new TestEdge(weight);
            a.edges.add(edge);
            b.edges.add(edge);
            edges.add(edge);
            return edge;
        }

        @Override
        public Set<TestVertex> getVertices() {
            return vertices;
        }

        @Override
        public Set<TestEdge> getEdges() {
            return edges;
        }

        public class TestVertex implements WeightedVertex<TestVertex, TestEdge> {
        
            public final String name;
        
            public Set<TestEdge> edges;
        
            public TestVertex(String name) {
                this.name = name;
                this.edges = new HashSet<>();
            }
        
            @Override
            public String getName() {
                return name;
            }
        
            @Override
            public Set<TestEdge> getEdges() {
                return edges;
            }
        
            @Override
            public double getWeight() {
                return 0;
            }
        }

        public class TestEdge implements WeightedEdge<TestVertex, TestEdge> {
        
            public final double weight;
            public Set<TestVertex> vertices;
        
            public TestEdge(double weight) {
                super();
                this.weight = weight;
            }
        
            @Override
            public Set<TestVertex> getVertices() {
                return vertices;
            }
        
            @Override
            public double getWeight() {
                return weight;
            }
        }
    }
}
