package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class ApplyContractAdjustRequestVo {
	
	private String contractType;
	private Long contractId;
	private Long ctrProductId;
	private String contractNo;
	private Boolean isback = false;
	private Long applyId;
	private Long userId;
	private String userName;
	private String operationType;
	private BigDecimal dealPrice;
	
	private BigDecimal oldDealNumber;//原合同明细数量
	private BigDecimal newDealNumber;//调整明细数量
	private String warehouseName;//原合同明细仓库
	
	private BigDecimal dealNumber;//库存主表要处理的数量
	private Long newStockContractId;//新合同库存id
	private Long oldStockContractId;//原合同库存id
	
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
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
	
	public Boolean getIsback() {
		return isback;
	}
	public void setIsback(Boolean isback) {
		this.isback = isback;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getOldDealNumber() {
		return oldDealNumber;
	}
	public void setOldDealNumber(BigDecimal oldDealNumber) {
		this.oldDealNumber = oldDealNumber;
	}
	public BigDecimal getNewDealNumber() {
		return newDealNumber;
	}
	public void setNewDealNumber(BigDecimal newDealNumber) {
		this.newDealNumber = newDealNumber;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public Long getCtrProductId() {
		return ctrProductId;
	}
	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}
	public Long getOldStockContractId() {
		return oldStockContractId;
	}
	public void setOldStockContractId(Long oldStockContractId) {
		this.oldStockContractId = oldStockContractId;
	}
	public Long getNewStockContractId() {
		return newStockContractId;
	}
	public void setNewStockContractId(Long newStockContractId) {
		this.newStockContractId = newStockContractId;
	}
	
	
	
	

}
