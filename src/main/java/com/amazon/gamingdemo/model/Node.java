package com.amazon.gamingdemo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private String vertex;
    private List<Pair> children;

    public Node(String vertex) {
        this.vertex = vertex;
        children = new ArrayList<>();
    }

    public void addChild(String vertex, int length) {
        Pair p = new Pair(vertex, length);
        children.add(p);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(vertex, node.vertex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex);
    }

    public List<Pair> getChildren() {
        return children;
    }
}
