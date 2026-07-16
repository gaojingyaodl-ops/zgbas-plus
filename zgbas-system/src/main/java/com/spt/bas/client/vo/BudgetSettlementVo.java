package com.spt.bas.client.vo;

import com.spt.bas.client.entity.BudgetSettlement;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-30 17:46
 */
public class BudgetSettlementVo extends BudgetSettlement {


    /**
     * 逾期服务费
     */
    private BigDecimal overdueAmount;

    /**
     * 已收逾期服务费
     */
    private BigDecimal receiveOverdueAmount;

    /**
     * 业务员罚金
     */
    private BigDecimal fineOfSalesman;

    /**
     * 决算时间
     */
    private Date updatedDate;


    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public BigDecimal getReceiveOverdueAmount() {
        return receiveOverdueAmount;
    }

    public void setReceiveOverdueAmount(BigDecimal receiveOverdueAmount) {
        this.receiveOverdueAmount = receiveOverdueAmount;
    }

    public BigDecimal getFineOfSalesman() {
        return fineOfSalesman;
    }

    public void setFineOfSalesman(BigDecimal fineOfSalesman) {
        this.fineOfSalesman = fineOfSalesman;
    }

    public Date getUpdatedDate() { return updatedDate; }

    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
}
