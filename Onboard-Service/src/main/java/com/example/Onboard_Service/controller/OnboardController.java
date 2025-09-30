package com.example.Onboard_Service.controller;

import com.example.Onboard_Service.entity.ApiResponse;
import com.example.Onboard_Service.entity.Onboard;
import com.example.Onboard_Service.service.ExcelExport;
import com.example.Onboard_Service.service.OnboardService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/onboard")
public class OnboardController {

    private final OnboardService onboardService;
    private final ExcelExport excelExport;

    public OnboardController(OnboardService onboardService, ExcelExport excelExport) {
        this.onboardService = onboardService;
        this.excelExport = excelExport;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Onboard>> createOnboard(@RequestBody Onboard onboard) {
        Onboard onboard1 = onboardService.create(onboard);
        ApiResponse<Onboard> response = new ApiResponse<>("Onboarding created successfully!!", onboard1);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Onboard>>> getAllOnboards() {
        List<Onboard> onboardList = onboardService.getAll();
        ApiResponse<List<Onboard>> response = new ApiResponse<>("Getting all onboardings", onboardList);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Onboard>> updateOnboard(@PathVariable Long id, @RequestBody Onboard updatedOnboard) {
        Onboard update = onboardService.update(id, updatedOnboard);
        ApiResponse<Onboard> response = new ApiResponse<>("Onboard updated successfully", update);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOnboard(@PathVariable Long id) {
        onboardService.delete(id); // assuming delete is void
        ApiResponse<Void> response = new ApiResponse<>("Onboard deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportOnboardToExcel() {
        List<Onboard> onboards = onboardService.getAll();
        ByteArrayInputStream in = excelExport.exportOnboardsToExcel(onboards);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=onboards.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
