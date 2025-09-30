package com.example.leadmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


//    private BindException ex;

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));

    }



    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
