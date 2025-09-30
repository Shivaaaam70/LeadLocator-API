package com.ect.Auth_Service.response;

public class ApiResponse<T>{

    private String message;

    private T token;

    // Default constructor
    public ApiResponse() {}

    // Constructor with all fields
    public ApiResponse(String message, T token) {
        this.message = message;
        this.token = token;
    }

    // Builder pattern
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    public static class ApiResponseBuilder<T> {
        private String message;
        private T token;

        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<T> token(T token) {
            this.token = token;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(message, token);
        }
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getToken() {
        return token;
    }

    public void setToken(T token) {
        this.token = token;
    }
}
