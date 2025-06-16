package com.warehouse;

import com.warehouse.graph.WarehouseGraph;
import com.warehouse.graph.WarehouseNode;
import com.warehouse.model.Product;

import java.util.List;
import java.util.Map;

/** Fachada: coordina grafo + productos locales. */
public class WarehouseService {

    private final WarehouseGraph graph = new WarehouseGraph();

    /* ---------- UBICACIONES ---------- */
    public void addLocation(int id, String label) { graph.addNode(id, label); }
    public void connect(int from, int to, double w){ graph.addEdge(from, to, w); }
    public void removeConnection(int from, int to){ graph.removeEdge(from, to); }
    public void updateConnection(int f, int t, double w){ graph.updateEdge(f, t, w); }

    /* ---------- PRODUCTOS EN UBICACIONES ---------- */
    public void addProduct(int nodeId, Product p) {
        graph.getNode(nodeId).putProduct(p);
    }
    public Product findProductInNode(int nodeId, String sku) {
        WarehouseNode n = graph.getNode(nodeId);
        return n == null ? null : n.getProduct(sku);
    }
    public Product bfsFind(String sku, int startId) {
        for (WarehouseNode n : graph.bfs(startId)) {
            Product p = n.getProduct(sku);
            if (p != null) return p;
        }
        return null;
    }
    public Product dfsFind(String sku, int startId) {
        for (WarehouseNode n : graph.dfs(startId)) {
            Product p = n.getProduct(sku);
            if (p != null) return p;
        }
        return null;
    }

    /* ---------- RUTAS ---------- */
    public Map<Integer, Double> shortestPathsFrom(int startId) {
        return graph.dijkstra(startId);
    }

    /* ---------- OPTIMIZACIÓN SENCILLA ---------- */
    /** Devuelve el id del nodo con menos productos: "zona óptima" simplificada. */
    public int suggestOptimalZone() {
        return graph.getNodes().stream()
                .min((a, b) -> Integer.compare(countProducts(a), countProducts(b)))
                .map(WarehouseNode::getId)
                .orElse(-1);
    }
    private int countProducts(WarehouseNode n) {
        // Suponiendo que tengas un método size() en tu B+; o implementa tu propio contador
        Product dummy = n.getProduct("∅"); // placeholder; aquí deberías exponer tal tamaño
        return 0; // reemplaza con cuenta real
    }

    /** Permite acceder al grafo para exportarlo o recorrerlo. */
    public WarehouseGraph getGraph() {
        return graph;
    }
}
