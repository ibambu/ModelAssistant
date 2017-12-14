/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.util.text.TextUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luo Tao
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
//        findPK();
        String filename = "F:\\work\\GDNG3BASS\\06数据仓库\\10配置数据\\CTL.JOB_PARAM_DEF.txt";
        List<String> aa = TextUtil.readTxtFileToList(filename, false);
        for (String a : aa) {
            String[] params = a.split(",");
            if (params.length > 1) {
                String job_id = params[0];
                String param_id = params[1];
                if (job_id.endsWith("60") || job_id.endsWith("61")) {
                    if (param_id.replaceAll("\"", "").equals("loadtab")) {
                        String paramdef = params[6];
                        int indx = paramdef.indexOf("branch=");
                        if (indx != -1) {
                            paramdef = paramdef.substring(0, indx - 1);
                            int idx = paramdef.lastIndexOf("/");
                            if (idx != -1) {
                                paramdef = paramdef.substring(idx+1);
                            }
                        }
                        System.out.println(paramdef);
                    }
                }
            }
        }
//        getPrimaryKeys();
    }

    public static String getPrimaryKeys() {

        String keystr = "aa,bb,cc,";
        String[] bb = keystr.split(",");
        System.out.println(Arrays.toString(bb));
//        keystr = tablecols.stream().filter((col) -> (col.isIsPrimaryKey())).map((col) -> col.getColumnName() + ",").reduce(keystr, String::concat);
        if (keystr != null) {
            keystr = keystr.substring(0, keystr.length() - 1);
        }
        System.out.println(keystr);
        return keystr;
    }

    public static void findPK() {
        String file1 = System.getProperty("user.dir") + "/tableinfo.txt";
        String file2 = System.getProperty("user.dir") + "/keyinfo.txt";
        List<String> tablelist = TextUtil.readTxtFileToList(file1, false);
        List<String> keyList = TextUtil.readTxtFileToList(file2, false);
        Map<String, List<String>> keymap = new HashMap();
        for (String keys : keyList) {
            String[] keyinfo = keys.split("#");
            String mName = keyinfo[0];
            String key = null;
            String col = keyinfo[1];
            if (mName.contains("2016") || mName.contains("2017")) {
                int idx = mName.lastIndexOf("_");
                key = mName.substring(0, idx);
            } else {
                key = mName;
            }
            List<String> colums = keymap.get(key);
            if (colums == null) {
                colums = new ArrayList();
                keymap.put(key, colums);
            }
            if (!colums.contains(col)) {

                colums.add(col);
            }
        }
        for (String tablename : tablelist) {
            String keystr = "";
            List<String> keys = keymap.get(tablename);
            if (keys == null) {
                continue;
            }
            for (String key : keys) {
                keystr += key + ",";
            }
            int idx = keystr.lastIndexOf(",");
            System.out.println(tablename + "#" + keystr.substring(0, idx));
        }
    }

    public static void getLatesedTable() throws Exception {

        String file = System.getProperty("user.dir") + "/22222.txt";
        List<String> tableList = TextUtil.readTxtFileToList(file, false);
        List<String> aa = new ArrayList();
        String tableName = null;
        int maxTime = 0;
        String lastItem = null;
        for (String mName : tableList) {
            String myName = "";
            if (mName.contains("2016") || mName.contains("2017")) {
                int idx = mName.lastIndexOf("_");
                myName = mName.substring(0, idx);
            } else {
                myName = mName;
            }
            if (tableName == null || mName.contains(tableName)) {
                lastItem = mName;
            } else {
                aa.add(lastItem);
                System.out.println(lastItem);
                lastItem = mName;
            }
            tableName = myName;
//            System.out.println(tableName);
        }
    }

    public void test() {
        String[] branches = {"GZ", "SZ", "DG", "FS", "ST", "ZH", "HZ", "ZS", "JM", "ZJ", "SG", "HY", "MZ", "SW", "YJ", "MM", "ZQ", "QY", "CZ", "JY", "YF"};

        for (String branch : branches) {
            String sql = "msck repair table jcfw.TO_E_GBIU_U_HTTP_PAESE_CDR_" + branch + ";";
            System.out.println(sql);
        }
        String[] cols = {
            "LONGITUDE#STRING",
            "LATITUDE#STRING",
            "LOCATION_TYP#STRING",
            "DEVIATION_VALUE#STRING",
            "IOT_GROUP_CODE#STRING",
            "IOT_SUB_CODE#STRING",
            "IOT_FUNC_CODE#STRING",
            "SITE_TYP_ID#STRING",
            "SITE_TYP_NAM#STRING",
            "SITE_ID#STRING",
            "SITE_NAM#STRING",
            "SITE_CATEGO_ID#STRING",
            "SITE_CATEGO_NAM#STRING",
            "SR_ENTER_ID#STRING",
            "SR_ENTER_NAM#STRING",
            "SR_SERVER_ID#STRING",
            "SR_SERVER_NAM#STRING",
            "SR_SOURCE_ID#STRING",
            "SR_SOURCE_NAM#STRING",
            "SR_CONT_TYP_ID#STRING",
            "SR_CONT_TYP_NAM#STRING",
            "SR_SP_ID#STRING",
            "SR_SP_NAM#STRING",
            "SR_IS_FREE_BUS#STRING",
            "SR_ACTION_ID#STRING",
            "SR_ACTION_NAM#STRING",
            "KEYWD#STRING",
            "PARSE_1#STRING",
            "PARSE_2#STRING",
            "PARSE_3#STRING",
            "CONT_CATE_LV1#STRING",
            "CONT_CATE_LV2#STRING",
            "CONT_CATE_LV3#STRING",
            "CONT_CATE_LV4#STRING",
            "CONT_CATE_TYP_NAM#STRING",
            "CONT_CATE_NAM#STRING",
            "CONT_NAM#STRING"
        };
        StringBuilder sqlbuffer1 = new StringBuilder();
        for (String branch : branches) {
            boolean isAdd = false;
            sqlbuffer1.append("perl ~schadm/dssprog/bin/remote_cli.pl bd_o beeline -e \"use jcfw;");
            for (int i = 0; i < cols.length; i++) {
                String aa[] = cols[i].split("#");
                String sql = "";
                if (i == cols.length - 4) {
                    isAdd = true;
                }
                if (isAdd) {
                    sql = "ALTER TABLE JCFW.TO_E_GBIU_U_HTTP_PAESE_CDR_" + branch + " ADD COLUMNS(" + aa[0] + " " + aa[1] + ") CASCADE;";
                    sqlbuffer1.append(sql);
//                    System.out.println(sql);
                } else {
                    String bb[] = cols[i + 4].split("#");
                    sql = "ALTER TABLE JCFW.TO_E_GBIU_U_HTTP_PAESE_CDR_" + branch + " CHANGE " + bb[0] + " " + aa[0] + " " + aa[1] + " CASCADE;";
//                    System.out.println(sql);
                    sqlbuffer1.append(sql);
                }
            }
            sqlbuffer1.append("\"\n");
        }
//        System.out.println(sqlbuffer1);
        String[] months = {
            "201604",
            "201605",
            "201606",
            "201607",
            "201608",
            "201609",
            "201610",
            "201611",
            "201612",
            "201701",
            "201702",
            "201703",
            "201704",
            "201705",
            "201706",
            "201707",
            "201708",
            "201709",
            "201710",
            "201711",
            "201712"
        };
        String[] dirs = {
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=CZ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=DG",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=FS",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=GMCC",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=GZ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=HY",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=HZ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=JM",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=JY",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=MM",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=MZ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=QY",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=SG",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=ST",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=SW",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=SZ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=YF",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=YJ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=ZH",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=ZJ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=ZQ",
            "/tenant/BIGDATA/JCFW/JRCL/BD_O/ODS/TO_E_GBIU_U_HTTP_PAESE_CDR/branch=ZS"
        };
        for (String dir : dirs) {
            for (String month : months) {
//                System.out.println("perl ~schadm/dssprog/bin/remote_cli.pl bd_o hadoop fs -rm -r " + dir + "/month=" + month);
            }
        }
        StringBuilder sqlbuffer = new StringBuilder();
        for (String branch : branches) {
            for (String month : months) {
                String sql = "perl ~schadm/dssprog/bin/remote_cli.pl bd_o beeline -e \"use jcfw;ALTER TABLE JCFW.TO_E_GBIU_U_HTTP_PAESE_CDR_" + branch + " drop if exists partition (month=" + month + ");\"\n";
                sqlbuffer.append(sql);
            }
        }
//        System.out.println(sqlbuffer.toString());
    }
}
