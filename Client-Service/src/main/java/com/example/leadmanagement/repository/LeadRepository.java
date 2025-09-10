package com.example.leadmanagement.repository;

import com.example.leadmanagement.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, String> {

    boolean existsByEmail(String email);

//    @Query("Select count(1) from Lead 1 WHERE 1.broughtBy = :broughtBy")
    Long countByBroughtBy(String broughtBy);


}
