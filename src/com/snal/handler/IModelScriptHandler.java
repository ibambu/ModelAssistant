/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.handler;

import com.snal.beans.Require;
import com.snal.beans.Table;
import com.snal.beans.TableOP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author tao.luo
 */
public interface IModelScriptHandler {
    
    public default List<TableOP> parse(List<String> cmdList, Require require) {
        List<TableOP> tableOps = new ArrayList<>(cmdList.size());
        cmdList.stream().map((cmdline) -> cmdline.split("\\|")).forEachOrdered((cmdInfo) -> {
            TableOP tableOp = new TableOP();
            tableOp.setDataBase(cmdInfo[0].toUpperCase());//数据库类型
            tableOp.setTableName(cmdInfo[1].toUpperCase());//模型名
            tableOp.setOptype(cmdInfo[2].toUpperCase());//操作类型
            tableOp.setRequireNo(require.getRequestCode());
            tableOp.setRequireName(require.getRequestName());
            if (cmdInfo.length > 3) {
                tableOp.setColumns(cmdInfo[3].split(","));//操作字段
            }
            if (cmdInfo.length > 4) {
//                tableOp.(cmdInfo[3]);//操作字段
            }
            tableOps.add(tableOp);
        });
        return tableOps;
    }

    /**
     *
     * @param require
     * @param configuration
     * @param metaData
     * @param cmdList
     * @return
     */
    public String makeScript(Require require, Properties configuration, Map<String, Table> metaData, List<String> cmdList);
    
}
