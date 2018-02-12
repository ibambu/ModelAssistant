/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Luo Tao
 */
public class ViewTool {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, IOException {
//        String viewName = args[0];
//        String tempfile = args[1];
//        String viewType = args[2];
        String starttime = "20171201";
        String endtime = "20171227";

        String[] branches = {"GZ", "SZ", "DG", "FS", "ST", "ZH", "HZ", "ZS", "JM", "ZJ", "SG", "HY", "MZ", "SW", "YJ", "MM", "ZQ", "QY", "CZ", "JY", "YF"};
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyyMMdd");
        Calendar oldday = Calendar.getInstance();
        oldday.setTime(sdfDay.parse(starttime));

        Calendar nowDay = Calendar.getInstance();
        nowDay.setTime(sdfDay.parse(endtime));

        while (nowDay.after(oldday) || nowDay.equals(oldday)) {
            test(branches, sdfDay.format(nowDay.getTime()));
            nowDay.add(Calendar.DAY_OF_MONTH, -1);
        }

    }

    public static void test(String[] branches, String datatime) {
        for (String branch : branches) {
            String sql = " ALTER TABLE STAGE.TS_E_GPRS_CDR_D_" + branch + datatime + " ADD COLUMN GPRS_UP_KB_FLUX BIGINT;";
            String sql1 = " ALTER TABLE STAGE.TS_E_GPRS_CDR_D_" + branch + datatime + " ADD COLUMN GPRS_DOWN_KB_FLUX BIGINT;";
//            System.out.println(sql);
//            System.out.println(sql1);
            System.out.println("REORG TABLE STAGE.TS_E_GPRS_CDR_D_" + branch + datatime);
        }
    }

    public static void makeViewSql(String[] branches, String datetime, String viewName, String tempfile, String viewType) throws IOException {
        StringBuilder buffer = new StringBuilder();
        StringBuilder databuffer = new StringBuilder();
        String filename = viewName + "_AA" + datetime + ".SQL";
        if (viewType.equalsIgnoreCase("month")) {
            filename = viewName + "_AA" + datetime.substring(0, 6) + ".SQL";
        } else if (viewType.equalsIgnoreCase("day")) {
            filename = viewName + "_AA" + datetime + ".SQL";
        }
        List<String> alist = readTxtFileToList(System.getProperty("user.dir") + "//" + tempfile, false);
        for (String a : alist) {
            int idx = a.indexOf("--");
            if (idx != -1) {
                a = a.substring(0, idx);
            }
            buffer.append(a).append("\n");
        }
        for (String branch : branches) {
            String sql = buffer.toString();
            if (viewType.equalsIgnoreCase("month")) {
                sql = sql.replaceAll("GZ201710", branch + datetime.substring(0, 6)).replaceAll("201710", datetime.substring(0, 6));
            } else if (viewType.equalsIgnoreCase("day")) {
                sql = sql.replaceAll("GZ20171001", branch + datetime).replaceAll("201710", datetime.substring(0, 6));
            }
            databuffer.append(sql).append("\n");
        }
        String outfile = "";

        if (viewType.equalsIgnoreCase("month")) {
            outfile = System.getProperty("user.dir") + "//month-view//";
        } else if (viewType.equalsIgnoreCase("day")) {
            outfile = System.getProperty("user.dir") + "//day-view//";
        }
        File outfileobj = new File(outfile);
        if (!outfileobj.exists()) {
            outfileobj.mkdir();
        }
        writeToFile(databuffer.toString(), outfile + filename);

    }

    public static List<String> readTxtFileToList(String filename, boolean doDistinct) {
        List dslist = new ArrayList(1500);
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 50 * 1024 * 1024);
            String readline = null;
            while ((readline = bufreader.readLine()) != null) {
                if (!doDistinct) {
                    dslist.add(readline);
                } else if (!dslist.contains(readline)) {
                    dslist.add(readline);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dslist;
    }

    public static void writeToFile(String script, String filename) throws IOException {
        BufferedWriter bufwriter = null;
        try {
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            bufwriter = new BufferedWriter(writerStream);
            bufwriter.write(script);
            bufwriter.newLine();
            bufwriter.close();
            System.out.println("输出结果：" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufwriter != null) {
                bufwriter.close();
            }
        }
    }
}
