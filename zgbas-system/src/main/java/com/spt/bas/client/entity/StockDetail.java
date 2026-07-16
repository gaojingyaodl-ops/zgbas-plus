package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 库存明细
 */
@Entity
@Table(name = "t_stock_detail")
public class StockDetail extends IdEntity{

	private static final long serialVersionUID = -1602914374207037366L;
	private	Long	stockId;				//库存ID
	private Long deliveryInApplyId;//入库申请单id
	private	String	productName;			//货名
	private	String	productCd;				//货名CD
	private	String	brandNumber;			//牌号
	private	Long	factoryId;				//厂商ID
	private	String	factoryName;			//厂商名称
	private	String	warehouseName;			//仓库名称
	private	BigDecimal	dealPrice;			//单价
	private	BigDecimal	warehouseAmount;		//仓储费
	private	BigDecimal	frozenNumber = BigDecimal.ZERO;		//冻结数量，新版本上线后，这个字段没用了
	private	BigDecimal	availableNumber = BigDecimal.ZERO;	//可用数量，采购数量
	private	BigDecimal	presellNumber = BigDecimal.ZERO;	//预售数量
	private	String	productAttr;			//属性 N:现货   P:在途
	private	String	stockStatus;			//库存状态，B-采购，S-销售，I-入库，O-出库，PI-部分入库，PO-部分出库
	private	String	remark;					//备注
	private	Long	enterpriseId;			//企业账套ID
	private String buyContractId;  //购买合同编号
	private Long buyCompanyId;      //采购企业ID
	private String buyCompanyName;  //采购企业名称
	private String sellContractCfs;//对应卖的合同明细数据
	private String sellContractId; //对应卖的合同编号
	private Long warehouseId;	//仓库Id
	private String businessNo;//单据号
	private BigDecimal deliveryInNumber = BigDecimal.ZERO;//入库数量
	private BigDecimal deliveryOutNumber = BigDecimal.ZERO;//出库数量
	private Long bizUserId;
	private String bizUserName;
	private String applyNo;
	private String warehousePosition;//仓位/货位
	private String warehouseBatchNo;//批号
	private Long ctrProductId;//对应采购合同的商品明细id
	private Long linkStockDetailId;//关联明细id
	private String source;//来源，用于区分内部采购生成的库存
	private String stockType;//库存类型
	private String spotType;//货权类型
	
	private Long stockContractId;//合同库存id
	private String wrapSpecs;//包装规格
	private String warehousePos;//仓库所在地
	private String warehouseAddr;//仓库地址
	private BigDecimal warehousePrice;//仓储费单价
	
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
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
//	public BigDecimal getFrozenNumber() {
//		return frozenNumber;
//	}
//	public void setFrozenNumber(BigDecimal frozenNumber) {
//		this.frozenNumber = frozenNumber;
//	}
	public String getProductAttr() {
		return productAttr;
	}
	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}
	
	public String getStockStatus() {
		return stockStatus;
	}
	public void setStockStatus(String stockStatus) {
		this.stockStatus = stockStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public BigDecimal getWarehouseAmount() {
		return warehouseAmount;
	}
	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}
	public BigDecimal getAvailableNumber() {
		return availableNumber;
	}
	public void setAvailableNumber(BigDecimal availableNumber) {
		this.availableNumber = availableNumber;
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
	public String getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(String buyContractId) {
		this.buyContractId = buyContractId;
	}
//	public String getSellContractId() {
//		return sellContractId;
//	}
//	public void setSellContractId(String sellContractId) {
//		this.sellContractId = sellContractId;
//	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
//	public String getSellContractCfs() {
//		return sellContractCfs;
//	}
//	public void setSellContractCfs(String sellContractCfs) {
//		this.sellContractCfs = sellContractCfs;
//	}
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
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}
	public String getWarehousePosition() {
		return warehousePosition;
	}
	public void setWarehousePosition(String warehousePosition) {
		this.warehousePosition = warehousePosition;
	}
	public String getWarehouseBatchNo() {
		return warehouseBatchNo;
	}
	public void setWarehouseBatchNo(String warehouseBatchNo) {
		this.warehouseBatchNo = warehouseBatchNo;
	}
	public BigDecimal getPresellNumber() {
		return presellNumber;
	}
	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}
	public Long getCtrProductId() {
		return ctrProductId;
	}
	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}
	public Long getLinkStockDetailId() {
		return linkStockDetailId;
	}
	public void setLinkStockDetailId(Long linkStockDetailId) {
		this.linkStockDetailId = linkStockDetailId;
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
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getStockType() {
		return stockType;
	}
	public void setStockType(String stockType) {
		this.stockType = stockType;
	}
	public String getSpotType() {
		return spotType;
	}
	public void setSpotType(String spotType) {
		this.spotType = spotType;
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
	public Long getStockContractId() {
		return stockContractId;
	}
	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}
	public BigDecimal getWarehousePrice() {
		return warehousePrice;
	}
	public void setWarehousePrice(BigDecimal warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	public BigDecimal getFrozenNumber() {
		return frozenNumber;
	}
	public void setFrozenNumber(BigDecimal frozenNumber) {
		this.frozenNumber = frozenNumber;
	}
	public String getSellContractCfs() {
		return sellContractCfs;
	}
	public void setSellContractCfs(String sellContractCfs) {
		this.sellContractCfs = sellContractCfs;
	}
	public String getSellContractId() {
		return sellContractId;
	}
	public void setSellContractId(String sellContractId) {
		this.sellContractId = sellContractId;
	}
	public Long getDeliveryInApplyId() {
		return deliveryInApplyId;
	}
	public void setDeliveryInApplyId(Long deliveryInApplyId) {
		this.deliveryInApplyId = deliveryInApplyId;
	}
	
}
