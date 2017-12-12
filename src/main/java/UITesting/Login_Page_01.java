package UITesting;

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

public class Login_Page_01 {

    private final static Log log = LogFactory.getLog(Login_Page_01.class);
    public static void main(String[] args) throws Exception{

        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
        Login_draft login = new Login_draft();
        WebDriver driver = login.getWebDriver();

        driver.get("http://train.ltrailways.com/");

        WebElement username = driver.findElement(By.name("name"));
        WebElement password = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.tagName("button"));
        WebElement isAdmin = driver.findElement(By.name("is_admin"));

        ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);

        //This is to get the values from Excel sheet, passing parameters (Row num &amp; Col num)to getCellData method
        String sUserName = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_login_master", 0, true).get(0);
        String sPassword = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_login_master", 0, true).get(1);

        username.sendKeys(sUserName);
        password.sendKeys(sPassword);

        isAdmin.click();
        loginButton.submit();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement logoElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#logo > h1")));

        //run a test
        String logo = logoElement.getText();
        log.info(logo.toString());
        log.debug(logo.toString());
        String successLogo = "铁路施工管理";

        Assert.assertEquals (logo, successLogo);

        driver.quit();
    }

}