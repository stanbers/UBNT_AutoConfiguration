package ubnt.m5;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.UpdateConfigFile;
import utility.WebDriverGiver;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 11:14
 * @Email stanxu526@gmail.com
 */
public class M5_Configuration {
    static{
//        System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+"\\SeleniumGecko\\geckodriver.exe");
        System.setProperty("webdriver.gecko.driver","D:\\SeleniumGecko\\geckodriver.exe");
    }

    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(M5_Configuration.class);

    public static int progress = 0;

    /**
     * here is to update the target M5(AP/ST) fields
     * @param side   AP or ST side
     * @param updatedSSID  the new ssid name from swing input box
     * @param updatedIP   the new IP address from swing input box
     * @param updatedNetmask   the new netmask from swing input box
     * @param updateGatewayIP   the new gateway IP from swing input box
     * @param updatedFruq  the new frequency fro swing input box
     * @param updatedMACAddress  the AP end mac address from swing input box
     */
    public static void configM5(String side,String updatedSSID,String updatedIP,String updatedNetmask,String updateGatewayIP,
                                String updatedFruq,String updatedMACAddress){

        //String relativePath = "D:\\ConfigFile\\"+side+"Config.cfg";

        //update M5 config file
        if (side.equals("AP")){
            UpdateConfigFile.updateFile(updatedSSID,updatedIP,updatedNetmask,updateGatewayIP,updatedFruq,null,side);
        }
        else if (side.equals("ST")){
            UpdateConfigFile.updateFile(updatedSSID,updatedIP,updatedNetmask,updateGatewayIP,null,updatedMACAddress,side);
        }

        driver.get("https://192.168.1.20/login.cgi");

        if (getUsername() != null && getAgreedCheckbox() != null){
            getUsername().sendKeys("ubnt");
            getPassword().sendKeys("ubnt");
            selectCountry("840");
            selectLanguage("en_US");
            getAgreedCheckbox().click();

        }else {
            //login page changed when configuration fail
            getUsername().sendKeys("ubnt");
            getPassword().sendKeys("ubnt");
        }

        getLoginButton().click();

        try {
            Thread.sleep(4000);
            //navigate to System tab
            int attempts = 0;
            while(attempts < 10) {
                try {
                    getSystemTab().click();
                    log.info("tried "+ attempts + (attempts <= 1 ? " time" : " times"));
                    break;
                } catch(Exception e) {
                }
                attempts++;
            }

            Thread.sleep(3000);

            getScanFileButton().sendKeys("D:\\ConfigFile\\"+side+"_Config.cfg");

            log.info("the target configuration file was found, waiting for upload");

            getUploadFileButton().click();

            Thread.sleep(1000);
            if (getApplyButton() != null){
                getApplyButton().click();
                progress = 1;
            }

            Thread.sleep(10000);
            log.info("uploaded done !");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get username element on login page.
     * @return the WebElement
     */
    private static WebElement getUsername(){
        return driver.findElement(By.id("username"));
    }

    /**
     * Get password element on login page.
     * @return the WebElement
     */
    private static WebElement getPassword(){
        return driver.findElement(By.id("password"));
    }

    /**
     * Select a country from country select dropdown list on login page
     */
    private static void selectCountry(String country){
        Select companyList = new Select(driver.findElement(By.id("country")));
        if(companyList != null){
            companyList.selectByValue(country);
            log.info(country + " was selected !");
        }else {
            log.info("there is no any countries can be selected !");
        }
    }

    /**
     * Select a language from country select dropdown list on login page
     * @param language   the language
     */
    private static void selectLanguage(String language){
        Select languageList = new Select(driver.findElement(By.id("ui_language")));
        if(languageList != null){
            languageList.selectByValue(language);
            log.info(language + " was selected !");
        }else {
            log.info("there is no any languages can be selected !");
        }
    }

    /**
     * Get 'agree' checkbox element
     * @return the WebElement
     */
    private static WebElement getAgreedCheckbox(){
        return driver.findElement(By.id("agreed"));
    }

    /**
     * Get Login button element on login page
     * @return the WebElement
     */
    private static WebElement getLoginButton(){
        return driver.findElement(By.xpath("//input[@value='Login' and @type='submit']"));
    }

    /**
     * Get M5 AP mac address under Main tab
     * @return the WebElement
     */
    private static WebElement getAPMacAddress(){
        WebElement apMac = driver.findElement(By.id("apmac"));
        if (apMac != null){
            log.info("The M5 ap mac address is: "+apMac.getText());
        }
        return apMac;
    }

    /**
     * Get System navigation tab
     * @return the WebElement
     */
    private static WebElement getSystemTab(){
        WebElement navigationTab = driver.findElement(By.xpath("//img[@alt='System']"));
        log.info("navigated to "+navigationTab+"tab");
        return navigationTab;
    }

    /**
     * Get scan_file button under System tab
     * @return the WebElement
     */
    private static WebElement getScanFileButton(){
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement scanFileButton = wait.until( ExpectedConditions.presenceOfElementLocated(By.id("cfgfile")));
        return scanFileButton;
    }

    /**
     * Get upload_file button under System tab
     * @return the WebElement
     */
    private static WebElement getUploadFileButton(){
        WebElement uploadFileButton = driver.findElement(By.id("cfgupload"));
        if (uploadFileButton != null){
            log.info(uploadFileButton.getAttribute("value") +" button was clicked !");
        }
        return uploadFileButton;
    }

    /**
     * Get apply button after click upload button
     * @return the WebElement
     */
    private static WebElement getApplyButton(){
        WebElement applyButton = driver.findElement(By.id("apply_button"));
        if (applyButton != null){
            log.info(applyButton.getAttribute("value"));
        }
        return applyButton;
    }
}
