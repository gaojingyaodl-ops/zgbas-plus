package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同-采购销售关系表
 */
@Entity
@Table(name = "t_ctr_contract_rela")
public class CtrContractRela extends IdEntity{

	private static final long serialVersionUID = -3750910159541037314L;
	private	Long	buyContractId;	//采购合同id
	private	Long buyProductId;		//采购合同明细id
	private	BigDecimal	buyPrice;	//采购单价
	private	Long	buyUserId;		//采购业务员ID
	private	String	buyUserName;	//采购业务员名称
	private Long buyCompanyId;      //采购企业ID
	private String buyCompanyName;  //采购企业名称
	private	Date	buyDate;		//采购时间
	private	Long	sellContractId;	//销售合同id
	private	Long sellProductId;		//销售合同明细id
	private	BigDecimal	sellPrice;	//销售单价
	private	Long	sellUserId;		//销售业务员ID
	private	String	sellUserName;	//销售业务员名称
	private	Date	sellDate;		//销售时间
//	private	Long	stockDetailId;	//库存明细ID
	private	Long	stockContractId;	//合同库存ID
	private Long sellCompanyId;     //销售企业ID
	private String sellCompanyName; //销售企业名称
	private	BigDecimal	dealNumber;	//成交数量
	private	String	productName;	//品名
	private	String	productCd;		//品名代码
	private	String	brandNumber;	//牌号
	private	Long	factoryId;		//厂商ID
	private	String	factoryName;	//厂商名称
	private	Long	warehouseId;	//仓库ID
	private	String	warehouseName;	//仓库名称
	private	Long	enterpriseId;	//企业账套ID
	public Long getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(Long buyContractId) {
		this.buyContractId = buyContractId;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public Long getBuyUserId() {
		return buyUserId;
	}
	public void setBuyUserId(Long buyUserId) {
		this.buyUserId = buyUserId;
	}
	public String getBuyUserName() {
		return buyUserName;
	}
	public void setBuyUserName(String buyUserName) {
		this.buyUserName = buyUserName;
	}
	public Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	public Long getSellContractId() {
		return sellContractId;
	}
	public void setSellContractId(Long sellContractId) {
		this.sellContractId = sellContractId;
	}
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public Long getSellUserId() {
		return sellUserId;
	}
	public void setSellUserId(Long sellUserId) {
		this.sellUserId = sellUserId;
	}
	public String getSellUserName() {
		return sellUserName;
	}
	public void setSellUserName(String sellUserName) {
		this.sellUserName = sellUserName;
	}
	public Date getSellDate() {
		return sellDate;
	}
	public void setSellDate(Date sellDate) {
		this.sellDate = sellDate;
	}
//	public Long getStockDetailId() {
//		return stockDetailId;
//	}
//	public void setStockDetailId(Long stockDetailId) {
//		this.stockDetailId = stockDetailId;
//	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
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
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getBuyProductId() {
		return buyProductId;
	}
	public void setBuyProductId(Long buyProductId) {
		this.buyProductId = buyProductId;
	}
	public Long getSellProductId() {
		return sellProductId;
	}
	public void setSellProductId(Long sellProductId) {
		this.sellProductId = sellProductId;
	}
	public Long getBuyCompanyId() {
		return buyCompanyId;
	}
	public void setBuyCompanyId(Long buyCompanyId) {
		this.buyCompanyId = buyCompanyId;
	}
	public String getBuyCompanyName() {
		return buyCompanyName;
	}
	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}
	public Long getSellCompanyId() {
		return sellCompanyId;
	}
	public void setSellCompanyId(Long sellCompanyId) {
		this.sellCompanyId = sellCompanyId;
	}
	public String getSellCompanyName() {
		return sellCompanyName;
	}
	public void setSellCompanyName(String sellCompanyName) {
		this.sellCompanyName = sellCompanyName;
	}
	public Long getStockContractId() {
		return stockContractId;
	}
	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}
	


}
