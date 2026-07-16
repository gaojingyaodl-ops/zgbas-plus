package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyCompanyCredit;
import com.spt.bas.client.entity.ApplyCompanyInfo;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

public interface ApplyCompanyCreditDao extends BaseDao<ApplyCompanyCredit> {

    @Query("from ApplyCompanyCredit bc where bc.type=?1 and  bc.companyId=?2 and bc.creditType=?3")
    ApplyCompanyCredit findByCompanyIdAndType(String type, Long companyId, String creditType);
}
