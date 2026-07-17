package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptCreditAmountMonitor;
import com.spt.bas.report.client.vo.RptCreditAmountMonitorSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptCreditAmountMonitorMapper {
    
    List<RptCreditAmountMonitor> findCreditAmountMonitorList(RptCreditAmountMonitorSearchVo vo);
    
}
