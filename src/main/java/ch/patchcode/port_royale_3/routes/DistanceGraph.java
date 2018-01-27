package ch.patchcode.port_royale_3.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import ch.patchcode.graphs.weighted.WeightedEdge;
import ch.patchcode.graphs.weighted.WeightedGraph;
import ch.patchcode.graphs.weighted.WeightedVertex;

public class DistanceGraph implements WeightedGraph {

    private final Set<Vertex> vertices;
    private final Set<Edge> edges;

    public DistanceGraph(DistanceData data) {
        ArrayList<VertexImpl> vertices = new ArrayList<>(
                data.getPlaces().stream().map(VertexImpl::new).collect(Collectors.toList()));
        Set<EdgeImpl> edges = new HashSet<EdgeImpl>();
        for (int i = 0; i < vertices.size(); ++i) {
            VertexImpl vertex1 = vertices.get(i);
            for (int j = i + 1; j < vertices.size(); ++j) {
                VertexImpl vertex2 = vertices.get(j);
                Double distance = data.getDistance(vertex1.getName(), vertex2.getName());
                EdgeImpl edge = new EdgeImpl(vertex1, vertex2, distance);
                edges.add(edge);
                vertex1.addEdge(edge);
                vertex2.addEdge(edge);
            }
        }
        this.edges = Collections.unmodifiableSet(edges);
        this.vertices = Collections.unmodifiableSet(new HashSet<>(vertices));
    }

    @Override
    public Set<Vertex> getVertices() {
        return vertices;
    }

    @Override
    public Set<Edge> getEdges() {
        return edges;
    }

    public abstract class Vertex implements WeightedVertex {
        public abstract String getName();
    }

    public abstract class Edge implements WeightedEdge {
    }

    private class VertexImpl extends Vertex {

        private final String name;
        private final Set<EdgeImpl> edges;
        private final Set<Edge> unmodifiableEdges;
        private final int hashCode;

        public VertexImpl(String name) {
            this.name = name;
            this.edges = new HashSet<>();
            this.unmodifiableEdges = Collections.unmodifiableSet(edges);
            this.hashCode = name.hashCode();
        }

        public void addEdge(EdgeImpl edge) {
            edges.add(edge);
        }

        public String getName() {
            return name;
        }

        @Override
        public Set<Edge> getEdges() {
            return unmodifiableEdges;
        }

        @Override
        public double getWeight() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private class EdgeImpl extends Edge {

        private final Set<Vertex> vertices;
        private final Double distance;
        private final int hashCode;

        public EdgeImpl(VertexImpl vertex1, VertexImpl vertex2, Double distance) {
            Set<VertexImpl> vertices = new HashSet<>();
            vertices.add(vertex1);
            vertices.add(vertex2);
            this.vertices = Collections.unmodifiableSet(vertices);
            this.distance = distance;
            this.hashCode = vertex1.hashCode() ^ vertex2.hashCode() ^ distance.hashCode();
        }

        @Override
        public Set<Vertex> getVertices() {
            return vertices;
        }

        @Override
        public double getWeight() {
            return distance;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}