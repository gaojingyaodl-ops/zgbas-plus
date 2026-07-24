package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.service.IContractService;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.remote.IRptWxCtrContractClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * <p>
 * 合同信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-09 09:13
 */
@RestController
@RequestMapping(value = "/wx/contract")
@Api(tags = "微信小程序用户合同信息")
@ApiSort(value = 5)
public class ContractController {

    @Autowired
    private IRptWxCtrContractClient ctrContractClient;

    @Autowired
    private IContractService contractService;

    @ApiOperation(value = "4.1.1获取白条与代采合同信息列表")
    @PostMapping("/getContractList")
    @ApiOperationSupport(order = 1)
    public ApiResult getContractList(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.findPageCtrContract(ctrContractSearch));
    }

    @ApiOperation(value = "4.1.2获取销售合同详情")
    @PostMapping("/getCreditContractDetail")
    @ApiOperationSupport(order = 2)
    public ApiResult getCreditContractDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getCreditContractDetail(ctrContractSearch));
    }

    @ApiOperation(value = "4.1.3获取白条服务费合同详情")
    @PostMapping("/getServiceContractDetail")
    @ApiOperationSupport(order = 3)
    public ApiResult getServiceContractDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getServiceContractDetail(ctrContractSearch));
    }

    @ApiOperation(value = "4.1.4获取合同的业务操作记录")
    @PostMapping("/getContractOperationList")
    @ApiOperationSupport(order = 4)
    public ApiResult getContractOperationList(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getContractOperationList(ctrContractSearch.getContractNo()));
    }

    @ApiOperation(value = "4.1.5获取合同的发货详情")
    @PostMapping("/getDeliveryOutDetail")
    @ApiOperationSupport(order = 5)
    public ApiResult getDeliveryOutDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getDeliveryOutDetail(ctrContractSearch.getContractNo()));
    }

    @ApiOperation(value = "4.1.6获取合同的未发货的详情")
    @PostMapping("/getUndeliveryOutDetail")
    @ApiOperationSupport(order = 6)
    public ApiResult getUndeliveryOutDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getUndeliveryOutDetail(ctrContractSearch.getContractNo()));
    }

    @ApiOperation(value = "4.1.7查询支付货款的历史详情")
    @PostMapping("/getPayDetail")
    @ApiOperationSupport(order = 7)
    public ApiResult getPayDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getPayDetail(ctrContractSearch.getContractNo()));
    }

    @ApiOperation(value = "4.1.8获取合同的付服务费的历史详情")
    @PostMapping("/getServicePayDetail")
    @ApiOperationSupport(order = 8)
    public ApiResult getServicePayDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getServicePayDetail(ctrContractSearch.getContractNo()));
    }

    @ApiOperation(value = "4.1.9获取合同的货款开票的历史详情")
    @PostMapping("/getBillDetail")
    @ApiOperationSupport(order = 9)
    public ApiResult getBillDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getBillDetail(ctrContractSearch.getContractNo()));
    }

    @ApiOperation(value = "4.1.10获取合同的服务费开票的历史详情")
    @PostMapping("/getServiceBillDetail")
    @ApiOperationSupport(order = 10)
    public ApiResult getServiceBillDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getServiceBillDetail(ctrContractSearch.getContractNo()));
    }

    @ApiOperation(value = "4.1.13.获取合同的确认收货的历史详情")
    @PostMapping("/getConfirmReceiptDetail")
    @ApiOperationSupport(order = 13)
    public ApiResult getConfirmReceiptDetail(@RequestBody RptCtrContractSearch ctrContractSearch) {
        ctrContractSearch.setUserId(UserHelper.getCurUserId());
        return ApiResult.ofSuccess(ctrContractClient.getConfirmReceiptDetail(ctrContractSearch.getContractNo()));
    }


    // ==================4.2.合同操作=====================================================================================

    @ApiOperation(value = "4.2.1.确认收货")
    @PostMapping("/confirmReceipt")
    @ApiOperationSupport(order = 14)
    public ApiResult confirmReceipt(@Valid @RequestBody RptConfirmReceiptVo confirmReceiptVo) {
        contractService.confirmReceipt(confirmReceiptVo);
        return ApiResult.ofSuccess();
    }

    @ApiOperation(value = "4.2.2.申请发货")
    @PostMapping("/applyDeliveryOut")
    @ApiOperationSupport(order = 15)
    public ApiResult applyDeliveryOut(@Valid @RequestBody RptApplyDeliveryOutPayload applyDeliveryOutPayload) {
        contractService.applyDeliveryOut(applyDeliveryOutPayload);
        return ApiResult.ofSuccess();
    }

    @ApiOperation(value = "4.2.3.支付货款")
    @PostMapping("/confirmPay")
    @ApiOperationSupport(order = 16)
    public ApiResult confirmPay(@Valid @RequestBody RptConfirmPayVo confirmPayVo) {
        contractService.confirmPay(confirmPayVo);
        return ApiResult.ofSuccess();
    }

    @ApiOperation(value = "4.2.4.支付服务费")
    @PostMapping("/confirmServicePay")
    @ApiOperationSupport(order = 17)
    public ApiResult confirmServicePay(@Valid @RequestBody RptConfirmPayVo confirmPayVo) {
        contractService.confirmServicePay(confirmPayVo);
        return ApiResult.ofSuccess();
    }

    @ApiOperation(value = "4.2.5.申请开票")
    @PostMapping("/applyBill")
    @ApiOperationSupport(order = 18)
    public ApiResult applyBill(@Valid @RequestBody RptApplyBillVo applyBillVo) {
        contractService.applyBill(applyBillVo);
        return ApiResult.ofSuccess();
    }


}
