package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.ApplySell;

import java.math.BigDecimal;
import java.util.List;

public class ApplySellVo extends ApplySell{
	/**
	 *
	 */
	private static final long serialVersionUID = 9012836519693760279L;
	private String 	deptAbbr;		//部门简码
	private	String	productName;	//货名
	private	String	productCd;		//货名Cd
	private	String	brandNumber;	//牌号
	private	Long	factoryId;		//厂商ID
	private	String	factoryName;	//厂商名称
	private	BigDecimal	dealNumber;	//入出库数量
	private	BigDecimal	dealPrice;	//单价
	private	BigDecimal	totalPrice;	//总价
	private	Long	enterpriseId;	//企业ID
	private	String	applyType;		//申请类型
	private	Long	applyId;		//申请单ID
	private Long bizId;
	private Long applyUserId;//当前登录人的id
	private List<ApplyProductDetailVo>productJSON;//撮合基本信息

	private List<ApplyProductDetail> lstInsert;
	private List<ApplyProductDetail> lstUpdate;
	private List<ApplyProductDetail> lstDelete;


	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}

	public Long getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(Long applyUserId) {
		this.applyUserId = applyUserId;
	}

	public List<ApplyProductDetailVo> getProductJSON() {
		return productJSON;
	}

	public void setProductJSON(List<ApplyProductDetailVo> productJSON) {
		this.productJSON = productJSON;
	}

	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductCd() {
		return productCd;
	}
	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public Long getFactoryId() {
		return factoryId;
	}
	public void setFactoryId(Long factoryId) {
		this.factoryId = factoryId;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public List<ApplyProductDetail> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<ApplyProductDetail> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<ApplyProductDetail> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<ApplyProductDetail> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<ApplyProductDetail> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<ApplyProductDetail> lstDelete) {
		this.lstDelete = lstDelete;
	}
	@Override
	public Class<?> getSubClass() {
		return ApplyProductDetail.class;
	}

	@Override
    @SuppressWarnings("unchecked")
	public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
		setLstInsert((List<ApplyProductDetail>)lstInsert);
		setLstUpdate((List<ApplyProductDetail>)lstUpdate);
		setLstDelete((List<ApplyProductDetail>)lstDelete);
	}
	public String getDeptAbbr() {
		return deptAbbr;
	}
	public void setDeptAbbr(String deptAbbr) {
		this.deptAbbr = deptAbbr;
	}


}
