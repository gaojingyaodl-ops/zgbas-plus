package com.spt.bas.client.vo;

public class SealUsageDcsxVo{
	
	private String companyName;
	
	private String ourCompanyName;

	private Long approveId;

	private Long contractId;
	
	private String contractNo;
	
	private String fileId;

	private Boolean virtualFlg = false;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Boolean getVirtualFlg() {
		return virtualFlg;
	}

	public void setVirtualFlg(Boolean virtualFlg) {
		this.virtualFlg = virtualFlg;
	}
}
