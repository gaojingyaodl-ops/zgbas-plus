package com.spt.bas.report.client.vo;



import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;

public class RptSellOnCreditSearchVo extends PageSearchVo{
	
	private String contractNo; //合同编号
	private String ourCompanyName;//我方企业名称
	private String companyName;// 对方企业
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date confirmDateGTED;// 大于收货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date confirmDateLTD;// 小于收货时间
	
	
	private String businessType; //业务状态
	private String piccPushFlg;//picc人保推送状态（投保成功、回款结束、投保失败）（目前就0和1,1表示成功）
	private Long enterpriseId;

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getPiccPushFlg() {
		return piccPushFlg;
	}

	public void setPiccPushFlg(String piccPushFlg) {
		this.piccPushFlg = piccPushFlg;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Date getConfirmDateGTED() {
		return confirmDateGTED;
	}

	public void setConfirmDateGTED(Date confirmDateGTED) {
		this.confirmDateGTED = confirmDateGTED;
	}

	public Date getConfirmDateLTD() {
		return confirmDateLTD;
	}

	public void setConfirmDateLTD(Date confirmDateLTD) {
		this.confirmDateLTD = confirmDateLTD;
	}
	
	
	
}
