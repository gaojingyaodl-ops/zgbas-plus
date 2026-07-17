package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  合同的货款开票的历史详情
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 15:28
 */
public class RptCtrBillVo {
    /**
     * 已付款的金额
     */
    private BigDecimal billedTotalAmount;

    /**
     * 未付款的金额
     */
    private BigDecimal unbilledTotalAmount;

    /**
     * 合同id
     */
    @JsonIgnore
    private Long ctrContractId;

    private List<RptCtrBillDetailVo> billedList;

    public BigDecimal getBilledTotalAmount() {
        return billedTotalAmount;
    }

    public void setBilledTotalAmount(BigDecimal billedTotalAmount) {
        this.billedTotalAmount = billedTotalAmount;
    }

    public BigDecimal getUnbilledTotalAmount() {
        return unbilledTotalAmount;
    }

    public void setUnbilledTotalAmount(BigDecimal unbilledTotalAmount) {
        this.unbilledTotalAmount = unbilledTotalAmount;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }

    public List<RptCtrBillDetailVo> getBilledList() {
        return billedList;
    }

    public void setBilledList(List<RptCtrBillDetailVo> billedList) {
        this.billedList = billedList;
    }
}
