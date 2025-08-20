package com.ect.Auth_Service.repo;

import com.ect.Auth_Service.entity.PasswordResetToken;
import com.ect.Auth_Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {

    Optional<PasswordResetToken> findByToken(String token);
}
