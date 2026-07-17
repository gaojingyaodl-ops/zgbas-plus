package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptProfitStatisticsSearchVo;

/**
 * 利润表
 */
public interface IRptProfitStatisticsService {
    
    /**
     * 获取利润表数据
     */
    RptProfitStatistics getRptProfitStatistics(RptProfitStatisticsSearchVo searchVo);
}
