package pageObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Constant;
import utility.ExcelUtils;
import utility.WebDriverGiver;

public class Login {

    static{
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(Login.class);

    public static void login(String url){
        driver.get(url);
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);
            //This is to get the values from Excel sheet
            String sUserName = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_login_master").get(0);
            String sPassword = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_login_master").get(1);

            String methodName = ExcelUtils.getMethodFromExcel("TrainScheduling_ltrailways_login_master");
            log.info(methodName);

            WebElement username = getUsername();
            WebElement password = getPassword();
            WebElement isAdmin = getIsAdminbutton();
            WebElement loginButton = getLoginButton();

            username.sendKeys(sUserName);
            password.sendKeys(sPassword);

            isAdmin.click();
            loginButton.submit();

            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement logoElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#logo > h1")));

            //run a test
            String logo = logoElement.getText();
            log.info(logo.toString());
            String successLogo = "铁路施工管理";

            Assert.assertEquals (logo, successLogo);

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Login.login("http://train.ltrailways.com/");
    }

    //get web element.
    public static WebElement getUsername(){
        return driver.findElement(By.name("name"));
    }

    public static WebElement getPassword(){
        return driver.findElement(By.name("password"));
    }

    public static WebElement getLoginButton(){
        return driver.findElement(By.tagName("button"));
    }

    public static WebElement getIsAdminbutton(){
        return driver.findElement(By.name("is_admin"));
    }


}