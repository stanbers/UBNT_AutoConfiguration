package functionalTesting.companyManagement.independently.stuffList;

import functionalTesting.companyManagement.independently.companyList.AddCompanyTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Login;
import utility.Constant;
import utility.ExcelUtils;

import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/18 17:01
 * @Email stanxu526@gmail.com
 */
public class RemoveStuffTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    private String rowIndex,targetStuff;
    private boolean isRemove;

    @Test
    public void removeStuff(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_removeStuff", 36);
            if(parameterList != null && parameterList.size() > 0){
                this.rowIndex = parameterList.get(0);
                this.isRemove = new Boolean(parameterList.get(1));
            }else {
                log.info("can not get the parameters from excel sheet !");
            }

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                Thread.sleep(2000);
                this.getStuffListElement().click();
                targetStuff = this.getStuffNameElement(this.rowIndex).getText();
                Thread.sleep(2000);
                this.getRemoveStuffButton(this.rowIndex).click();

                //remove stuff or not
                if(isRemove){
                    Login.driver.switchTo().alert().accept();
                    //there are 2 alert() while using firefox, here sleep 2s is good for the next js alert(), otherwise cannot handle the next alert()
                    Thread.sleep(2000);
                    Login.driver.switchTo().alert().accept();
                }else {
                    Login.driver.switchTo().alert().dismiss();
                }

                Thread.sleep(2000);
            }else {
                log.info("can not open company_management accordion !");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 1; i <= getStuffRecords().size(); i++) {
            log.info("stuffRecords: "+ getStuffRecords().size() + " / stuffName: " + getStuffNameElement(String.valueOf(i)).getText());
            Assert.assertTrue("the job already removed !", getStuffNameElement(String.valueOf(i)).getText() != targetStuff);
        }
    }

    /**
     * Get company management element on the left navigation list
     * @return the WebElement
     */
    private WebElement getCompanyManagementElement(){
        WebElement companyManagement = wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("#accordion > li:nth-child(2) > a")));
        return companyManagement;
    }

    /**
     * Get stuff list sub element under company management tab
     * @return the WebElement
     */
    private WebElement getStuffListElement(){
        WebElement stuffList = Login.driver.findElement(By.cssSelector("#collapseOne > ul:nth-child(1) > li:nth-child(4) > a"));
        log.info(stuffList.getText());
        return stuffList;
    }

    /**
     * Get 'remove_stuff' button from stuff record list table
     * @param rowIndex   the stuff record index
     * @return the WebElement
     */
    private WebElement getRemoveStuffButton(String rowIndex){
        WebElement editStuffButton = Login.driver.findElement(By.cssSelector("#userTbody > tr:nth-child("+rowIndex+") > td:nth-child(12) > button:nth-child(2)"));
        log.info(editStuffButton.getAttribute("type"));
        return editStuffButton;
    }

    /**
     * Get stuff name from stuff record list table
     * @param rowIndex   the stuff record index
     * @return the WebElement
     */
    private WebElement getStuffNameElement(String rowIndex){
        WebElement stuffName = Login.driver.findElement(By.cssSelector("#userTbody > tr:nth-child("+ rowIndex +") > td:nth-child(4)"));
        return stuffName;
    }

    /**
     * Get stuff records list from job list table
     * @return the WebElement
     */
    private List<WebElement> getStuffRecords(){
        List<WebElement> stuffRecords = Login.driver.findElements(By.cssSelector("#userTbody > tr"));
        return stuffRecords;
    }
}
