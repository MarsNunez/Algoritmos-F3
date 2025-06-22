package com.warehouse.btree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Árbol B genérico, orden ≥ 3.
 *  – Las claves y valores residen en **todos** los nodos.
 *  – Operaciones implementadas: search, insert (sin delete).
 */
public class BTree<K extends Comparable<K>, V> {

    private int order;
    private Node<K, V> root;

    public BTree(int order) {
        if (order < 3) throw new IllegalArgumentException("Orden debe ser ≥ 3");
        this.order = order;
        this.root  = new Node<>(true);
    }

    /* ---------- SEARCH ---------- */
    public V search(K key) {
        return search(root, key);
    }
    private V search(Node<K,V> n, K key) {
        int idx = Collections.binarySearch(n.keys, key);
        if (idx >= 0) return n.values.get(idx);
        if (n.leaf) return null;
        int child = -idx - 1;
        return search(n.children.get(child), key);
    }

    /* ---------- INSERT ---------- */
    public void insert(K key, V val) {
        Node<K,V> actualRoot = root;
//    Raíz llena?
        if (actualRoot.keys.size() == order-1) {  // valida si esta lleno
            Node<K,V> newRoot = new Node<>(false); // nueva raiz
            newRoot.children.add(actualRoot); // este nuevo nodo tendra como primer hijo al antiguo
            splitChild(newRoot,0);
            root = newRoot;
            insertNonFull(newRoot, key, val);
        } else insertNonFull(actualRoot, key, val);
    }

    private void insertNonFull(Node<K,V> n, K key, V val) {
        int i = n.keys.size()-1;
        if (n.leaf) {                // insertar en hoja
            int pos = Collections.binarySearch(n.keys, key);
            if (pos >= 0) { n.values.set(pos, val); return; }
            pos = -pos-1;
            n.keys.add(pos,key);
            n.values.add(pos,val);
        } else {
            while (i>=0 && key.compareTo(n.keys.get(i))<0) i--;
            i++;
            if (n.children.get(i).keys.size()==order-1) { // Verificamos si el hijo esta lleno
                splitChild(n,i);
                if (key.compareTo(n.keys.get(i))>0) i++;
            }
            insertNonFull(n.children.get(i), key, val); // volvemos a hacer insert en el hijo correspondiente
        }
    }

    private void splitChild(Node<K,V> parent, int idx) {
        Node<K,V> full = parent.children.get(idx);
        Node<K,V> right = new Node<>(full.leaf);
        int mid = (int) Math.ceil(order/2.0) - 1;


        // mover mitad derecha a nuevo nodo
        right.keys.addAll(full.keys.subList(mid+1, full.keys.size()));
        right.values.addAll(full.values.subList(mid+1, full.values.size()));
        full.keys.subList(mid+1, full.keys.size()).clear();
        full.values.subList(mid+1, full.values.size()).clear();

        // Condición: Si el nodo no es hoja (tiene hijos), también necesitamos dividirlos
        if (!full.leaf) {
            right.children.addAll(full.children.subList(mid+1, full.children.size()));
            full.children.subList(mid+1, full.children.size()).clear();
        }

        // subir clave media al padre
        K midKey = full.keys.remove(mid);
        V midVal = full.values.remove(mid);

        parent.keys.add(idx, midKey);
        parent.values.add(idx, midVal);
        parent.children.add(idx+1, right); // Agregamos al padre el nuevo nodo derecha.
    }

    /* ---------- DELETE ---------- */
    public boolean delete(K key) {
        if (root == null) return false;
        boolean deleted = delete(root, key);
        // Si la raíz queda vacía
        if (root.keys.isEmpty() && !root.leaf) {
            root = root.children.get(0);
        }
        return deleted;
    }

    private boolean delete(Node<K,V> node, K key) {
        int idx = Collections.binarySearch(node.keys, key);

        // Caso 1: Clave encontrada
        if (idx >= 0) {
            if (node.leaf) {
                node.keys.remove(idx);
                node.values.remove(idx);
                return true;
            }
            return deleteInternalNode(node, idx);
        }

        // Caso 2: Clave no encontrada
        if (node.leaf) return false;

        // Buscar en el hijo adecuado
        int childIdx = -idx - 1;
        Node<K,V> child = node.children.get(childIdx);

        // Verificar si el hijo necesita refuerzo antes de eliminar
        if (child.keys.size() < order / 2) {
            fillChild(node, childIdx);
            // Recalcular el índice después de posible fusión
            idx = Collections.binarySearch(node.keys, key);
            childIdx = idx >= 0 ? idx : -idx - 1;
            child = node.children.get(childIdx);
        }

        return delete(child, key);
    }

    private boolean deleteInternalNode(Node<K,V> node, int idx) {
        // Intentar con el predecesor (hijo izquierdo)
        Node<K,V> leftChild = node.children.get(idx);
        if (leftChild.keys.size() >= order / 2) {
            Node<K,V> predecessor = getRightmostNode(leftChild);
            K predKey = predecessor.keys.remove(predecessor.keys.size() - 1);
            V predVal = predecessor.values.remove(predecessor.values.size() - 1);
            node.keys.set(idx, predKey);
            node.values.set(idx, predVal);
            return delete(leftChild, predKey);
        }

        // Intentar con el sucesor (hijo derecho)
        Node<K,V> rightChild = node.children.get(idx + 1);
        if (rightChild.keys.size() >= order / 2) {
            Node<K,V> successor = getLeftmostNode(rightChild);
            K succKey = successor.keys.remove(0);
            V succVal = successor.values.remove(0);
            node.keys.set(idx, succKey);
            node.values.set(idx, succVal);
            return delete(rightChild, succKey);
        }

        // Fusión si ambos hijos tienen mínimo de claves
        mergeNodes(node, idx, leftChild, rightChild);
        return delete(leftChild, node.keys.remove(idx));
    }

    // Métodos auxiliares esenciales
    private void fillChild(Node<K,V> parent, int childIdx) {
        Node<K,V> child = parent.children.get(childIdx);

        // Intentar tomar prestado del hermano izquierdo
        if (childIdx > 0 && parent.children.get(childIdx - 1).keys.size() >= order / 2) {
            Node<K,V> leftSibling = parent.children.get(childIdx - 1);
            child.keys.add(0, parent.keys.get(childIdx - 1));
            child.values.add(0, parent.values.get(childIdx - 1));
            parent.keys.set(childIdx - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));
            parent.values.set(childIdx - 1, leftSibling.values.remove(leftSibling.values.size() - 1));
            if (!child.leaf) {
                child.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            }
        }
        // Intentar tomar prestado del hermano derecho
        else if (childIdx < parent.children.size() - 1 &&
                parent.children.get(childIdx + 1).keys.size() >= order / 2) {
            Node<K,V> rightSibling = parent.children.get(childIdx + 1);
            child.keys.add(parent.keys.get(childIdx));
            child.values.add(parent.values.get(childIdx));
            parent.keys.set(childIdx, rightSibling.keys.remove(0));
            parent.values.set(childIdx, rightSibling.values.remove(0));
            if (!child.leaf) {
                child.children.add(rightSibling.children.remove(0));
            }
        }
        // Fusión si no se puede tomar prestado
        else {
            if (childIdx > 0) {
                mergeNodes(parent, childIdx - 1, parent.children.get(childIdx - 1), child);
            } else {
                mergeNodes(parent, childIdx, child, parent.children.get(childIdx + 1));
            }
        }
    }

    private void mergeNodes(Node<K,V> parent, int idx, Node<K,V> left, Node<K,V> right) {
        left.keys.add(parent.keys.remove(idx));
        left.values.add(parent.values.remove(idx));
        left.keys.addAll(right.keys);
        left.values.addAll(right.values);
        if (!left.leaf) {
            left.children.addAll(right.children);
        }
        parent.children.remove(idx + 1);
    }

    private Node<K,V> getRightmostNode(Node<K,V> node) {
        while (!node.leaf) {
            node = node.children.get(node.children.size() - 1);
        }
        return node;
    }

    private Node<K,V> getLeftmostNode(Node<K,V> node) {
        while (!node.leaf) {
            node = node.children.get(0);
        }
        return node;
    }


//    LO USAMOS PARA EL GRAFICO DEL GRAFO
    public List<K> keysInOrder() {
        List<K> keys = new ArrayList<>();
        keysInOrder(root, keys);
        return keys;
    }
    //    LO USAMOS PARA EL GRAFICO DEL GRAFO
    private void keysInOrder(Node<K,V> node, List<K> keys) {
        if (node == null) return;

        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.leaf) {
                keysInOrder(node.children.get(i), keys);
            }
            keys.add(node.keys.get(i));
        }

        if (!node.leaf) {
            keysInOrder(node.children.get(node.children.size() - 1), keys);
        }
    }


    /* ---------- INTERNAL NODE ---------- */
    private static class Node<K,V> {
        List<K> keys = new ArrayList<>();
        List<V> values = new ArrayList<>();
        List<Node<K,V>> children = new ArrayList<>();
        boolean leaf;
        Node(boolean leaf){this.leaf=leaf;}
    }
}
