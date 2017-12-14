package utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ExcelUtils {

    private static XSSFSheet ExcelWSheet;
    private static XSSFWorkbook ExcelWBook;
    private static XSSFCell Cell;
    private static XSSFRow Row;

    private final static Log log = LogFactory.getLog(ExcelUtils.class);

    /**
     * This method is to set the File path and to open the Excel file, Pass Excel Path and Sheetname as Arguments to this method
     * @param Path  the data excel absolute path
     * @param SheetName  the sheet name inside of the excel
     * @throws Exception
     */
    public static void setExcelFile(String Path,String SheetName) throws Exception {
        try {
            // Open the Excel file
            FileInputStream ExcelFile = new FileInputStream(Path);
            // Access the required test data sheet
            if(ExcelFile != null){
                ExcelWBook = new XSSFWorkbook(ExcelFile);
                ExcelWSheet = ExcelWBook.getSheet(SheetName);
            }
        } catch (Exception e){
            throw (e);
        }
    }

    /**
     * This method is to read the test data from the Excel cell, firstly we find the specify test case name
     * then we are collect parameters as Row num and Col num based on the cell Row num adn Col num of the test case name
     * @param caseName  the test case name
     * @param rowIndex  the test case location
     * @return   the parameter list
     * @throws Exception
     */
    public static List<String> getParametersViaCaseName(String caseName, int rowIndex) throws Exception{
        List<String> list = new ArrayList<String>();
        try{
            int lastRowIndex = ExcelWSheet.getLastRowNum();
            labelA:
            for (int i = rowIndex; i <= lastRowIndex; i++) {
                XSSFRow row  = ExcelWSheet.getRow(i+2);
                if (row == null) { break; }

                short lastCellNum = row.getLastCellNum();
                for (int j = 0; j < lastCellNum; j++) {
                    String cellValue = row.getCell(j).getStringCellValue();
                    if(caseName != null && caseName.equals(cellValue)){
                        for (int k = 0; k < lastCellNum ; k++) {
                           if(row.getCell(k+3) != null){
                                list.add(row.getCell(k+3).getStringCellValue());
                            }else {
                                break labelA;
                            }
                        }
                    }else {
                        return null;
                    }
                }
            }
            return list;
        }catch (Exception e){
            return null;
        }
    }

    public static String getMethodFromExcel(String caseName){

        String methodName = null;
        try{
            int lastRowIndex = ExcelWSheet.getLastRowNum();
            labelA:
            for (int i = 0; i <= lastRowIndex; i++) {
                XSSFRow row  = ExcelWSheet.getRow(i+2);
                if (row == null) { break; }

                short lastCellNum = row.getLastCellNum();
                for (int j = 0; j < lastCellNum; j++) {
                    String cellValue = row.getCell(j).getStringCellValue();
                    if(caseName != null && caseName.equals(cellValue)){
                        String test = row.getCell(j+1).getRawValue();
                        String classAndMethod = row.getCell(j+2).getStringCellValue();
                        if(classAndMethod != null){
                            String[] methods = classAndMethod.split("\\.");
                            methodName= methods[1];
                        }
                    }else {
                        break labelA;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        log.info(methodName);
        return methodName;
    }


    public static void findCell(String caseName,int offset){
        try{
            int lastRowIndex = ExcelWSheet.getLastRowNum();
            labelA:
            for (int i = 0; i <= lastRowIndex; i++) {
                XSSFRow row  = ExcelWSheet.getRow(i+2);
                if (row == null) { break; }

                short lastCellNum = row.getLastCellNum();
                for (int j = 0; j < lastCellNum; j++) {
                    String cellValue = row.getCell(j).getStringCellValue();
                    if(caseName != null && caseName.equals(cellValue)){
                        for (int k = 0; k < lastCellNum ; k++) {
                            if(row.getCell(k+offset).getRawValue() != null){
                                //list.add(row.getCell(k+offset).getRawValue());
                            }else {
                                break labelA;
                            }
                        }
                    }else {
                        //return null;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method is to write in the Excel cell, Row num and Col num are the parameters
     * No use for current.
     * @param Result   the target result which will be written into the excel
     * @param RowNum   the rol number
     * @param ColNum   the column number
     * @throws Exception
     */
    public static void setCellData(String Result,  int RowNum, int ColNum) throws Exception	{
        try{
            Row  = ExcelWSheet.getRow(RowNum);
            Cell = Row.getCell(ColNum, MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (Cell == null) {
                Cell = Row.createCell(ColNum);
                Cell.setCellValue(Result);
            } else {
                Cell.setCellValue(Result);
            }

            // Constant variables Test Data path and Test Data file name
            FileOutputStream fileOut = new FileOutputStream(Constant.Path_TestData);
            ExcelWBook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        }catch(Exception e){
            throw (e);
        }
    }
}
