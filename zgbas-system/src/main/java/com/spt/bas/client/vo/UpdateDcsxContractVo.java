package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 更新代采赊销合同
 */
public class UpdateDcsxContractVo {
    
    /**
     * 合同id
     */
    private Long id;
    
    /**
     * 调整前附件id
     */
    private String oldFileId;
    
    /**
     * 调整后附件id
     */
    private String fileId;
    
    /**
     * 调整前合同总价
     */
    private BigDecimal totalAmount;
    
    /**
     * 调整前合同数量
     */
    private BigDecimal totalNumber;
    
    /**
     * 调整前合同单价
     */
    private BigDecimal dealPrice;
    
    /**
     * 调整前定付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;
    
    /**
     * 修改后合同总价
     */
    private BigDecimal newTotalAmount;
    
    /**
     * 修改后合同数量
     */
    private BigDecimal newTotalNumber;
    
    /**
     * 修改后合同单价
     */
    private BigDecimal newDealPrice;
    
    /**
     * 修改后约定付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date newPayFullTime;
    
    /**
     * 操作人id
     */
    private Long matchUserId;

    /**
     * 操作人名称
     */
    private String matchUserName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOldFileId() {
        return oldFileId;
    }

    public void setOldFileId(String oldFileId) {
        this.oldFileId = oldFileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public BigDecimal getNewTotalAmount() {
        return newTotalAmount;
    }

    public void setNewTotalAmount(BigDecimal newTotalAmount) {
        this.newTotalAmount = newTotalAmount;
    }

    public BigDecimal getNewTotalNumber() {
        return newTotalNumber;
    }

    public void setNewTotalNumber(BigDecimal newTotalNumber) {
        this.newTotalNumber = newTotalNumber;
    }

    public BigDecimal getNewDealPrice() {
        return newDealPrice;
    }

    public void setNewDealPrice(BigDecimal newDealPrice) {
        this.newDealPrice = newDealPrice;
    }

    public Date getNewPayFullTime() {
        return newPayFullTime;
    }

    public void setNewPayFullTime(Date newPayFullTime) {
        this.newPayFullTime = newPayFullTime;
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
}
