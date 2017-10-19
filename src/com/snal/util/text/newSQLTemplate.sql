#!/usr/bin/perl
###############################################################################
# �ںϼƷ��嵥��ͼ����
#
# FILENAME: NOGPRS_CDR_VIEW.pl
# PURPOSE:  ���ڴ����ںϼƷѷ�GPRS�嵥��ͼ��������������ͼ�͵�������ͼ��
# AUTHOR:   ����
# DESCRIPTION:

# ����ͼ����Դ��
# TS_E_VOC_CDR_D STAGE_�ںϼƷѱ�׼�����嵥��
# TS_E_WLAN_CDR_D STAGE_�ںϼƷѱ�׼WLAN�嵥��
# TS_E_SMMS_NEWBUSI_CDR_D STAGE_�ںϼƷѶ̲��ż�����ҵ���嵥��
# TS_E_OTH_PAY_CDR_D  STAGE_�ںϼƷ��������ÿ۷Ѽ�¼��
# TS_E_PKG_FIXFEE_CDR_D   STAGE_�ںϼƷ��ײͼ��̶��ѱ�
# TS_E_PERS_SPCL_BUSI_CDR_D   STAGE_�ںϼƷ�����ҵ���嵥��
#
# ����ͼ����Դ��
# TO_E_VOC_CDR_D    ODS_�ںϼƷѱ�׼�����嵥��
# TO_E_WLAN_CDR_D ODS_�ںϼƷѱ�׼WLAN�嵥��
# TO_E_SMMS_NEWBUSI_CDR_D ODS_�ںϼƷѶ̲��ż�����ҵ���嵥��
# TO_E_OTH_PAY_CDR_D  ODS_�ںϼƷ��������ÿ۷Ѽ�¼��
# TO_E_PKG_FIXFEE_CDR_D   ODS_�ںϼƷ��ײͼ��̶��ѱ�
# TO_E_PERS_SPCL_BUSI_CDR_D   ODS_�ںϼƷ�����ҵ���嵥��

# PARAMETERS:
#    $vDate            ͳ�����ڣ�YYYYMMDD��
#    $vBranch          �ֹ�˾����(GZ, SZ, ...)
#    $vViewName        ��GPRS�嵥��ͼ���ƣ�ֻ֧������������ͼ��
#                      STAGE.TS_E_NOGPRS_CDR_VIEW ����ʾ����������ͼ�� 
#                      ODS.TO_E_NOGPRS_CDR_VIEW   ����ʾ����������ͼ��
# EXIT STATUS:
#    0                �ɹ�
#    ����             ʧ��
# HISTORY:
# DATE       AUTHOR      VERSION    MODIFICATIONS
# ########## ########    ########## #############################################
# 2017-06-08 ����      V01.00.000 �½�����
# 2071-07-28 ����      V01.00.001 ·������Ϊ"-1"���ĳ�Ϊ"0"
# 2071-09-20 ����      V01.00.002 ��ͼ�����ֶ� "VIDEO_CALL_TYP_CD ��Ƶ�绰����"��
#################################################################################
use strict;
use dss_common;

use constant VERSION => "V01.00.001";          # �汾��

# TODO:�޸���������Ӧ��������в������
die("usage: $0 <statdate> <city> <viewname>\n")
   if ($#ARGV != 2);

# �����ܵ�����ͼ���ƣ�ֻ֧�����֣�
die("Invalid parameter, viewname must be 'STAGE.TS_E_NOGPRS_CDR_VIEW' or 'ODS.TO_E_NOGPRS_CDR_VIEW' !\n")
if(($ARGV[2] ne "STAGE.TS_E_NOGPRS_CDR_VIEW") and ($ARGV[2] ne "ODS.TO_E_NOGPRS_CDR_VIEW"));

# TODO:
my ($vDate, $vBranch,$vViewName) = @ARGV;

#my $de=dss_common::get_parallel($vBranch,"L");
my $vMonth=substr($vDate,0,6);


# ���ݿ�����
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

# ��ͼ�ֶ���
my $vViewColumn = "
    A_SWITCH_CD,    ----������
    CMCC_BRANCH_CD,    ----�ƶ��ֹ�˾����
    ROLLBACK_ID,    ----������־
    ROLLBACK_CNT,    ----��������
    USR_MOB_NBR,    ----�û�����
    A_BRND_CD,    ----�Ʒѷ�Ʒ�Ʊ���
    A_USR_TYP_CD,    ----�Ʒѷ��û�����
    USR_PKG_TYP,    ----�û��ײͷ���
    ROAM_TYP_CD,    ----�������ͱ���
    VISITED_SWITCH_CD,    ----���þ�
    CALL_AREA_CD,    ----���з�����
    CALL_DT,    ----����
    CALL_TIM,    ----ʱ��
    HR_CD,    ----ʱ�α���
    DEBET_DT,    ----���۴�������
    CARRY_TYP_CD,    ----�������ͱ���
    CNCT_NBR,    ----�����
    TOLL_TYP_CD,    ----��;���ͱ���
    DIR_TYP_CD,    ----�������ͱ���
    SRV_TYP_CD,    ----ͨ��ҵ�����ͱ���
    SUBSRV_TYP_CD,    ----ͨ����ҵ�����ͱ���
    RATE_TYP_CD,    ----�Ʒ����ͱ���
    B_NBR,    ----�Է�����
    B_SWITCH_CD,    ----�Է��û�������
    B_OPER_CD,    ----�Է���Ӫ�̱���
    B_BRND_CD,    ----�Է�Ʒ�Ʊ���
    B_USR_TYP_CD,    ----�Է��û�����
    B_AREA_CD,    ----�Է�������
    CALL_DUR,    ----ʱ��
    TOLL_DUR,    ----��;�Ʒ�ʱ��
    CALL_FLUX,    ----����
    CALL_CNT,    ----����
    IMSI,    ----IMSI
    LOCAL_CD,    ----λ�ô���
    CELL_CD,    ----С������
    OUT_ROUTE_CD,    ----��·��
    IN_ROUTE_CD,    ----��·��
    ROUTE_TYP,    ----·������
    SWITCH_NBR,    ----����������
    ROAM_NBR,    ----��̬���κ�
    IMEI,    ----IMEI
    BILL_TYP_CD,    ----�ʵ����ͱ���
    DISC_CD,    ----�Żݴ���
    VPMN_IND,    ----VPMN�Żݱ�־
    DISC_MOB_FEE,    ----�ƶ�����
    DISC_TOLL_FEE,    ----��;����
    DISC_INF_FEE,    ----��Ϣ��
    DISC_FEE,    ----�ʵ��Żݼ�����
    CALL_FREE_DUR,    ----�Żݷ�����
    ACCT_BAL,    ----�ʻ����
    ICP_STAT_CD,    ----����ҵ��ͳ�����ͱ���
    LOAD_ID,    ----���ݼ��ر��
    FILE_TYP,    ----�ļ�����
    CX_FILE_IND,    ----�����ļ���־
    NET_TYP_CD,    ----�������ͱ���
    FILE_ID,    ----�ļ�ID
    BUSI_CD,    ----ҵ�����
    CDR_BUSI_TYP,    ----�嵥ҵ������
    CORP_BUSI_IND,    ----�Ƿ���ҵ��
    VIDEO_CALL_TYP_CD  ----��Ƶ�绰����
";
# ��ͼ�ֶ�ֵ
# 1.�����嵥�ֶ�ֵ
my $vCol_VOC_CDR= "
    A_SWITCH_CD    ----������
    ,CMCC_BRANCH_CD    ----�ƶ��ֹ�˾����
    ,ROLLBACK_ID    ----������־
    ,ROLLBACK_CNT    ----��������
    ,USR_MOB_NBR    ----�û�����
    ,A_BRND_CD    ----�Ʒѷ�Ʒ�Ʊ���
    ,A_USR_TYP_CD    ----�Ʒѷ��û�����
    ,USR_PKG_TYP    ----�û��ײͷ���
    ,ROAM_TYP_CD    ----�������ͱ���
    ,VISITED_SWITCH_CD    ----���þ�
    ,CALL_AREA_CD    ----���з�����
    ,CALL_DT    ----����
    ,CALL_TIM    ----ʱ��
    ,HR_CD    ----ʱ�α���
    ,DEBET_DT    ----���۴�������
    ,CARRY_TYP_CD    ----�������ͱ���
    ,CNCT_NBR    ----�����
    ,TOLL_TYP_CD    ----��;���ͱ���
    ,DIR_TYP_CD    ----�������ͱ���
    ,SRV_TYP_CD    ----ͨ��ҵ�����ͱ���
    ,SUBSRV_TYP_CD    ----ͨ����ҵ�����ͱ���
    ,RATE_TYP_CD    ----�Ʒ����ͱ���
    ,B_NBR    ----�Է�����
    ,B_SWITCH_CD    ----�Է��û�������
    ,B_OPER_CD    ----�Է���Ӫ�̱���
    ,B_BRND_CD    ----�Է�Ʒ�Ʊ���
    ,B_USR_TYP_CD    ----�Է��û�����
    ,B_AREA_CD    ----�Է�������
    ,CALL_DUR    ----ʱ��
    ,TOLL_DUR    ----��;�Ʒ�ʱ��
    ,0    ----����
    ,CALL_CNT    ----����
    ,IMSI    ----IMSI
    ,A_LOCAL_CD    ----λ�ô���
    ,A_CELL_CD    ----С������
    ,OUT_ROUTE_CD    ----��·��
    ,IN_ROUTE_CD    ----��·��
    ,'0'    ----·������
    ,SWITCH_NBR    ----����������
    ,ROAM_NBR    ----��̬���κ�
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----�ʵ����ͱ���
    ,DISC_CD    ----�Żݴ���
    ,VPMN_IND    ----VPMN�Żݱ�־
    ,DISC_MOB_FEE    ----�ƶ�����
    ,DISC_TOLL_FEE    ----��;����
    ,DISC_INF_FEE    ----��Ϣ��
    ,DISC_FEE    ----�ʵ��Żݼ�����
    ,CALL_FREE_DUR    ----�Żݷ�����
    ,0    ----�ʻ����
    ,ICP_STAT_CD    ----����ҵ��ͳ�����ͱ���
    ,LOAD_ID    ----���ݼ��ر��
    ,FILE_TYP    ----�ļ�����
    ,CX_FILE_IND    ----�����ļ���־
    ,NET_TYP_CD    ----�������ͱ���
    ,FILE_ID    ----�ļ�ID
    ,BUSI_CD    ----ҵ�����
    ,EVENT_FORMAT_YTP_CD    ----�嵥ҵ������
    ,CORP_BUSI_IND    ----�Ƿ���ҵ��
    ,VIDEO_CALL_TYP_CD  ----��Ƶ�绰����     
";
# 2.�̲����嵥�ֶ�ֵ
my $vCol_SMMS_CDR= "
    A_SWITCH_CD    ----������
    ,CMCC_BRANCH_CD    ----�ƶ��ֹ�˾����
    ,ROLLBACK_ID    ----������־
    ,ROLLBACK_CNT    ----��������
    ,USR_MOB_NBR    ----�û�����
    ,A_BRND_CD    ----�Ʒѷ�Ʒ�Ʊ���
    ,A_USR_TYP_CD    ----�Ʒѷ��û�����
    ,USR_PKG_TYP    ----�û��ײͷ���
    ,ROAM_TYP_CD    ----�������ͱ���
    ,VISITED_SWITCH_CD    ----���þ�
    ,CALL_AREA_CD    ----���з�����
    ,CALL_DT    ----����
    ,CALL_TIM    ----ʱ��
    ,HR_CD    ----ʱ�α���
    ,DEBET_DT    ----���۴�������
    ,CARRY_TYP_CD    ----�������ͱ���
    ,CNCT_NBR    ----�����
    ,TOLL_TYP_CD    ----��;���ͱ���
    ,DIR_TYP_CD    ----�������ͱ���
    ,SRV_TYP_CD    ----ͨ��ҵ�����ͱ���
    ,SUBSRV_TYP_CD    ----ͨ����ҵ�����ͱ���
    ,RATE_TYP_CD    ----�Ʒ����ͱ���
    ,B_NBR    ----�Է�����
    ,B_SWITCH_CD    ----�Է��û�������
    ,B_OPER_CD    ----�Է���Ӫ�̱���
    ,B_BRND_CD    ----�Է�Ʒ�Ʊ���
    ,B_USR_TYP_CD    ----�Է��û�����
    ,B_AREA_CD    ----�Է�������
    ,CALL_DUR    ----ʱ��
    ,TOLL_DUR    ----��;�Ʒ�ʱ��
    ,0    ----����
    ,CALL_CNT    ----����
    ,IMSI    ----IMSI
    ,'-1'    ----λ�ô���
    ,'-1'    ----С������
    ,'-1'    ----��·��
    ,IN_ROUTE_CD    ----��·��
    ,'0'    ----·������
    ,SWITCH_NBR    ----����������
    ,ROAM_NBR    ----��̬���κ�
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----�ʵ����ͱ���
    ,DISC_CD    ----�Żݴ���
    ,VPMN_IND    ----VPMN�Żݱ�־
    ,DISC_MOB_FEE    ----�ƶ�����
    ,DISC_TOLL_FEE    ----��;����
    ,DISC_INF_FEE    ----��Ϣ��
    ,DISC_FEE    ----�ʵ��Żݼ�����
    ,CALL_FREE_DUR    ----�Żݷ�����
    ,0    ----�ʻ����
    ,ICP_STAT_CD    ----����ҵ��ͳ�����ͱ���
    ,LOAD_ID    ----���ݼ��ر��
    ,FILE_TYP    ----�ļ�����
    ,CX_FILE_IND    ----�����ļ���־
    ,NET_TYP_CD    ----�������ͱ���
    ,FILE_ID    ----�ļ�ID
    ,BUSI_CD    ----ҵ�����
    ,EVENT_FORMAT_YTP_CD    ----�嵥ҵ������
    ,CORP_BUSI_IND    ----�Ƿ���ҵ��
    ,''      ----��Ƶ�绰����
";
# 3. WLAN�嵥�ֶ�ֵ
my $vCol_WLAN_CDR = "
    A_SWITCH_CD    ----������
    ,CMCC_BRANCH_CD    ----�ƶ��ֹ�˾����
    ,ROLLBACK_ID    ----������־
    ,ROLLBACK_CNT    ----��������
    ,USR_MOB_NBR    ----�û�����
    ,A_BRND_CD    ----�Ʒѷ�Ʒ�Ʊ���
    ,A_USR_TYP_CD    ----�Ʒѷ��û�����
    ,USR_PKG_TYP    ----�û��ײͷ���
    ,ROAM_TYP_CD    ----�������ͱ���
    ,VISITED_SWITCH_CD    ----���þ�
    ,CALL_AREA_CD    ----���з�����
    ,CALL_DT    ----����
    ,CALL_TIM    ----ʱ��
    ,HR_CD    ----ʱ�α���
    ,DEBET_DT    ----���۴�������
    ,'01'    ----�������ͱ���
    ,'-1'    ----�����
    ,-1    ----��;���ͱ���
    ,'0'    ----�������ͱ���
    ,SRV_TYP_CD    ----ͨ��ҵ�����ͱ���
    ,SUBSRV_TYP_CD    ----ͨ����ҵ�����ͱ���
    ,RATE_TYP_CD    ----�Ʒ����ͱ���
    ,B_NBR    ----�Է�����
    ,'-1'    ----�Է��û�������
    ,B_OPER_CD    ----�Է���Ӫ�̱���
    ,-1    ----�Է�Ʒ�Ʊ���
    ,'0'    ----�Է��û�����
    ,'-1'    ----�Է�������
    ,CALL_DUR    ----ʱ��
    ,0    ----��;�Ʒ�ʱ��
    ,CALL_FLUX    ----����
    ,CALL_CNT    ----����
    ,IMSI    ----IMSI
    ,'-1'    ----λ�ô���
    ,'-1'    ----С������
    ,'-1'    ----��·��
    ,'-1'    ----��·��
    ,'0'    ----·������
    ,SWITCH_NBR    ----����������
    ,ROAM_NBR    ----��̬���κ�
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----�ʵ����ͱ���
    ,DISC_CD    ----�Żݴ���
    ,'0'    ----VPMN�Żݱ�־
    ,DISC_MOB_FEE    ----�ƶ�����
    ,DISC_TOLL_FEE    ----��;����
    ,DISC_INF_FEE    ----��Ϣ��
    ,DISC_FEE    ----�ʵ��Żݼ�����
    ,CALL_FREE_DUR    ----�Żݷ�����
    ,0    ----�ʻ����
    ,'-1'    ----����ҵ��ͳ�����ͱ���
    ,LOAD_ID    ----���ݼ��ر��
    ,FILE_TYP    ----�ļ�����
    ,CX_FILE_IND    ----�����ļ���־
    ,'0'    ----�������ͱ���
    ,FILE_ID    ----�ļ�ID
    ,BUSI_CD    ----ҵ�����
    ,EVENT_FORMAT_YTP_CD    ----�嵥ҵ������
    ,CORP_BUSI_IND    ----�Ƿ���ҵ��
    ,''      ----��Ƶ�绰����
";
# 4. �ײ͹̶����嵥�ֶ�ֵ
my $vCol_PKG_FIX_FEE_CDR ="
    A_SWITCH_CD    ----������
    ,CMCC_BRANCH_CD    ----�ƶ��ֹ�˾����
    ,ROLLBACK_ID    ----������־
    ,ROLLBACK_CNT    ----��������
    ,USR_MOB_NBR    ----�û�����
    ,A_BRND_CD    ----�Ʒѷ�Ʒ�Ʊ���
    ,'03'    ----�Ʒѷ��û�����
    ,USR_PKG_TYP    ----�û��ײͷ���
    ,0    ----�������ͱ���
    ,'0'    ----���þ�
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
          WHEN SUBSTR(CMCC_BRANCH_CD,1,2) = 'YF' THEN  '0766' ELSE '020' END    ----���з�����
    ,CALL_DT    ----����
    ,CALL_TIM    ----ʱ��
    ,0    ----ʱ�α���
    ,DEBET_DT    ----���۴�������
    ,'02'    ----�������ͱ���
    ,'0'    ----�����
    ,20    ----��;���ͱ���
    ,'2'    ----�������ͱ���
    ,'70'    ----ͨ��ҵ�����ͱ���
    ,'04'    ----ͨ����ҵ�����ͱ���
    ,'3'    ----�Ʒ����ͱ���
    ,BILL_ACCT_CD    ----�Է�����
    ,'0'    ----�Է��û�������
    ,'0'    ----�Է���Ӫ�̱���
    ,0    ----�Է�Ʒ�Ʊ���
    ,'0'    ----�Է��û�����
    ,'0'    ----�Է�������
    ,0    ----ʱ��
    ,0    ----��;�Ʒ�ʱ��
    ,0    ----����
    ,CALL_CNT    ----����
    ,'0'    ----IMSI
    ,'0'    ----λ�ô���
    ,'0'    ----С������
    ,'0'    ----��·��
    ,'0'    ----��·��
    ,'0'    ----·������
    ,SWITCH_NBR    ----����������
    ,'0'    ----��̬���κ�
    ,'0'    ----IMEI
    ,BILL_TYP_CD    ----�ʵ����ͱ���
    ,'0'    ----�Żݴ���
    ,'0'    ----VPMN�Żݱ�־
    ,0    ----�ƶ�����
    ,FEE    ----��;����
    ,0    ----��Ϣ��
    ,0    ----�ʵ��Żݼ�����
    ,0    ----�Żݷ�����
    ,0    ----�ʻ����
    ,ICP_STAT_CD    ----����ҵ��ͳ�����ͱ���
    ,LOAD_ID    ----���ݼ��ر��
    ,'TC'    ----�ļ�����
    ,CX_FILE_IND    ----�����ļ���־
    ,'0'    ----�������ͱ���
    ,FILE_ID    ----�ļ�ID
    ,'0'    ----ҵ�����
    ,EVENT_FORMAT_YTP_CD    ----�嵥ҵ������
    ,'0'    ----�Ƿ���ҵ��
    ,''      ----��Ƶ�绰����    
";
# 5. ����֧���嵥�ֶ�ֵ
my $vCol_OTH_PAY_CDR = "
    A_SWITCH_CD    ----������
    ,CMCC_BRANCH_CD    ----�ƶ��ֹ�˾����
    ,ROLLBACK_ID    ----������־
    ,ROLLBACK_CNT    ----��������
    ,USR_MOB_NBR    ----�û�����
    ,A_BRND_CD    ----�Ʒѷ�Ʒ�Ʊ���
    ,A_USR_TYP_CD    ----�Ʒѷ��û�����
    ,USR_PKG_TYP    ----�û��ײͷ���
    ,0    ----�������ͱ���
    ,'0'    ----���þ�
    ,'0'    ----���з�����
    ,CALL_DT    ----����
    ,CALL_TIM    ----ʱ��
    ,0    ----ʱ�α���
    ,DEBET_DT    ----���۴�������
    ,'03'    ----�������ͱ���
    ,'0'    ----�����
    ,20    ----��;���ͱ���
    ,'4'    ----�������ͱ���
    ,SRV_TYP_CD    ----ͨ��ҵ�����ͱ���
    ,SUBSRV_TYP_CD    ----ͨ����ҵ�����ͱ���
    ,'1'    ----�Ʒ����ͱ���
    ,B_NBR    ----�Է�����
    ,B_SWITCH_CD    ----�Է��û�������
    ,'0'    ----�Է���Ӫ�̱���
    ,B_BRND_CD    ----�Է�Ʒ�Ʊ���
    ,'0'    ----�Է��û�����
    ,'0'    ----�Է�������
    ,0    ----ʱ��
    ,0    ----��;�Ʒ�ʱ��
    ,0    ----����
    ,CALL_CNT    ----����
    ,'0'    ----IMSI
    ,'0'    ----λ�ô���
    ,'0'    ----С������
    ,'0'    ----��·��
    ,'0'    ----��·��
    ,'0'    ----·������
    ,SWITCH_NBR    ----����������
    ,'0'    ----��̬���κ�
    ,'0'    ----IMEI
    ,BILL_TYP_CD    ----�ʵ����ͱ���
    ,'0'    ----�Żݴ���
    ,'0'    ----VPMN�Żݱ�־
    ,0    ----�ƶ�����
    ,FEE    ----��;����
    ,0    ----��Ϣ��
    ,0    ----�ʵ��Żݼ�����
    ,0    ----�Żݷ�����
    ,0    ----�ʻ����
    ,ICP_STAT_CD    ----����ҵ��ͳ�����ͱ���
    ,LOAD_ID    ----���ݼ��ر��
    ,'01'    ----�ļ�����
    ,CX_FILE_IND    ----�����ļ���־
    ,'0'    ----�������ͱ���
    ,FILE_ID    ----�ļ�ID
    ,'0'    ----ҵ�����
    ,EVENT_FORMAT_YTP_CD    ----�嵥ҵ������
    ,'0'    ----�Ƿ���ҵ��
    ,VIDEO_CALL_TYP_CD  ----��Ƶ�绰���� 
";
# 6. ����SPCL�嵥�ֶ�ֵ
my $vCol_SPCL_CDR ="
    A_SWITCH_CD    ----������
    ,CMCC_BRANCH_CD    ----�ƶ��ֹ�˾����
    ,ROLLBACK_ID    ----������־
    ,ROLLBACK_CNT    ----��������
    ,USR_MOB_NBR    ----�û�����
    ,A_BRND_CD    ----�Ʒѷ�Ʒ�Ʊ���
    ,A_USR_TYP_CD    ----�Ʒѷ��û�����
    ,USR_PKG_TYP    ----�û��ײͷ���
    ,ROAM_TYP_CD    ----�������ͱ���
    ,VISITED_SWITCH_CD    ----���þ�
    ,CALL_AREA_CD    ----���з�����
    ,CALL_DT    ----����
    ,CALL_TIM    ----ʱ��
    ,HR_CD    ----ʱ�α���
    ,DEBET_DT    ----���۴�������
    ,CARRY_TYP_CD    ----�������ͱ���
    ,CNCT_NBR    ----�����
    ,TOLL_TYP_CD    ----��;���ͱ���
    ,DIR_TYP_CD    ----�������ͱ���
    ,SRV_TYP_CD    ----ͨ��ҵ�����ͱ���
    ,SUBSRV_TYP_CD    ----ͨ����ҵ�����ͱ���
    ,RATE_TYP_CD    ----�Ʒ����ͱ���
    ,B_NBR    ----�Է�����
    ,B_SWITCH_CD    ----�Է��û�������
    ,B_OPER_CD    ----�Է���Ӫ�̱���
    ,B_BRND_CD    ----�Է�Ʒ�Ʊ���
    ,B_USR_TYP_CD    ----�Է��û�����
    ,B_AREA_CD    ----�Է�������
    ,CALL_DUR    ----ʱ��
    ,TOLL_DUR    ----��;�Ʒ�ʱ��
    ,0    ----����
    ,CALL_CNT    ----����
    ,IMSI    ----IMSI
    ,'-1'    ----λ�ô���
    ,'-1'    ----С������
    ,'-1'    ----��·��
    ,'-1'    ----��·��
    ,'0'    ----·������
    ,SWITCH_NBR    ----����������
    ,ROAM_NBR    ----��̬���κ�
    ,IMEI    ----IMEI
    ,BILL_TYP_CD    ----�ʵ����ͱ���
    ,DISC_CD    ----�Żݴ���
    ,VPMN_IND    ----VPMN�Żݱ�־
    ,DISC_MOB_FEE    ----�ƶ�����
    ,DISC_TOLL_FEE    ----��;����
    ,DISC_INF_FEE    ----��Ϣ��
    ,DISC_FEE    ----�ʵ��Żݼ�����
    ,CALL_FREE_DUR    ----�Żݷ�����
    ,0    ----�ʻ����
    ,'-1'    ----����ҵ��ͳ�����ͱ���
    ,LOAD_ID    ----���ݼ��ر��
    ,FILE_TYP    ----�ļ�����
    ,CX_FILE_IND    ----�����ļ���־
    ,NET_TYP_CD    ----�������ͱ���
    ,FILE_ID    ----�ļ�ID
    ,BUSI_CD    ----ҵ�����
    ,EVENT_FORMAT_YTP_CD    ----�嵥ҵ������
    ,'0'    ----�Ƿ���ҵ��
    ,''  ----��Ƶ�绰���� 
";

# ��ͼ���ƺ�����Դ����
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
   # ����ձ����ڣ�����±���ͳ�Ƶ��յ����ݡ�
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
# �����������ͼ�Ѿ����ڣ���ɹ��˳���
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