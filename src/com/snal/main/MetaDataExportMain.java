/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.beans.TableCol;
import com.snal.beans.Table;
import com.snal.util.excel.ExcelUtil;
import com.snal.util.text.TextUtil;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author luotao
 */
public class MetaDataExportMain {

    /**
     * test
     *
     * @param args
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        byte[] bytes = new byte[]{1};
        String sendString = new String(bytes, "GBK");
        System.out.println(sendString);
        String c = "\u0080";
        System.out.println(c);
        System.out.println(Integer.toHexString('€')); //输出20ac  
        System.out.println(Integer.toOctalString('€')); //输出20ac  
        System.out.println(Integer.toBinaryString('€')); //输出20ac  

        System.out.println('\001');//输出€  
        System.out.println('\u20ac');//输出€  
        String a = "[fieldcolnam]";
        System.out.println(a.replaceAll("\\[", "").replaceAll("\\]", ""));
    }

    /**
     * 从元数据导出分地市模型和主模型
     *
     * @param tablelist
     * @param metaDataMap
     * @param branches
     * @param distFile
     * @return
     */
    public String exportBranchTables(List<String> tablelist, Map<String, Table> metaDataMap, String distFile, String[] branches) {
        MetaDataExportMain exportmain = new MetaDataExportMain();
        List<Table> metaTableMap = exportmain.exportTablesForDacp(tablelist, metaDataMap, branches);
        StringBuilder sqlbuffer = new StringBuilder();
        String insertsql = exportmain.writeTableToExcel(metaTableMap, distFile);
        sqlbuffer.append(insertsql).append("\n\n");
        return sqlbuffer.toString();
    }

    public String writeTableToExcel(List<Table> tables, String outfile) {
        String[] headnames1 = {"xmlid", "dbname", "dataname", "datacnname", "state", "cycletype", "topiccode", "extend_cfg", "rightlevel", "creater",
            "curdutyer", "eff_date", "state_date", "team_code", "open_state", "remark"};
        String[] headnames2 = {"xmlid", "col_xmlid", "dataname", "col_seq", "colname", "colcnname", "datatype", "length",
            "precision_val", "party_seq", "isprimarykey", "isnullable", "remark", "filed_type_child", "sensitive_level", "policyid"};

//        Workbook wkbook = new SXSSFWorkbook();
//        Sheet indxsheet = wkbook.createSheet("主模型索引");
//        Sheet columnsheet = wkbook.createSheet("主模型结构");
//        ExcelUtil.createTableHeader(headnames1, indxsheet);
//        ExcelUtil.createTableHeader(headnames2, columnsheet);
//
//        Sheet indxsheetBranch = wkbook.createSheet("共享模型索引");
//        Sheet columnsheetBranch = wkbook.createSheet("共享模型结构");
//        ExcelUtil.createTableHeader(headnames1, indxsheetBranch);
//        ExcelUtil.createTableHeader(headnames2, columnsheetBranch);
//        createExcelData(indxsheet, columnsheet, tables, 1);
//        createExcelData(indxsheetBranch, columnsheetBranch, tables, 2);
        String impdatasql = makeImportDataSql(tables, headnames1, headnames2);
//        ExcelUtil.writeLargeDataToExcel(wkbook, outfile);

        return impdatasql;
    }
    
    private String makeImportDataSql(List<Table> tables, String[] tableColNames, String[] tableFieldsColNames) {
        StringBuilder sqlbuffer = new StringBuilder();
        int count = 0;
        int colcount = 0;
        try {
            String tablecolnam = (Arrays.toString(tableColNames)).replaceAll("\\[", "").replaceAll("\\]", "");
            String fieldcolnam = (Arrays.toString(tableFieldsColNames)).replaceAll("\\[", "").replaceAll("\\]", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            /**
             * 将要导入的模型写入PAAS平台的 IMP_TABLE_LIST 表。
             */
            tables.stream().map((table) -> "INSERT INTO IMP_TABLE_LIST VALUES ('" + table.getTableName() + "','" + table.getTableId() + "');\n").forEachOrdered((tableImp) -> {
                sqlbuffer.append(tableImp);
            });
            for (Table table : tables) {
                count++;
                sqlbuffer.append("\n");
                String insertsql1 = "insert into tablefile (" + tablecolnam + ") values ("
                        + "'" + table.getTableId() + "',"
                        + "'" + table.getDbServName() + "',"
                        + "'" + table.getTableName() + "',"
                        + "'" + table.getTableNameZh() + "',"
                        + "'" + table.getState() + "',"
                        + "'" + table.getCycleType() + "',"
                        + "'" + table.getTopicCode() + "',"
                        + "'" + table.getExtendcfg() + "',"
                        + "'" + table.getRightlevel() + "',"
                        + "'" + table.getCreater() + "',"
                        + "'" + table.getCurdutyer() + "',"
                        + " TIMESTAMP '" + sdf1.format(sdf.parse(table.getEffDate())) + "',"
                        + " TIMESTAMP '" + sdf1.format(sdf.parse(table.getStateDate())) + "',"
                        + "'" + table.getTeamCode() + "',"
                        + "'" + table.getOpenState() + "',"
                        + "'" + table.getRemark() + "');";
                sqlbuffer.append(insertsql1).append("\n");
                List<TableCol> tablecols = table.getTablecols();
                if (tablecols != null) {
                    for (TableCol tablecol : tablecols) {
                        if (tablecol.getDataType().startsWith("DECIMAL")) {
                            tablecol.setDataType("DECIMAL");//长度和精度在后面两列记录
                        }
                        String isIsPrimaryKey = tablecol.isIsPrimaryKey() ? "1" : "";
                        String isIsNullable = tablecol.isIsNullable() ? "Y" : "N";
                        String insertsql2 = "insert into column_val (" + fieldcolnam + ") values ("
                                + "'" + tablecol.getTableId() + "',"
                                + "'" + tablecol.getColumnId() + "',"
                                + "'" + tablecol.getTableName() + "',"
                                + "'" + tablecol.getColumnSeq() + "',"
                                + "'" + tablecol.getColumnName() + "',"
                                + "'" + tablecol.getColumnNameZh() + "',"
                                + "'" + tablecol.getDataType() + "',"
                                + "'" + tablecol.getLength() + "',"
                                + "'" + tablecol.getPrecision() + "',"
                                + "'" + tablecol.getPartitionSeq() + "',"
                                + "'" + isIsPrimaryKey + "',"
                                + "'" + isIsNullable + "',"
                                + "'" + tablecol.getRemark().replaceAll("'", "\"") + "',"
                                + "'" + tablecol.getSecurityType3() + "',"
                                + "'" + tablecol.getSensitivityLevel() + "',"
                                + "'" + tablecol.getOutSensitivityId() + "');";
                        sqlbuffer.append(insertsql2).append("\n");
                        colcount++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlbuffer.toString();
    }

    /**
     * 根据Table对象生成excel记录
     *
     * @param indxsheet 模型索引工作表
     * @param columnsheet 模型结构工作表
     * @param tables 待写入excel的表对象
     * @param tableType 1:导出主模型 2：导出地市共享模型
     */
    private void createExcelData(Sheet indxsheet, Sheet columnsheet, List<Table> tables, int tableType) {
        int indxrowcnt = 1;
        int colrowcnt = 1;
        for (Table table : tables) {
            if ((tableType == 1 && !table.isMainTable()) || (tableType == 2 && table.isMainTable())) {
                continue;
            }
            Row row = indxsheet.createRow(indxrowcnt++);
            int cellcnt1 = 0;
            row.createCell(cellcnt1++).setCellValue(table.getTableId());
            row.createCell(cellcnt1++).setCellValue(table.getDbServName());
            row.createCell(cellcnt1++).setCellValue(table.getTableName());
            row.createCell(cellcnt1++).setCellValue(table.getTableNameZh());
            row.createCell(cellcnt1++).setCellValue(table.getState());
            row.createCell(cellcnt1++).setCellValue(table.getCycleType());
            row.createCell(cellcnt1++).setCellValue(table.getTopicCode());
            row.createCell(cellcnt1++).setCellValue(table.getExtendcfg());
            row.createCell(cellcnt1++).setCellValue(table.getRightlevel());
            row.createCell(cellcnt1++).setCellValue(table.getCreater());
            row.createCell(cellcnt1++).setCellValue(table.getCurdutyer());
            row.createCell(cellcnt1++).setCellValue(table.getEffDate());
            row.createCell(cellcnt1++).setCellValue(table.getStateDate());
            row.createCell(cellcnt1++).setCellValue(table.getTeamCode());
            row.createCell(cellcnt1++).setCellValue(table.getOpenState());
            row.createCell(cellcnt1++).setCellValue(table.getRemark());
            if (table.getTablecols() != null) {
                for (TableCol tablecol : table.getTablecols()) {
                    Row colrow = columnsheet.createRow(colrowcnt++);
                    int cellcnt2 = 0;
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getTableId());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getColumnId());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getTableName());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getColumnSeq());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getColumnName());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getColumnNameZh());
                    if (tablecol.getDataType().startsWith("DECIMAL")) {
                        tablecol.setDataType("DECIMAL");//长度和精度在后面两列记录
                    }
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getDataType());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getLength());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getPrecision());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getPartitionSeq());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.isIsPrimaryKey() ? "1" : "");
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.isIsNullable() ? "Y" : "N");
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getRemark());
                    colrow.createCell(cellcnt2++).setCellValue(tablecol.getSecurityType3());
                }
            }
        }
    }

    private List<Table> exportTablesForDacp(List<String> tableList, Map<String, Table> metaDataMap, String[] branches) {
        List<Table> childtables = new ArrayList();//地市共享模型集合
        List<Table> maintables = new ArrayList();//主模型集合
        LocalDate today = LocalDate.now();
        String currentMonth = today.format(DateTimeFormatter.ofPattern("yyyyMM"));
        List<String> tableIdList = TextUtil.readTxtFileToList("tableid.txt", true);
        int counttable = 0;
        for (String tablename : metaDataMap.keySet()) {
            Table table = metaDataMap.get(tablename);
            /**
             * 只处理指定模型，其他模型跳过。
             */
            if (tableList != null && !tableList.isEmpty() && !tableList.contains(table.getTableName())) {
                continue;
            }
            table.setCreater("谢英俊");
            table.setCurdutyer("谢英俊");
            for (String tableIdStr : tableIdList) {
                String[] tableIdArray = tableIdStr.split(",");
                if (tableIdArray[0].equalsIgnoreCase(table.getTableName())
                        && tableIdArray[1] != null
                        && tableIdArray[1].trim().length() > 0) {
                    table.setTableId(tableIdArray[1]);//从PAAS平台导出的模型唯一标识XMLID
                    table.getTablecols().forEach((tablecol) -> {
                        tablecol.setTableId(table.getTableId());
                    });
                    break;
                }
            }
            if (table.isShared()) {
                counttable++;
                for (String branch : branches) {
                    if(branch.equalsIgnoreCase("OTH")){
                        continue;
                    }
                    Table branchTable = (Table) table.clone();
                    branchTable.setMainTable(false);
                    branchTable.setTableName(tablename + "_" + branch);
                    branchTable.setTableId(branchTable.getTableName());
                    for (String tableIdStr : tableIdList) {
                        String[] tableIdArray = tableIdStr.split(",");
                        if (tableIdArray[0].equalsIgnoreCase(branchTable.getTableName())
                                && tableIdArray[1] != null
                                && tableIdArray[1].trim().length() > 0) {
                            branchTable.setTableId(tableIdArray[1]);//从PAAS平台导出的模型唯一标识XMLID
                            branchTable.getTablecols().forEach((tablecol) -> {
                                tablecol.setTableId(table.getTableId());
                            });
                            break;
                        }
                    }
                    /**
                     * 如果是共享全省数据，则地市模型分区指向主模型的GMCC分区。否则指向主表的地市分区。
                     */
                    if (branchTable.isShareAllDataToCity()) {
                        branchTable.setLocation(branchTable.getLocation() + "/branch=GMCC");
                    } else {
                        branchTable.setLocation(branchTable.getLocation() + "/branch=" + branch);
                    }
                    /**
                     * 地市模型需要去掉地市分区字段branch
                     */
                    String[] partycols = branchTable.getPartitionCols();
                    String[] newpartycols = new String[partycols.length - 1];
                    int i = 0;
                    for (String partycol : partycols) {
                        if (!partycol.equalsIgnoreCase("branch")) {
                            newpartycols[i++] = partycol;
                        }
                    }
                    branchTable.setPartitionCols(newpartycols);
                    List<TableCol> tablecols = new ArrayList<>();
                    branchTable.getTablecols().stream().filter((branchTablecol) -> (!branchTablecol.getColumnName().equalsIgnoreCase("branch"))).map((branchTablecol) -> {
                        if (branchTablecol.getColumnName().equalsIgnoreCase("month")
                                || branchTablecol.getColumnName().equalsIgnoreCase("day")) {
                            int colseq = Integer.parseInt(branchTablecol.getColumnSeq()) - 1;
                            int partyseq = Integer.parseInt(branchTablecol.getPartitionSeq()) - 1;
                            branchTablecol.setColumnSeq(String.valueOf(colseq));
                            branchTablecol.setPartitionSeq(String.valueOf(partyseq));
                        }
                        return branchTablecol;
                    }).forEachOrdered((branchTablecol) -> {
                        tablecols.add(branchTablecol);
                    });
                    branchTable.setTablecols(tablecols);
                    String extendcfg = "{\"EXTERNAL\":\"y\",\"LOCATION\":\"" + branchTable.getLocation() + "\",\"FILEFORMAT\":\"" + branchTable.getStoredFormat() + "\",\"DELIMITER\":\"" + branchTable.getColDelimiter() + "\"}";
                    branchTable.setExtendcfg(extendcfg);
                    if (branchTable.getTablecols() != null) {
                        branchTable.getTablecols().stream().map((tablecol) -> {
                            /**
                             * 地市模型的列是从主表复制过来的，所以复制过来后需要将列对象中的表名改为地市模型，模型ID也需要改成地市模型的ID。
                             */
                            tablecol.setTableName(branchTable.getTableName());
                            return tablecol;
                        }).map((tablecol) -> {
                            tablecol.setTableId(branchTable.getTableId());
                            return tablecol;
                        }).forEach((tablecol) -> {
                            tablecol.setColumnId(tablecol.getTableName() + "_" + currentMonth + tablecol.getColumnSeq());
                        });
                    } else {
                        System.out.println(table.getTableName() + " has no table columns!");
                    }
                    childtables.add(branchTable);
                }
            }else{
                System.out.println("not share:"+table.getTableName());
            }
            maintables.add(table);
        }
        //maintables.addAll(childtables);
        System.out.println("需拆分主模型数量：" + counttable);
        return maintables;
    }
}
