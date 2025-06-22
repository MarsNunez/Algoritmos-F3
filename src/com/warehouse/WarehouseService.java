package com.warehouse;

import com.warehouse.graph.WarehouseGraph;
import com.warehouse.graph.WarehouseNode;
import com.warehouse.model.Product;

import java.util.Map;

/** Fachada: coordina grafo + productos locales. */
public class WarehouseService {

    private WarehouseGraph graph = new WarehouseGraph();

    /* ---------- UBICACIONES ---------- */
    public void addLocation(int id, String label) { graph.addNode(id, label); }
    public void connect(int from, int to, double w){ graph.addEdge(from, to, w); }
    public void removeConnection(int from, int to){ graph.removeEdge(from, to); }
    public void updateConnection(int from, int to, double w){ graph.updateEdge(from, to, w); }

    /* ---------- PRODUCTOS EN UBICACIONES ---------- */
    public void addProduct(int nodeId, Product p) {
        graph.getNode(nodeId).putProduct(p);
    }
    public Product findProductInNode(int nodeId, String sku) {
        WarehouseNode n = graph.getNode(nodeId);
        return n == null ? null : n.getProduct(sku);
    }

    public boolean addStock(int nodeId, String sku, int qty) {
        WarehouseNode n = graph.getNode(nodeId);
        return n != null && n.addStock(sku, qty);
    }
    public boolean removeStock(int nodeId, String sku, int qty) {
        WarehouseNode n = graph.getNode(nodeId);
        return n != null && n.removeStock(sku, qty);
    }

    public Product bfsFind(String sku, int startId) {
        for (WarehouseNode n : graph.breadthFirstSearch(startId)) {
            Product p = n.getProduct(sku);
            if (p != null) return p;
        }
        return null;
    }
    public Product dfsFind(String sku, int startId) {
        for (WarehouseNode n : graph.depthFirstSearch(startId)) {
            Product p = n.getProduct(sku);
            if (p != null) return p;
        }
        return null;
    }

    /** Permite acceder al grafo para exportarlo o recorrerlo. */
    public WarehouseGraph getGraph() {
        return graph;
    }
}
