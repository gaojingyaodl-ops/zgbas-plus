package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyInsurance;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 保险申报
 *
 * @author shengong
 */
public interface ApplyInsuranceDao extends BaseDao<ApplyInsurance> {
    ApplyInsurance getTopByCompanyIdOrderByCreatedDateDesc(Long companyId);

    ApplyInsurance findByCorpSerialNo(String corpSerialNo);

    ApplyInsurance findByCompanyIdAndApplyStatus(Long companyId, String applyStatus);

    ApplyInsurance findByRiskCompName(String companyName);

    @Query("select a from ApplyInsurance a where a.companyId = ?1 and a.status is null order by a.createdDate desc")
    List<ApplyInsurance> findTopByCompanyIdAndStatusIsNull(Long companyId);

    @Query("select a from ApplyInsurance a where a.riskCompName = ?1 and (a.status is null or a.status =?2) order by a.createdDate desc")
    List<ApplyInsurance> findTopByCompanyNameAndStatusIsNullOrStatus(String riskCompanyName, String status);
    
    ApplyInsurance findTopByCompanyIdAndStatusOrderByCreatedDateDesc(Long companyId, String status);

    ApplyInsurance findTopByCompanyIdOrderByCreatedDateDesc(Long companyId);
    
    ApplyInsurance findTopByCompanyIdAndApplyStatusOrderByCreatedDateDesc(Long companyId, String applyStatus);
}
