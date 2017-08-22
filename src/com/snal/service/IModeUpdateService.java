/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.service;

import com.snal.beans.Table;
import java.util.List;

/**
 *
 * @author tao.luo
 */
public interface IModeUpdateService {

    /**
     * 创建Hive数据库模型脚本
     *
     * @param table
     * @param branches
     * @param failtables
     * @param successtables
     * @param optype
     * @return
     * @throws Exception
     */
    public String createHiveTableScript(Table table, String[] branches,
            List failtables, List successtables, String optype) throws Exception;

    /**
     * 创建Hive模型升级相关的命令
     *
     * @param table
     * @param branches
     * @param optype
     * @return
     * @throws Exception
     */
    public String createHiveTableCmd(Table table, String[] branches, String optype) throws Exception;

    /**
     * 创建DB2数据库模型脚本
     *
     * @param table
     * @param branches
     * @param optype
     * @return
     * @throws Exception
     */
    public String createDB2TableScript(Table table, String[] branches, String optype) throws Exception;

    /**
     * 创建DB2数据库模型升级相关命令
     *
     * @param table
     * @param branches
     * @param optype
     * @return
     * @throws Exception
     */
    public String createDB2TableCmd(Table table, String[] branches, String optype) throws Exception;
}
