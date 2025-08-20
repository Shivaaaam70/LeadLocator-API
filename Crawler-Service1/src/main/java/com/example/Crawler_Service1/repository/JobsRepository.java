package com.example.Crawler_Service1.repository;

import com.example.Crawler_Service1.entity.Jobs;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobsRepository extends JpaRepository<Jobs,Long> {

    List<Jobs> findByPositionContainingIgnoreCase(String keyword);

    @Query("SELECT j FROM Jobs j ORDER BY j.date DESC")
    List<Jobs> findAllJobs();
}
