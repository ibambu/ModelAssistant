/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.dataloader.PropertiesFileLoader;
import com.snal.util.text.TextUtil;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Luo Tao
 */
public class MainTableUtil {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         Properties prop = PropertiesFileLoader.loadConfData();
        List<String> all = TextUtil.readTxtFileToList("e:/all.txt", true);
        List<String> branchTables = TextUtil.readTxtFileToList("e:/lost_table_list.txt", true);
        String[] branches = prop.getProperty("branch.code.value").split(",");
        for(String mainTable:all){
            for(String branch:branches){
                if(branchTables.contains(mainTable+"_"+branch)){
                    System.out.println(mainTable);
                }
            }
        }
    }
    
}
