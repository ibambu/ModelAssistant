/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.test;

import com.snal.util.excel.ExcelUtil;
import java.util.Arrays;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author Luo Tao
 */
public class ExcelDataProcessMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        splitExcelRow();
    }

    public static void splitExcelRow() {
        String datafile = System.getProperty("user.dir") + "/3.xlsx";
        XSSFSheet sheet = ExcelUtil.readHighExcelSheet(datafile, 0);
        int rowcnt = sheet.getLastRowNum() + 1;
        System.out.println("row count="+rowcnt);
        int newRowCount = rowcnt + 3;
        for (int i = 0; i < rowcnt; i++) {
            XSSFRow row = sheet.getRow(i);
            String groupIdStr = ExcelUtil.getXCellValueString(row.getCell(3));
            String[] groupIds = null;
            if (groupIdStr != null) {
                if (groupIdStr.contains("/")) {
                    groupIds = groupIdStr.split("/");
                } else if (groupIdStr.contains("\n")) {
                    groupIds = groupIdStr.split("\n");
                }
                if (groupIds != null) {
                    for (String groupId : groupIds) {
                        if (groupId.trim().length() > 0) {
                            XSSFRow newrow = sheet.createRow(newRowCount++);
                            newrow.copyRowFrom(row, new CellCopyPolicy());
                            newrow.getCell(3).setCellValue(groupId.trim());
                        }
                    }
                    System.out.println("remove row:" + row.getCell(3).getStringCellValue());
                    sheet.removeRow(row);
                }
            }
        }
        ExcelUtil.writeToHighExcelFile(sheet.getWorkbook(), System.getProperty("user.dir") + "/2.xlsx");
    }

}
