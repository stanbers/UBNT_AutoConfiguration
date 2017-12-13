package UITesting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Login;

public class LogoutTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(LogoutTest.class);

    WebDriverWait wait = new WebDriverWait(Login.driver, 3);
    @Test
    public void logout(){
        Login.login("http://10.102.0.222:8070/web/user/login");
        if(null != getLogout()){
            getLogout().click();
        }
        String loginText = "登陆";
        String loginButton = Login.getLoginButton().getText();
        Assert.assertEquals(loginText, loginButton);

//        Login.driver.quit();
    }

    /**
     * Get logout element.
     * @return the WebElement.
     */
    public WebElement getLogout(){
        //this driver has to be Login's driver, otherwise it will trigger another browser window.
        //do not use vpn here
        WebElement logoutElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector(".notifications-wrapper > ul > li:nth-child(2) > a")));
        return logoutElement;

    }
}
