package functionalTesting.companyManagement.independently.departmentList;

import functionalTesting.companyManagement.independently.companyList.AddCompanyTest;
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

import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/18 10:44
 * @Email stanxu526@gmail.com
 */
public class RemoveDepartmentTEst {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void removeDepartment(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        boolean isRemove = false;
        String rowIndex = null;
        String targetDepartment = null;

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_removeDepartment", 24);

            if(parameterList != null && parameterList.size() >0){
                rowIndex = parameterList.get(0);
                isRemove = new Boolean(parameterList.get(1));
            }else {
                log.info("can not get the parameters from excel !");
            }

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                this.getDepartmentListElement().click();

                Thread.sleep(3000);
                targetDepartment = this.getDepartmentNameElement(rowIndex).getText();
                this.getRemoveDepartmentButton(rowIndex).click();

                //remove department or not
                if(isRemove){
                    Login.driver.switchTo().alert().accept();
                    //there are 2 alert() while using firefox, here sleep 2s is good for the next js alert(), other
                    Thread.sleep(2000);
                    Login.driver.switchTo().alert().accept();
                }else {
                    Login.driver.switchTo().alert().dismiss();
                }

                Thread.sleep(3000);
            }else {
                log.info("can not open company_management accordion !");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 1; i <= getDepartmentRecords().size(); i++) {
            log.info(getDepartmentRecords().size());
            Assert.assertTrue("the company already removed !", getDepartmentNameElement(String.valueOf(i)).getText() != targetDepartment);
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
     * Get department name from department list table
     * @param rowIndex    the department row index
     * @return the WebElement
     */
    private WebElement getDepartmentNameElement(String rowIndex){
        WebElement departmentName = Login.driver.findElement(By.cssSelector("#departmentTbody > tr:nth-child("+rowIndex+") > td:nth-child(5)"));
        return departmentName;
    }

    /**
     * Get remove_department button from department list table
     * @param rowIndex the department row index
     * @return the WebElement
     */
    private WebElement getRemoveDepartmentButton(String rowIndex){
        WebElement removeDepartmentButton = Login.driver.findElement(By.cssSelector("#departmentTbody > tr:nth-child("+rowIndex+") > td:nth-child(9) > button:nth-child(2)"));
        return removeDepartmentButton;
    }

    /**
     * Get department records on company table
     * @return department records
     */
    private List<WebElement> getDepartmentRecords(){
        List<WebElement> departmentRecords = Login.driver.findElements(By.cssSelector("#departmentTbody > tr"));
        return departmentRecords;
    }
}
