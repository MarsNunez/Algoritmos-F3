package com.warehouse.graph;

import java.util.*;

/** Grafo dirigido + algoritmos clásicos. */
public class WarehouseGraph {

    private final Map<Integer, WarehouseNode> nodes = new HashMap<>();

    /* ---------- nodos ---------- */
    public WarehouseNode addNode(int id, String label) {
        return nodes.computeIfAbsent(id, k -> new WarehouseNode(id, label));
    }
    public WarehouseNode getNode(int id) { return nodes.get(id); }
    public Collection<WarehouseNode> getNodes() { return nodes.values(); }

    /* ---------- aristas ---------- */
    public void addEdge(int fromId, int toId, double w) {
        WarehouseNode from = nodes.get(fromId), to = nodes.get(toId);
        if (from == null || to == null)
            throw new IllegalArgumentException("Nodo(s) inexistente(s)");
        from.addEdge(to, w);
    }
    public void removeEdge(int fromId, int toId) {
        WarehouseNode from = nodes.get(fromId), to = nodes.get(toId);
        if (from == null || to == null) return;
        from.removeEdge(to);
    }
    public void updateEdge(int fromId, int toId, double w) {
        WarehouseNode from = nodes.get(fromId), to = nodes.get(toId);
        if (from == null || to == null)
            throw new IllegalArgumentException("Nodos inexistentes");
        from.updateEdgeWeight(to, w);
    }

    /* ---------- Dijkstra ---------- */ // Cola de prioridad(pesos)
    public void printShortestPath(int fromId, int toId) {

        if (!nodes.containsKey(fromId) || !nodes.containsKey(toId)) {
            System.out.println("Alguno de los IDs no existe.");
            return;
        }

        /* --- estructuras auxiliares --- */
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        nodes.keySet().forEach(id -> dist.put(id, Double.POSITIVE_INFINITY));
        dist.put(fromId, 0.0);

        record Pair(int id, double d) implements Comparable<Pair> {
            public int compareTo(Pair o) { return Double.compare(d, o.d); }
        }
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(fromId, 0));

        /* --- Dijkstra (parada temprana cuando llegamos a destino) --- */
        while (!pq.isEmpty()) {
            Pair cur = pq.poll();
            if (cur.d > dist.get(cur.id)) continue;
            if (cur.id == toId) break;                    // ya llegamos

            for (var e : nodes.get(cur.id).getEdges().entrySet()) {
                int vId = e.getKey().getId();
                double alt = cur.d + e.getValue();
                if (alt < dist.get(vId)) {
                    dist.put(vId, alt);
                    prev.put(vId, cur.id);                // guardar padre
                    pq.add(new Pair(vId, alt));
                }
            }
        }

        /* --- reconstruir ruta --- */
        if (!prev.containsKey(toId) && fromId != toId) {
            System.out.println("No hay camino de " + fromId + " a " + toId);
            return;
        }
        List<Integer> route = new ArrayList<>();
        for (Integer at = toId; at != null; at = prev.get(at)) route.add(at);
        Collections.reverse(route);

        /* --- imprimir --- */
        System.out.print("Camino óptimo: ");
        for (int i = 0; i < route.size(); i++) {
            System.out.print(nodes.get(route.get(i)).getLabel());
            if (i < route.size() - 1) System.out.print(" -> ");
        }
        System.out.println("   (distancia = " + dist.get(toId) + ")");
    }













    /* ---------- BFS (Breadth-First Search) ---------- */
    /**
     * Realiza un recorrido en anchura (por niveles) comenzando desde un nodo específico.
     */
    public List<WarehouseNode> breadthFirstSearch(int startId) {
        List<WarehouseNode> visitedOrder = new ArrayList<>();
        WarehouseNode startingNode = nodes.get(startId);

        // Si el nodo inicial no existe, retornar lista vacía
        if (startingNode == null) return visitedOrder;

        Set<WarehouseNode> visitedNodes = new HashSet<>();
        Queue<WarehouseNode> nodesToVisit = new ArrayDeque<>();

        // Inicialización con el nodo de partida
        nodesToVisit.add(startingNode);
        visitedNodes.add(startingNode);

        while (!nodesToVisit.isEmpty()) {
            WarehouseNode currentNode = nodesToVisit.poll();
            visitedOrder.add(currentNode);

            // Explorar todos los nodos adyacentes
            for (WarehouseNode neighborNode : currentNode.getEdges().keySet()) {
                if (visitedNodes.add(neighborNode)) { // Si no ha sido visitado
                    nodesToVisit.add(neighborNode);
                }
            }
        }
        return visitedOrder;
    }

    /* ---------- DFS ---------- */ // PILA(LIFO) Explorar pasillos completos sistemáticamente
    public List<WarehouseNode> depthFirstSearch(int startId) {
        List<WarehouseNode> visitOrder = new ArrayList<>();
        WarehouseNode startingNode = nodes.get(startId);

        // Si el nodo inicial no existe, retornar lista vacía
        if (startingNode == null) return visitOrder;

        Set<WarehouseNode> visitedNodes = new HashSet<>();
        Deque<WarehouseNode> nodeStack = new ArrayDeque<>(); // Usamos Deque como pila

        // Inicialización con el nodo de partida
        nodeStack.push(startingNode);

        while (!nodeStack.isEmpty()) {
            WarehouseNode currentNode = nodeStack.pop();

            // Procesar solo si no ha sido visitado
            if (visitedNodes.add(currentNode)) {
                visitOrder.add(currentNode);

                // Obtener vecinos en orden inverso para procesamiento natural
                List<WarehouseNode> neighbors = new ArrayList<>(currentNode.getEdges().keySet());
                Collections.reverse(neighbors);

                // Apilar todos los vecinos para procesarlos después
                for (WarehouseNode neighbor : neighbors) {
                    nodeStack.push(neighbor);
                }
            }
        }
        return visitOrder;
    }
}
