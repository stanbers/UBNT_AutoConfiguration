package functionalTesting.companyManagement.independently.jobList;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/18 11:15
 * @Email stanxu526@gmail.com
 */
public class UpdateJob {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void updateJob(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        String companyName = null;
        String departmentName = null;
        String jobTitle = null;
        String detailsOfJob = null;
        String rowIndex = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimestamp = sdf.format(new Date());

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_updateJob", 27);
            if (parameterList != null && parameterList.size() > 0) {
                //update job via click 'edit' button in job list table immediately, didn't filter out the company and the department
                //then the companyName and departmentName may not be used.
                companyName = parameterList.get(0);
                departmentName = parameterList.get(1);
                jobTitle = parameterList.get(2);
                detailsOfJob = parameterList.get(3);
                rowIndex = parameterList.get(4);
            }else {
                log.info("can not get correct parameters from excel !");
            }

            //to make sure there is no repetition job info
            jobTitle = jobTitle + "_" + currentTimestamp;
            detailsOfJob = detailsOfJob + "_" + currentTimestamp;

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                this.getJobListElement().click();
                Thread.sleep(2000);

                this.getEditJobButton(rowIndex).click();

                //clear all original fields' value
                this.getJobTitleInputElement().clear();
                this.getJobDetailsInputElement().clear();

                //update all fields with new auto_generated values
                this.getJobTitleInputElement().sendKeys(jobTitle);
                this.getJobDetailsInputElement().sendKeys(detailsOfJob);

                this.getSaveButton().click();
                Login.driver.switchTo().alert().accept();
                Thread.sleep(2000);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        WebElement updatedJobTitle = this.getJobInfo(rowIndex,5);
        WebElement updatedJobDetails = this.getJobInfo(rowIndex,6);
        if (updatedJobTitle != null && updatedJobDetails != null){
            Assert.assertEquals(jobTitle,updatedJobTitle.getText());
            Assert.assertEquals(detailsOfJob,updatedJobDetails.getText());
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
     * Get edit_Job button from job list table
     * @param rowIndex   the job record index
     * @return the WebElement
     */
    private WebElement getEditJobButton(String rowIndex){
        WebElement editJobButton = Login.driver.findElement(By.cssSelector("#position_tbody > tr:nth-child("+rowIndex+") > td:nth-child(10) > button:nth-child(1)"));
        return editJobButton;
    }

    /**
     * Get job_title input element from 'edit_job_info' overlay
     * @return the WebElement
     */
    private WebElement getJobTitleInputElement(){
        WebElement jobTitle = Login.driver.findElement(By.id("update_position_name"));
        return jobTitle;
    }

    /**
     * Get job_details input element from 'edit_job_info' overlay
     * @return the WebElement
     */
    private WebElement getJobDetailsInputElement(){
        WebElement jobDetails = Login.driver.findElement(By.id("update_position_desc"));
        return jobDetails;
    }

    /**
     * Get save button from 'edit_job_info' overlay
     * @return the WebElement
     */
    private WebElement getSaveButton(){
        WebElement saveButton  = Login.driver.findElement(By.id("position_update"));
        return saveButton;
    }

    /**
     * Get job title and job details from job list table;
     * @param rowIndex   the job row index
     * @param cellIndex   the job details <td> index
     * @return the WebElement
     */
    private WebElement getJobInfo(String rowIndex, int cellIndex){
        WebElement jobInfo = Login.driver.findElement(By.cssSelector("#position_tbody > tr:nth-child("+rowIndex+") > td:nth-child("+cellIndex+")"));
        return jobInfo;
    }
}
