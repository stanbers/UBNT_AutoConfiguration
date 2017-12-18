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

import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/18 13:56
 * @Email stanxu526@gmail.com
 */
public class RemoveJobTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void removeJob(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        String rowIndex = null;
        String targetJob = null;
        boolean isRemove = false;

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_removeJob", 30);

            if (parameterList != null && parameterList.size() > 0){
                rowIndex = parameterList.get(0);
                isRemove = new Boolean(parameterList.get(1));
            }else {
                log.info("can not get the parameters from excel !");
            }

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                this.getJobListElement().click();
                Thread.sleep(2000);

                targetJob = this.getJobTitleElement(rowIndex).getText();

                this.getRemoveJobButton(rowIndex).click();

                //remove job or not
                if(isRemove){
                    Login.driver.switchTo().alert().accept();
                    //there are 2 alert() while using firefox, here sleep 2s is good for the next js alert(), otherwise cannot handle the next alert()
                    Thread.sleep(2000);
                    Login.driver.switchTo().alert().accept();
                }else {
                    Login.driver.switchTo().alert().dismiss();
                }

                Thread.sleep(2000);
            }else {
                log.info("can not open company_management accordion !");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 1; i <= getJobRecords().size(); i++) {
            log.info(getJobRecords().size() + getJobTitleElement(String.valueOf(i)).getText());
            Assert.assertTrue("the job already removed !", getJobTitleElement(String.valueOf(i)).getText() != targetJob);
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
     * Get job title from job list table
     * @param rowIndex    the job record index
     * @return the WebElement
     */
    private WebElement getJobTitleElement(String rowIndex){
        WebElement jobTitle = Login.driver.findElement(By.cssSelector("#position_tbody > tr:nth-child("+rowIndex+") > td:nth-child(5)"));
        return jobTitle;
    }

    /**
     * Get remove_job button from job list table
     * @param rowIndex   the job record index
     * @return the WebElement
     */
    private WebElement getRemoveJobButton(String rowIndex){
        WebElement removeJobButton = Login.driver.findElement(By.cssSelector("#position_tbody > tr:nth-child("+rowIndex+") > td:nth-child(10) > button:nth-child(2)"));
        return removeJobButton;
    }

    /**
     * Get job records list from job list table
     * @return the WebElement
     */
    private List<WebElement> getJobRecords(){
        List<WebElement> jobRecords = Login.driver.findElements(By.cssSelector("#position_tbody > tr"));
        return jobRecords;
    }

}
