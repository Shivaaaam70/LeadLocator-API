package com.ect.Auth_Service.dto;

public class ResetPasswordRequest {

    private String token;
    private String newPassword;
    private String confirmPassword;

    // Default constructor
    public ResetPasswordRequest() {}

    // Constructor with all fields
    public ResetPasswordRequest(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Builder pattern
    public static ResetPasswordRequestBuilder builder() {
        return new ResetPasswordRequestBuilder();
    }

    public static class ResetPasswordRequestBuilder {
        private String token;
        private String newPassword;
        private String confirmPassword;

        public ResetPasswordRequestBuilder token(String token) {
            this.token = token;
            return this;
        }

        public ResetPasswordRequestBuilder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ResetPasswordRequestBuilder confirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public ResetPasswordRequest build() {
            return new ResetPasswordRequest(token, newPassword, confirmPassword);
        }
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
