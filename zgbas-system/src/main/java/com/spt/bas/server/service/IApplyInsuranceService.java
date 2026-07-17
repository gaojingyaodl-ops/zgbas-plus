package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInsurance;
import com.spt.tools.jpa.service.IBaseService;

/**
 * 保险额度申报
 * @author shengong
 */
public interface IApplyInsuranceService extends IBaseService<ApplyInsurance> {
    ApplyInsurance getLatestInsurance(Long companyId);

    /**
     * 保险资料审批
     */
    void insuranceApply(ApplyInsurance insurance);

    ApplyInsurance findByCorpSerialNo(String corpSerialNo);

    ApplyInsurance findTopByCompanyIdAndApplyStatus(Long companyId, String applyStatus);

    ApplyInsurance findByRiskCompName(String companyName);

    ApplyInsurance findTopByCompanyIdAndStatus(Long companyId, String status);
    
    ApplyInsurance findTopByCompanyNameAndStatusIsNullOrStatus(String riskCompanyName, String status);
    
    ApplyInsurance findTopByCompanyIdAndStatusIsNull(Long companyId);

    ApplyInsurance findByCompanyId(Long companyId);



}
