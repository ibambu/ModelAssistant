/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.util.excel.BigExcelUtil;
import com.snal.dataloader.PropertiesFileLoader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luotao
 */
public class ModeScriptCreatorMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ModeScriptCreatorMain creator = new ModeScriptCreatorMain();
        Properties prop = PropertiesFileLoader.loadConfData();
        String usrdir = System.getProperty("user.dir");
        String metaDataFile = prop.getProperty("metaData");//元数据文件路径
        String inputfile = usrdir + "/" + prop.getProperty("input");//要处理的模型列表文件
        String outputfile = usrdir + "/" + prop.getProperty("output");//脚本输出文件
        String sqltype = prop.getProperty("sqltype");//脚本类型
        String[] branchList = prop.getProperty("branch_cd").split(",");//地市简称
        String serDeLibrary = prop.getProperty("SERDE_LIBRARY");//SerDe Library
        String storeFileType = prop.getProperty("STORED_FILE_TYPE");//Stored File Type
        String startsheet = prop.getProperty("START_SHEET");
        String endsheet = prop.getProperty("END_SHEET");
        String[] sheetmincell = prop.getProperty("MIN_CELL_TO_READ").split(",");
        int[] mincelltoread = {Integer.parseInt(sheetmincell[0], Integer.parseInt(sheetmincell[1]))};
        List<String> modelist = readTxtFileToList(inputfile);
        /**
         * 加载所有元数据
         */
        BigExcelUtil bigExlUtil = new BigExcelUtil();
        Map<String, List<List<String>>> metaDataMap = bigExlUtil.readExcelData(metaDataFile,
                Integer.parseInt(startsheet), Integer.parseInt(endsheet), mincelltoread);
        /**
         * 检查字段英文名，中文名，数据类型是否为空
         */
        List<List<String>> rows = metaDataMap.get("1");
        List<String> errtables = new ArrayList();
        rows.stream().filter((onerow) -> (onerow.get(5) == null || onerow.get(6) == null || onerow.get(7) == null
                || onerow.get(5).trim().length() == 0
                || onerow.get(6).trim().length() == 0
                || onerow.get(7).trim().length() == 0)).filter((onerow) -> (!errtables.contains(onerow.get(1)))).forEach((onerow) -> {
            errtables.add(onerow.get(2));
        });
        /**
         * 加载接口模型列表
         */
        StringBuilder sbd = new StringBuilder();
        int count = 0;
        List<String> failtables = new ArrayList();
        Map<String, StringBuilder> hqlmap = new HashMap();//key :租户名  value：模型脚本
        if (null != sqltype) {
            switch (sqltype) {
                /**
                 * 生成HIVE主表建表语句，如果有拆分地市模型，则同时创建分地市模型建表语句。
                 */
                case "1":
                    for (String tablename : modelist) {
                        String sqlscript = creator.createHiveMainTableScript(metaDataMap, tablename, hqlmap, branchList, serDeLibrary, storeFileType);
                        if (sqlscript != null && sqlscript.trim().length() != 0) {
                            count++;
                        } else {
                            failtables.add(tablename);
                        }
                    }
                    for (String key : hqlmap.keySet()) {
                        StringBuilder userrenthql = hqlmap.get(key);
                        if (userrenthql != null) {
                            sbd.append(userrenthql.toString()).append("\r\n");
                        }
                    }
                    break;
                /**
                 * 生成可以导入ERWin的建表语句。
                 */
                case "2":
                    for (String tablename : modelist) {
                        String sqlscript = creator.createHiveModeScriptForERWin(metaDataMap, tablename);
                        if (sqlscript != null && sqlscript.trim().length() != 0) {
                            count++;
                            sbd.append(sqlscript).append("\r\n");
                        } else {
                            failtables.add(tablename);
                        }
                    }
                    break;
                /**
                 * 生成拆分地市模型语句。
                 */
                case "3":
                    for (String tablename : modelist) {
                        String sqlscript = creator.createHiveBranchTableScript(metaDataMap, tablename, branchList, hqlmap, serDeLibrary, storeFileType);
                        if (sqlscript != null && sqlscript.trim().length() != 0) {
                            count++;
                        } else {
                            failtables.add(tablename);
                        }
                    }
                    for (String key : hqlmap.keySet()) {
                        StringBuilder userrenthql = hqlmap.get(key);
                        if (userrenthql != null) {
                            sbd.append(userrenthql.toString()).append("\r\n");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        if (sbd.length() > 0) {
            try {
                OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(outputfile), "UTF-8");
                BufferedWriter bufwriter = new BufferedWriter(writerStream);
                bufwriter.write(sbd.toString());
                bufwriter.newLine();
                bufwriter.close();
            } catch (IOException e) {
                Logger.getLogger(ModeScriptCreatorMain.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        System.out.println("输入：" + modelist.size() + " 个   输出：" + count + " 个");
        if (!failtables.isEmpty()) {
            System.out.println("缺少脚本模型如下：");
            for (String a : failtables) {
                System.out.println(a);
            }
        }
        if (!errtables.isEmpty()) {
            System.out.println("模型字段定义有误：");
            for (String a : failtables) {
                System.out.println(a);
            }
        }

        metaDataMap.clear();
    }

    private String createHiveModeScriptForERWin(Map<String, List<List<String>>> metaDataMap, String tablename) {
        StringBuilder scriptbuf = new StringBuilder();
        StringBuilder commentsbuf = new StringBuilder();
        List<List<String>> tableIndxList = metaDataMap.get("0");//第一个工作表格，模型索引
        int indxsize = tableIndxList.size();
        String tablenamezh = "";
        for (int i = 0; i < indxsize; i++) {
            String itablename = tableIndxList.get(i).get(3);
            if (itablename.equals(tablename)) {
                tablenamezh = tableIndxList.get(i).get(4);
            }
        }
        commentsbuf.append("COMMENT ON TABLE ").append(tablename).append(" IS '").append(tablenamezh).append("';\n");
        List<List<String>> tableStructList = metaDataMap.get("1");//第二个工作表格，模型结构
        int structsize = tableStructList.size();
        boolean found = false;
        for (int i = 0; i < structsize; i++) {
            List<String> rowdata = tableStructList.get(i);
            String itablename = rowdata.get(1);//模型名称
            String dbtype = rowdata.get(0);//数据库类型
            String colseq = rowdata.get(2);//字段序号
            if (found && !tablename.equals(itablename)) {
                scriptbuf.append("\n);\n");//新模型开始行
                break;
            }
            /**
             * 找到模型，开始创建脚本。
             */
            if ("HIVE".contains(dbtype) && tablename.equals(itablename)) {
                found = true;
                String colname = rowdata.get(3);//字段名
                String datatype = rowdata.get(5);//数据类型
                if (colseq.equals("1")) {
                    scriptbuf.append("CREATE TABLE ").append(tablename).append(" (").append("\n");
                    scriptbuf.append("   ").append(colname).append("    ").append(datatype);
                } else {
                    scriptbuf.append(",\n").append("   ").append(colname).append("    ").append(datatype);
                }
                String colnameZH = rowdata.get(4);
                commentsbuf.append("COMMENT ON COLUMN ").append(tablename).append(".").append(colname).append(" IS '").append(colnameZH).append("';\n");
                if (i == structsize - 1) {
                    scriptbuf.append("\n);\n");//到达文件末尾
                }
            }
        }
        scriptbuf.append(commentsbuf.toString());
        return scriptbuf.toString();
    }

    /**
     * 是否地市共享模型
     *
     * @param tablename
     * @param tableIndxList
     * @return
     */
    private boolean isBranchSharedTable(String tablename, List<List<String>> tableIndxList) {
        int rowcnt = tableIndxList.size();
        for (int i = 0; i < rowcnt; i++) {
            List<String> row = tableIndxList.get(i);
            if (tablename.equals(row.get(5)) && "是".equals(row.get(26))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成HIVE主表建表语句
     *
     * @param metaDataMap 元数据字典数据
     * @param tablename 需要创建的模型名
     * @param hqlmap 输出的脚本语句
     * @param serDeLibrary 序列化引用类（从配置文件init.properties传入）
     * @param storeFileType 模型存储方式（从配置文件init.properties传入）
     * @return
     */
    private String createHiveMainTableScript(Map<String, List<List<String>>> metaDataMap, String tablename,
            Map<String, StringBuilder> hqlmap, String[] branchList, String serDeLibrary, String storeFileType) {
        StringBuilder sbd = new StringBuilder();
        List<List<String>> tableIndxList = metaDataMap.get("0");//第一个工作表格，模型索引

        boolean isSharedForBranch = isBranchSharedTable(tablename, tableIndxList);//是否地市共享
        List<List<String>> tableStructList = metaDataMap.get("1");//第二个工作表格，模型结构
        int structsize = tableStructList.size();
        boolean found = false;
        for (int i = 0; i < structsize; i++) {
            List<String> rowdata = tableStructList.get(i);
            String itablename = rowdata.get(2);//模型名称
            String dbtype = rowdata.get(0);//数据库类型
            String colseq = rowdata.get(4);//字段序号
            if (found && !tablename.equals(itablename)) {
                sbd.append("\r)");//新模型开始行
                break;
            }
            /**
             * 找到模型，开始创建脚本。
             */
            if ("HIVE".contains(dbtype) && tablename.equals(itablename)) {
                found = true;
                String colname = rowdata.get(5);//字段名
                String datatype = rowdata.get(7);//数据类型
                if (colseq.equals("1")) {
                    sbd.append("DROP TABLE IF EXISTS ").append(tablename).append(";\r\n");
                    sbd.append("CREATE EXTERNAL TABLE ").append(tablename).append(" (").append("\r");
                    sbd.append("   ").append(colname).append("    ").append(datatype);
                } else {
                    sbd.append(",\r").append("   ").append(colname).append("    ").append(datatype);
                }
                if (i == structsize - 1) {
                    sbd.append("\r)");//到达文件末尾
                }
            }
        }
        if (sbd.length() > 0) {
            String partionscript = makeTablePartionScript(tablename, tableIndxList, serDeLibrary, storeFileType);
            String userent = queryTableUserRent(tablename, tableIndxList);
            sbd.append(partionscript).append("\r");
            sbd.append("ALTER TABLE ").append(tablename).append(" SET SERDEPROPERTIES('serialization.null.format' = '', 'serialization.encoding'='gbk');").append("\r");
            if (partionscript == null || partionscript.trim().length() == 0) {
                System.out.println("[ERRO] " + tablename + "{缺少分区语句}");
            } else {
                System.out.println("[OK] " + tablename);
            }
            StringBuilder retbuf = hqlmap.get(userent);
            if (retbuf == null) {
                retbuf = new StringBuilder("----").append(userent).append("租户脚本----\r\n");
                retbuf.append(sbd.toString()).append("\r\n");
                hqlmap.put(userent, retbuf);
            } else {
                retbuf.append(sbd.toString()).append("\r\n");
            }
            /**
             * 检查是否需要建拆分地市模型，如果需要拆分则输出拆分模型建表语句。
             */
            if (isSharedForBranch) {
                createHiveBranchTableScript(metaDataMap, tablename, branchList, hqlmap, serDeLibrary, storeFileType);
            }
        }
        return sbd.toString();
    }

    private String createHiveBranchTableScript(Map<String, List<List<String>>> metaDataMap, String tablename,
            String[] branchlist, Map<String, StringBuilder> sqlmap,
            String serDeLibrary, String storeFileType) {
        StringBuilder sbd = new StringBuilder();
        List<List<String>> tableIndxList = metaDataMap.get("0");//第一个工作表格，模型索引
        List<List<String>> tableStructList = metaDataMap.get("1");//第二个工作表格，模型结构
        int structsize = tableStructList.size();
        boolean found = false;
        for (int i = 0; i < structsize; i++) {
            List<String> rowdata = tableStructList.get(i);
            String itablename = rowdata.get(2);//模型名称
            String dbtype = rowdata.get(0);//数据库类型
            String colseq = rowdata.get(4);//字段序号
            if (found && !tablename.equals(itablename)) {
                sbd.append("\r\n)");//新模型开始行
                break;
            }
            /**
             * 找到模型，开始创建脚本。
             */
            if ("HIVE".contains(dbtype) && tablename.equals(itablename)) {
                found = true;
                String colname = rowdata.get(5);//字段名
                String datatype = rowdata.get(7);//数据类型
                if (colseq.equals("1")) {
                    sbd.append("CREATE EXTERNAL TABLE ").append(tablename).append(" (").append("\r");
                    sbd.append("   ").append(colname).append("    ").append(datatype);
                } else {
                    sbd.append(",\r\n").append("   ").append(colname).append("    ").append(datatype);
                }
                if (i == structsize - 1) {
                    sbd.append("\r\n)");//到达文件末尾
                }
            }
        }
        StringBuilder retbdf = null;
        if (sbd.length() > 0) {
            String partionscript = makeTablePartionScript(tablename, tableIndxList, serDeLibrary, storeFileType);
            sbd.append(partionscript).append("\r\n");
            sbd.append("ALTER TABLE ").append(tablename).append(" SET SERDEPROPERTIES('serialization.null.format' = '','serialization.encoding'='gbk');").append("\r");
            if (partionscript == null || partionscript.trim().length() == 0) {
                System.out.println("[ERRO] " + tablename + "{缺少分区语句}");
            } else {
                System.out.println("[OK] " + tablename);
            }
            String userrent = queryTableUserRent(tablename, tableIndxList);
            retbdf = sqlmap.get(userrent);
            if (retbdf == null) {
                retbdf = new StringBuilder("----").append(userrent).append("租户脚本----\r\n");
                sqlmap.put(userrent, retbdf);
            }
            for (String branch : branchlist) {
                String mainTableScript = sbd.toString();
                String branchTableName = tablename + "_" + branch;
                int indx1 = mainTableScript.indexOf("PARTITIONED BY (branch STRING,month INT");//判断是否有时间分区
                if (indx1 != -1) {
                    mainTableScript = mainTableScript.replaceAll(tablename, branchTableName).replaceAll("branch STRING,", "");
                    mainTableScript = mainTableScript.replaceAll(branchTableName + "';", tablename + "/branch=" + branch + "';");
                } else {
                    mainTableScript = mainTableScript.replaceAll(tablename, branchTableName).replaceAll("PARTITIONED BY \\(branch STRING\\)", "");
                    mainTableScript = mainTableScript.replaceAll(branchTableName + "';", tablename + "/branch=" + branch + "';");
                }
                retbdf.append("DROP TABLE IF EXISTS ").append(branchTableName).append(";\r\n");
                retbdf.append(mainTableScript).append("\r\n");
            }
        }
        return retbdf != null ? retbdf.toString() : "";
    }

    private String makeTablePartionScript(String tablename, List<List<String>> tableIndxList, String serDeLibrary, String storeFileType) {
        String script = null;
        int rowcnt = tableIndxList.size();
        for (int i = 1; i < rowcnt; i++) {
            List<String> rowdata = tableIndxList.get(i);
            if (rowdata == null) {
                continue;
            }
            String itablename = rowdata.get(5);
            String dbtype = rowdata.get(1);
            if (itablename.equals(tablename) && dbtype.equalsIgnoreCase("HIVE")) {
                String location = rowdata.get(13);//存储路径
                String splitstr = rowdata.get(14);//分隔符
                if (splitstr.equals(";")) {
                    splitstr = "\\" + splitstr;
                }
                String paritions = rowdata.get(15);//分区字段
                if (paritions != null) {
                    String[] pktcols = paritions.split(",");
                    String pktstr = "";
                    for (int m = 0; m < pktcols.length; m++) {
                        if (m == 0) {
                            pktstr = pktcols[m] + " STRING";
                        } else {
                            pktstr = pktstr + "," + pktcols[m] + " INT";
                        }
                    }
                    script = "PARTITIONED BY (" + pktstr + ")" + " ROW FORMAT SERDE "
                            + "'" + serDeLibrary + "' WITH SERDEPROPERTIES (\"field.delim\"=\"" + splitstr + "\")"
                            + " STORED AS " + storeFileType + " LOCATION '" + location + "';\r\n";
                }
                break;
            }
        }
        return script;
    }

    private String queryTableUserRent(String tablename, List<List<String>> tableIndxList) {
        String usertent = null;
        int rowcnt = tableIndxList.size();
        for (int i = 1; i < rowcnt; i++) {
            List<String> rowdata = tableIndxList.get(i);
            if (rowdata == null) {
                continue;
            }
            String itablename = rowdata.get(5);
            String dbtype = rowdata.get(1);
            if (itablename.equals(tablename) && dbtype.equalsIgnoreCase("HIVE")) {
                usertent = rowdata.get(12);
                break;
            }
        }
        return usertent;
    }

    private static List<String> readTxtFileToList(String filename) {
        List dslist = new ArrayList(1500);
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            while ((readline = bufreader.readLine()) != null) {
                if (!dslist.contains(readline)) {
                    dslist.add(readline);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ModeScriptCreatorMain.class.getName()).log(Level.SEVERE, null, e);
        }
        return dslist;
    }
}
