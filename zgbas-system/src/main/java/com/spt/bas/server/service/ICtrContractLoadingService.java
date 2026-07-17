package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContractLoading;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/3/16 9:28
 * @version: 1.0
 * @description:
 */
public interface ICtrContractLoadingService extends IBaseService<CtrContractLoading> {

    /**
     * 生成电子签合同
     * @param loadingId
     * @return
     */
    CtrContractLoading axqLoadingBill(Long loadingId) throws ApplicationException;

    /**
     * 刷新提货单状态
     * @param loadingId
     */
    CtrContractLoading refreshLoadingBillStatus(Long loadingId);

    /**
     * 刷新提货单状态
     * @param contractNo
     */
    CtrContractLoading refreshLoadingBillByContractNo(String contractNo);

    /**
     * 查询盖章审批单
     * @param searchVo
     * @return
     */
    List<PmApprove> findSealUsageApprove(PmApproveSearchVo searchVo);
}
