package pageObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Homepage {

    static{
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }
//    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(Homepage.class);

    public static void logout(){

        if(null != getLogout()){
            getLogout().click();
        }
    }

    public static void main(String[] args) {
        Login.login("http://train.ltrailways.com/");
        logout();
    }
    /**
     * Get logout element.
     * @return the WebElement.
     */
    public static WebElement getLogout(){
        WebDriverWait wait = new WebDriverWait(Login.driver, 10);
        return wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector(".notifications-wrapper > li:nth-child(2) > a")));
//        return Login.driver.findElement(By.cssSelector(".notifications-wrapper > li:nth-child(2) > a"));
    }
}
