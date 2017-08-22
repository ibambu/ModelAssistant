/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.service;

import com.snal.beans.Table;
import com.snal.dataloader.ModeScriptBuilder;
import java.util.List;

/**
 *
 * @author tao.luo
 */
public class ModeUpdateServiceImpl implements IModeUpdateService {

    @Override
    public String createHiveTableScript(Table table, String[] branches, List failtables, List successtables, String optype) throws Exception {
        switch (optype) {
            case "1": {
                //新增模型
                String sqlstr = ModeScriptBuilder.createHiveMainTableScript(branches, table, failtables, successtables, true,false);
                break;
            }
            case "2": {
                //表结构发生变化
                
                //重建模型
                /**
                 * 1.将表重命名为 表名_BAK_时间  
                 *   eg: alter table tablename rename to tablename_bak_201702161602
                 * 2.将表数据迁移到备份目录
                 *   eg: hadoop fs -mkdir <备份目录>
                 *       hadoop fs -mv <表目录> <新目录>
                 * 3.修改表和表分区的location指向备份目录
                 * alter table tablename_bak_201702161602 set location <备份目录>
                 * alter table tablename_bak_201702161602 partition(branch='GMCC') set location <备份目录>/branch=GMCC'
                 * 4.按照最新表结构重建模型
                 * 5.将备份数据从备份表中写入新建模型（注意要插入对应分区）
                 *  insert into table xxx partition(xxx=xxx) select col1,col2,null,col3,null,col4 from tablename_bak_201702161602 where xxx=xxx
                 */
                String sqlstr = ModeScriptBuilder.createHiveMainTableScript(branches, table, failtables, successtables, true,false);
                
                break;
            }
            case "3": {
                //追加字段
                break;
            }
            case "4": {
                //修改模型属性
                break;
            }
            default: {
                break;
            }
        }
        return null;
    }

    @Override
    public String createHiveTableCmd(Table table, String[] branches, String optype) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createDB2TableScript(Table table, String[] branches, String optype) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createDB2TableCmd(Table table, String[] branches, String optype) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
