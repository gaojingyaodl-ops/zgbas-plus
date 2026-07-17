package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptCountOfRiskOverdueCompany;
import com.spt.bas.report.client.entity.RptRiskOverdueCompanyReport;
import com.spt.bas.report.client.payload.OverdueCompany;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * 企业统计
 * @author shengong
 */
@MyBatisDao
public interface RptRiskCompanyReportMapper {
    /**
     * 企业逾期统计信息
     * @param overdueCompany
     * @return
     */
    List<RptRiskOverdueCompanyReport> getOverdueCompanyList(OverdueCompany overdueCompany);

    /**
     * 企业逾期统计信息(footer)
     * @param overdueCompany
     * @return
     */
    RptCountOfRiskOverdueCompany countOverdueCompanyList(OverdueCompany overdueCompany);

}
