package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 *  人保赊销金额回款Vo
 *
 */
public class PiccReceiveAmountVo {
	private String POLICYNO;						//保险单号
	private String INSUREDPICCCODE;					//被保险人PICCCODE
	private String BUYCOMPNAME;						//买方名称
	private String BUYCOMPADDRESS;					//买方地址
	private String RISKPICCCODE;					//风险方PICCCODE
	private String RISKNAME;						//风险方名称
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date RECOVERDATE;						//回款日期
	private String PROJECTCODE;						//项目编号
	private BigDecimal XAMRECOVERAMOUNT;			//确认收汇金额
	public String getPOLICYNO() {
		return POLICYNO;
	}
	public void setPOLICYNO(String pOLICYNO) {
		POLICYNO = pOLICYNO;
	}
	public String getINSUREDPICCCODE() {
		return INSUREDPICCCODE;
	}
	public void setINSUREDPICCCODE(String iNSUREDPICCCODE) {
		INSUREDPICCCODE = iNSUREDPICCCODE;
	}
	public String getBUYCOMPNAME() {
		return BUYCOMPNAME;
	}
	public void setBUYCOMPNAME(String bUYCOMPNAME) {
		BUYCOMPNAME = bUYCOMPNAME;
	}
	public String getBUYCOMPADDRESS() {
		return BUYCOMPADDRESS;
	}
	public void setBUYCOMPADDRESS(String bUYCOMPADDRESS) {
		BUYCOMPADDRESS = bUYCOMPADDRESS;
	}
	public String getRISKPICCCODE() {
		return RISKPICCCODE;
	}
	public void setRISKPICCCODE(String rISKPICCCODE) {
		RISKPICCCODE = rISKPICCCODE;
	}
	public String getRISKNAME() {
		return RISKNAME;
	}
	public void setRISKNAME(String rISKNAME) {
		RISKNAME = rISKNAME;
	}
	public Date getRECOVERDATE() {
		return RECOVERDATE;
	}
	public void setRECOVERDATE(Date rECOVERDATE) {
		RECOVERDATE = rECOVERDATE;
	}
	public String getPROJECTCODE() {
		return PROJECTCODE;
	}
	public void setPROJECTCODE(String pROJECTCODE) {
		PROJECTCODE = pROJECTCODE;
	}
	public BigDecimal getXAMRECOVERAMOUNT() {
		return XAMRECOVERAMOUNT;
	}
	public void setXAMRECOVERAMOUNT(BigDecimal xAMRECOVERAMOUNT) {
		XAMRECOVERAMOUNT = xAMRECOVERAMOUNT;
	} 
	
	
}
