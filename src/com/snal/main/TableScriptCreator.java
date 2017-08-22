/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.beans.TenantAttribute;
import com.snal.beans.Table;
import com.snal.dataloader.MetaDataLoader;
import com.snal.dataloader.ModeScriptBuilder;
import com.snal.dataloader.PropertiesFileLoader;
import com.snal.util.text.TextUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author luotao
 */
public class TableScriptCreator {

    public static void main(String[] args) throws IOException, Exception {

        MetaDataLoader metaDataUtil = new MetaDataLoader();
        Properties prop = PropertiesFileLoader.loadConfData();
        Map<String, TenantAttribute> tenantMap = PropertiesFileLoader.initTenantAttribute(prop);
        String usrdir = System.getProperty("user.dir");
        String hiveMetaDataFile = prop.getProperty("hive.meta.data.file");//hive元数据文件路径
        String db2MetaDataFile = prop.getProperty("db2.meta.data.file");//db2元数据文件路径
        String inputfile = usrdir + "\\" + prop.getProperty("input.file");//要处理的模型列表文件
        String outputfile = usrdir + "\\" + prop.getProperty("output.file");//脚本输出文件
        String optionValue = prop.getProperty("function.option.value");//脚本类型
        boolean outputSharedTable = Boolean.parseBoolean(prop.getProperty("script.output.share.table"));//是否输出共享模型脚本
        boolean useproxy = Boolean.parseBoolean(prop.getProperty("script.proxy.program.enabled"));//是否输出共享模型脚本
        String[] branchList = prop.getProperty("branch.code.value").split(",");//地市简称
        String importpaasfile = usrdir + "\\" + prop.getProperty("script.import.paas.sqlfile");//PAAS导入脚本
        int startsheet = Integer.parseInt(prop.getProperty("start.sheet.index"));
        int endsheet = Integer.parseInt(prop.getProperty("end.sheet.index"));
        String[] sheetmincell = prop.getProperty("sheet.min.cell.load").split(",");
        List<String> hivekeywords = Arrays.asList(prop.getProperty("hive.keywords").split(","));
        int[] mincelltoread = {Integer.parseInt(sheetmincell[0]), Integer.parseInt(sheetmincell[1])};
        List<String> modelist = TextUtil.readTxtFileToList(inputfile, true);
        /**
         * 检查模型名称是否有空格或者空行
         */
        boolean isValid = true;
        if (!optionValue.equals("5")) {
            for (String tablename : modelist) {
                if (tablename.contains(" ") || tablename.trim().length() == 0) {
                    System.out.println("[" + tablename + "] 模型名前后不能包含空格或者是空行！");
                    isValid = false;
                    break;
                }
            }
        }
        /**
         * 加载元数据
         */
        if (!isValid) {
            System.exit(-1);
        }
        String metaDataFile = optionValue.equals("7") ? db2MetaDataFile : hiveMetaDataFile;
        System.out.println("正在加载并检查元数据：" + metaDataFile);
        Map<String, Table> tableMap = metaDataUtil.loadMetaData(metaDataFile, tenantMap, startsheet, endsheet, mincelltoread);
        /**
         * 检查模型字段是否正确
         */
        for (String tablename : modelist) {
            Table table = tableMap.get(tablename);
            if (table == null) {
                System.out.println("模型不存在：" + tablename);
                continue;
            }
            boolean isok = metaDataUtil.checkTableColumn(table, hivekeywords);
            if (!isok) {
                isValid = isValid & isok;
            }
        }
        if (true) {
            Map<String, StringBuilder> hqlmap = new HashMap();//存放建表语句，租户相同的模型其建表语句放在一起，便于升级。
            /**
             * 根据function.option.value 调用不同的功能函数。
             */
            switch (optionValue) {
                case "1":
                    hqlmap = ModeScriptBuilder.makeHiveMainTableScript(modelist, tableMap, branchList, outputSharedTable, useproxy);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                case "2":
                    hqlmap = ModeScriptBuilder.makeHiveBranchTableScript(modelist, tableMap, branchList, useproxy);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                case "3":
                    break;
                case "4":
                    MetaDataExportMain exportmain = new MetaDataExportMain();

                    String importsql = exportmain.exportBranchTables(modelist, tableMap, outputfile, branchList);
                    hqlmap.put("XX", new StringBuilder(importsql));
                    ModeScriptBuilder.writeToFile(hqlmap, importpaasfile);
                    break;
                case "5":
                    StringBuilder readdparthql = ModeScriptBuilder.reAddTableParition(inputfile, tableMap, branchList, useproxy);
                    hqlmap.put("AA", readdparthql);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                case "6":
                    StringBuilder modifyDelimiters = ModeScriptBuilder.modifyTableColDelimiters(modelist, tableMap, branchList, useproxy);
                    hqlmap.put("BB", modifyDelimiters);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                case "7":
                    hqlmap = ModeScriptBuilder.makeTableCreateScriptForDB2(modelist, tableMap, branchList);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                case "8":
                    ModeScriptBuilder.changeColumnSecurityType(modelist, tableMap, branchList);
                    break;
                case "9":
                    hqlmap = ModeScriptBuilder.addBranchTablePartition(modelist, tableMap, "20161001", "20170810", branchList);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                case "10":
                    hqlmap = ModeScriptBuilder.changeBranchTableFileFormat(modelist, tableMap, "20161001", "20170706", branchList, false);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                case "11":
                    hqlmap = ModeScriptBuilder.changeColumnDelimiter(modelist, tableMap, "20161001", "20161130", branchList, false);
                    ModeScriptBuilder.writeToFile(hqlmap, outputfile);
                    break;
                default:
                    break;
            }
        }
    }
}
