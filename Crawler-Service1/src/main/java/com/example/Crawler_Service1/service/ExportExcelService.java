package com.example.Crawler_Service1.service;

import com.example.Crawler_Service1.entity.Jobs;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportExcelService {

    public ByteArrayInputStream exportJobsToExcel(List<Jobs> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            throw new IllegalArgumentException("No jobs found to export.");
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Jobs");

            String[] headers = {"ID", "Position", "Company", "Description", "Location", "Tags", "Date", "Active"};
            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add job data safely
            int rowIdx = 1;
            for (Jobs job : jobs) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(nullSafeToString(job.getId()));
                row.createCell(1).setCellValue(nullSafeToString(job.getPosition()));
                row.createCell(2).setCellValue(nullSafeToString(job.getCompany()));
                row.createCell(3).setCellValue(nullSafeToString(job.getDescription()));
                row.createCell(4).setCellValue(nullSafeToString(job.getLocation()));
                row.createCell(5).setCellValue(nullSafeToString(job.getTags()));

                // Handle LocalDate or String safely
                Object date = job.getDate();
                row.createCell(6).setCellValue(
                        (date instanceof java.time.LocalDate)
                                ? ((java.time.LocalDate) date).format(DateTimeFormatter.ISO_DATE)
                                : nullSafeToString(date)
                );

                row.createCell(7).setCellValue(job.isActive());
            }

            // Auto-size columns after data insertion
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
        if (jobs == null || jobs.isEmpty()) {
            throw new IllegalArgumentException("No jobs found to write.");
        }

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Jobs");
            String[] headers = {"ID", "Position", "Company", "Description", "Location", "Tags", "Date", "Active"};
            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

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
