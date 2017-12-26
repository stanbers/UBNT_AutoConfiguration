package utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

/**
 * the firefox webdriver giver
 */
public class WebDriverGiver {
    public static WebDriver getWebDriver(){
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myProfile = profile.getProfile("testProfile");
        FirefoxOptions options = new FirefoxOptions().setProfile(myProfile);
        WebDriver driver = new FirefoxDriver(options);
        return driver;
    }
}
