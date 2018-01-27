package ch.patchcode.port_royale_3.routes;

import java.util.Set;

import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

public class NeighbourDistanceScore implements Comparable<NeighbourDistanceScore> {

    private final Vertex vertex;
    private final int count;
    private final double meanDistance;

    public NeighbourDistanceScore(Vertex vertex) {
        Set<Edge> edges = vertex.getEdges();
        int count = edges.size();
        double meanDistance = edges.stream().mapToDouble(it -> it.getWeight()).sum() / count;
        this.vertex = vertex;
        this.count = count;
        this.meanDistance = meanDistance;

    }

    public Vertex getVertex() {
        return vertex;
    }


    public int getCount() {
        return count;
    }


    public double getMeanDistance() {
        return meanDistance;
    }

    @Override
    public int compareTo(NeighbourDistanceScore o) {
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