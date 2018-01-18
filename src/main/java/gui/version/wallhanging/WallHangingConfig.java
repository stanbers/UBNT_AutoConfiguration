package gui.version.wallhanging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utility.WebDriverGiver;

/**
 * @Author by XuLiang
 * @Date 2018/01/17 9:37
 * @Email stanxu526@gmail.com
 */
public class WallHangingConfig {
    {
//        System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+"\\SeleniumGecko\\geckodriver.exe");
        System.setProperty("webdriver.gecko.driver","D:\\SeleniumGecko\\geckodriver.exe");
    }

    public WebDriver driver = WebDriverGiver.getWebDriver();

    private final Log log = LogFactory.getLog(WallHangingConfig.class);

    public int progress = 0;

    public int config(String ssidName, String wallHangingIP, String wallHangingNetmask, String wallHangingGatewayIP, String serverIP){
        log.info("ssid is: " +ssidName+" ;wall hanging IP is :" + wallHangingIP +" ; net mask is :"+wallHangingNetmask+" ; gateway IP is "+wallHangingGatewayIP
            +" ; server IP is "+serverIP);
        String URL = "http://admin:admin@10.10.100.254/index_cn.html";
        driver.get(URL);
        try {

            //click penetrate parameter tab
            this.getWiFiParametersOption(3).click();
            Thread.sleep(1000);
            //select protocol of socket A
            this.selectOption("UDPC","__SL_P_USM");
            Thread.sleep(1000);
            driver.switchTo().frame("ifrPage");
            //the server IP
            this.inputData("__SL_P_USI").sendKeys(serverIP);
            this.saveButton().click();
            Thread.sleep(1000);
            driver.switchTo().defaultContent();

            //click WiFi parameter tab
            this.getWiFiParametersOption(2).click();
            Thread.sleep(3000);
            //select model
            this.selectOption("sta","__SL_P_WMD");
            //fill up ssid
            Thread.sleep(1000);
            driver.switchTo().frame("ifrPage");
            this.inputData("__SL_P_WSM").sendKeys(ssidName);
            driver.switchTo().defaultContent();
            Thread.sleep(1000);
            //select encrypt type
            this.selectOption("OPEN","__SL_P_WST");
            Thread.sleep(1000);
            //select DHCP
            this.selectOption("OFF","__SL_P_WDH");
            Thread.sleep(1000);

            //fill up IP/netmask/gateway IP
            driver.switchTo().frame("ifrPage");
            this.inputData("__SL_P_WDI").sendKeys(wallHangingIP);
            this.inputData("__SL_P_WDM").sendKeys(wallHangingNetmask);
            this.inputData("__SL_P_WDG").sendKeys(wallHangingGatewayIP);
            this.saveButton().click();
            driver.switchTo().defaultContent();

            //navigate to system management tab
            this.getWiFiParametersOption(5).click();

            //click restart button to make the configuration became effective
            driver.switchTo().frame("ifrPage");
            this.restartButton().click();
            progress = 1;
            log.info("the restart button was clicked !");
            driver.switchTo().defaultContent();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return progress;
    }

    /**
     * Get WiFi_Parameters tab on the left navigation
     * @return the web element
     */
    private WebElement getWiFiParametersOption(int index){
        WebElement wifiParameters = driver.findElement(By.cssSelector("#sidebar2 > ul > li:nth-child("+index+") >a"));
        log.info("the " + wifiParameters.getText() + " was clicked !");
        return wifiParameters;
    }

    /**
     * Select combo box
     * @param optionValue  the option value under specific combo box
     * @param selectName  the combo box element name
     */
    private void selectOption(String optionValue, String selectName){
        driver.switchTo().frame("ifrPage");
        Select encryptTypeList = new Select(driver.findElement(By.name(selectName)));
        encryptTypeList.selectByValue(optionValue);
        driver.switchTo().defaultContent();
        log.info(optionValue + " was selected !");
    }

    /**
     * Fill up IP/netmask/gateway ip etc
     * @param inputName  the input text field element name
     * @return the web element
     */
    private WebElement inputData(String inputName){
        WebElement input = driver.findElement(By.name(inputName));
        input.clear();
        return input;
    }

    /**
     * Get save button
     * @return the web element
     */
    private WebElement saveButton(){
        WebElement save = driver.findElement(By.xpath("//input[@value='保存' and @type='button']"));
        return save;
    }

    /**
     * Get restart button
     * @return  the web element
     */
    private WebElement restartButton(){
        WebElement restart = driver.findElement(By.xpath("//input[@value='重启' and @type='button']"));
        return restart;
    }

}
