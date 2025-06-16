package com.warehouse.util;

import com.warehouse.graph.WarehouseGraph;
import com.warehouse.graph.WarehouseNode;

public final class GraphExport {

    /** Convierte el grafo a texto DOT (Graphviz). */
    public static String toDot(WarehouseGraph g) {
        StringBuilder sb = new StringBuilder("digraph G {\n")
                .append("  rankdir=LR;\n"); // izquierda→derecha
        for (WarehouseNode n : g.getNodes()) {
            sb.append("  ")
                    .append(n.getId())
                    .append(" [label=\"")
                    .append(n.getLabel())
                    .append("\"];\n");
        }
        for (WarehouseNode n : g.getNodes()) {
            n.getEdges().forEach((dst, w) ->
                    sb.append("  ")
                            .append(n.getId())
                            .append(" -> ")
                            .append(dst.getId())
                            .append(" [label=\"")
                            .append(w)
                            .append("\"];\n"));
        }
        sb.append("}\n");
        return sb.toString();
    }

    private GraphExport() {} // utilidad estática
}
