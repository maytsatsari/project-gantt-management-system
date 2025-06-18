package util;

import org.apache.poi.ss.usermodel.CellStyle;

public class ProjectInfo {
    private String projectName;
    private String sourceFileName;
    private String targetFileName;
    private int totalNumTasks;
    private int totalTopTasks;
    private FileTypes fileType;
    
    public ProjectInfo() {
    	// empty constructor without parametres
    }

    public ProjectInfo(String projectName, String sourceFileName, String targetFileName, int totalNumTasks,
            int totalTopTasks) {
        this.projectName = projectName;
        this.sourceFileName = sourceFileName;
        this.targetFileName = targetFileName;
        this.totalNumTasks = totalNumTasks;
        this.totalTopTasks = totalTopTasks;
    }

    // Getters
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }
    
    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    
    public String getTargetFileName() {
        return targetFileName;
    }
    
    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public int getTotalNumTasks() {
        return totalNumTasks;
    }
    
    public void setTotalNumTasks(int totalNumTasks) {
        this.totalNumTasks = totalNumTasks;
    }

    public int getTotalTopTasks() {
        return totalTopTasks;
    }
    
    public void setTotalTopTasks(int totalTopTasks) {
        this.totalTopTasks = totalTopTasks;
    }

    // Setters are needed for the  MainController
    public void setTargetPath(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public FileTypes  getFileType() {
        return fileType;
    }

    public void setFileType(FileTypes fileType) {
        this.fileType = fileType;
    }
    // ToString for print the object 
    @Override
    public String toString() {
        return "ProjectInfo [projectName=" + projectName + "\n sourceFileName=" + sourceFileName + "\n targetFileName="
                + targetFileName + "\n totalNumTasks=" + totalNumTasks + "\n totalTopTasks=" + totalTopTasks + "]";
    }

	public CellStyle getStyle(String headerStyleName) {
		return null;
	}
	
}
