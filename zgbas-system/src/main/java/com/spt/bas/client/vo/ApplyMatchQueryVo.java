package com.spt.bas.client.vo;

public class ApplyMatchQueryVo {


	private Long applyMatchId;  //撮合业务id
	private String contractType;
	private String contractNo;
	public Long getApplyMatchId() {
		return applyMatchId;
	}

	public void setApplyMatchId(Long applyMatchId) {
		this.applyMatchId = applyMatchId;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public ApplyMatchQueryVo(Long applyMatchId, String contractType) {
		super();
		this.applyMatchId = applyMatchId;
		this.contractType = contractType;
		this.contractNo = contractNo;
	}

	public ApplyMatchQueryVo() {
		super();
		// TODO Auto-generated constructor stub
	}







}
