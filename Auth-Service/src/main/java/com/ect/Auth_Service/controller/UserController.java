package com.ect.Auth_Service.controller;

import com.ect.Auth_Service.dto.AuthRequest;
import com.ect.Auth_Service.entity.User;
import com.ect.Auth_Service.service.UserService;
import com.ect.Auth_Service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    // POST: /auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Normalize inputs
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        if (user.getFirstName() != null) {
            user.setFirstName(user.getFirstName().trim());
        }
        if (user.getLastName() != null) {
            user.setLastName(user.getLastName().trim());
        }
        if (user.getFirstName() == null || user.getFirstName().isBlank() ||
                user.getLastName() == null || user.getLastName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "First name and last name are required"));
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        // Ensure role has ROLE_ prefix
        if (!user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole());
        }

        userService.register(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            String email = request.getEmail() == null ? null : request.getEmail().trim().toLowerCase();
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            User user = userService.findByEmail(email);
            String token = jwtUtil.generateToken(email);

            Map<String, Object> userData = new HashMap<>();
            userData.put("role", user.getRole());
            userData.put("email", user.getEmail());
            userData.put("first_name", user.getFirstName());
            userData.put("last_name", user.getLastName());
            userData.put("token", token);

            Map<String, Object> response = new HashMap<>();
            response.put("data", userData);
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
    }

    private static Map<String, Object> getStringObjectMap(User user, String token) {
        Map<String, Object> userData = new HashMap<>();

        userData.put("role", user.getRole());
        userData.put("email", user.getEmail());
        userData.put("first_name", user.getFirstName());
        userData.put("last_name", user.getLastName());
        userData.put("token", token);

        // Final response
        Map<String, Object> response = new HashMap<>();
        response.put("data", userData);
        response.put("message", "Login successful");
        return response;
    }

    // POST /auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String token = userService.forgotPassword(email);

        return ResponseEntity.ok(Map.of(
                "message", "Reset token generated successfully",
                "token", token
        ));
    }

    // POST /auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        userService.resetPassword(token, newPassword, confirmPassword);

        return ResponseEntity.ok(Map.of(
                "message", "Password reset successful"
        ));
    }
}
