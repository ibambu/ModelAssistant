/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.test;

import com.snal.util.text.TextUtil;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luo Tao
 */
public class SenceDataProcessMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String filename = "e:/tr_sence.txt";
        List<String> datalist = TextUtil.readTxtFileToList(filename, false);
        findSence(datalist);
    }

    public static void findSence(List<String> senceList) {
        Map<String, List<Sence>> senceMap = new HashMap();
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(4);
        //设置最小整数位数    
        nf.setMinimumIntegerDigits(4);

        for (String senceStr : senceList) {
            String[] senceArray = senceStr.split(",");
            Sence sence = new Sence(null, null, null, senceArray[0], senceArray[1], senceArray[2], senceArray[3], senceArray[4]);
            List<Sence> mylist = senceMap.get(sence.getBranchCode());
            if (mylist == null) {
                mylist = new ArrayList();
                senceMap.put(sence.getBranchCode(), mylist);
            }
            int memberCount = mylist.size();
            String seq = nf.format(memberCount + 1);
            sence.setSenceSeq(seq);
            sence.setSenceId("86" + sence.getCityId() + "-" + sence.getSenceTypeId() + "-" + sence.getSenceSeq());
            sence.setSenceCode(sence.getBranchCode() + sence.getSenceSeq());
            mylist.add(sence);
        }
        for (String key : senceMap.keySet()) {
            List<Sence> list = senceMap.get(key);
            for (Sence sence : list) {
                System.out.println(sence.toString());
            }
        }
    }
}
