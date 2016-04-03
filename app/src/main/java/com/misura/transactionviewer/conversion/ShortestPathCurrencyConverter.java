package com.misura.transactionviewer.conversion;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by kmisura on 03/04/16.
 * This class uses BFS to find the shortest path between source and sink currencies.
 * Even though it is BFS we don't simply return the number of hops but return the
 * multiplication of all conversions on the route.
 */
public class ShortestPathCurrencyConverter implements CurrencyConverter {
    private double[] distTo;
    private DirectedEdge[] edgeTo;
    int source = -1;
    EdgeWeightedDigraph G;

    public ShortestPathCurrencyConverter(int V, List<DirectedEdge> edges) {
        G = new EdgeWeightedDigraph(V);
        for (DirectedEdge e : edges) {
            G.addEdge(e);
        }
    }

    @Override
    public double getConversionRate(int currency1, int currency2) {
        if (source != currency1) {
            doBFS(currency1);   //avoid doing BFS if already done with the same source
        }
        return distTo(currency2);
    }

    private void doBFS(int s) {
        distTo = new double[G.V()];
        edgeTo = new DirectedEdge[G.V()];
        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        Queue<Integer> pq = new LinkedList<>();
        pq.add(s);
        while (!pq.isEmpty()) {
            int x = pq.poll();
            for (DirectedEdge e : G.adj(x)) {
                int v = e.from(), w = e.to();
                if (distTo[w] > distTo[v] + e.weight()) {
                    distTo[w] = distTo[v] + e.weight();
                    edgeTo[w] = e;
                }
            }
        }
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<DirectedEdge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }
}
