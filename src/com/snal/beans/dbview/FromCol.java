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
public class FromCol {
    private String fromTalbe;
    private String viewColName;
    private String viewColNameZh;
    private String viewValue;

    public FromCol(String fromTalbe, String viewColName, String viewColNameZh, String viewValue) {
        this.fromTalbe = fromTalbe;
        this.viewColName = viewColName;
        this.viewColNameZh = viewColNameZh;
        this.viewValue = viewValue;
    }

    
    public String getViewColNameZh() {
        return viewColNameZh;
    }

    public void setViewColNameZh(String viewColNameZh) {
        this.viewColNameZh = viewColNameZh;
    }

    
    public String getFromTalbe() {
        return fromTalbe;
    }

    public void setFromTalbe(String fromTalbe) {
        this.fromTalbe = fromTalbe;
    }

    
    public String getViewColName() {
        return viewColName;
    }

    public void setViewColName(String viewColName) {
        this.viewColName = viewColName;
    }

    public String getViewValue() {
        return viewValue;
    }

    public void setViewValue(String viewValue) {
        this.viewValue = viewValue;
    }

}
