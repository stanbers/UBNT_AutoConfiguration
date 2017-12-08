package appModules;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Constant;
import utility.ExcelUtils;
import draft.Login;

public class SignIn_Action {

    public static void main(String[] args) throws Exception{

        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
        Login login = new Login();
        WebDriver driver = login.getWebDriver();

        driver.get("http://train.ltrailways.com/");

        WebElement username = driver.findElement(By.name("name"));
        WebElement password = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.tagName("button"));
        WebElement isAdmin = driver.findElement(By.name("is_admin"));

        ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);

        //This is to get the values from Excel sheet, passing parameters (Row num &amp; Col num)to getCellData method
        String sUserName = ExcelUtils.getCellData(1, 1);
        String sPassword = ExcelUtils.getCellData(1, 2);

        username.sendKeys(sUserName);
        password.sendKeys(sPassword);

        isAdmin.click();
        loginButton.submit();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement logoElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#logo > h1")));

        //run a test
        String logo = logoElement.getText();

        String successLogo = "铁路施工管理";

        System.out.println("Inside testPrintMessage()");
        Assert.assertEquals (logo, successLogo);

        ExcelUtils.setCellData("Pass", 1, 3);

        driver.quit();
    }

}