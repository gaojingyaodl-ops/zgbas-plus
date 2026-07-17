package com.spt.bas.report.client.entity;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * <p>
 *    for confirmPay
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-19 10:07
 */
public class RptConfirmPayVo {
    /**
     * 销售合同编号
     */
    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    /**
     * 付款金额
     */
    private BigDecimal dealedAmount;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getDealedAmount() {
        return dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
    }
}
