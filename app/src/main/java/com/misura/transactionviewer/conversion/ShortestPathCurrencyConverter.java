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
            G.addEdge(new DirectedEdge(e.to(), e.from(), 1.0/e.weight()));  //TODO: Not sure if we should go back the edges? If this was to test
            //finding a directed path, then I would say no. But a lot of conversions are impossible in the example if we don't go back on edges
        }
    }

    @Override
    public double getConversionRate(int currency1, int currency2) {
        if (source != currency1) {
            doBFS(currency1);   //avoid doing BFS if already done with the same source
            source = currency1;
        }
        return distTo(currency2);
    }

    private void doBFS(int s) {
        distTo = new double[G.V()];
        edgeTo = new DirectedEdge[G.V()];
        boolean[] visited = new boolean[G.V()];

        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        visited[s] = true;
        distTo[s] = 1;
        while (!queue.isEmpty()) {
            int x = queue.poll();
            for (DirectedEdge e : G.adj(x)) {
                int v = e.from(), w = e.to();
                if(!visited[w]){
                    visited[w] = true;
                    distTo[w] = distTo[v] * e.weight();
                    edgeTo[w] = e;
                    queue.add(w);
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


    /**
     * Useful to tell the person just what exchanges a person needs to make to get his target currency.
     */
    public Iterable<DirectedEdge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }
}
