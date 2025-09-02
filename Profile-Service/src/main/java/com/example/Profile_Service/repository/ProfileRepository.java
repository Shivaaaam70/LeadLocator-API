package com.example.Profile_Service.repository;

import com.example.Profile_Service.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,String> {

    Optional<Profile> findByEmail(String email);
}
