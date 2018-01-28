package ch.patchcode.port_royale_3.routes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.junit.Before;
import org.junit.Test;

import ch.patchcode.port_royale_3.routes.DistanceGraph.Vertex;

public class GreedyMinimumDistanceSpanningTreeTest {

    private DistanceGraph graph;

    @Before
    public void setup() throws IOException {
        try (InputStream is = PortRoyaleTriangleInequalityTest.class.getClassLoader()
                .getResourceAsStream("port-royale-3-distances.csv")) {
            graph = new DistanceGraph(new DistanceCsvData(is));
        }
    }

    @Test
    public void test() throws IOException {
        GreedyMinimumDistanceSpanningTree tree = new GreedyMinimumDistanceSpanningTree(graph);

        File file = new File("GreedyMinimumDistanceSpanningTree.dot");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
            writer.println("digraph G {");

            Deque<Vertex> nodes = new ArrayDeque<>();
            nodes.push(tree.getRoot());

            while (nodes.size() > 0) {
                Vertex current = nodes.pop();
                Collection<Vertex> children = tree.getChildren(current);
                nodes.addAll(children);
                for(Vertex child : children) {
                    writer.println(String.format("  \"%s\" -> \"%s\"", current.getName(), child.getName()));
                }
            }

            writer.println("}");
        }

    }
}
