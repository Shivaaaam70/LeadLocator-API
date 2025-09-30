package com.example.Company_Service.controller;

import com.example.Company_Service.entity.Company;
import com.example.Company_Service.repository.CompanyRepository;
import com.example.Company_Service.service.CrawlerService;
import com.example.Company_Service.service.ExportExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CrawlerService crawlerService;
    private final CompanyRepository repo;
    private final ExportExcelService exportExcelService;

    public CompanyController(CrawlerService crawlerService,
                             CompanyRepository repo,
                             ExportExcelService exportExcelService) {
        this.crawlerService = crawlerService;
        this.repo = repo;
        this.exportExcelService = exportExcelService;
    }

    @PostMapping("/crawl")
    public ResponseEntity<String> crawl() {
        try {
            boolean success = crawlerService.crawlAllEndpoints();
            if (success) {
                return ResponseEntity.ok(" Crawl completed successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Crawl failed. Check logs for details.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception during crawl: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Company>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Company> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/search")
    public ResponseEntity<List<Company>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(repo.findByNameContainingIgnoreCase(name));
    }


    @GetMapping("/hiring")
    public ResponseEntity<List<Company>> getHiring() {
        return ResponseEntity.ok(repo.findByHiringFlagTrue());
    }


    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportCompanies() {
        List<Company> companies = repo.findAll();
        ByteArrayInputStream in = exportExcelService.exportCompaniesToExcel(companies);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=companies.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
