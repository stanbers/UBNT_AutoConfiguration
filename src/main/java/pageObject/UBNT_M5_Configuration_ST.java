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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class UBNT_M5_Configuration_ST {

    static{
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(UBNT_M5_Configuration_ST.class);

    private final static String CASE_NAME = "UBNT_configuration_M5_ST";

    //private final  static WebDriverWait wait = new WebDriverWait(driver,10);

    private String username,password,country,language,tabName,wirelessMode,SSID,chanelWidth,frequency,antennaGain,
            outputPower,newPassword,tabName2,IPAddressName,netmask,gatewayIP,tabName3;
    private boolean isFirstTimeLogin = true;

    public void login(String url){

        driver.get(url);
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData_UBNT_M5_ST,Constant.File_TestData);
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
                tabName3 = parameterList.get(17);

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

                //navigate to WIRELESS tab
                this.getNavigationTab(tabName.charAt(0)).click();
                //to make sure has navigated to WIRELESS
                Thread.sleep(3000);
                this.selectWirelessMode(wirelessMode);

                this.getWDSElement().click();

                if(this.getSelectSSIDButton() != null){
                    log.info(this.getSelectSSIDButton().getAttribute("value"));
                    this.getSelectSSIDButton().click();
                }

                //handle switch to the popup window
                String currentWindow = driver.getWindowHandle(); // Store your parent window
                String subWindowHandler = null;

                Set<String> handles = driver.getWindowHandles(); // get all window handles
                Iterator<String> iterator = handles.iterator();
                while (iterator.hasNext()){
                    subWindowHandler = iterator.next();
                }
                driver.switchTo().window(subWindowHandler); // switch to popup window
                Thread.sleep(20000); // waiting for the popup window load completed.

                if (this.getLockUpButton() != null){
                    log.info("switch to popup window successfully !");

                    int recordsNum = this.getSurveyRecords().size();

                    //this list are full with SSID records
                    List<String> SSIDRecords = new ArrayList<String>();
                    for (int i = 0; i < recordsNum ; i++) {
                            i++;
                            WebElement SSIDCell = driver.findElement(By.cssSelector("#survey > tbody > tr:nth-child("+i+") > td:nth-child(3)"));
                            SSIDRecords.add(SSIDCell.getText());
                            i--;
                    }
                    for (int j = SSIDRecords.size()-1; j < SSIDRecords.size() ; j--) {
                        //SSID should be get from AP side, that means must config AP site
                        //then the SSID can be write into ST data source excel
                        //then read SSID from ST data source excel
                        String record = SSIDRecords.get(j);
                        if ((record.toString().trim()).equals(SSID.toString().trim())){
                            j++;
                            WebElement targetRecordRadio = driver.findElement(By.cssSelector("#survey > tbody > tr:nth-child("+j+") > td:nth-child(1) > input"));
                            targetRecordRadio.click();
                            break;
                        }
                    }

                    this.getLockUpButton().click();
                   // Thread.sleep(1000);
                }
                driver.switchTo().window(currentWindow);  // switch back to parent window

                this.getWDSElement();


                this.selectChanelWidth(chanelWidth);

                this.getOutputPowerElement().clear();
                this.getOutputPowerElement().sendKeys(outputPower);
                this.getChangeButton().click();

                //navigate to NETWORK tab
                if (this.getNavigationTab(tabName2.charAt(0)) != null){
                    this.getNavigationTab(tabName2.charAt(0)).click();
                    Thread.sleep(3000);
                }

                //update the following fields
                this.getIPAddressInputElement().clear();
                this.getNetmaskInputElement().clear();
                this.getGatewayIPInputElement().clear();
                this.getIPAddressInputElement().sendKeys(IPAddressName);
                this.getNetmaskInputElement().sendKeys(netmask);
                this.getGatewayIPInputElement().sendKeys(gatewayIP);

                if (this.getFinalChangeButton() != null){
                    this.getFinalChangeButton().click();
                    Thread.sleep(2000);
                    this.getApplyButton().click();
                    log.info("all configuration have been completed !");
                }else {
                    log.info("something wrong with the configuration, pls have a check !");
                }
                Thread.sleep(10000);
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
//        WebElement navigationTab = driver.findElement(By.xpath("//img[@alt='"+ NavigationTabName +"']/parent::a[1]"));
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
        log.info(WDS.getAttribute("name"));
        return WDS;
    }

    /**
     * Get 'select'button beside of SSID input element
     * @return the WebElement
     */
    private WebElement getSelectSSIDButton(){
        WebElement selectButton = driver.findElement(By.cssSelector("input[value='Select...'][type='button']"));
        return selectButton;
    }

    /**
     * Get lockup button on the popup window
     * @return the WebElement
     */
    private WebElement getLockUpButton(){
        WebElement lockupButton = driver.findElement(By.id("lock_btn"));
        return lockupButton;
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
        Select chanelWidthList = new Select(driver.findElement(By.id("clksel_select")));
        if(chanelWidthList != null){
            chanelWidthList.selectByVisibleText(chanelWidth);
            log.info(chanelWidth + " was selected !");
        }else {
            log.info("there is no any chanel width can be selected !");
        }
    }

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
     * Get output power element under WIRELESS tab
     * @return the WebElement
     */
    private WebElement getOutputPowerElement(){
        WebElement outputPower = driver.findElement(By.id("txpower"));
        log.info("output power is: " + this.outputPower);
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
        log.info("the original password is: " + this.password);
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
        log.info("the new password has changed to " + this.newPassword);
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

    /**
     * Get apply button after click change button
     * @return the WebElement
     */
    private WebElement getApplyButton(){
        WebElement applyButton = driver.findElement(By.id("apply_button"));
        log.info(applyButton.getAttribute("value"));
        return applyButton;
    }

    /**
     * Get SSID records on survey popup window
     * @return the SSID set
     */
    private List<WebElement> getSurveyRecords(){
        List<WebElement> list = driver.findElements(By.cssSelector("#survey > tbody > tr"));
        return list;
    }
}