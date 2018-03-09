/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.handler.impl;

import com.snal.beans.ModelChangeReq;
import com.snal.beans.Table;
import com.snal.common.TableUtil;
import com.snal.handler.IModelUpgradeHandler;
import com.snal.service.IModelUpgradeHiveService;
import com.snal.service.impl.HiveModelUpgradeService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Luo Tao
 */
public class HiveModelUpgradeHanlder implements IModelUpgradeHandler {

    @Override
    public Map<String, StringBuilder> makeModelUpgradeSql(List<ModelChangeReq> reqList, Map<String, Table> metaMap, Properties properties) {
        Map<String, StringBuilder> sqlMap = new HashMap<>();//key：租户名

        String[] branches = properties.getProperty("branch.code.value").split(",");//地市简称
        boolean outMainTable = Boolean.parseBoolean(properties.getProperty("script.output.main.table"));//是否输出主模型脚本
        boolean outBranchTable = Boolean.parseBoolean(properties.getProperty("script.output.share.table"));//是否输出地市模型脚本

        IModelUpgradeHiveService upgradeHiveService = new HiveModelUpgradeService();
        StringBuilder chekPKRuleBuffer = new StringBuilder();
        for (ModelChangeReq changeReq : reqList) {
            Table mainTable = metaMap.get(changeReq.getTableName());
            List<Table> branchTables = TableUtil.cloneTableForBranch(mainTable, branches);

            StringBuilder sqlbuffer = sqlMap.get(mainTable.getTenantUser());
            if (sqlbuffer == null) {
                sqlbuffer = new StringBuilder();
                sqlMap.put(mainTable.getTenantUser(), sqlbuffer);
            }

            switch (changeReq.getChangeType()) {
                //新建表
                case 1: {
                    if (outMainTable) {
                        String mainTableSql = upgradeHiveService.createTable(mainTable);
                        sqlbuffer.append(mainTableSql).append("\n");
                    }
                    if (outBranchTable) {
                        for (Table branchTable : branchTables) {
                            String branchTableSql = upgradeHiveService.createTable(branchTable);
                            sqlbuffer.append(branchTableSql).append("\n");
                        }
                    }
                    break;
                }
                //追加字段
                case 2: {
                    String[] newColumns = changeReq.getChangeContent().split(",");
                    if (outMainTable) {
                        String mainTableSql = upgradeHiveService.addColumns(mainTable, newColumns);
                        sqlbuffer.append(mainTableSql).append("\n");
                    }
                    if (outBranchTable) {
                        for (Table branchTable : branchTables) {
                            String branchTableSql = upgradeHiveService.addColumns(branchTable, newColumns);
                            sqlbuffer.append(branchTableSql);
                        }
                    }
                    break;
                }
                //修改字段
                case 3: {
                    String[] columnPairs = changeReq.getChangeContent().split(",");
                    if (outMainTable) {
                        String mainTableSql = upgradeHiveService.modifyColumns(mainTable, columnPairs);
                        sqlbuffer.append(mainTableSql).append("\n");
                    }
                    if (outBranchTable) {
                        for (Table branchTable : branchTables) {
                            String branchTableSql = upgradeHiveService.modifyColumns(branchTable, columnPairs);
                            sqlbuffer.append(branchTableSql);
                        }
                    }
                }
                break;
            }
            String checkPKRule = upgradeHiveService.createPKCheckRule(mainTable);
            chekPKRuleBuffer.append(checkPKRule);
            sqlMap.put("CTL.HIVE_CHECK_RULE", chekPKRuleBuffer);
        }
        return sqlMap;
    }
}
