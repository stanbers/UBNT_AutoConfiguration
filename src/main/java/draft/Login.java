package draft;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Login {
    public WebDriver getWebDriver(){
        ProfilesIni profile = new ProfilesIni();

        FirefoxProfile myProfile = profile.getProfile("testProfile");

        FirefoxOptions options = new FirefoxOptions().setProfile(myProfile);
        WebDriver driver = new FirefoxDriver(options);
        return driver;
    }
    public String getLogoText(){
        WebDriver driver = getWebDriver();
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");

        driver.get("http://train.ltrailways.com/");

        // Perform actions on HTML elements, entering text and submitting the form
        WebElement username = driver.findElement(By.name("name"));
        WebElement password = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.tagName("button"));
        WebElement isAdmin = driver.findElement(By.name("is_admin"));

        username.sendKeys("13659191907");
        password.sendKeys("654321");


        isAdmin.click();
        loginButton.submit();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement logoElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#logo > h1")));

        //run a test
        String logo = logoElement.getText();
        return logo;


    }
}
