/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.beans.dbview.FromCol;
import com.snal.beans.dbview.FromTable;
import com.snal.beans.dbview.View;
import com.snal.beans.dbview.ViewCol;
import com.snal.util.excel.ExcelUtil;
import com.snal.util.text.TextUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author csandy
 */
public class BillViewCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String filename = "E:\\work\\SVN\\GDBI\\01GDBI文档\\05详细设计\\04模型\\广东移动NG3BASS项目 - 主库融合计费清单视图设计.xlsx";
        XSSFSheet sheet = ExcelUtil.readHighExcelSheet(filename, 0);
        int rowcnt = sheet.getLastRowNum() + 1;
        Map<String, View> viewMap = new HashMap();
        for (int i = 1; i < rowcnt; i++) {
            XSSFRow row = sheet.getRow(i);
            String colName = ExcelUtil.getXCellValueString(row.getCell(3));
            String colNameZh = ExcelUtil.getXCellValueString(row.getCell(4));
            String cdrNeeded = ExcelUtil.getXCellValueString(row.getCell(5));
            String nogprsNeeded = ExcelUtil.getXCellValueString(row.getCell(6));
            String gprsNeeded = ExcelUtil.getXCellValueString(row.getCell(7));
            View view1 = viewMap.get("TO_E_CDR_VIEW");
            if (cdrNeeded.equals("Y")) {
                if (view1 == null) {
                    view1 = new View("TO_E_CDR_VIEW");
                    view1.setViewCols(new ArrayList());
                    viewMap.put("TO_E_CDR_VIEW", view1);
                }
                view1.getViewCols().add(new ViewCol(colName,colNameZh));
            }

            View view2 = viewMap.get("TO_E_NOGPRS_CDR_VIEW");
            if (nogprsNeeded.equals("Y")) {
                if (view2 == null) {
                    view2 = new View("TO_E_NOGPRS_CDR_VIEW");
                    view2.setViewCols(new ArrayList());
                    viewMap.put("TO_E_NOGPRS_CDR_VIEW", view2);
                }
                view2.getViewCols().add(new ViewCol(colName,colNameZh));
            }

            View view3 = viewMap.get("TO_E_GPRS_CDR_VIEW");
            if (gprsNeeded.equals("Y")) {
                if (view3 == null) {
                    view3 = new View("TO_E_GPRS_CDR_VIEW");
                    view3.setViewCols(new ArrayList());
                    viewMap.put("TO_E_GPRS_CDR_VIEW", view3);
                }
                view3.getViewCols().add(new ViewCol(colName,colNameZh));
            }

            String colvalue1 = ExcelUtil.getXCellValueString(row.getCell(8));
            FromCol col1 = new FromCol("TO_E_VOC_CDR_D",colName,colNameZh,colvalue1);
            addColumn(viewMap, col1, cdrNeeded, nogprsNeeded, gprsNeeded);

            String colvalue2 = ExcelUtil.getXCellValueString(row.getCell(9));
            FromCol col2 = new FromCol("TO_E_SMMS_NEWBUSI_CDR_D",colName,colNameZh,colvalue2);
            addColumn(viewMap, col2, cdrNeeded, nogprsNeeded, gprsNeeded);

            String colvalue3 = ExcelUtil.getXCellValueString(row.getCell(10));
            FromCol col3 = new FromCol("TO_E_GPRS_CDR_D",colName,colNameZh,colvalue3);
            addColumn(viewMap, col3, cdrNeeded, nogprsNeeded, gprsNeeded);

            String colvalue4 = ExcelUtil.getXCellValueString(row.getCell(11));
            FromCol col4 = new FromCol("TO_E_WLAN_CDR_D",colName,colNameZh,colvalue4);
            addColumn(viewMap, col4, cdrNeeded, nogprsNeeded, gprsNeeded);
            
            String colvalue5 = ExcelUtil.getXCellValueString(row.getCell(12));
            FromCol col5 = new FromCol("TO_E_PKG_FIXFEE_CDR_D",colName,colNameZh,colvalue5);
            addColumn(viewMap, col5, cdrNeeded, nogprsNeeded, gprsNeeded);

            String colvalue6 = ExcelUtil.getXCellValueString(row.getCell(13));
            FromCol col6 = new FromCol("TO_E_OTH_PAY_CDR_D",colName,colNameZh,colvalue6);
            addColumn(viewMap, col6, cdrNeeded, nogprsNeeded, gprsNeeded);

            String colvalue7 = ExcelUtil.getXCellValueString(row.getCell(14));
            FromCol col7 = new FromCol("TO_E_PERS_SPCL_BUSI_CDR_D",colName,colNameZh,colvalue7);
            addColumn(viewMap, col7, cdrNeeded, nogprsNeeded, gprsNeeded);
        }
        createViewSql(viewMap);

    }

    public static void addColumn(Map<String, View> viewMap, FromCol fromCol,
            String cdrNeeded, String nogprsNeeded, String gprsNeeded) {
        if (cdrNeeded.equals("Y")) {
            addFromCol(viewMap, fromCol, "TO_E_CDR_VIEW");
        }
        if (nogprsNeeded.equals("Y")) {
            if (!fromCol.getFromTalbe().equals("TO_E_GPRS_CDR_D")) {
                addFromCol(viewMap, fromCol, "TO_E_NOGPRS_CDR_VIEW");
            }
        }
        if (gprsNeeded.equals("Y")) {
            if (fromCol.getFromTalbe().equals("TO_E_GPRS_CDR_D")) {
                addFromCol(viewMap, fromCol, "TO_E_GPRS_CDR_VIEW");
            }
        }
    }

    public static void addFromCol(Map<String, View> viewMap, FromCol fromCol, String viewName) {
        View view = viewMap.get(viewName);
        List<FromTable> fromList = view.getFromTables();
        if (fromList == null) {
            fromList = new ArrayList();
            view.setFromTables(fromList);
        }
        boolean isExist = false;
        for (FromTable fromTable : fromList) {
            if (fromTable.getName().equals(fromCol.getFromTalbe())) {
                if (fromTable.getFromCols() == null) {
                    fromTable.setFromCols(new ArrayList());
                }
                fromTable.getFromCols().add(fromCol);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            FromTable fromTable = new FromTable();
            fromTable.setName(fromCol.getFromTalbe());
            fromTable.setFromCols(new ArrayList());
            fromTable.getFromCols().add(fromCol);
            fromList.add(fromTable);
        }
    }

    public static void createViewSql(Map<String, View> viewMap) throws Exception {
        for (String viewName : viewMap.keySet()) {
            StringBuilder sqlbuffer = new StringBuilder();
            View view = viewMap.get(viewName);
            List<FromTable> fromTables = view.getFromTables();
            sqlbuffer.append("----VIEW NAME:").append(viewName).append("----\n");
            sqlbuffer.append("CREATE VIEW ODS.").append(viewName).append("(\n");
            List<ViewCol> viewCols = view.getViewCols();
            for (ViewCol viewCol : viewCols) {
                sqlbuffer.append("\t").append(viewCol.getColName()).append(",    ----").append(viewCol.getColNameZh()).append("\n");
            }
            int indx = sqlbuffer.lastIndexOf(",");
            sqlbuffer.deleteCharAt(indx);//删除最后一个逗号
            sqlbuffer.append(") AS ").append("\n");
            for (FromTable fromTable : fromTables) {
                String sqlstr = buildeSql(fromTable, viewName);
                sqlbuffer.append(sqlstr);
                sqlbuffer.append(" UNION ALL ").append("\n");
            }
            int index = sqlbuffer.lastIndexOf("UNION ALL");
            sqlbuffer.delete(index, sqlbuffer.length() - 1);
            sqlbuffer.append(";\n");
            TextUtil.writeToFile(sqlbuffer.toString(), viewName + ".sql");

            System.out.println(sqlbuffer.toString());
        }
    }

    public static String buildeSql(FromTable fromTable, String viewName) {

        StringBuilder viewBody = new StringBuilder();
        List<FromCol> cols = fromTable.getFromCols();
        for (FromCol col : cols) {
            /**
             * 拼视图值
             */
            if (viewBody.length() == 0) {
                viewBody.append("\t").append(col.getViewValue()).append("    ----").append(col.getViewColNameZh()).append("\n");
            } else {
                viewBody.append("\t").append(",").append(col.getViewValue()).append("    ----").append(col.getViewColNameZh()).append("\n");
            }
        }
        StringBuilder sqlBuffer = new StringBuilder();
        sqlBuffer.append(" SELECT \n")
                .append(viewBody)
                .append(" FROM ODS.").append(fromTable.getName()).append("\n");
        return sqlBuffer.toString();

    }
}
