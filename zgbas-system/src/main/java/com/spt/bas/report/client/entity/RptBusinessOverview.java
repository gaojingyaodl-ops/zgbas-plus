package com.spt.bas.report.client.entity;

/**
 * 业务总览
 *
 */
public class RptBusinessOverview {

	/**
	 * 状态
	 */
	private String status;
	/**
	 * 采购
	 */
	private String buyNum;
	/**
	 * 中游
	 */
	private String dcsxNum;
	/**
	 * 销售
	 */
	private String sellNum;
	/**
	 * 逻辑说明
	 */
	private String logicalDeclaration;

	/**
	 * 统计类型
	 */
	private String statisticalType;


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(String buyNum) {
		this.buyNum = buyNum;
	}

	public String getDcsxNum() {
		return dcsxNum;
	}

	public void setDcsxNum(String dcsxNum) {
		this.dcsxNum = dcsxNum;
	}

	public String getSellNum() {
		return sellNum;
	}

	public void setSellNum(String sellNum) {
		this.sellNum = sellNum;
	}

	public String getLogicalDeclaration() {
		return logicalDeclaration;
	}

	public void setLogicalDeclaration(String logicalDeclaration) {
		this.logicalDeclaration = logicalDeclaration;
	}

	public String getStatisticalType() {
		return statisticalType;
	}

	public void setStatisticalType(String statisticalType) {
		this.statisticalType = statisticalType;
	}
}
