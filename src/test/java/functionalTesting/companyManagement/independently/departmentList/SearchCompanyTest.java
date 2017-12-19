package functionalTesting.companyManagement.independently.departmentList;

import functionalTesting.companyManagement.independently.companyList.AddCompanyTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Login;
import utility.Constant;
import utility.ExcelUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author by XuLiang
 * @Date 2017/12/18 17:28
 * @Email stanxu526@gmail.com
 */
public class SearchCompanyTest {
    static {
        System.setProperty("webdriver.gecko.driver","C:\\SeleniumGecko\\geckodriver.exe");
    }

    private final static Log log = LogFactory.getLog(AddCompanyTest.class);

    private final static WebDriverWait wait = new WebDriverWait(Login.driver, 10);

    private String companyName;
    @Test
    public void searchCompany(){
        Login.login("http://10.103.0.4:8080/web/user/login");
        try {
            ExcelUtils.setExcelFile(Constant.Path_TestData, Constant.File_TestData);
            //This is to get the values from Excel sheet
            List<String> parameterList = ExcelUtils.getParametersViaCaseName("TrainScheduling_ltrailways_searchCompany", 39);
            if (parameterList != null && parameterList.size() > 0){
                this.companyName = parameterList.get(0);

            }else {
                log.info("can not read parameters from excel sheet !");
            }

            if (this.getCompanyManagementElement() != null){
                this.getCompanyManagementElement().click();
                Thread.sleep(2000);
                this.getDepartmentListElement().click();
                Thread.sleep(2000);

                this.selectCompany(companyName);

                this.getSearchButton().click();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        List<WebElement> companyNames = this.getListOutCompanyRecords();
        if (companyNames != null && companyNames.size() > 0){
            for (int i = 0; i < companyNames.size(); i++) {
                log.info(companyNames.get(i));
                Assert.assertEquals(companyName,companyNames.get(i));
            }
        }else {
            log.info("there is no company name called: " + companyName);
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
     * Get department list sub element under company management tab
     * @return the WebElement
     */
    private WebElement getDepartmentListElement(){
        WebElement companyList = Login.driver.findElement(By.cssSelector("#collapseOne > ul > li:nth-child(2) > a"));
        return companyList;
    }

    /**
     * select a company from company select dropdown list on company records list table
     */
    private void selectCompany(String company){
        Select companyList = new Select(Login.driver.findElement(By.id("search-department-company-select")));
        if(companyList != null){
            companyList.selectByValue(company);
            log.info(company);
        }else {
            log.info("there is no any company can be selected !");
        }
    }

    /**
     * Get search_company button on update_company overlay
     * @return the WebElement
     */
    private WebElement getSearchButton(){
        WebElement closeButton = Login.driver.findElement(By.xpath("//select[@id='search-department-company-select']/following-sibling::button"));
        return closeButton;
    }

    /**
     * Get list out company names after click search_company button
     * @return  the company names list
     */
    private List<WebElement> getListOutCompanyRecords(){
        List<WebElement> companyRecords = Login.driver.findElements(By.cssSelector("#departmentTbody > tr"));
        List<WebElement> companyNames = new ArrayList<WebElement>();
        for (int i = 0; i < companyRecords.size(); i++) {
            WebElement companyName = Login.driver.findElement(By.cssSelector("#departmentTbody > tr:nth-child(" + i + ") > td:nth-child(4)"));
            companyNames.add(companyName);
        }
        return companyNames;
    }

}
