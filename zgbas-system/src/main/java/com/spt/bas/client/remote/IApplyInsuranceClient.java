package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInsurance;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 保险申报
 *
 * @author shengong
 */
@FeignClient(qualifier = "applyInsuranceClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/insurance", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyInsuranceClient extends BaseClient<ApplyInsurance> {

    @PostMapping("getLatestInsurance")
    ApplyInsurance getLatestInsurance(@RequestBody Long companyId);

    @PostMapping("insuranceApply")
    void insuranceApply(@RequestBody ApplyInsurance insurance);

    @PostMapping("findByCorpSerialNo")
    ApplyInsurance findByCorpSerialNo(@RequestBody String corpSerialNo);

    @PostMapping("findTopByCompanyIdAndApplyStatus")
    ApplyInsurance findTopByCompanyIdAndApplyStatus(@RequestParam("companyId") Long companyId, @RequestParam("applyStatus") String applyStatus);

    @PostMapping(value = "findByRiskCompName", consumes = "application/json;charset=UTF-8")
    ApplyInsurance findByRiskCompName(@RequestBody String companyName);

    @PostMapping("findTopByCompanyIdAndStatus")
    ApplyInsurance findTopByCompanyIdAndStatus(@RequestParam("companyId") Long companyId, @RequestParam("status") String status);

    @PostMapping("findTopByCompanyNameAndStatusIsNullOrStatus")
    ApplyInsurance findTopByCompanyNameAndStatusIsNullOrStatus(@RequestParam("riskCompanyName") String riskCompanyName, @RequestParam("status") String status);
    
    @PostMapping("findTopByCompanyIdAndStatusIsNull")
    ApplyInsurance findTopByCompanyIdAndStatusIsNull(@RequestParam("companyId") Long companyId);

    @PostMapping("findByCompanyId")
    ApplyInsurance findByCompanyId(@RequestParam("companyId") Long companyId);




}
