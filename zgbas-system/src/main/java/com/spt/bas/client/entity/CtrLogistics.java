package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合同物流单
 */
@Entity
@Table(name = "t_ctr_logistics")
public class CtrLogistics extends IdEntity {
    private static final long serialVersionUID = 4758263908822365278L;
    /**
     * 物流合同尾号
     */
    private String logisticsNo;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 提货数量
     */
    private BigDecimal logisticsNumber = BigDecimal.ZERO;

    /**
     * 提货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date logisticsDate;

    /**
     * 货名
     */
    private String productNames;

    /**
     * 供应商
     */
    private String supplierName;

    /**
     * 客户
     */
    private String companyName;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员
     */
    private String matchUserName;
    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 业务员手机号
     */
    private String matchUserPhone;

    /**
     * 贸易链条
     */
    private String tradeChain;

    /**
     * 采购合同ID
     */
    private Long buyContractId;

    /**
     * 采购合同编号
     */
    private String buyContractNo;

    /**
     * 采购我方抬头
     */
    private String buyOurCompanyName;

    /**
     * 采购单价
     */
    private BigDecimal buyDealPrice;

    /**
     * 采购总价
     */
    private BigDecimal buyTotalAmount;

    /**
     * 提货仓库ID
     */
    private Long warehouseId;

    /**
     * 提货仓库
     */
    private String warehouseName;

    /**
     * 供方合同号
     */
    private String supplierNo;

    /**
     * 采购交货方式
     */
    private String buyDeliveryType;

    /**
     * 采购交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyDeliveryDate;

    /**
     * 提货地址
     */
    private String takeDelieveryAddr;

    /**
     * 销售合同ID
     */
    private Long sellContractId;

    /**
     * 销售合同编号
     */
    private String sellContractNo;

    /**
     * 销售我方抬头
     */
    private String sellOurCompanyName;

    /**
     * 销售单价
     */
    private BigDecimal sellDealPrice;

    /**
     * 销售总价
     */
    private BigDecimal sellTotalAmount;

    /**
     * 销售交货方式
     */
    private String sellDeliveryType;

    /**
     * 销售交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellDeliveryDate;

    /**
     * 收货地址
     */
    private String receiveDeliveryAddr;

    /**
     * 距离测算
     */
    private String logisticsDistance;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;
    
    private Long approveId;
    private Boolean matchCreditFlg;

    /**
     * 提货次数
     */
    private Long logisticsDeliveryNum;

    /**
     * 是否有效
     * 1-有效，0-无效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg=true ;


    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public BigDecimal getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(BigDecimal logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getMatchUserPhone() {
        return matchUserPhone;
    }

    public void setMatchUserPhone(String matchUserPhone) {
        this.matchUserPhone = matchUserPhone;
    }

    public String getTradeChain() {
        return tradeChain;
    }

    public void setTradeChain(String tradeChain) {
        this.tradeChain = tradeChain;
    }

    public Long getBuyContractId() {
        return buyContractId;
    }

    public void setBuyContractId(Long buyContractId) {
        this.buyContractId = buyContractId;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public BigDecimal getBuyDealPrice() {
        return buyDealPrice;
    }

    public void setBuyDealPrice(BigDecimal buyDealPrice) {
        this.buyDealPrice = buyDealPrice;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
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

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getBuyDeliveryType() {
        return buyDeliveryType;
    }

    public void setBuyDeliveryType(String buyDeliveryType) {
        this.buyDeliveryType = buyDeliveryType;
    }

    public Date getBuyDeliveryDate() {
        return buyDeliveryDate;
    }

    public void setBuyDeliveryDate(Date buyDeliveryDate) {
        this.buyDeliveryDate = buyDeliveryDate;
    }

    public String getTakeDelieveryAddr() {
        return takeDelieveryAddr;
    }

    public void setTakeDelieveryAddr(String takeDelieveryAddr) {
        this.takeDelieveryAddr = takeDelieveryAddr;
    }

    public Long getSellContractId() {
        return sellContractId;
    }

    public void setSellContractId(Long sellContractId) {
        this.sellContractId = sellContractId;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public BigDecimal getSellDealPrice() {
        return sellDealPrice;
    }

    public void setSellDealPrice(BigDecimal sellDealPrice) {
        this.sellDealPrice = sellDealPrice;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public String getSellDeliveryType() {
        return sellDeliveryType;
    }

    public void setSellDeliveryType(String sellDeliveryType) {
        this.sellDeliveryType = sellDeliveryType;
    }

    public Date getSellDeliveryDate() {
        return sellDeliveryDate;
    }

    public void setSellDeliveryDate(Date sellDeliveryDate) {
        this.sellDeliveryDate = sellDeliveryDate;
    }

    public String getReceiveDeliveryAddr() {
        return receiveDeliveryAddr;
    }

    public void setReceiveDeliveryAddr(String receiveDeliveryAddr) {
        this.receiveDeliveryAddr = receiveDeliveryAddr;
    }

    public String getLogisticsDistance() {
        return logisticsDistance;
    }

    public void setLogisticsDistance(String logisticsDistance) {
        this.logisticsDistance = logisticsDistance;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public String getBuyOurCompanyName() {
        return buyOurCompanyName;
    }

    public void setBuyOurCompanyName(String buyOurCompanyName) {
        this.buyOurCompanyName = buyOurCompanyName;
    }

    public String getSellOurCompanyName() {
        return sellOurCompanyName;
    }

    public void setSellOurCompanyName(String sellOurCompanyName) {
        this.sellOurCompanyName = sellOurCompanyName;
    }

    public Date getLogisticsDate() {
        return logisticsDate;
    }

    public void setLogisticsDate(Date logisticsDate) {
        this.logisticsDate = logisticsDate;
    }

    public Long getLogisticsDeliveryNum() {
        return logisticsDeliveryNum;
    }

    public void setLogisticsDeliveryNum(Long logisticsDeliveryNum) {
        this.logisticsDeliveryNum = logisticsDeliveryNum;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
