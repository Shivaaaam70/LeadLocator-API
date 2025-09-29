package com.ect.Auth_Service.controller;

import com.ect.Auth_Service.dto.AuthRequest;
import com.ect.Auth_Service.entity.User;
import com.ect.Auth_Service.service.UserService;
import com.ect.Auth_Service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    //POST: /auth/register
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        userService.register(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }


    //POST: /auth/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = jwtUtil.generateToken(request.getEmail());
            User user = userService.findByEmail(request.getEmail());
            String randomId = UUID.randomUUID().toString();
            user.setUserId(randomId);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("email", user.getEmail());
            data.put("role", user.getRole());
            data.put("first_name", user.getFirstName());
            data.put("last_name", user.getLastName());
            data.put("userId", user.getUserId());

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Login successful",
                            "data", data
                    )
            );
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Invalid email or password");
            return ResponseEntity.status(403).body(error);
        }
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
