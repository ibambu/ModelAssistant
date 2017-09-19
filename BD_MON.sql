----bd_mon租户脚本----
DROP TABLE IF EXISTS JCFW.TW_TERMPAY_PROV_ACTVCALL_M;
CREATE EXTERNAL TABLE JCFW.TW_TERMPAY_PROV_ACTVCALL_M (
   STAT_DT    INT,
   IMEI    STRING,
   USR_NBR    STRING,
   CMCC_BRANCH_CD    STRING,
   B_NBR    STRING,
   B_AREA_CD    STRING,
   LOCAL_CD    STRING,
   CELL_CD    STRING,
   CALL_AREA_CD    STRING,
   CALL_CNT    INT,
   SMS_UP_CNT    INT,
   MMS_UP_CNT    INT,
   GPRS_FLUX    BIGINT,
   WLAN_FLUX    BIGINT,
   UP_GPRS_CALL_CNT    BIGINT
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
WITH SERDEPROPERTIES ('field.delim'='&','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/MON/EDS/TW_TERMPAY_PROV_ACTVCALL_M';

DROP TABLE IF EXISTS JCFW.TW_CORP_ECPN_CHRG_M;
CREATE EXTERNAL TABLE JCFW.TW_CORP_ECPN_CHRG_M (
   EBOX_CHG_LOG_ID    STRING,
   CMCC_BRANCH_CD    STRING,
   EBOX_ID    STRING,
   EBOX_UNIT_CD    STRING,
   ECPN_TYP_CD    STRING,
   USR_NBR    STRING,
   GROUP_ID    STRING,
   CORP_PRDCT_NBR    STRING,
   CHRG_AMT    DECIMAL(14,2),
   EBOX_CHG_RSN_CD    STRING,
   LVL2_CHG_RSN    STRING,
   CHG_DT    INT,
   CHG_TIM    STRING
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/MON/EDS/TW_CORP_ECPN_CHRG_M';

DROP TABLE IF EXISTS JCFW.TW_CORP_ECPN_WOFF_M;
CREATE EXTERNAL TABLE JCFW.TW_CORP_ECPN_WOFF_M (
   EBOX_CHG_LOG_ID    STRING,
   CMCC_BRANCH_CD    STRING,
   EBOX_ID    STRING,
   EBOX_UNIT_CD    STRING,
   ECPN_TYP_CD    STRING,
   USR_NBR    STRING,
   GROUP_ID    STRING,
   CORP_PRDCT_NBR    STRING,
   WOFF_AMT    DECIMAL(14,2),
   EBOX_CHG_RSN_CD    STRING,
   LVL2_CHG_RSN    STRING,
   CHG_DT    INT,
   CHG_TIM    STRING
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/MON/EDS/TW_CORP_ECPN_WOFF_M';

DROP TABLE IF EXISTS JCFW.TW_CORP_ECPN_BAL_M;
CREATE EXTERNAL TABLE JCFW.TW_CORP_ECPN_BAL_M (
   EBOX_CHG_LOG_ID    STRING,
   CMCC_BRANCH_CD    STRING,
   EBOX_ID    STRING,
   EBOX_UNIT_CD    STRING,
   ECPN_TYP_CD    STRING,
   USR_NBR    STRING,
   GROUP_ID    STRING,
   CORP_PRDCT_NBR    STRING,
   BAL_AMT    DECIMAL(14,2),
   EBOX_CHG_RSN_CD    STRING,
   LVL2_CHG_RSN    STRING,
   CHG_DT    INT,
   CHG_TIM    STRING
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/MON/EDS/TW_CORP_ECPN_BAL_M';

