package com.spt.bas.client.remote;

import com.spt.bas.client.common.ApiResult;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApiExternalHis;
import com.spt.bas.client.entity.ApplyBusinessPay;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.risk.BusinessLedgerVo;
import com.spt.bas.client.vo.risk.PiccApplyVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/risk/apply", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IRiskApplyClient extends BaseClient<ApiExternalHis> {

    @PostMapping(value = "applyFreightSettlementPay")
    void applyFreightSettlementPay(@RequestBody ApplyBusinessPay applyBusinessPay);

    /**
     * 重新计算数据评分
     * @param searchVo
     */
    @PostMapping("/calcCompanyDataScore")
    void calcCompanyDataScore(@RequestBody CompanySearchVo searchVo);

    /**
     * 白条准入申请
     *
     * @param creditRating 参数
     */
    @PostMapping("/applyAllowedApprove")
    void applyAllowedApprove(@RequestBody CreditRatingVo creditRating);

    /**
     * 终端工厂-申报保险申请
     *
     * @param payload 参数
     */
    @PostMapping("/sendPiccInsurance")
    void sendPiccInsurance(@RequestBody SendPiccInsurancePayloadVo payload);

    /**
     * 终端工厂-申报保险申请
     *
     * @param quotaVo 参数
     */
    @PostMapping("/applyQuataApprove")
    void applyQuataApprove(@RequestBody ApplyQuotaVo quotaVo);

    /**
     * 终端工厂-申报上浮额度审批
     *
     * @param param 参数
     */
    @PostMapping("/applyFloatingRateApprove")
    void applyFloatingRateApprove(@RequestBody FloatingRateApproveVo param);

    /**
     * 终端工厂-申请修改服务费率
     *
     * @param param 参数
     */
    @PostMapping("/applyRateApprove")
    void applyRateApprove(@RequestBody ApplyRateVo param);

    /**
     * 终端工厂-申请修改服务费率
     *
     * @param param 参数
     */
    @PostMapping("/applyCreditDeliveryApprove")
    void applyCreditDeliveryApprove(@RequestBody CreditDeliveryVo param);

    /**
     * 终端工厂-供应商准入申请
     *
     * @param param 参数
     */
    @PostMapping("/supplierApply")
    void supplierApply(@RequestBody SupplierApplyVo param);

    /**
     * 获取估图免登录链接
     * @return
     */
    @PostMapping("/getGuTuTokenKeyLink")
    String getGuTuTokenKeyLink();

    /**
     * 访厂报告-申请
     *
     * @param applyCompanyVisitVo 参数
     */
    @PostMapping("/applyCompanyVisitApprove")
    void applyCompanyVisitApprove(@RequestBody ApplyCompanyVisitVo applyCompanyVisitVo);
    /**
     * 终端工厂-供应商配送申请
     *
     * @param param 参数
     */
    @PostMapping("/supplierDelivery")
    void supplierDelivery(@RequestBody SupplierApplyVo param);

    /**
     * 人保赊销申请
     *
     * @param vo 参数
     */
    @PostMapping("/sendPiccCredit")
    ApiResult sendPiccCredit(@RequestBody PiccApplyVo vo);

    /**
     * 人保回款申请
     *
     * @param vo 参数
     */
    @PostMapping("/sendPiccRecover")
    ApiResult sendPiccRecover(@RequestBody PiccApplyVo vo);

    /**
     * 刷新中游逾期罚息
     * @param businessLedgerVo
     */
    @PostMapping("refreshOverdueInterest")
    void refreshOverdueInterest(@RequestBody BusinessLedgerVo businessLedgerVo) throws Exception;

    @PostMapping("refreshProfitData")
    void refreshProfitData(@RequestBody List<String> approveNoList);

    @PostMapping("/applyPlasticTypeApprove")
    void applyPlasticTypeApprove(@RequestBody ApplyPlasticTypeVo plasticTypeVo);
}

