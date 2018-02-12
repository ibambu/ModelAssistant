/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.service;

import com.snal.beans.Table;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luo Tao
 */
public interface IModelSyncPaasService {

    /**
     * 返回同步到PaaS的 SQL 语句。
     *
     * @param tableList 模型列表
     * @param modelMap 元数据字典数据
     * @param branches 地市简拼数组，如 GZ,HZ,DG,FS,...,...
     * @return
     */
    public String syncModelToPaas(List<String> tableList, Map<String, Table> modelMap, String[] branches);

    /**
     * 返回同步到内部模型查询平台的 SQL 语句。
     *
     * @param tableList
     * @param modelMap
     * @param branches
     * @return
     */
    public String syncModelToQRY(List<String> tableList, Map<String, Table> modelMap, String[] branches);
}
