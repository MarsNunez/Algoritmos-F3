package com.warehouse.graph;

import com.warehouse.btree.BPlusTree;
import com.warehouse.model.Product;

import java.util.HashMap;
import java.util.Map;

/** Nodo = estantería o zona (carga/descarga) con índice local B+. */
public class WarehouseNode {

    private static final int INDEX_ORDER = 5;   // orden B+ típico

    private final int id;
    private final String label;
    private final Map<WarehouseNode, Double> edges = new HashMap<>();
    private final BPlusTree<String, Product> index =
            new BPlusTree<>(INDEX_ORDER);

    /* --------- constructores y básicos --------- */
    public WarehouseNode(int id, String label) {
        this.id = id;
        this.label = label;
    }

    /* --------- aristas (pasillos) --------- */
    public void addEdge(WarehouseNode target, double weight) {
        edges.put(target, weight);
    }
    public void removeEdge(WarehouseNode target) {
        edges.remove(target);
    }
    public void updateEdgeWeight(WarehouseNode target, double weight) {
        if (!edges.containsKey(target))
            throw new IllegalArgumentException("La arista no existe");
        edges.put(target, weight);
    }
    public Map<WarehouseNode, Double> getEdges() { return edges; }

    /* --------- índice de productos local --------- */
    public void putProduct(Product p)               { index.insert(p.getSku(), p); }
    public Product getProduct(String sku)           { return index.search(sku);    }
    public void deleteProduct(String sku)           { /* insertar delete() si lo implementas */ }
    public void updateProduct(Product p)            { index.insert(p.getSku(), p); }

    /* --------- getters --------- */
    public int getId()       { return id; }
    public String getLabel() { return label; }

    @Override public String toString() { return label + "(#" + id + ")"; }
}
