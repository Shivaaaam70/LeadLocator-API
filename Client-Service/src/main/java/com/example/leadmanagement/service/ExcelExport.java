package com.example.leadmanagement.service;

import com.example.leadmanagement.entity.Lead;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExport {

    public ByteArrayInputStream exportLeadsToExcel(List<Lead> leads) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Leads");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Name", "Email", "Location", "Requirement", "Description","BroughtBy"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Data rows
            int rowIdx = 1;
            for (Lead lead : leads) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(lead.getName());
                row.createCell(1).setCellValue(lead.getEmail());
                row.createCell(2).setCellValue(lead.getLocation());
                row.createCell(3).setCellValue(lead.getRequirement());
                row.createCell(4).setCellValue(lead.getStatus());
                row.createCell(5).setCellValue(lead.getContact());
                row.createCell(6).setCellValue(lead.getNote());
                row.createCell(7).setCellValue(lead.getBroughtBy());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export leads to Excel", e);
        }
    }
}
