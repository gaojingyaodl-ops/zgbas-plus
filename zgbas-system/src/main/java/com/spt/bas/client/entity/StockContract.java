package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同库存明细
 */
@Entity
@Table(name = "t_stock_contract")
public class StockContract extends IdEntity {

	private static final long serialVersionUID = -1602914374207037366L;
	private Long buyContractId; // 采购合同ID
	private Long buyProductId;// 对应采购合同的商品明细id
	private String productName; // 货名
	private String productCd; // 货名CD
	private String brandNumber; // 牌号
	private Long factoryId; // 厂商ID
	private String factoryName; // 厂商名称
	private Long warehouseId; // 仓库Id
	private String warehouseName; // 仓库名称
	private BigDecimal dealPrice; // 采购单价
	private BigDecimal warehouseAmount; // 仓储费
	private BigDecimal buyNumber = BigDecimal.ZERO; // 采购数量
	private BigDecimal sellingNumber = BigDecimal.ZERO; // 销售中数量
	private BigDecimal sellNumber = BigDecimal.ZERO; // 已销售数量
	private BigDecimal presellNumber = BigDecimal.ZERO; // 预售数量
	private BigDecimal deliveryInNumber = BigDecimal.ZERO;// 入库数量
	private BigDecimal deliveryOutNumber = BigDecimal.ZERO;// 出库数量
	private Long enterpriseId; // 企业账套ID
	//采购业务员
	private Long bizUserId;
	private String bizUserName;
//	//关联查询 合同
//	private List<CtrContract> ctrContract;


	public Long getBuyContractId() {
		return buyContractId;
	}

	public void setBuyContractId(Long buyContractId) {
		this.buyContractId = buyContractId;
	}

	public Long getBuyProductId() {
		return buyProductId;
	}

	public void setBuyProductId(Long buyProductId) {
		this.buyProductId = buyProductId;
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

	public BigDecimal getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}

	public BigDecimal getWarehouseAmount() {
		return warehouseAmount;
	}

	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}

	public BigDecimal getBuyNumber() {
		return buyNumber;
	}

	public void setBuyNumber(BigDecimal buyNumber) {
		this.buyNumber = buyNumber;
	}

	public BigDecimal getSellingNumber() {
		return sellingNumber;
	}

	public void setSellingNumber(BigDecimal sellingNumber) {
		this.sellingNumber = sellingNumber;
	}

	public BigDecimal getSellNumber() {
		return sellNumber;
	}

	public void setSellNumber(BigDecimal sellNumber) {
		this.sellNumber = sellNumber;
	}

	public BigDecimal getPresellNumber() {
		return presellNumber;
	}

	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}

	public BigDecimal getDeliveryInNumber() {
		return deliveryInNumber;
	}

	public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
		this.deliveryInNumber = deliveryInNumber;
	}

	public BigDecimal getDeliveryOutNumber() {
		return deliveryOutNumber;
	}

	public void setDeliveryOutNumber(BigDecimal deliveryOutNumber) {
		this.deliveryOutNumber = deliveryOutNumber;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getBizUserId() {
		return bizUserId;
	}

	public void setBizUserId(Long bizUserId) {
		this.bizUserId = bizUserId;
	}

	public String getBizUserName() {
		return bizUserName;
	}

	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}
	
//	@JsonIgnore
//	@OneToMany(fetch = FetchType.LAZY)
//	@JoinColumn(name = "id",referencedColumnName = "buyContractId",updatable=false,insertable=false)
//	public List<CtrContract> getCtrContract() {
//		return ctrContract;
//	}
//
//	public void setCtrContract(List<CtrContract> ctrContract) {
//		this.ctrContract = ctrContract;
//	}
//	
	
	
	
}
