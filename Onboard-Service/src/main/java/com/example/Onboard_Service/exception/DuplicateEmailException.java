package com.example.Onboard_Service.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message){
        super(message);
    }

    public DuplicateEmailException(){

    }
}
