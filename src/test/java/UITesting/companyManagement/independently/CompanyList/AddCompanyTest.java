package UITesting.companyManagement.independently.CompanyList;

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

public class AddCompanyTest {

    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void addCompany(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        String companyName = null;
        String detailOfCompany = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_addCompany", 3);
            companyName = parameterList.get(0);
            detailOfCompany = parameterList.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        companyName = companyName + "_" + currentTime;
        detailOfCompany = detailOfCompany + "_" + currentTime;

        if(this.getCompanyManagementElement() != null){

            this.getCompanyManagementElement().click();
            this.getCompanyListElement().click();
            this.getAddCompanyButton().click();
            this.getCompanyNameElement().sendKeys(companyName);
            this.getDetailsOfCompanyElement().sendKeys(detailOfCompany);
            this.getConfirmAddButton().click();
            //Login.driver.switchTo().alert().accept();
            this.getCloseButton().click();
        }

        if(this.getAddedCompanyName() != null && this.getAddedCompanyName().getText().equals(companyName)){

            String remindingMessage = this.getAddedCompanyName().getText().equals(companyName) ? "company added successfully !" : "company added failed !";
            Assert.assertEquals(remindingMessage, companyName, this.getAddedCompanyName().getText());

            log.info(this.getAddedCompanyName().getText().toString());
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
     * Get company list sub element under company management tab
     * @return the WebElement
     */
    private WebElement getCompanyListElement(){
        WebElement companyList = Login.driver.findElement(By.cssSelector("#collapseOne > ul > li:nth-child(1) > a"));
        return companyList;
    }

    /**
     * Get add company button in company list page
     * @return the WebElement
     */
    private WebElement getAddCompanyButton(){
        WebElement addCompany = Login.driver.findElement(By.cssSelector(".dment-fn > button"));
        return addCompany;
    }

    /**
     * Get company name input element on the add_company details overlay
     * @return the WebElement
     */
    private WebElement getCompanyNameElement(){
        WebElement companyName = Login.driver.findElement(By.cssSelector("#company_name"));
        return companyName;
    }

    /**
     * Get details_of_company input element on the add_company details overlay
     * @return the WebElement
     */
    private WebElement getDetailsOfCompanyElement(){
        WebElement detailsOfCompany = Login.driver.findElement(By.cssSelector("#company_desc"));
        return detailsOfCompany;
    }

    /**
     * Get confirm_add_company submit button on the add_company details overlay
     * @return the WebElement
     */
    private WebElement getConfirmAddButton(){
        WebElement confirmAdd = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(2)"));
        return confirmAdd;
    }

    /**
     * not for firefox.
     * @return
     */
//    public WebElement getCloseOverlayButton(){
//        WebElement closeButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(1)"));
//        return closeButton;
//    }

    /**
     * Get the company name which just added before in company table, normally pick up the first record of company list table
     * @return the WebElement
     */
    private WebElement getAddedCompanyName(){
        WebElement addedCompanyName = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#com-listPage > tr:nth-child(1) > td:nth-child(2)")));
        return addedCompanyName;
    }

    /**
     * get close button on add_details_of_company overlay
     * @return the WebElement
     */
    private WebElement getCloseButton(){
        WebElement closeButton = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(1)"));
        return closeButton;
    }
}
