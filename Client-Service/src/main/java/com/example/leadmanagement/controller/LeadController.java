package com.example.leadmanagement.controller;

import com.example.leadmanagement.service.ExcelExport;
import com.example.leadmanagement.entity.Lead;
import com.example.leadmanagement.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/leads")
public class LeadController {

    private final LeadService leadService;

    private final ExcelExport excelExport;

    public LeadController(LeadService leadService, ExcelExport excelExport) {
        this.leadService = leadService;
        this.excelExport = excelExport;
    }

    // Create
    @PostMapping
    public ResponseEntity<?>addLead(@Valid @RequestBody Lead lead) {
        Lead saveLead = leadService.createLead(lead);

        Map<String, Object> response = Map.of(
                "message", "lead created successfully",
                "data", saveLead
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // Read All
    @GetMapping
    public ResponseEntity<Map<String, Object>>getAllLeads() {
        List<Lead> leads = leadService.getAllLead();
        return ResponseEntity.ok(Map.of(
                "message" , "Lead received successfully",
                "data", leads
        ));
    }

    // Read by Id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>>getLeadById(@PathVariable String id) {
        Lead lead = leadService.getLeadById(id);

        Map<String , Object> response = Map.of(
                "message" , "Lead received successfully",
                "data", lead

        );
        return ResponseEntity.ok(response);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Map<String,Object>>updateLead(@PathVariable String id, @RequestBody Lead leadDetails) {

        Lead updatedLead=leadService.updateLead(id, leadDetails);

        return ResponseEntity.ok(Map.of(
                "message", "Lead updated successfully",
                "data", updatedLead
        ));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,Object>>deleteLead(@PathVariable String id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(Map.of(
                "message", "Lead deleted successfully"
        ));
    }

    @GetMapping("/count/{broughtBy}")
    public ResponseEntity<Map<String,Object>> getLeadCount (@PathVariable String broughtBy) {
        long count = leadService.getLeadCount(broughtBy);

        Map<String , Object> data = Map.of(
                "broughtBy", broughtBy ,
                "totalLeads", count
        );

        Map<String , Object> response = Map.of(
                "message" , "Lead count retrieved successfully",
                "data", data
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportLeadsToExcel() {
        List<Lead> leads = leadService.getAllLead();
        ByteArrayInputStream in = excelExport.exportLeadsToExcel(leads);

        Map<String,Object> response=Map.of("Excel sheet downloaded",leads);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leads.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }



}
