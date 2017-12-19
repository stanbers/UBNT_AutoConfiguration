package remoteConfigurationTesting;

import functionalTesting.companyManagement.independently.companyList.AddCompanyTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Login;

/**
 * @Author by XuLiang
 * @Date 2017/12/19 11:14
 * @Email stanxu526@gmail.com
 */
public class UBNTConfigration {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);
}
