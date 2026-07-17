package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementAmount;
import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.bas.client.vo.CtrCalCulateParam;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * 合同结算提成表
 *
 * @author MoonLight
 */
public interface ICtrContractSettlementCommissionService extends IBaseService<CtrContractSettlementCommission> {

    /**
     * 初始化保存结算提成数据-货款
     *
     * @param settlementAmount
     */
    CtrContractSettlementCommission initSaveSettlementCommission(CtrContractSettlement settlement, CtrContractSettlementAmount settlementAmount, CtrCalCulateParam param);

    /**
     * 更新合计结算提成数据
     *
     * @param settlement
     */
    CtrContractSettlement refreshSettlementAmount(CtrContractSettlement settlement);

    /**
     * 查询合同结算提成明细
     * @param settlementId
     * @return
     */
    List<CtrContractSettlementCommission> findSettlementCommissionList(Long settlementId);
}
