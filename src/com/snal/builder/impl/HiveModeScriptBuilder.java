/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.builder.impl;

import com.snal.beans.Table;
import com.snal.builder.IModeScriptBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tao.luo
 */
public class HiveModeScriptBuilder implements IModeScriptBuilder {

    @Override
    public String createNewTable(Table table, String[] branchList, boolean isCreateBranchTable) {
        String hql = createHiveMainTableScript(branchList, table, isCreateBranchTable);
        return hql;
    }

    @Override
    public String reCreateTable(Table table, String[] branchList, boolean isCreateBranchTable) {
        /**
         * 1.生成建表语句
         */
        String hql = createHiveMainTableScript(branchList, table, isCreateBranchTable);
        /**
         * 2.生成重建分区语句
         */
        return hql;
    }

    @Override
    public String addColumns(Table table, String[] columns, String[] branchList) {
        List colsList = Arrays.asList(columns);
        StringBuilder hqlBuffer = new StringBuilder();
        hqlBuffer.append("ALTER TABLE ").append(table.getDbName()).append(".").append(table.getTableName());
        StringBuilder addColumnsStr = new StringBuilder(" ADD COLUMNS (");
        table.getTablecols().stream().filter((tableCol) -> (colsList.contains(tableCol.getColumnName()))).forEachOrdered((tableCol) -> {
            addColumnsStr.append(tableCol.getColumnName()).append(" ").append(tableCol.getDataType()).append(",");
        });
        addColumnsStr.deleteCharAt(addColumnsStr.length() - 1);//删除最后一个多余的“,”。
        addColumnsStr.append(");\n");
        hqlBuffer.append(addColumnsStr.toString());
        /**
         * 如果是共享地市模型，则修改分地市模型字段。
         */
        if (table.isShared()) {
            StringBuilder branchHqlBuffer = new StringBuilder();
            for (String branch : branchList) {
                branchHqlBuffer.append("ALTER TABLE ").append(table.getDbName())
                        .append(".").append(table.getTableName()).append("_").append(branch)
                        .append(addColumnsStr.toString());
            }
            hqlBuffer.append(branchHqlBuffer.toString());
        }
        return hqlBuffer.toString();
    }

    @Override
    public String modifyColumns(Table table, String[] modifyColumns, String[] branchList) {
        StringBuilder hqlBuffer = new StringBuilder();
        for (String modifyCol : modifyColumns) {
            hqlBuffer.append("ALTER TABLE ").append(table.getDbName()).append(".").append(table.getTableName());
            String[] colPair = modifyCol.split("-");
            String oldCol = colPair[0];
            String newCol = colPair[1];
            hqlBuffer.append(" CHANGE COLUMN ")
                    .append(oldCol).append(" ")
                    .append(newCol).append(" ")
                    .append(table.getTableCol(newCol).getDataType())
                    .append(";\n");
        }
        /**
         * 如果是共享地市模型，则修改分地市模型字段。
         */
        if (table.isShared()) {
            StringBuilder branchHqlBuffer = new StringBuilder();
            for (String branch : branchList) {
                for (String modifyCol : modifyColumns) {
                    hqlBuffer.append("ALTER TABLE ").append(table.getDbName()).append(".")
                            .append(table.getTableName()).append("_").append(branch);
                    String[] colPair = modifyCol.split("-");
                    String oldCol = colPair[0];
                    String newCol = colPair[1];
                    hqlBuffer.append(" CHANGE COLUMN ")
                            .append(oldCol).append(" ")
                            .append(newCol).append(" ")
                            .append(table.getTableCol(newCol).getDataType())
                            .append(";\n");
                }
            }
            hqlBuffer.append(branchHqlBuffer.toString());
        }
        return hqlBuffer.toString();
    }

    @Override
    public String modifyTableAttribute(Table table, String[] branches) {
        StringBuilder hqlbuffer = new StringBuilder();
        String modifyhql = "";
        String startDate = "";
        String endDate = "";
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfMon = new SimpleDateFormat("yyyyMM");

        
        Calendar oldday = Calendar.getInstance();
        Calendar nowDay = Calendar.getInstance();
        Calendar nowMon = Calendar.getInstance();
        try {
            oldday.setTime(sdfDay.parse(startDate));
            nowDay.setTime(sdfDay.parse(endDate));
            nowMon.setTime(sdfDay.parse(endDate));
        } catch (ParseException ex) {
            Logger.getLogger(HiveModeScriptBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
//        while(nowDay.after(oldday)){
//            hqlbuffer.append("perl ~schadm/dssprog/bin/remote_cli.pl bd_b beeline -e \"USE JCFW;");
//        }
        modifyhql = "ALTER TABLE " + table.getDbName() + "." + table.getTableName() + " SET SERDE '"
                + table.getSerdeClass()
                + "' WITH SERDEPROPERTIES ('field.delim'='"
                + table.getColDelimiter()
                + "','serialization.encoding'='"
                + table.getCharacterSet()
                + "','serialization.null.format'='NVL');";
        hqlbuffer.append(modifyhql).append("\n");
        if (table.isShared()) {
            for (String branch : branches) {
                String branchTablename = table.getTableName() + "_" + branch;
                hqlbuffer.append(modifyhql.replaceAll(table.getTableName(), branchTablename)).append("\n");
            }
        }
        return hqlbuffer.toString();
    }

    /**
     * 生成创建脚本字段语句
     *
     * @param table
     * @return
     */
    private String makeTableCreateScript(Table table) {
        StringBuilder sqlbuff = new StringBuilder();
        sqlbuff.append("DROP TABLE IF EXISTS ").append(table.getDbName()).append(".").append(table.getTableName()).append(";\r\n");
        sqlbuff.append("CREATE EXTERNAL TABLE ").append(table.getDbName()).append(".").append(table.getTableName()).append(" (").append("\r");
        table.getTablecols().stream().filter((tablecol) -> (tablecol.getPartitionSeq() == null
                || tablecol.getPartitionSeq().trim().length() == 0)).forEach((tablecol) -> {
            sqlbuff.append("   ").append(tablecol.getColumnName()).append("    ").append(tablecol.getDataType()).append(",\n");
        });
        sqlbuff.deleteCharAt(sqlbuff.length() - 2);//删除末尾多余的逗号。
        sqlbuff.append(")\r\n");
        return sqlbuff.toString();
    }

    /**
     * 生成设置表属性语句
     *
     * @param table
     * @return
     */
    public static String makeTablePropertiesScript(Table table) {
        List<String> partitioncols = new ArrayList();
        StringBuilder strbuffer = new StringBuilder();
        for (String partitionCol : table.getPartitionCols()) {
            //地市共享模型不需要建branch分区
            if (!table.isMainTable() && partitionCol.equals("branch")) {
                continue;
            }
            partitioncols.add(partitionCol);
        }
        String partitionstr = "";
        if (!partitioncols.isEmpty()) {
            for (int i = 0; i < partitioncols.size(); i++) {
                String datatype = partitioncols.get(i).equals("branch") ? " STRING" : " INT";
                if (partitionstr == null || partitionstr.trim().length() == 0) {
                    partitionstr = partitioncols.get(i) + datatype;
                } else {
                    partitionstr += "," + partitioncols.get(i) + datatype;
                }
            }
        }
        if (partitionstr != null && partitionstr.trim().length() > 0) {
            strbuffer.append("PARTITIONED BY (").append(partitionstr).append(")").append("\r\n");
        }
        strbuffer.append("ROW FORMAT SERDE ").append("'").append(table.getSerdeClass()).append("'").append("\r\n")
                .append("WITH SERDEPROPERTIES ('field.delim'='").append(table.getColDelimiter()).append("'").append(",")
                .append("'serialization.null.format' ='NVL'").append(",")
                .append("'serialization.encoding' ='").append(table.getCharacterSet()).append("')\r\n")
                .append("STORED AS ").append(table.getStoredFormat()).append("\r\n")
                .append("LOCATION '").append(table.getLocation()).append("';\r\n\r\n");

        return strbuffer.toString();
    }

    /**
     * 根据主表生成地市共享模型建表语句，地市共享模型分区是在主表分区的基础上去掉地市分区。 如果主表只按地市分区，则共享模型不分区。
     *
     * @param branches
     * @param table
     * @param failtables
     * @param successtables
     * @return
     */
    private String createHiveBranchTableScript(String[] branches, Table table) {
        StringBuilder sqlbuff = new StringBuilder();
        for (String branch : branches) {
            Table branchTable = (Table) table.clone();
            branchTable.setMainTable(false);
            branchTable.setTableName(table.getTableName() + "_" + branch);
            if (branchTable.isShareAllDataToCity()) {
                branchTable.setLocation(branchTable.getLocation() + "/branch=GMCC");
            } else {
                branchTable.setLocation(branchTable.getLocation() + "/branch=" + branch);
            }
            String tablecols = makeTableCreateScript(branchTable);
            sqlbuff.append(tablecols);//表字段语句
            String tablepropertes = makeTablePropertiesScript(branchTable);
            sqlbuff.append(tablepropertes);//表属性语句
            if (tablecols != null && tablecols.length() > 0 && tablepropertes != null && tablepropertes.length() > 0) {
                System.out.println("[OK][Shared Table]" + branchTable.getTableName());
            } else {
                System.out.println("[Fail]" + branchTable.getTableName());
                return null;
            }
        }
        return sqlbuff.toString();
    }

    private String createHiveMainTableScript(String[] branches, Table table, boolean outputSharedTable) {
        StringBuilder sqlbuffer = new StringBuilder();
        String tablecols = makeTableCreateScript(table);
        sqlbuffer.append(tablecols);//表字段语句

        String tableproperties = makeTablePropertiesScript(table);
        sqlbuffer.append(tableproperties);//表属性语句
        if (table.isConstantParam()) {
            sqlbuffer.append("ALTER TABLE JCFW.").append(table.getTableName()).append(" ADD PARTITION (branch='GMCC');\n");
        }
        if (tablecols != null && tablecols.length() > 0 && tableproperties != null && tableproperties.length() > 0) {
            System.out.println("[OK][Main Table]" + table.getTableName());
        } else {
            System.out.println("[Fail]" + table.getTableName());
            return null;
        }
        /**
         * 如果是地市共享模型，则生成创建地市共享模型脚本。
         */
        if (table.isShared() && outputSharedTable) {
            String branchTableHql = createHiveBranchTableScript(branches, table);
            if (branchTableHql != null && branchTableHql.trim().length() > 0) {
                sqlbuffer.append(branchTableHql).append("\r\n");
            }
        }
        return sqlbuffer.toString();
    }

    @Override
    public String exportTableToPaas(Table table, String[] branchList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String reAddPartitions(Table table, String[] branchList, boolean isBranch) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
