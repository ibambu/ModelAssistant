/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.beans.Require;
import com.snal.dataloader.MetaDataLoader;
import com.snal.dataloader.PropertiesFileLoader;
import com.snal.handler.IModelScriptHandler;
import com.snal.handler.impl.DB2ModelScriptHandler;
import com.snal.handler.impl.HiveModelScriptHandler;
import com.snal.util.text.TextUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * @author tao.luo
 */
public class MenuMain {

    private static final String MENU_FILE = "menu.txt";

    /**
     * 
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        /**
         * 初始化配置
         */
        Properties initProp = PropertiesFileLoader.loadConfData();
        Scanner scanner = new Scanner(System.in);
        int op = 0;//程序计数器
        String[] suptparamsvalue = {"NEW", "RENEW", "ADD-COLS", "MOD-COLS", "MOD-ATTR", "EXPORT"};//支持的操作类型集
        List<String> supportOp = Arrays.asList(suptparamsvalue);
        List<String> inputLines = new ArrayList<>();//输入的参数集
        Require require = new Require();//记录输入的需求信息
        String inputCmd = null;
        printMenu();//输出菜单说明
        while (op != -1) {
            switch (op) {
                case 0: {
                    if (TextUtil.isEmpty(require.getRequestCode())) {
                        System.out.print("请输入需求编号:");
                        inputCmd = scanner.nextLine();
                        if (inputCmd.equals("exit")) {
                            op = -1;
                            System.out.println("Bye.");
                        } else {
                            require.setRequestCode(inputCmd);
                        }
                    } else if (TextUtil.isEmpty(require.getRequestName())) {
                        System.out.print("请输入需求名称:");
                        inputCmd = scanner.nextLine();
                        if (inputCmd.equals("exit")) {
                            op = -1;
                            System.out.println("Bye.");
                        } else {
                            require.setRequestName(inputCmd);
                        }
                    }
                    if (!TextUtil.isEmpty(require.getRequestCode()) && !TextUtil.isEmpty(require.getRequestName())) {
                        System.out.println("请输入模型操作命令->");
                        op++;
                    }
                    break;
                }
                case 1: {
                    inputCmd = scanner.nextLine();
                    if ("commit".equalsIgnoreCase(inputCmd)) {
                        op--;
                        executeCmd(require, inputLines, initProp);
                        inputLines.clear();
                        require.setRequestCode(null);
                        require.setRequestName(null);
                    } else if ("exit".equalsIgnoreCase(inputCmd)) {
                        op = -1;
                        System.out.println("Bye.");
                    } else {
                        boolean isOk = checkParams(inputCmd, supportOp);
                        if (isOk) {
                            inputLines.add(inputCmd);
                        }
                    }
                    break;
                }
                default: {
                    System.out.println("异常退出！");
                    break;
                }
            }
        }
    }

    /**
     * 执行命令
     *
     * @param require
     * @param cmdList
     * @param initProp
     */
    public static void executeCmd(Require require, List<String> cmdList, Properties initProp) {
        try {
            List<String> hiveCmds = new ArrayList<>();
            List<String> db2Cmds = new ArrayList<>();
            cmdList.forEach((cmdline) -> {
                if (cmdline.toUpperCase().startsWith("HIVE")) {
                    hiveCmds.add(cmdline);
                } else if (cmdline.toUpperCase().startsWith("DB2")) {
                    db2Cmds.add(cmdline);
                }
            });
            /**
             * 加载元数据
             */
            Map tenantMap = PropertiesFileLoader.initTenantAttribute(initProp);
            String hiveMetaDataFile = initProp.getProperty("hive.meta.data.file");
            String db2MetaDataFile = initProp.getProperty("db2.meta.data.file");
            int startsheet = Integer.parseInt(initProp.getProperty("start.sheet.index"));
            int endsheet = Integer.parseInt(initProp.getProperty("end.sheet.index"));
            String[] sheetmincell = initProp.getProperty("sheet.min.cell.load").split(",");
            int[] mincelltoread = {Integer.parseInt(sheetmincell[0]), Integer.parseInt(sheetmincell[1])};
            MetaDataLoader metaDataLoader = new MetaDataLoader();
            String userDir = System.getProperty("user.dir");
            if (!hiveCmds.isEmpty()) {
                Map hiveMetaData = metaDataLoader.loadMetaData(hiveMetaDataFile, tenantMap, startsheet, endsheet, mincelltoread);
                String filename = userDir + "\\" + require.getRequestCode() + "_" + require.getRequestName() + "_HIVE_模型升级.hql";
                IModelScriptHandler hiveHandler = new HiveModelScriptHandler();
                String hql = hiveHandler.makeScript(require, initProp, hiveMetaData, cmdList);
                if (hql != null && hql.length() > 0) {
                    TextUtil.writeToFile(hql, filename);
                }
            }
            if (!db2Cmds.isEmpty()) {
                Map db2MetaData = metaDataLoader.loadMetaData(db2MetaDataFile, tenantMap, startsheet, endsheet, mincelltoread);
                String filename = userDir + "\\" + require.getRequestCode() + "_" + require.getRequestName() + "_DB2_模型升级.hql";
                IModelScriptHandler db2Handler = new DB2ModelScriptHandler();
                String sql = db2Handler.makeScript(require, initProp, db2MetaData, cmdList);
                if (sql != null && sql.length() > 0) {
                    TextUtil.writeToFile(sql, filename);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    /**
     * 检查参数是否正确
     *
     * @param param
     * @param supportOp
     * @return
     */
    public static boolean checkParams(String param, List<String> supportOp) {
        boolean checkflag = true;
        String[] paramL1 = param.split("\\|");
        if (paramL1.length < 3) {
            System.out.println("命令格式错误:" + param);
            checkflag = false;
        }
        if (checkflag && !"HIVE".equalsIgnoreCase(paramL1[0]) && !"DB2".equalsIgnoreCase(paramL1[0])) {
            System.out.println("不支持的数据库类型:" + paramL1[0]);
            checkflag = false;
        }
        if (checkflag && paramL1[1].trim().length() == 0) {
            System.out.println("模型名不能为空:" + paramL1[1]);
            checkflag = false;
        }
        if (checkflag && !supportOp.contains(paramL1[2].toUpperCase())) {
            System.out.println("不支持的操作类型:" + paramL1[2]);
            checkflag = false;
        }
        if (!checkflag) {
            System.out.println("请输入模型操作命令->");
        }
        return true;
    }

    /**
     * 输出菜单说明
     */
    public static void printMenu() {
        String usrdir = System.getProperty("user.dir");
        String menufile = usrdir + "\\" + MENU_FILE;
        List<String> menuText = TextUtil.readTxtFileToList(menufile, false);
        menuText.forEach((line) -> {
            System.out.println(line);
        });
    }
}
