package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http
                .csrf().disable()
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("/auth/**", "/oauth2/**","/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form->form
                         .permitAll()
                )
                .oauth2Login(auth->auth
                        .loginPage("/api/auth/login")
                        .defaultSuccessUrl("/api/auth/oauth-success",true)
                        .permitAll()
                )
                .logout(logout->logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/api/auth/login")
                        .permitAll()
                )
                .sessionManagement(session ->session
                        .maximumSessions(10)
                        .maxSessionsPreventsLogin(false)
                );
        return http.build();
    }
}
