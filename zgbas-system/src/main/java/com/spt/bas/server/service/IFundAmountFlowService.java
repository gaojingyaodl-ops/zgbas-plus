package com.spt.bas.server.service;

import com.spt.bas.client.entity.FundAmountFlow;
import com.spt.bas.server.enums.FundFlowEnum;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;

/**
 * @Author MoonLight
 * @Date 2024/7/12 17:51
 * @Version 1.0
 */
public interface IFundAmountFlowService extends IBaseService<FundAmountFlow> {

    FundAmountFlow addFundFlow(String fundCompanyName, String ourCompanyName, BigDecimal flowAmount, FundFlowEnum fundFlowEnum, PmApprove linkApprove) throws ApplicationException;
}
