package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptEvaluateTotalSearch;
import com.spt.bas.report.client.vo.RptEvaluateTotalVo;
import org.springframework.data.domain.Page;

public interface IRptEvaluateTotalService {
    Page<RptEvaluateTotalVo> findPageEvaluateTotal(RptEvaluateTotalSearch vo);
}
