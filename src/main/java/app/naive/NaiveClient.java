package app.naive;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import app.ApplicationController;
import util.FileTypes;
import util.ProjectInfo;

public class NaiveClient {

	/*
	 * add -Dlog4j2.loggerContextFactory=org.apache.logging.log4j.simple.SimpleLoggerContextFactory 
	 * at the vm arguments, to avoid the complaints about the logger
	 */
	public static void main(String args[]) {

		ApplicationController appController = new ApplicationController();
		
		//  ////////////// Load /////////////////
		List<String> loadedStr = appController.load("./src/test/resources/input/table.tsv", FileTypes.CSV);
		//List<String> loadedStr = appController.load("./src/test/resources/input/Shop.tsv", FileTypes.CSV);
		//List<String> loadedStr = appController.load("src/test/resources/input/EggsScrambled.xlsx", FileTypes.XLSX);	
		System.out.println();System.out.println();
		System.out.println("----------");
		for (String s: loadedStr)
			System.out.println(s);

		//  ///////////////////// create workbook ///////////////
		//create workbook: absolutely needed for all else.
		ProjectInfo prjInfo = appController.prepareTargetWorkbook(FileTypes.XLSX, "./src/test/resources/output/Table_Output.xlsx");
		//ProjectInfo prjInfo = appController.prepareTargetWorkbook(FileTypes.XLSX,"./src/test/resources/input/ShopOutput.xlsx");
        //ProjectInfo prjInfo = appController.prepareTargetWorkbook(FileTypes.XLSX,".src/test/resources/input/EggsScrambled_Output.xlsx");
		System.out.println("----------");
		System.out.println("\n" + prjInfo);
		System.out.println("----------");
		
		//  ///////////////////// create sheets ///////////////
		appController.rawWriteToExcelFile(appController.getAllTasks());
		

		//String styleName,	
		//short styleFontColor, short styleFontHeightInPoints, String styleFontName, 
		//boolean styleFontBold, boolean styleFontItalic,boolean styleFontStrikeout,
		//short styleFillForegroundColor,String styleFillPatternString,	String HorizontalAlignmentString, boolean styleWrapText
		String greenStyleName = appController.addFontedStyle("myTealThing", 
			    IndexedColors.TEAL.getIndex(), (short)10, "Times New Roman", 
			    false, false, false, 
			    IndexedColors.WHITE.getIndex(), 
			    "SOLID_FOREGROUND", "LEFT", false);

		
		String orangeStyleName = appController.addFontedStyle("myOrangeThing", 
			    IndexedColors.RED.getIndex(), (short)10, "Times New Roman", 
			    false, false, false, 
			    IndexedColors.ORANGE.getIndex(), 
			    "SOLID_FOREGROUND", "LEFT", false);

		String YellowStyleName = appController.addFontedStyle("myYellowThing", 
			    IndexedColors.YELLOW.getIndex(), (short)13, "Arial", 
			    false, true, false, 
			    IndexedColors.TEAL.getIndex(), 
			    "SOLID_FOREGROUND", "RIGHT", false);
		
		String NewHeaderStyleName = appController.addFontedStyle("myHeader", 
			    IndexedColors.YELLOW.getIndex(), (short)13, "Collibri", 
			    true, true, false, 
			    IndexedColors.RED.getIndex(), 
			    "SOLID_FOREGROUND", "LEFT", false);
		
		String NewBarStyleName = appController.addFontedStyle("myBar", 
			    IndexedColors.WHITE.getIndex(), (short)13, "Collibri", 
			    false, false, false, 
			    IndexedColors.BLACK.getIndex(), 
			    "SOLID_FOREGROUND", "LEFT", false);
		
		
		
		appController.createNewSheet("ALL_Styled", appController.getAllTasks(), 
				"DefaultHeaderStyle", "TopTask_bar_style", "TopTask_data_style", "NonTopTask_bar_style", "NonTopTask_data_style", "Normal"); 

		appController.createNewSheet("Τop_Level", appController.getTopLevelTasksOnly(), 
				"DefaultHeaderStyle", "TopTask_bar_style", "TopTask_data_style", "NonTopTask_bar_style", "NonTopTask_data_style", "Normal"); 

		appController.createNewSheet("Range", appController.getTasksInRange(103,202), 
				"DefaultHeaderStyle", "TopTask_bar_style", "TopTask_data_style", "NonTopTask_bar_style", "NonTopTask_data_style", "Normal"); 
		
		System.out.println("End of naive xlsx client");
	}

}
