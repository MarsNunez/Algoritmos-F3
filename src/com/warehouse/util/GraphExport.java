package com.warehouse.util;

import com.warehouse.graph.WarehouseGraph;
import com.warehouse.graph.WarehouseNode;

public class GraphExport {
    public static String toDot(WarehouseGraph g) {
        StringBuilder sb = new StringBuilder("digraph G {\n")
                .append("  rankdir=LR;\n")
                .append("  node [shape=record, style=filled, fillcolor=lightblue];\n\n");

        // Nodos con formato mejorado
        for (WarehouseNode node : g.getNodes()) {
            sb.append("  ")
                    .append(node.getId())
                    .append(" [label=\"{")
                    .append(node.getLabel())
                    .append("|")
                    .append(node.getFormattedProducts().replace("\n", "|")) // Formato para record
                    .append("}\", fontsize=10];\n\n");
        }

        // Conexiones
        for (WarehouseNode node : g.getNodes()) {
            node.getEdges().forEach((destino, peso) -> {
                sb.append("  ")
                        .append(node.getId())
                        .append(" -> ")
                        .append(destino.getId())
                        .append(" [label=\"")
                        .append(peso)
                        .append("\", fontsize=8];\n");
            });
        }

        sb.append("}\n");
        return sb.toString();
    }
}
