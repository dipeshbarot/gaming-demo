package com.amazon.gamingdemo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTest {

    @Test
    void testEquality() {
        Node node1 = new Node("A");
        node1.addChild("B", 9);

        Node node2 = new Node("A");
        node2.addChild("D", 4);

        assertEquals(node1, node2);
        assertEquals(node1.hashCode(), node2.hashCode());
    }
}
