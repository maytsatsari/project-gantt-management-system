package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.List;
import app.ApplicationController;
import util.FileTypes;

public class FileLoaderTest {

    private ApplicationController appController;

    @Before
    public void setUp() {
        appController = new ApplicationController();
    }

    @Test
    public void testLoadHappyDayScenario() {
        // Happy Day: Φορτώνουμε ένα έγκυρο TSV αρχείο
        String validPath = "src/test/resources/input/EggsScrambled.tsv";
        List<String> result = appController.load(validPath, FileTypes.TSV);

        // 'Ελεγχος ότι η λίστα δεν είναι κενή
        assertNotNull("The result should not be null", result);
        assertFalse("The result should not be empty", result.isEmpty());
       
    }

    @Test
    public void testLoadFileDoesNotExist() {
        // Rainy Day: Το αρχείο δεν υπάρχει
        String invalidPath = "src/test/resources/input/NonExistentFile.tsv";
        List<String> result = appController.load(invalidPath, FileTypes.TSV);

        // 'Ελεγχος ότι το αποτέλεσμα είναι άδειο
        assertNotNull("The result should not be null", result);
        assertTrue("The result should be empty for non-existent file", result.isEmpty());
    }

    @Test
    public void testLoadUnsupportedFileType() {
        try {
            appController.load("src/test/resources/input/Scrambled.tsv", null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception message: " + e.getMessage());
            assertEquals("Μη υποστηριζόμενος τύπος αρχείου", e.getMessage());
        }
    }


    @Test
    public void testLoadInvalidFileContent() {
        // Rainy Day: Αρχείο με μη έγκυρα δεδομένα
        String invalidContentPath = "src/test/resources/input/InvalidFile.tsv";
        List<String> result = appController.load(invalidContentPath, FileTypes.TSV);

        // 'Ελεγχος ότι το αποτέλεσμα είναι άδειο
        assertNotNull("The result should not be null", result);
        assertTrue("The result should be empty for invalid file content", result.isEmpty());
    }
}
