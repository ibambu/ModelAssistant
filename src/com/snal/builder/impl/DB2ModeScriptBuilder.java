/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.builder.impl;

import com.snal.beans.Table;
import com.snal.builder.IModeScriptBuilder;

/**
 *
 * @author tao.luo
 */
public class DB2ModeScriptBuilder implements IModeScriptBuilder {

    @Override
    public String createNewTable(Table table, String[] branchList, boolean isCreateBranchTable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String reCreateTable(Table table, String[] branchList, boolean isCreateBranchTable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String addColumns(Table table, String[] columns, String[] branchList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String modifyColumns(Table table, String[] modifyColumns, String[] branchList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String modifyTableAttribute(Table table, String[] branchList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
