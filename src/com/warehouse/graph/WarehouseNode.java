package com.warehouse.graph;

import com.warehouse.btree.BTree;
import com.warehouse.model.Product;

import java.util.HashMap;
import java.util.Map;

public class WarehouseNode {

    private static int ORDER = 5;     // orden del árbol B

    private int id;
    private String label;
    private Map<WarehouseNode,Double> edges = new HashMap<>(); //

    private BTree<String, Product> index = new BTree<>(ORDER); // El arbol que alacena los productos en esta ubicacion(Nodo).

    public WarehouseNode(int id, String label) {
        this.id = id;
        this.label = label;
    }

    /* ---------- aristas ---------- */
    public void addEdge(WarehouseNode t, double w){edges.put(t,w);}
    public void removeEdge(WarehouseNode t){edges.remove(t);}
    public void updateEdgeWeight(WarehouseNode t,double w){
        if(!edges.containsKey(t)) throw new IllegalArgumentException("No existe arista");
        edges.put(t,w);
    }
    public Map<WarehouseNode,Double> getEdges(){return edges;}

    /* ---------- inventario ---------- */
    public void putProduct(Product p){ index.insert(p.getSku(), p); } // inserta y actualiza a la vez
    public Product getProduct(String sku){ return index.search(sku); }

    /* ---------- inventario ---------- */
    public boolean addStock(String sku, int qty) {
        Product p = index.search(sku);
        if (p == null) return false;           // SKU no existe en este nodo
        p.addStock(qty);
        return true;
    }

    public boolean removeStock(String sku, int qty) {
        Product p = index.search(sku);
        if (p == null) return false;
        try {
            p.removeStock(qty);                // puede lanzar excepción
            return true;
        } catch (IllegalArgumentException e) {
            return false;                      // stock insuficiente
        }
    }


    public void deleteProduct(String sku){ /* delete opcional */ }

// SE USA PARA MOSTRAR EL GRAFO EN IMAGEN
    public String getFormattedProducts() {
        StringBuilder products = new StringBuilder();

        // Recorremos todos los productos en el BTree
        for (String sku : index.keysInOrder()) {
            Product p = index.search(sku);
            if (p != null) {
                if (products.length() > 0) products.append("\\n"); // Salto de línea para Graphviz
                products.append(String.format("%s: %s(%d)",
                        p.getSku(),
                        p.getName(),
                        p.getQuantity()));
            }
        }

        return products.length() > 0 ? products.toString() : "Sin productos";
    }

    public int getId(){return id;}
    public String getLabel(){return label;}
    public String toString(){return label+"Nodo(estante) (#"+id+")";}
}
