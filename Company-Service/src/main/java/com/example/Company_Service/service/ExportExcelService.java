package com.example.Company_Service.service;

import com.example.Company_Service.entity.Company;
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

    private static final String[] HEADERS = {
            "ID", "YC ID", "Name", "Description", "Homepage", "Domain",
            "Team Size", "Tags", "Locations", "Hiring Flag", "Top Flag"
    };

    public ByteArrayInputStream exportCompaniesToExcel(List<Company> companies) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Companies");

            createHeaderRow(workbook, sheet);

            int rowIdx = 1;
            for (Company company : companies) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullSafeToString(company.getId()));
                row.createCell(1).setCellValue(nullSafeToString(company.getYcId()));
                row.createCell(2).setCellValue(nullSafeToString(company.getName()));
                row.createCell(3).setCellValue(nullSafeToString(company.getDescription()));
                row.createCell(4).setCellValue(nullSafeToString(company.getHomepage()));
                row.createCell(5).setCellValue(nullSafeToString(company.getDomain()));
                row.createCell(6).setCellValue(company.getTeamSize() != null ? company.getTeamSize() : 0);
                row.createCell(7).setCellValue(nullSafeToString(company.getTags()));
                row.createCell(8).setCellValue(nullSafeToString(company.getLocations()));
                row.createCell(9).setCellValue(company.isHiringFlag());
                row.createCell(10).setCellValue(company.isTopFlag());
            }

            autoSizeColumns(sheet);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export companies to Excel", e);
        }
    }

    public void writeCompaniesToFile(List<Company> companies, String filePath) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Companies");

            createHeaderRow(workbook, sheet);

            int rowIdx = 1;
            for (Company company : companies) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullSafeToString(company.getId()));
                row.createCell(1).setCellValue(nullSafeToString(company.getYcId()));
                row.createCell(2).setCellValue(nullSafeToString(company.getName()));
                row.createCell(3).setCellValue(nullSafeToString(company.getDescription()));
                row.createCell(4).setCellValue(nullSafeToString(company.getHomepage()));
                row.createCell(5).setCellValue(nullSafeToString(company.getDomain()));
                row.createCell(6).setCellValue(company.getTeamSize() != null ? company.getTeamSize() : 0);
                row.createCell(7).setCellValue(nullSafeToString(company.getTags()));
                row.createCell(8).setCellValue(nullSafeToString(company.getLocations()));
                row.createCell(9).setCellValue(company.isHiringFlag());
                row.createCell(10).setCellValue(company.isTopFlag());
            }

            autoSizeColumns(sheet);

            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write companies to file", e);
        }
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row header = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(style);
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String nullSafeToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
