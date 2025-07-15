package com.ob.Crawler_Service.repository;

import com.ob.Crawler_Service.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost,Long> {

    boolean existsByUrl(String url);
}
