package com.gaea.gamemaster.mobile;

import com.gaea.gamemaster.publicTool.Loginfo;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.imagecomparison.SimilarityMatchingOptions;
import io.appium.java_client.imagecomparison.SimilarityMatchingResult;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


public class AppiumManager {

    private static String getText1, getText2, sendKeysStr;

    public static void main(String[] args) {

    }

    public static void appiumManagement(AppiumDriver<WebElement> driver, String caseIndex, String infoValue, String position, String positionValue, String operation, String operationValue, FileWriter pfp, TakesScreenshot drivername) throws Exception {
        WebElement element = null;
        String cmdstr;

        switch (position) {

            case "sleep":
                Thread.sleep(Integer.parseInt(positionValue));
                break;

            case "swipe":
                String swipeMethod = "up,down,left,right";
                int times = 2000;

                if (swipeMethod.contains(positionValue)) {

                    int x = driver.manage().window().getSize().getWidth() / 4;
                    int y = driver.manage().window().getSize().getHeight() / 4;

                    switch (positionValue) {

                        case "down":
                            swipeTo(driver,x * 2, y * 3, x * 2, y, times);
                            break;
                        case "up":
                            swipeTo(driver,x * 2, y, x * 2, y * 3, times);
                            break;
                        case "left":
                            swipeTo(driver,x, y * 2, x * 3, y * 2, times);
                            break;
                        case "right":
                            swipeTo(driver,x * 3, y * 2, x, y * 2, times);
                            break;
                        default:
                            System.out.println(positionValue + "------输入不符合标准-------");
                            break;
                    }

                } else {

                    String[] swipeInfo = positionValue.split(",");

                    try {
                        swipeTo(driver,Integer.parseInt(swipeInfo[0]), Integer.parseInt(swipeInfo[1]), Integer.parseInt(swipeInfo[2]), Integer.parseInt(swipeInfo[3]), Integer.parseInt(swipeInfo[4]));
                    } catch (Exception ignored) {
                    }
                }
                break;

            case "assert":
                if (positionValue.contains("sendkeys")) {
                    sendKeysStr = positionValue.replace("sendkeys", "") + sendKeysStr;
                    Loginfo.checkInfo("equals", sendKeysStr, getText1, infoValue, pfp, drivername);

                } else if (positionValue.equals("getText")) {
                    Loginfo.checkInfo("equals", getText1, getText2, infoValue, pfp, drivername);

                } else {
                    Loginfo.checkInfo("equals", positionValue, getText1, infoValue, pfp, drivername);
                }
                break;

            case "assertContains":
                Loginfo.checkInfo("contains", getText1, positionValue, infoValue, pfp, drivername);
                break;

            case "assertImage":

                SimilarityMatchingOptions opts = new SimilarityMatchingOptions();
                opts.withEnabledVisualization();
                File newBaseline = driver.getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(newBaseline, new File(getScreenImageXpath(positionValue + "-截屏")));
                Thread.sleep(500);
                File file1 = new File(getImageXpath(positionValue));
                File file2 = new File(getScreenImageXpath(positionValue + "-截屏"));
                SimilarityMatchingResult res = driver.getImagesSimilarity(file1, file2, opts);
//                System.out.println("res.getScore()" + res.getScore());
                double score = 0.96;
                if (res.getScore() < score) {
                    res.storeVisualization(new File(getResultImageXpath(positionValue + "-比对不一致" + res.getScore())));
                }

                break;

            case "tapPoint":
                String xStr = (positionValue.split(","))[0];
                String yStr = (positionValue.split(","))[1];

                int xPoint = 1, yPoint = 1;
                try {
                    xPoint = Integer.parseInt(xStr);
                    yPoint = Integer.parseInt(yStr);
                } catch (Exception ignored) {
                }

                double xPercent = 1, yPercent = 1;
                try {
                    xPercent = Double.parseDouble(xStr);
                    yPercent = Double.parseDouble(yStr);
                } catch (Exception ignored) {
                }

                if (xPercent < 1 && yPercent < 1) {
                    int x = driver.manage().window().getSize().getWidth();
                    int y = driver.manage().window().getSize().getHeight();
                    xPoint = (int) (x * xPercent);
                    yPoint = (int) (y * yPercent);
                }

//                (new TouchAction(driver)).tap(xPoint, yPoint).perform();
                Thread.sleep(500);
                break;

            case "tryXpathClick":
                int index = 1;
                while (index == 1) {
                    try {
                        driver.findElement(By.xpath(positionValue)).click();
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        index++;
                    }
                }
                break;

            case "back":
                cmdstr = "adb shell input keyevent 4";
                Runtime.getRuntime().exec(cmdstr).waitFor();
                break;

            case "home":
                cmdstr = "adb shell input keyevent 3";
                Runtime.getRuntime().exec(cmdstr).waitFor();
                break;

            case "enter":
                cmdstr = "adb shell input keyevent 66";
                Runtime.getRuntime().exec(cmdstr).waitFor();
                break;

            default:

                switch (position) {
                    case "image":
                        element = getElementWait(driver, MobileBy.image(getImageAsBase64(positionValue)));
                        break;

                    case "images":
                        List elements;
                        elements = driver.findElements(MobileBy.image(getImageAsBase64(positionValue)));
                        System.out.println("elementList:" + elements);
                        System.out.println(elements.size());
                        break;

                    case "id":
                        element = getElementWait(driver, By.id(positionValue));
                        break;

                    case "noId":
                        try {
                            element = driver.findElement(By.id(positionValue));
                            Loginfo.checkInfo("equals", "元素不存在", "元素仍然存在", infoValue, pfp, drivername);
                        } catch (Exception e) {
                            element = getElementWait(driver, By.xpath("//*"));
                        }
                        break;

                    case "className":
                        element = getElementWait(driver, By.className(positionValue));
                        break;

                    case "xpath":
                        element = getElementWait(driver, By.xpath(positionValue));
                        break;

                    case "xpathLast":
                        elements = getElementsWait(driver, By.xpath(positionValue));
                        element = (WebElement) elements.get(elements.size() - 1);
                        break;

                    case "toast":
                        element = getElementWait(driver, By.xpath("//*[@class='android.widget.Toast']"));
                        break;

                    default:
                        element = getElementWait(driver, By.linkText(positionValue));
                        break;
                }

                switch (operation) {

                    case "click":
                        element.click();
                        Thread.sleep(2000);
                        break;

                    case "longPress":
                        TouchAction ta = new TouchAction(driver);
//                        ta.longPress(element, Integer.parseInt(operationValue)).release().perform();
                        Thread.sleep(500);
                        break;

                    case "sendkeys":
                        String gaeaUUID = "gaeaUUID";
                        String uuid = UUID.randomUUID().toString().replace("-", "").substring(1, 11);
                        if (operationValue.contains(gaeaUUID)) {
                            sendKeysStr = operationValue.replace(gaeaUUID, "") + uuid;
                            element.sendKeys(sendKeysStr);
                        } else {
                            element.sendKeys(operationValue);
                        }
                        Thread.sleep(500);
                        break;

                    case "getText":
                        getText1 = element.getText().replaceAll("[\r\n]", "");
                        break;

                    case "getText2":
                        getText2 = element.getText().replaceAll("[\r\n]", "");
                        break;

                    case "getTextSpace":
                        getText1 = element.getText().replaceAll("[\r\n]", "").replace(" ", "");
                        break;

                    case "clear":
                        element.clear();
                        break;
                }
                break;
        }

        Loginfo.printLog(caseIndex, infoValue, pfp);
    }

    //间隔500ms查询一次元素信息
    private static WebElement getElementWait(AppiumDriver driver, By by) throws Exception {
        for (int i = 1; i < 60; i++) {
            try {
                return driver.findElement(by);
            } catch (Exception e) {
                Thread.sleep(100);
                i++;
            }
        }
        return driver.findElement(by);
    }

    //间隔500ms查询一次元素信息
    private static List getElementsWait(AppiumDriver<WebElement> driver, By by) throws Exception {
        for (int i = 1; i < 60; i++) {
            try {
                return driver.findElements(by);
            } catch (Exception e) {
                Thread.sleep(100);
                i++;
            }
        }

        return driver.findElements(by);
    }

    protected static String getImageAsBase64(String imageName) throws Exception {
        String xpath = System.getProperty("user.dir") + "\\imageData\\" + imageName + ".png";
        File file = new File(xpath);
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }

    public static String getImageXpath(String imageName) {
        return System.getProperty("user.dir") + "\\imageData\\" + imageName + ".png";
    }

    public static String getScreenImageXpath(String imageName) {
        return System.getProperty("user.dir") + "\\screenImg\\" + imageName + ".png";
    }

    public static String getResultImageXpath(String imageName) {
        return System.getProperty("user.dir") + "\\resultImg\\" + imageName + ".png";
    }

    public static void swipeTo(AppiumDriver<WebElement> driver,int x1,int y1,int x2,int y2,int time) {
        Duration duration= Duration.ofSeconds(1);
        new TouchAction(driver).press(PointOption.point(x1, y1)).waitAction(WaitOptions.waitOptions(duration)).moveTo(PointOption.point(x2, y2)).release().perform();
//        TouchAction action1 = new TouchAction(driver).press(width / 2,height * 4/ 5).waitAction(duration).moveTo(width /2, height /4).release();
//        action1.perform();
    }
}
