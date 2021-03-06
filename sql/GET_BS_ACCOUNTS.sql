CREATE OR REPLACE TYPE OBJECT_BS_ACCOUNTS IS OBJECT (
  SEQ_NBR NUMBER,
  ROW_NBR NUMBER,  
  TITLE VARCHAR2(128), 
  DISPLAY_TXT VARCHAR2(128),
  SUB_TITLE VARCHAR2(128),   
  COA_CDE VARCHAR2(32),
  AMNT NUMBER,
  MAM_ID NUMBER
);
/
CREATE OR REPLACE TYPE TABLE_BS_ACCOUNTS IS TABLE OF OBJECT_BS_ACCOUNTS;
/
CREATE OR REPLACE
FUNCTION GET_BS_ACCOUNTS(FP_MANAGEMENT_ACCOUNTS_ID IN NUMBER, FP_BU_ID IN NUMBER, FP_END_DTE IN VARCHAR2, FP_FINYEAR_ID IN NUMBER, FP_COMPANY_ID IN NUMBER) 
RETURN TABLE_BS_ACCOUNTS PIPELINED IS
CURSOR CS_BS_ACCOUNTS IS 
  SELECT MAM.MANAGEMENT_ACCOUNTS_MASTER_ID, MAM.SEQ_NBR, MAM.TITLE, MAM.DISPLAY_TXT, MAD.SUB_TITLE, MAD.COA_CDE, 
  MAD.ROW_NBR, MAD.SOURCE_TYP DETAIL_SOURCE_TYP, MAD.SOURCE_TXT, MAM.SOURCE_TYP MASTER_SOURCE_TYP,
  MAD.MAP_SEQ_NBR, MAD.MAP_TITLE, MAD.MAP_DISPLAY_TXT, MAD.MAP_SUB_TITLE, MAD.MAP_SOURCE_TYP, MAD.MAP_ROW_NBR
  FROM MANAGEMENT_ACCOUNTS MA, MANAGEMENT_ACCOUNTS_MASTER MAM, MANAGEMENT_ACCOUNTS_DETAIL MAD
    WHERE MA.MANAGEMENT_ACCOUNTS_ID=MAM.MANAGEMENT_ACCOUNTS_ID
      AND MAM.MANAGEMENT_ACCOUNTS_MASTER_ID=MAD.MANAGEMENT_ACCOUNTS_MASTER_ID(+)
      AND MA.MANAGEMENT_ACCOUNTS_ID=FP_MANAGEMENT_ACCOUNTS_ID      
      ORDER BY MAM.SEQ_NBR;

CURSOR CS_DATA(CP_COA_CDE IN VARCHAR2,CP_SOURCE_TYP IN VARCHAR2) IS  
    SELECT VD.COA_CDE, 
      SUM(CASE WHEN (CP_SOURCE_TYP='DR' AND VD.AMNT>0) THEN NVL(VD.AMNT,0) 
               WHEN (CP_SOURCE_TYP='CR' AND VD.AMNT<0) THEN 
                  (ABS(CASE WHEN NVL(VD.AMNT,0)<0 THEN CASE WHEN (S.BASED_ON='NON-COMMISSION' AND VD.COA_CDE LIKE '500-01-01%' AND VST.VOUCHER_SUB_TYP_DESC IN ('SI','FAL')) THEN 
                        CASE WHEN (S.BUYSELL_START_DTE IS NOT NULL AND VM.VOUCHER_DTE >= S.BUYSELL_START_DTE) THEN NVL(VD.COST_VALUE,0) ELSE NVL(VD.AMNT,0) END ELSE NVL(VD.AMNT,0) END ELSE 0 END))
               WHEN (CP_SOURCE_TYP='BOTH') THEN 
                  (CASE WHEN NVL(VD.AMNT,0)<0 THEN CASE WHEN (S.BASED_ON='NON-COMMISSION' AND VD.COA_CDE LIKE '500-01-01%' AND VST.VOUCHER_SUB_TYP_DESC IN ('SI','FAL')) THEN 
                    CASE WHEN (S.BUYSELL_START_DTE IS NOT NULL AND VM.VOUCHER_DTE >= S.BUYSELL_START_DTE) THEN NVL(VD.COST_VALUE,0) ELSE NVL(VD.AMNT,0) END ELSE NVL(VD.AMNT,0) END ELSE NVL(VD.AMNT,0) END)
               ELSE 0 END) AMNT
      FROM VOUCHER_DETAIL VD, VOUCHER_MASTER VM, VOUCHER_SUB_TYPE VST, SITE S
      WHERE VM.VOUCHER_MASTER_ID=VD.VOUCHER_MASTER_ID
       AND VST.VOUCHER_SUB_TYP_ID=VM.VOUCHER_SUB_TYP_ID AND VM.SITE_ID=S.SITE_ID
       AND VM.SITE_ID IN (SELECT S.SITE_ID FROM SITE S, SITE_BUSINESS_UNIT SBU
           WHERE S.SITE_ID=SBU.SITE_ID AND S.COMPANY_ID=SBU.COMPANY_ID 
             AND SBU.COMPANY_ID=1 AND SBU.BUSINESS_UNIT_ID=FP_BU_ID)        
       AND VM.COMPANY_ID=FP_COMPANY_ID AND VM.CANCELLED_BY IS NULL AND VM.POSTED_IND='Y' 
       AND VM.FIN_YEAR_ID<=FP_FINYEAR_ID AND VM.VOUCHER_DTE <= TO_DATE(FP_END_DTE,'DD-MM-YYYY')
       AND VD.COA_CDE IN (SELECT COA.COA_CDE FROM CHART_OF_ACCOUNT COA WHERE COA.ENTRY_LEVEL_IND='Y'
                            START WITH COA.COA_CDE=CP_COA_CDE CONNECT BY PRIOR COA.COA_CDE=COA.PARENT_CDE)
       GROUP BY VD.COA_CDE ORDER BY VD.COA_CDE;
       
BEGIN  
  FOR FL_MA IN CS_BS_ACCOUNTS LOOP    
    IF (FL_MA.COA_CDE IS NULL OR FL_MA.MASTER_SOURCE_TYP='FORMULA') THEN
      PIPE ROW (OBJECT_BS_ACCOUNTS(FL_MA.SEQ_NBR, FL_MA.ROW_NBR, FL_MA.TITLE, FL_MA.DISPLAY_TXT, NVL(FL_MA.SUB_TITLE,FL_MA.TITLE), NULL, 0, FL_MA.MANAGEMENT_ACCOUNTS_MASTER_ID));
    ELSE 
      FOR FL_DATA IN CS_DATA(FL_MA.COA_CDE,FL_MA.DETAIL_SOURCE_TYP) LOOP   
        IF (FL_MA.MAP_SOURCE_TYP IS NOT NULL AND FL_MA.MAP_SOURCE_TYP='DR' AND FL_DATA.AMNT>0) THEN
          PIPE ROW (OBJECT_BS_ACCOUNTS(FL_MA.MAP_SEQ_NBR, FL_MA.MAP_ROW_NBR, FL_MA.MAP_TITLE, FL_MA.MAP_DISPLAY_TXT, FL_MA.MAP_SUB_TITLE, FL_DATA.COA_CDE, FL_DATA.AMNT, FL_MA.MANAGEMENT_ACCOUNTS_MASTER_ID));        
        ELSIF (FL_MA.MAP_SOURCE_TYP IS NOT NULL AND FL_MA.MAP_SOURCE_TYP='CR' AND FL_DATA.AMNT<0) THEN
          PIPE ROW (OBJECT_BS_ACCOUNTS(FL_MA.MAP_SEQ_NBR, FL_MA.MAP_ROW_NBR, FL_MA.MAP_TITLE, FL_MA.MAP_DISPLAY_TXT, FL_MA.MAP_SUB_TITLE, FL_DATA.COA_CDE, FL_DATA.AMNT, FL_MA.MANAGEMENT_ACCOUNTS_MASTER_ID));        
        ELSE
          PIPE ROW (OBJECT_BS_ACCOUNTS(FL_MA.SEQ_NBR, FL_MA.ROW_NBR, FL_MA.TITLE, FL_MA.DISPLAY_TXT, FL_MA.SUB_TITLE, FL_DATA.COA_CDE, FL_DATA.AMNT, FL_MA.MANAGEMENT_ACCOUNTS_MASTER_ID));        
        END IF;          
      END LOOP;          
    END IF;  
  END LOOP;   
END;
/