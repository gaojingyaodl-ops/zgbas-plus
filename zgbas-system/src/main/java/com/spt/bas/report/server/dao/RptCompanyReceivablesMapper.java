package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptCompanyReceivables;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptCompanyReceivablesSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptCompanyReceivablesMapper {
    
    List<RptCompanyReceivables> findRptCompanyReceivablesPage(RptCompanyReceivablesSearchVo searchVo);
    
    List<RptCompanyReceivables> findRptCompanyReceivablesDetailPage(RptCompanyReceivablesSearchVo searchVo);
    
    RptCompanyReceivables findRptCompanyReceivablesSum(RptCompanyReceivablesSearchVo searchVo);

    /**
     * 根据用户ID查询资金方管理数据
     * @param userId
     * @return
     */
    RptFunderVo selectFunderByUserId(Long userId);
}
