package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyUserBak;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IBsCompanyUserBakService extends IBaseService<BsCompanyUserBak> {
    /**
     * 查询领用人id和时间
     */
    BsCompanyUserBak findByMatchUserIdAndFollowDate(Long companyId,Long matchUserId);


    List<BsCompany> getCompanyForDate(@RequestBody Long matchUserId);

    BsCompanyUserBak findByCompanyId( Long companyId);

}
