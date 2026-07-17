package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;

public class RptStockDetailReport extends PageSearchVo{
	private Long id;						//明细Id
	private	Long	stockId;				//库存ID
	private	String	productName;			//货名
	private	String	productCd;				//货名CD
	private	String	brandNumber;			//牌号
	private	Long	factoryId;				//厂商ID
	private	String	factoryName;			//厂商名称
	private	String	warehouseName;			//仓库名称
	private	BigDecimal	dealPrice;			//单价
	private	BigDecimal	warehouseAmount;	//仓储费
	private	BigDecimal	frozenNumber = BigDecimal.ZERO;		//冻结数量
	private	BigDecimal	availableNumber = BigDecimal.ZERO;	//可用数量
	private	BigDecimal	presellNumber = BigDecimal.ZERO;	//预售数量
	private	String	productAttr;			//属性 N:现货   P:在途
	private	String	stockStatus;			//库存状态，B-采购，S-销售，I-入库，O-出库，PI-部分入库，PO-部分出库
	private	String	remark;					//备注
	private	Long	enterpriseId;			//企业账套ID
	private String buyContractId;  			//购买合同编号
	private Long buyCompanyId;      		//采购企业ID
	private String buyCompanyName;  		//采购企业名称
	private String sellContractCfs;			//对应卖的合同明细数据
	private String sellContractId; 			//对应卖的合同编号
	private Long warehouseId;				//仓库Id
	private String businessNo;				//单据号
	private BigDecimal deliveryInNumber = BigDecimal.ZERO;	//入库数量
	private BigDecimal deliveryOutNumber = BigDecimal.ZERO;	//出库数量
	private Long bizUserId;
	private String bizUserName;
	private Long deptId;					//部门ID 
	private String deptName;				//部门名称
	private String applyNo;
	private String warehousePosition;
	private String warehouseBatchNo;
	private Long ctrProductId;//对应采购合同的商品明细id
	private Long linkStockDetailId;//关联明细id
	private String source;//来源，用于区分内部采购生成的库存
	private String stockType;//库存类型
	private String spotType;//货权类型
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date createdDate;//创建时间
	private String contractNo;// 合同编号 
	private String ourCompanyName;//我方抬头
	private String status;//C：当前、A：历史、''：全部
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
	public BigDecimal getFrozenNumber() {
		return frozenNumber;
	}
	public void setFrozenNumber(BigDecimal frozenNumber) {
		this.frozenNumber = frozenNumber;
	}
	public BigDecimal getAvailableNumber() {
		return availableNumber;
	}
	public void setAvailableNumber(BigDecimal availableNumber) {
		this.availableNumber = availableNumber;
	}
	public BigDecimal getPresellNumber() {
		return presellNumber;
	}
	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}
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
	public String getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(String buyContractId) {
		this.buyContractId = buyContractId;
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
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
}
