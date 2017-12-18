package functionalTesting.companyManagement.independently.stuffList;

import functionalTesting.companyManagement.independently.companyList.AddCompanyTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Login;
import utility.Constant;
import utility.ExcelUtils;

import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/18 14:33
 * @Email stanxu526@gmail.com
 */
public class UpdateStuffTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    String companyName,departmentName,jobTitle,stuffName,stuffMail,stuffPhoneNum,schedulingInfo,rowIndex;
    boolean isAssignHWAccount;
    @Test
    public void updateStuff(){
        try {
            Login.login("http://10.103.0.4:8080/web/user/login");
        }catch (WebDriverException wde){
            //wde.printStackTrace();
        }

        long currentTimestamp = System.currentTimeMillis();
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_updateStuff", 33);
            if (parameterList != null && parameterList.size() > 0){
                companyName = parameterList.get(0);
                departmentName = parameterList.get(1);
                jobTitle = parameterList.get(2);
                stuffName = parameterList.get(3) + currentTimestamp;
                stuffMail = parameterList.get(4);
                stuffPhoneNum = parameterList.get(5);
                schedulingInfo = parameterList.get(6);
                isAssignHWAccount = new Boolean(parameterList.get(7));
                rowIndex = parameterList.get(8);
                log.info(stuffName);
            }else {
                log.info("can not get correct parameters from excel !");
            }

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                Thread.sleep(3000);
                this.getStuffListElement().click();

                this.getEditStuffButton(rowIndex).click();

                this.getStuffNameElement().clear();
                this.getStuffNameElement().sendKeys(stuffName);

                this.selectCompany(companyName);
                this.selectDepartment(departmentName);
                this.selectJob(jobTitle);

                this.getStuffMailboxElement().clear();
                this.getStuffMailboxElement().sendKeys(stuffMail);

                this.getStuffPhoneNumElement().clear();
                this.getStuffPhoneNumElement().sendKeys(stuffPhoneNum);

                this.selectSchedulingInfo(schedulingInfo);

                List<WebElement> assignHWAccountRadios = getAssignHWAccountRadios();
                if(!isAssignHWAccount){
                    assignHWAccountRadios.get(0).click();
                }else {
                    assignHWAccountRadios.get(1).click();
                }

                this.getSaveButton().click();
                Thread.sleep(3000);
                Login.driver.switchTo().alert().accept();
                Thread.sleep(2000);
                log.info("all fields have been updated !");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        WebElement updatedStuffName = this.getStuffRealNameElement(rowIndex);
        if (updatedStuffName != null){
            int attempts = 0;
            while (attempts < 5){
                try {
                    if(updatedStuffName.getText() != null){
                        log.info(updatedStuffName.getText());
                        log.info("stuffName_"+stuffName);
                        Assert.assertEquals(stuffName,updatedStuffName.getText());
                        break;
                    }
                }catch(StaleElementReferenceException e){
                    //e.printStackTrace();
                }
                attempts++;
            }
        }

    }

    /**
     * Get company management element on the left navigation list
     * @return the WebElement
     */
    private WebElement getCompanyManagementElement(){
        WebDriverWait wait = new WebDriverWait(Login.driver, 10);
        WebElement companyManagement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#accordion > li:nth-child(2) > a")));
        return companyManagement;
    }

    /**
     * Get stuff list sub element under company management tab
     * @return the WebElement
     */
    private WebElement getStuffListElement(){
        WebElement stuffList = Login.driver.findElement(By.cssSelector("#collapseOne > ul:nth-child(1) > li:nth-child(4) > a"));
        log.info(stuffList.getText());
        return stuffList;
    }

    /**
     * Get 'edit_stuff' button from stuff record list table
     * @param rowIndex   the stuff record index
     * @return the WebElement
     */
    private WebElement getEditStuffButton(String rowIndex){
        WebElement editStuffButton = Login.driver.findElement(By.cssSelector("#userTbody > tr:nth-child("+rowIndex+") > td:nth-child(12) > button:nth-child(1)"));
        log.info(editStuffButton.getAttribute("type"));
        return editStuffButton;
    }

    /**
     * Get stuff real name from stuff records list table
     * @param rowIndex   the stuff record index
     * @return the WebElement
     */
    private WebElement getStuffRealNameElement(String rowIndex){
        WebElement stuffRealName = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#userTbody > tr:nth-child("+rowIndex+") > td:nth-child(4)")));
        log.info(stuffRealName.getText());
        return stuffRealName;
    }

    /**
     * Get stuff name from 'edit_stuff_info' overlay
     * @return the WebElement
     */
    private WebElement getStuffNameElement(){
        WebElement stuffName = Login.driver.findElement(By.id("update_user_name"));
        return stuffName;
    }

    /**
     * select a company from company drop down list on 'update_stuff_details' overlay.
     * @param company  a value of company drop down list
     */
    private void selectCompany(String company){
        Select companyList = new Select(Login.driver.findElement(By.id("revice-user-company-select")));
        if(companyList != null){
            companyList.selectByValue(company);
            log.info(company);
        }else {
            log.info("there is no any company can be selected !");
        }
    }

    /**
     * select a department from department drop down list on 'update_stuff_details' overlay.
     * @param department  a value of department drop down list
     */
    private void selectDepartment(String department){
        Select departmentList = new Select(Login.driver.findElement(By.id("revice-user-department-select")));
        if(departmentList != null){
            departmentList.selectByValue(department);
            log.info(department);
        }else {
            log.info("there is no any department can be selected !");
        }
    }

    /**
     * select a job from department drop down list on 'update_stuff_details' overlay.
     * @param job  a value of department drop down list
     */
    private void selectJob(String job){
        Select departmentList = new Select(Login.driver.findElement(By.id("revice-user-position-select")));
        if(departmentList != null){
            departmentList.selectByValue(job);
            log.info(job);
        }else {
            log.info("there is no any department can be selected !");
        }
    }

    /**
     * Get stuff mail address on 'update_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getStuffMailboxElement(){
        WebElement stuffMailbox = Login.driver.findElement(By.id("update_user_email"));
        log.info(stuffMailbox.getText());
        return stuffMailbox;
    }

    /**
     * Get stuff phone number on 'update_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getStuffPhoneNumElement(){
        WebElement stuffPhoneNum = Login.driver.findElement(By.id("update_user_phone"));
        log.info(stuffPhoneNum.getText());
        return stuffPhoneNum;
    }

    /**
     * select a type from schedulingInfo drop down list on 'update_stuff_details' overlay.
     * @param schedulingInfo  a value of job drop down list
     */
    private void selectSchedulingInfo(String schedulingInfo){
        Select schedulingInfoList = new Select(Login.driver.findElement(By.id("update-user-type")));
        if(schedulingInfoList != null){
            schedulingInfoList.selectByValue(schedulingInfo);
            log.info(schedulingInfoList.getOptions());
        }else {
            log.info("there is no any schedulingInfo can be selected !");
        }
    }

    /**
     * Get AssignHWAccount radios on 'update_stuff_details' overlay.
     * @return the radios list
     */
    private List<WebElement> getAssignHWAccountRadios(){
        List<WebElement> assignHWAccountRadios = Login.driver.findElements(By.name("huawei"));
        return assignHWAccountRadios;
    }

    /**
     * Get save button on 'update_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getSaveButton(){
        WebElement saveButton = Login.driver.findElement(By.id("user_update"));
        log.info(saveButton.getText());
        return saveButton;
    }
}
