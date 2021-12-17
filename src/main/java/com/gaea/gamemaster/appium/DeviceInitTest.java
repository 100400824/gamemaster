package com.gaea.gamemaster.appium;

import com.gaea.gamemaster.publicTool.ExcelTest;
import com.gaea.gamemaster.publicTool.FileManage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.imagecomparison.SimilarityMatchingOptions;
import io.appium.java_client.imagecomparison.SimilarityMatchingResult;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.xml.bind.SchemaOutputResolver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;

public class DeviceInitTest {

    private static String[] dveiceMessage;

    public static void main(String[] args) throws Exception {

        AppiumDriver<WebElement> driver = initDriver();

/*        for (int i = 1; i <= 1000; i++) {
            getScreenImage(driver);
        }*/

    }

    public static void testImageDiff(AppiumDriver driver) throws Exception {
        SimilarityMatchingOptions opts = new SimilarityMatchingOptions();
        opts.withEnabledVisualization();
        File newBaseline = driver.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(newBaseline, new File(getImageXpath("learn1")));
        Thread.sleep(1200);
        File file3 = new File(getImageXpath("learn"));
        File file4 = new File(getImageXpath("learn1"));
        SimilarityMatchingResult res = driver.getImagesSimilarity(file3,file4,opts);
        System.out.println("res.getScore()"+res.getScore());
        double score = 0.998;
        if (res.getScore() > score) {
            res.storeVisualization(new File(getImageXpath("res")));
        }
    }

    public static void testImageFind(){
        /*   Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@text=\"同意\"]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@text=\"开始使用\"]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@text=\"始终允许\"]")).click();
        Thread.sleep(2000);
//        driver.findElement(MobileBy.id("com.gonlan.iplaymtg:id/main_lab_tv")).click();
        driver.findElement(MobileBy.image(getImageAsBase64("test"))).click();
        Thread.sleep(3000);
        WebElement webElement = driver.findElement(MobileBy.image(getImageAsBase64("4444")));
        System.out.println("webElement.getSize()" + webElement.getSize());
        System.out.println("webElement.getLocation()" + webElement.getLocation());
        System.out.println("webElement.getRect()" + webElement.getRect().x);
        System.out.println("webElement.isDisplayed()" + webElement.isDisplayed());
        webElement.click();*/

    }

    public static void getScreenImage(AppiumDriver<WebElement> driver) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("输入当前屏幕截图名称：");
        String screenName = sc.next();
        File newBaseline = driver.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(newBaseline, new File(getImageXpath(screenName)));
    }


    public static String getImageXpath(String imageName) {
        return System.getProperty("user.dir") + "\\imageData\\" + imageName + ".png";
    }

    protected static String getImageAsBase64(String imageName) throws Exception {
        String xpath = System.getProperty("user.dir") + "\\img\\" + imageName + ".png";
        System.out.println(xpath);
        File file = new File(xpath);
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }

    public static AppiumDriver<WebElement> initDriver() throws Exception {

        System.out.println("初始化appium服务器V1.0.1。。。");

        String sheetName = "deviceManager";
        getDeviceValue(sheetName, FileManage.dzxyCasePath);
        String dveiceID = dveiceMessage[1];
        String apkPath = dveiceMessage[2];
        String packgeName = dveiceMessage[3];
        String avtivetyName = dveiceMessage[4];

        File app = new File(apkPath);
        DesiredCapabilities capabilities = new DesiredCapabilities();

        //放开注释重新安装APP
//        capabilities.setCapability(MobileCapabilityType.APP, app.getAbsoluteFile());

        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, dveiceID);
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, packgeName);
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, avtivetyName);
        capabilities.setCapability("newCommandTimeout", "2000");//设置连接超时时间
        capabilities.setCapability("unicodeKeyboard", true);//使用 Unicode 输入法
        capabilities.setCapability("resetKeyboard", true);  //重置输入法到原有状态
        return new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

    }

    private static void getDeviceValue(String sheetName, String casePath) throws Exception {

        dveiceMessage = ExcelTest.getCell(casePath, sheetName, 2);
    }


}
