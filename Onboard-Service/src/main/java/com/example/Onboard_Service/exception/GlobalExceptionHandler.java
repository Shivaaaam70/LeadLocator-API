package com.example.Onboard_Service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String,Object>> handlingException(DuplicateEmailException ex){
        Map<String,Object> error=new HashMap<>();
        error.put("message",ex.getMessage());
        return ResponseEntity.ok(error);

    }
}
