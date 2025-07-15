package com.example.demo.controller;

import com.example.demo.entity.AuthUser;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String,String> request) {
        try {
            service.registerUser(request.get("email"), request.get("password"), request.get("mobileNo"));
            return ResponseEntity.ok("User Registered");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/oauth-success")
    public ResponseEntity<?> oauthSucess(Authentication authentication){
        OAuth2User oauthuser= (OAuth2User) authentication.getPrincipal();
        String email=oauthuser.getAttribute("email");
        AuthUser user=service.processOAuthPostLogin(email);
        return ResponseEntity.ok("Login successful: "+user.getEmail());
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(Authentication authentication){
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Authenticated");
        }
        return ResponseEntity.ok(authentication.getName());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(Map<String,String> body){
        try{
            service.initiatePasswordReset(body.get("emai"));
            return ResponseEntity.ok("password reset link sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(Map<String,String> body){
        try{
            service.ResetPassword(body.get("token"), body.get("newPassword"));
            return ResponseEntity.ok("Password reset successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
