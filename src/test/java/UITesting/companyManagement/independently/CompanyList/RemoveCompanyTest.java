package UITesting.companyManagement.independently.CompanyList;

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
 * @Date 2017/12/15 17:15
 * @Email stanxu526@gmail.com
 */
public class RemoveCompanyTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    @Test
    public void removeCompany(){
        Login.login("http://10.103.0.4:8080/web/user/login");

        boolean isRemove = false;
        String rowIndex = null;
        String targetCompany = null;

        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_removeCompany", 18);

            if(parameterList != null && parameterList.size() >0){
                rowIndex = parameterList.get(0);
                isRemove = new Boolean(parameterList.get(1));
            }else {
                log.info("can not get the parameters from excel !");
            }

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                this.getCompanyListElement().click();

                Thread.sleep(3000);
                targetCompany = this.getCompanyInfo(rowIndex).getText();
                this.getRemoveCompanyButton(rowIndex).click();
                //remove company or not
                if(isRemove){
                    Login.driver.switchTo().alert().accept();
                    //there are 2 alert() while using firefox, here sleep 2s is good for the next js alert(), otherwise it will hit error.
                    Thread.sleep(2000);
                    Login.driver.switchTo().alert().accept();
                }else {
                    Login.driver.switchTo().alert().dismiss();
                }

                Thread.sleep(3000);
            }else {
                log.info("can not open company_management accordion !");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 1; i <= getCompanyRows().size(); i++) {
            log.info(getCompanyRows().size());
            Assert.assertTrue("the company already removed !", getCompanyInfo(String.valueOf(i)).getText() != targetCompany);
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
     * Get remove_company button
     * @param rowIndex  the company row index
     * @return the WebElement
     */
    private WebElement getRemoveCompanyButton(String rowIndex){
        WebElement removeCompanyButton = Login.driver.findElement(By.cssSelector("#com-listPage > tr:nth-child("+ rowIndex +") > td:nth-child(7) > button:nth-child(2)"));
        log.info(removeCompanyButton.getText());
        return removeCompanyButton;
    }

    /**
     * Get company name and details on company table.
     * @param rowIndex   the company record row index
     * @return the WebElement
     */
    private WebElement getCompanyInfo(String rowIndex){
        WebElement updatedCompanyInfo = Login.driver.findElement(By.cssSelector("#com-listPage > tr:nth-child("+ rowIndex +") > td:nth-child(3)"));
        log.info(updatedCompanyInfo.getText());
        return updatedCompanyInfo;
    }

    /**
     * Get company records on company table
     * @return company list
     */
    private List<WebElement> getCompanyRows(){
        List<WebElement> rows = Login.driver.findElements(By.cssSelector("#com-listPage > tr"));
        return rows;
    }
}
