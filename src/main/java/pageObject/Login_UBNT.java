package pageObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Constant;
import utility.ExcelUtils;
import utility.WebDriverGiver;

import java.util.List;

public class Login_UBNT {

    static{
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(Login_UBNT.class);

    private final static String CASE_NAME = "UBNT_configuration";

    private String username,password,country,language,tabName,wirelessMode,SSID,chanelWidth,frequency,antennaGain,
            outputPower,newPassword,tabName2,IPAddressName,netmask,gatewayIP;
    private boolean isFirstTimeLogin = true;

    public void login(String url){

        driver.get(url);
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData_UBNT,Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName(CASE_NAME, 0);

            if (parameterList != null && parameterList.size() > 0){
                username = parameterList.get(0);
                password = parameterList.get(1);
                country = parameterList.get(2);
                language = parameterList.get(3);
                isFirstTimeLogin = new Boolean(parameterList.get(4));
                tabName = parameterList.get(5);
                wirelessMode = parameterList.get(6);
                SSID = parameterList.get(7);
                chanelWidth = parameterList.get(8);
                frequency = parameterList.get(9);
                antennaGain = parameterList.get(10);
                outputPower = parameterList.get(11);
                newPassword = parameterList.get(12);
                tabName2 = parameterList.get(13);
                IPAddressName = parameterList.get(14);
                netmask = parameterList.get(15);
                gatewayIP = parameterList.get(16);

                this.getUsername().sendKeys(username);
                this.getPassword().sendKeys(password);
                if(isFirstTimeLogin){
                    this.selectCountry(country);
                    this.selectLanguage(language);
                    this.getAgreedCheckbox().click();
                }

                this.getLoginButton().click();

                WebDriverWait wait = new WebDriverWait(driver, 10);
                WebElement logoElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[value='Logout'][type='button']")));

                //assert login successfully
                log.info("get the logout tag name: " + logoElement.getTagName());
                //Assert.assertEquals("input",logoElement.getTagName());
                this.getNavigationTab(tabName.charAt(0)).click();
                this.selectWirelessMode(wirelessMode);
                this.getWDSElement().click();

                this.getSSIDElement().clear();
                this.getSSIDElement().sendKeys(SSID);

                this.selectChanelWidth(chanelWidth);
                this.selectFrequency(frequency);

                this.getAntennaGainElement().clear();
                this.getOutputPowerElement().clear();
                this.getAntennaGainElement().sendKeys(antennaGain);
                this.getOutputPowerElement().sendKeys(outputPower);
                this.getChangeButton().click();

                //change password
                this.getCurrentPassword().sendKeys(password);
                this.getNewPassword(false).sendKeys(newPassword);
                this.getNewPassword(true).sendKeys(newPassword);

                //click change button on 'Change Password' overlay
                this.getChangeButtonOnOverlay().click();
                this.getApplyConfigurationButton().click();

                //navigate to NETWORK tab
                this.getNavigationTab(tabName2.charAt(0)).click();

                //update the following fields
                this.getIPAddressInputElement().clear();
                this.getNetmaskInputElement().clear();
                this.getGatewayIPInputElement().clear();
                this.getIPAddressInputElement().sendKeys(IPAddressName);
                this.getNetmaskInputElement().sendKeys(netmask);
                this.getGatewayIPInputElement().sendKeys(gatewayIP);

                this.getFinalChangeButton().click();

            }
//            driver.quit();
        } catch (Exception e) {
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
    private void selectCountry(String country){
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
    private void selectLanguage(String language){
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
    private WebElement getAgreedCheckbox(){
        return driver.findElement(By.id("agreed"));
    }

    /**
     * Get Login button element on login page
     * @return the WebElement
     */
    private WebElement getLoginButton(){
        return driver.findElement(By.xpath("//input[@value='Login' and @type='submit']"));
    }

    /**
     * Get navigation tab on homepage
     * @param tabNamePrefix   the navigation tab name
     * @return the WebElement
     */
    private WebElement getNavigationTab(char tabNamePrefix){
        String NavigationTabName = null;
        switch (tabNamePrefix){
            case 'W': NavigationTabName = "Wireless";
                      log.info("navigated to "+NavigationTabName);
                            break;
            case 'N': NavigationTabName = "Network";
                      log.info("navigated to "+NavigationTabName);
                            break;
            case 'U': NavigationTabName = "UBNT";
                      log.info("navigated to "+NavigationTabName);
                            break;
            default : NavigationTabName = null;
                      log.info("navigated to "+NavigationTabName);
                            break;
        }
        WebElement navigationTab = driver.findElement(By.xpath("//img[@alt='"+ NavigationTabName +"']"));
        return navigationTab;
    }

    /**
     * select a wireless mode under WIRELESS tab
     * @param wirelessMode  the wireless mode
     */
    private void selectWirelessMode(String wirelessMode){
        Select wirelessModeList = new Select(driver.findElement(By.id("wmode")));
        if(wirelessModeList != null){
            wirelessModeList.selectByValue(wirelessMode);
            log.info(wirelessMode + " was selected !");
        }else {
            log.info("there is no any wireless mode can be selected !");
        }
    }

    /**
     * Get WDS(Transparent Bridge Mode) element under WIRELESS tab
     * @return the WebElement
     */
    private WebElement getWDSElement(){
        WebElement WDS = driver.findElement(By.id("wds_chkbox"));
        log.info("WDS was enabled !");
        return WDS;
    }

    /**
     * Get SSID element under WIRELESS tab
     * @return the WebElement
     */
    private WebElement getSSIDElement(){
        WebElement SSID = driver.findElement(By.id("essid"));
        log.info("SSID is " + this.SSID);
        return SSID;
    }

    /**
     * select a chanel width under WIRELESS tab
     * @param chanelWidth  the chanel width
     */
    private void selectChanelWidth(String chanelWidth){
        Select chanelWidthList = new Select(driver.findElement(By.id("chanbw_select")));
        if(chanelWidthList != null){
            chanelWidthList.selectByValue(chanelWidth);
            log.info(chanelWidth + " was selected !");
        }else {
            log.info("there is no any chanel width can be selected !");
        }
    }

    //chan_freq

    /**
     * select frequency under WIRELESS tab
     * @param frequency  the frequency
     */
    private void selectFrequency(String frequency){
        Select frequencyList = new Select(driver.findElement(By.id("chan_freq")));
        if(frequencyList != null){
            frequencyList.selectByValue(frequency);
            log.info(frequency + " was selected !");
        }else {
            log.info("there is no any frequency can be selected !");
        }
    }

    /**
     * Get antenna gain element under WIRELESS tab
     * @return the WebElement
     */
    private WebElement getAntennaGainElement(){
        WebElement antennaGain = driver.findElement(By.id("antenna_info"));
        log.info("anntenna gain is: "+antennaGain.getText());
        return antennaGain;
    }

    /**
     * Get output power element under WIRELESS tab
     * @return the WebElement
     */
    private WebElement getOutputPowerElement(){
        WebElement outputPower = driver.findElement(By.id("txpower"));
        log.info("output power is: " + outputPower.getText());
        return outputPower;
    }

    /**
     * Get change button element under WIRELESS tab
     * @return the WebElement
     */
    private WebElement getChangeButton(){
        WebElement changeButton = driver.findElement(By.cssSelector("input[value='Change'][type='submit']"));
        log.info("change button was clicked !");
        return changeButton;
    }

    /**
     * Get current password input element on 'Change_Password' overlay after 'change' button clicked
     * @return the WebElement
     */
    private WebElement getCurrentPassword(){
        WebElement currentPassword = driver.findElement(By.id("dlgOldPassword"));
        log.info("the original password is: " + currentPassword.getText());
        return currentPassword;
    }

    /**
     * Get new password and confirm password input element on 'Change_Password' overlay after 'change' button clicked
     * @param isConfirm  switch to confirm password input or not
     * @return the WebElement
     */
    private WebElement getNewPassword(boolean isConfirm){
        String newPasswordId = !isConfirm ? "dlgNewPassword" : "dlgNewPassword2";
        WebElement newPassword = driver.findElement(By.id(newPasswordId));
        log.info("the new password has changed to " + newPassword.getText());
        return newPassword;
    }

    /**
     * Get change button on 'Change_Password' overlay after 'change' button clicked
     * @return the WebElement
     */
    private WebElement getChangeButtonOnOverlay(){
        WebElement changeButton = driver.findElement(By.xpath("//div[@id='warning-dlg']/following-sibling::div[1]/div[3]/div[1]/button[2]"));
        log.info(changeButton.getAttribute("type"));
        return changeButton;
    }

    /**
     * Get apply button on the top the page after changed password
     * @return the WebElement
     */
    private WebElement getApplyConfigurationButton(){
        WebElement applyButton = driver.findElement(By.id("apply_button"));
        return applyButton;
    }

    /**
     * Get IP address element under NETWORK tab
     * @return the WebElement
     */
    private WebElement getIPAddressInputElement(){
        WebElement IPAddress = driver.findElement(By.id("mgmtIpAddr"));
        log.info(IPAddress.getAttribute("name"));
        return IPAddress;
    }

    /**
     * Get netmask input element under NETWORK tab
     * @return the WebElement
     */
    private WebElement getNetmaskInputElement(){
        WebElement netmask = driver.findElement(By.id("mgmtIpNetmask"));
        log.info(netmask.getAttribute("name"));
        return netmask;
    }

    /**
     * Get gateway IP input element under NETWORK tab
     * @return the WebElement
     */
    private WebElement getGatewayIPInputElement(){
        WebElement gatewayIP = driver.findElement(By.id("mgmtGateway"));
        log.info(gatewayIP.getAttribute("name"));
        return gatewayIP;
    }

    /**
     * Get change button under NETWORK tab
     * @return the WebElement
     */
    private WebElement getFinalChangeButton(){
        WebElement changeButton = driver.findElement(By.id("change"));
        log.info(changeButton.getAttribute("value"));
        return changeButton;
    }
}