package com.ect.Auth_Service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        return ResponseEntity
                .status(400)
                .body(Map.of("message", ex.getMessage() == null ? "An error occurred" : ex.getMessage()));
    }
}
