package com.example.Company_Service.repository;

import com.example.Company_Service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyRepositoryCustom {
    List<Company> findByNameContainingIgnoreCase(String name);
    List<Company> findByHiringFlagTrue();
}
