package com.spt.bas.client.vo;

public class ApplyImportQueryVo {
	
	
	private Long applyImportId;  //进口代理业务id
	private String contractType;

	public Long getApplyImportId() {
		return applyImportId;
	}

	public void setApplyImportId(Long applyImportId) {
		this.applyImportId = applyImportId;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	
	public ApplyImportQueryVo(Long applyImportId,String contractType) {
		super();
		this.applyImportId = applyImportId;
		this.contractType = contractType;
	}

	public ApplyImportQueryVo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	

}
