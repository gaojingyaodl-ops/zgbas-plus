package com.spt.bas.client.vo;

import java.math.BigDecimal;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrProduct;

public class StockDetailRequest {
	private Long ctrContractId;//合同主表ID
	private String productName;//商品名称
	private String productCd;//商品代码
	private String brandNumber;//牌号
	private	Long	factoryId;		//厂商ID
	private	String	factoryName;	//厂商名称
	private Long warehouseId;
	private String warehouseName;//仓库
	
	//入库时使用--start
	private Long warehouseIdNew;
	private String warehouseNameNew;// 新仓库名称
	private String applyNo;
	private String warehousePosition;
	private String warehouseBatchNo;
	private String stockType;//库存类型
	private String spotType;//货权类型
	private Boolean matchBl = false;//撮合订单判断
	//入库时使用--end
	
	private BigDecimal dealPrice;//单价
	private BigDecimal dealNumber;//数量
	private	Long	enterpriseId;			//企业账套ID
	
	private String linkContractId;
	private Long linkDetailId;
	private Long stockContractId;//合同库存id
	private Long stockId;
	private Long applyId;
	private Long approveId;//审批编号
	private String applyType;
	private boolean back;
	private Long ctrProductId;
	private Long sellContractId;//预售合同Id
	

	private Long bizUserId;
	private String bizUserName;

	private BigDecimal preRealNumber;// 上期可用
	private BigDecimal preFrozenNumber;// 上期冻结
	private String wrapSpecs;//包装规格
	private String warehousePos;//仓库所在地
	private String warehouseAddr;//仓库地址
	public static StockDetailRequest build(CtrProduct product) {
		StockDetailRequest request =new StockDetailRequest();
//		request.setApplyId(userInfor.getApproveId());
		request.setBack(false);
		request.setBrandNumber(product.getBrandNumber());
		request.setCtrContractId(product.getCtrContractId());
		request.setDealNumber(product.getDealNumber());
		request.setDealPrice(product.getDealPrice());
		request.setFactoryId(product.getFactoryId());
		request.setFactoryName(product.getFactoryName());
		request.setLinkContractId(product.getCtrContractId()+"");
		request.setStockContractId(product.getStockContractId());
		request.setProductCd(product.getProductCd());
		request.setProductName(product.getProductName());
		request.setWarehouseId(product.getWarehouseId());
		request.setWarehouseName(product.getWarehouseName());
		request.setEnterpriseId(product.getEnterpriseId());
		request.setCtrProductId(product.getId());
		request.setWarehouseIdNew(product.getWarehouseId());
		request.setWarehouseNameNew(product.getWarehouseName());
		request.setWarehousePos(product.getWarehousePos());
		request.setWrapSpecs(product.getWrapSpecs());
		request.setWarehouseAddr(product.getWarehouseAddr());
		return request;
	}
	
	public static StockDetailRequest build(CtrProduct product,ApplyProductDetail apd) {
		StockDetailRequest request = build(product);
		request.setLinkDetailId(apd.getStockDetailId());
		request.setStockContractId(apd.getStockContractId());
		request.setApplyId(apd.getApplyId());
		request.setWarehouseIdNew(apd.getWarehouseId());
		request.setWarehouseNameNew(apd.getWarehouseName());
		request.setDealNumber(apd.getCurNumber());
		return request;
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
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public String getLinkContractId() {
		return linkContractId;
	}
	public void setLinkContractId(String linkContractId) {
		this.linkContractId = linkContractId;
	}
	public Long getLinkDetailId() {
		return linkDetailId;
	}
	public void setLinkDetailId(Long linkDetailId) {
		this.linkDetailId = linkDetailId;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public boolean isBack() {
		return back;
	}
	public void setBack(boolean back) {
		this.back = back;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public Long getStockId() {
		return stockId;
	}

	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getCtrProductId() {
		return ctrProductId;
	}

	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}

	public Long getSellContractId() {
		return sellContractId;
	}

	public void setSellContractId(Long sellContractId) {
		this.sellContractId = sellContractId;
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

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public Long getWarehouseIdNew() {
		return warehouseIdNew;
	}

	public void setWarehouseIdNew(Long warehouseIdNew) {
		this.warehouseIdNew = warehouseIdNew;
	}

	public String getWarehouseNameNew() {
		return warehouseNameNew;
	}

	public void setWarehouseNameNew(String warehouseNameNew) {
		this.warehouseNameNew = warehouseNameNew;
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

	public Boolean getMatchBl() {
		return matchBl;
	}

	public void setMatchBl(Boolean matchBl) {
		this.matchBl = matchBl;
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
	
}
