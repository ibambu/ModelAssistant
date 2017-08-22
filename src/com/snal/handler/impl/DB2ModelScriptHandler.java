/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.handler.impl;

import com.snal.beans.Require;
import com.snal.beans.Table;
import com.snal.handler.IModelScriptHandler;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author csandy
 */
public class DB2ModelScriptHandler implements IModelScriptHandler {


    @Override
    public String makeScript(Require require, Properties configuration, Map<String, Table> metaData, List<String> cmdList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
