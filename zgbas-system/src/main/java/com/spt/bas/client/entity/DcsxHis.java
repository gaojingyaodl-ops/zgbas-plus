package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_dcsx_his")
public class DcsxHis extends IdEntity {
    
    /**
     * 调整前合同Id
     */
    private Long contractId;
    
    /**
     * 调整前合同附件ID
     */
    private String oldFileId;
    
    /**
     * 调整前合同总价
     */
    private BigDecimal oldTotalAmount;
    
    /**
     * 调整前合同数量
     */
    private BigDecimal oldTotalNumber;
    
    /**
     * 调整前合同单价
     */
    private BigDecimal oldDealPrice;
    
    /**
     * 调整前约定付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date oldPayFullTime;

    /**
     * 调整前合同附件ID
     */
    private String newFileId;

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

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getOldFileId() {
        return oldFileId;
    }

    public void setOldFileId(String oldFileId) {
        this.oldFileId = oldFileId;
    }

    public BigDecimal getOldTotalAmount() {
        return oldTotalAmount;
    }

    public void setOldTotalAmount(BigDecimal oldTotalAmount) {
        this.oldTotalAmount = oldTotalAmount;
    }

    public BigDecimal getOldTotalNumber() {
        return oldTotalNumber;
    }

    public void setOldTotalNumber(BigDecimal oldTotalNumber) {
        this.oldTotalNumber = oldTotalNumber;
    }

    public BigDecimal getOldDealPrice() {
        return oldDealPrice;
    }

    public void setOldDealPrice(BigDecimal oldDealPrice) {
        this.oldDealPrice = oldDealPrice;
    }

    public Date getOldPayFullTime() {
        return oldPayFullTime;
    }

    public void setOldPayFullTime(Date oldPayFullTime) {
        this.oldPayFullTime = oldPayFullTime;
    }

    public String getNewFileId() {
        return newFileId;
    }

    public void setNewFileId(String newFileId) {
        this.newFileId = newFileId;
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
