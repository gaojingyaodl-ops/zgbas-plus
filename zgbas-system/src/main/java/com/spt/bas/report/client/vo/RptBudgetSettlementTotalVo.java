package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.math.BigDecimal;


public class RptBudgetSettlementTotalVo extends PageSearchVo {


    /**
     * 业务员
     */
    private Long matchUserId;
    private String matchUserName;

    /**
     * 赊销 销售提成
     */
    private BigDecimal sellMatchAmount = BigDecimal.ZERO;
    /**
     * 赊销 采购提成
     */
    private BigDecimal buyMatchAmount = BigDecimal.ZERO;
    /**
     * 赊销 销售负责人提成
     */
    private BigDecimal sellHeadCommissionAmount = BigDecimal.ZERO;
    /**
     * 赊销 采购负责人提成
     */
    private BigDecimal buyHeadCommissionAmount = BigDecimal.ZERO;

    /**
     * 结算日期
     */

    private String settlementDate;

    private Boolean matchCreditFlg;

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
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

    public BigDecimal getSellMatchAmount() {
        return sellMatchAmount;
    }

    public void setSellMatchAmount(BigDecimal sellMatchAmount) {
        this.sellMatchAmount = sellMatchAmount;
    }

    public BigDecimal getBuyMatchAmount() {
        return buyMatchAmount;
    }

    public void setBuyMatchAmount(BigDecimal buyMatchAmount) {
        this.buyMatchAmount = buyMatchAmount;
    }

    public BigDecimal getSellHeadCommissionAmount() {
        return sellHeadCommissionAmount;
    }

    public void setSellHeadCommissionAmount(BigDecimal sellHeadCommissionAmount) {
        this.sellHeadCommissionAmount = sellHeadCommissionAmount;
    }

    public BigDecimal getBuyHeadCommissionAmount() {
        return buyHeadCommissionAmount;
    }

    public void setBuyHeadCommissionAmount(BigDecimal buyHeadCommissionAmount) {
        this.buyHeadCommissionAmount = buyHeadCommissionAmount;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }
}
