package com.example.Crawler_Service1.service;

import com.example.Crawler_Service1.entity.Jobs;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportExcelService {

    public ByteArrayInputStream exportJobsToExcel(List<Jobs> jobs) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Jobs");

            // Add headers including Description
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Position", "Company", "Description", "Location", "Tags", "Date", "Active"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Add job data
            int rowIdx = 1;
            for (Jobs job : jobs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullSafeToString(job.getId()));
                row.createCell(1).setCellValue(nullSafeToString(job.getPosition()));
                row.createCell(2).setCellValue(nullSafeToString(job.getCompany()));
                row.createCell(3).setCellValue(nullSafeToString(job.getDescription())); // Added description
                row.createCell(4).setCellValue(nullSafeToString(job.getLocation()));
                row.createCell(5).setCellValue(nullSafeToString(job.getTags()));
                row.createCell(6).setCellValue(nullSafeToString(job.getDate()));
                row.createCell(7).setCellValue(job.isActive());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export jobs to Excel", e);
        }
    }

    public void writeJobsToFile(List<Jobs> jobs, String filePath) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Jobs");

            // Add headers including Description
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Position", "Company", "Description", "Location", "Tags", "Date", "Active"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Add job data
            int rowIdx = 1;
            for (Jobs job : jobs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullSafeToString(job.getId()));
                row.createCell(1).setCellValue(nullSafeToString(job.getPosition()));
                row.createCell(2).setCellValue(nullSafeToString(job.getCompany()));
                row.createCell(3).setCellValue(nullSafeToString(job.getDescription()));
                row.createCell(4).setCellValue(nullSafeToString(job.getLocation()));
                row.createCell(5).setCellValue(nullSafeToString(job.getTags()));
                row.createCell(6).setCellValue(nullSafeToString(job.getDate()));
                row.createCell(7).setCellValue(job.isActive());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write jobs to file", e);
        }
    }

    private String nullSafeToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
