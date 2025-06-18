package app;

import java.util.List;
import java.util.Objects;

import dom.gantt.TaskAbstract;
import service.IMainController;
import service.MainControllerFactory;
import util.FileTypes;
import util.ProjectInfo;

public class ApplicationController {
	private MainControllerFactory factory;
	private IMainController mainController;
	
	public ApplicationController() {
		this.factory = new MainControllerFactory ();
		this.mainController = factory.createMainController();
		if (Objects.isNull(this.mainController)){
			System.err.println("MainController : null controller, exiting.");
			System.exit(-1);
		}
	}
	
	public List<String> load(String fileName, FileTypes filetype) {
        List<String> result = null;
        try {
            result = this.mainController.load(fileName, filetype);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error loading file: " + fileName);
            e.printStackTrace();
        }
        return result;
    }

	public ProjectInfo prepareTargetWorkbook(FileTypes fileType, String targetPath) { //elenxos kata thn proetoimasia
		    ProjectInfo projectInfo = null;
		    try {
		        projectInfo = this.mainController.prepareTargetWorkbook(fileType, targetPath);
		    } catch (Exception e) {
		        System.err.println("Error at target path: " + targetPath);
		        e.printStackTrace();
		    }
		    return projectInfo;
		}

	


	public List<TaskAbstract> getAllTasks(){
		return this.mainController.getAllTasks();
	}

	public List<TaskAbstract> getTopLevelTasksOnly(){
		return this.mainController.getTopLevelTasksOnly();
	}


	public List<TaskAbstract> getTasksInRange(int firstIncluded, int lastIncluded){
		return this.mainController.getTasksInRange(firstIncluded, lastIncluded);
	}

	public String addFontedStyle(String styleName, short styleFontColor, short styleFontHeightInPoints, String styleFontName,
			boolean styleFontBold, boolean styleFontItalic, boolean styleFontStrikeout, short styleFillForegroundColor,
			String styleFillPatternString, String HorizontalAlignmentString, boolean styleWrapText) {
		return this.mainController.addFontedStyle(styleName, 
				styleFontColor, styleFontHeightInPoints, styleFontName, 
				styleFontBold, styleFontItalic, styleFontStrikeout, 
				styleFillForegroundColor, styleFillPatternString, HorizontalAlignmentString, styleWrapText);
	}

	public boolean createNewSheet(String sheetName, List<TaskAbstract> tasks, String headerStyleName, String topBarStyleName,
			String topDataStyleName, String nonTopBarStyleName, String nonTopDataStyleName, String normalStyleName) {
	    boolean isSuccess = false;
	    try {
	        isSuccess = this.mainController.createNewSheet(sheetName, tasks, 
	                headerStyleName, topBarStyleName, topDataStyleName, nonTopBarStyleName, nonTopDataStyleName, normalStyleName);
	    } catch (Exception e) {
	        System.err.println("Error creating new sheet: " + sheetName);
	        e.printStackTrace();
	    }
	    return isSuccess;
	}

	public boolean rawWriteToExcelFile(List<TaskAbstract> tasks) {
		boolean isSuccess = false;
	    try {
	        isSuccess = this.mainController.rawWriteToExcelFile(tasks);
	    } catch (Exception e) {
	        System.err.println("Error writing  to Excel file.");
	        e.printStackTrace();
	    }
	    return isSuccess;
	}
	
}//end class
