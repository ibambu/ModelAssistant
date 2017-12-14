/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.util.text.TextUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luo Tao
 */
public class FilterMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String filename = "C:\\Users\\yx\\Desktop\\111.txt";
        String[] startStrArray = {"TMP", "SESSION", "TEMP"};
        String[] endStrArray = {"TMP", "SESSION", "_OTH", "TEMP"};
        List<String> lines = TextUtil.readTxtFileToList(filename, false);
        List<String> alist = new ArrayList();

        lines.forEach((line) -> {
            boolean mathflag = false;
            if (!line.startsWith("TMP")
                    && !line.startsWith("SESSION")
                    && !line.endsWith("_OTH")
                    && !line.startsWith("TEMP")) {
                int idx = line.indexOf("_");
                String moshi = "未知";
                if (idx != -1) {
                    moshi = line.substring(0, idx);
                    switch (moshi) {
                        case "TW":
                            moshi = "EDS";
                            break;
                        case "TM":
                            moshi = "DM";
                            break;
                        case "TO":
                            moshi = "ODS";
                            break;
                        case "TS":
                            moshi = "STAGE";
                            break;
                        case "TR":
                            moshi = "REF";
                            break;
                        default:
                            moshi = "未知";
                            break;
                    }
                }
                String mainTableName = line;
                if (line.endsWith("_GZ")
                        || line.endsWith("_SZ")
                        || line.endsWith("_DG")
                        || line.endsWith("_FS")
                        || line.endsWith("_ST")
                        || line.endsWith("_ZH")
                        || line.endsWith("_HZ")
                        || line.endsWith("_ZS")
                        || line.endsWith("_JM")
                        || line.endsWith("_ZJ")
                        || line.endsWith("_SG")
                        || line.endsWith("_HY")
                        || line.endsWith("_MZ")
                        || line.endsWith("_SW")
                        || line.endsWith("_YJ")
                        || line.endsWith("_MM")
                        || line.endsWith("_ZQ")
                        || line.endsWith("_QY")
                        || line.endsWith("_CZ")
                        || line.endsWith("_JY")
                        || line.endsWith("_YF")) {
                    mainTableName = line.substring(0, line.length() - 3);
                }
                System.out.println("INSERT INTO IMP_TABLE_LIST VALUES ('"+line+"','"+line+"');\n");
            }
        });

    }

}
