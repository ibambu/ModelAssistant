/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.main;

import com.snal.beans.TenantAttribute;
import com.snal.beans.TableCol;
import com.snal.beans.Table;
import com.snal.dataloader.MetaDataLoader;
import com.snal.dataloader.PropertiesFileLoader;
import com.snal.util.excel.ExcelUtil;
import com.snal.util.text.TextUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author csandy
 */
public class ModeTxtProcessMain {

    public static final String[] TABLE_LIST = {
        "TR_P_LTECELL",};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        MetaDataLoader metaDataUtil = new MetaDataLoader();
        Properties prop = PropertiesFileLoader.loadConfData();

        Map<String, TenantAttribute> tenantMap = PropertiesFileLoader.initTenantAttribute(prop);
        String metaDataFile = prop.getProperty("hive.meta.data.file");//Ԫ�����ļ�·��
        String usrdir = System.getProperty("user.dir");
        String inputfile = usrdir + "\\" + prop.getProperty("input.file");//Ҫ�����ģ���б��ļ�
        String outputfile = usrdir + "\\" + prop.getProperty("output.file");//�ű�����ļ�
        int startsheet = Integer.parseInt(prop.getProperty("start.sheet.index"));
        int endsheet = Integer.parseInt(prop.getProperty("end.sheet.index"));
        String[] sheetmincell = prop.getProperty("sheet.min.cell.load").split(",");
        String[] branches = prop.getProperty("branch.code.value").split(",");
        int[] mincelltoread = {Integer.parseInt(sheetmincell[0]), Integer.parseInt(sheetmincell[1])};
        /**
         * ����Ԫ����
         */
//        Map<String, Table> tableMap = metaDataUtil.loadMetaData(metaDataFile, tenantMap, startsheet, endsheet, mincelltoread);
//        test(tableMap, branches);
        //updateSecurity(tableMap);
//        makeBranchTablePartition(tableMap, branches);
        //System.out.println(System.currentTimeMillis());
        //insertVersionLog();
//        bbb(branches);
//        exportCDRTables();
        //loadCellCdTableChanged();
        //ttt();
//        update3monthData();
//        String startDay = "20170701";
//        String endDay = "20170731";
//        List<String> gmccTables = new ArrayList();
//        gmccTables.add("TW_SHL_AREA_USE_DTL_MO");
//        gmccTables.add("TO_DNO_GPRS_CDR");
//        gmccTables.add("TO_INTL_VOICE_VSTD_ROAM_DAY");
//        gmccTables.add("TR_CELL");
//        gmccTables.add("TR_CELL_TYP");
//        List<String> tableNames = TextUtil.readTxtFileToList(inputfile, true);
//        for(String tableName:tableNames){
//            Table table = tableMap.get(tableName);
//            System.out.println(tableName+":"+(table.getTablecols().size()-4));
//        }
//        for (String tableName : tableNames) {
//            if (gmccTables.contains(tableName)) {
//                insertSelect(tableName, tableMap, new String[]{"GMCC"}, startDay, endDay);
//            } else {
//                insertSelect(tableName, tableMap, branches, startDay, endDay);
//            }
//        }
//        trcellinfo(branches);
//        cdrtest(branches);
//        reAddPartitions(tableNames, branches);
//        checkCreateTableSql();
        showSharedTable(branches);
    }

    public static void checkCreateTableSql() {
        String filename = "C:\\Users\\yx\\Desktop\\1.sql";
        List<String> newSqlList = new ArrayList();
        List<String> sqlline = TextUtil.readTxtFileToList(filename, false);
        for (String sql : sqlline) {
            if (sql.toLowerCase().contains("create table")) {
                String dropsql = sql.toLowerCase().replaceAll("create table", "drop table").replace("(", ";");
                newSqlList.add(dropsql);
            }
            newSqlList.add(sql);
        }
        for (String sql : newSqlList) {
            System.out.println(sql);
        }
    }

    public static void hebing() {
        String proxy = "perl ~schadm/dssprog/bin/remote_cli.pl bd_day ";
        List<String> dirs = TextUtil.readTxtFileToList("e:/dirs.txt", false);
        for (String dir : dirs) {
            if (dir != null && dir.trim().length() > 0) {
                int idx = dir.indexOf("branch=");
                String path = dir.substring(0, idx);
                int idx1 = dir.lastIndexOf("/");
                String filename = dir.substring(idx1 + 1, dir.length());
                String branch = dir.substring(idx1 - 2, idx1);
                String cmd = proxy + " hadoop fs -mv " + dir + " " + path + "branch=GMCC/" + branch + filename;
                System.out.println(cmd);
            }
        }
    }

    public static void showSharedTable(String[] branches) {
        List<String> alist = TextUtil.readTxtFileToList("E:\\1.txt", false);
        List<String> blist = TextUtil.readTxtFileToList("e:\\2.txt", false);
        for (String a : alist) {
            boolean isSharedMain = false;
            boolean isSharedBranch = false;
            for (String b : blist) {
                if (a.trim().equalsIgnoreCase(b.trim())) {
                    isSharedMain = true;
                    break;
                }
            }
            for (String b : blist) {
                for (String branch : branches) {
                    String tableName = a + "_" + branch;
                    if (tableName.trim().equalsIgnoreCase(b.trim())) {
                        isSharedBranch = true;
                        break;
                    }
                }
                if (isSharedBranch) {
                    break;
                }
            }
            if (isSharedBranch || isSharedMain) {
                System.out.println(a + "|" + isSharedMain + "|" + isSharedBranch);
            }
        }
    }

    public static void cdrtest(String[] branches) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String path = "E:\\i-work\\SVN\\GDBI\\02GDBI代码\\01 主体仓库\\04PERL代码\\PERL脚本\\融合计费清单视图脚本\\";
        List<String> alist = TextUtil.readTxtFileToList(path + "CDR_VIEW示例.SQL", false);
        for (String a : alist) {
            int idx = a.indexOf("--");
            if (idx != -1) {
                a = a.substring(0, idx);
            }
            buffer.append(a).append("\n");
        }
//        System.out.println(buffer.toString());
        for (String branch : branches) {
            String sql = buffer.toString();
            sql = sql.replaceAll("GZ201710", branch + "201710");
            System.out.println(sql);
        }
//      TextUtil.writeToFile(buffer.toString(), "E:\\i-work\\SVN\\GDBI\\02GDBI����\\01 ����ֿ�\\04PERL����\\PERL�ű�\\�ںϼƷ��嵥��ͼ�ű�\\test.pl");
    }

    public static void trcellinfo(String[] branches) {
        String aa = "INSERT INTO REF.TR_CELL_INFO(\n"
                + "	LOCAL_CD,\n"
                + "	CELL_CD,\n"
                + "	CMCC_BRANCH_CD,\n"
                + "	DISTRICT_CD,\n"
                + "	DISTRICT_NAM,\n"
                + "	TOWN_CD,\n"
                + "	TOWN_NAM,\n"
                + "	FAINT_CELL_IND,\n"
                + "	BUSI_OFFICE_CD,\n"
                + "	BUSI_OFFICE_NAM,\n"
                + "	REGION_EXT_CD1,\n"
                + "	REGION_EXT_NAM1,\n"
                + "	REGION_EXT_CD2,\n"
                + "	REGION_EXT_NAM2,\n"
                + "	REGION_EXT_CD3,\n"
                + "	REGION_EXT_NAM3\n"
                + ")SELECT \n"
                + "	LOCAL_CD,\n"
                + "	REPEAT('0',8-LENGTH(TRIM(CELL_CD)))||UPPER(TRIM(CELL_CD)),\n"
                + "	CMCC_BRANCH_CD,\n"
                + "	DISTRICT_CD,\n"
                + "	DISTRICT_NAM,\n"
                + "	TOWN_CD,\n"
                + "	TOWN_NAM,\n"
                + "	FAINT_CELL_IND,\n"
                + "	BUSI_OFFICE_CD,\n"
                + "	BUSI_OFFICE_NAM,\n"
                + "	REGION_EXT_CD1,\n"
                + "	REGION_EXT_NAM1,\n"
                + "	REGION_EXT_CD2,\n"
                + "	REGION_EXT_NAM2,\n"
                + "	REGION_EXT_CD3,\n"
                + "	REGION_EXT_NAM3\n"
                + "FROM REF.TR_CELL_INFO;\n";
        for (String branch : branches) {
            String sql = aa.replaceAll("TR_CELL_INFO", "TR_CELL_INFO_" + branch);
            System.out.println(sql);
        }
    }

    public static void reAddPartitions(List<String> tableNames, String[] branches) {
        for (String tableName : tableNames) {
            for (String branche : branches) {
                String sql = "MSCK REPAIR TABLE JCFW." + tableName + "_" + branche + ";";
                System.out.println(sql);
            }
        }
    }

    /**
     * ��չ�ֶγ���
     *
     * @param tableName
     * @param columnName
     * @return
     */
    public static String extendColumnLength(String tableName, String columnName) {
        String extendSql = "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " SET DATA TYPE VARCHAR(8);\nREORG TABLE " + tableName + ";\n";
        return extendSql;
    }

    /**
     * �ֶ���0��8λ���ȡ�
     *
     * @param tableName
     * @param columnName
     * @return
     */
    public static String updateColumnValue(String tableName, String columnName) {
        String updateColumnValueSql = "UPDATE " + tableName + " SET " + columnName
                + " = REPEAT('0',8-LENGTH(TRIM(" + columnName + ")))||TRIM(" + columnName + ");\n";
        return updateColumnValueSql;
    }

    /**
     * ��ģ��ʵ��������ȡ������ģ����
     *
     * @param tableName
     * @return
     */
    public static String getRealTableName(String tableName) {
        String[] branches = {"GZ", "SZ", "DG", "FS", "ST", "ZH", "HZ", "ZS", "JM", "ZJ", "SG", "HY", "MZ", "SW", "YJ", "MM", "ZQ", "QY", "CZ", "JY", "YF"};
        String realTableName = tableName;
        int pointIndex = realTableName.indexOf(".");
        realTableName = realTableName.substring(pointIndex + 1, realTableName.length());

        if (realTableName.contains("2017")) {
            int idx = realTableName.indexOf("2017");
            realTableName = realTableName.substring(0, idx);
            if (realTableName.endsWith("_")) {
                realTableName = realTableName.substring(0, realTableName.length() - 1);
            }
        }
        for (String branch : branches) {
            if (realTableName.endsWith("_" + branch)) {
                int branchIndex = realTableName.lastIndexOf("_" + branch);
                realTableName = realTableName.substring(0, branchIndex);
                break;
            }
        }
        return realTableName;
    }

    public static void insertSelect(String tableName, Map<String, Table> tableMap, String[] branches, String sDay, String eDay) throws ParseException, IOException {

        Table table = tableMap.get(tableName);
        Optional<Table> table1 = Optional.of(table);
        table1.map(t -> t.getTableName());
        SimpleDateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatMon = new SimpleDateFormat("yyyyMM");

        Calendar startDay = Calendar.getInstance();
        startDay.setTime(formatDay.parse(sDay));

        Calendar endDay = Calendar.getInstance();
        endDay.setTime(formatDay.parse(eDay));

        Calendar endMon = Calendar.getInstance();
        endMon.setTime(formatDay.parse(eDay));

        List partitionCols = Arrays.asList(table.getPartitionCols());

        StringBuilder fieldBuffer = new StringBuilder();
        for (TableCol tableCol : table.getTablecols()) {

            String colName = tableCol.getColumnName();
            if (partitionCols.contains(colName)) {
                continue;
            }
            if (colName.endsWith("CELL_CD")) {
                colName = "LPAD(UPPER(TRIM(" + colName + ")),8,'0')";
            }
            fieldBuffer.append(colName).append(",");
        }
        fieldBuffer.deleteCharAt(fieldBuffer.length() - 1);//ɾ�����һ����,����

        StringBuilder sqlBuffer = new StringBuilder();
        if (partitionCols.contains("month") && partitionCols.contains("day")) {
            while (startDay.before(endDay) || startDay.equals(endDay)) {
                String month = formatMon.format(startDay.getTime());
                String day = formatDay.format(startDay.getTime());
                for (String branch : branches) {
                    sqlBuffer.append("perl ~schadm/dssprog/bin/remote_cli.pl ").append(table.getTenantUser()).append(" beeline -e \"use jcfw;");
                    sqlBuffer.append("INSERT OVERWRITE TABLE JCFW.")
                            .append(table.getTableName()).append("_BAK ")
                            .append("PARTITION (branch='").append(branch).append("',")
                            .append("month=").append(month).append(",")
                            .append("day=").append(day).append(") ")
                            .append("SELECT ").append(fieldBuffer.toString())
                            .append(" FROM JCFW.").append(table.getTableName())
                            .append(" WHERE branch='").append(branch).append("'")
                            .append(" AND month=").append(month)
                            .append(" AND day=").append(day).append(";");
                    sqlBuffer.append("\"\n");
                }
                startDay.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if (partitionCols.contains("month") && !partitionCols.contains("day")) {
            while (endMon.after(startDay) || endMon.equals(startDay)) {
                String month = formatMon.format(endMon.getTime());
                for (String branch : branches) {
                    sqlBuffer.append("perl ~schadm/dssprog/bin/remote_cli.pl ").append(table.getTenantUser()).append(" beeline -e \"use jcfw;");
                    sqlBuffer.append("INSERT OVERWRITE TABLE JCFW.")
                            .append(table.getTableName()).append("_BAK ")
                            .append("PARTITION (branch='").append(branch).append("',")
                            .append("month=").append(month).append(")")
                            .append("SELECT ").append(fieldBuffer.toString())
                            .append(" FROM JCFW.").append(table.getTableName())
                            .append(" WHERE branch='").append(branch).append("'")
                            .append(" AND month=").append(month).append(";");
                    sqlBuffer.append("\"\n");
                }
                endMon.add(Calendar.MONTH, -1);
            }
        } else if (partitionCols.contains("branch")
                && !partitionCols.contains("month")
                && !partitionCols.contains("day")) {
            for (String branch : branches) {
                sqlBuffer.append("perl ~schadm/dssprog/bin/remote_cli.pl ").append(table.getTenantUser()).append(" beeline -e \"use jcfw;");
                sqlBuffer.append("INSERT OVERWRITE TABLE JCFW.")
                        .append(table.getTableName()).append("_BAK ")
                        .append("PARTITION (branch='").append(branch).append("')")
                        .append("SELECT ").append(fieldBuffer.toString())
                        .append(" FROM JCFW.").append(table.getTableName())
                        .append(" WHERE branch='").append(branch).append("';");
                sqlBuffer.append("\"\n");
            }
        }
        TextUtil.writeToFile(sqlBuffer.toString(), tableName + ".sh");
    }

    /**
     * DB2
     */
    public static void update3monthData() {
        String targetFile = "C:\\Users\\yx\\Desktop\\�ںϼƷ��嵥����-С�����볤�ȸ���\\target.txt";
        String exceptFile = "C:\\Users\\yx\\Desktop\\�ںϼƷ��嵥����-С�����볤�ȸ���\\except.txt";
        String columnFile = "C:\\Users\\yx\\Desktop\\�ںϼƷ��嵥����-С�����볤�ȸ���\\ģ���ֶζ�Ӧ��ϵ.txt";
        List<String> targetTables = TextUtil.readTxtFileToList(targetFile, false);
        List<String> modifyColumns = TextUtil.readTxtFileToList(columnFile, false);
        List<String> exceptTables = TextUtil.readTxtFileToList(exceptFile, false);
        StringBuilder buffer0 = new StringBuilder();//�޸ı�ṹ
        StringBuilder buffer1 = new StringBuilder();//�޸��±�����
        StringBuilder buffer2 = new StringBuilder();//�޸Ĳ��ֱ�����
        targetTables.forEach((targetTable) -> {
            modifyColumns.stream().map((mocifyColumn) -> mocifyColumn.split(",")).forEachOrdered((tableAndCol) -> {
                String columnName = tableAndCol[1];
                String realName = getRealTableName(targetTable);
                if (realName.equals(tableAndCol[0]) && !exceptTables.contains(realName)) {
                    if (targetTable.contains("2017")) {
                        String targetTable1 = targetTable.replaceAll("201708", "201706");
                        /**
                         * �޸Ķ�Ӧ�·ݱ��С�������ֶΣ���չ��8λ���ȡ�
                         */
                        String chgsql1 = extendColumnLength(targetTable1, columnName);
                        buffer0.append(chgsql1);
                        /**
                         * �޸Ķ�Ӧ�±��С���������ݣ���0��8λ���ȡ�
                         */
                        String chgValueSql1 = updateColumnValue(targetTable1, columnName);
                        buffer1.append(chgValueSql1);
                    } else {
                        /**
                         * ���ֱ�ı�ֱ���޸�С���������ݣ���0��8λ���ȡ�
                         */
                        String chgValueSql = updateColumnValue(targetTable, columnName);
                        buffer2.append(chgValueSql);
                    }
                }
            });
        });
        //System.out.println(buffer0.toString());
        System.out.println(buffer1.toString());
        //System.out.println(buffer2.toString());
    }

    public static void loadCellCdTableChanged() {
        String logfile = "C:\\Users\\yx\\Desktop\\�ںϼƷ��嵥����-С�����볤�ȸ���\\��ȡ����ģ��.txt";
        List<String> loglines = TextUtil.readTxtFileToList(logfile, false);
        for (String line : loglines) {
            if (line.contains("SET DATA TYPE")) {
                int start = line.indexOf("ALTER TABLE ");
                int end = line.indexOf(" ALTER COLUMN ");
                String tablename = line.substring(start, end).replaceAll("ALTER TABLE", "").trim();
                System.out.println(tablename);
            } else if (line.contains("CREATE TABLE")) {
                int startIndex = line.indexOf("CREATE TABLE ");
                int endIndex = line.indexOf(" (");
                String tablename = line.substring(startIndex, endIndex).replaceAll("CREATE TABLE", "").trim();;
                System.out.println(tablename);
            }
        }
    }

    public static void aaa(String[] branches) {
        StringBuilder buffer = new StringBuilder();
        for (String branche : branches) {
            buffer.append("ALTER TABLE EDS.TW_CELL_USE_MO_").append(branche).append("201708 ALTER COLUMN  CELL_CD SET DATA TYPE VARCHAR(8);\n");
        }
        System.out.println(buffer.toString());
    }

    public static void bbb(String[] branches) {
        String tablename = "TR_GSMTDLTE_CELL";
        StringBuilder buffer = new StringBuilder("perl ~schadm/dssprog/bin/remote_cli.pl bd_day beeline -e \"use jcfw;");
        for (String branche : branches) {
            buffer.append("MSCK REPAIR TABLE JCFW.").append(tablename).append("_").append(branche).append(";");
        }
        buffer.append("\"");
        System.out.println(buffer.toString());
    }

    public static void exportCDRTables() {
        String filename = "C:\\Users\\yx\\Desktop\\cdrtable.log";
        List<String> alist = TextUtil.readTxtFileToList(filename, false);
        Workbook wkbook = new SXSSFWorkbook();
        Sheet sheet = wkbook.createSheet("�ںϼƷ��嵥��ṹ");
        int rowcount = 0;
        String tablename = "";
        int colseq = 1;
        boolean isTableName = false;
        for (String line : alist) {
            if (line == null
                    || line.trim().length() == 0
                    || line.contains("---")
                    || line.contains("record(s) selected")
                    || line.contains("NULLS")) {
                isTableName = false;
                continue;
            }
            if (line.contains("desc")) {
                System.out.println("start:" + line);
                isTableName = true;
                colseq = 1;
                continue;
            }
            List<String> rowcells = new ArrayList();
            if (isTableName) {
                int indx = line.indexOf(" ");
                tablename = line.substring(0, indx);
                if (tablename.contains("2017")) {
                    int indx1 = tablename.lastIndexOf("_");
                    tablename = tablename.substring(0, indx1);
                }
                isTableName = false;
                continue;
            }
            String[] tempCols = line.split(" ");
            for (String tempcol : tempCols) {
                tempcol = tempcol.trim();
                if (tempcol != null && tempcol.length() > 0) {
                    rowcells.add(tempcol);
                }
            }
            rowcells.add(0, tablename);
            rowcells.add(1, String.valueOf(colseq++));
            Row row = sheet.createRow(rowcount++);
            int cellcount = 0;
            for (String rowcell : rowcells) {
                row.createCell(cellcount++).setCellValue(rowcell);
            }
        }
        ExcelUtil.writeLargeDataToExcel(wkbook, "e:\\cdrtable.xlsx");
    }

    public static void writeVersionLog(String filepath, String outpath) throws Exception {
        File file = new File(filepath);
        List<String> datalist = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File oneFile : files) {
                FileInputStream instream = new FileInputStream(oneFile);
                String charset = TextUtil.getFileCharset(oneFile);
                System.out.println(oneFile.getName() + " " + charset);
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, charset), 50 * 1024 * 1024);
                String readline = null;
                boolean isAdded = false;
                while ((readline = bufreader.readLine()) != null) {
//                    if (!isAdded && readline.contains("Current Version")) {
//                        String element = "--  2017-6-27   ���ݹ̶�����_R201704210015001_������һ�廯��ȡ����;�����ηѣ�ҵ��֧��ϵͳ�����޸Ĵ洢����";
//                        String preline = datalist.get(datalist.size() - 1);
//                        String prelinestr = "---------------------------------------------------------------";
//                        if (!preline.contains(prelinestr)) {
//                            datalist.add(datalist.size() - 2, element);
//                        } else {
//                            datalist.add(datalist.size() - 1, element);
//                        }
//                    }
//                    datalist.add(readline);
                    String regx = "################################################################";
                    if (!isAdded && readline.contains(regx) && !readline.contains("˵��")) {
                        String element = "# 2017-07-7   ���ݹ̶�����_R201704210015001_������һ�廯��ȡ����;�����ηѣ�ҵ��֧��ϵͳ�����޸Ĵ洢����";
                        datalist.add(element);
                        isAdded = true;
                    }
                    datalist.add(readline);
                }
                OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(outpath + oneFile.getName()), charset);
                BufferedWriter bufwriter = new BufferedWriter(writerStream);
                for (String value : datalist) {
                    bufwriter.write(value);
                    bufwriter.newLine();
                }
                bufwriter.flush();
                bufwriter.close();
                datalist.clear();
            }
        }
    }

    public static void insertVersionLog() throws Exception {
        String baseDir = "F:\\work\\����_SR201704210015001_������һ�廯��ȡ����;�����ηѣ�ҵ��֧��ϵͳ����\\";
//        String dir1 = baseDir + "SR201704210015001\\SSBPT\\PROC_PERL";
        String dir1 = baseDir + "SR201704210015001\\DSS\\PROC_PERL";
//        String outpath = baseDir + "SR201704210015001_new\\SSBPT\\PROC_PERL\\";
        String outpath = baseDir + "SR201704210015001_new\\DSS\\PROC_PERL\\";
        File out = new File(outpath);
        if (!out.exists()) {
            out.mkdir();
        }
        writeVersionLog(dir1, outpath);
    }

    public static void test222() throws IOException {
        String outpath = "F:\\data\\";

        List<String> datalist = TextUtil.readTxtFileToList("C:\\Users\\yx\\Desktop\\ȫʡ - ����.txt", false);
        int count = 0;
        int dataRows = datalist.size();
        int maxLine = dataRows / 20;
        int fileCount = 1;
        String filename = outpath + "TR_OUTCALL_TERM_USR_20170713.txt";
        OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(filename), "GBK");
        BufferedWriter bufwriter = new BufferedWriter(writerStream);
        for (String dataline : datalist) {
            dataline = dataline
                    .replaceAll("����", "GZ")
                    .replaceAll("����", "HZ")
                    .replaceAll("��ɽ", "FS")
                    .replaceAll("��ݸ", "DG")
                    .replaceAll("��ɽ", "ZS")
                    .replaceAll("�麣", "ZH")
                    .replaceAll("����", "JM")
                    .replaceAll("����", "ZQ")
                    .replaceAll("����", "HZ")
                    .replaceAll("��ͷ", "ST")
                    .replaceAll("����", "CZ")
                    .replaceAll("����", "JY")
                    .replaceAll("��β", "SW")
                    .replaceAll("տ��", "ZJ")
                    .replaceAll("ï��", "MM")
                    .replaceAll("����", "YJ")
                    .replaceAll("�ع�", "SG")
                    .replaceAll("��Զ", "QY")
                    .replaceAll("�Ƹ�", "YF")
                    .replaceAll("÷��", "MZ")
                    .replaceAll("��Դ", "HY")
                    .replaceAll("��", "1")
                    .replaceAll("��", "0");
            bufwriter.write(dataline);
            bufwriter.newLine();
        }
        bufwriter.close();
    }

    public static void ttt() {
        List<String> datalist = TextUtil.readTxtFileToList("E:\\TEST2.TXT", false);
        for (String tablename : datalist) {
            int idx = tablename.lastIndexOf("_");
            System.out.println(tablename.substring(0, idx).trim());
        }
    }

    public static void test(Map<String, Table> tableMap, String[] branches) {
        Table table = tableMap.get("TW_PERS_STAR_CUST_ASES_M");
        for (String branch : branches) {
            String tablename = table.getTableName() + "_" + branch;
            String sql1 = "ALTER TABLE JCFW." + tablename + " CHANGE USR_NBR  USR_NBR1  STRING;";
            String sql2 = "ALTER TABLE JCFW." + tablename + " CHANGE STAT_MO  USR_NBR  STRING;";
            String sql3 = "ALTER TABLE JCFW." + tablename + " CHANGE USR_NBR1  STAT_MO  INT;";
            System.out.println(sql1);
            System.out.println(sql2);
            System.out.println(sql3);
        }
    }

    public static StringBuilder addBranchTableParition(String showPartitionsLogFile, Map<String, Table> tableMap,
            String[] branches, boolean useProxyProgram) {
        List<String> loglines = TextUtil.readTxtFileToList(showPartitionsLogFile, false);
        Map<String, StringBuilder> usermap = new HashMap();
        String tablename = null;
        String username = null;
        for (String linestr : loglines) {
            if (linestr.contains("IF EXISTS") || linestr.contains("branch='GMCC'")) {
                continue;
            }
            if (linestr.contains("show partitions ")) {
                int idx = linestr.indexOf("show partitions");
                tablename = linestr.substring(idx).replaceAll("show partitions", "").replaceAll(";", "").trim();
                Table tab = tableMap.get(tablename);
                if (tab == null) {
                    int indx = tablename.lastIndexOf("_");
                    String tmptabname = tablename.substring(0, indx);
                    tab = tableMap.get(tmptabname);
                }
                if (tab != null) {
                    username = tab.getTenantUser();
                }
                StringBuilder sbdf = usermap.get(username);
                if (sbdf == null) {
                    sbdf = new StringBuilder();
                    usermap.put(username, sbdf);
                }
                continue;
            }
            if (!linestr.contains("branch=") && !linestr.contains("month=")
                    && !linestr.contains("day=") && !linestr.contains("year=")
                    && !linestr.contains("ds=") && !linestr.contains("hour=")) {
                continue;
            }
            String[] partitions = linestr.replaceAll("\\|", "").trim().split("/");
            /**
             * ��ɾ���ɷ������ټ��·�����
             */
            String droparrtsql = "ALTER TABLE JCFW." + tablename + " DROP IF EXISTS PARTITION (";

            for (int i = 0; i < partitions.length; i++) {
                String[] paritioncells = partitions[i].split("=");
                if (i == partitions.length - 1) {
                    droparrtsql += paritioncells[0] + "='" + paritioncells[1] + "');";
                } else {
                    droparrtsql += paritioncells[0] + "='" + paritioncells[1] + "',";
                }
            }
            String addpartsql = "ALTER TABLE JCFW." + tablename + " ADD PARTITION (";
            for (int i = 0; i < partitions.length; i++) {
                String[] paritioncells = partitions[i].split("=");
                if (i == partitions.length - 1) {
                    addpartsql += paritioncells[0] + "='" + paritioncells[1] + "');";
                } else {
                    addpartsql += paritioncells[0] + "='" + paritioncells[1] + "',";
                }
            }
            if (useProxyProgram) {
                droparrtsql = "remote_cli.pl " + username + " beeline -e \"USE JCFW;" + droparrtsql + "\"";
                addpartsql = "remote_cli.pl " + username + " beeline -e \"USE JCFW;" + addpartsql + "\"";
            }
            usermap.get(username).append(droparrtsql).append("\n");
            usermap.get(username).append(addpartsql).append("\n");
        }
        StringBuilder retbuffer = new StringBuilder();
        usermap.keySet().stream().forEach((key) -> {
            StringBuilder sbdf1 = usermap.get(key);
            retbuffer.append("---").append(key).append("�⻧�ű�---\n");
            retbuffer.append(sbdf1.toString());
            System.out.println(retbuffer.toString());
        });
        System.out.println("�������");
        return retbuffer;
    }

    private static void makeBranchTablePartition(Map<String, Table> tableMap,
            String tableName, String startDate, String endDate, String[] branches) throws ParseException {
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfMon = new SimpleDateFormat("yyyyMM");

        Calendar oldday = Calendar.getInstance();
        oldday.setTime(sdfDay.parse(startDate));

        Calendar nowDay = Calendar.getInstance();
        nowDay.setTime(sdfDay.parse(endDate));
        Calendar nowMon = Calendar.getInstance();
        nowMon.setTime(sdfDay.parse(endDate));
        Table table = tableMap.get(tableName);

        StringBuilder sqlbuffer = new StringBuilder();
        List parititons = Arrays.asList(table.getPartitionCols());
        if (parititons.contains("month") && parititons.contains("day")) {
            while (nowDay.after(oldday)) {
                for (String branch : branches) {
                    String alterTabelStr = "ALTER TABLE JCFW." + table.getTableName() + "_" + branch + " ADD PARTITION (";
                    String pStr = "month=" + sdfMon.format(nowDay.getTime()) + ",day=" + sdfDay.format(nowDay.getTime());
                    sqlbuffer.append(alterTabelStr).append(pStr).append(");\n");
                }
                nowDay.add(Calendar.DAY_OF_MONTH, -1);
            }
        } else if (parititons.contains("month") && !parititons.contains("day")) {
            while (nowMon.after(oldday)) {
                for (String branch : branches) {
                    String alterTabelStr = "ALTER TABLE JCFW." + table.getTableName() + "_" + branch + " ADD PARTITION (";
                    String pStr = "month=" + sdfMon.format(nowMon.getTime());
                    sqlbuffer.append(alterTabelStr).append(pStr).append(");\n");
                }
                nowMon.add(Calendar.MONTH, -1);
            }
        }
        System.out.println(sqlbuffer.toString());
    }

    public static void updateSecurity(Map<String, Table> tableMap) {
        String filename = "F:\\work\\GDNG3BASS\\01GDNG3BASS�ĵ�\\07��ϸ���\\02ģ�����\\Ԫ����\\ģ�ͽű����ɹ���\\reftable.txt";
        List<String> tables = TextUtil.readTxtFileToList(filename, false);
        for (String tablename : tables) {
            Table table = tableMap.get(tablename);
            if (table != null) {
                List<TableCol> tablecols = table.getTablecols();
                for (TableCol tablecol : tablecols) {
                    if (tablecol.getPartitionSeq() != null && tablecol.getPartitionSeq().trim().length() > 0) {
                        continue;
                    }
                    String sql = "update column_val set filed_type_child='" + tablecol.getSecurityType3() + "',"
                            + "sensitive_level='" + tablecol.getSensitivityLevel() + "',"
                            + "policyid='" + tablecol.getOutSensitivityId() + "' "
                            + "where dataname='" + tablename + "' and colname='" + tablecol.getColumnName() + "';";
                    System.out.println(sql);
                }
            }
        }
    }

    public static void test4(Map<String, Table> tableMap) {
        String filename = "C:\\Users\\yx\\Desktop\\branchtable.txt";
        List<String> branchtables = TextUtil.readTxtFileToList(filename, false);
        for (String branchtable : branchtables) {
            String maintable = branchtable.substring(0, branchtable.length() - 3);

            Table maintabobj = tableMap.get(maintable);
        }
    }

    public static void test5() {
        String[] branches = {"GZ", "SZ", "DG", "FS", "ST", "ZH", "HZ", "ZS", "JM", "ZJ", "SG", "HY", "MZ", "SW", "YJ", "MM", "ZQ", "QY", "CZ", "JY", "YF"};
        String filename = "E:\\work\\�㶫�ƶ�NG3��Ŀ -GDNG3BASS\\GDNG3BASS\\01GDNG3BASS�ĵ�\\07��ϸ���\\02ģ�����\\Ԫ����\\ģ�ͽű����ɹ���\\test.txt";
        List<String> tables = TextUtil.readTxtFileToList(filename, false);
        for (String table : tables) {
            for (String branch : branches) {
                String sql = "insert into imp_table_list(table_name)values('" + table + "_" + branch + "');";
                System.out.println(sql);
            }
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void test() {
        String filename = "C:\\Users\\yx\\Documents\\aa.txt";
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            while ((readline = bufreader.readLine()) != null) {
                System.out.println(readline.replaceAll("\001", "#"));
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void modifySplitStr() {
        String filename = "E:\\sessionlog\\session_20161206_4.log";
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            while ((readline = bufreader.readLine()) != null) {
                String locationInfo = readline.trim();
                int indx = locationInfo.indexOf("/tenant");
                if (indx == -1) {
                    continue;
                }
                String location = locationInfo.substring(indx);
                String[] arrays = location.split("/");
                if (arrays.length < 10) {
                    continue;
                }
                int indx1 = location.lastIndexOf("/");
                String filaname = location.substring(indx1);
                System.out.println(filaname);
//                String cmd1 = "hadoop fs -get "+location+" "+"/etl01_data03/KFT_PUBLIC/luotao";
//                String cmd2 = "gunzip "+filaname;
//                String cmd3 = "sed -i 's/@#\\$/\001/g'  /etl01_data03/KFT_PUBLIC/luotao/"+filaname;
//                String cmd4 = "gzip "+filaname +" "+filaname+".gz";
//                String cmd5 = "hadoop fs -put /etl01_data03/KFT_PUBLIC/luotao/"+filaname+" "+location;
//                System.out.println(cmd1);
//                System.out.println(cmd2);
//                System.out.println(cmd3);
//                System.out.println(cmd4);
//                System.out.println(cmd5);
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void makeUpdataTableXMLID() {
        String filename = "C:\\Users\\yx\\Desktop\\updatexmlid.txt";
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            while ((readline = bufreader.readLine()) != null) {
                String oldxmlid = readline.trim();
                int indx = oldxmlid.lastIndexOf("_");
                String newxmlid = oldxmlid.substring(0, indx) + "_O" + oldxmlid.substring(indx);
                String update_tablefile = "update tablefile set xmlid ='" + newxmlid + "',state=null where xmlid='" + oldxmlid + "';";
                String update_metaobj = "update metaobj set xmlid ='" + newxmlid + "',state=null where xmlid='" + oldxmlid + "';";
                String update_tableall = "update tableall set xmlid ='" + newxmlid + "' where xmlid='" + oldxmlid + "';";
                System.out.println(update_tablefile);
                System.out.println(update_metaobj);
                System.out.println(update_tableall);
                System.out.println();
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void makeUpdataTableCNName(Map<String, Table> tableMap) {
        String filename = "C:\\Users\\yx\\Desktop\\update_cn_name.txt";
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            while ((readline = bufreader.readLine()) != null) {
                String[] tmpstr = readline.trim().split("#");
                String mainTable = tmpstr[0].substring(0, tmpstr[0].lastIndexOf("_"));
                String modetype = tableMap.get(mainTable).getTableModel();
                if (!tmpstr[1].startsWith(modetype)) {
                    String update_tablefile = "update tablefile set datacnname ='" + modetype + "_" + tmpstr[1] + "' where xmlid like '%_20161128' and dataname='" + tmpstr[0] + "';";
                    String update_metaobj = "update metaobj set objcnname ='" + modetype + "_" + tmpstr[1] + "' where xmlid like '%_20161128' and objname='" + tmpstr[0] + "';";
                    System.out.println(update_tablefile);
                    System.out.println(update_metaobj);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void makeReplaceShell(File infile, StringBuilder cmdbuffer, String rsplit, String dsplit) {
        if (infile.exists()) {
            if (infile.isDirectory()) {
                if (infile.getPath().contains("day=")) {
                    System.out.println(infile.getPath());
                } else {
                    File[] children = infile.listFiles();
                    for (File file : children) {
                        makeReplaceShell(file, cmdbuffer, rsplit, dsplit);
                    }
                }
            }
        }
    }

    public static void showBranchTablePartitions() {
        String filename = "C:\\Users\\yx\\Desktop\\���ֵ���ģ��.log";
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            String tablename = null;
            String username = null;
            while ((readline = bufreader.readLine()) != null) {
                if (readline.contains("Table not found")) {
                    int idx0 = readline.indexOf("Table not found");
                    int idx1 = readline.lastIndexOf("_");
                    System.out.println(readline.substring(idx0, idx1));
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void modifyTableSerdClass(Map<String, Table> tabledata) {
        String filename = "C:\\Users\\yx\\Desktop\\�ػ�20161122_01��ƽ̨.log";
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            String tablename = null;
            String username = null;
            while ((readline = bufreader.readLine()) != null) {
                if (readline.contains("SHOW PARTITIONS")) {
                    int idx = readline.indexOf("SHOW PARTITIONS");
                    tablename = readline.substring(idx).replaceAll("SHOW PARTITIONS", "").replaceAll(";", "").trim();
                    username = tabledata.get(tablename).getTenantUser();
                    continue;
                }
                if ("TO_CDR".equals(tablename)) {
                    continue;
                }
                if (readline.contains("branch=")) {
                    String[] partitions = readline.replaceAll("\\|", "").trim().split("/");
                    String partitionstr = "";
                    for (int i = 0; i < partitions.length; i++) {
                        String[] paritioncells = partitions[i].split("=");
                        if (i == partitions.length - 1) {
                            partitionstr += paritioncells[0] + "='" + paritioncells[1] + "'";
                        } else {
                            partitionstr += paritioncells[0] + "='" + paritioncells[1] + "',";
                        }
                    }
                    if (partitionstr.contains("month=") && !partitionstr.contains("month='201611'")) {
                        continue;
                    }
                    String cmd = "remote_cli.pl " + username + " beeline -e \"USE JCFW;ALTER TABLE " + tablename + " PARTITION(" + partitionstr + ") SET SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' "
                            + "WITH SERDEPROPERTIES ('serialization.encoding'='GBK','serialization.null.format'='');\"";
                    System.out.println(cmd);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void modifyPartitionSplit() {
        String filename = "C:\\Users\\yx\\Desktop\\�ػ�20161116_01��ƽ̨.log";
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            while ((readline = bufreader.readLine()) != null) {
                if (readline.contains("branch=")) {
                    String[] partitions = readline.replaceAll("\\|", "").trim().split("/");
                    String partitionstr = "";
                    for (int i = 0; i < partitions.length; i++) {
                        String[] paritioncells = partitions[i].split("=");
                        if (i == partitions.length - 1) {
                            partitionstr += paritioncells[0] + "='" + paritioncells[1] + "'";
                        } else {
                            partitionstr += paritioncells[0] + "='" + paritioncells[1] + "',";
                        }
                    }
                    String aa = "remote_cli.pl bd_b beeline -e \"USE JCFW;ALTER TABLE TO_CDR PARTITION(" + partitionstr + ") SET SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' "
                            + "WITH SERDEPROPERTIES ('field.delim'='&','serialization.encoding'='GBK','serialization.null.format'='');\"";
                    System.out.println(aa);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void showBranchTable(String filename, String[] branches) {
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            StringBuilder tembdf = new StringBuilder();
            while ((readline = bufreader.readLine()) != null) {
                for (String branch : branches) {
                    if (readline.startsWith("TS_") || readline.startsWith("TO_")
                            || readline.startsWith("TW_")
                            || readline.startsWith("TM_")
                            || readline.startsWith("TT_")
                            || readline.startsWith("TYJ_")
                            || readline.startsWith("TR_")) {
                        int indx = readline.lastIndexOf("_");
                        if (indx != -1) {
                            String tmp = readline.substring(indx);
                            if (tmp.contains("_" + branch)) {
                                String aa = tmp.substring(3, tmp.length() - 1);
                                if (aa != null) {
                                    //System.out.println(aa);
                                    if (isNumeric(aa.trim())) {
                                        System.out.println(readline.substring(0, indx));
                                    }
                                } else if (aa == null || aa.trim().length() == 0) {
                                    System.out.println(readline.substring(0, indx));
                                }
                            }
                        }

                    }
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void showQuryResult(String filename, Map<String, Table> tableMap) {
        Map<String, StringBuilder> scriptbuffer = new HashMap();//new StringBuilder();
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            String tablename = null;
            StringBuilder tembdf = new StringBuilder();
            while ((readline = bufreader.readLine()) != null) {
                if (readline.contains("select * from")) {
                    int indx = readline.indexOf("select * from");
                    tablename = readline.substring(indx + 13).replaceAll(";", "").replaceAll("limit 1", "").trim();
                    continue;
                }
                if (readline.contains("1 row selected")) {
                    int indx = tablename.lastIndexOf("_");
                    String branch = tablename.substring(indx + 1);
                    String tmptabname = tablename.substring(0, indx);
                    String username = tableMap.get(tmptabname).getTenantUser();
                    StringBuilder buffer = scriptbuffer.get(username);
                    if (buffer == null) {
                        buffer = new StringBuilder();
                        scriptbuffer.put(username, buffer);
                        buffer.append("----").append(username).append("�⻧�ű�----\r\n");
                    }
                    String script = "ALTER TABLE JCFW." + tablename + " ADD PARTITION(branch='" + branch + "');";
                    buffer.append(script).append("\r\n");
                }
            }
            for (String key : scriptbuffer.keySet()) {
                StringBuilder buffer = scriptbuffer.get(key);
                System.out.println(buffer.toString());
            }

        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void showTablePartitionsCMD(String filename, String[] branches) {
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            StringBuilder tembdf = new StringBuilder();
            while ((readline = bufreader.readLine()) != null) {
                for (String branch : branches) {
                    String tablename = readline.trim() + "_" + branch;
                    System.out.println("select * from  " + tablename + " limit 1;");
                }
            }
            System.out.println(tembdf.toString());
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void run(String filename) {
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            StringBuilder tembdf = new StringBuilder();
            while ((readline = bufreader.readLine()) != null) {
                int idx = readline.indexOf("#");
                if (idx != -1) {
                    tembdf.append("@@@").append(readline);
                } else {
                    tembdf.append(" ").append(readline);
                    //System.out.println(readline);
                }
            }
            String[] lines = tembdf.toString().split("@@@");
            for (String table : TABLE_LIST) {
                int seq = 1;
                for (String line : lines) {
                    if (line.startsWith(table + "#")) {
                        System.out.println((seq++) + "#" + line);
                    }
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void test(String filename) {
        try {
            FileInputStream instream = new FileInputStream(filename);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            String readline = null;
            StringBuilder tembdf = new StringBuilder();
            while ((readline = bufreader.readLine()) != null) {
                int idx = readline.indexOf("PARTITIONED");
                if (idx != -1) {
                    tembdf.append("\n\n").append(readline);
                } else {
                    tembdf.append(" ").append(readline);
                    //System.out.println(readline);
                }
            }
            System.out.println(tembdf.toString());
        } catch (IOException e) {
            Logger.getLogger(ModeTxtProcessMain.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
