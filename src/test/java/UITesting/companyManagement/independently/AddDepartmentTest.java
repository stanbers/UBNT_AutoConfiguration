package UITesting.companyManagement.independently;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Login;
import utility.Constant;
import utility.ExcelUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/13 14:06
 * @Email stanxu526@gmail.com
 */
public class AddDepartmentTest {

    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 10);


    @Test
    public void addDepartment(){
        Login.login("http://10.102.0.222:8070/web/user/login");

        String departmentName = null;
        String detailsOfDepartment = null;
        String companyName = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_addDepartment", 6);
            if(parameterList != null && parameterList.size() > 0){
                companyName = parameterList.get(0);
                departmentName = parameterList.get(1);
                detailsOfDepartment = parameterList.get(2);
            }else {
                log.info("can not get correct parameters from excel !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        departmentName = departmentName + "_" + currentTime;
        detailsOfDepartment = detailsOfDepartment + "_" + currentTime;

        if(this.getCompanyManagementElement() != null){
            this.getCompanyManagementElement().click();
            this.getDepartmentListElement().click();
            this.getAddDepartmentButton().click();
            this.selectCompany(companyName);
            this.getDepartmentNameInputElement().sendKeys(departmentName);
            this.getDetailsOfDepartmentInputElement().sendKeys(detailsOfDepartment);
            this.getConfirmAddButton().click();
            //the following line may need to remove , cause the overlay may closed after "add_confirm" button clicked.
            this.getCloseButton().click();
//            Login.driver.switchTo().alert().accept();
        }

        if(this.getAddedDepartmentName() != null && this.getAddedDepartmentName().getText().equals(companyName)){

            String remindingMessage = this.getAddedDepartmentName().getText().equals(companyName) ? "department added successfully !" : "department added failed !";
            Assert.assertEquals(remindingMessage, companyName, this.getAddedDepartmentName().getText());

            log.info(this.getAddedDepartmentName().getText().toString());
        }

    }

    /**
     * select a company when create a department.
     * @param company  a value of company drop down list
     */
    public void selectCompany(String company){
        Select companyList = new Select(Login.driver.findElement(By.id("add-department-company-select")));
        if(companyList != null){
            companyList.selectByValue(company);
        }else {
            log.info("there is no any company can be selected !");
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
     * Get add department button in company list page
     * @return the WebElement
     */
    private WebElement getAddDepartmentButton(){
        WebElement addDepartment = Login.driver.findElement(By.cssSelector(".dment-fn > button"));
        return addDepartment;
    }

    /**
     * get department_name input element on add_details_of_Department overlay
     * @return the WebElement
     */
    private WebElement getDepartmentNameInputElement(){
        WebElement departmentNameInput = Login.driver.findElement(By.id("add-department-name"));
        return departmentNameInput;
    }

    /**
     * get details of department input element on add_details_of_Department overlay
     * @return the WebElement
     */
    private WebElement getDetailsOfDepartmentInputElement(){
        WebElement detailsOfDepartmentInput = Login.driver.findElement(By.id("add-department-desc"));
        return detailsOfDepartmentInput;
    }

    /**
     * get confirm add button on add_details_of_Department overlay
     * @return the WebElement
     */
    private WebElement getConfirmAddButton(){
        WebElement confirmAddButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(2)"));
        return confirmAddButton;
    }

    /**
     * get close button on add_details_of_Department overlay
     * @return
     */
    private WebElement getCloseButton(){
        WebElement closeButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(1)"));
        return closeButton;
    }

    /**
     * Get the department name which just added before in department list table, normally pick up the first record of department list table
     * @return
     */
    private WebElement getAddedDepartmentName(){
        WebElement addedDepartmentName = Login.driver.findElement(By.cssSelector("#departmentTbody > tr:nth-child(1) > td:nth-child(2)"));
        return addedDepartmentName;
    }

}
