package com.example.Crawler_Service1.repository;

import com.example.Crawler_Service1.entity.Jobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobsRepository extends JpaRepository<Jobs, String> {

    List<Jobs> findByPositionContainingIgnoreCase(String keyword);

    @Query("SELECT j FROM Jobs j ORDER BY j.date DESC")
    List<Jobs> findAllJobs();

    // delete jobs older than given date
    @Modifying
    @Transactional
    @Query("DELETE FROM Jobs j WHERE j.date < :cutoffDate")
    int deleteOldJobs(String cutoffDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM Jobs j WHERE j.active = false")
    int deleteInactiveJobs();
}
