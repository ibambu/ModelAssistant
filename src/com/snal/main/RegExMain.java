/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author luotao
 */
public class RegExMain {

    public static void main(String[] args) {
//        String fileDir = args[0];
        String fileDir = "e:/test.txt";
        File file = new File(fileDir);
        List<String> urls = new ArrayList();
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (File aFile : fileList) {
                List<String> urlList = findURL(aFile);
                urls.addAll(urlList);
            }
        } else {
            List<String> urlList = findURL(file);
            urls.addAll(urlList);
        }
        urls.forEach((url) -> {
            System.out.println(url);
        });
    }

    /**
     * 匹配 URL
     *
     * @param text
     * @return
     */
    public static boolean isURL(String text) {
        // URL 正则表达式
        String regEx = "^http://[\\w\\.\\-]+(?:/|(?:/[\\w\\.\\-]+)*)?$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        
        Matcher matcher = pattern.matcher(text);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();
    }

    public static List<String> findURL(File file) {
        List dslist = new ArrayList(1500);
        try {
            FileInputStream instream = new FileInputStream(file);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 50 * 1024 * 1024);
            String readLine = null;
            while ((readLine = bufreader.readLine()) != null) {
                if (isURL(readLine)) {
                    dslist.add(readLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dslist;
    }
}
