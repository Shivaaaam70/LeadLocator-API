package com.example.leadmanagement.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
