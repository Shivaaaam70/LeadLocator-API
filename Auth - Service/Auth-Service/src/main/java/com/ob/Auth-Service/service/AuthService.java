package com.example.demo.service;

import com.example.demo.repository.AuthRepository;
import com.example.demo.entity.AuthUser;
import com.example.demo.entity.PasswordResetToken;
import com.example.demo.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;


    public AuthUser registerUser(String email, String rawPassword,String mobileNo){
        if(authRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email already exist");
        }

        AuthUser user=new AuthUser();
        user.setEmail(email);
        user.setMobileNo(mobileNo);
        user.setProvider("local");
        user.setPassword(passwordEncoder.encode(rawPassword));

        return authRepository.save(user);
    }

    public AuthUser processOAuthPostLogin(String email){
        return authRepository.findByEmail(email).orElseGet(()->{
            AuthUser user=new AuthUser();
            user.setEmail(email);
            user.setPassword("");
            user.setMobileNo("");
            user.setProvider("google");

            return authRepository.save(user);
        });
    }

    public void initiatePasswordReset(String email){
        AuthUser user=authRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken=new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        System.out.println("Password reset link: http://localhost:8082/api/auth/reset-password?token="+token);
    }

    public void ResetPassword(String token,String newPassword){
        PasswordResetToken resetToken=tokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Invalid or expired token"));

        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token expired");
        }

        AuthUser user=resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        authRepository.save(user);

        tokenRepository.delete(resetToken);

    }
}
