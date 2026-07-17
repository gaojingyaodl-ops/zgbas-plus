package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  支付货款历史详情
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 14:40
 */
public class RptCtrPayVo {
    /**
     * 已付款的金额
     */
    private BigDecimal dealedTotalAmount;

    /**
     * 未付款的金额
     */
    private BigDecimal undealedTotalAmount;

    /**
     * 合同id
     */
    @JsonIgnore
    private Long ctrContractId;

    private List<RptCtrPayDetailVo> dealedList;

    public BigDecimal getDealedTotalAmount() {
        return dealedTotalAmount;
    }

    public void setDealedTotalAmount(BigDecimal dealedTotalAmount) {
        this.dealedTotalAmount = dealedTotalAmount;
    }

    public BigDecimal getUndealedTotalAmount() {
        return undealedTotalAmount;
    }

    public void setUndealedTotalAmount(BigDecimal undealedTotalAmount) {
        this.undealedTotalAmount = undealedTotalAmount;
    }

    public List<RptCtrPayDetailVo> getDealedList() {
        return dealedList;
    }

    public void setDealedList(List<RptCtrPayDetailVo> dealedList) {
        this.dealedList = dealedList;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }
}
