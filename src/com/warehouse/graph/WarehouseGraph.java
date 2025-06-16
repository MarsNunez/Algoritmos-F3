package com.warehouse.graph;

import java.util.*;

/** Grafo dirigido + algoritmos clásicos. */
public class WarehouseGraph {

    private final Map<Integer, WarehouseNode> nodes = new HashMap<>();

    /* ---------- nodos ---------- */
    public WarehouseNode addNode(int id, String label) {
        return nodes.computeIfAbsent(id, k -> new WarehouseNode(id, label));
    }
    public WarehouseNode getNode(int id)            { return nodes.get(id); }
    public Collection<WarehouseNode> getNodes()     { return nodes.values(); }

    /* ---------- aristas ---------- */
    public void addEdge(int fromId, int toId, double w) {
        WarehouseNode from = nodes.get(fromId), to = nodes.get(toId);
        if (from == null || to == null)
            throw new IllegalArgumentException("Nodos inexistentes");
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

    /* ---------- BFS ---------- */
    public List<WarehouseNode> bfs(int startId) {
        List<WarehouseNode> order = new ArrayList<>();
        WarehouseNode start = nodes.get(startId);
        if (start == null) return order;

        Set<WarehouseNode> vis = new HashSet<>();
        Queue<WarehouseNode> q = new ArrayDeque<>();
        q.add(start); vis.add(start);

        while (!q.isEmpty()) {
            WarehouseNode u = q.poll();
            order.add(u);
            for (WarehouseNode v : u.getEdges().keySet())
                if (vis.add(v)) q.add(v);
        }
        return order;
    }

    /* ---------- DFS ---------- */
    public List<WarehouseNode> dfs(int startId) {
        List<WarehouseNode> order = new ArrayList<>();
        WarehouseNode start = nodes.get(startId);
        if (start == null) return order;

        Set<WarehouseNode> vis = new HashSet<>();
        Deque<WarehouseNode> st = new ArrayDeque<>();
        st.push(start);

        while (!st.isEmpty()) {
            WarehouseNode u = st.pop();
            if (vis.add(u)) {
                order.add(u);
                List<WarehouseNode> neigh = new ArrayList<>(u.getEdges().keySet());
                Collections.reverse(neigh);
                for (WarehouseNode v : neigh) st.push(v);
            }
        }
        return order;
    }

    /* ---------- Dijkstra ---------- */
    public Map<Integer, Double> dijkstra(int srcId) {
        Map<Integer, Double> dist = new HashMap<>();
        for (Integer id : nodes.keySet()) dist.put(id, Double.POSITIVE_INFINITY);
        dist.put(srcId, 0.0);

        record Pair(WarehouseNode n, double d) implements Comparable<Pair> {
            public int compareTo(Pair o) { return Double.compare(d, o.d); }
        }
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(nodes.get(srcId), 0));

        while (!pq.isEmpty()) {
            Pair cur = pq.poll();
            if (cur.d > dist.get(cur.n.getId())) continue;
            for (Map.Entry<WarehouseNode, Double> e : cur.n.getEdges().entrySet()) {
                double alt = cur.d + e.getValue();
                if (alt < dist.get(e.getKey().getId())) {
                    dist.put(e.getKey().getId(), alt);
                    pq.add(new Pair(e.getKey(), alt));
                }
            }
        }
        return dist;
    }
}
