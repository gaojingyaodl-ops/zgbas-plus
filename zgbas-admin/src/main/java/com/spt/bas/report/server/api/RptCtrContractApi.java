package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptCtrContractSearch;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.service.IRptCtrContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-09 11:29
 */
@RestController
@RequestMapping(value = "/wx/contract")
public class RptCtrContractApi {

    @Autowired
    private IRptCtrContractService ctrContractService;

    /**
     * 合同列表
     * @param vo
     * @return
     */
    @PostMapping("findPageCtrContract")
    public Page<RptCtrContractVo> findPageCtrContract(@RequestBody RptCtrContractSearch vo){
        Page<RptCtrContractVo> pageCtrContract = ctrContractService.findPageCtrContract(vo);
        return pageCtrContract;
    }

    /**
     * 合同详细
     * @param vo
     * @return
     */
    @PostMapping("getCreditContractDetail")
    public RptCtrContractDetailVo getCreditContractDetail(@RequestBody RptCtrContractSearch vo) {
        return ctrContractService.getCtrContract(vo);
    }

    /**
     * 服务合同详细
     * @param vo
     * @return
     */
    @PostMapping("getServiceContractDetail")
    public RptCtrServiceContractVo getServiceContractDetail(@RequestBody RptCtrContractSearch vo) {
        return ctrContractService.getServiceContract(vo);
    }

    /**
     * 获取合同的业务操作记录
     * @param contractNo
     * @return
     */
    @PostMapping("getContractOperationList")
    public List<RptContractOperationVo> getContractOperationList(@RequestBody String contractNo) {
        return ctrContractService.getContractOperationList(contractNo);
    }

    /**
     * 获取合同的发货详情
     * @param contractNo
     * @return
     */
    @PostMapping("getDeliveryOutDetail")
    public RptCtrWarehouseDetailVo getDeliveryOutDetail(@RequestBody String contractNo) {
        return ctrContractService.getDeliveryOutDetail(contractNo);
    }

    /**
     * 获取合同的未发货的详情
     * @param contractNo
     * @return
     */
    @PostMapping("getUndeliveryOutDetail")
    public RptCtrUnDeliveryOutVo getUndeliveryOutDetail(@RequestBody String contractNo) {
        return ctrContractService.getUndeliveryOutDetail(contractNo);
    }

    /**
     * 查询支付货款的历史详情
     * @param contractNo
     * @return
     */
    @PostMapping("getPayDetail")
    public RptCtrPayVo getPayDetail(@RequestBody String contractNo) {
        return ctrContractService.getPayDetail(contractNo);
    }

    /**
     * 获取合同的付服务费的历史详情
     * @param contractNo
     * @return
     */
    @PostMapping("getServicePayDetail")
    public RptCtrPayVo getServicePayDetail(@RequestBody String contractNo) {
        return ctrContractService.getServicePayDetail(contractNo);
    }

    /**
     * 获取合同的货款开票的历史详情
     * @param contractNo
     * @return
     */
    @PostMapping("getBillDetail")
    public RptCtrBillVo getBillDetail(@RequestBody String contractNo) {
        return ctrContractService.getBillDetail(contractNo);
    }

    /**
     * 获取合同的货款开票的历史详情
     * @param contractNo
     * @return
     */
    @PostMapping("getServiceBillDetail")
    public RptCtrBillVo getServiceBillDetail(@RequestBody String contractNo) {
        return ctrContractService.getServiceBillDetail(contractNo);
    }

    /**
     * 获取合同的确认收货的历史详情
     * @param contractNo
     * @return
     */
    @PostMapping("getConfirmReceiptDetail")
    public RptCtrConfirmReceiptVo getConfirmReceiptDetail(@RequestBody String contractNo) {
        return ctrContractService.getConfirmReceiptDetail(contractNo);
    }


}
