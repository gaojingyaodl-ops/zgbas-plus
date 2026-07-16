package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 *  人保内贸赊销申请Vo
 *
 */
public class PiccDomesticTradeVo {
	private String POLICYNO;						//保险单号
	private String INSUREDNAME;						//被保险人名称
	private String INSUREDPICCCODE;					//被保险人PICCCODE	
	private String LIMITFLAG;						//是否为自行掌握限额赊销记录 0,1
	private String RISKNAME;						//买方名称
	private String RISKCOMPADDRESS;					//买方地址
	private String RISKPHONE;						//买方联系电话
	private String RISKMARK;						//买方注册号
	private String PRODUCTCATEGORY;					//商品类别
	private String PRODUCT;							//商品名称
	private String PRODUCTNUM;						//商品数量及单位
	private String CONTRACTNO;						//发票号
	private String PAYMENT;							//费率计算标准
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date HAPPENDATE;						//出货日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date STARTDATE;							//信用期限起始日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date ACCRUALDATE;						//应付款日
	private BigDecimal INVOICEAMOUT;				//赊销金额
	private BigDecimal RECOVERAMOUNT;				//回收金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")				
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date RECOVERDATE;						//回款日期
	private Long  PAYMENTTERMS;						//信用期限（天）
	private String SERVLERTNAME;					//系统名称
	private String REMARK;							//备注
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date HAPPEN2DATE;						//发票日期
	private String EXPORTTRADE;						//商品类别
	private String EXPORTTRADEINPUT;				//其他商品类别
	public String getPOLICYNO() {
		return POLICYNO;
	}
	public void setPOLICYNO(String pOLICYNO) {
		POLICYNO = pOLICYNO;
	}
	public String getINSUREDNAME() {
		return INSUREDNAME;
	}
	public void setINSUREDNAME(String iNSUREDNAME) {
		INSUREDNAME = iNSUREDNAME;
	}
	public String getINSUREDPICCCODE() {
		return INSUREDPICCCODE;
	}
	public void setINSUREDPICCCODE(String iNSUREDPICCCODE) {
		INSUREDPICCCODE = iNSUREDPICCCODE;
	}
	public String getLIMITFLAG() {
		return LIMITFLAG;
	}
	public void setLIMITFLAG(String lIMITFLAG) {
		LIMITFLAG = lIMITFLAG;
	}
	public String getRISKNAME() {
		return RISKNAME;
	}
	public void setRISKNAME(String rISKNAME) {
		RISKNAME = rISKNAME;
	}
	public String getRISKCOMPADDRESS() {
		return RISKCOMPADDRESS;
	}
	public void setRISKCOMPADDRESS(String rISKCOMPADDRESS) {
		RISKCOMPADDRESS = rISKCOMPADDRESS;
	}
	public String getRISKPHONE() {
		return RISKPHONE;
	}
	public void setRISKPHONE(String rISKPHONE) {
		RISKPHONE = rISKPHONE;
	}
	public String getRISKMARK() {
		return RISKMARK;
	}
	public void setRISKMARK(String rISKMARK) {
		RISKMARK = rISKMARK;
	}
	public String getPRODUCTCATEGORY() {
		return PRODUCTCATEGORY;
	}
	public void setPRODUCTCATEGORY(String pRODUCTCATEGORY) {
		PRODUCTCATEGORY = pRODUCTCATEGORY;
	}
	public String getPRODUCT() {
		return PRODUCT;
	}
	public void setPRODUCT(String pRODUCT) {
		PRODUCT = pRODUCT;
	}
	public String getPRODUCTNUM() {
		return PRODUCTNUM;
	}
	public void setPRODUCTNUM(String pRODUCTNUM) {
		PRODUCTNUM = pRODUCTNUM;
	}
	public String getCONTRACTNO() {
		return CONTRACTNO;
	}
	public void setCONTRACTNO(String cONTRACTNO) {
		CONTRACTNO = cONTRACTNO;
	}
	public String getPAYMENT() {
		return PAYMENT;
	}
	public void setPAYMENT(String pAYMENT) {
		PAYMENT = pAYMENT;
	}
	public Date getHAPPENDATE() {
		return HAPPENDATE;
	}
	public void setHAPPENDATE(Date hAPPENDATE) {
		HAPPENDATE = hAPPENDATE;
	}
	public Date getSTARTDATE() {
		return STARTDATE;
	}
	public void setSTARTDATE(Date sTARTDATE) {
		STARTDATE = sTARTDATE;
	}
	public Date getACCRUALDATE() {
		return ACCRUALDATE;
	}
	public void setACCRUALDATE(Date aCCRUALDATE) {
		ACCRUALDATE = aCCRUALDATE;
	}
	public BigDecimal getINVOICEAMOUT() {
		return INVOICEAMOUT;
	}
	public void setINVOICEAMOUT(BigDecimal iNVOICEAMOUT) {
		INVOICEAMOUT = iNVOICEAMOUT;
	}
	public BigDecimal getRECOVERAMOUNT() {
		return RECOVERAMOUNT;
	}
	public void setRECOVERAMOUNT(BigDecimal rECOVERAMOUNT) {
		RECOVERAMOUNT = rECOVERAMOUNT;
	}
	public Date getRECOVERDATE() {
		return RECOVERDATE;
	}
	public void setRECOVERDATE(Date rECOVERDATE) {
		RECOVERDATE = rECOVERDATE;
	}
	public Long getPAYMENTTERMS() {
		return PAYMENTTERMS;
	}
	public void setPAYMENTTERMS(Long pAYMENTTERMS) {
		PAYMENTTERMS = pAYMENTTERMS;
	}
	public String getSERVLERTNAME() {
		return SERVLERTNAME;
	}
	public void setSERVLERTNAME(String sERVLERTNAME) {
		SERVLERTNAME = sERVLERTNAME;
	}
	public String getREMARK() {
		return REMARK;
	}
	public void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}
	public Date getHAPPEN2DATE() {
		return HAPPEN2DATE;
	}
	public void setHAPPEN2DATE(Date hAPPEN2DATE) {
		HAPPEN2DATE = hAPPEN2DATE;
	}
	public String getEXPORTTRADE() {
		return EXPORTTRADE;
	}
	public void setEXPORTTRADE(String eXPORTTRADE) {
		EXPORTTRADE = eXPORTTRADE;
	}
	public String getEXPORTTRADEINPUT() {
		return EXPORTTRADEINPUT;
	}
	public void setEXPORTTRADEINPUT(String eXPORTTRADEINPUT) {
		EXPORTTRADEINPUT = eXPORTTRADEINPUT;
	}
	
	
}
