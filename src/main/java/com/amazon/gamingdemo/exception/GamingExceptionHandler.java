package com.amazon.gamingdemo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GamingExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamingExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleErrors(Exception e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleErrors(ResponseStatusException e) {
        LOGGER.error(e.getReason());
        return new ResponseEntity<>(e.getReason(), e.getStatus());
    }
}
