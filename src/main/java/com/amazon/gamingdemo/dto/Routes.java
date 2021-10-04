package com.amazon.gamingdemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Routes {

    @JsonProperty
    private Set<String> routes;

    public Set<String> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<String> routes) {
        this.routes = routes;
    }
}
