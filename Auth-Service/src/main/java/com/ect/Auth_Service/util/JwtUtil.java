package com.ect.Auth_Service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private String secret= "hmtgfUoVMe";

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("EcomAuth")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*60*10))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();

    }
    // RESET PASSWORD token - valid for 15 minutes
    public String generateResetToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer("PasswordReset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public String extractUsername(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token){
        try{
            Claims claims=getClaims(token);
            return claims.getExpiration().after(new Date());
        }catch (Exception e){
            return false;
        }
    }
    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

}
