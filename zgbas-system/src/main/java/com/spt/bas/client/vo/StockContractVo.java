/**
 * 
 */
package com.spt.bas.client.vo;

import java.math.BigDecimal;

import com.spt.bas.client.entity.StockContract;

/**
 * @author wlddh
 *
 */
public class StockContractVo extends StockContract {

	private static final long serialVersionUID = -8042218840429889913L;
	private String wrapSpecs;// 包装规格
	private String warehousePos;// 仓库所在地
	private String productAttr;//现货期货
	private BigDecimal remainNumber;// 剩余数量
	private String ourCompanyName;//我方抬头
	private String contractNo; //采购合同编号
	private String qualityStandard; //质量标准  Y-原厂标准，G-过渡料，F-副牌料
	private BigDecimal warehousePrice;//仓储费单价
	
	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public BigDecimal getRemainNumber() {
		return remainNumber;
	}

	public void setRemainNumber(BigDecimal remainNumber) {
		this.remainNumber = remainNumber;
	}

	public String getProductAttr() {
		return productAttr;
	}

	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}

	public String getQualityStandard() {
		return qualityStandard;
	}

	public void setQualityStandard(String qualityStandard) {
		this.qualityStandard = qualityStandard;
	}

	public BigDecimal getWarehousePrice() {
		return warehousePrice;
	}

	public void setWarehousePrice(BigDecimal warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	
	
}
