/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.service.impl;

import com.snal.beans.Table;
import com.snal.beans.TableCol;
import com.snal.service.IModelUpgradeHiveService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luo Tao
 */
public class HiveModelUpgradeService implements IModelUpgradeHiveService {

    @Override
    public String createTable(Table table) {
        boolean isUseProxy = false;
        StringBuilder sqlbuffer = new StringBuilder();
        String tablecols = makeTableCreateScript(table, isUseProxy);
        sqlbuffer.append(tablecols);//表字段语句

        String tableproperties = makeTablePropertiesScript(table, isUseProxy);
        sqlbuffer.append(tableproperties);//表属性语句

        if (table.isConstantParam()) {
            sqlbuffer.append("\nALTER TABLE ").append(table.getDbName()).append(".").append(table.getTableName()).append(" ADD PARTITION (branch='GMCC');\n");
        }
        return sqlbuffer.toString();
    }

    @Override
    public String addColumns(Table table, String[] newColumns) {
        StringBuilder sqlbuffer = new StringBuilder();
        String newcols = "";
        List<TableCol> cols = table.getTablecols();
        for (String newColumn : newColumns) {
            TableCol col = table.getTableCol(newColumn);
            newcols += col.getColumnName() + " " + col.getDbType() + ",";
        }
        return sqlbuffer.toString();
    }

    @Override
    public String modifyColumns(Table table, String[] columnPairs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String makeTableCreateScript(Table table, boolean isUseProxy) {
        StringBuilder sqlbuff = new StringBuilder();
        if (isUseProxy) {
            sqlbuff.append("DROP TABLE IF EXISTS ").append(table.getDbName()).append(".").append(table.getTableName()).append(";");
            sqlbuff.append("CREATE EXTERNAL TABLE ").append(table.getDbName()).append(".").append(table.getTableName()).append(" (").append("");
            table.getTablecols().stream().filter((tablecol) -> (tablecol.getPartitionSeq() == null
                    || tablecol.getPartitionSeq().trim().length() == 0)).forEach((tablecol) -> {
                sqlbuff.append("   ").append(tablecol.getColumnName()).append("    ").append(tablecol.getDataType()).append(",");
            });
            sqlbuff.deleteCharAt(sqlbuff.length() - 1);//删除末尾多余的逗号。
            sqlbuff.append(")");
        } else {
            sqlbuff.append("DROP TABLE IF EXISTS ").append(table.getDbName()).append(".").append(table.getTableName()).append(";\n");
            sqlbuff.append("CREATE EXTERNAL TABLE ").append(table.getDbName()).append(".").append(table.getTableName()).append(" (").append("\n");
            table.getTablecols().stream().filter((tablecol) -> (tablecol.getPartitionSeq() == null
                    || tablecol.getPartitionSeq().trim().length() == 0)).forEach((tablecol) -> {
                sqlbuff.append("   ").append(tablecol.getColumnName()).append("    ").append(tablecol.getDataType()).append(",\n");
            });
            sqlbuff.deleteCharAt(sqlbuff.length() - 2);//删除末尾多余的逗号。
            sqlbuff.append(")\n");
        }
        return sqlbuff.toString();
    }

    /**
     * 生成设置表属性语句
     *
     * @param table
     * @param isUseProxy
     * @return
     */
    public static String makeTablePropertiesScript(Table table, boolean isUseProxy) {
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
            strbuffer.append("PARTITIONED BY (").append(partitionstr).append(")").append("");
            if (!isUseProxy) {
                strbuffer.append("\n");
            }
        }
        if (table.getStoredFormat().equalsIgnoreCase("PARQUET")
                || table.getStoredFormat().equalsIgnoreCase("ORC")) {
            if (isUseProxy) {
                strbuffer.append(" STORED AS ").append(table.getStoredFormat()).append("")
                        .append(" LOCATION '").append(table.getLocation()).append("'")
                        .append(" TBLPROPERTIES ('serialization.null.format' ='NVL'").append(",")
                        .append("'serialization.encoding' ='").append(table.getCharacterSet()).append("');");
            } else {
                strbuffer.append("STORED AS ").append(table.getStoredFormat()).append("\n")
                        .append("LOCATION '").append(table.getLocation()).append("'\n")
                        .append("TBLPROPERTIES ('serialization.null.format' ='NVL'").append(",")
                        .append("'serialization.encoding' ='").append(table.getCharacterSet()).append("');\n");
            }
        } else {
            if (isUseProxy) {
                strbuffer.append(" ROW FORMAT SERDE ").append("'").append(table.getSerdeClass()).append("'").append("")
                        .append(" WITH SERDEPROPERTIES ('field.delim'='").append(table.getColDelimiter()).append("'").append(",")
                        .append("'serialization.null.format' ='NVL'").append(",")
                        .append("'serialization.encoding' ='").append(table.getCharacterSet()).append("')")
                        .append(" STORED AS ").append(table.getStoredFormat()).append("")
                        .append(" LOCATION '").append(table.getLocation()).append("';");
            } else {
                strbuffer.append("ROW FORMAT SERDE ").append("'").append(table.getSerdeClass()).append("'").append("\n")
                        .append("WITH SERDEPROPERTIES ('field.delim'='").append(table.getColDelimiter()).append("'").append(",")
                        .append("'serialization.null.format' ='NVL'").append(",")
                        .append("'serialization.encoding' ='").append(table.getCharacterSet()).append("')\n")
                        .append("STORED AS ").append(table.getStoredFormat()).append("\n")
                        .append("LOCATION '").append(table.getLocation()).append("';\n");
            }
        }
        return strbuffer.toString();
    }
}
