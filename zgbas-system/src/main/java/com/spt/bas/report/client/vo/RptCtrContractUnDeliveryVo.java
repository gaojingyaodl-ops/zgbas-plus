package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class RptCtrContractUnDeliveryVo {
    
    private Long id;

    /**
     * 合同编号
     */
    private String contractNo;
    
    /**
     * 代采赊销业务类型
     */
    private String businessTypeDcsx;

    /**
     * 货名
     */
    private String productsName;

    /**
     * 对方企业名称
     */
    private String companyName;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 实际已入\出库数量
     */
    private BigDecimal warehouseNumber = BigDecimal.ZERO;

    /**
     * 交货日期开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDateFrom;

    /**
     * 业务员名称
     */
    private String matchUserName;

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 逾期发货天数
     */
    private Long overdueDay;

    /**
     * 是否发起合同作废申请
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean applyCancelFlg = false;

    /**
     * 合同类型
     */
    private String contractType;

    private Boolean matchCreditFlg;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getBusinessTypeDcsx() {
        return businessTypeDcsx;
    }

    public void setBusinessTypeDcsx(String businessTypeDcsx) {
        this.businessTypeDcsx = businessTypeDcsx;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getWarehouseNumber() {
        return warehouseNumber;
    }

    public void setWarehouseNumber(BigDecimal warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    public Date getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(Date deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Boolean getApplyCancelFlg() {
        return applyCancelFlg;
    }

    public void setApplyCancelFlg(Boolean applyCancelFlg) {
        this.applyCancelFlg = applyCancelFlg;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public Long getOverdueDay() {
        return overdueDay;
    }

    public void setOverdueDay(Long overdueDay) {
        this.overdueDay = overdueDay;
    }
}
