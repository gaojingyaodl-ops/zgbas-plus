package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyInsurance;
import com.spt.bas.server.service.IApplyInsuranceService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  保险额度申报
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-19 13:38
 */
@RestController
@RequestMapping(value = "apply/insurance")
public class ApplyInsuranceApi extends BaseApi<ApplyInsurance> {

    @Autowired
    private IApplyInsuranceService applyInsuranceService;

    @Override
    public IDataService<ApplyInsurance> getService() {
        return applyInsuranceService;
    }

    @PostMapping("getLatestInsurance")
    public ApplyInsurance getLatestInsurance(@RequestBody Long companyId){
        return applyInsuranceService.getLatestInsurance(companyId);
    }

    @PostMapping("findByCorpSerialNo")
    public ApplyInsurance findByCorpSerialNo(@RequestBody String corpSerialNo){
        return applyInsuranceService.findByCorpSerialNo(corpSerialNo);
    }

    @PostMapping("findTopByCompanyIdAndApplyStatus")
    ApplyInsurance findTopByCompanyIdAndApplyStatus(@RequestParam("companyId") Long companyId, @RequestParam("applyStatus") String applyStatus){
        return applyInsuranceService.findTopByCompanyIdAndApplyStatus(companyId,applyStatus);
    }

    @PostMapping("findByRiskCompName")
    ApplyInsurance findByRiskCompName(@RequestBody String companyName){
        return applyInsuranceService.findByRiskCompName(companyName);
    }

    @PostMapping("findTopByCompanyIdAndStatus")
    ApplyInsurance findTopByCompanyIdAndStatus(@RequestParam("companyId") Long companyId, @RequestParam("status") String status){
        return applyInsuranceService.findTopByCompanyIdAndStatus(companyId, status);
    }
    
    @PostMapping("findTopByCompanyNameAndStatusIsNullOrStatus")
    ApplyInsurance findTopByCompanyNameAndStatusIsNullOrStatus(@RequestParam("riskCompanyName") String riskCompanyName, @RequestParam("status") String status){
        return applyInsuranceService.findTopByCompanyNameAndStatusIsNullOrStatus(riskCompanyName, status);
    }
    
    @PostMapping("findTopByCompanyIdAndStatusIsNull")
    ApplyInsurance findTopByCompanyIdAndStatusIsNull(@RequestParam("companyId") Long companyId){
        return applyInsuranceService.findTopByCompanyIdAndStatusIsNull(companyId);
    }

    @PostMapping("findByCompanyId")
    ApplyInsurance findByCompanyId(@RequestParam("companyId") Long companyId){
        return applyInsuranceService.findByCompanyId(companyId);
    }
    @PostMapping("insuranceApply")
    public void insuranceApply(@RequestBody ApplyInsurance insurance) {
        applyInsuranceService.insuranceApply(insurance);
    }

}
