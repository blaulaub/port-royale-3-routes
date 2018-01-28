package ch.patchcode.graphs.trees;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import ch.patchcode.graphs.basic.Vertex;

public class TreeUtils {

    /**
     * This takes a {@link Tree} and outputs GraphViz content to some
     * {@link PrintWriter} that represents the tree (as directed graph). If the
     * output is written to a file {@code X.dot}, you can invoke sth. like
     * {@code dot X.dot -Tpng -o X.png} to generate a PNG image of the graph.
     *
     * @param tree
     * @param writer
     */
    public static <V extends Vertex<?>> void writeGraphvizDotFileContent(Tree<V> tree, PrintWriter writer) {

        writer.println("digraph G {");

        Deque<V> nodes = new ArrayDeque<>();
        nodes.push(tree.getRoot());

        while (nodes.size() > 0) {
            V current = nodes.pop();
            Collection<V> children = tree.getChildren(current);
            nodes.addAll(children);
            for (V child : children) {
                writer.println(String.format("  \"%s\" -> \"%s\"", current.getName(), child.getName()));
            }
        }

        writer.println("}");
    }

}
