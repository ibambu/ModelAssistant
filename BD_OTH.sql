----bd_oth租户脚本----
DROP TABLE IF EXISTS JCFW.TO_BASS_CORP_PRDCT;
CREATE EXTERNAL TABLE JCFW.TO_BASS_CORP_PRDCT (
   GUSR_ID    STRING,
   AREA_CD    STRING,
   INTRL_ID    STRING,
   GCUST_ID    STRING,
   BASS_PRDCT_CD    STRING,
   YJ_PRD_CODE    STRING,
   BUSI_TYP_IND    SMALLINT,
   REL_GUSR_ID    STRING,
   MAX_MBR_CNT    INT,
   EFF_DT    INT,
   USR_STS_CD    STRING,
   STS_DT    INT,
   CUST_TYP    SMALLINT,
   PRDCT_PKG_IND    STRING,
   PRDCT_PKG_ID    STRING,
   CORP_PRDCT_NBR    STRING,
   CORP_USR_NBR    STRING,
   INTR_PRVC_IND    STRING,
   MSTR_IND    STRING,
   PAY_GCUST_ID    STRING,
   ORDR_ID    STRING,
   INTR_USR_ID    STRING,
   GUSR_NAM    STRING,
   EBOX_ID    STRING,
   MEMCOUNT    INT,
   CONTRACTCODE    STRING,
   CONTRACTOID    STRING,
   CMCC_BRANCH_CD    STRING,
   DATA_DT    INT,
   CORP_PRDCT_CODE    STRING,
   MAIN_ATR_DESC    STRING,
   VTL_CORP_PRD_IND    STRING,
   IOT_TYP_CD    STRING,
   IOT_ATR_IND    STRING,
   BRND_CD    SMALLINT,
   PRDCT_SRC    SMALLINT
)
PARTITIONED BY (branch STRING)
STORED AS PARQUET
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/ODS/TO_BASS_CORP_PRDCT'
TBLPROPERTIES ('serialization.null.format' ='NVL','serialization.encoding' ='GBK');

DROP TABLE IF EXISTS JCFW.TO_USR_BRANCH_BIND;
CREATE EXTERNAL TABLE JCFW.TO_USR_BRANCH_BIND (
   USR_MOB_NBR    STRING,
   BRND_CD    SMALLINT,
   CMCC_BRANCH_CD    STRING,
   VALID_IND    STRING,
   EFF_DT    INT,
   INVALID_DT    INT
)
PARTITIONED BY (branch STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/ODS/TO_USR_BRANCH_BIND';

DROP TABLE IF EXISTS JCFW.TR_IOT_ATR;
CREATE EXTERNAL TABLE JCFW.TR_IOT_ATR (
   IOT_ATR_IND    STRING,
   IOT_ATR_NAM    STRING
)
PARTITIONED BY (branch STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
WITH SERDEPROPERTIES ('field.delim'=',','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/REF/TR_IOT_ATR';

ALTER TABLE JCFW.TR_IOT_ATR ADD PARTITION (branch='GMCC');

DROP TABLE IF EXISTS JCFW.TW_FWD_USR_WEEK;
CREATE EXTERNAL TABLE JCFW.TW_FWD_USR_WEEK (
   START_DT    INT,
   END_DT    INT,
   USR_NBR    STRING,
   B_NBR    STRING,
   B_AREA_CD    STRING,
   BRND_CD    SMALLINT,
   CMCC_BRANCH_CD    STRING,
   B_BRND_CD    SMALLINT,
   FWD_DEBET_DUR    INT,
   FWD_CNT    INT,
   DEBET_DUR    INT,
   VOCCALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT,day INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_FWD_USR_WEEK';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_KEYWD_D;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_KEYWD_D (
   HR    INT,
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   KEYWD    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT,day INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_KEYWD_D';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_CNTN_D;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_CNTN_D (
   HR    INT,
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   URL_APP_LVL1    STRING,
   URL_APP_LVL2    STRING,
   URL_APP_LVL3    STRING,
   URL_APP_NAM    STRING,
   CNTN_LV2    STRING,
   CNTN_LV3    STRING,
   CNTN_NAM    STRING,
   AUTHR_NAM    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT,day INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_CNTN_D';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_NTLOG_D;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_NTLOG_D (
   HR    INT,
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   NET_APP_ID    STRING,
   URL_APP_LVL1    STRING,
   URL_APP_LVL2    STRING,
   URL_APP_LVL3    STRING,
   URL_APP_NAM    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT,day INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_NTLOG_D';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_CMPT_D;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_CMPT_D (
   HR    INT,
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   NET_APP_ID    STRING,
   URL_APP_LVL2    STRING,
   URL_APP_LVL3    STRING,
   URL_APP_NAM    STRING,
   TASK_NAM    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT,day INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_CMPT_D';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_NETSHOP_D;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_NETSHOP_D (
   HR    INT,
   USR_NBR    STRING,
   NET_TYP    STRING,
   URL_APP_NAM    STRING,
   TERM_BRND_NAM    STRING,
   TERM_MDL_NAM    STRING,
   TERM_PRC    DECIMAL(12,2),
   CLCK_CNT    INT,
   GPRS_FLUX    BIGINT,
   PLAT_TYP    STRING,
   KEYWD    STRING,
   ADD_SHOP_CART_CNT    INT,
   CHK_REV_CNT    INT,
   CHK_DTL_CNT    INT,
   PAY_CNT    INT
)
PARTITIONED BY (branch STRING,month INT,day INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_NETSHOP_D';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_NETPAY_D;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_NETPAY_D (
   HR    INT,
   USR_NBR    STRING,
   NET_TYP    STRING,
   URL_APP_NAM    STRING,
   ACT_TYP    STRING,
   PRC    DECIMAL(12,2),
   PARVAL    DECIMAL(12,2),
   GPRS_FLUX    BIGINT
)
PARTITIONED BY (branch STRING,month INT,day INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_NETPAY_D';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_KEYWD_M;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_KEYWD_M (
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   KEYWD    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_KEYWD_M';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_CNTN_M;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_CNTN_M (
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   URL_APP_LVL1    STRING,
   URL_APP_LVL2    STRING,
   URL_APP_LVL3    STRING,
   URL_APP_NAM    STRING,
   CNTN_LV2    STRING,
   CNTN_LV3    STRING,
   CNTN_NAM    STRING,
   AUTHR_NAM    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_CNTN_M';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_NTLOG_M;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_NTLOG_M (
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   NET_APP_ID    STRING,
   URL_APP_LVL1    STRING,
   URL_APP_LVL2    STRING,
   URL_APP_LVL3    STRING,
   URL_APP_NAM    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_NTLOG_M';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_CMPT_M;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_CMPT_M (
   USR_NBR    STRING,
   NET_TYP    STRING,
   CELL_CGI    STRING,
   NET_APP_ID    STRING,
   URL_APP_LVL2    STRING,
   URL_APP_LVL3    STRING,
   URL_APP_NAM    STRING,
   TASK_NAM    STRING,
   UP_FLUX    BIGINT,
   DOWN_FLUX    BIGINT,
   CALL_CNT    INT
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_CMPT_M';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_NETSHOP_M;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_NETSHOP_M (
   USR_NBR    STRING,
   NET_TYP    STRING,
   URL_APP_NAM    STRING,
   TERM_BRND_NAM    STRING,
   TERM_MDL_NAM    STRING,
   TERM_PRC    DECIMAL(12,2),
   CLCK_CNT    INT,
   GPRS_FLUX    BIGINT,
   PLAT_TYP    STRING,
   KEYWD    STRING,
   ADD_SHOP_CART_CNT    INT,
   CHK_REV_CNT    INT,
   CHK_DTL_CNT    INT,
   PAY_CNT    INT
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_NETSHOP_M';

DROP TABLE IF EXISTS JCFW.TW_NET_PARSE_NETPAY_M;
CREATE EXTERNAL TABLE JCFW.TW_NET_PARSE_NETPAY_M (
   USR_NBR    STRING,
   NET_TYP    STRING,
   URL_APP_NAM    STRING,
   ACT_TYP    STRING,
   PRC    DECIMAL(12,2),
   PARVAL    DECIMAL(12,2),
   GPRS_FLUX    BIGINT
)
PARTITIONED BY (branch STRING,month INT)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ('field.delim'='@#$','serialization.null.format' ='NVL','serialization.encoding' ='GBK')
STORED AS TEXTFILE
LOCATION '/tenant/BIGDATA/JCFW/BZMX/OTH/EDS/TW_NET_PARSE_NETPAY_M';


