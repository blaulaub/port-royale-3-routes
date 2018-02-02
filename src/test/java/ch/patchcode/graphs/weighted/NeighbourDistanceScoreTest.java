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
    public void findMostRemoteCorpusChristi() {
        NeighbourDistanceScore<?, ?> remoteVertex = graph.getVertices().stream().map(NeighbourDistanceScore::new).sorted().reduce((first, second) -> second).get();
        assertThat(remoteVertex.getVertex().getName(), equalTo("C"));
        assertThat(remoteVertex.getMeanDistance(), closeTo(2.5, 0.01));
    }

    private class TestGraph implements WeightedGraph<TestGraph.Vertex, TestGraph.Edge> {

        public Set<Vertex> vertices = new HashSet<>();
        public Set<Edge> edges = new HashSet<>();

        public Vertex createVertex(String name) {
            Vertex v = new Vertex(name);
            vertices.add(v);
            return v;
        }

        public Edge createEdge(Vertex a, Vertex b, double weight) {
            Edge edge = new Edge(weight);
            a.edges.add(edge);
            b.edges.add(edge);
            edges.add(edge);
            return edge;
        }

        @Override
        public Set<Vertex> getVertices() {
            return vertices;
        }

        @Override
        public Set<Edge> getEdges() {
            return edges;
        }

        public class Vertex implements WeightedVertex<Vertex, Edge> {
        
            public final String name;
        
            public Set<Edge> edges;
        
            public Vertex(String name) {
                this.name = name;
                this.edges = new HashSet<>();
            }
        
            @Override
            public String getName() {
                return name;
            }
        
            @Override
            public Set<Edge> getEdges() {
                return edges;
            }
        
            @Override
            public double getWeight() {
                return 0;
            }
        }

        public class Edge implements WeightedEdge<Vertex, Edge> {
        
            public final double weight;
            public Set<Vertex> vertices;
        
            public Edge(double weight) {
                super();
                this.weight = weight;
            }
        
            @Override
            public Set<Vertex> getVertices() {
                return vertices;
            }
        
            @Override
            public double getWeight() {
                return weight;
            }
        }
    }
}
