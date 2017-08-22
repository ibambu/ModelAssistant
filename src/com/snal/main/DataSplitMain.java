/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author luotao
 */
public class DataSplitMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String tableName = args[0];//正式表名
        LocalDate today = LocalDate.now();
        String sToday = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tempTableName = tableName + "_" + sToday;
        /**
         * 根据正式表创建临时表
         */
        String createTmpTableSql = "CREATE EXTERNAL TABLE JCFW." + tempTableName + " LIKE " + tableName + ";";
    }

}
