package pageObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Constant;
import utility.ExcelUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Homepage {

    static{
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }
//    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(Homepage.class);
    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    public static void main(String[] args) {
        Login.login("http://train.ltrailways.com/");
        //logout();
        addCompany();
    }

    /**
     * To logout.
     */
    public static void logout(){
        if(null != getLogout()){
            getLogout().click();
        }
    }

    /**
     * To add company in company management page.
     */
    public static void addCompany(){
        Login.login("http://10.102.0.222:8070/web/user/login");

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
        String companyNameWithTime = companyName + "_" + currentTime;
        String detailsOfCompanyWithTime = detailOfCompany + "_" + currentTime;

        if(getCompanyManagementElement() != null){
            getCompanyManagementElement().click();
            getCompanyListElement().click();
            getAddCompanyButton().click();
            getCompanyNameElement().sendKeys(companyNameWithTime);
            getDetailsOfCompanyElement().sendKeys(detailsOfCompanyWithTime);
            getConfirmAddButton().click();
            Login.driver.switchTo().alert().accept();
        }

        //To assert company added successfully or not
        if(getAddedCompanyName() != null && getAddedCompanyName().getText().equals(companyName)){

            String remindingMessage = getAddedCompanyName().getText().equals(companyName) ? "company added successfully !" : "company added failed !";
            Assert.assertEquals(remindingMessage, companyName, getAddedCompanyName().getText());

            log.info(getAddedCompanyName().getText().toString());
        }
    }

    /**
     * Get logout element.
     * @return the WebElement.
     */
    public static WebElement getLogout(){
        return wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector(".notifications-wrapper > li:nth-child(2) > a")));
//        return Login.driver.findElement(By.cssSelector(".notifications-wrapper > li:nth-child(2) > a"));
    }

    /**
     * Get company management element on the left navigation list
     * @return the WebElement
     */
    public static WebElement getCompanyManagementElement(){
        WebElement companyManagement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#accordion > li:nth-child(2) > a")));
        return companyManagement;
    }

    /**
     * Get company list sub element under company management tab
     * @return the WebElement
     */
    public static WebElement getCompanyListElement(){
        WebElement companyList = Login.driver.findElement(By.cssSelector("#collapseOne > ul > li:nth-child(1) > a"));
        return companyList;
    }

    /**
     * Get add company button in company list page
     * @return the WebElement
     */
    public static WebElement getAddCompanyButton(){
        WebElement addCompany = Login.driver.findElement(By.cssSelector(".dment-fn > button"));
        return addCompany;
    }

    /**
     * Get company name input element on the add_company details overlay
     * @return the WebElement
     */
    public static WebElement getCompanyNameElement(){
        WebElement companyName = Login.driver.findElement(By.cssSelector("#company_name"));
        return companyName;
    }

    /**
     * Get details_of_company input element on the add_company details overlay
     * @return the WebElement
     */
    public static WebElement getDetailsOfCompanyElement(){
        WebElement detailsOfCompany = Login.driver.findElement(By.cssSelector("#company_desc"));
        return detailsOfCompany;
    }

    /**
     * Get confirm_add_company submit button on the add_company details overlay
     * @return the WebElement
     */
    public static WebElement getConfirmAddButton(){
        WebElement confirmAdd = Login.driver.findElement(By.cssSelector("#myModal-add > div > div > div:nth-child(3) > button:nth-child(2)"));
        return confirmAdd;
    }

    /**
     * Get the company name which just added before in company table, normally pick up the first record of company list table
     * @return the WebElement
     */
    public static WebElement getAddedCompanyName(){
        WebElement addedCompanyName = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#com-listPage > tr:nth-child(1) > td:nth-child(2)")));
        return addedCompanyName;
    }
}
