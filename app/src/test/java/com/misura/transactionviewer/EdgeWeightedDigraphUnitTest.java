package com.misura.transactionviewer;

import com.misura.transactionviewer.conversion.DirectedEdge;
import com.misura.transactionviewer.conversion.EdgeWeightedDigraph;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kmisura on 03/04/16.
 */
public class EdgeWeightedDigraphUnitTest {
    @Test
    public void edgeAddsCorrectly() throws Exception {
        EdgeWeightedDigraph g = new EdgeWeightedDigraph(10);
        g.addEdge(new DirectedEdge(0, 1, 5.0));
        assertNotNull(g.adj(0));
        assertEquals(1, g.adj(0));
    }
}
