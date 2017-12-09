package draft;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class LoginTest {

    @Test
    public void testPrintMessage() {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
        Login_draft login = new Login_draft();
        WebDriver driver = login.getWebDriver();
        String logo = login.getLogoText();
        String successLogo = "铁路施工管理";

        System.out.println("Inside testPrintMessage()");
        Assert.assertEquals (logo, successLogo);
        driver.quit();

    }
}
