package com.example.Onboard_Service.service;

import com.example.Onboard_Service.entity.Onboard;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExport {

    private static final String[] HEADERS = {
            "ID", "Name", "Email", "Phone No", "Location",
            "Workplace Type", "Employment Type", "Field",
            "Onboarded By", "Experience", "Company Name",
            "Skills", "Status"
    };

    public ByteArrayInputStream exportOnboardsToExcel(List<Onboard> onboards) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Onboards");

            // Create header style (bold + background)
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowIdx = 1;
            for (Onboard onboard : onboards) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(onboard.getId());
                row.createCell(1).setCellValue(onboard.getName());
                row.createCell(2).setCellValue(onboard.getEmail());
                row.createCell(3).setCellValue(onboard.getPhoneNo());
                row.createCell(4).setCellValue(onboard.getLocation());
                row.createCell(5).setCellValue(onboard.getWorkplaceType());
                row.createCell(6).setCellValue(onboard.getEmploymentType());
                row.createCell(7).setCellValue(onboard.getField());
                row.createCell(8).setCellValue(onboard.getOnboarded_By());
                row.createCell(9).setCellValue(onboard.getExperience());
                row.createCell(10).setCellValue(onboard.getCompany_name());
                row.createCell(11).setCellValue(onboard.getSkills());
                row.createCell(12).setCellValue(onboard.getStatus());
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export onboards to Excel", e);
        }
    }
}
