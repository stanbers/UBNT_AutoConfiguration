package UITesting.loginPage.independently;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import pageObject.Login;

public class LoginTest {

    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(LoginTest.class);

    @Test
    public void login() {
        String logo  = Login.getLogoText("http://train.ltrailways.com/");
        String successLogo = "铁路施工管理";

        log.info(logo.toString());
        Assert.assertEquals (logo, successLogo);
//        WebDriverGiver.getWebDriver().quit();
    }
}
