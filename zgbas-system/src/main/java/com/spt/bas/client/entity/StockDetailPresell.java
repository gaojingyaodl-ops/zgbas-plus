package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 预售库存明细
 */
@Entity
@Table(name = "t_stock_detail_presell")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StockDetailPresell extends IdEntity{

	private static final long serialVersionUID = 6092704173070688728L;
	private Long stockId;					//库存Id
	private	String	productName;			//货名
	private	String	productCd;				//货名CD
	private	String	brandNumber;			//牌号
	private	Long	factoryId;				//厂商ID
	private	String	factoryName;			//厂商名称
	private Long warehouseId;				//仓库Id
	private	String	warehouseName;			//仓库名称
	private	BigDecimal	dealPrice;			//单价
	private BigDecimal presellNumber = BigDecimal.ZERO;		//预售数量
	private BigDecimal buyedNumber = BigDecimal.ZERO;			//已购数量
	private BigDecimal approveBuyNumber = BigDecimal.ZERO;//当前审批中的采购数量
	private Long contractId;
	private Long ctrProductId;
	private Long enterpriseId; //企业账套Id
	private String remark;
	//关联查询 合同
	//private List<CtrContract> ctrContract;
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
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
	public BigDecimal getPresellNumber() {
		return presellNumber;
	}
	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}
	public BigDecimal getBuyedNumber() {
		return buyedNumber;
	}
	public void setBuyedNumber(BigDecimal buyedNumber) {
		this.buyedNumber = buyedNumber;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public Long getCtrProductId() {
		return ctrProductId;
	}
	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public BigDecimal getApproveBuyNumber() {
		return approveBuyNumber;
	}
	public void setApproveBuyNumber(BigDecimal approveBuyNumber) {
		this.approveBuyNumber = approveBuyNumber;
	}
//	@JsonIgnore
//	@OneToMany(fetch = FetchType.LAZY)
//	@JoinColumn(name = "id",referencedColumnName = "contractId",updatable=false,insertable=false)
//	public List<CtrContract> getCtrContract() {
//		return ctrContract;
//	}
//	public void setCtrContract(List<CtrContract> ctrContract) {
//		this.ctrContract = ctrContract;
//	}
	
	

}
