/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.beans.dbview;

/**
 *
 * @author csandy
 */
public class ViewCol {
    private String colName;
    private String colNameZh;

    public ViewCol(String colName, String colNameZh) {
        this.colName = colName;
        this.colNameZh = colNameZh;
    }

    
    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColNameZh() {
        return colNameZh;
    }

    public void setColNameZh(String colNameZh) {
        this.colNameZh = colNameZh;
    }
    
}
