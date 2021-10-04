package com.amazon.gamingdemo.controller;

import com.amazon.gamingdemo.dto.Routes;
import com.amazon.gamingdemo.service.LandmarkService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "LandmarkService", tags = "Landmark Service")
@RestController
@RequestMapping("/landmark/v0.1")
public class LandmarkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandmarkController.class);

    private LandmarkService landMarkService;

    @Autowired
    public LandmarkController(LandmarkService landMarkService) {
        this.landMarkService = landMarkService;
    }

    @ApiOperation(value = "Get minimum distance between landmarks")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully returned distance between landmarks"),
            @ApiResponse(code = 400, message = "Invalid route"),
            @ApiResponse(code = 404, message = "Route not found"),
            @ApiResponse(code = 500, message = "An error occurred while calculating distance between landmarks")
    })
    @GetMapping(value = "/distance/{route}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDistanceBetweenLandMarks(@ApiParam(value = "Route", required = true)
                                                                  @PathVariable String route) {
        LOGGER.info("Request received for route : {}", route);
        return ResponseEntity.of(landMarkService.getDistanceBetweenLandMarks(route));
    }

    @ApiOperation(value = "Add landmark mappings")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added all mappings"),
            @ApiResponse(code = 400, message = "Invalid route"),
            @ApiResponse(code = 500, message = "An error occurred while adding landmark mappings")
    })
    @PostMapping(value = "/mappings", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addLandmarkMappings(@RequestBody Routes routes) {
        LOGGER.info("Request received : {}", routes);
        landMarkService.addLandMarks(routes.getRoutes());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Get number of routes between two nodes with pre-defined max stops")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully returned the number of routes between two nodes"),
            @ApiResponse(code = 500, message = "An error occurred while finding routes between two nodes")
    })
    @GetMapping(value = "/routes/between/nodes/{source}/{destination}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getNumberOfRoutesBetweenNodes(@PathVariable String source,
                                                                 @PathVariable String destination) {
        LOGGER.info("Request received for source : {} and destination : {}",
                source, destination);
        return ResponseEntity.of(landMarkService.getNorOfRoutesBetweenNodes(source, destination));
    }
}
