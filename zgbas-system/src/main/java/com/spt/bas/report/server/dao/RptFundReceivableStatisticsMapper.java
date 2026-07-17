package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptFundReceivableStatisticsMapper {
    List<RptFundReceivableStatistics> findPage(RptFundReceivableStatisticsVo searchVo);
}
