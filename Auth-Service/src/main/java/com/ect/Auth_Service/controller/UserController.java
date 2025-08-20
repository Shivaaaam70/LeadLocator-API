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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }


    //POST: /auth/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtUtil.generateToken(request.getEmail());
        User user = userService.findByEmail(request.getEmail());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("email", user.getEmail());
        data.put("role", user.getRole());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "data", data
                )
        );
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

//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<String>> login(@RequestBody AuthRequest request) {
//        Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//        String token = jwtUtil.generateToken(auth.getName());
//        return ResponseEntity.ok(new ApiResponse<>("Login successful", token));
//    }

//    @PostMapping("/login")
//    public ResponseEntity<Map<String,String>> login(@RequestBody AuthRequest request){
//        Authentication auth=authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
//        String token= jwtUtil.generateToken(auth.getName());
//        return ResponseEntity.ok(Map.of("token",token));
//    }




// @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody User user){
//        User user1=userService.register(user);
//        return ResponseEntity.ok("User Registered Successfully!!");
//    }
//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
//        User saved = userService.register(user);
//        return ResponseEntity.ok(new ApiResponse<>("User signed up successfully", saved));
//    }
}
