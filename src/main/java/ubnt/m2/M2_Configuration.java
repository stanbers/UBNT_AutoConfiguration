package ubnt.m2;

import com.google.common.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utility.WebDriverGiver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/22 11:37
 * @Email stanxu526@gmail.com
 */
public class M2_Configuration {
    static{
        System.setProperty("webdriver.gecko.driver","D:\\SeleniumGecko\\geckodriver.exe");
//        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(M2_Configuration.class);

//    private final static String relativePath = System.getProperty("user.dir")+"\\src\\main\\java\\configData\\M2_Config.cfg";
    private final static String relativePath = "D:\\ConfigFile\\M2_Config.cfg";

    private static String originalSSID = "wireless.1.ssid=";
    private static String originalIP = "netconf.3.ip=";
    private static String originalNetmask1 = "netconf.1.netmask=";
    private static String originalNetmask2 = "netconf.2.netmask=";
    private static String originalNetmask3 = "netconf.3.netmask=";
    private static String originalGateway = "route.1.gateway=";

    public static void configM2(String updatedSSID,String updatedIP,String updatedNetmask,String updateGatewayIP ){
        //update the 5 fields which passed from swing GUI input box in the config file
        List<String> newLines = new ArrayList<String>();
        try {
            for (String line : Files.readLines(new File(relativePath), StandardCharsets.UTF_8)) {
                if (line.contains(originalSSID)){
                    newLines.add(line.replace(line,originalSSID+updatedSSID));
                }
                else if(line.contains(originalIP)){
                    newLines.add(line.replace(line,originalIP+updatedIP));
                }
                else if (line.contains(originalNetmask1)){
                    newLines.add(line.replace(line,originalNetmask1+updatedNetmask));
                }
                else if (line.contains(originalNetmask2)){
                    newLines.add(line.replace(line,originalNetmask2+updatedNetmask));
                }
                else if (line.contains(originalNetmask3)){
                    newLines.add(line.replace(line,originalNetmask3+updatedNetmask));
                }
                else if(line.contains(originalGateway)){
                    newLines.add(line.replace(line,originalGateway+updateGatewayIP));
                }
                else{
                    newLines.add(line);
                }
            }

            //write into the same file
            FileWriter writer = new FileWriter(relativePath);
            for (String newLine: newLines) {
                writer.write(newLine);
                writer.append(System.getProperty("line.separator"));
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        driver.get("https://192.168.1.20/login.cgi");
        getUsername().sendKeys("ubnt");
        getPassword().sendKeys("ubnt");
        selectCountry("840");
        selectLanguage("en_US");
        getAgreedCheckbox().click();
        getLoginButton().click();

        //record AP mac address
        //may need to write into excel, may handle it later if needed
        String apMacAddress = getAPMacAddress().getText();
        log.info("The AP mac address is: "+apMacAddress);

        //navigate to System tab
        getSystemTab().click();

        try {
            Thread.sleep(3000);

            getScanFileButton().sendKeys(relativePath);

            log.info("the target configuration file was found, waiting for upload");

            getUploadFileButton().click();

            Thread.sleep(1000);
            getApplyButton().click();

            Thread.sleep(20000);
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
        log.info("The M5 ap mac address is: "+apMac.getText());
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
        WebElement scanFileButton = driver.findElement(By.id("cfgfile"));
        log.info(scanFileButton.getAttribute("realname"));
        return scanFileButton;
    }

    /**
     * Get upload_file button under System tab
     * @return the WebElement
     */
    private static WebElement getUploadFileButton(){
        WebElement uploadFileButton = driver.findElement(By.id("cfgupload"));
        log.info(uploadFileButton.getAttribute("value") +" button was clicked !");
        return uploadFileButton;
    }

    /**
     * Get apply button after click upload button
     * @return the WebElement
     */
    private static WebElement getApplyButton(){
        WebElement applyButton = driver.findElement(By.id("apply_button"));
        log.info(applyButton.getAttribute("value"));
        return applyButton;
    }
}
