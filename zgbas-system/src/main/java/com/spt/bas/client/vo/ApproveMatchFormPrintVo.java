package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.List;

public class ApproveMatchFormPrintVo {
	
	private String content;
	
	private List<ApplyProductDetailVo>buyList; 
	
	private List<ApplyProductDetailVo>sellList; 
	
	private String contractAttr;//合同属性
	
	private String buyTimeStr; //付款时间
	
	private String sellTimeStr;//收款时间
	
	private BigDecimal buyBondAmount;//上家定金
	
	private BigDecimal sellBondAmount;//下家定金
	
	private String deliveryType;//配送方式
	
	private String shippingAddr; //配送地址
	
	private String warehouseName; //仓库
	
	private String remark; //备注
	
	private String arrivalTimeStr;//到货时间
	
	private	BigDecimal	transportAmount;//运输费
	
	private String matchUserName;

	private String  buyCompanyName;
	
	private String  sellCompanyName;
	
    private String  buyContractNo;
	
	private String  sellContractNo;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	public List<ApplyProductDetailVo> getBuyList() {
		return buyList;
	}

	public void setBuyList(List<ApplyProductDetailVo> buyList) {
		this.buyList = buyList;
	}

	public List<ApplyProductDetailVo> getSellList() {
		return sellList;
	}

	public void setSellList(List<ApplyProductDetailVo> sellList) {
		this.sellList = sellList;
	}

	public String getContractAttr() {
		return contractAttr;
	}

	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}

	public String getBuyTimeStr() {
		return buyTimeStr;
	}

	public void setBuyTimeStr(String buyTimeStr) {
		this.buyTimeStr = buyTimeStr;
	}

	public String getSellTimeStr() {
		return sellTimeStr;
	}

	public void setSellTimeStr(String sellTimeStr) {
		this.sellTimeStr = sellTimeStr;
	}

	public BigDecimal getBuyBondAmount() {
		return buyBondAmount;
	}

	public void setBuyBondAmount(BigDecimal buyBondAmount) {
		this.buyBondAmount = buyBondAmount;
	}

	public BigDecimal getSellBondAmount() {
		return sellBondAmount;
	}

	public void setSellBondAmount(BigDecimal sellBondAmount) {
		this.sellBondAmount = sellBondAmount;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getShippingAddr() {
		return shippingAddr;
	}

	public void setShippingAddr(String shippingAddr) {
		this.shippingAddr = shippingAddr;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getArrivalTimeStr() {
		return arrivalTimeStr;
	}

	public void setArrivalTimeStr(String arrivalTimeStr) {
		this.arrivalTimeStr = arrivalTimeStr;
	}

	public BigDecimal getTransportAmount() {
		return transportAmount;
	}

	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}

	public String getMatchUserName() {
		return matchUserName;
	}

	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	public String getBuyCompanyName() {
		return buyCompanyName;
	}

	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}

	public String getSellCompanyName() {
		return sellCompanyName;
	}

	public void setSellCompanyName(String sellCompanyName) {
		this.sellCompanyName = sellCompanyName;
	}

	public String getBuyContractNo() {
		return buyContractNo;
	}

	public void setBuyContractNo(String buyContractNo) {
		this.buyContractNo = buyContractNo;
	}

	public String getSellContractNo() {
		return sellContractNo;
	}

	public void setSellContractNo(String sellContractNo) {
		this.sellContractNo = sellContractNo;
	}
	
	
	
	
	
}
