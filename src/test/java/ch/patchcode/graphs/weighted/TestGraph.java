package ch.patchcode.graphs.weighted;

import java.util.HashSet;
import java.util.Set;

public class TestGraph implements WeightedGraph<TestGraph.Vertex, TestGraph.Edge> {

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