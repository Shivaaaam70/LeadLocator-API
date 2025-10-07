package com.example.Crawler_Service1.controller;

import com.example.Crawler_Service1.entity.Jobs;
import com.example.Crawler_Service1.service.ExportExcelService;
import com.example.Crawler_Service1.service.JobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobsController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ExportExcelService exportExcelService;

    private static final Logger logger= LoggerFactory.getLogger(JobsController.class);

    @GetMapping("/all")
    public List<Jobs> getAllJobs(){
        logger.info("GET/jobs/all");
        return jobService.getAllJobsFromDb();
    }

    @GetMapping("/search")
    public List<Jobs> searchForJobs(@RequestParam @NotBlank String keyword){
        logger.info("GET/jobs/search keyword={}", keyword);
        return jobService.getJobsByKeyword(keyword);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshJobs() throws JsonProcessingException {
        logger.info("POST/jobs/refresh called");
        jobService.fetchAndStoreAllJobs();
        return ResponseEntity.ok("Jobs refreshed successfully!!");
    }

    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupJobs() {
        jobService.removeOldJobs();
        return ResponseEntity.ok("Old jobs cleanup executed");
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportJobs() {
        List<Jobs> jobs = jobService.getAllJobs();

        if (jobs == null || jobs.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }

        try {
            ByteArrayInputStream in = exportExcelService.exportJobsToExcel(jobs);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=jobs.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            e.printStackTrace(); // Optional: remove later, but useful for debugging
            return ResponseEntity.internalServerError().build();
        }
    }

}
