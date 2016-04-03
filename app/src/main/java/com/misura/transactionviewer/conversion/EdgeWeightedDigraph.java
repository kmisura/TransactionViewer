package com.misura.transactionviewer.conversion;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kmisura on 03/04/16.
 */
public class EdgeWeightedDigraph {
    private final int V;
    private int E;
    private Set<DirectedEdge>[] adj;
    private int[] indegree;

    public EdgeWeightedDigraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        this.V = V;
        this.E = 0;
        this.indegree = new int[V];
        adj = (Set<DirectedEdge>[]) new Set[V];
        for (int v = 0; v < V; v++)
            adj[v] = new HashSet<DirectedEdge>();
    }

    public int V() {
        return V;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
    }

    public void addEdge(DirectedEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].add(e);
        indegree[w]++;
        E++;
    }

    public Iterable<DirectedEdge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }
}