package com.gaea.gamemaster.appium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.util.Scanner;

public class Test1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("输入1截屏");
        int isScreen = sc.nextInt();
        if (isScreen==1){
            System.out.println(isScreen);
        }

    }
}
