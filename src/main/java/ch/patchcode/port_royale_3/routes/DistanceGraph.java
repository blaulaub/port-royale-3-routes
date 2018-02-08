package ch.patchcode.port_royale_3.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import ch.patchcode.graphs.weighted.WeightedEdge;
import ch.patchcode.graphs.weighted.WeightedGraph;
import ch.patchcode.graphs.weighted.WeightedVertex;

public class DistanceGraph implements WeightedGraph<DistanceGraph.Vertex, DistanceGraph.Edge> {

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

    public abstract class Vertex implements WeightedVertex<Vertex, Edge>, Comparable<Vertex> {

        public abstract String getName();

        @Override
        public abstract Set<Edge> getEdges();

        @Override
        public int compareTo(Vertex o) {
            return getName().compareTo(o.getName());
        }
    }

    public abstract class Edge implements WeightedEdge<Vertex, Edge>, Comparable<Edge> {

        @Override
        public abstract Set<Vertex> getVertices();

        @Override
        public abstract int compareTo(Edge o);

        @Override
        public String toString() {
            return String.format("%s[%.1f:%s]", this.getClass().getSimpleName(), getWeight(), getVertices().stream().map(it -> it.getName()).collect(Collectors.joining("-")));
        }


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
            this.hashCode = Objects.hash(vertex1, vertex2, distance);
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
        public int compareTo(Edge o) {
            int distanceAscending = Double.compare(getWeight(), o.getWeight());
            if (distanceAscending != 0) return distanceAscending;

            List<Vertex> ownVertices = sortByNameAscending(this.getVertices());
            List<Vertex> otherVertices = sortByNameAscending(o.getVertices());

            int firstVertexNameAscending = ownVertices.get(0).getName().compareTo(otherVertices.get(0).getName());
            if (firstVertexNameAscending != 0) return firstVertexNameAscending;

            int secondVertexNameAscending = ownVertices.get(1).getName().compareTo(otherVertices.get(1).getName());
            return secondVertexNameAscending;
        }

        private List<Vertex> sortByNameAscending(Set<Vertex> v1) {
            return v1.stream().sorted((a,b) -> a.getName().compareTo(b.getName())).collect(Collectors.toList());
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