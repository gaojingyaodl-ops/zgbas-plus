package com.spt.bas.client.vo;

public class StockDetailLinkVo {

	private String linkContractId;
	private Long stockDetailId;
	private Long stockContractId;//合同库存id
	private Long applyId;
	public String getLinkContractId() {
		return linkContractId;
	}
	public void setLinkContractId(String linkContractId) {
		this.linkContractId = linkContractId;
	}
	public Long getStockDetailId() {
		return stockDetailId;
	}
	public void setStockDetailId(Long detailId) {
		this.stockDetailId = detailId;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public Long getStockContractId() {
		return stockContractId;
	}
	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}
}
