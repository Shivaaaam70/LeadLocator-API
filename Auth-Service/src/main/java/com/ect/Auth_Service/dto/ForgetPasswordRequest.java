package com.ect.Auth_Service.dto;

public class ForgetPasswordRequest {

    private String email;

    // Default constructor
    public ForgetPasswordRequest() {}

    // Constructor with all fields
    public ForgetPasswordRequest(String email) {
        this.email = email;
    }

    // Builder pattern
    public static ForgetPasswordRequestBuilder builder() {
        return new ForgetPasswordRequestBuilder();
    }

    public static class ForgetPasswordRequestBuilder {
        private String email;

        public ForgetPasswordRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ForgetPasswordRequest build() {
            return new ForgetPasswordRequest(email);
        }
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
