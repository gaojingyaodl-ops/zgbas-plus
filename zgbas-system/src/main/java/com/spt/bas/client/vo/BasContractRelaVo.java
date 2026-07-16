package com.spt.bas.client.vo;

import com.spt.bas.client.entity.BasContractRela;

import java.math.BigDecimal;
import java.util.List;

public class BasContractRelaVo extends BasContractRela {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8544137497797085155L;
	
	private String buyContractNo;//合同编号:采购
	private String sellContractNo;//合同编号:销售
	private BigDecimal buyDeposit;//买方定金
	private BigDecimal sellDeposit;
	private BigDecimal buyFinalPayment;//尾款
	private BigDecimal sellFinalPayment;
	private String buyDeliveryType;//交货方式
	private String sellDeliveryType;
	private String buyWarehouse;//尾款
	private String sellWarehouse;
	
	private String buyDeliveryDateFrom;//交货开始日期
	private String sellDeliveryDateFrom;
	private String buyDeliveryDateTo;//交货结束日期
	private String sellDeliveryDateTo;
	

	private String buyOrSellContractId;		//买或者卖合同id
	
	private List<BasContractVo> lstInsert;
	
	private String matchUserName;
	private Long matchUserId;

	private List<BasContractVo> lstUpdate;
	private List<BasContractVo> lstDelete;
	
	private String brandNumber;
	
	
	public String getBuyOrSellContractId() {
		return buyOrSellContractId;
	}

	public void setBuyOrSellContractId(String buyOrSellContractId) {
		this.buyOrSellContractId = buyOrSellContractId;
	}

	public String getBuyContractNo() {
		return buyContractNo;
	}

	public void setBuyContractNo(String buyContractNo) {
		this.buyContractNo = buyContractNo;
	}
	
	public String getBuyDeliveryType() {
		return buyDeliveryType;
	}

	public void setBuyDeliveryType(String buyDeliveryType) {
		this.buyDeliveryType = buyDeliveryType;
	}

	public String getSellDeliveryType() {
		return sellDeliveryType;
	}

	public void setSellDeliveryType(String sellDeliveryType) {
		this.sellDeliveryType = sellDeliveryType;
	}

	public String getBuyWarehouse() {
		return buyWarehouse;
	}

	public void setBuyWarehouse(String buyWarehouse) {
		this.buyWarehouse = buyWarehouse;
	}

	public String getSellWarehouse() {
		return sellWarehouse;
	}

	public void setSellWarehouse(String sellWarehouse) {
		this.sellWarehouse = sellWarehouse;
	}

	public BigDecimal getBuyDeposit() {
		return buyDeposit;
	}

	public void setBuyDeposit(BigDecimal buyDeposit) {
		this.buyDeposit = buyDeposit;
	}

	public BigDecimal getSellDeposit() {
		return sellDeposit;
	}

	public void setSellDeposit(BigDecimal sellDeposit) {
		this.sellDeposit = sellDeposit;
	}

	public BigDecimal getBuyFinalPayment() {
		return buyFinalPayment;
	}

	public void setBuyFinalPayment(BigDecimal buyFinalPayment) {
		this.buyFinalPayment = buyFinalPayment;
	}

	public BigDecimal getSellFinalPayment() {
		return sellFinalPayment;
	}

	public void setSellFinalPayment(BigDecimal sellFinalPayment) {
		this.sellFinalPayment = sellFinalPayment;
	}

	public String getSellContractNo() {
		return sellContractNo;
	}

	public void setSellContractNo(String sellContractNo) {
		this.sellContractNo = sellContractNo;
	}

	public String getBuyDeliveryDateFrom() {
		return buyDeliveryDateFrom;
	}

	public void setBuyDeliveryDateFrom(String buyDeliveryDateFrom) {
		this.buyDeliveryDateFrom = buyDeliveryDateFrom;
	}

	public String getSellDeliveryDateFrom() {
		return sellDeliveryDateFrom;
	}

	public void setSellDeliveryDateFrom(String sellDeliveryDateFrom) {
		this.sellDeliveryDateFrom = sellDeliveryDateFrom;
	}

	public String getBuyDeliveryDateTo() {
		return buyDeliveryDateTo;
	}

	public void setBuyDeliveryDateTo(String buyDeliveryDateTo) {
		this.buyDeliveryDateTo = buyDeliveryDateTo;
	}

	public String getSellDeliveryDateTo() {
		return sellDeliveryDateTo;
	}

	public void setSellDeliveryDateTo(String sellDeliveryDateTo) {
		this.sellDeliveryDateTo = sellDeliveryDateTo;
	}
	
	@Override
	public Class<?> getSubClass() {
		return BasContractVo.class;
	}
	
	@Override
    @SuppressWarnings("unchecked")
	public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
		setLstInsert((List<BasContractVo>)lstInsert);
		setLstUpdate((List<BasContractVo>)lstUpdate);
		setLstDelete((List<BasContractVo>)lstDelete);
	}

	public List<BasContractVo> getLstInsert() {
		return lstInsert;
	}

	public void setLstInsert(List<BasContractVo> lstInsert) {
		this.lstInsert = lstInsert;
	}

	public String getMatchUserName() {
		return matchUserName;
	}

	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	public List<BasContractVo> getLstUpdate() {
		return lstUpdate;
	}

	public void setLstUpdate(List<BasContractVo> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}

	public List<BasContractVo> getLstDelete() {
		return lstDelete;
	}

	public void setLstDelete(List<BasContractVo> lstDelete) {
		this.lstDelete = lstDelete;
	}

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public String getBrandNumber() {
		return brandNumber;
	}

	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}

}
