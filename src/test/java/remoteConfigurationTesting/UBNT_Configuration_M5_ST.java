package remoteConfigurationTesting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import pageObject.UBNT_M5_Configuration_ST;

public class UBNT_Configuration_M5_ST {

    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(UBNT_Configuration_M5_ST.class);

    @Test
    public void login() {

        new UBNT_M5_Configuration_ST().login("https://192.168.1.20/login.cgi");

//        String logoutButton  = new UBNT_M2_Configuration().getLogoText("https://192.168.1.20/login.cgi");
//
//        new ScreenCapture().stepCapture("UBNT_configuration");
//        log.info(logoutButton);
//
//        Assert.assertEquals ("input", logoutButton);
//        WebDriverGiver.getWebDriver().quit();
    }
}
