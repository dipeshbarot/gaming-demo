package com.amazon.gamingdemo.dao;

import com.amazon.gamingdemo.model.Node;
import com.amazon.gamingdemo.repository.LandmarkRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class LandmarkDAO implements LandmarkRepository {

    private Map<String, Node> landmarks = new HashMap<>();

    @Override
    public void addLandmarks(String source, String destination, Integer distance) {
        landmarks.putIfAbsent(source, new Node(source));
        landmarks.putIfAbsent(source, new Node(destination));
        landmarks.get(source).addChild(destination, distance);
    }

    public Map<String, Node> getLandmarks() {
        return landmarks;
    }
}
