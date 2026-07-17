package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementAmount;
import com.spt.bas.client.vo.CtrCalCulateParam;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import sun.net.www.ApplicationLaunchException;

import java.util.List;

/**
 * 合同结算金额表
 *
 * @author MoonLight
 */
public interface ICtrContractSettlementAmountService extends IBaseService<CtrContractSettlementAmount> {

    /**
     * 初始化保存结算金额数据-货款
     *
     * @param settlement
     */
    void initSaveSettlementAmount(CtrContractSettlement settlement, CtrCalCulateParam param);

    /**
     * 初始化保存结算金额数据-逾期罚息
     *
     * @param receive
     * @param invalidFlg
     */
    void initSaveBreachSettlementAmount(ApplyReceive receive, Boolean invalidFlg) throws ApplicationException;

    /**
     * 更新合同结算表，已结算金额、待结算金额
     *
     * @param settlement
     */
    CtrContractSettlement refreshSettlementAmount(CtrContractSettlement settlement);

    /**
     * 结算 结算单
     * @param settlementIds
     */
    void makeComplete(List<Long> settlementIds);

    /**
     * 补偿结算单收逾期罚息提成
     * @param contractNo
     */
    void refreshBreachCommission(String contractNo);
}
