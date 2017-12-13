package utility;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import pageObject.Login;

import java.io.File;
import java.io.IOException;

/**
 * @Author by XuLiang
 * @Date 2017/12/13 15:49
 * @Email stanxu526@gmail.com
 */
public class ScreenCapture {
    private long ct = System.currentTimeMillis();
    private final String ctString = String.valueOf(ct);

    /**
     * Trying to capture every single step
     */
    public void stepCapture(String caseName){
        File srcFile = ((TakesScreenshot) Login.driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile,new File("D:\\output\\"+ caseName + "\\" + ctString +".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
