package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.List;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;

public class ApproveFormPrintVo {

	private String content;

	private List<ApplyProductDetail> detailList;

	private List<CtrProduct> buyDetailList; // 商品对应的采购信息
	
	private List<ApplyProductDetailVo> newList;
	
	private String contractAttr;// 合同属性

	private String payTimeStr;

	private BigDecimal bondAmount;

	private String deliveryType;// 提货方式

	private String warehouseName;

	private String remark;

	private String arrivalTimeStr;

	private String deliveryTimeStr; // 交货时间

	private BigDecimal transportAmount;
	
	private BigDecimal goodsTotalNum;

	private String companyName;

	private String contractNo;

	private String matchUserName;

	private String buyMatchUserName; // 上家业务员
	
	private List<CtrContract> contractList; // 销售商品对应的采购合同
	
	private String ourCompanyName;
	
	private String strDate;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<ApplyProductDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ApplyProductDetail> detailList) {
		this.detailList = detailList;
	}

	public String getPayTimeStr() {
		return payTimeStr;
	}

	public void setPayTimeStr(String payTimeStr) {
		this.payTimeStr = payTimeStr;
	}

	public BigDecimal getBondAmount() {
		return bondAmount;
	}

	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
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

	public String getContractAttr() {
		return contractAttr;
	}

	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getMatchUserName() {
		return matchUserName;
	}

	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	public String getDeliveryTimeStr() {
		return deliveryTimeStr;
	}

	public void setDeliveryTimeStr(String deliveryTimeStr) {
		this.deliveryTimeStr = deliveryTimeStr;
	}

	public List<CtrProduct> getBuyDetailList() {
		return buyDetailList;
	}

	public void setBuyDetailList(List<CtrProduct> buyDetailList) {
		this.buyDetailList = buyDetailList;
	}

	public String getBuyMatchUserName() {
		return buyMatchUserName;
	}

	public void setBuyMatchUserName(String buyMatchUserName) {
		this.buyMatchUserName = buyMatchUserName;
	}
	
	public List<CtrContract> getContractList() {
		return contractList;
	}

	public void setContractList(List<CtrContract> contractList) {
		this.contractList = contractList;
	}

	public List<ApplyProductDetailVo> getNewList() {
		return newList;
	}

	public void setNewList(List<ApplyProductDetailVo> newList) {
		this.newList = newList;
	}

	public BigDecimal getGoodsTotalNum() {
		return goodsTotalNum;
	}

	public void setGoodsTotalNum(BigDecimal goodsTotalNum) {
		this.goodsTotalNum = goodsTotalNum;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	
	
}
