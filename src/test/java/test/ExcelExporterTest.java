package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporterTest {

    private ExcelExporter excelExporter;
    private Workbook workbook;

    @Before
    public void setUp() {
        excelExporter = new ExcelExporter();
        workbook = new XSSFWorkbook(); 
    }

    @Test
    public void testExportExcelHappyDayScenario() {
        // Υπάρχει έτοιμο διάγραμμα
        Sheet sheet = workbook.createSheet("Tasks");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Task ID");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Cost");

        // Δεδομένα εργασιών
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(1);
        dataRow.createCell(1).setCellValue("Task Description");
        dataRow.createCell(2).setCellValue(100.0);

        //Εξαγωγή στο Excel
        String filePath = "src/test/resources/output/HappyDayOutput.xlsx";
        boolean result = excelExporter.export(workbook, filePath);

        assertTrue(result);

        //Το αρχείο υπάρχει στο path
        File file = new File(filePath);
        assertTrue(file.exists());

        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testExportExcelFileNotSaved() {
        // Υπάρχει έτοιμο διάγραμμα
        Sheet sheet = workbook.createSheet("Tasks");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Task ID");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Cost");

        //Mη προσβάσιμη διαδρομή
        String invalidFilePath = "src/test/resources/output/Invalid/NonExistentFolder/Output.xlsx";
        boolean result = excelExporter.export(workbook, invalidFilePath);
        assertFalse(result);
    }

    // Mock υλοποίηση του ExcelExporter
    static class ExcelExporter {
        public boolean export(Workbook workbook, String filePath) {
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                return true;
            } catch (IOException e) {
                System.err.println("Error exporting Excel: " + e.getMessage());
                return false;
            }
        }
    }
}
