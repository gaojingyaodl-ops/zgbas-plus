package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import org.springframework.data.domain.Page;

public interface IRptFundReceivableStatisticsService {
    Page<RptFundReceivableStatistics> findPage(RptFundReceivableStatisticsVo searchVo);
}
