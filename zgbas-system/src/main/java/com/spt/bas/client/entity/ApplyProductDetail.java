package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请商品明细表
 */
@Entity
@Table(name = "t_apply_product_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyProductDetail extends IdEntity {

    private static final long serialVersionUID = 5330488031532380570L;
    private String productName; // 货名
    private String productCd; // 货名Cd
    private String brandNumber; // 牌号
    private Long factoryId; // 厂商ID
    private String factoryName; // 厂商名称
    private BigDecimal dealNumber; // 入出库数量
    private BigDecimal minDealPrice;//最低销售价
    private BigDecimal dealPrice; // 单价
    private BigDecimal premium = BigDecimal.ZERO; // 加价(元)
    private BigDecimal otherDealPrice;
    private BigDecimal totalPrice; // 总价
    private BigDecimal taxPrice;    //不含税单价
    private Long enterpriseId; // 企业ID
    private Long applyId; // 申请单ID
    private String applyType; // 申请类型
    private String productAttr; // 属性：在途、现货:N\P
    private String numberUnit = BasConstants.NUMBER_UNIT_DUN;// 数量单位
    private Long warehouseId;// 仓库Id
    private String warehouseName;// 仓库名称
    private BigDecimal curNumber = BigDecimal.ZERO;// 当前出入库数量
    private String warehouseNo;// 入库单号
    private String wrapSpecs;// 包装规格
    private String warehousePos;// 仓库所在地
    private String warehouseAddr;// 仓库地址
    private BigDecimal warehousePrice;// 仓储费单价

    private Long stockContractId;// 合同库存id
    private Long stockDetailId;// 库存明细id
    private Long ctrProductId;

    private Long applyDeliveryOutId;

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

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
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

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getNumberUnit() {
        return numberUnit;
    }

    public void setNumberUnit(String numberUnit) {
        this.numberUnit = numberUnit;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }


    public BigDecimal getCurNumber() {
        return curNumber;
    }

    public void setCurNumber(BigDecimal curNumber) {
        this.curNumber = curNumber;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(String productType) {
        this.productAttr = productType;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public Long getStockDetailId() {
        return stockDetailId;
    }

    public void setStockDetailId(Long stockDetailId) {
        this.stockDetailId = stockDetailId;
    }

    public Long getCtrProductId() {
        return ctrProductId;
    }

    public void setCtrProductId(Long ctrProductId) {
        this.ctrProductId = ctrProductId;
    }

    public BigDecimal getOtherDealPrice() {
        return otherDealPrice;
    }

    public void setOtherDealPrice(BigDecimal otherDealPrice) {
        this.otherDealPrice = otherDealPrice;
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

    public BigDecimal getMinDealPrice() {
        return minDealPrice;
    }

    public void setMinDealPrice(BigDecimal minDealPrice) {
        this.minDealPrice = minDealPrice;
    }

    public Long getApplyDeliveryOutId() {
        return applyDeliveryOutId;
    }

    public void setApplyDeliveryOutId(Long applyDeliveryOutId) {
        this.applyDeliveryOutId = applyDeliveryOutId;
    }

    public BigDecimal getTaxPrice() { return taxPrice; }

    public void setTaxPrice(BigDecimal taxPrice) { this.taxPrice = taxPrice; }
}
