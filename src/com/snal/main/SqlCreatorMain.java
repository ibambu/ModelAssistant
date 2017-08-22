/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.dataloader.ModeScriptBuilder;
import com.snal.util.text.TextUtil;
import java.util.List;

public class SqlCreatorMain {

    public static void main(String[] args) {
//        String filename = "‪d:\\x.xlsx";
//        XSSFSheet sheet1 = ExcelUtil.readHighExcelSheet(filename, 0);
//        XSSFSheet sheet2 = sheet1.getWorkbook().getSheetAt(1);
//
//        int rowcount1 = sheet1.getLastRowNum() + 1;
//        int rowcount2 = sheet2.getLastRowNum() + 1;
//        StringBuilder sqlbuffer = new StringBuilder();
//        for (int i = 0; i < rowcount1; i++) {
//            XSSFRow row = sheet1.getRow(i);
//            String senceid = ExcelUtil.getXCellValueString(row.getCell(0));
//            String sencename = ExcelUtil.getXCellValueString(row.getCell(1));
//            String sencefullname = ExcelUtil.getXCellValueString(row.getCell(2));
//            String parentsenceid = ExcelUtil.getXCellValueString(row.getCell(3));
//            String sencetype = ExcelUtil.getXCellValueString(row.getCell(4));
//            String insertsql = "insert into meta_scene_def (scene_id,scene_name,scene_full_name,parent_scene_id,scene_type) values ("
//                    + "'" + senceid + "',"
//                    + "'" + sencename + "',"
//                    + "'" + sencefullname + "',"
//                    + "'" + parentsenceid + "',"
//                    + "'" + sencetype + "'"
//                    + ")";
//            sqlbuffer.append(insertsql).append("\n");
//        }
//        sqlbuffer.append("\n\n");
//        for (int i = 0; i < rowcount2; i++) {
//            XSSFRow row = sheet2.getRow(i);
//            String senceid = ExcelUtil.getXCellValueString(row.getCell(0));
//            String interface_id = ExcelUtil.getXCellValueString(row.getCell(1));
//            String imode_name = ExcelUtil.getXCellValueString(row.getCell(2));
//            String insertsql = "insert into meta_scene_imodel_rela (scene_id,interface_id,scene_full_name,imode_name) values ("
//                    + "'" + senceid + "',"
//                    + "'" + interface_id + "',"
//                    + "'" + imode_name + "'"
//                    + ")";
//            sqlbuffer.append(insertsql).append("\n");
//        }
//        System.out.println(sqlbuffer.toString());
        test3();
    }

    public static void test2() {
        String filename = "C:\\Users\\yx\\Desktop\\O.txt";
        String[] branches = {"GZ", "SZ", "DG", "FS", "ST", "ZH", "HZ", "ZS", "JM", "ZJ", "SG", "HY", "MZ", "SW", "YJ", "MM", "ZQ", "QY", "CZ", "JY", "YF"};
        List<String> list = TextUtil.readTxtFileToList(filename, false);
        for (String aa : list) {
            String[] arrays = aa.split("#");
            String tablename = arrays[0];
            String colname = arrays[3];
            String anquanlevel = arrays[4];
            String tuomin = arrays[5];
            //String updatesql = "update column_val set filed_type_child='" + anquanlevel + "',policeid='" + tuomin + "' where dataname='" + tablename + "' and colname='" + colname + "';";
            //System.out.println(updatesql);
            String branchetables = "";
            for (String branch : branches) {
                if (branchetables.length() == 0) {
                    branchetables += "'" + tablename + "_" + branch + "'";
                } else {
                    branchetables += ",'" + tablename + "_" + branch + "'";
                }

            }
            String updatesql1 = "update column_val set filed_type_child='" + anquanlevel + "',POLICYID='" + tuomin + "' where dataname in (" + branchetables + ") and colname='" + colname + "';";
            System.out.println(updatesql1);
        }
    }

    public static void test3() {
        String filename = "C:\\Users\\yx\\Desktop\\111.txt";
        String[] branches = {"GZ", "SZ", "DG", "FS", "ST", "ZH", "HZ", "ZS", "JM", "ZJ", "SG", "HY", "MZ", "SW", "YJ", "MM", "ZQ", "QY", "CZ", "JY", "YF"};
        List<String> list = TextUtil.readTxtFileToList(filename, false);
        for (String aa : list) {
            String[] arrays = aa.split("#");
            String tablename = arrays[1];
            String xmlid = arrays[0];
            String updatesql1 = "update column_val_import_new  set xmlid='" + xmlid + "' where dataname ='" + tablename + "';";
            System.out.println(updatesql1);
        }
    }

    public static void test4() {
         String filename = "C:\\Users\\yx\\Desktop\\branchtable.txt";
         
    }

    public static void test() {
        String filename1 = "e:\\场景定义表.txt";
        String filename2 = "e:\\relation.txt";
        List<String> sencedef = TextUtil.readTxtFileToList(filename1, false);
        List<String> sencedrel = TextUtil.readTxtFileToList(filename2, false);

        StringBuilder sqlbuffer = new StringBuilder();

        for (String line : sencedef) {
            String[] cells = line.split("#");
            String senceid = cells[0];
            String sencename = cells[1];
            String sencefullname = cells[2];
            String parentsenceid = cells.length > 3 ? cells[3] : "";
            String sencetype = (cells.length > 4) ? cells[4] : "";
            String insertsql = "insert into meta_scene_def (scene_id,scene_name,scene_full_name,parent_scene_id,scene_type) values ("
                    + "'" + senceid + "',"
                    + "'" + sencename + "',"
                    + "'" + sencefullname + "',"
                    + "'" + parentsenceid + "',"
                    + "'" + sencetype + "'"
                    + ");";
            sqlbuffer.append(insertsql).append("\n");
        }

        sqlbuffer.append("\n\n");
        for (String line : sencedrel) {
            String[] cells = line.split("#");
            String senceid = cells[0];
            String interface_id = cells[1];
            String imode_name = cells[2];
            String insertsql = "insert into meta_scene_imodel_rela (scene_id,interface_id,imode_name) values ("
                    + "'" + senceid + "',"
                    + "'" + interface_id + "',"
                    + "'" + imode_name + "'"
                    + ");";
            sqlbuffer.append(insertsql).append("\n");
        }
        System.out.println(sqlbuffer.toString());
    }
}
