package service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import dom.gantt.Task;
import dom.gantt.TaskAbstract;
import util.FileTypes;
import util.ProjectInfo;
import java.util.stream.Collectors;
import java.util.Comparator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.poi.xssf.usermodel.XSSFColor;



public class MainController implements IMainController {

    private List<TaskAbstract> abstractTasksList = new ArrayList<>(); // Αποθηκεύει όλες τις εργασίες
    private String inputPath;
    protected Map<String, CellStyle> styles = new HashMap<>(); // Δημιουργία και αρχικοποίηση του χάρτη στυλ private Workbook workbook = new XSSFWorkbook(); // Υποθέτουμε ότι υπάρχει ένα Workbook ήδη δημιουργημένο
    private List<String> parsedLines; // Αποθηκεύει τις γραμμές που διαβάστηκαν από το αρχείο
    private Workbook workbook;
    

    public List<TaskAbstract> topLevelTasks = new ArrayList<>();       // Κορυφαίες εργασίες (mamaId == 0)
    @Override
    public List<String> load(String inputPath, FileTypes fileType) {
        this.inputPath = inputPath;
        parsedLines = new ArrayList<>();
        abstractTasksList.clear(); 
        topLevelTasks.clear();
       
        if (fileType == null) {
            throw new IllegalArgumentException("Μη υποστηριζόμενος τύπος αρχείου");
        }

        try {
            switch (fileType) {
                case XLS:
                case XLSX:
                    parseExcelFile(inputPath, fileType);
                    break;
                case CSV:
                case TSV:
                case CSV_EU:
                    parseDelimitedTextFile(inputPath, fileType);
                    break;
                default:
                    throw new IllegalArgumentException("Μη υποστηριζόμενος τύπος αρχείου");

            }

            // Κλήση της μεθόδου για εύρεση κορυφαίων εργασιών
            getTopLevelTasksOnly();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        

        return parsedLines;
    }

    public void parseExcelFile(String path, FileTypes type) throws IOException {
        try (FileInputStream fileInput = new FileInputStream(path)) {
            Workbook workbook = type == FileTypes.XLS ? 
                new HSSFWorkbook(fileInput) : 
                new XSSFWorkbook(fileInput); // Άνοιγμα Excel αρχείου 

            Sheet firstSheet = workbook.getSheetAt(0); // Εργασία με το πρώτο φύλλο
            int Counterline = 0;

            for (Row row : firstSheet) {
            	Counterline++;
                StringBuilder lineData = new StringBuilder();

                try {
                    // Αν η γραμμή είναι κενή, παράβλεψέ την
                    if (row == null || row.getCell(0) == null) {
                        System.err.println("Παράλειψη κενής γραμμής #" + Counterline);
                        continue;
                    }

                    // Ανάγνωση δεδομένων από τις στήλες
                    Cell taskIdCell = row.getCell(0);
                    Cell taskTextCell = row.getCell(1);
                    Cell mamaIdCell = row.getCell(2);
                    Cell taskStartCell = row.getCell(3); // Ανάγνωση της στήλης taskStart
                    Cell taskEndCell = row.getCell(4);   // Ανάγνωση της στήλης taskEnd
                    Cell costCell = row.getCell(5);      // Ανάγνωση της στήλης cost
                    Cell effortCell = row.getCell(6);    // Ανάγνωση της στήλης effort
                    Cell isSimpleCell = row.getCell(7);  // Ανάγνωση της στήλης isSimple

                    if (taskIdCell == null || mamaIdCell == null || taskTextCell == null) {
                        System.err.println("Παράλειψη γραμμής #" + Counterline + ": Ανεπαρκή δεδομένα");
                        continue;
                    }

                    int taskId = (int) taskIdCell.getNumericCellValue();  // Task ID
                    int mamaId = (int) mamaIdCell.getNumericCellValue();  // Mama ID
                    String taskText = taskTextCell.getStringCellValue().trim(); // Περιγραφή

                    // Διαβάζοντας τις υπόλοιπες στήλες
                    int taskStart = (taskStartCell != null) ? (int) taskStartCell.getNumericCellValue() : 0;
                    int taskEnd = (taskEndCell != null) ? (int) taskEndCell.getNumericCellValue() : 0;
                    int cost = (costCell != null) ? (int) costCell.getNumericCellValue() : 0;
                    int effort = (effortCell != null) ? (int) effortCell.getNumericCellValue() : 0;
                    boolean isSimple = (isSimpleCell != null) && isSimpleCell.getBooleanCellValue();

                    // Αν το mamaId είναι 0, θέσε το isSimple σε true
                    if (mamaId == 0) {
                        isSimple = true;
                    }

                    // Δημιουργία Task αντικειμένου με όλες τις παραμέτρους
                    Task task = new Task(taskId, mamaId, taskText, taskStart, taskEnd, cost, effort, isSimple);
                    abstractTasksList.add(task);

                    // Εκτύπωση τιμών κάθε στήλης για τη συγκεκριμένη γραμμή
                    System.out.println("Γραμμή #" + Counterline + ":");
                    System.out.println("taskId = " + taskId + "\t" + 
                                       "taskText = " + taskText + "\t" + 
                                       "mamaId = " + mamaId + "\t" + 
                                       "taskStart = " + taskStart + "\t" +
                                       "taskEnd = " + taskEnd + "\t" +
                                       "cost = " + cost + "\t" +
                                       "effort = " + effort + "\t" +
                                       "isSimple = " + isSimple);

                    // Καταγραφή γραμμής στο parsedLines
                    lineData.append(taskId).append("\t")
                               .append(taskText).append("\t")
                               .append(mamaId).append("\t")
                               .append(taskStart).append("\t")
                               .append(taskEnd).append("\t")
                               .append(cost).append("\t")
                               .append(effort).append("\t")
                               .append(isSimple);
                    parsedLines.add(lineData.toString());

                } catch (Exception e) {
                    System.err.println("Σφάλμα στη γραμμή #" + Counterline + ": " + e.getMessage());
                }
            }

            // Τύπωσε τον συνολικό αριθμό γραμμών
            System.out.println("Συνολικές γραμμές που διαβάστηκαν από Excel: " + parsedLines.size());

            workbook.close(); // Κλείσιμο του workbook
        }

        // Καλούμε τη μέθοδο ταξινόμησης μετά την ανάγνωση του Excel
        sortTasks();
    }
   

    // Μέθοδος ταξινόμησης
    private void sortTasks() {
        // Λίστα για τις top-level εργασίες
    	List<TaskAbstract> topLevelTasks = abstractTasksList.stream()
    		    .filter(task -> task.getMamaId() == 0) // Επιλογή μόνο top-level εργασιών
    		    .sorted(Comparator.comparingInt(TaskAbstract::getTaskStart)
    		            .thenComparingInt(TaskAbstract::getTaskId)) // Ταξινόμηση με βάση start και taskId
    		    .collect(Collectors.toList());


        // Δημιουργία της τελικής ταξινομημένης λίστας
        List<TaskAbstract> sortedTasks = new ArrayList<>();
        
        for (TaskAbstract topTask : topLevelTasks) {
            // Προσθήκη της top-level εργασίας
            sortedTasks.add(topTask);

            // Βρίσκουμε όλες τις child εργασίες για την top-level εργασία
            List<TaskAbstract> childTasks = abstractTasksList.stream()
                .filter(task -> task.getMamaId() == topTask.getTaskId()) // Υποεργασίες της συγκεκριμένης top-level
                .sorted(Comparator.comparingInt(TaskAbstract::getTaskStart)
                        .thenComparingInt(TaskAbstract::getTaskId)) // Ταξινόμηση με βάση start και taskId
                .collect(Collectors.toList());

            // Προσθήκη των child εργασιών στην τελική λίστα
            sortedTasks.addAll(childTasks);
        }

        // Ενημέρωση της abstractTasksList με την ταξινομημένη λίστα
        abstractTasksList = sortedTasks;

        // Τύπωσε τις ταξινομημένες εργασίες για επιβεβαίωση
        System.out.println("Ταξινομημένες εργασίες:");
        abstractTasksList.forEach(task -> System.out.println(task));
    }


 
    private void parseDelimitedTextFile(String path, FileTypes type) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int Counterline = 0;
            int totalTasks = 0;

            // Διαβάζουμε κάθε γραμμή του αρχείου
            while ((line = reader.readLine()) != null) {
            	Counterline++;

                // Παράλειψη κενών γραμμών
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Διαχωρισμός της γραμμής χρησιμοποιώντας το tab ως διαχωριστικό
                String[] entries = line.split("\t");

                // Έλεγχος αν τα πεδία είναι λιγότερα από 7 και προσθήκη των απαιτούμενων τιμών για την επεξεργασία
                if (entries.length < 7) {
                	entries = Arrays.copyOf(entries, 7);

                    // Αν το mamaId είναι 0, προσθέτουμε default τιμές στα υπόλοιπα πεδία
                    if (entries[2].trim().equals("0")) {
                    	entries[3] = "0";   // taskStart
                    	entries[4] = "0";   // taskEnd
                    	entries[5] = "0";   // cost
                    	entries[6] = "0";   // effort
                    }
                }

                // Καθαρισμός των πεδίων από περιττά κενά
                entries = Arrays.stream(entries).map(String::trim).toArray(String[]::new);

                try {
                    // Ανάγνωση πεδίων από τη γραμμή και μετατροπή τους σε τύπους δεδομένων
                    int taskId = Integer.parseInt(entries[0].trim());
                    String taskText = entries[1].trim();
                    int mamaId = Integer.parseInt(entries[2].trim());

                    // Ανάγνωση των υπολοίπων πεδίων, με έλεγχο κενών
                    int taskStart = Integer.parseInt(entries[3].trim());
                    int taskEnd = Integer.parseInt(entries[4].trim());
                    int cost = Integer.parseInt(entries[5].trim());
                    int effort = Integer.parseInt(entries[6].trim());

                    // Διαχείριση γραμμής με mamaId == 0
                    boolean isSimple = (mamaId == 0);

                    // Δημιουργία Task αντικειμένου με όλες τις παραμέτρους
                    Task task = new Task(taskId, mamaId, taskText, taskStart, taskEnd, cost, effort, isSimple);
                    abstractTasksList.add(task);

                    // Αύξηση του μετρητή για κάθε προσθήκη εργασίας
                    totalTasks++;
                    
                    // Προσθήκη της γραμμής στην parsedLines
                    parsedLines.add(line);  // Προσθήκη της γραμμής στην parsedLines

                } catch (NumberFormatException e) {
                    System.err.println("Σφάλμα στη γραμμή #" + Counterline + ": " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Σφάλμα στην επεξεργασία γραμμής #" + Counterline + ": " + e.getMessage());
                }
            }

            // Ενημέρωση του συνολικού αριθμού εργασιών
            System.out.println("Συνολικές γραμμές που διαβάστηκαν από το αρχείο: " + totalTasks);

            if (abstractTasksList.isEmpty()) {
                System.err.println("Η AbstractTasksList είναι κενή.");
            }

        } catch (IOException e) {
            System.err.println("Σφάλμα κατά το άνοιγμα ή διάβασμα του αρχείου: " + e.getMessage());
        }       
        sortTasks();
        printSortedTasks();
        
    }

  

    // Μέθοδος εκτύπωσης των ταξινομημένων εργασιών
    private void printSortedTasks() {
        if (abstractTasksList.isEmpty()) {
            System.out.println("Δεν υπάρχουν εργασίες για εκτύπωση.");
        } else {
            System.out.println("Ταξινομημένες εργασίες:");
            abstractTasksList.forEach(task -> {
                System.out.println("taskId = " + task.getTaskId() + "\t" +
                                   "taskText = " + task.getTaskText() + "\t" +
                                   "mamaId = " + task.getMamaId() + "\t" +
                                   "taskStart = " + task.getTaskStart() + "\t" +
                                   "taskEnd = " + task.getTaskEnd() + "\t" +
                                   "cost = " + task.getCost() + "\t" +
                                   "effort = " + task.getEffort());
            });
        }
        
        System.out.println("\n");      
    }

 
    @Override
    public ProjectInfo prepareTargetWorkbook(FileTypes fileType, String targetPath) {
        ProjectInfo projectInfo = new ProjectInfo();

        try {
            Workbook workbook;
            if (fileType == FileTypes.XLSX) {
                workbook = new XSSFWorkbook();
            } else if (fileType == FileTypes.XLS) {
                workbook = new HSSFWorkbook();
            } else {
                throw new IllegalArgumentException("Μη υποστηριζόμενος τύπος αρχείου");
            }

            // Αποθήκευση του workbook στο target path
            FileOutputStream fileOut = new FileOutputStream(targetPath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            projectInfo.setTargetPath(targetPath);
            projectInfo.setFileType(fileType);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ProjectInfo("2024-2025_PoiGantt", inputPath, targetPath, parsedLines.size(), topLevelTasks.size());
    }

    @Override
    public List<TaskAbstract> getAllTasks() {
        // Επιστρέφει τη λίστα των TaskAbstract
        return new ArrayList<>(abstractTasksList);
    }




    @Override
    public List<TaskAbstract> getTopLevelTasksOnly() {
        // Έλεγχος αν η λίστα abstractTasksList έχει δεδομένα
        if (abstractTasksList == null || abstractTasksList.isEmpty()) {
            System.out.println("abstractTasksList is empty or null. No tasks to process.");
            return new ArrayList<>(); // Επιστρέφουμε κενή λίστα
        }
        topLevelTasks.clear();
       // Μεταβλητή για καταμέτρηση των top-level εργασιών
        int topLevelTaskCount = 0;

        // Επανάληψη σε όλες τις εργασίες της abstractTasksList
        for (TaskAbstract task : abstractTasksList) {
            // Έλεγχος αν η εργασία είναι top-level (δηλαδή ContainerTaskId == 0)
            if (task.getContainerTaskId() == 0) {
                topLevelTasks.add(task);
                topLevelTaskCount++;
            }
        }

        // Εμφάνιση αποτελεσμάτων στην κονσόλα για έλεγχο
        System.out.println("Found " + topLevelTaskCount + " top-level tasks.");
        for (TaskAbstract topTask : topLevelTasks) {
            System.out.println("Top-Level Task ID: " + topTask.getTaskId() + ", Description: " + topTask.getTaskText());
        }
        return topLevelTasks;
    
    }


    public List<TaskAbstract> getTasksInRange(int firstIncluded, int lastIncluded) {
        try {
            // Δημιουργία μιας νέας λίστας για να αποθηκεύσουμε τις εργασίες που πληρούν το κριτήριο
            List<TaskAbstract> tasksInRange = new ArrayList<>();

            if (abstractTasksList != null) {
                // Φιλτράρισμα των εργασιών ώστε να μείνουν μόνο εκείνες που έχουν TaskId εντός του εύρους [firstIncluded, lastIncluded]
                for (TaskAbstract task : abstractTasksList) {
                    // Έλεγχος αν το TaskId είναι εντός του καθορισμένου εύρους
                    if (task.getTaskId() >= firstIncluded && task.getTaskId() <= lastIncluded) {
                        tasksInRange.add(task);
                    }
                }
            }

            // Τύπωσε τις εργασίες που βρίσκονται εντός του εύρους
            if (!tasksInRange.isEmpty()) {
                System.out.println("Tasks in range [" + firstIncluded + ", " + lastIncluded + "]:");
                for (TaskAbstract task : tasksInRange) {
                    System.out.println(task); 
                }
            } else {
                System.out.println("No tasks found in range [" + firstIncluded + ", " + lastIncluded + "].");
            }

            // Επιστροφή της λίστας με τις φιλτραρισμένες εργασίες
            return tasksInRange.isEmpty() ? null : tasksInRange; // Επιστροφή null αν η λίστα είναι κενή
        } catch (Exception e) {
            // Σε περίπτωση σφάλματος, εκτυπώνουμε το σφάλμα και επιστρέφουμε null
            e.printStackTrace();
            return null;
        }
    }

    public boolean rawWriteToExcelFile(List<TaskAbstract> tasks) {
        // Παίρνουμε τις top-level εργασίες
        List<TaskAbstract> topLevelTasks = getTopLevelTasksOnly();

        // Δημιουργία της ημερομηνίας για το όνομα της καρτέλας
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH_mm_ss");
        String sheetName = "Initial-" + sdf.format(new Date());

        try {
            // Έλεγχος αν υπάρχει ήδη workbook
            if (this.workbook == null) {
                this.workbook = new XSSFWorkbook();
            }

            Sheet sheet = workbook.createSheet(sheetName);

            // Δημιουργία της πρώτης γραμμής για τίτλους στηλών
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Level");
            headerRow.createCell(1).setCellValue("TaskId");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Cost");
            headerRow.createCell(4).setCellValue("Effort");

            // Εύρεση του μέγιστου χρόνου
            int maxTime = findMaxTime(tasks);
            for (int i = 5; i <= maxTime + 4; i++) {
                headerRow.createCell(i).setCellValue(String.valueOf(i - 4));
                sheet.setColumnWidth(i, 2000);
            }

            // Γράφουμε τα δεδομένα
            int rowIndex = 1;

            // Επεξεργασία Top-Level Εργασιών
            for (TaskAbstract topTask : topLevelTasks) {
                // Υπολογισμός εύρους (start και end) της top-level εργασίας
                int topStart = Integer.MAX_VALUE;
                int topEnd = Integer.MIN_VALUE;

                // Αρχικοποίηση συνολικών κόστους και προσπάθειας
                double totalCost = 0.0;
                double totalEffort = 0.0;

                // Εύρεση υποεργασιών
                for (TaskAbstract subTask : tasks) {
                    if (subTask.getContainerTaskId() == topTask.getTaskId()) {
                        topStart = Math.min(topStart, subTask.getTaskStart());
                        topEnd = Math.max(topEnd, subTask.getTaskEnd());

                        // Προσθήκη του κόστους και της προσπάθειας της υποεργασίας
                        totalCost += subTask.getCost();
                        totalEffort += subTask.getEffort();
                    }
                }

                // Εγγραφή της top-level εργασίας στο Excel
                Row topRow = sheet.createRow(rowIndex++);
                topRow.createCell(0).setCellValue("TOP");
                topRow.createCell(1).setCellValue(topTask.getTaskId());
                topRow.createCell(2).setCellValue(topTask.getTaskText());
                topRow.createCell(3).setCellValue(totalCost); // Συνολικό Κόστος
                topRow.createCell(4).setCellValue(totalEffort); // Συνολική Προσπάθεια

                // Συμπλήρωση του εύρους της top-level
                for (int i = 5; i <= maxTime + 4; i++) {
                    if (i - 4 >= topStart && i - 4 <= topEnd) {
                        topRow.createCell(i).setCellValue("x");
                    } else {
                        topRow.createCell(i).setCellValue("");
                    }
                }

                // Εγγραφή των υποεργασιών
                for (TaskAbstract subTask : tasks) {
                    if (subTask.getContainerTaskId() == topTask.getTaskId()) {
                        Row subRow = sheet.createRow(rowIndex++);
                        subRow.createCell(0).setCellValue("");
                        subRow.createCell(1).setCellValue(subTask.getTaskId());
                        subRow.createCell(2).setCellValue(subTask.getTaskText());
                        subRow.createCell(3).setCellValue(subTask.getCost());
                        subRow.createCell(4).setCellValue(subTask.getEffort());

                        // Συμπλήρωση του εύρους των υποεργασιών
                        for (int i = 5; i <= maxTime + 4; i++) {
                            if (i - 4 >= subTask.getTaskStart() && i - 4 <= subTask.getTaskEnd()) {
                                subRow.createCell(i).setCellValue("x");
                            } else {
                                subRow.createCell(i).setCellValue("");
                            }
                        }
                    }
                }
            }

            // Αποθήκευση του workbook στο αρχείο
            try (FileOutputStream fos = new FileOutputStream("./src/test/resources/output/Table_Output.xlsx")) {
            	//try (FileOutputStream fos = new FileOutputStream("./src/test/resources/input/ShopOutput.xlsx")){
                //try (FileOutputStream fos = new FileOutputStream(".src/test/resources/input/EggsScrambled_Output.xlsx")){
                workbook.write(fos);
                System.out.println("Workbook saved successfully with the new sheet: " + sheetName);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Map<String, CellStyle> createStyles(Workbook workbook) {
        //Map<String, CellStyle> styles = new HashMap<>();

        // Δημιουργία στυλ για την κεφαλίδα
        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
        CellStyle headerStyle = xssfWorkbook.createCellStyle();
        Font headerFont = xssfWorkbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex()); 
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Δημιουργία μωβ χρώματος με χρήση XSSFColor
        XSSFColor purpleColor = new XSSFColor(new java.awt.Color(200, 160, 200), null); // Απαλό λιλά
        headerStyle.setFillForegroundColor(purpleColor);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.put("DefaultHeaderStyle", headerStyle);
        

        // Δημιουργία στυλ για τις top-level μπάρες
        CellStyle topTaskBarStyle = workbook.createCellStyle();
        topTaskBarStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        topTaskBarStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.put("TopTask_bar_style", topTaskBarStyle);
  

        // Δημιουργία στυλ για τις non-top-level μπάρες
        CellStyle nonTopTaskBarStyle = workbook.createCellStyle();
        nonTopTaskBarStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        nonTopTaskBarStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.put("NonTopTask_bar_style", nonTopTaskBarStyle);

   
        // Δημιουργία στυλ για top-level δεδομένα
        CellStyle topDataStyle = workbook.createCellStyle();
        Font topDataFont = workbook.createFont();
        topDataFont.setBold(true);
        topDataFont.setColor(IndexedColors.BLUE.getIndex());
        topDataFont.setFontHeightInPoints((short) 14);
        topDataStyle.setFont(topDataFont);
        styles.put("TopTask_data_style", topDataStyle);

       
        // Δημιουργία στυλ για non-top-level δεδομένα
        CellStyle nonTopDataStyle = workbook.createCellStyle();
        Font nonTopDataFont = workbook.createFont();
        nonTopDataFont.setFontHeightInPoints((short) 12);
        nonTopDataFont.setColor(IndexedColors.BLUE.getIndex());
        nonTopDataStyle.setFont(nonTopDataFont);
        styles.put("NonTopTask_data_style", nonTopDataStyle);
        
        
        // Δημιουργία του Normal στυλ
        CellStyle normalStyle = workbook.createCellStyle();
        Font normalFont = workbook.createFont();
        normalFont.setFontName("Arial");
        normalFont.setFontHeightInPoints((short) 12);
        normalFont.setColor(IndexedColors.BLACK.getIndex()); // Μαύρο χρώμα γραμμάτων
        normalStyle.setFont(normalFont);
        nonTopDataStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        nonTopDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        topDataStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        topDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);   
        topTaskBarStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        topTaskBarStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex()); 
        nonTopTaskBarStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.put("Normal", normalStyle);
          
        return styles;
    }

    
    private int findMaxTime(List<TaskAbstract> tasks) {
        int maxTime = 0;
        for (TaskAbstract task : tasks) {
            if (task.getTaskEnd() > maxTime) {
                maxTime = task.getTaskEnd();
            }
        }
        return maxTime;
    }
    
    public String addFontedStyle(String styleName, short styleFontColor, short styleFontHeightInPoints, 
            String styleFontName, boolean styleFontBold, boolean styleFontItalic, 
            boolean styleFontStrikeout, short styleFillForegroundColor, 
            String styleFillPatternString, String HorizontalAlignmentString, 
            boolean styleWrapText) {
			try {
			// Δημιουργία του στυλ
				CellStyle style = workbook.createCellStyle();
				Font font = workbook.createFont();
				
				// Ρύθμιση γραμματοσειράς
				font.setColor(styleFontColor);
				font.setFontHeightInPoints(styleFontHeightInPoints);
				font.setFontName(styleFontName);
				font.setBold(styleFontBold);
				font.setItalic(styleFontItalic);
				font.setStrikeout(styleFontStrikeout);
				style.setFont(font);
				
				// Ρύθμιση χρώματος φόντου
				style.setFillForegroundColor(styleFillForegroundColor);
				if ("SOLID_FOREGROUND".equalsIgnoreCase(styleFillPatternString)) {
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				} else {
				style.setFillPattern(FillPatternType.NO_FILL);
				}
				
				// Ρύθμιση ευθυγράμμισης
				if ("CENTER".equalsIgnoreCase(HorizontalAlignmentString)) {
				style.setAlignment(HorizontalAlignment.CENTER);
				} else if ("LEFT".equalsIgnoreCase(HorizontalAlignmentString)) {
				style.setAlignment(HorizontalAlignment.LEFT);
				} else if ("RIGHT".equalsIgnoreCase(HorizontalAlignmentString)) {
				style.setAlignment(HorizontalAlignment.RIGHT);
				} else {
				style.setAlignment(HorizontalAlignment.GENERAL);
				}
			
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setWrapText(styleWrapText);
			
				// Αποθήκευση του στυλ
				styles.put(styleName, style);
				System.out.println(styles);  // Εκτυπώνει όλα τα στυλ που υπάρχουν στον χάρτη.

				return styleName;
			
				} catch (Exception e) {
					e.printStackTrace();
					return "Normal"; // Επιστροφή προεπιλογής σε περίπτωση σφάλματος
				}
				}


    
    public boolean createNewSheet(String sheetName, List<TaskAbstract> tasks, String headerStyleName, 
    		String topBarStyleName, String topDataStyleName, String nonTopBarStyleName, 
            String nonTopDataStyleName, String normalStyleName) {
        try {
            if (this.workbook == null) {
                this.workbook = new XSSFWorkbook();
            }
            createStyles(workbook);  // Βεβαιωθείτε ότι καλείτε πρώτα την createStyles
            ProjectInfo projectInfo = new ProjectInfo();
            Sheet sheet = workbook.createSheet(sheetName);

            // Δημιουργία στυλ
            //Map<String, CellStyle> styles = createStyles(workbook);

            // Δημιουργία κεφαλίδας για το φύλλο
            Row headerRow = sheet.createRow(0); // Δημιουργία της πρώτης γραμμής για την κεφαλίδα

         // Δημιουργία κεφαλίδας
            createHeaderCell(headerRow, 0, "Level", headerStyleName, projectInfo, styles);
            createHeaderCell(headerRow, 1, "Task ID", headerStyleName, projectInfo, styles);
            createHeaderCell(headerRow, 2, "Description", headerStyleName, projectInfo, styles);
            createHeaderCell(headerRow, 3, "Cost", headerStyleName, projectInfo, styles);
            createHeaderCell(headerRow, 4, "Effort", headerStyleName, projectInfo, styles);

            
         // Υπολογισμός του μέγιστου αριθμού στηλών για το "Range"
            if (sheetName.equalsIgnoreCase("Range")) {
                int maxTime = findMaxTime(tasks); // Βρίσκουμε το μέγιστο χρόνο από τις υπο-εργασίες
                int maxColumns = maxTime + 4; // Υπολογισμός της μέγιστης στήλης (τέλος της μπάρας)
                System.out.println("Calculated maxColumns for Range: " + maxColumns);

                // Δημιουργία αριθμών κεφαλίδας από τη 6η στήλη και μετά
                for (int i = 5; i <= maxColumns; i++) {
                    // Δημιουργία κελιού κεφαλίδας με το σωστό περιεχόμενο και στυλ
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(String.valueOf(i - 4)); // Εκτύπωση αριθμού στήλης
                    cell.setCellStyle(styles.get(headerStyleName)); // Εφαρμογή του στυλ κεφαλίδας

                    // Ρύθμιση του πλάτους της στήλης για καλύτερη εμφάνιση
                    sheet.setColumnWidth(i, 2000); // Προσαρμογή του πλάτους ανάλογα με την ανάγκη
                    //System.out.println("Created header cell for column: " + i);
                }
            }
            
            
         // Υπολογισμός του μέγιστου χρόνου από όλες τις υπο-εργασίες
            int maxTime = 0;
            System.out.println("Checking sub-tasks for " + sheetName + "...");
            for (TaskAbstract task : tasks) {
                List<Task> subTasks = getSubTasks(task.getTaskId(), getAllTasks());
                for (Task subTask : subTasks) {
                    // Εκτύπωση υπο-εργασιών για έλεγχο
                    System.out.println("SubTask ID: " + subTask.getTaskId() + " | Start: " + subTask.getTaskStart() + " | End: " + subTask.getTaskEnd());
                    maxTime = Math.max(maxTime, subTask.getTaskEnd()); // Υπολογισμός του μέγιστου χρόνου
                }
            }
            System.out.println("Calculated maxTime for " + sheetName + ": " + maxTime);

            // Υπολογισμός των στηλών που θα χρησιμοποιηθούν (maxColumns)
            int maxColumns = maxTime + 4; // Προσθήκη 4 για τις πρώτες στήλες (Level, Task ID, Description, Cost)
            System.out.println("Calculated maxColumns for " + sheetName + ": " + maxColumns);

            // Δημιουργία αριθμών κεφαλίδας από τη 6η στήλη και μετά
            for (int i = 5; i <= maxColumns; i++) {
                // Δημιουργία κελιού κεφαλίδας με το σωστό περιεχόμενο και στυλ
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(String.valueOf(i - 4)); // Εκτύπωση αριθμού στήλης
                cell.setCellStyle(styles.get(headerStyleName)); // Εφαρμογή του στυλ κεφαλίδας

                // Ρύθμιση του πλάτους της στήλης για καλύτερη εμφάνιση
                sheet.autoSizeColumn(i);
                //System.out.println("Created header cell for column: " + i);
            }


            // Εξασφάλιση ότι η κεφαλίδα είναι ορατή
            headerRow.setHeightInPoints(25); // Ρύθμιση του ύψους της γραμμής για καλύτερη εμφάνιση

            int rowIndex=1;
    
            headerRow.setHeightInPoints(25);
   

            List<TaskAbstract> allTasks = new ArrayList<>(abstractTasksList);

            for (TaskAbstract task : tasks) {
                Row taskRow = sheet.createRow(rowIndex++);
                if (task.isTopLevel()) {
                    double totalCost = 0;
                    double totalEffort = 0;
                    List<Task> subTasks = getSubTasks(task.getTaskId(), allTasks);

                    for (Task subTask : subTasks) {
                        totalCost += subTask.getCost();
                        
                        totalEffort += subTask.getEffort();
                    }

                    createDataCell(taskRow, 0, "TOP", topDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 1, String.valueOf(task.getTaskId()), topDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 2, task.getTaskText(), topDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 3, String.valueOf(totalCost), topDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 4, String.valueOf(totalEffort), topDataStyleName, projectInfo, styles);

                    int start = Integer.MAX_VALUE;
                    int end = Integer.MIN_VALUE;

                    for (Task subTask : subTasks) {
                        start = Math.min(start, subTask.getTaskStart());
                        end = Math.max(end, subTask.getTaskEnd());
                    }

                    for (int i = start; i <= end; i++) {
                        Cell barCell = taskRow.createCell(i + 4);
                        barCell.setCellStyle(styles.get(topBarStyleName));
                        barCell.setCellValue(" ");
                        sheet.setColumnWidth(i + 4, 2000);
                     // Ρύθμιση του ύψους της γραμμής για να γίνουν οι μπάρες πιο χοντρές
                        taskRow.setHeightInPoints(25);
                    }
                } else {
                    createDataCell(taskRow, 0, "", nonTopDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 1, String.valueOf(task.getTaskId()), nonTopDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 2, task.getTaskText(), nonTopDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 3, String.valueOf(task.getCost()), nonTopDataStyleName, projectInfo, styles);
                    createDataCell(taskRow, 4, String.valueOf(task.getEffort()), nonTopDataStyleName, projectInfo, styles);

                    for (int i = task.getTaskStart(); i <= task.getTaskEnd(); i++) {
                        Cell barCell = taskRow.createCell(i + 4);
                        barCell.setCellStyle(styles.get(nonTopBarStyleName));
                        barCell.setCellValue(" ");
                        sheet.setColumnWidth(i + 4, 2000);
                        
                    }
                }
            }

            for (int i = 0; i <= 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            if (sheetName.equalsIgnoreCase("Range")) {
                try (FileOutputStream fos = new FileOutputStream("./src/test/resources/output/Table_Output.xlsx")){
                //try (FileOutputStream fos = new FileOutputStream("./src/test/resources/input/ShopOutput.xlsx")){
                //try (FileOutputStream fos = new FileOutputStream(".src/test/resources/input/EggsScrambled_Output.xlsx")){
                    workbook.write(fos);
                    workbook.close();
                    workbook = null;
                    System.out.println("Workbook saved successfully as output_with_styles.xlsx!");
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // Δημιουργία κελιού κεφαλίδας
    private void createHeaderCell(Row row, int column, String value, String headerStyleName, ProjectInfo projectInfo, Map<String, CellStyle> styles) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(styles.get(headerStyleName)); // Ανάκτηση του στυλ από το Map
    }


    // Δημιουργία κελιού δεδομένων (με υποστήριξη για διάφορους τύπους δεδομένων)
    private void createDataCell(Row row, int column, Object value, String styleName, ProjectInfo projectInfo, Map<String, CellStyle> styles) {
        Cell cell = row.createCell(column);
        //System.out.println("Requesting style: " + styleName);
        if (!styles.containsKey(styleName)) {
            System.out.println("Style not found for: " + styleName);
        }

     // Έλεγχος αν το στυλ υπάρχει στο Map
        CellStyle style = styles.get(styleName);
        if (style == null) {
            System.out.println("Style not found for: " + styleName);
        } else {
            cell.setCellStyle(style); // Εφαρμογή του στυλ
        }

        cell.setCellValue(value.toString()); // Convert value to string for simplicity
        cell.setCellStyle(styles.get(styleName)); // Apply the style from the map
    }



    // Μέθοδος για να βρούμε τις υποεργασίες μιας top-level εργασίας
    private List<Task> getSubTasks(int parentTaskId, List<TaskAbstract> tasks) {
        return tasks.stream()
            .filter(task -> task instanceof Task)
            .map(task -> (Task) task)
            .filter(subTask -> subTask.getMamaId() == parentTaskId)
            .collect(Collectors.toList());
    }

}