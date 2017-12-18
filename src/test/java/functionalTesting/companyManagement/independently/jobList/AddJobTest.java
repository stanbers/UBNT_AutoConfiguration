package functionalTesting.companyManagement.independently.jobList;

import functionalTesting.companyManagement.independently.companyList.AddCompanyTest;
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
 * @Date 2017/12/14 9:38
 * @Email stanxu526@gmail.com
 */
public class AddJobTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void addJob(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        String companyName = null;
        String departmentName = null;
        String jobTitle = null;
        String detailsOfJob = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimestamp = sdf.format(new Date());

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_addJob", 9);
            if(parameterList != null && parameterList.size() > 0){
                companyName = parameterList.get(0);
                departmentName = parameterList.get(1);
                jobTitle = parameterList.get(2);
                detailsOfJob = parameterList.get(3);
            }else {
                log.info("can not get correct parameters from excel !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        jobTitle = jobTitle + "_" + currentTimestamp;
        detailsOfJob = detailsOfJob + "_" + currentTimestamp;

        if(this.getCompanyManagementElement() != null){
            this.getCompanyManagementElement().click();
            this.getJobListElement().click();
            this.getAddJobButton().click();
            this.selectCompany(companyName);
            this.selectDepartment(departmentName);
            this.getJobTitleElement().sendKeys(jobTitle);
            this.getDetailsOfJobElement().sendKeys(detailsOfJob);
            this.getConfirmAddButton().click();
            this.getCloseButton().click();
        }else {
            log.info("can not open company management tab !");
        }

        if(this.getAddedJobTitle() != null && this.getAddedJobTitle().getText().equals(companyName)){

            String remindingMessage = this.getAddedJobTitle().getText().equals(companyName) ? "job added successfully !" : "job added failed !";
            Assert.assertEquals(remindingMessage, companyName, this.getAddedJobTitle().getText());

            log.info(this.getAddedJobTitle().getText().toString());
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
     * Get job list sub element under company management tab
     * @return the WebElement
     */
    private WebElement getJobListElement(){
        WebElement jobList = Login.driver.findElement(By.cssSelector("#collapseOne > ul > li:nth-child(3) > a"));
        return jobList;
    }

    /**
     * Get add job button in company list page
     * @return the WebElement
     */
    private WebElement getAddJobButton(){
        WebElement addDepartment = Login.driver.findElement(By.cssSelector(".dment-fn > button"));
        return addDepartment;
    }

    /**
     * select a company from company drop down list on 'add_job_details' overlay.
     * @param company  a value of company drop down list
     */
    private void selectCompany(String company){
        Select companyList = new Select(Login.driver.findElement(By.id("add-position-company-select")));
        if(companyList != null){
            companyList.selectByValue(company);
        }else {
            log.info("there is no any company can be selected !");
        }
    }

    /**
     * select a department from department drop down list on 'add_job_details' overlay.
     * @param department a value of department drop down list
     */
    private void selectDepartment(String department){
        Select departmentList = new Select(Login.driver.findElement(By.id("add-position-department-select")));
        if(departmentList != null){
            departmentList.selectByValue(department);
        }else {
            log.info("there is no any department can be selected !");
        }
    }

    /**
     * Get job title element on 'add_job_details' overlay.
     * @return the WebElement
     */
    private WebElement getJobTitleElement(){
        WebElement jobTitle = Login.driver.findElement(By.id("add-position-name"));
        return jobTitle;
    }

    /**
     * Get details of job element on 'add_job_details' overlay.
     * @return the WebElement
     */
    private WebElement getDetailsOfJobElement(){
        WebElement detailsOfJob = Login.driver.findElement(By.id("add-position-desc"));
        return detailsOfJob;
    }

    /**
     * Get confirm add button on add_details_of_job overlay
     * @return the WebElement
     */
    private WebElement getConfirmAddButton(){
        WebElement confirmAddButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(2)"));
        return confirmAddButton;
    }

    /**
     * Get the job title which just added before in job list table, normally pick up the first record of job list table
     * @return the WebElement
     */
    private WebElement getAddedJobTitle(){
        WebElement addedJobTitle = Login.driver.findElement(By.cssSelector("#position_tbody > tr:nth-child(1) > td:nth-child(2)"));
        return addedJobTitle;
    }

    /**
     * get close button on add_details_of_job overlay
     * @return the WebElement
     */
    private WebElement getCloseButton(){
        WebElement closeButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(1)"));
        return closeButton;
    }
}
