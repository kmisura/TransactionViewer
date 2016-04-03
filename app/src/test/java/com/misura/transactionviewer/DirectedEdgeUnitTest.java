package com.misura.transactionviewer;

import com.misura.transactionviewer.conversion.DirectedEdge;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class DirectedEdgeUnitTest {
    private static final double delta = 0.00000000001;
    @Test
    public void constructorAndGetters() throws Exception {
        DirectedEdge de = new DirectedEdge(3,4,1.3);
        assertEquals(3, de.from());
        assertEquals(4, de.to());
        assertEquals(1.3, de.weight(), delta);
    }
}