/**
 * 
 */
package com.spt.bas.client.vo;

import java.math.BigDecimal;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrProduct;

/**
 * 库存请求VO
 * 
 * @author wlddh
 *
 */
public class StockRequest {
	private Long enterpriseId;//企业套账ID
	
	private BigDecimal dealPrice;//单价
	private String productName; // 品名
	private String productCd; // 货名Cd
	private String brandNumber; // 牌号
	private Long factoryId; // 厂商ID
	private String factoryName; // 厂商名称
	private Long warehouseId;
	private String warehouseName;// 仓库名称
	private Long warehouseIdNew;
	private String warehouseNameNew;// 新仓库名称
	private BigDecimal totalNumber; // 总数量
	private BigDecimal dealNumber; // 本次操作数量
	private Long ctrContractId;//合同主表ID
	private String productAttr;
	
	private boolean isBack;
	private String contractType;// 合同类型:B-采购，S-销售
	private String applyType;// 申请单类型：B-采购、A-预售采购、S-销售、M-撮合、R-进口代理
	
	private Long stockId;//库存ID
	private Long stockContractId;//合同库存id
	private Long applyId;
	private String applyNo;
	
//	private BigDecimal stockRemainNumber;//库存历史剩余可用
	
	private Long otherStockId;//另一个库存ID，数据代入用
//	private BigDecimal otherStockRemainNumber;
//	private BigDecimal otherStockRemainFrozen;
	
//	private BigDecimal stockRemainFrozen;//库存剩余冻结
	
	
	private Boolean matchBl = false;//撮合订单判断
	
	private String warehousePosition;
	private String warehouseBatchNo;
	private Long ctrProductId;
	private Long stockDetailId;
	private String stockType;//库存类型
	private String spotType;//货权类型
	

	private BigDecimal preRealNumber;// 上期可用
	private BigDecimal preFrozenNumber;// 上期冻结
	
	public static StockRequest build(CtrProduct product,ApplyProductDetail apd) {
		StockRequest request =new StockRequest();
//		request.setApplyType(BasConstants.APPLY_TYPE_I);
//		request.setContractType(BasConstants.CONTRACT_TYPE_B);
		request.setCtrContractId(product.getCtrContractId());
		request.setBrandNumber(product.getBrandNumber());
		request.setCtrProductId(product.getId());
		request.setDealNumber(apd.getCurNumber());
		request.setDealPrice(product.getDealPrice());
		request.setFactoryId(product.getFactoryId());
		request.setFactoryName(product.getFactoryName());
		request.setProductCd(product.getProductCd());
		request.setProductName(product.getProductName());
		request.setEnterpriseId(product.getEnterpriseId());
		request.setTotalNumber(product.getDealNumber());
		request.setWarehouseId(product.getWarehouseId());
		request.setWarehouseName(product.getWarehouseName());
		request.setWarehouseIdNew(apd.getWarehouseId());
		request.setWarehouseNameNew(apd.getWarehouseName());
		if (apd.getStockDetailId() != null) {
			request.setStockDetailId(apd.getStockDetailId());
		} else {
//			request.setStockDetailId(product.getStockDetailId());
		}
		request.setStockContractId(apd.getStockContractId());
		request.setApplyId(apd.getApplyId());
		request.setProductAttr(product.getProductAttr());
//		request.setApplyNo(entity.getApplyNo());
//		request.setWarehousePosition(entity.getWarehousePosition());
//		request.setWarehouseBatchNo(entity.getWarehouseBatchNo());
		
		return request;
	}
	
	public static StockRequest build(CtrProduct product) {
		StockRequest request =new StockRequest();
		request.setBrandNumber(product.getBrandNumber());
		request.setDealNumber(product.getDealNumber());
		request.setDealPrice(product.getDealPrice());
		request.setFactoryId(product.getFactoryId());
		request.setFactoryName(product.getFactoryName());
		request.setProductCd(product.getProductCd());
		request.setProductName(product.getProductName());
		request.setTotalNumber(product.getDealNumber());
		request.setWarehouseId(product.getWarehouseId());
		request.setWarehouseName(product.getWarehouseName());
		request.setWarehouseIdNew(product.getWarehouseId());
		request.setWarehouseNameNew(product.getWarehouseName());
		request.setEnterpriseId(product.getEnterpriseId());
//		request.setStockDetailId(product.getStockDetailId());
//		request.setApplyId(product.getApplyId());
		request.setProductAttr(product.getProductAttr());
		return request;
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

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public boolean isBack() {
		return isBack;
	}

	public void setBack(boolean isBack) {
		this.isBack = isBack;
	}

	public Long getCtrContractId() {
		return ctrContractId;
	}

	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}

	public BigDecimal getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getWarehouseNameNew() {
		return warehouseNameNew;
	}

	public void setWarehouseNameNew(String warehouseNameNew) {
		this.warehouseNameNew = warehouseNameNew;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getStockId() {
		return stockId;
	}

	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}

//	public BigDecimal getStockRemainNumber() {
//		return stockRemainNumber;
//	}
//
//	public void setStockRemainNumber(BigDecimal stockRemainNumber) {
//		this.stockRemainNumber = stockRemainNumber;
//	}

//	public Long getOtherStockId() {
//		return otherStockId;
//	}
//
//	public void setOtherStockId(Long otherStockId) {
//		this.otherStockId = otherStockId;
//	}
//
//	public BigDecimal getOtherStockRemainNumber() {
//		return otherStockRemainNumber;
//	}
//
//	public void setOtherStockRemainNumber(BigDecimal otherStockRemainNumber) {
//		this.otherStockRemainNumber = otherStockRemainNumber;
//	}

//	public BigDecimal getStockRemainFrozen() {
//		return stockRemainFrozen;
//	}
//
//	public void setStockRemainFrozen(BigDecimal stockRemainFrozen) {
//		this.stockRemainFrozen = stockRemainFrozen;
//	}

//	public BigDecimal getOtherStockRemainFrozen() {
//		return otherStockRemainFrozen;
//	}
//
//	public void setOtherStockRemainFrozen(BigDecimal otherStockRemainFrozen) {
//		this.otherStockRemainFrozen = otherStockRemainFrozen;
//	}

	public String getProductAttr() {
		return productAttr;
	}

	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}

	public Boolean getMatchBl() {
		return matchBl;
	}

	public void setMatchBl(Boolean matchBl) {
		this.matchBl = matchBl;
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

	public Long getWarehouseIdNew() {
		return warehouseIdNew;
	}

	public void setWarehouseIdNew(Long warehouseIdNew) {
		this.warehouseIdNew = warehouseIdNew;
	}

	public Long getOtherStockId() {
		return otherStockId;
	}

	public void setOtherStockId(Long otherStockId) {
		this.otherStockId = otherStockId;
	}

	public Long getCtrProductId() {
		return ctrProductId;
	}

	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}

	public Long getStockDetailId() {
		return stockDetailId;
	}

	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}

	public Long getApplyId() {
		return applyId;
	}

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
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

	public Long getStockContractId() {
		return stockContractId;
	}

	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}
	
}
