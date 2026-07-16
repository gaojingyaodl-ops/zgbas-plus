package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrServiceContract;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-26 15:46
 */
public class CtrServiceContractChooseVo extends CtrServiceContract {
    private static final long serialVersionUID = 7140054781041629197L;
    /**
     * 服务费申请金额
     */
    private BigDecimal applyServiceAmount;

    /**
     * 开票申请金额
     */
    private BigDecimal applyBillAmount;

    public BigDecimal getApplyServiceAmount() {
        return applyServiceAmount;
    }

    public void setApplyServiceAmount(BigDecimal applyServiceAmount) {
        this.applyServiceAmount = applyServiceAmount;
    }

    public BigDecimal getApplyBillAmount() {
        return applyBillAmount;
    }

    public void setApplyBillAmount(BigDecimal applyBillAmount) {
        this.applyBillAmount = applyBillAmount;
    }
}
