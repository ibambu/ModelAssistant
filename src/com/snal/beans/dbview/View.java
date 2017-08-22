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
public class View {

    private String name;
    private List<ViewCol> viewCols;
    private List<FromTable> fromTables;

    public View(String name) {
        this.name = name;
    }

    public List<ViewCol> getViewCols() {
        return viewCols;
    }

    public void setViewCols(List<ViewCol> viewCols) {
        this.viewCols = viewCols;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FromTable> getFromTables() {
        return fromTables;
    }

    public void setFromTables(List<FromTable> fromTables) {
        this.fromTables = fromTables;
    }

}
