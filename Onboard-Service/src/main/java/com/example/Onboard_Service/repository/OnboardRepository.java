package com.example.Onboard_Service.repository;

import com.example.Onboard_Service.entity.Onboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardRepository extends JpaRepository<Onboard,Long> {

    Optional<Onboard> findByEmail(String email);
}
