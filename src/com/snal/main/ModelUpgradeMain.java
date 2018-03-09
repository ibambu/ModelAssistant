/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.beans.ModelChangeReq;
import com.snal.beans.Table;
import com.snal.beans.TenantAttribute;
import com.snal.dataloader.MetaDataLoader;
import com.snal.dataloader.PropertiesFileLoader;
import com.snal.handler.IModelUpgradeHandler;
import com.snal.handler.impl.HiveModelUpgradeHanlder;
import com.snal.util.excel.ExcelUtil;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author Luo Tao
 */
public class ModelUpgradeMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MetaDataLoader metaDataUtil = new MetaDataLoader();
            Properties prop = PropertiesFileLoader.loadConfData();
            Map<String, TenantAttribute> tenantMap = PropertiesFileLoader.initTenantAttribute(prop);
            //两份元数据字典，hive一份，DB2一份，两份结构一致。
            String hiveMetaDataFile = prop.getProperty("hive.meta.data.file");//hive元数据文件路径
            String db2MetaDataFile = prop.getProperty("db2.meta.data.file");//db2元数据文件路径
            //读取元数据字典excel时必须要用到的参数
            int startsheet = Integer.parseInt(prop.getProperty("start.sheet.index"));
            int endsheet = Integer.parseInt(prop.getProperty("end.sheet.index"));
            String[] sheetmincell = prop.getProperty("sheet.min.cell.load").split(",");//两份EXCEL 需要读取的sheet。
            int[] mincelltoread = {Integer.parseInt(sheetmincell[0]), Integer.parseInt(sheetmincell[1])};//两份excel至少读取的列数。
            System.out.println("正在加载并检查元数据：" + hiveMetaDataFile);
            Map<String, Table> tableMap = metaDataUtil.loadMetaData(hiveMetaDataFile, tenantMap, startsheet, endsheet, mincelltoread);
            System.out.println("元数据加载完毕...");
            /**
             * 读取模型变更申请EXCEL表，将HIVE 和 DB2 的变更分别保存到两个结合中。然后分别调用对应的接口生成SQL语句。
             */
            List<ModelChangeReq> hiveChangeReqList = new ArrayList<>();
            List<ModelChangeReq> db2ChangeReqList = new ArrayList<>();
            String usrdir = System.getProperty("user.dir");
            String changeReqFile = "upgrade.xlsx";
            XSSFSheet reqSheet = ExcelUtil.readHighExcelSheet(changeReqFile, 0);
            int rowcnt = reqSheet.getLastRowNum() + 1;
            System.out.println("rowcntrowcnt=" + rowcnt);
            ModelChangeReq req = null;
            for (int i = 1; i < rowcnt; i++) {
                XSSFRow row = reqSheet.getRow(i);
                req = new ModelChangeReq();
                req.setReqId(ExcelUtil.getXCellValueString(row.getCell(0)));
                req.setTableName(ExcelUtil.getXCellValueString(row.getCell(1)));
                req.setDbType(ExcelUtil.getXCellValueString(row.getCell(2)));
                req.setChangeType(Integer.parseInt(ExcelUtil.getXCellValueString(row.getCell(3))));
                req.setChangeContent(ExcelUtil.getXCellValueString(row.getCell(4)));
                req.setStatTime(ExcelUtil.getXCellValueString(row.getCell(5)));
                req.setEndTime(ExcelUtil.getXCellValueString(row.getCell(6)));
                if (req.getDbType().equalsIgnoreCase("HIVE")) {
                    hiveChangeReqList.add(req);
                } else if (req.getDbType().equalsIgnoreCase("DB2")) {
                    db2ChangeReqList.add(req);
                }
                System.out.println(req.toString());
            }
            if (!hiveChangeReqList.isEmpty()) {
                IModelUpgradeHandler hiveHander = new HiveModelUpgradeHanlder();
                Map<String, StringBuilder> hiveSqlMap = hiveHander.makeModelUpgradeSql(hiveChangeReqList, tableMap, prop);
                writeToFile(hiveSqlMap, req);
            }
            if (!db2ChangeReqList.isEmpty()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void writeToFile(Map<String, StringBuilder> dataBufferMap, ModelChangeReq changeReq) throws IOException {
        BufferedWriter bufwriter = null;
        try {
            if (!dataBufferMap.isEmpty()) {
                for (String key : dataBufferMap.keySet()) {
                    String usrRentSql = dataBufferMap.get(key).toString();
                    String outFileName = changeReq.getReqId();
                    if (key.equalsIgnoreCase("CTL.HIVE_CHECK_RULE")) {
                        outFileName = System.getProperty("user.dir") + "\\" + changeReq.getReqId() + "_" + key.toUpperCase() + ".txt";
                    } else if (key.trim().length() > 0) {
                        outFileName = System.getProperty("user.dir") + "\\" + changeReq.getReqId() + "_" + changeReq.getDbType() + "_" + key.toUpperCase() + ".sql";
                    }
                    OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8");
                    bufwriter = new BufferedWriter(writerStream);
                    bufwriter.write(usrRentSql);
                    bufwriter.newLine();
                    bufwriter.close();
                    System.out.println("输出结果：" + outFileName);
                }
            }
        } catch (IOException e) {
            System.out.println("[FAIL]输出建表语句失败！\n");
            Logger.getLogger(TableScriptCreator.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (bufwriter != null) {
                bufwriter.close();
            }
        }
    }

}
