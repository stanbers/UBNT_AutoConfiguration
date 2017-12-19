package pageObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utility.Constant;
import utility.ExcelUtils;
import utility.ScreenCapture;
import utility.WebDriverGiver;

import java.util.List;

public class Login_UBNT {

    static{
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    public static WebDriver driver = WebDriverGiver.getWebDriver();

    private final static Log log = LogFactory.getLog(Login_UBNT.class);

    private final static String CASE_NAME = "UBNT_configuration";

    private String username,password,country,language;
    private boolean isFirstTimeLogin = true;

    public void login(String url){

        driver.get(url);
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData,Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName(CASE_NAME, 0);

            if (parameterList != null && parameterList.size() > 0){
                username = parameterList.get(0);
                password = parameterList.get(1);
                country = parameterList.get(2);
                language = parameterList.get(3);
                isFirstTimeLogin = new Boolean(parameterList.get(4));

                this.getUsername().sendKeys(username);
                this.getPassword().sendKeys(password);
                if(isFirstTimeLogin){
                    this.selectCountry(country);
                    this.selectLanguage(language);
                    this.getAgreedCheckbox().click();
                }
                new ScreenCapture().stepCapture(CASE_NAME);

                this.getLoginButton().click();

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
        return driver.findElement(By.name("name"));
    }

    /**
     * Get password element on login page.
     * @return the WebElement
     */
    private static WebElement getPassword(){
        return driver.findElement(By.name("password"));
    }

    /**
     * Select a country from country select dropdown list on login page
     */
    private void selectCountry(String country){
        Select companyList = new Select(Login.driver.findElement(By.id("country")));
        if(companyList != null){
            companyList.selectByValue(country);
            log.info(country + " was selected !");
        }else {
            log.info("there is no any countries can be selected !");
        }
    }

    /**
     * Select a language from country select dropdown list on login page
     */
    private void selectLanguage(String language){
        Select languageList = new Select(Login.driver.findElement(By.id("ui_language")));
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
    private static WebElement getLoginButton(){
        return driver.findElement(By.cssSelector("input[type='submit'][value='Login']"));
    }

}