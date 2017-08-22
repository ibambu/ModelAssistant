/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.handler.impl;

import com.snal.beans.Comment;
import com.snal.beans.Require;
import com.snal.beans.Table;
import com.snal.beans.TableOP;
import com.snal.builder.IModeScriptBuilder;
import com.snal.builder.impl.HiveModeScriptBuilder;
import com.snal.handler.IModelScriptHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author csandy
 */
public class HiveModelScriptHandler implements IModelScriptHandler {

    @Override
    public String makeScript(Require require, Properties configuration, Map<String, Table> metaData, List<String> cmdList) {
        List<TableOP> tableOpList = IModelScriptHandler.super.parse(cmdList, require);
        StringBuilder scriptBuffer = new StringBuilder();
        String[] branchList = configuration.getProperty("branch.code.value").split(",");//地市简称
        /**
         * 解析命令
         */
        Map<String, StringBuilder> hqlbufferMap = new HashMap<>();
        IModeScriptBuilder scriptBuilder = new HiveModeScriptBuilder();
        for (TableOP tableOp : tableOpList) {
            Table table = metaData.get(tableOp.getTableName().toUpperCase());
            if (table == null) {
                System.out.println(tableOp.getTableName() + " 模型不存在.");
                continue;
            }
            String hql = null;
            String optype = tableOp.getOptype();
            switch (optype) {
                case "NEW": {
                    hql = scriptBuilder.createNewTable(table, branchList, true);
                    break;
                }
                case "RENEW": {
                    hql = scriptBuilder.reCreateTable(table, branchList, true);
                    break;
                }
                case "ADD-COLS": {
                    hql = scriptBuilder.addColumns(table, tableOp.getColumns(), branchList);
                    break;
                }
                case "MOD-COLS": {
                    hql = scriptBuilder.modifyColumns(table, tableOp.getColumns(), branchList);
                    break;
                }
                case "MOD-ATTR": {
                    hql = scriptBuilder.modifyTableAttribute(table, branchList);
                    break;
                }
                case "EXPORT": {
                    hql = scriptBuilder.exportTableToPaas(table, branchList);
                    break;
                }
                case "READD-PARTITION": {
                    hql = scriptBuilder.reAddPartitions(table, branchList,true);
                    break;
                }
            }
            StringBuilder buffer = hqlbufferMap.get(table.getTenantUser());
            if (buffer == null) {
                buffer = new StringBuilder(Comment.startComment(table.getTenantUser() + "\n"));
                hqlbufferMap.put(table.getTenantUser(), buffer);
            }
            buffer.append(hql).append("\n");
        }
        hqlbufferMap.keySet().forEach((key) -> {
            scriptBuffer.append(hqlbufferMap.get(key).toString()).append("\n");
        });
        return scriptBuffer.toString();
    }
}
