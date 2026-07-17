package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptCtrContractSearch;
import com.spt.bas.report.client.vo.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRptCtrContractService {
    /**
     * 合同列表
     *
     * @param vo 多查询条件
     */
    Page<RptCtrContractVo> findPageCtrContract(RptCtrContractSearch vo);

    /**
     * 合同详细
     * @param vo
     * @return
     */
    RptCtrContractDetailVo getCtrContract(RptCtrContractSearch vo);

    /**
     * 服务合同详细
     * @param vo
     * @return
     */
    RptCtrServiceContractVo getServiceContract(RptCtrContractSearch vo);

    /**
     * 获取合同的业务操作记录
     *
     * @param contractNo
     * @return
     */
    List<RptContractOperationVo> getContractOperationList(String contractNo);

    /**
     * 获取合同的发货详情
     * @param contractNo
     * @return
     */
    RptCtrWarehouseDetailVo getDeliveryOutDetail(String contractNo);

    /**
     * 获取合同的未发货的详情
     * @param contractNo
     * @return
     */
    RptCtrUnDeliveryOutVo getUndeliveryOutDetail(String contractNo);

    /**
     * 查询支付货款的历史详情
     * @param contractNo
     * @return
     */
    RptCtrPayVo getPayDetail(String contractNo);

    /**
     * 获取合同的付服务费的历史详情
     * @param contractNo
     * @return
     */
    RptCtrPayVo getServicePayDetail(String contractNo);

    /**
     * 获取合同的货款开票的历史详情
     * @param contractNo
     * @return
     */
    RptCtrBillVo getBillDetail(String contractNo);

    /**
     * 获取合同的服务费开票的历史详情
     * @param contractNo
     * @return
     */
    RptCtrBillVo getServiceBillDetail(String contractNo);

    /**
     * 获取合同的确认收货的历史详情
     * @param contractNo
     * @return
     */
    RptCtrConfirmReceiptVo getConfirmReceiptDetail(String contractNo);

}
