package com.spt.bas.server.dao;


import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyUserBak;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BsCompanyUserBakDao extends BaseDao<BsCompanyUserBak> {

    @Query("from BsCompanyUserBak a where a.companyId =?1 and a.matchUserId =?2")
    BsCompanyUserBak findByMatchUserIdAndFollowDate(Long companyId,Long matchUserId);

    @Query("from BsCompanyUserBak a where a.matchUserId =?1 and DateDiff(a.matchFollowDate,NOW())=0 ")
    List<BsCompany> getCompanyForDate(Long matchUserId);

    @Query("from BsCompanyUserBak a where a.companyId=?1")
    BsCompanyUserBak findByCompanyId( Long companyId);
}
