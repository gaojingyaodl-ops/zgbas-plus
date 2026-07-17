package com.spt.bas.server.service;

import com.spt.bas.client.constant.CreditFlowEnum;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyCreditFlow;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;

public interface IBsCompanyCreditFlowService extends IBaseService<BsCompanyCreditFlow> {

    /**
     * 更新客户审批占用额度
     *
     * @param approveNo  审批单编号
     * @param company    客户
     * @param flowAmount 变动金额
     * @param flowEnum   流水类型
     */
    void updateApproveCreditAmount(String approveNo, BsCompany company, BigDecimal flowAmount, CreditFlowEnum flowEnum);

    /**
     * 更新客户已使用额度
     *
     * @param approveNo  审批单编号
     * @param company    客户
     * @param flowAmount 变动金额
     * @param flowEnum   流水类型
     */
    void updateUsedCreditAmount(String approveNo, BsCompany company, BigDecimal flowAmount, CreditFlowEnum flowEnum);

    /**
     * 更新客户已使用额度
     *
     * @param approve         审批单
     * @param companyCreditId 授信额度ID
     * @param flowAmount      变动金额
     * @param flowEnum        流水类型
     */
    void updateUsedCreditAmount(PmApprove approve, Long companyCreditId, BigDecimal flowAmount, CreditFlowEnum flowEnum);
}

