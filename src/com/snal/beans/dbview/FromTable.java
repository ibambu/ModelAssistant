/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.beans.dbview;

import java.util.List;

/**
 *
 * @author csandy
 */
public class FromTable {

    private String name;
    private List<FromCol> fromCols;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FromCol> getFromCols() {
        return fromCols;
    }

    public void setFromCols(List<FromCol> fromCols) {
        this.fromCols = fromCols;
    }

}
