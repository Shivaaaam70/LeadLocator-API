package com.ect.Auth_Service.service;

import com.ect.Auth_Service.entity.User;
import com.ect.Auth_Service.repo.PasswordResetTokenRepository;
import com.ect.Auth_Service.repo.UserRepository;
import com.ect.Auth_Service.util.JwtUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            PasswordResetTokenRepository tokenRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user=userRepository.findById(email).orElseThrow(()->new UsernameNotFoundException("User not found with id"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate a reset JWT token valid for 15 mins
        return jwtUtil.generateResetToken(email);
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (!jwtUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
