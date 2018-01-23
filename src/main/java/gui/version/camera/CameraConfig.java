package gui.version.camera;

import gui.version.wallhanging.WallHangingConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utility.WebDriverGiver;

/**
 * @Author by XuLiang
 * @Date 2018/01/23 15:23
 * @Email stanxu526@gmail.com
 */
public class CameraConfig {

    {
//        System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+"\\SeleniumGecko\\geckodriver.exe");
        System.setProperty("webdriver.gecko.driver","D:\\SeleniumGecko\\geckodriver.exe");
    }

    public WebDriver driver = WebDriverGiver.getWebDriver();

    private final Log log = LogFactory.getLog(WallHangingConfig.class);

    public int progress = 0;

    public void config(){
        String URL = "http://192.168.1.64/doc/page/login.asp";
        driver.get(URL);
        try {
            Thread.sleep(1000);
            if (this.getOverlayContainer().getCssValue("display").trim().equals("none")){
                this.viaID("username").sendKeys("admin");
                this.viaID("password").sendKeys("xalt12345");
                this.getLoginButton().click();
            }else {

                this.getUpdatePwd().sendKeys("xalt12345");
                this.getConfirmUpdatePwd().sendKeys("xalt12345");
                this.getUpdateButton().click();
                //need wait for few seconds in order to load the next overlay
                Thread.sleep(2000);
                this.getCancelButton().click();
            }

            Thread.sleep(1000);
            this.getConfigTab().click();

            Thread.sleep(1000);
            this.viaID("ui-id-2").click();

            Thread.sleep(1000);
            this.viaID("radioNTP").click();

            this.getSaveButton("settingTime").click();

            //navigate to internet tab
            this.getMenuTab(3).click();
            Thread.sleep(1000);

            //IPv4
            this.getIPInputBox(5).clear();
            this.getIPInputBox(5).sendKeys("192.168.1.63");

            //IPv4 net mask
            this.getIPInputBox(6).clear();
            this.getIPInputBox(6).sendKeys("255.255.254.0");

            //IPv4 gateway IP
            this.getIPInputBox(7).clear();
            this.getIPInputBox(7).sendKeys("192.168.1.2");
            this.getSaveButton("basicTcpIp").click();

            //advianced configuration
            this.getSubTab(3,3).click();
            Thread.sleep(3000);

            this.viaID("ui-id-13").click();
            Thread.sleep(3000);

            this.selectAccess(2,"E-Home",false);
            this.selectAccess(3,"0",true);
            this.accessInputBox(2).clear();
            this.accessInputBox(2).sendKeys("124.115.21.16");
            //device id
            this.accessInputBox(4).clear();
            this.accessInputBox(4).sendKeys("164070392");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * select platform access method
     */
    private void selectAccess(int index, String optionValue, boolean isProtocol){
        String pathOfAccessMethod = "#advancedPlatform > div > div:nth-child("+index+") > span:nth-child(2) > select";
        String pathOfProtocolVersion = "#advancedPlatform > div > div:nth-child("+index+") > div:nth-child(1) > span:nth-child(2) > select";
        String path = isProtocol ? pathOfProtocolVersion : pathOfAccessMethod;
        WebElement accessMethod = driver.findElement(By.cssSelector(path));
        Select select = new Select(accessMethod);
        select.selectByValue(optionValue);
    }

    /**
     * Fill up input box under platform access tab
     * @param index the input box index
     * @return the web element
     */
    private WebElement accessInputBox(int index){
        WebElement inputBox = driver.findElement(By.cssSelector("#advancedPlatform > div > div:nth-child(3) > div:nth-child("+index+") > span:nth-child(2) > input "));
        return inputBox;
    }

    /**
     * Get update pwd
     * @return the web element
     */
    private WebElement getUpdatePwd(){
        WebElement updatePwd = driver.findElement(By.id("activePassword"));
        return updatePwd;
    }

    /**
     * Get confirm update pwd
     * @return the web element
     */
    private WebElement getConfirmUpdatePwd(){
        WebElement confirmUpdatePwd = driver.findElement(By.cssSelector("#active > div:nth-child(1) > div:nth-child(4) > span:nth-child(2) > input "));
        return confirmUpdatePwd;
    }

    /**
     * Get update button
     * @return the web element
     */
    private WebElement getUpdateButton(){
        WebElement updateButton = driver.findElement(By.xpath("//button[@class='aui_state_highlight' and @type='button']"));
        return updateButton;
    }

    /**
     * Get cancel button after update the password when first login
     * @return the web element
     */
    private WebElement getCancelButton(){
        WebElement cancelButton = driver.findElement(By.cssSelector(".aui_buttons > button:nth-child(2)"));
        log.info(cancelButton.getText());
        return cancelButton;
    }

    /**
     * Get config tab from the navigation bar which on the top of the page
     * @return
     */
    private WebElement getConfigTab(){
        WebElement configTab = driver.findElement(By.cssSelector("#nav > li:nth-child(5) > a"));
        return configTab;
    }

    /**
     * Get the overlay container when the user first login
     * @return the web element
     */
    private WebElement getOverlayContainer(){
        WebElement overlayContainer = driver.findElement(By.id("active"));
        return overlayContainer;
    }

    /**
     * Get username or password element on the main login page
     * @param id  the element id
     * @return the web element
     */
    private WebElement viaID(String id){
        WebElement loginInfo = driver.findElement(By.id(id));
        return loginInfo;
    }

    /**
     * Get login button on main login page
     * @return the web element
     */
    private WebElement getLoginButton(){
        WebElement loginButton = driver.findElement(By.cssSelector("#login > table > tbody > tr > td:nth-child(2) > div > div:nth-child(5) > button"));
        return loginButton;
    }

    /**
     * Get save button
     * @return the web element
     */
    private WebElement getSaveButton(String tabID){
        WebElement saveButton = driver.findElement(By.cssSelector("#"+tabID+" > button"));
        return saveButton;
    }

    /**
     * Get the menu tab on left menu navigation bar
     * @param index the tab index
     * @return the web element
     */
    private WebElement getMenuTab(int index){
        WebElement menuTab = driver.findElement(By.cssSelector("#menu > div > div:nth-child("+index+") > div "));
        return menuTab;
    }

    /**
     * Get IP input box
     * @param index the input box index
     * @return the web element
     */
    private WebElement getIPInputBox(int index){
        WebElement inputBox = driver.findElement(By.cssSelector("#basicTcpIp > div > div:nth-child("+ index +") > span:nth-child(2) > input"));
        return inputBox;
    }

    /**
     * Get the sub tab on left menu navigation bar
     * @param index the sub tab index
     * @return the web element
     */
    private WebElement getSubTab(int index, int subIndex){
        WebElement subTab = driver.findElement(By.cssSelector("#menu > div > div:nth-child("+index+") > div:nth-child("+subIndex+") "));
        return subTab;
    }

}
