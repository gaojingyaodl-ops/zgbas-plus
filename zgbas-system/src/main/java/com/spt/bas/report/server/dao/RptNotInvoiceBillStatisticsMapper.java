package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptNotInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptNotInvoiceBillStatisticsSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptNotInvoiceBillStatisticsMapper {
    
    List<RptNotInvoiceBillStatistics> findRptNotInvoiceBillStatisticsPage(RptNotInvoiceBillStatisticsSearchVo searchVo);
    
    RptNotInvoiceBillStatistics findRptNotInvoiceBillStatisticsSum(RptNotInvoiceBillStatisticsSearchVo searchVo);

    /**
     * 根据用户ID查询资金方管理数据
     * @param userId
     * @return
     */
    RptFunderVo selectFunderByUserId(Long userId);
}
