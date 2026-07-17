package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractSearch;
import com.spt.bas.report.client.utils.PageHelper;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 采购管家合同信息服务
 */
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/wx/contract",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptWxCtrContractClient {

    /**
     * 合同列表
     * @param search
     * @return
     */
    @PostMapping("findPageCtrContract")
    PageHelper<RptCtrContractVo> findPageCtrContract(@RequestBody RptCtrContractSearch search);

    /**
     * 合同详细
     * @param search
     * @return
     */
    @PostMapping("getCreditContractDetail")
    RptCtrContractDetailVo getCreditContractDetail(@RequestBody RptCtrContractSearch search);

    /**
     * 服务合同详细
     * @param search
     * @return
     */
    @PostMapping("getServiceContractDetail")
    RptCtrServiceContractVo getServiceContractDetail(@RequestBody RptCtrContractSearch search);

    /**
     * 获取合同的业务操作记录
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getContractOperationList")
    List<RptContractOperationVo> getContractOperationList(@RequestBody String contractNo);

    /**
     * 获取合同的发货详情
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getDeliveryOutDetail")
    RptCtrWarehouseDetailVo getDeliveryOutDetail(@RequestBody String contractNo);

    /**
     * 获取合同的未发货的详情
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getUndeliveryOutDetail")
    RptCtrUnDeliveryOutVo getUndeliveryOutDetail(@RequestBody String contractNo);

    /**
     * 查询支付货款的历史详情
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getPayDetail")
    RptCtrPayVo getPayDetail(@RequestBody String contractNo);

    /**
     * 获取合同的付服务费的历史详情
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getServicePayDetail")
    RptCtrPayVo getServicePayDetail(@RequestBody String contractNo);

    /**
     * 获取合同的货款开票的历史详情
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getBillDetail")
    RptCtrBillVo getBillDetail(@RequestBody String contractNo);

    /**
     * 获取合同的服务费开票的历史详情
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getServiceBillDetail")
    RptCtrBillVo getServiceBillDetail(@RequestBody String contractNo);

    /**
     * 获取合同的确认收货的历史详情
     * @param contractNo 销售合同编号
     * @return
     */
    @PostMapping("getConfirmReceiptDetail")
    RptCtrConfirmReceiptVo getConfirmReceiptDetail(@RequestBody String contractNo);

}
