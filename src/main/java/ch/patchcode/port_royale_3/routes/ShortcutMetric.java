package ch.patchcode.port_royale_3.routes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

public class ShortcutMetric implements Comparable<ShortcutMetric> {

    public final Vertex center;
    public final List<Vertex> neighbours;
    public final double cost;

    public ShortcutMetric(Vertex center, List<Vertex> neighbours, double cost) {
        this.center = center;
        this.neighbours = Collections.unmodifiableList(neighbours.stream().sorted().collect(Collectors.toList()));
        this.cost = cost;
    }

    @Override
    public int compareTo(ShortcutMetric o) {
        int byBenefitDescending = Double.compare(cost, o.cost);
        if (byBenefitDescending != 0) return byBenefitDescending;

        int byCenter = center.compareTo(o.center);
        if (byCenter != 0) return byCenter;

        int byFirstNeighbour = neighbours.get(0).compareTo(o.neighbours.get(0));
        if (byFirstNeighbour != 0) return byFirstNeighbour;

        return neighbours.get(1).compareTo(o.neighbours.get(1));
    }

    @Override
    public String toString() {
        return String.format("ShortCutMetric[%s-(%s)-%s saves %.1f]", neighbours.get(0).getName(), center.getName(), neighbours.get(1).getName(), cost);
    }
}