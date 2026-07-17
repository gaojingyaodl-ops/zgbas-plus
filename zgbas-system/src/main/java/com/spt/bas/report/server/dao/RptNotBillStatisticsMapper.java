package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptNotBillStatistics;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptNotBillStatisticsSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptNotBillStatisticsMapper {
    
    List<RptNotBillStatistics> findRptNotBillStatisticsPage(RptNotBillStatisticsSearchVo searchVo);
    
    RptNotBillStatistics findRptNotBillStatisticsSum(RptNotBillStatisticsSearchVo searchVo);

    /**
     * 根据用户ID查询资金方管理数据
     * @param userId
     * @return
     */
    RptFunderVo selectFunderByUserId(Long userId);
}
