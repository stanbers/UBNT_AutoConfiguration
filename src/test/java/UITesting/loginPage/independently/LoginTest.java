package UITesting.loginPage.independently;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import pageObject.Login;
import utility.ScreenCapture;

public class LoginTest {

    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(LoginTest.class);

    @Test
    public void login() {
        String logo  = Login.getLogoText("http://10.103.0.4:8080/web/user/login");
        String successLogo = "铁路施工管理";

        new ScreenCapture().stepCapture("TrainScheduling_ltrailways_login_master");
        log.info(logo.toString());

        Assert.assertEquals (logo, successLogo);
//        WebDriverGiver.getWebDriver().quit();
    }
}
