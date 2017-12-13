package pageObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Constant;
import utility.ExcelUtils;
import utility.ScreenCapture;
import utility.WebDriverGiver;

import java.util.List;

public class Login {

    static{
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(Login.class);

    private final static String CASE_NAME = "TrainScheduling_ltrailways_login_master";

    public static void login(String url){

        driver.get(url);
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName(CASE_NAME, 0);
            String sUserName = parameterList.get(0);
            String sPassword = parameterList.get(1);

//            String methodName = ExcelUtils.getMethodFromExcel(CASE_NAME);
//            log.info(methodName);

            WebElement username = getUsername();
            WebElement password = getPassword();
            WebElement isAdmin = getIsAdminbutton();
            WebElement loginButton = getLoginButton();

            username.sendKeys(sUserName);
            password.sendKeys(sPassword);

            new ScreenCapture().stepCapture(CASE_NAME);

            isAdmin.click();
            loginButton.submit();
//            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLogoText(String url){
//        driver.get(url);
        try {
            login(url);
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement logoElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#logo > h1")));

            new ScreenCapture().stepCapture(CASE_NAME);
            return logoElement.getText();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "element can not be found !";
    }

    public static void main(String[] args){
//        Login.login("http://train.ltrailways.com/");
        Login.login("http://10.102.0.222:8070/web/user/login");
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

    public static WebElement getIsAdminbutton() {
        return driver.findElement(By.name("is_admin"));

    }

}