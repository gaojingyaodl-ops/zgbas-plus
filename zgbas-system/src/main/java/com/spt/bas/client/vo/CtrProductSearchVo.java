package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class CtrProductSearchVo extends PageSearchVo{
	private Long ctrContractId;
	private Long enterpriseId;
	public Long getCtrContractId() {
		return ctrContractId;
	}
	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
}
