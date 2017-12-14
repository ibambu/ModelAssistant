/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.util.text.TextUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luo Tao
 */
public class NewTextMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
//        String filename = "E:\\zhdsjs.dim_scene_gd.log";
        String filename = "E:\\SEQ.txt";

        Map<String, Integer> seqMap = new HashMap();

        List<String> result = new ArrayList();
        StringBuilder sbd = new StringBuilder();
        List<String> alist = TextUtil.readTxtFileToList(filename, false);
        for (String line : alist) {
            Integer a = seqMap.get(line);
            if (a == null) {
                a = 1;
            } else {
                a++;
            }
            seqMap.put(line, a);
            System.out.println(line + "#" + a);
//            /*
//            if (line.contains("+-----") || line.contains("dim_scene_gd.vccity") || line.trim().length() == 0) {
//                continue;
//            }
//            result.add(line);
//            sbd.append(line.replaceAll(" ", "").replaceAll("\"", "")).append("\n");*/
//            String[] values = line.split("\\|");
//            System.out.println(values[1] + "%" + values[2] + "%" + values[3] + "%" + values[5] + "%" + values[6] + "%" + values[7]);
        }
//        TextUtil.writeToFile(sbd.toString(), "E://bb.TXT");
    }

}
