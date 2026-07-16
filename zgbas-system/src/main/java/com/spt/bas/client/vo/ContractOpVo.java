/**
 * 
 */
package com.spt.bas.client.vo;

/**
 * @author wlddh
 *
 */
public class ContractOpVo {
	private Long id;
	// 采购：N-新增，S-已签约，F1-已付款，G1-已收货，V1-已收票
	// 销售：N-新增，S-已签约，F2-已收款，G2-已发货，V2-已开票
	private String contractStatus = "N";// 合同状态

	private Boolean fondFlg = false;// 已收款
	private Boolean payFlg = false;// 已发货/已收货
	private Boolean billFlg = false;// 已收票

	private String createUserName;
	private Long createUserId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}

	public Boolean getFondFlg() {
		return fondFlg;
	}

	public void setFondFlg(Boolean fondFlg) {
		this.fondFlg = fondFlg;
	}

	public Boolean getPayFlg() {
		return payFlg;
	}

	public void setPayFlg(Boolean payFlg) {
		this.payFlg = payFlg;
	}

	public Boolean getBillFlg() {
		return billFlg;
	}

	public void setBillFlg(Boolean billFlg) {
		this.billFlg = billFlg;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
}
