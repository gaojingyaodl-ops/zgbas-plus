package com.spt.bas.client.entity;

public class CtrContractExtants extends CtrContract{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1479223103051011691L;
	
	private StringBuilder products;//货品
	
	private Double freight;//运费
	
	private Double difference;//差额
	
	private Double grossprofit;//毛利
	
	private Double rewardAmount;//奖励金额
	
	private Double salesmanAward;//业务员奖励
	
	private Boolean canStartBuyFlg = false;

	public StringBuilder getProducts() {
		return products;
	}

	public void setProducts(StringBuilder str) {
		this.products = str;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	public Double getDifference() {
		return difference;
	}

	public void setDifference(Double difference) {
		this.difference = difference;
	}

	public Double getGrossprofit() {
		return grossprofit;
	}

	public void setGrossprofit(Double grossprofit) {
		this.grossprofit = grossprofit;
	}

	public Double getRewardAmount() {
		return rewardAmount;
	}

	public void setRewardAmount(Double rewardAmount) {
		this.rewardAmount = rewardAmount;
	}

	public Double getSalesmanAward() {
		return salesmanAward;
	}

	public void setSalesmanAward(Double salesmanAward) {
		this.salesmanAward = salesmanAward;
	}

	public Boolean getCanStartBuyFlg() {
		return canStartBuyFlg;
	}

	public void setCanStartBuyFlg(Boolean canStartBuyFlg) {
		this.canStartBuyFlg = canStartBuyFlg;
	}

	
	

	
	
	
	

}
