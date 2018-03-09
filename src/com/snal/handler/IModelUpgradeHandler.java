/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.handler;

import com.snal.beans.ModelChangeReq;
import com.snal.beans.Table;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Luo Tao
 */
public interface IModelUpgradeHandler {
    
    public Map<String,StringBuilder> makeModelUpgradeSql(List<ModelChangeReq> reqList,
            Map<String, Table> metaMap, Properties properties);
    
}
