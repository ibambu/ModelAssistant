/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.service.impl;

import com.snal.beans.Table;
import com.snal.beans.TableCol;
import com.snal.common.TableUtil;
import com.snal.service.IModelSyncPaasService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luo Tao
 */
public class HiveModelSyncPaasService implements IModelSyncPaasService {

    @Override
    public String syncModelToPaas(List<String> tableList, Map<String, Table> modelMap, String[] branches) {
        StringBuilder sqlbuffer = new StringBuilder();
        List<Table> mTableList = new ArrayList();
        modelMap.keySet().stream().map((tablename) -> modelMap.get(tablename)).filter((mainTable) -> !(tableList != null && !tableList.isEmpty() && !tableList.contains(mainTable.getTableName()))).map((mainTable) -> {
            //只处理指定模型，其他模型跳过。
            mTableList.add(mainTable);
            return mainTable;
        }).map((mainTable) -> TableUtil.cloneTableForBranch(mainTable, branches)).forEachOrdered((branchTableList) -> {
            mTableList.addAll(branchTableList);
        });
        //生成写入 paas 平台的 tablefile_imp_temp,column_val_imp_temp 临时表的语句，这两个表用于存储当前最新模型信息。
        String[] headnames1 = {"xmlid", "dbname", "dataname", "datacnname", "state", "cycletype", "topiccode", "extend_cfg", "rightlevel", "creater",
            "curdutyer", "eff_date", "state_date", "team_code", "open_state", "remark"};
        String[] headnames2 = {"xmlid", "col_xmlid", "dataname", "col_seq", "colname", "colcnname", "datatype", "length",
            "precision_val", "party_seq", "isprimarykey", "isnullable", "remark", "filed_type_child", "sensitive_level", "policyid"};
        String sqlToTemp = updateTableToTemp(mTableList, headnames1, headnames2);
        sqlbuffer.append(sqlToTemp);
        //恢复模型状态
        String sqlToRecoverSts = "update tablefile_imp_temp a set state = (select state from tablefile b where a.xmlid=b.xmlid) where exists (select 1  from tablefile c where a.xmlid=c.xmlid);";
        sqlbuffer.append(sqlToRecoverSts).append("\n\n");
        //将要同步到 paas 平台的模型信息从 paas 正式表 tablefile,column_val 删除。
        sqlbuffer.append("delete from md.tablefile a where xmlid in (select xmlid from md.tablefile_imp_tem);\n");
        sqlbuffer.append("delete from md.column_val a where xmlid in (select xmlid from md.tablefile_imp_tem);\n");
        sqlbuffer.append("delete from md.metaobj where xmlid in (select xmlid from md.tablefile_imp_tem);\n");
        sqlbuffer.append("delete from md.tableall where xmlid in(select xmlid from md.tablefile_imp_tem);\n");
        sqlbuffer.append("delete from mds.tablefile a where xmlid in (select xmlid from md.tablefile_imp_tem);\n");
        sqlbuffer.append("delete from mds.column_val a where xmlid in (select xmlid from md.tablefile_imp_tem);\n");
        sqlbuffer.append("delete from mds.metaobj where xmlid in (select xmlid from md.tablefile_imp_tem);\n");
        sqlbuffer.append("delete from mds.tableall where xmlid in(select xmlid from md.tablefile_imp_tem);\n");
        //写入最新模型信息到 PAAS 平台TABLEFILE和COLUMN_VAL两个表。
        String sqlToTableFile = "insert into md.tablefile( xmlid, dbname, dataname, datacnname, state, cycletype, topiccode, extend_cfg, rightlevel, creater, curdutyer, eff_date, state_date, team_code, open_state, remark )select  xmlid, dbname, dataname, datacnname, state, cycletype, topiccode, extend_cfg, rightlevel, creater, curdutyer, eff_date, state_date, team_code, open_state, remark from md.tablefile_imp_temp;\n";
        String sqlToColumVal = "insert into md.column_val( xmlid, col_xmlid, dataname, col_seq, colname, colcnname, datatype, length, precision_val, party_seq, isprimarykey, isnullable, remark, filed_type_child, sensitive_level, policyid )select  xmlid, col_xmlid, dataname, col_seq, colname, colcnname, datatype, length, precision_val, party_seq, isprimarykey, isnullable, remark, filed_type_child, sensitive_level, policyid from md.column_val_imp_temp;\n";
        sqlbuffer.append("\n")
                .append(sqlToTableFile)
                .append(sqlToColumVal)
                .append(sqlToTableFile.replaceAll("insert into md.tablefile", "insert into mds.tablefile"))
                .append(sqlToColumVal.replaceAll("insert into md.column_val", "insert into mds.column_val"));
        //写入最新元数据对象信息至PAAS平台的METAOBJ和TABLEALL两个表。
        String sqlToMetaObj = "insert into md.metaobj (xmlid, dbname, objname, objcnname, objtype, team_code, cycletype, topiccode, eff_date, creater, state, state_date, remark) select  xmlid, dbname, dataname, datacnname, 'tab', team_code, cycletype, topiccode, eff_date, creater, state, state_date, remark from tablefile where xmlid  in (select xmlid from md.tablefile_imp_temp);\n";
        String sqlToTableAll = "insert into md.tableall (dbname,dataname,eff_date,xmlid,modeltab,creator,taskid,dropdate) select  dbname,  dataname,  eff_date,  xmlid,  dataname,  '谢英俊',  '20161101',  '9999/12/31' from tablefile  where xmlid  in (select xmlid from md.tablefile_imp_temp);\n";
        sqlbuffer.append(sqlToMetaObj)
                .append(sqlToTableAll)
                .append(sqlToMetaObj.replaceAll("insert into md.metaobj", "insert into mds.metaobj"))
                .append(sqlToTableAll.replaceAll("insert into md.tableall", "insert into mds.metaobj"));
        return sqlbuffer.toString();
    }

    /**
     * 将要导入的模型从临时表中删除，然后写入最新的模型信息到临时表。
     *
     * @param tables
     * @return
     */
    private String updateTableToTemp(List<Table> tables, String[] colNames, String[] fieldsNames) {
        StringBuilder sqlbuffer = new StringBuilder();
        for (Table table : tables) {
            sqlbuffer.append("delete from tablefile_imp_temp where xmlid ='").append(table.getTableId()).append("';\n");
            sqlbuffer.append("delete from column_val_imp_temp where xmlid ='").append(table.getTableId()).append("';\n\n");
            String addsql = addTableToTemp(table, colNames, fieldsNames);
            sqlbuffer.append(addsql).append("\n");
        }
        return sqlbuffer.toString();
    }

    /**
     * 将一个模型索引及其字段写入到临时表。
     *
     * @param table
     * @param colNames
     * @param fieldsNames
     * @return
     */
    private String addTableToTemp(Table table, String[] colNames, String[] fieldsNames) {
        StringBuilder sqlbuffer = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            String tablecolnam = (Arrays.toString(colNames)).replaceAll("\\[", "").replaceAll("\\]", "");
            String fieldcolnam = (Arrays.toString(fieldsNames)).replaceAll("\\[", "").replaceAll("\\]", "");
            //生成写入 md.tablefile_imp_temp 的语句。
            sqlbuffer.append("insert into  md.tablefile_imp_temp (").append(tablecolnam).append(") values (")
                    .append("'").append(table.getTableId()).append("',")
                    .append("'").append(table.getDbServName()).append("',")
                    .append("'").append(table.getTableNameZh()).append("',")
                    .append("'").append(table.getState()).append("',")
                    .append("'").append(table.getCycleType()).append("',")
                    .append("'").append(table.getTopicCode()).append("',")
                    .append("'").append(table.getExtendcfg()).append("',")
                    .append("'").append(table.getRightlevel()).append("',")
                    .append("'").append(table.getCreater()).append("',")
                    .append("'").append(table.getCurdutyer()).append("',")
                    .append(" TIMESTAMP '").append(sdf1.format(sdf.parse(table.getEffDate()))).append("',")
                    .append(" TIMESTAMP '").append(sdf1.format(sdf.parse(table.getStateDate()))).append("',")
                    .append("'").append(table.getTeamCode()).append("',")
                    .append("'").append(table.getOpenState()).append("',")
                    .append("'").append(table.getRemark()).append("');\n");
            //生成写入 md.column_val_imp_temp 的语句。
            List<TableCol> tableColumns = table.getTablecols();
            if (tableColumns != null) {
                for (TableCol tablecol : tableColumns) {
                    if (tablecol.getDataType().startsWith("DECIMAL")) {
                        tablecol.setDataType("DECIMAL");//长度和精度在后面两列记录
                    }
                    String isIsPrimaryKey = tablecol.isIsPrimaryKey() ? "1" : "";
                    String isIsNullable = tablecol.isIsNullable() ? "Y" : "N";
                    sqlbuffer.append("insert into md.column_val_imp_temp (").append(fieldcolnam).append(") values (")
                            .append("'").append(tablecol.getTableId()).append("',")
                            .append("'").append(tablecol.getColumnId()).append("',")
                            .append("'").append(tablecol.getTableName()).append("',")
                            .append("'").append(tablecol.getColumnSeq()).append("',")
                            .append("'").append(tablecol.getColumnName()).append("',")
                            .append("'").append(tablecol.getColumnNameZh()).append("',")
                            .append("'").append(tablecol.getDataType()).append("',")
                            .append("'").append(tablecol.getLength()).append("',")
                            .append("'").append(tablecol.getPrecision()).append("',")
                            .append("'").append(tablecol.getPartitionSeq()).append("',")
                            .append("'").append(isIsPrimaryKey).append("',")
                            .append("'").append(isIsNullable).append("',")
                            .append("'").append(tablecol.getRemark().replaceAll("'", "\"")).append("',")
                            .append("'").append(tablecol.getSecurityType3()).append("',").append("'")
                            .append(tablecol.getSensitivityLevel()).append("',")
                            .append("'").append(tablecol.getOutSensitivityId()).append("');\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlbuffer.toString();
    }

    @Override
    public String syncModelToQRY(List<String> tableList, Map<String, Table> modelMap, String[] branches) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
