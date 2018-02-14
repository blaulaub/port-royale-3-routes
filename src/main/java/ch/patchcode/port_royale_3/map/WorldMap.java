package ch.patchcode.port_royale_3.map;

import static java.util.function.Function.identity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ch.patchcode.port_royale_3.routes.DistanceGraph.Edge;
import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

public class WorldMap {

    private final double scale;

    private Map<Vertex, PosImpl> positions = new HashMap<>();
    private Set<Vertex> fixed = new HashSet<>();
    private Set<Vertex> free = new HashSet<>();

    public WorldMap(double scale) {
        this.scale = scale;
    }

    public Collection<Vertex> vertices() {
        return Collections.unmodifiableCollection(positions.keySet());
    }

    public boolean contains(Vertex node) {
        return positions.keySet().contains(node);
    }

    public void addFixed(Vertex node, int x, int y) {
        if (contains(node)) {
            throw new DuplicateEntryException(node);
        }
        fixed.add(node);
        positions.put(node, new PosImpl(x, y));
    }

    public void add(Vertex node) {
        if (contains(node)) {
            throw new DuplicateEntryException(node);
        }
        free.add(node);
        positions.put(node, randomPos(1.));
    }

    public void rebalanceAll(double residualLimit) {
        double residual;
        do {
            Map<Vertex, PosImpl> residuals = rebalanceAllOnce();
            residual = residuals.values().stream().mapToDouble(PosImpl::absSquare).sum();
        } while (residual > residualLimit);
    }

    private Map<Vertex, PosImpl> rebalanceAllOnce() {
        double weight = free.size();
        // compute residuals
        Map<Vertex, PosImpl> residuals = computeResiduals(free);
        // apply residual correction
        for (Entry<Vertex, PosImpl> res : residuals.entrySet()) {
            positions.get(res.getKey()).shiftBy(res.getValue().times(1/weight));
        }
        return residuals;
    }

    private Map<Vertex, PosImpl> computeResiduals(Set<Vertex> subset) {
        return subset.stream().collect(Collectors.toMap(identity(), this::computeResidual));
    }

    private PosImpl computeResidual(Vertex node) {
        PosImpl residual = new PosImpl();
        for (Edge edge : node.getEdges()) {
            edge.getVertices().stream()
                .filter(it -> !node.equals(it))
                .filter(it -> positions.keySet().contains(it)).findFirst()
                .map(it -> positions.get(it))
                .ifPresent(it -> residual.shiftBy(it.minus(positions.get(node), edge.getWeight())));
        }
        return residual;
    }

    public Pos getPosition(Vertex node) {
        return positions.get(node);
    }

    public interface Pos {
        double getX();
        double getY();
    }

    public PosImpl randomPos(double multiplier) {
        return new PosImpl(Math.random() * multiplier, Math.random() * multiplier);
    }

    private class PosImpl implements Pos {

        double x;
        double y;

        public PosImpl() {}

        public PosImpl(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double absSquare() {
            return x*x+y*y;
        }

        public void shiftBy(PosImpl delta) {
            x += delta.x;
            y += delta.y;
        }

        public PosImpl minus(PosImpl refNode, double weight) {
            PosImpl d = new PosImpl(x - refNode.x, y - refNode.y).times(1/scale);
            return d.times((1. - weight/Math.sqrt(d.absSquare()))*scale);
        }

        public PosImpl times(double multiplier) {
            return new PosImpl(x*multiplier, y*multiplier);
        }
    }

    private static class DuplicateEntryException extends RuntimeException {
        private static final long serialVersionUID = -176430125631335959L;
        public DuplicateEntryException(Vertex node) {
            super(node.toString());
        }
    }
}
