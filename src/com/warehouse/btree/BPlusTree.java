package com.warehouse.btree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Árbol B+ genérico de orden N (N ≥ 3).
 * – Las claves se duplican en nodos internos; los valores existen
 *   exclusivamente en las hojas.
 * – Todas las hojas están enlazadas (puntero «next») para recorridos
 *   secuenciales eficientes.
 *
 * @param <K> Clave comparable
 * @param <V> Valor asociado (solo se almacena en las hojas)
 */
public class BPlusTree<K extends Comparable<K>, V> {

    /* ======== Configuración central ======== */
    private final int order;          // Máx. hijos por nodo interno
    private Node<K, V> root;          // Puede ser hoja o interno

    /* ---------- constructor ---------- */
    public BPlusTree(int order) {
        if (order < 3) throw new IllegalArgumentException("Orden debe ser ≥ 3");
        this.order = order;
        this.root  = new LeafNode<>();          // árbol vacío = una hoja
    }

    /* ======== OPERACIÓN: BÚSQUEDA ======== */
    public V search(K key) {
        LeafNode<K, V> leaf = descendToLeaf(root, key);
        return leaf.getValue(key);
    }

    private LeafNode<K, V> descendToLeaf(Node<K, V> n, K key) {
        if (n instanceof LeafNode<?, ?>)        // llegó a hoja
            return (LeafNode<K, V>) n;

        InternalNode<K, V> internal = (InternalNode<K, V>) n;
        int pos = Collections.binarySearch(internal.keys, key);
        int childIndex = (pos >= 0) ? pos + 1 : -pos - 1;
        return descendToLeaf(internal.children.get(childIndex), key);
    }

    /* ======== OPERACIÓN: INSERCIÓN ======== */
    public void insert(K key, V value) {
        Split<K, V> split = insertRecursive(root, key, value);

        /* Si la raíz se dividió, crear nueva raíz */
        if (split != null) {
            InternalNode<K, V> newRoot = new InternalNode<>();
            newRoot.keys.add(split.key);
            newRoot.children.add(split.left);
            newRoot.children.add(split.right);
            root = newRoot;
        }
    }

    private Split<K, V> insertRecursive(Node<K, V> node, K key, V value) {

        if (node instanceof LeafNode<?, ?>) {               // caso hoja
            LeafNode<K, V> leaf = (LeafNode<K, V>) node;
            leaf.insert(key, value);

            if (leaf.isOverflow(order))                     // ¿rebalsa?
                return leaf.split();                        // regresa info de split
            return null;                                    // sin desbordamiento
        }

        /* caso nodo interno */
        InternalNode<K, V> internal = (InternalNode<K, V>) node;
        int pos = Collections.binarySearch(internal.keys, key);
        int childIndex = (pos >= 0) ? pos + 1 : -pos - 1;

        Split<K, V> childSplit =
                insertRecursive(internal.children.get(childIndex), key, value);

        if (childSplit == null) return null;                // hijo no rebalsó

        /* insertar clave promovida en este nodo interno */
        internal.keys.add(childIndex, childSplit.key);
        internal.children.set(childIndex, childSplit.left);
        internal.children.add(childIndex + 1, childSplit.right);

        if (internal.isOverflow(order))
            return internal.split(order);                   // propagar split
        return null;
    }

    /* ============ NODOS & AUXILIARES ============ */

    private interface Node<K, V> { }

    /* ----- NODO HOJA ----- */
    private static class LeafNode<K extends Comparable<K>, V> implements Node<K, V> {
        private final List<K> keys    = new ArrayList<>();
        private final List<V> values  = new ArrayList<>();
        private LeafNode<K, V> next;        // enlace a la hoja derecha

        /* inserta o actualiza */
        void insert(K key, V value) {
            int pos = Collections.binarySearch(keys, key);
            if (pos >= 0) {
                values.set(pos, value);     // clave existe → reemplaza valor
            } else {
                pos = -pos - 1;             // nueva posición
                keys.add(pos, key);
                values.add(pos, value);
            }
        }

        boolean isOverflow(int order) {         // N claves = order → overflow
            return keys.size() == order;
        }

        /* divide la hoja en dos y retorna estructura Split */
        Split<K, V> split() {
            int mid = keys.size() / 2;          // división “a la mitad”

            LeafNode<K, V> sibling = new LeafNode<>();
            /* pasar mitad derecha a hermano */
            while (keys.size() > mid) {
                sibling.keys.add(keys.remove(mid));
                sibling.values.add(values.remove(mid));
            }

            /* enlazar hojas */
            sibling.next = this.next;
            this.next = sibling;

            K promote = sibling.keys.get(0);    // primera clave del nuevo hermano
            return new Split<>(promote, this, sibling);
        }

        V getValue(K key) {
            int pos = Collections.binarySearch(keys, key);
            return (pos >= 0) ? values.get(pos) : null;
        }
    }

    /* ----- NODO INTERNO ----- */
    private static class InternalNode<K extends Comparable<K>, V> implements Node<K, V> {
        private final List<K> keys               = new ArrayList<>();
        private final List<Node<K, V>> children  = new ArrayList<>();

        boolean isOverflow(int order) {          // hijos > order → overflow
            return children.size() > order;
        }

        Split<K, V> split(int order) {
            int midIdx = keys.size() / 2;
            K midKey = keys.get(midIdx);

            InternalNode<K, V> sibling = new InternalNode<>();
            /* copiar mitad derecha */
            sibling.keys.addAll(keys.subList(midIdx + 1, keys.size()));
            sibling.children.addAll(children.subList(midIdx + 1, children.size()));

            /* recortar mitad derecha de este nodo */
            keys.subList(midIdx, keys.size()).clear();
            children.subList(midIdx + 1, children.size()).clear();

            return new Split<>(midKey, this, sibling);
        }
    }

    /* ----- ESTRUCTURA SPLIT ----- */
    private static class Split<K, V> {
        final K key;             // clave a promover
        final Node<K, V> left;   // subárbol izquierdo
        final Node<K, V> right;  // subárbol derecho
        Split(K k, Node<K, V> l, Node<K, V> r) {
            key = k; left = l; right = r;
        }
    }
}
