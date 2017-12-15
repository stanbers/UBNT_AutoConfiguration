package UITesting.companyManagement.independently;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/15 9:36
 * @Email stanxu526@gmail.com
 */
public class UpdateCompanyTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void updateCompany(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        //these fields are for accept the parameters from excel
        String companyName = null;
        String detailOfCompany = null;
        String rowIndex = null;

        //the updated fields
        String appendDetailOfCompany = null;
        String appendCompanyName = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimestamp = sdf.format(new Date());
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_updateCompany", 15);
            if(parameterList != null && parameterList.size() > 0){
                companyName = parameterList.get(0);
                detailOfCompany = parameterList.get(1);
                rowIndex = parameterList.get(2);
            }else {
                log.info("can not get the parameters from excel !");
            }

            if(this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                this.getCompanyListElement().click();

                Thread.sleep(3000);

                //to make sure there is no repetition company info
                appendCompanyName = companyName + "_" + currentTimestamp;
                appendDetailOfCompany = detailOfCompany + currentTimestamp;

                this.getCompanyEditButton(rowIndex).click();

                this.getCompanyNameInputElement().clear();
                this.getDetailsOfCompanyElement().clear();

                this.getCompanyNameInputElement().sendKeys(appendCompanyName);
                this.getDetailsOfCompanyElement().sendKeys(appendDetailOfCompany);

                this.getSaveButton().click();
                //this.getCloseButton();
                Login.driver.switchTo().alert().accept();
                //even though sleep() method is not recommended , but I think it based on the actual situation
                Thread.sleep(3000);

            }else {
                log.info("can not open company_management accordion !");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        WebElement updatedCompanyNameElement = this.getCompanyInfo(rowIndex,3);
        WebElement updatedCompanyDetailElement = this.getCompanyInfo(rowIndex,4);
        if(updatedCompanyNameElement != null && updatedCompanyDetailElement != null){
            Assert.assertEquals(appendCompanyName, updatedCompanyNameElement.getText());
            Assert.assertEquals(appendDetailOfCompany, updatedCompanyDetailElement.getText());
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
     * Get company list sub element under company management tab
     * @return the WebElement
     */
    private WebElement getCompanyListElement(){
        WebElement companyList = Login.driver.findElement(By.cssSelector("#collapseOne > ul > li:nth-child(1) > a"));
        return companyList;
    }

    /**
     * Get update_company button
     * @param rowIndex  the company row index
     * @return the WebElement
     */
    private WebElement getCompanyEditButton(String rowIndex){
        WebElement updateCompanyButton = Login.driver.findElement(By.cssSelector("#com-listPage > tr:nth-child("+ rowIndex +") > td:nth-child(7) > button:nth-child(1)"));
        log.info(updateCompanyButton.getText());
        return updateCompanyButton;
    }

    /**
     * Get company name input element on update_company overlay
     * @return the WebElement
     */
    private WebElement getCompanyNameInputElement(){
        WebElement companyName = Login.driver.findElement(By.id("update_company_name"));
        return companyName;
    }

    /**
     * Get company details area element on update_company overlay
     * @return the WebElement
     */
    private WebElement getDetailsOfCompanyElement(){
        WebElement detailsOfCompany = Login.driver.findElement(By.id("update_company_desc"));
        return detailsOfCompany;
    }

    /**
     * Get save button on update_company overlay
     * @return the WebElement
     */
    private WebElement getSaveButton(){
        WebElement saveButton = Login.driver.findElement(By.id("com_update"));
        return saveButton;
    }

    /**
     * Get close button on update_company overlay
     * @return the WebElement
     */
    private WebElement getCloseButton(){
        WebElement closeButton = Login.driver.findElement(By.xpath("//button[@id='com_update']/preceding-sibling::button"));
        return closeButton;
    }

    /**
     * Get  company name and details on company table.
     * @param rowIndex   the company record row index
     * @param cellIndex  the company name/details cell index
     * @return the WebElement
     */
    private WebElement getCompanyInfo(String rowIndex, int cellIndex){
        WebElement updatedCompanyInfo = Login.driver.findElement(By.cssSelector("#com-listPage > tr:nth-child("+ rowIndex +") > td:nth-child("+ cellIndex +")"));
        return updatedCompanyInfo;
    }

}
