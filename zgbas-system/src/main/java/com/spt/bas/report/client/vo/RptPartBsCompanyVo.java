package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

public class RptPartBsCompanyVo {


	private  String supplierRating;

	public String getSupplierRating() {
		return supplierRating;
	}

	public void setSupplierRating(String supplierRating) {
		this.supplierRating = supplierRating;
	}

	private Long id;

	public String getCreditRating() {
		return creditRating;
	}

	public void setCreditRating(String creditRating) {
		this.creditRating = creditRating;
	}

	private String creditRating;
	private String companyName;
	private String status;
	private Long matchUserId;
	private Long currentUserId;
	private Long sharedUserId;
	private Boolean myFlag;
	private Long enterpriseId;
	private String text;
	private String companyPhone;	// 公司电话
	private String contactPhone; 	// 联系电话
	private String contactName; 	// 联系人
	private String address; 		// 公司地址
	private String companyType;		// 客户类型
	private String bankName;
	private String bankAccount;
	private String taxNo;
	private Boolean onLineFlg;		// 线上化标识
	private BigDecimal totalCreditAmount;
	private BigDecimal usedCreditAmount;
	private BigDecimal remainCreditAmount;
	private Long creditAmountDays;
	private String searchType;		//搜索类型 SX：查询授信额度大于0的企业
	private BigDecimal rate = BigDecimal.ZERO; 			//销售合同服务费的费率
	private BigDecimal interestRate = BigDecimal.ZERO; 	//逾期罚金的费率
	private String rateType;							// 服务合同类型（服务费先收B，或服务费后收A）
	private String creditCycleType;						// 固定赊销S，或浮动赊销D
	private int creditCycle;    						//赊销天数，固定赊销类型的用户记录赊销周期
	/**
	 * vip等级
	 */
	private Integer vipLevel;
	/**
	 *终端工厂自提审批
	 */
	private String creditDelivery;

	/**
	 * 预付款额度（元）
	 */
	private BigDecimal supplierPrepayAmount;

	/**
	 * 已使用的预付款额度（元）
	 */
	private BigDecimal usedSupplierPrepayAmount;

	/**
	 * 是否可以做期货合同
	 * 0:否 1:是
	 */
	private String supplierFuture;

	/**
	 * 是否上游配送
	 * 0：否 1：是
	 */
	private String supplierDelivery;

	/**
	 * 人保风控额度
	 */
	private BigDecimal piccRiskAmount = BigDecimal.ZERO;

	/**
	 * 大地风控额度
	 */
	private BigDecimal daDiRiskAmount = BigDecimal.ZERO;
	
	/**
	 * 中银风控额度
	 */
	private BigDecimal zhongYinRiskAmount = BigDecimal.ZERO;

	/**
	 * 自主风控额度
	 */
	private BigDecimal ourRiskAmount = BigDecimal.ZERO;

	/**
	 * 人保临时额度
	 */
	private BigDecimal piccTemporaryAmount = BigDecimal.ZERO;

	/**
	 * 大地临时额度
	 */
	private BigDecimal daDiTemporaryAmount = BigDecimal.ZERO;
	
	/**
	 * 中银临时额度
	 */
	private BigDecimal zhongYinTemporaryAmount = BigDecimal.ZERO;

	/**
	 * 自主临时额度
	 */
	private BigDecimal ourTemporaryAmount = BigDecimal.ZERO;
	
	/**
	 * 人保可用额度
	 */
	private BigDecimal piccAmount = BigDecimal.ZERO;

	/**
	 * 大地可用额度
	 */
	private BigDecimal daDiAmount = BigDecimal.ZERO;

	/**
	 * 中银可用额度
	 */
	private BigDecimal zyAmount = BigDecimal.ZERO;

	/**
	 * 自主可用额度
	 */
	private BigDecimal ourAmount = BigDecimal.ZERO;

	public String getCreditDelivery() {
		return creditDelivery;
	}



	/**
	 *采购额度
	 */
	private BigDecimal supplierPurchaseAmount;

	/**
	 *已使用的采购额度
	 */
	private BigDecimal usedSupplierPurchaseAmount;
	/**
	 * 客户分类 等级为 A类客户 B类客户 C类客户 D类客户
	 */
	private String companyGrade;
	/**
	 * 供应商分类等级
	 */
	private String supplierGrade;

	/**
	 * 访厂报告是否通过
	 */
	private Boolean accessReportFlg;

	/**
	 * 实控人担保
	 */
	private Boolean actualGuaranteeFlg;

	/**
	 * 人保批复额度
	 */
	private BigDecimal piccCreditAmount;

	/**
	 * 大地批复额度
	 */
	private BigDecimal daDiCreditAmount;

	public String getSupplierGrade() {
		return supplierGrade;
	}

	public void setSupplierGrade(String supplierGrade) {
		this.supplierGrade = supplierGrade;
	}

	public String getCompanyGrade() {
		return companyGrade;
	}

	public void setCompanyGrade(String companyGrade) {
		this.companyGrade = companyGrade;
	}

	public BigDecimal getSupplierPurchaseAmount() {
		return supplierPurchaseAmount;
	}

	public void setSupplierPurchaseAmount(BigDecimal supplierPurchaseAmount) {
		this.supplierPurchaseAmount = supplierPurchaseAmount;
	}

	public BigDecimal getUsedSupplierPurchaseAmount() {
		return usedSupplierPurchaseAmount;
	}

	public void setUsedSupplierPurchaseAmount(BigDecimal usedSupplierPurchaseAmount) {
		this.usedSupplierPurchaseAmount = usedSupplierPurchaseAmount;
	}

	public void setCreditDelivery(String creditDelivery) {
		this.creditDelivery = creditDelivery;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public Long getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Long currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Boolean getMyFlag() {
		return myFlag;
	}
	public void setMyFlag(Boolean myFlag) {
		this.myFlag = myFlag;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getSharedUserId() {
		return sharedUserId;
	}
	public void setSharedUserId(Long sharedUserId) {
		this.sharedUserId = sharedUserId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getTaxNo() {
		return taxNo;
	}
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}
	public Boolean getOnLineFlg() {
		return onLineFlg;
	}
	public void setOnLineFlg(Boolean onLineFlg) {
		this.onLineFlg = onLineFlg;
	}
	public BigDecimal getTotalCreditAmount() {
		return totalCreditAmount;
	}
	public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
		this.totalCreditAmount = totalCreditAmount;
	}
	public BigDecimal getUsedCreditAmount() {
		return usedCreditAmount;
	}
	public void setUsedCreditAmount(BigDecimal usedCreditAmount) {
		this.usedCreditAmount = usedCreditAmount;
	}
	public BigDecimal getRemainCreditAmount() {
		return remainCreditAmount;
	}
	public void setRemainCreditAmount(BigDecimal remainCreditAmount) {
		this.remainCreditAmount = remainCreditAmount;
	}
	public Long getCreditAmountDays() {
		return creditAmountDays;
	}
	public void setCreditAmountDays(Long creditAmountDays) {
		this.creditAmountDays = creditAmountDays;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getCreditCycleType() {
		return creditCycleType;
	}

	public void setCreditCycleType(String creditCycleType) {
		this.creditCycleType = creditCycleType;
	}

	public int getCreditCycle() {
		return creditCycle;
	}

	public void setCreditCycle(int creditCycle) {
		this.creditCycle = creditCycle;
	}

	public Integer getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(Integer vipLevel) {
		this.vipLevel = vipLevel;
	}

	public BigDecimal getSupplierPrepayAmount() {
		return supplierPrepayAmount;
	}

	public void setSupplierPrepayAmount(BigDecimal supplierPrepayAmount) {
		this.supplierPrepayAmount = supplierPrepayAmount;
	}

	public BigDecimal getUsedSupplierPrepayAmount() {
		return usedSupplierPrepayAmount;
	}

	public void setUsedSupplierPrepayAmount(BigDecimal usedSupplierPrepayAmount) {
		this.usedSupplierPrepayAmount = usedSupplierPrepayAmount;
	}

	public String getSupplierFuture() {
		return supplierFuture;
	}

	public void setSupplierFuture(String supplierFuture) {
		this.supplierFuture = supplierFuture;
	}

	public String getSupplierDelivery() {
		return supplierDelivery;
	}

	public void setSupplierDelivery(String supplierDelivery) {
		this.supplierDelivery = supplierDelivery;
	}

	public Boolean getAccessReportFlg() {
		return accessReportFlg;
	}

	public void setAccessReportFlg(Boolean accessReportFlg) {
		this.accessReportFlg = accessReportFlg;
	}

	public Boolean getActualGuaranteeFlg() {
		return actualGuaranteeFlg;
	}

	public void setActualGuaranteeFlg(Boolean actualGuaranteeFlg) {
		this.actualGuaranteeFlg = actualGuaranteeFlg;
	}

	public BigDecimal getPiccCreditAmount() {
		return piccCreditAmount;
	}

	public void setPiccCreditAmount(BigDecimal piccCreditAmount) {
		this.piccCreditAmount = piccCreditAmount;
	}

	public BigDecimal getDaDiCreditAmount() {
		return daDiCreditAmount;
	}

	public void setDaDiCreditAmount(BigDecimal daDiCreditAmount) {
		this.daDiCreditAmount = daDiCreditAmount;
	}

	public BigDecimal getPiccAmount() {
		return piccAmount;
	}

	public void setPiccAmount(BigDecimal piccAmount) {
		this.piccAmount = piccAmount;
	}

	public BigDecimal getDaDiAmount() {
		return daDiAmount;
	}

	public void setDaDiAmount(BigDecimal daDiAmount) {
		this.daDiAmount = daDiAmount;
	}

	public BigDecimal getOurAmount() {
		return ourAmount;
	}

	public void setOurAmount(BigDecimal ourAmount) {
		this.ourAmount = ourAmount;
	}

	public BigDecimal getPiccRiskAmount() {
		return piccRiskAmount;
	}

	public void setPiccRiskAmount(BigDecimal piccRiskAmount) {
		this.piccRiskAmount = piccRiskAmount;
	}

	public BigDecimal getDaDiRiskAmount() {
		return daDiRiskAmount;
	}

	public void setDaDiRiskAmount(BigDecimal daDiRiskAmount) {
		this.daDiRiskAmount = daDiRiskAmount;
	}

	public BigDecimal getOurRiskAmount() {
		return ourRiskAmount;
	}

	public void setOurRiskAmount(BigDecimal ourRiskAmount) {
		this.ourRiskAmount = ourRiskAmount;
	}

	public BigDecimal getPiccTemporaryAmount() {
		return piccTemporaryAmount;
	}

	public void setPiccTemporaryAmount(BigDecimal piccTemporaryAmount) {
		this.piccTemporaryAmount = piccTemporaryAmount;
	}

	public BigDecimal getDaDiTemporaryAmount() {
		return daDiTemporaryAmount;
	}

	public void setDaDiTemporaryAmount(BigDecimal daDiTemporaryAmount) {
		this.daDiTemporaryAmount = daDiTemporaryAmount;
	}

	public BigDecimal getOurTemporaryAmount() {
		return ourTemporaryAmount;
	}

	public void setOurTemporaryAmount(BigDecimal ourTemporaryAmount) {
		this.ourTemporaryAmount = ourTemporaryAmount;
	}

	public BigDecimal getZyAmount() {
		return zyAmount;
	}

	public void setZyAmount(BigDecimal zyAmount) {
		this.zyAmount = zyAmount;
	}

	public BigDecimal getZhongYinRiskAmount() {
		return zhongYinRiskAmount;
	}

	public void setZhongYinRiskAmount(BigDecimal zhongYinRiskAmount) {
		this.zhongYinRiskAmount = zhongYinRiskAmount;
	}

	public BigDecimal getZhongYinTemporaryAmount() {
		return zhongYinTemporaryAmount;
	}

	public void setZhongYinTemporaryAmount(BigDecimal zhongYinTemporaryAmount) {
		this.zhongYinTemporaryAmount = zhongYinTemporaryAmount;
	}

}

