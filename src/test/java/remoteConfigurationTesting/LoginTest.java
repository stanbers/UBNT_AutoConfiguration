package remoteConfigurationTesting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import pageObject.Login_UBNT;

public class LoginTest {

    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(LoginTest.class);

    @Test
    public void login() {

        new Login_UBNT().login("https://192.168.1.20/login.cgi");

//        String logoutButton  = new Login_UBNT().getLogoText("https://192.168.1.20/login.cgi");
//
//        new ScreenCapture().stepCapture("UBNT_configuration");
//        log.info(logoutButton);
//
//        Assert.assertEquals ("input", logoutButton);
//        WebDriverGiver.getWebDriver().quit();
    }
}
