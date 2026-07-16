package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 库存流水
 */
@Entity
@Table(name = "t_stock_flow")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StockFlow extends IdEntity {

	private static final long serialVersionUID = -5337430664465212194L;
	private Long contractId; // 合同Id
	private Long stockId; // 库存Id
	private Long applyId; // 申请Id
	private String operationType; // 操作类型
	private String productName; // 货名
	private String productCd; // 货名Cd
	private String brandNumber; // 牌号
	private Long factoryId; // 厂商Id
	private String factoryName; // 厂商名称
	private Long warehouseId; // 仓库Id
	private String warehouseName; // 仓库名称
	private String addSub; // 增减
	private BigDecimal preRealNumber;// 上期可用
	private BigDecimal preFrozenNumber;// 上期冻结
	private BigDecimal dealNumber; // 数量
	private BigDecimal dealPrice; // 单价
	private BigDecimal totalPrice; // 总价
	private String remark; // 备注
	private BigDecimal restRealNumber;// 剩余可用
	private BigDecimal restFrozenNumber;// 剩余冻结
	private BigDecimal restPresellNumber = BigDecimal.ZERO; // 预售数量
	private Long createdUserId; // 创建人Id
	private String createdUserName; // 创建人姓名
	private Long enterpriseId; // 企业套账Id

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Long getStockId() {
		return stockId;
	}

	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}

	public Long getApplyId() {
		return applyId;
	}

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
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

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getAddSub() {
		return addSub;
	}

	public void setAddSub(String addSub) {
		this.addSub = addSub;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getRestRealNumber() {
		return restRealNumber;
	}

	public void setRestRealNumber(BigDecimal restRealNumber) {
		this.restRealNumber = restRealNumber;
	}

	public BigDecimal getRestFrozenNumber() {
		return restFrozenNumber;
	}

	public void setRestFrozenNumber(BigDecimal restFrozenNumber) {
		this.restFrozenNumber = restFrozenNumber;
	}

	public Long getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Long createdUserId) {
		this.createdUserId = createdUserId;
	}

	public String getCreatedUserName() {
		return createdUserName;
	}

	public void setCreatedUserName(String createdUserName) {
		this.createdUserName = createdUserName;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public BigDecimal getRestPresellNumber() {
		return restPresellNumber;
	}

	public void setRestPresellNumber(BigDecimal restPresellNumber) {
		this.restPresellNumber = restPresellNumber;
	}

	public BigDecimal getPreRealNumber() {
		return preRealNumber;
	}

	public void setPreRealNumber(BigDecimal preRealNumber) {
		this.preRealNumber = preRealNumber;
	}

	public BigDecimal getPreFrozenNumber() {
		return preFrozenNumber;
	}

	public void setPreFrozenNumber(BigDecimal preFrozenNumber) {
		this.preFrozenNumber = preFrozenNumber;
	}

}
