package functionalTesting.companyManagement.independently.stuffList;

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

import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/14 10:30
 * @Email stanxu526@gmail.com
 */
public class AddStuffTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    String companyName,departmentName,jobTitle,stuffName,password,realStuffName,stuffMail,stuffPhoneNum,schedulingInfo;
    boolean assignHWAccount = false;

    @Test
    public void addStuff(){
        Login.login("http://10.103.0.4:8080/web/user/login");

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long ctm = System.currentTimeMillis();

//        String currentTimestamp = sdf.format(new Date());
        String currentTimestamp = String.valueOf(ctm);
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_addStuff", 12);
            if(parameterList != null && parameterList.size() > 0){
                companyName = parameterList.get(0);
                departmentName = parameterList.get(1);
                jobTitle = parameterList.get(2);
                stuffName = parameterList.get(3);
                password = parameterList.get(4);
                realStuffName = parameterList.get(5);
                stuffMail = parameterList.get(6);
                stuffPhoneNum = parameterList.get(7);
                schedulingInfo = parameterList.get(8);
                assignHWAccount = new Boolean(parameterList.get(9));
            }else {
                log.info("can not get correct parameters from excel !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stuffName = stuffName + "_" + currentTimestamp;
        realStuffName = realStuffName + "_" + currentTimestamp;

        if(this.getCompanyManagementElement() != null){
            this.getCompanyManagementElement().click();
            //the reason of thread sleep 3s is to waiting for CompanyManagement accordion open up
            //otherwise the following element would not be found!
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.getStuffListElement().click();
            this.getAddStuffButton().click();

            this.selectCompany(companyName);
            this.selectDepartment(departmentName);
            this.selectJob(jobTitle);
            this.getStuffNameElement().sendKeys(stuffName);
            this.getStuffPWDElement().sendKeys(password);
            this.getRealStuffNameElement().sendKeys(realStuffName);
            this.getStuffMailElement().sendKeys(stuffMail);
            this.getStuffPhoneNumElement().sendKeys(stuffPhoneNum);
            this.selectSchedulingInfo(schedulingInfo);

            List<WebElement> assignHWAccountRadios = getAssignHWAccountRadios();
            if(!assignHWAccount){
                assignHWAccountRadios.get(0).click();
            }else {
                assignHWAccountRadios.get(1).click();
            }

            this.getConfirmAddButton().click();
            this.getCloseButton().click();

        }else {
            log.info("can not open company management tab !");
        }

        if(this.getAddedStuffName() != null && this.getAddedStuffName().getText().equals(stuffName)){

            log.info(this.getAddedStuffName().getText().toString());
            String remindingMessage = this.getAddedStuffName().getText().equals(stuffName) ? "stuff added successfully !" : "job added failed !";
            Assert.assertEquals(remindingMessage, stuffName, this.getAddedStuffName().getText());

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
     * Get add stuff button in company list page
     * @return the WebElement
     */
    private WebElement getAddStuffButton(){
        WebElement addDepartment = Login.driver.findElement(By.cssSelector(".dment-fn > button:nth-child(1)"));
        return addDepartment;
    }

    /**
     * select a company from company drop down list on 'add_stuff_details' overlay.
     * @param company  a value of company drop down list
     */
    private void selectCompany(String company){
        Select companyList = new Select(Login.driver.findElement(By.id("add-user-company-select")));
        if(companyList != null){
            companyList.selectByValue(company);
        }else {
            log.info("there is no any company can be selected !");
        }
    }

    /**
     * select a department from department drop down list on 'add_stuff_details' overlay.
     * @param department  a value of department drop down list
     */
    private void selectDepartment(String department){
        Select departmentList = new Select(Login.driver.findElement(By.id("add-user-department-select")));
        if(departmentList != null){
            departmentList.selectByValue(department);
        }else {
            log.info("there is no any department can be selected !");
        }
    }

    /**
     * select a job from department drop down list on 'add_stuff_details' overlay.
     * @param job  a value of department drop down list
     */
    private void selectJob(String job){
        Select departmentList = new Select(Login.driver.findElement(By.id("add-user-position-select")));
        if(departmentList != null){
            departmentList.selectByValue(job);
        }else {
            log.info("there is no any department can be selected !");
        }
    }

    /**
     * Get stuff name from 'add_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getStuffNameElement(){
        WebElement stuffName = Login.driver.findElement(By.id("add-user-position-name"));
        return stuffName;
    }

    /**
     * Get stuff password from 'add_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getStuffPWDElement(){
        WebElement stuffPWD = Login.driver.findElement(By.id("add-user-position-password"));
        return stuffPWD;
    }

    /**
     * Get stuff real name from 'add_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getRealStuffNameElement(){
        WebElement realStuffName = Login.driver.findElement(By.id("add-user-position-real-name"));
        return realStuffName;
    }

    /**
     * Get stuff mailbox from 'add_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getStuffMailElement(){
        WebElement stuffMail = Login.driver.findElement(By.id("add-user-position-email"));
        return stuffMail;
    }

    /**
     * Get stuff phone number from 'add_stuff_details' overlay.
     * @return the WebElement
     */
    private WebElement getStuffPhoneNumElement(){
        WebElement stuffPhoneNum = Login.driver.findElement(By.id("add-user-position-phone"));
        return stuffPhoneNum;
    }

    /**
     * select a job from schedulingInfo drop down list on 'add_stuff_details' overlay.
     * @param schedulingInfo  a value of department drop down list
     */
    private void selectSchedulingInfo(String schedulingInfo){
        Select schedulingInfoList = new Select(Login.driver.findElement(By.id("add-user-type")));
        if(schedulingInfoList != null){
            schedulingInfoList.selectByValue(schedulingInfo);
        }else {
            log.info("there is no any schedulingInfo can be selected !");
        }
    }

    /**
     * Get AssignHWAccount radios on 'add_stuff_details' overlay.
     * @return the radios list
     */
    private List<WebElement> getAssignHWAccountRadios(){
        List<WebElement> assignHWAccountRadios = Login.driver.findElements(By.name("addP"));
        return assignHWAccountRadios;
    }

    /**
     * Get confirm add button on add_stuff_of_job overlay
     * @return the WebElement
     */
    private WebElement getConfirmAddButton(){
        WebElement confirmAddButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(2)"));
        return confirmAddButton;
    }

    /**
     * get close button on add_stuff_of_job overlay
     * @return the WebElement
     */
    private WebElement getCloseButton(){
        WebElement closeButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(1)"));
        return closeButton;
    }

    /**
     * Get stuff name which just added before from stuff list table, normally pick up the first record of stuff list table
     * @return the WebElement
     */
    private WebElement getAddedStuffName(){
        WebElement addedStuffName = Login.driver.findElement(By.cssSelector("#userTbody > tr:nth-child(1) > td:nth-child(3) "));
        log.info(addedStuffName.getText());
        return addedStuffName;
    }

}
