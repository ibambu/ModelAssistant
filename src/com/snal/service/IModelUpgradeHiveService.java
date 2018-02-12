/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.service;

import com.snal.beans.Table;

/**
 *
 * @author Luo Tao
 */
public interface IModelUpgradeHiveService {

    public String createTable(Table table);

    /**
     * 增加字段（只适用在末尾追加字段）。
     *
     * @param table
     * @param newColumns
     * @return
     */
    public String addColumns(Table table, String[] newColumns);

    /**
     * 修改字段，新旧字段需成对出现，多对用因为逗号隔开。
     *
     * @param table
     * @param columnPairs {oldcol1-newcol1,oldcol2-newclol2,oldcol3-newcol3,...}
     * @return
     */
    public String modifyColumns(Table table, String[] columnPairs);
}
