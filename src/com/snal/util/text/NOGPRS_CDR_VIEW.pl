#!/usr/bin/perl
###############################################################################
# 融合计费清单视图创建
#
# FILENAME: NOGPRS_CDR_VIEW.pl
# PURPOSE:  用于创建融合计费非GPRS清单视图，包括地市日视图和地市月视图。
# AUTHOR:   罗涛
# DESCRIPTION:

# 日视图数据源表：
# TS_E_VOC_CDR_D STAGE_融合计费标准语音清单表
# TS_E_WLAN_CDR_D STAGE_融合计费标准WLAN清单表
# TS_E_SMMS_NEWBUSI_CDR_D STAGE_融合计费短彩信及数据业务清单表
# TS_E_OTH_PAY_CDR_D  STAGE_融合计费其他费用扣费记录表
# TS_E_PKG_FIXFEE_CDR_D   STAGE_融合计费套餐及固定费表
# TS_E_PERS_SPCL_BUSI_CDR_D   STAGE_融合计费特殊业务清单表
#
# 月视图数据源表：
# TO_E_VOC_CDR_D    ODS_融合计费标准语音清单表
# TO_E_WLAN_CDR_D ODS_融合计费标准WLAN清单表
# TO_E_SMMS_NEWBUSI_CDR_D ODS_融合计费短彩信及数据业务清单表
# TO_E_OTH_PAY_CDR_D  ODS_融合计费其他费用扣费记录表
# TO_E_PKG_FIXFEE_CDR_D   ODS_融合计费套餐及固定费表
# TO_E_PERS_SPCL_BUSI_CDR_D   ODS_融合计费特殊业务清单表

# PARAMETERS:
#    $vDate            统计日期（YYYYMMDD）
#    $vBranch          分公司编码(GZ, SZ, ...)
#    $vViewName        非GPRS清单视图名称，只支持如下两种视图：
#                      STAGE.TS_E_NOGPRS_CDR_VIEW （表示各地市日视图） 
#                      ODS.TO_E_NOGPRS_CDR_VIEW   （表示各地市月视图）
# EXIT STATUS:
#    0                成功
#    其它             失败
# HISTORY:
# DATE       AUTHOR      VERSION    MODIFICATIONS
# ########## ########    ########## #############################################
# 2017-06-08 罗涛      V01.00.000 新建程序
# 2071-07-28 罗涛      V01.00.001 路由类型为"-1"，改成为"0"
# 2071-09-20 罗涛      V01.00.002 视图新增字段 "VIDEO_CALL_TYP_CD 视频电话类型"。
# 2017-09-29 罗涛      V01.00.004 VIDEO_CALL_TYP_CD 没有值时取值默 '-'。
#################################################################################
use strict;
use dss_common;

use constant VERSION => "V01.00.001";          # 版本号

# TODO:修改这里以适应你的命令行参数设计
die("usage: $0 <statdate> <city> <viewname>\n")
   if ($#ARGV != 2);

# 检查接受到的视图名称（只支持两种）
die("Invalid parameter, viewname must be 'STAGE.TS_E_NOGPRS_CDR_VIEW' or 'ODS.TO_E_NOGPRS_CDR_VIEW' !\n")
if(($ARGV[2] ne "STAGE.TS_E_NOGPRS_CDR_VIEW") and ($ARGV[2] ne "ODS.TO_E_NOGPRS_CDR_VIEW"));

# TODO:
my ($vDate, $vBranch,$vViewName) = @ARGV;

#my $de=dss_common::get_parallel($vBranch,"L");
my $vMonth=substr($vDate,0,6);


# 数据库连接
my $conn = dss_common->new();
$conn->writelog(dss_common::INFO, "VERSION=" . VERSION);
$conn->writelog(dss_common::INFO, "Data_Date=" .$vDate."  Branch=".$vBranch . " CREATE VIEW ".$vViewName);
$conn->connect_dwdb($vBranch);  # change to connect_ctldb() when needed

#my $ssid = $conn->get_sessionid();

END
{
 if($conn){
     $conn->cleanup();
     $conn->writelog(dss_common::INFO, "exit($?)");
 }
}

# 视图字段名
my $vViewColumn = "
    A_SWITCH_CD,    ----归属局
    CMCC_BRANCH_CD,    ----移动分公司编码
    ROLLBACK_ID,    ----冲销标志
    ROLLBACK_CNT,    ----冲销计数
    USR_MOB_NBR,    ----用户号码
    A_BRND_CD,    ----计费方品牌编码
    A_USR_TYP_CD,    ----计费方用户类型
    USR_PKG_TYP,    ----用户套餐分类
    ROAM_TYP_CD,    ----漫游类型编码
    VISITED_SWITCH_CD,    ----被访局
    CALL_AREA_CD,    ----呼叫发生地
    CALL_DT,    ----日期
    CALL_TIM,    ----时间
    HR_CD,    ----时段编码
    DEBET_DT,    ----批价处理日期
    CARRY_TYP_CD,    ----承载类型编码
    CNCT_NBR,    ----接入号
    TOLL_TYP_CD,    ----长途类型编码
    DIR_TYP_CD,    ----方向类型编码
    SRV_TYP_CD,    ----通信业务类型编码
    SUBSRV_TYP_CD,    ----通信子业务类型编码
    RATE_TYP_CD,    ----计费类型编码
    B_NBR,    ----对方号码
    B_SWITCH_CD,    ----对方用户归属局
    B_OPER_CD,    ----对方运营商编码
    B_BRND_CD,    ----对方品牌编码
    B_USR_TYP_CD,    ----对方用户类型
    B_AREA_CD,    ----对方归属地
    CALL_DUR,    ----时长
    TOLL_DUR,    ----长途计费时长
    CALL_FLUX,    ----流量
    CALL_CNT,    ----次数
    IMSI,    ----IMSI
    LOCAL_CD,    ----位置代码
    CELL_CD,    ----小区代码
    OUT_ROUTE_CD,    ----出路由
    IN_ROUTE_CD,    ----入路由
    ROUTE_TYP,    ----路由类型
    SWITCH_NBR,    ----交换机代码
    ROAM_NBR,    ----动态漫游号
    IMEI,    ----IMEI
    BILL_TYP_CD,    ----帐单类型编码
    DISC_CD,    ----优惠代码
    VPMN_IND,    ----VPMN优惠标志
    DISC_MOB_FEE,    ----移动话费
    DISC_TOLL_FEE,    ----长途话费
    DISC_INF_FEE,    ----信息费
    DISC_FEE,    ----帐单优惠减免金额
    CALL_FREE_DUR,    ----优惠分钟数
    ACCT_BAL,    ----帐户余额
    ICP_STAT_CD,    ----数据业务统计类型编码
    LOAD_ID,    ----数据加载编号
    FILE_TYP,    ----文件类型
    CX_FILE_IND,    ----冲销文件标志
    NET_TYP_CD,    ----网络类型编码
    FILE_ID,    ----文件ID
    BUSI_CD,    ----业务编码
    CDR_BUSI_TYP,    ----清单业务类型
    CORP_BUSI_IND,    ----是否集团业务
    VIDEO_CALL_TYP_CD  ----视频电话类型
";
# 视图字段值
# 1.语音清单字段值
my $vCol_VOC_CDR= "
    A_SWITCH_CD    ----归属局
    ,CMCC_BRANCH_CD    ----移动分公司编码
    ,ROLLBACK_ID    ----冲销标志
    ,ROLLBACK_CNT    ----冲销计数
    ,USR_MOB_NBR    ----用户号码
    ,A_BRND_CD    ----计费方品牌编码
    ,A_USR_TYP_CD    ----计费方用户类型
    ,USR_PKG_TYP    ----用户套餐分类
    ,ROAM_TYP_CD    ----漫游类型编码
    ,VISITED_SWITCH_CD    ----被访局
    ,CALL_AREA_CD    ----呼叫发生地
    ,CALL_DT    ----日期
    ,CALL_TIM    ----时间
    ,HR_CD    ----时段编码
    ,DEBET_DT    ----批价处理日期
    ,CARRY_TYP_CD    ----承载类型编码
    ,CNCT_NBR    ----接入号
    ,TOLL_TYP_CD    ----长途类型编码
    ,DIR_TYP_CD    ----方向类型编码
    ,SRV_TYP_CD    ----通信业务类型编码
    ,SUBSRV_TYP_CD    ----通信子业务类型编码
    ,RATE_TYP_CD    ----计费类型编码
    ,B_NBR    ----对方号码
    ,B_SWITCH_CD    ----对方用户归属局
    ,B_OPER_CD    ----对方运营商编码
    ,B_BRND_CD    ----对方品牌编码
    ,B_USR_TYP_CD    ----对方用户类型
    ,B_AREA_CD    ----对方归属地
    ,CALL_DUR    ----时长
    ,TOLL_DUR    ----长途计费时长
    ,0    ----流量
    ,CALL_CNT    ----次数
    ,IMSI    ----IMSI
    ,A_LOCAL_CD    ----位置代码
    ,A_CELL_CD    ----小区代码
    ,OUT_ROUTE_CD    ----出路由
    ,IN_ROUTE_CD    ----入路由
    ,'0'    ----路由类型
    ,SWITCH_NBR    ----交换机代码
    ,ROAM_NBR    ----动态漫游号
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----帐单类型编码
    ,DISC_CD    ----优惠代码
    ,VPMN_IND    ----VPMN优惠标志
    ,DISC_MOB_FEE    ----移动话费
    ,DISC_TOLL_FEE    ----长途话费
    ,DISC_INF_FEE    ----信息费
    ,DISC_FEE    ----帐单优惠减免金额
    ,CALL_FREE_DUR    ----优惠分钟数
    ,0    ----帐户余额
    ,ICP_STAT_CD    ----数据业务统计类型编码
    ,LOAD_ID    ----数据加载编号
    ,FILE_TYP    ----文件类型
    ,CX_FILE_IND    ----冲销文件标志
    ,NET_TYP_CD    ----网络类型编码
    ,FILE_ID    ----文件ID
    ,BUSI_CD    ----业务编码
    ,EVENT_FORMAT_YTP_CD    ----清单业务类型
    ,CORP_BUSI_IND    ----是否集团业务
    ,VIDEO_CALL_TYP_CD  ----视频电话类型     
";
# 2.短彩信清单字段值
my $vCol_SMMS_CDR= "
    A_SWITCH_CD    ----归属局
    ,CMCC_BRANCH_CD    ----移动分公司编码
    ,ROLLBACK_ID    ----冲销标志
    ,ROLLBACK_CNT    ----冲销计数
    ,USR_MOB_NBR    ----用户号码
    ,A_BRND_CD    ----计费方品牌编码
    ,A_USR_TYP_CD    ----计费方用户类型
    ,USR_PKG_TYP    ----用户套餐分类
    ,ROAM_TYP_CD    ----漫游类型编码
    ,VISITED_SWITCH_CD    ----被访局
    ,CALL_AREA_CD    ----呼叫发生地
    ,CALL_DT    ----日期
    ,CALL_TIM    ----时间
    ,HR_CD    ----时段编码
    ,DEBET_DT    ----批价处理日期
    ,CARRY_TYP_CD    ----承载类型编码
    ,CNCT_NBR    ----接入号
    ,TOLL_TYP_CD    ----长途类型编码
    ,DIR_TYP_CD    ----方向类型编码
    ,SRV_TYP_CD    ----通信业务类型编码
    ,SUBSRV_TYP_CD    ----通信子业务类型编码
    ,RATE_TYP_CD    ----计费类型编码
    ,B_NBR    ----对方号码
    ,B_SWITCH_CD    ----对方用户归属局
    ,B_OPER_CD    ----对方运营商编码
    ,B_BRND_CD    ----对方品牌编码
    ,B_USR_TYP_CD    ----对方用户类型
    ,B_AREA_CD    ----对方归属地
    ,CALL_DUR    ----时长
    ,TOLL_DUR    ----长途计费时长
    ,0    ----流量
    ,CALL_CNT    ----次数
    ,IMSI    ----IMSI
    ,'-1'    ----位置代码
    ,'-1'    ----小区代码
    ,'-1'    ----出路由
    ,IN_ROUTE_CD    ----入路由
    ,'0'    ----路由类型
    ,SWITCH_NBR    ----交换机代码
    ,ROAM_NBR    ----动态漫游号
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----帐单类型编码
    ,DISC_CD    ----优惠代码
    ,VPMN_IND    ----VPMN优惠标志
    ,DISC_MOB_FEE    ----移动话费
    ,DISC_TOLL_FEE    ----长途话费
    ,DISC_INF_FEE    ----信息费
    ,DISC_FEE    ----帐单优惠减免金额
    ,CALL_FREE_DUR    ----优惠分钟数
    ,0    ----帐户余额
    ,ICP_STAT_CD    ----数据业务统计类型编码
    ,LOAD_ID    ----数据加载编号
    ,FILE_TYP    ----文件类型
    ,CX_FILE_IND    ----冲销文件标志
    ,NET_TYP_CD    ----网络类型编码
    ,FILE_ID    ----文件ID
    ,BUSI_CD    ----业务编码
    ,EVENT_FORMAT_YTP_CD    ----清单业务类型
    ,CORP_BUSI_IND    ----是否集团业务
    ,'-'      ----视频电话类型
";
# 3. WLAN清单字段值
my $vCol_WLAN_CDR = "
    A_SWITCH_CD    ----归属局
    ,CMCC_BRANCH_CD    ----移动分公司编码
    ,ROLLBACK_ID    ----冲销标志
    ,ROLLBACK_CNT    ----冲销计数
    ,USR_MOB_NBR    ----用户号码
    ,A_BRND_CD    ----计费方品牌编码
    ,A_USR_TYP_CD    ----计费方用户类型
    ,USR_PKG_TYP    ----用户套餐分类
    ,ROAM_TYP_CD    ----漫游类型编码
    ,VISITED_SWITCH_CD    ----被访局
    ,CALL_AREA_CD    ----呼叫发生地
    ,CALL_DT    ----日期
    ,CALL_TIM    ----时间
    ,HR_CD    ----时段编码
    ,DEBET_DT    ----批价处理日期
    ,'01'    ----承载类型编码
    ,'-1'    ----接入号
    ,-1    ----长途类型编码
    ,'0'    ----方向类型编码
    ,SRV_TYP_CD    ----通信业务类型编码
    ,SUBSRV_TYP_CD    ----通信子业务类型编码
    ,RATE_TYP_CD    ----计费类型编码
    ,B_NBR    ----对方号码
    ,'-1'    ----对方用户归属局
    ,B_OPER_CD    ----对方运营商编码
    ,-1    ----对方品牌编码
    ,'0'    ----对方用户类型
    ,'-1'    ----对方归属地
    ,CALL_DUR    ----时长
    ,0    ----长途计费时长
    ,CALL_FLUX    ----流量
    ,CALL_CNT    ----次数
    ,IMSI    ----IMSI
    ,'-1'    ----位置代码
    ,'-1'    ----小区代码
    ,'-1'    ----出路由
    ,'-1'    ----入路由
    ,'0'    ----路由类型
    ,SWITCH_NBR    ----交换机代码
    ,ROAM_NBR    ----动态漫游号
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----帐单类型编码
    ,DISC_CD    ----优惠代码
    ,'0'    ----VPMN优惠标志
    ,DISC_MOB_FEE    ----移动话费
    ,DISC_TOLL_FEE    ----长途话费
    ,DISC_INF_FEE    ----信息费
    ,DISC_FEE    ----帐单优惠减免金额
    ,CALL_FREE_DUR    ----优惠分钟数
    ,0    ----帐户余额
    ,'-1'    ----数据业务统计类型编码
    ,LOAD_ID    ----数据加载编号
    ,FILE_TYP    ----文件类型
    ,CX_FILE_IND    ----冲销文件标志
    ,'0'    ----网络类型编码
    ,FILE_ID    ----文件ID
    ,BUSI_CD    ----业务编码
    ,EVENT_FORMAT_YTP_CD    ----清单业务类型
    ,CORP_BUSI_IND    ----是否集团业务
    ,'-'      ----视频电话类型
";
# 4. 套餐固定费清单字段值
my $vCol_PKG_FIX_FEE_CDR ="
    A_SWITCH_CD    ----归属局
    ,CMCC_BRANCH_CD    ----移动分公司编码
    ,ROLLBACK_ID    ----冲销标志
    ,ROLLBACK_CNT    ----冲销计数
    ,USR_MOB_NBR    ----用户号码
    ,A_BRND_CD    ----计费方品牌编码
    ,'03'    ----计费方用户类型
    ,USR_PKG_TYP    ----用户套餐分类
    ,0    ----漫游类型编码
    ,'0'    ----被访局
    ,CASE WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'GZ' THEN  '020'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'SZ' THEN  '0755'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'ZH' THEN  '0756'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'FS' THEN  '0757'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'ST' THEN  '0754'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'HZ' THEN  '0752'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'ZJ' THEN  '0759'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'JM' THEN  '0750'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'ZQ' THEN  '0758'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'SG' THEN  '0751'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'MZ' THEN  '0753'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'DG' THEN  '0769'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'ZS' THEN  '0760'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'FS' THEN  '0757'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'MM' THEN  '0668'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'SW' THEN  '0660'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'CZ' THEN  '0768'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'JY' THEN  '0663'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'YJ' THEN  '0662'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'QY' THEN  '0763'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'HY' THEN  '0762'      
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'YF' THEN  '0766' ELSE '020' END    ----呼叫发生地
    ,CALL_DT    ----日期
    ,CALL_TIM    ----时间
    ,0    ----时段编码
    ,DEBET_DT    ----批价处理日期
    ,'02'    ----承载类型编码
    ,'0'    ----接入号
    ,20    ----长途类型编码
    ,'2'    ----方向类型编码
    ,'70'    ----通信业务类型编码
    ,'04'    ----通信子业务类型编码
    ,'3'    ----计费类型编码
    ,BILL_ACCT_CD    ----对方号码
    ,'0'    ----对方用户归属局
    ,'0'    ----对方运营商编码
    ,0    ----对方品牌编码
    ,'0'    ----对方用户类型
    ,'0'    ----对方归属地
    ,0    ----时长
    ,0    ----长途计费时长
    ,0    ----流量
    ,CALL_CNT    ----次数
    ,'0'    ----IMSI
    ,'0'    ----位置代码
    ,'0'    ----小区代码
    ,'0'    ----出路由
    ,'0'    ----入路由
    ,'0'    ----路由类型
    ,SWITCH_NBR    ----交换机代码
    ,'0'    ----动态漫游号
    ,'0'    ----IMEI
    ,BILL_TYP_CD    ----帐单类型编码
    ,'0'    ----优惠代码
    ,'0'    ----VPMN优惠标志
    ,0    ----移动话费
    ,FEE    ----长途话费
    ,0    ----信息费
    ,0    ----帐单优惠减免金额
    ,0    ----优惠分钟数
    ,0    ----帐户余额
    ,ICP_STAT_CD    ----数据业务统计类型编码
    ,LOAD_ID    ----数据加载编号
    ,'TC'    ----文件类型
    ,CX_FILE_IND    ----冲销文件标志
    ,'0'    ----网络类型编码
    ,FILE_ID    ----文件ID
    ,'0'    ----业务编码
    ,EVENT_FORMAT_YTP_CD    ----清单业务类型
    ,'0'    ----是否集团业务
    ,'-'      ----视频电话类型    
";
# 5. 其他支付清单字段值
my $vCol_OTH_PAY_CDR = "
    A_SWITCH_CD    ----归属局
    ,CMCC_BRANCH_CD    ----移动分公司编码
    ,ROLLBACK_ID    ----冲销标志
    ,ROLLBACK_CNT    ----冲销计数
    ,USR_MOB_NBR    ----用户号码
    ,A_BRND_CD    ----计费方品牌编码
    ,A_USR_TYP_CD    ----计费方用户类型
    ,USR_PKG_TYP    ----用户套餐分类
    ,0    ----漫游类型编码
    ,'0'    ----被访局
    ,'0'    ----呼叫发生地
    ,CALL_DT    ----日期
    ,CALL_TIM    ----时间
    ,0    ----时段编码
    ,DEBET_DT    ----批价处理日期
    ,'03'    ----承载类型编码
    ,'0'    ----接入号
    ,20    ----长途类型编码
    ,'4'    ----方向类型编码
    ,SRV_TYP_CD    ----通信业务类型编码
    ,SUBSRV_TYP_CD    ----通信子业务类型编码
    ,'1'    ----计费类型编码
    ,B_NBR    ----对方号码
    ,B_SWITCH_CD    ----对方用户归属局
    ,'0'    ----对方运营商编码
    ,B_BRND_CD    ----对方品牌编码
    ,'0'    ----对方用户类型
    ,'0'    ----对方归属地
    ,0    ----时长
    ,0    ----长途计费时长
    ,0    ----流量
    ,CALL_CNT    ----次数
    ,'0'    ----IMSI
    ,'0'    ----位置代码
    ,'0'    ----小区代码
    ,'0'    ----出路由
    ,'0'    ----入路由
    ,'0'    ----路由类型
    ,SWITCH_NBR    ----交换机代码
    ,'0'    ----动态漫游号
    ,'0'    ----IMEI
    ,BILL_TYP_CD    ----帐单类型编码
    ,'0'    ----优惠代码
    ,'0'    ----VPMN优惠标志
    ,0    ----移动话费
    ,FEE    ----长途话费
    ,0    ----信息费
    ,0    ----帐单优惠减免金额
    ,0    ----优惠分钟数
    ,0    ----帐户余额
    ,ICP_STAT_CD    ----数据业务统计类型编码
    ,LOAD_ID    ----数据加载编号
    ,'01'    ----文件类型
    ,CX_FILE_IND    ----冲销文件标志
    ,'0'    ----网络类型编码
    ,FILE_ID    ----文件ID
    ,'0'    ----业务编码
    ,EVENT_FORMAT_YTP_CD    ----清单业务类型
    ,'0'    ----是否集团业务
    ,VIDEO_CALL_TYP_CD  ----视频电话类型 
";
# 6. 个人SPCL清单字段值
my $vCol_SPCL_CDR ="
    A_SWITCH_CD    ----归属局
    ,CMCC_BRANCH_CD    ----移动分公司编码
    ,ROLLBACK_ID    ----冲销标志
    ,ROLLBACK_CNT    ----冲销计数
    ,USR_MOB_NBR    ----用户号码
    ,A_BRND_CD    ----计费方品牌编码
    ,A_USR_TYP_CD    ----计费方用户类型
    ,USR_PKG_TYP    ----用户套餐分类
    ,ROAM_TYP_CD    ----漫游类型编码
    ,VISITED_SWITCH_CD    ----被访局
    ,CALL_AREA_CD    ----呼叫发生地
    ,CALL_DT    ----日期
    ,CALL_TIM    ----时间
    ,HR_CD    ----时段编码
    ,DEBET_DT    ----批价处理日期
    ,CARRY_TYP_CD    ----承载类型编码
    ,CNCT_NBR    ----接入号
    ,TOLL_TYP_CD    ----长途类型编码
    ,DIR_TYP_CD    ----方向类型编码
    ,SRV_TYP_CD    ----通信业务类型编码
    ,SUBSRV_TYP_CD    ----通信子业务类型编码
    ,RATE_TYP_CD    ----计费类型编码
    ,B_NBR    ----对方号码
    ,B_SWITCH_CD    ----对方用户归属局
    ,B_OPER_CD    ----对方运营商编码
    ,B_BRND_CD    ----对方品牌编码
    ,B_USR_TYP_CD    ----对方用户类型
    ,B_AREA_CD    ----对方归属地
    ,CALL_DUR    ----时长
    ,TOLL_DUR    ----长途计费时长
    ,0    ----流量
    ,CALL_CNT    ----次数
    ,IMSI    ----IMSI
    ,'-1'    ----位置代码
    ,'-1'    ----小区代码
    ,'-1'    ----出路由
    ,'-1'    ----入路由
    ,'0'    ----路由类型
    ,SWITCH_NBR    ----交换机代码
    ,ROAM_NBR    ----动态漫游号
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----帐单类型编码
    ,DISC_CD    ----优惠代码
    ,VPMN_IND    ----VPMN优惠标志
    ,DISC_MOB_FEE    ----移动话费
    ,DISC_TOLL_FEE    ----长途话费
    ,DISC_INF_FEE    ----信息费
    ,DISC_FEE    ----帐单优惠减免金额
    ,CALL_FREE_DUR    ----优惠分钟数
    ,0    ----帐户余额
    ,'-1'    ----数据业务统计类型编码
    ,LOAD_ID    ----数据加载编号
    ,FILE_TYP    ----文件类型
    ,CX_FILE_IND    ----冲销文件标志
    ,NET_TYP_CD    ----网络类型编码
    ,FILE_ID    ----文件ID
    ,BUSI_CD    ----业务编码
    ,EVENT_FORMAT_YTP_CD    ----清单业务类型
    ,'0'    ----是否集团业务
    ,'-'  ----视频电话类型 
";

# 视图名称和数据源表名
my $vView ="";
my $vViewSql =  "";
if($vViewName eq "STAGE.TS_E_NOGPRS_CDR_VIEW"){
   $vView = "STAGE.TS_E_NOGPRS_CDR_VIEW_".$vBranch.$vDate;
   my $vTS_E_VOC_CDR_D ="STAGE.TS_E_VOC_CDR_D_".$vBranch.$vDate;
   my $vTS_E_SMMS_NEWBUSI_CDR_D ="STAGE.TS_E_SMMS_NEWBUSI_CDR_D_".$vBranch.$vDate;
   my $vTS_E_WLAN_CDR_D ="STAGE.TS_E_WLAN_CDR_D_".$vBranch.$vDate;
   my $vTS_E_PKG_FIXFEE_CDR_D ="STAGE.TS_E_PKG_FIXFEE_CDR_D_".$vBranch.$vDate;
   my $vTS_E_OTH_PAY_CDR_D ="STAGE.TS_E_OTH_PAY_CDR_D_".$vBranch.$vDate;
   my $vTS_E_PERS_SPCL_BUSI_CDR_D ="STAGE.TS_E_PERS_SPCL_BUSI_CDR_D_".$vBranch.$vDate;
   # 如果日表不存在，则从月表中统计当日的数据。
   if ($conn->table_exists($vTS_E_VOC_CDR_D)==0){
     $vTS_E_VOC_CDR_D = "ODS.TO_E_VOC_CDR_D_".$vBranch.$vMonth." WHERE DEBET_DT=".$vDate;
   }
   if ($conn->table_exists($vTS_E_SMMS_NEWBUSI_CDR_D)==0){
     $vTS_E_SMMS_NEWBUSI_CDR_D = "ODS.TO_E_SMMS_NEWBUSI_CDR_D_".$vBranch.$vMonth." WHERE DEBET_DT=".$vDate;
   }
   if ($conn->table_exists($vTS_E_WLAN_CDR_D)==0){
     $vTS_E_WLAN_CDR_D = "ODS.TO_E_WLAN_CDR_D_".$vBranch.$vMonth." WHERE DEBET_DT=".$vDate;
   }
   if ($conn->table_exists($vTS_E_PKG_FIXFEE_CDR_D)==0){
     $vTS_E_PKG_FIXFEE_CDR_D = "ODS.TO_E_PKG_FIXFEE_CDR_D_".$vBranch.$vMonth." WHERE DEBET_DT=".$vDate;
   }
   if ($conn->table_exists($vTS_E_OTH_PAY_CDR_D)==0){
     $vTS_E_OTH_PAY_CDR_D = "ODS.TO_E_OTH_PAY_CDR_D_".$vBranch.$vMonth." WHERE DEBET_DT=".$vDate;
   }
   if ($conn->table_exists($vTS_E_PERS_SPCL_BUSI_CDR_D)==0){
     $vTS_E_PERS_SPCL_BUSI_CDR_D = "ODS.TO_E_PERS_SPCL_BUSI_CDR_D_".$vBranch.$vMonth." WHERE DEBET_DT=".$vDate;
   }

   $vViewSql = "CREATE VIEW ".$vView." (".$vViewColumn.") AS 
             SELECT ".$vCol_VOC_CDR." FROM ".$vTS_E_VOC_CDR_D."
   UNION ALL SELECT ".$vCol_SMMS_CDR." FROM ".$vTS_E_SMMS_NEWBUSI_CDR_D."
   UNION ALL SELECT ".$vCol_WLAN_CDR." FROM ".$vTS_E_WLAN_CDR_D."
   UNION ALL SELECT ".$vCol_PKG_FIX_FEE_CDR." FROM ".$vTS_E_PKG_FIXFEE_CDR_D."
   UNION ALL SELECT ".$vCol_OTH_PAY_CDR." FROM ".$vTS_E_OTH_PAY_CDR_D."
   UNION ALL SELECT ".$vCol_SPCL_CDR." FROM ".$vTS_E_PERS_SPCL_BUSI_CDR_D
   
}elsif($vViewName eq "ODS.TO_E_NOGPRS_CDR_VIEW"){
   $vView  = "ODS.TO_E_NOGPRS_CDR_VIEW_".$vBranch.$vMonth;
   my $vTO_E_VOC_CDR_D ="ODS.TO_E_VOC_CDR_D_".$vBranch.$vMonth;
   my $vTO_E_SMMS_NEWBUSI_CDR_D ="ODS.TO_E_SMMS_NEWBUSI_CDR_D_".$vBranch.$vMonth;
   my $vTO_E_WLAN_CDR_D ="ODS.TO_E_WLAN_CDR_D_".$vBranch.$vMonth;
   my $vTO_E_PKG_FIXFEE_CDR_D ="ODS.TO_E_PKG_FIXFEE_CDR_D_".$vBranch.$vMonth;
   my $vTO_E_OTH_PAY_CDR_D ="ODS.TO_E_OTH_PAY_CDR_D_".$vBranch.$vMonth;
   my $vTO_E_PERS_SPCL_BUSI_CDR_D ="ODS.TO_E_PERS_SPCL_BUSI_CDR_D_".$vBranch.$vMonth;

   $vViewSql = "CREATE VIEW ".$vView." (".$vViewColumn.") AS 
             SELECT ".$vCol_VOC_CDR." FROM ".$vTO_E_VOC_CDR_D."
   UNION ALL SELECT ".$vCol_SMMS_CDR." FROM ".$vTO_E_SMMS_NEWBUSI_CDR_D."
   UNION ALL SELECT ".$vCol_WLAN_CDR." FROM ".$vTO_E_WLAN_CDR_D."
   UNION ALL SELECT ".$vCol_PKG_FIX_FEE_CDR." FROM ".$vTO_E_PKG_FIXFEE_CDR_D."
   UNION ALL SELECT ".$vCol_OTH_PAY_CDR." FROM ".$vTO_E_OTH_PAY_CDR_D."
   UNION ALL SELECT ".$vCol_SPCL_CDR." FROM ".$vTO_E_PERS_SPCL_BUSI_CDR_D
}
# 如果待建的视图已经存在，则成功退出。
if($conn->table_exists($vView)){
    print("the view ".$vView." already exists. \n");
    exit(0);
}
my $sql_stmts = <<EOF
    $vViewSql;
    COMMIT;
EOF
;

exit $conn->batch_execute_sql($sql_stmts);
