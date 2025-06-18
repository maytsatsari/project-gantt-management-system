package test;

import static org.junit.Assert.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorksheetStylerTest {

    private Workbook workbook;
    private WorksheetStyler worksheetStyler;

    @Before
    public void setUp() {
        workbook = new XSSFWorkbook(); // Mock Workbook
        worksheetStyler = new WorksheetStyler();
    }

    @Test
    public void testApplyCellStylesHappyDayScenario() {
        // Υπάρχει έτοιμο φύλλο εργασίας
        Sheet sheet = workbook.createSheet("StyledSheet");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Task ID");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Cost");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(1);
        dataRow.createCell(1).setCellValue("Task Description");
        dataRow.createCell(2).setCellValue(100.0);

        // Εφαρμογή στυλ
        boolean result = worksheetStyler.applyStyles(workbook);
        assertTrue(result);

        //Τα στυλ εφαρμόστηκαν στο header
        CellStyle headerStyle = sheet.getRow(0).getCell(0).getCellStyle();
        assertNotNull(headerStyle);
        assertEquals(IndexedColors.BLUE.getIndex(), headerStyle.getFillForegroundColor());

        Font font = workbook.getFontAt(headerStyle.getFontIndex());
        assertNotNull(font);
        assertTrue(font.getBold());
        assertEquals(IndexedColors.WHITE.getIndex(), font.getColor());

        try (FileOutputStream fos = new FileOutputStream("src/test/resources/output/StyledHappyDay.xlsx")) {
            workbook.write(fos);
        } catch (IOException e) {
            fail("Could not save the test file for manual verification.");
        }
    }

    @Test
    public void testApplyInvalidCellStyles() {
        // Υπάρχει έτοιμο φύλλο εργασίας
        Sheet sheet = workbook.createSheet("StyledSheet");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Task ID");

        // Εφαρμογή μη υποστηριζόμενων στυλ
        try {
            worksheetStyler.applyStylesWithInvalidStyle(workbook);
            fail("Expected IllegalArgumentException for unsupported style");
        } catch (IllegalArgumentException e) {
            // Αναμενόμενο σφάλμα
            assertTrue(e.getMessage().contains("Unsupported style"));
        }

        //Το workbook παραμένει αμετάβλητο
        CellStyle headerStyle = sheet.getRow(0).getCell(0).getCellStyle();
        CellStyle defaultStyle = workbook.createCellStyle(); // Δημιουργία προεπιλεγμένου στυλ για σύγκριση

        // Έλεγχος εάν το στυλ του κελιού είναι το προεπιλεγμένο
        assertEquals(defaultStyle.getFillForegroundColor(), headerStyle.getFillForegroundColor());
        assertEquals(defaultStyle.getFontIndex(), headerStyle.getFontIndex());
    }


    // Mock κλάση για τη μορφοποίηση του Excel
    static class WorksheetStyler {
        public boolean applyStyles(Workbook workbook) {
            try {
                Sheet sheet = workbook.getSheetAt(0);
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Row headerRow = sheet.getRow(0);
                for (Cell cell : headerRow) {
                    cell.setCellStyle(headerStyle);
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean applyStylesWithInvalidStyle(Workbook workbook) {
            throw new IllegalArgumentException("Unsupported style");
        }
    }
}
