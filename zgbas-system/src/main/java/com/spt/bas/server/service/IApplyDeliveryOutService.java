package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyDeliveryOutVo;
import com.spt.bas.client.vo.ApplyProductDetailVo;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.List;

public interface IApplyDeliveryOutService extends IBaseService<ApplyDeliveryOut> {

    void updateFileId(Long id, String fileId);

    List<ApplyDeliveryOut> findByContractId(Long contractId);

    void updateApplyStatus(Long contractId);

    void doWithdraw(PmApproveWithdrawVo pwVo) throws ApplicationException;

    /**
     * 查询已出库未确认批次信息
     *
     * @param contractId
     *
     * @return
     */
    List<ApplyProductDetailVo> getUnConfirmDeliveryOut(Long contractId);

    /**
     * 查询已出库信息
     * @param contractId
     * @return
     */
    List<ApplyProductDetailVo> getAllDeliveryOut(Long contractId);

    /**
     * 查询中游已出库未确认批次信息
     * @param contractId
     * @return
     */
    List<ApplyProductDetailVo> getUnConfirmDeliveryOutDcsx(Long contractId);

    /**
     * 查询详细
     *
     * @param applyDeliveryOutId
     *
     * @return
     */
    ApplyProductDetail findByApplyDeliveryOutId(Long applyDeliveryOutId);

    ApplyProductDetail findByApplyDeliveryOutApplyNo(String applyNo);

    ApplyDeliveryOut findByApplyNo(String applyNo);

    ApplyDeliveryOut findEntity(Long approveId);

    List<ApplyDeliveryOut> findByContractNo2(String contractNo);

    /**
     * 出库申请
     */
    void applyDeliveryOut(ApplyDeliveryOutVo deliveryOutVo)throws ApplicationException;

    /**
     * 查询有效的出库审批单
     * @param contractId
     * @throws ApplicationException
     */
    List<ApplyDeliveryOut> findByContractIdNoStatusB(Long contractId)throws ApplicationException;

    ApplyDeliveryOut generateApplyNo(Long contractId);

    /**
     * 刷新发货单
     */
    void refreshShippingFile(String contractNo);
}

