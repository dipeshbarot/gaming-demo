package com.amazon.gamingdemo.controller;

import com.amazon.gamingdemo.GamingDemoApplication;
import com.amazon.gamingdemo.dao.LandmarkDAO;
import com.amazon.gamingdemo.dto.Routes;
import com.amazon.gamingdemo.model.Node;
import com.amazon.gamingdemo.model.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.amazon.gamingdemo.util.TestUtils.getJsonFromObject;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GamingDemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LandmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @SpyBean
    LandmarkDAO landmarkDAO;

    @BeforeEach
    void initialize(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testAddLandmarkMappingsSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/landmark/v0.1/mappings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getJsonFromObject(getRoutes()))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void testAddLandmarkMappingsFailure() throws Exception {
        // Bad Request - Empty Routes
        mockMvc.perform(MockMvcRequestBuilders.post("/landmark/v0.1/mappings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(getJsonFromObject(new Routes()))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        // Bad Request - Empty Routes
        Routes routes = new Routes();
        routes.setRoutes(new HashSet<>());
        mockMvc.perform(MockMvcRequestBuilders.post("/landmark/v0.1/mappings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(getJsonFromObject(routes))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        // Bad Request - Invalid Routes (missing distance)
        routes = new Routes();
        routes.setRoutes(Set.of("AB"));
        mockMvc.perform(MockMvcRequestBuilders.post("/landmark/v0.1/mappings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(getJsonFromObject(routes))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetDistanceBetweenLandMarksSuccess() throws Exception {
        Mockito.when(landmarkDAO.getLandmarks()).thenReturn(getLandmarkMappings());
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/distance/A-D-E")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value(10));

        // Nodes not connected
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/distance/A-E-C")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Path not found"));
    }

    @Test
    void testGetDistanceBetweenLandMarksFailure() throws Exception {
        // Route Not Found
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/distance/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        // Invalid separator
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/distance/A+D+E")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());


    }

    @Test
    void testGetNumberOfRoutesBetweenNodesSuccess() throws Exception {
        Mockito.when(landmarkDAO.getLandmarks()).thenReturn(getLandmarkMappings());
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/routes/between/nodes/A/C")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value(2));
    }

    @Test
    void testGetNumberOfRoutesBetweenNodesFailure() throws Exception {
        // No destination given
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/routes/between/nodes/A/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        // Empty Landmarks
        Mockito.when(landmarkDAO.getLandmarks()).thenReturn(new HashMap<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/routes/between/nodes/A/C")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        // Unknown Node
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/routes/between/nodes/A/J")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        // Any other error
        Mockito.when(landmarkDAO.getLandmarks()).thenThrow(new RuntimeException());
        mockMvc.perform(MockMvcRequestBuilders.get("/landmark/v0.1/routes/between/nodes/A/C")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isInternalServerError());
    }

    private Routes getRoutes() {
        Routes routes = new Routes();
        Set<String> routeSet = Set.of("AB3", "BC9", "CD3", "DE6",
                "AD4", "DA5", "CE2", "AE4", "EB1");
        routes.setRoutes(routeSet);
        return  routes;

    }
    private Map<String, Node> getLandmarkMappings() {
        HashMap<String, Node> map = new HashMap<>();

        Node node1 = new Node("A");
        Node node2 = new Node("B");
        Node node3 = new Node("C");
        Node node4 = new Node("D");
        Node node5 = new Node("E");

        node1.getChildren().add(new Pair("B", 3));
        node2.getChildren().add(new Pair("C", 9));
        node3.getChildren().add(new Pair("D", 3));
        node4.getChildren().add(new Pair("E", 6));
        node1.getChildren().add(new Pair("D", 4));
        node4.getChildren().add(new Pair("A", 5));
        node3.getChildren().add(new Pair("E", 2));
        node1.getChildren().add(new Pair("E", 4));
        node5.getChildren().add(new Pair("B", 1));

        map.put("A", node1);
        map.put("B", node2);
        map.put("C", node3);
        map.put("D", node4);
        map.put("E", node5);

        return map;
    }
}
