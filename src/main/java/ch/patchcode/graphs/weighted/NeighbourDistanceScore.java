package ch.patchcode.graphs.weighted;

import java.util.Set;

public class NeighbourDistanceScore<V extends WeightedVertex, E extends WeightedEdge> implements Comparable<NeighbourDistanceScore<V, E>> {

    private final V vertex;
    private final int count;
    private final double meanDistance;

    public NeighbourDistanceScore(V vertex) {
        Set<? extends WeightedEdge> edges = vertex.getEdges();
        int count = edges.size();
        double meanDistance = edges.stream().mapToDouble(it -> it.getWeight()).sum() / count;
        this.vertex = vertex;
        this.count = count;
        this.meanDistance = meanDistance;

    }

    public V getVertex() {
        return vertex;
    }


    public int getCount() {
        return count;
    }


    public double getMeanDistance() {
        return meanDistance;
    }

    @Override
    public int compareTo(NeighbourDistanceScore<V, E> o) {
        int countDescending = Integer.compare(o.count, count);
        if (countDescending != 0) {
            return countDescending;
        } else {
            int meanDistanceAscending = Double.compare(meanDistance, o.meanDistance);
            return meanDistanceAscending;
        }
    }

    @Override
    public String toString() {
        return String.format("%s[name: %s, count: %d, meanDistance: %.2f]", getClass().getSimpleName(), vertex.getName(), count, meanDistance);  
    }
}