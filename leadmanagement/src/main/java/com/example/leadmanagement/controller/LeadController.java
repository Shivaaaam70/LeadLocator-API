package com.example.leadmanagement.controller;

import com.example.leadmanagement.entity.Lead;
import com.example.leadmanagement.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
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
    public ResponseEntity<Map<String, Object>>getLeadById(@PathVariable Long id) {
        Lead lead = leadService.getLeadById(id);

        Map<String , Object> response = Map.of(
                "message" , "Lead received successfully",
                "data", lead

        );
        return ResponseEntity.ok(response);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Map<String,Object>>updateLead(@PathVariable Long id, @RequestBody Lead leadDeatils) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(Map.of(
                "message", "Lead updated successfully",
                "data", leadDeatils
        ));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,Object>>deleteLead(@PathVariable Long id) {
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
                "message" , "Lead count retrived successfully",
                "data", data
        );
        return ResponseEntity.ok(response);
    }



}
