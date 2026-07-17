package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyQuotaV1;
import com.spt.bas.client.entity.BsCompanyVisit;
import com.spt.bas.client.vo.ApplyCompanyVisitVo;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @Author 田起立
 * @Date 2024/5/31 17:09
 * @Description:
 */
public interface IBsCompanyVisitService extends IBaseService<BsCompanyVisit> {
    BsCompanyVisit getCompanyVisitById(Long id);
}
