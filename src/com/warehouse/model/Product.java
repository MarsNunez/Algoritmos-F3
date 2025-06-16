// ---------- Product.java ----------
package com.warehouse.model;

/**
 * Representa un artículo almacenado en el inventario.
 *  - sku: identificador único (clave primaria)
 *  - name: descripción legible
 *  - quantity: existencias disponibles
 */
public class Product {

    private final String sku;      // e.g. "SKU-001"
    private final String name;     // e.g. "Taladro"
    private int quantity;          // stock actual

    public Product(String sku, String name, int quantity) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
    }

    /* ---------- Getters ---------- */
    public String getSku() { return sku; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    /* ---------- Operaciones de stock ---------- */
    /** Incrementa existencias (puede ser negativo para restar). */
    public void addStock(int delta) {
        this.quantity += delta;
    }

    /** Resta existencias; lanza excepción si no hay suficiente. */
    public void removeStock(int delta) {
        if (delta > quantity) {
            throw new IllegalArgumentException("Stock insuficiente para " + sku);
        }
        this.quantity -= delta;
    }

    public String toString() {
        return String.format("%s [%s] : %d unidades disponibles.", name, sku, quantity);
    }
}
