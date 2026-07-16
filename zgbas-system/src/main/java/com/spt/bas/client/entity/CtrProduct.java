package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同商品
 */
@Entity
@Table(name = "t_ctr_product")
public class CtrProduct extends IdEntity{

	private static final long serialVersionUID = -6450503416197618508L;

	private Long enterpriseId;// 企业套账ID
	private Long ctrContractId;// 合同主表ID
	private String productName;// 商品名称
	private String productCd;// 商品代码
	private BigDecimal dealPrice;// 单价
	private BigDecimal premium = BigDecimal.ZERO; // 加价(元)
	private BigDecimal dealNumber;// 数量
	private String numberUnit;// 数量单位

	private BigDecimal totalPrice;// 商品价格
	private BigDecimal taxAmount;// 进项税
	private BigDecimal dealAmountNotax;// 不含税价
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean stockFlg = false;// 库存状态
	private String brandNumber;// 牌号
	private BigDecimal remainNumber;// 剩余数量

	private Long factoryId; // 厂商ID
	private String factoryName; // 厂商名称
	private Long warehouseId;
	private String warehouseName;// 仓库
	private BigDecimal warehouseNumber = BigDecimal.ZERO;// 实际已入\出库数量
	private String productAttr; // 产品属性：现货-N、在途-P
	private BigDecimal curApproveNumber = BigDecimal.ZERO;// 当前审批中的数量
//	private Long stockDetailId;//库存明细id，销售时对应的库存明细id
	private Long stockContractId; // 合同库存ID；注意：预售合同在多次采购时，关联是最后一条采购库存合同id，这个字段只能作为参考值不能直接使用

	private String wrapSpecs;// 包装规格
	private String warehousePos;// 仓库所在地
	private String warehouseAddr;// 仓库地址
	private BigDecimal warehousePrice = BigDecimal.ZERO;// 仓储费单价
	private String settlementCode;//结算单号

	/**
	 * 销售合同下游已确认收货数量
	 */
	private BigDecimal confirmReceiptNumber = BigDecimal.ZERO;

	/**
	 * 当前审批中 销售合同下游确认收货数量
	 */
	private BigDecimal curConfirmReceiptNumber = BigDecimal.ZERO;

	/**
	 * 质量标准  Y-原厂标准，G-过渡料，F-副牌料
	 */
	private String qualityStandard;

	public BigDecimal getCurConfirmReceiptNumber() {
		return curConfirmReceiptNumber;
	}

	public void setCurConfirmReceiptNumber(BigDecimal curConfirmReceiptNumber) {
		this.curConfirmReceiptNumber = curConfirmReceiptNumber;
	}

	public BigDecimal getConfirmReceiptNumber() {
		return confirmReceiptNumber;
	}

	public void setConfirmReceiptNumber(BigDecimal confirmReceiptNumber) {
		this.confirmReceiptNumber = confirmReceiptNumber;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getCtrContractId() {
		return ctrContractId;
	}
	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
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
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getPremium() {
		return premium;
	}
	public void setPremium(BigDecimal premium) {
		this.premium = premium;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public String getNumberUnit() {
		return numberUnit;
	}
	public void setNumberUnit(String numberUnit) {
		this.numberUnit = numberUnit;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	public BigDecimal getDealAmountNotax() {
		return dealAmountNotax;
	}
	public void setDealAmountNotax(BigDecimal dealAmountNotax) {
		this.dealAmountNotax = dealAmountNotax;
	}

	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public BigDecimal getRemainNumber() {
		return remainNumber;
	}
	public void setRemainNumber(BigDecimal remainNumber) {
		this.remainNumber = remainNumber;
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
	public Boolean getStockFlg() {
		return stockFlg;
	}
	public void setStockFlg(Boolean stockFlg) {
		this.stockFlg = stockFlg;
	}
	public String getProductAttr() {
		return productAttr;
	}
	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}
	public BigDecimal getWarehouseNumber() {
		return warehouseNumber;
	}
	public void setWarehouseNumber(BigDecimal warehouseNumber) {
		this.warehouseNumber = warehouseNumber;
	}
	public BigDecimal getCurApproveNumber() {
		return curApproveNumber;
	}
	public void setCurApproveNumber(BigDecimal curApproveNumber) {
		this.curApproveNumber = curApproveNumber;
	}
//	public Long getStockDetailId() {
//		return stockDetailId;
//	}
//	public void setStockDetailId(Long stockDetailId) {
//		this.stockDetailId = stockDetailId;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ctrContractId == null) ? 0 : ctrContractId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CtrProduct other = (CtrProduct) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public String getWrapSpecs() {
		return wrapSpecs;
	}
	public void setWrapSpecs(String wrapSpecs) {
		this.wrapSpecs = wrapSpecs;
	}
	public String getWarehousePos() {
		return warehousePos;
	}
	public void setWarehousePos(String warehousePos) {
		this.warehousePos = warehousePos;
	}
	public String getWarehouseAddr() {
		return warehouseAddr;
	}
	public void setWarehouseAddr(String warehouseAddr) {
		this.warehouseAddr = warehouseAddr;
	}
	public BigDecimal getWarehousePrice() {
		return warehousePrice;
	}
	public void setWarehousePrice(BigDecimal warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	public Long getStockContractId() {
		return stockContractId;
	}
	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}
	public String getSettlementCode() {
		return settlementCode;
	}
	public void setSettlementCode(String settlementCode) {
		this.settlementCode = settlementCode;
	}

	public String getQualityStandard() {
		return qualityStandard;
	}

	public void setQualityStandard(String qualityStandard) {
		this.qualityStandard = qualityStandard;
	}
}
