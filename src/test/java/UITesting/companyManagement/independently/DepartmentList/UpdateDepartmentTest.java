package UITesting.companyManagement.independently.DepartmentList;

import UITesting.companyManagement.independently.CompanyList.AddCompanyTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Login;
import utility.Constant;
import utility.ExcelUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/18 8:55
 * @Email stanxu526@gmail.com
 */
public class UpdateDepartmentTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void updateDepartment(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        String rowIndex = null;
        String departmentName = null;
        String detailsOfDepartment = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimestamp = sdf.format(new Date());

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_updateDepartment", 21);
            if (parameterList != null && parameterList.size() > 0){
                rowIndex = parameterList.get(0);
                departmentName = parameterList.get(1);
                detailsOfDepartment = parameterList.get(2);
            }else {
                log.info("can not get correct parameters from excel !");
            }

            //to make sure there is no repetition company info
            departmentName = departmentName + "_" + currentTimestamp;
            detailsOfDepartment = detailsOfDepartment + "_" + currentTimestamp;

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                this.getDepartmentListElement().click();
                Thread.sleep(3000);

                this.getEditCompanyButton(rowIndex).click();
                //clear all original values
                this.getDepartmentName().clear();
                this.getDetailsOfDepartment().clear();
                //update all fields with new auto_generated values
                this.getDepartmentName().sendKeys(departmentName);
                this.getDetailsOfDepartment().sendKeys(detailsOfDepartment);

                this.saveButton().click();
                Login.driver.switchTo().alert().accept();
                Thread.sleep(3000);
            }else {
                log.info("can not open company_management accordion !");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        WebElement updatedDepartmentNameElement = this.getDepartmentInfo(rowIndex,5);
        WebElement updatedDepartmentDetailElement = this.getDepartmentInfo(rowIndex,6);
        if(updatedDepartmentNameElement != null && updatedDepartmentDetailElement != null){
            Assert.assertEquals(departmentName, updatedDepartmentNameElement.getText());
            Assert.assertEquals(detailsOfDepartment, updatedDepartmentDetailElement.getText());
        }

    }

    /**
     * Get company management element on the left navigation list
     * @return the WebElement
     */
    private WebElement getCompanyManagementElement(){
        WebElement companyManagement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#accordion > li:nth-child(2) > a")));
        return companyManagement;
    }

    /**
     * Get department list sub element under company management tab
     * @return the WebElement
     */
    private WebElement getDepartmentListElement(){
        WebElement companyList = Login.driver.findElement(By.cssSelector("#collapseOne > ul > li:nth-child(2) > a"));
        return companyList;
    }

    /**
     * Get edit company input button from department list table
     * @param rowIndex  the department row index
     * @return
     */
    private WebElement getEditCompanyButton(String rowIndex){
        WebElement editCompanyButton = Login.driver.findElement(By.cssSelector("#departmentTbody > tr:nth-child("+ rowIndex +") > td:nth-child(9) > button:nth-child(1)"));
        return editCompanyButton;
    }

    /**
     * Get department name input element from 'update_department_details' overlay
     * @return the WebElement
     */
    private WebElement getDepartmentName(){
        WebElement departmentName = Login.driver.findElement(By.id("update_department_name"));
        return departmentName;
    }

    /**
     * Get details of department area element from 'update_department_details' overlay
     * @return the WebElement
     */
    private WebElement getDetailsOfDepartment(){
        WebElement detailsOfDepartment = Login.driver.findElement(By.id("update_department_desc"));
        return detailsOfDepartment;
    }

    /**
     * Get save button from 'update_department_details' overlay
     * @return
     */
    private WebElement saveButton(){
        WebElement saveButton = Login.driver.findElement(By.id("department_update"));
        return saveButton;
    }

    /**
     * Get department name and details on company table.
     * @param rowIndex   the department record row index
     * @param cellIndex  the department name/details cell index
     * @return the WebElement
     */
    private WebElement getDepartmentInfo(String rowIndex, int cellIndex){
        WebElement updatedDepartmentInfo = Login.driver.findElement(By.cssSelector("#departmentTbody > tr:nth-child("+ rowIndex +") > td:nth-child("+ cellIndex +")"));
        return updatedDepartmentInfo;
    }

}
