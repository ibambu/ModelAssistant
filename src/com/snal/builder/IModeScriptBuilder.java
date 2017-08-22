/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.builder;

import com.snal.beans.Table;

/**
 *
 * @author tao.luo
 */
public interface IModeScriptBuilder {

    public String createNewTable(Table table, String[] branchList, boolean isCreateBranchTable);

    public String reCreateTable(Table table, String[] branchList, boolean isCreateBranchTable);

    public String addColumns(Table table, String[] columns, String[] branchList);

    public String modifyColumns(Table table, String[] modifyColumns, String[] branchList);

    public String modifyTableAttribute(Table table, String[] branchList);

    public String reAddPartitions(Table table, String[] branchList,boolean isBranch);

    public String exportTableToPaas(Table table, String[] branchList);

}
