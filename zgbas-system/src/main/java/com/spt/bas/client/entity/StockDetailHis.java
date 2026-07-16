package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_stock_detail_his")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StockDetailHis extends IdEntity{

	/**
	 * 库存明细流水
	 */
	private static final long serialVersionUID = -2531317727951131664L;
	private	String	contractId;			//合同id
	private	Long	applyId;			//申请ID
	private	Long	stockDetailId;			//库存明细ID
	private	String	operationType;		//操作类型
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date 	operationDate;		//操作日期
	private	String	productName;		//货名
	private	String	productCd;			//货名CD
	private	String	brandNumber;		//牌号
	private	Long	factoryId;			//厂商ID
	private	String	factoryName;		//厂商名称
	private	Long	warehouseId;		//仓库id
	private	String	warehouseName;		//仓库名称
	//private	String	warehouseAddr;		//仓库地址

	private BigDecimal preRealNumber;// 上期可用
	private BigDecimal preFrozenNumber;// 上期冻结
	
	private	BigDecimal	dealNumber;		//入出库数量
	private	BigDecimal	dealPrice;		//单价
	private	BigDecimal	totalPrice;		//总价
	private	BigDecimal	warehouseCost;	//仓储费
//	private	BigDecimal	warehouseRemain;//仓库剩余可用
	private	BigDecimal	remainFrozenNumber;	//冻结数量
	private	BigDecimal	realRemainNumber;//可用数量
	private BigDecimal restPresellNumber = BigDecimal.ZERO; // 预售数量
	private	String	productAttr;		//属性
	private	String	remark;				//备注
	private	Long	enterpriseId;		//企业账套ID
//	private Long stockId; //存储ID
	private	String	stockStatus;			//库存状态，B-采购，S-销售，I-入库，O-出库，PI-部分入库，PO-部分出库
//	private BigDecimal warehouseFrozenRemain;
	
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
	/*public String getWarehouseAddr() {
		return warehouseAddr;
	}
	public void setWarehouseAddr(String warehouseAddr) {
		this.warehouseAddr = warehouseAddr;
	}*/
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
	public BigDecimal getWarehouseCost() {
		return warehouseCost;
	}
	public void setWarehouseCost(BigDecimal warehouseCost) {
		this.warehouseCost = warehouseCost;
	}
//	public BigDecimal getWarehouseRemain() {
//		return warehouseRemain;
//	}
//	public void setWarehouseRemain(BigDecimal warehouseRemain) {
//		this.warehouseRemain = warehouseRemain;
//	}
	public BigDecimal getRealRemainNumber() {
		return realRemainNumber;
	}
	public void setRealRemainNumber(BigDecimal realRemainNumber) {
		this.realRemainNumber = realRemainNumber;
	}
	public String getProductAttr() {
		return productAttr;
	}
	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
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
	public Long getStockDetailId() {
		return stockDetailId;
	}
	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}
	public BigDecimal getRemainFrozenNumber() {
		return remainFrozenNumber;
	}
	public void setRemainFrozenNumber(BigDecimal remainFrozenNumber) {
		this.remainFrozenNumber = remainFrozenNumber;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
//	public Long getStockId() {
//		return stockId;
//	}
//	public void setStockId(Long stockId) {
//		this.stockId = stockId;
//	}
	public String getStockStatus() {
		return stockStatus;
	}
	public void setStockStatus(String stockStatus) {
		this.stockStatus = stockStatus;
	}
//	public BigDecimal getWarehouseFrozenRemain() {
//		return warehouseFrozenRemain;
//	}
//	public void setWarehouseFrozenRemain(BigDecimal warehouseFrozenRemain) {
//		this.warehouseFrozenRemain = warehouseFrozenRemain;
//	}
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
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}


}
