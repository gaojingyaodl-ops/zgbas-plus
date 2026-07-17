package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.payload.FinanceStatics;
import com.spt.bas.report.client.payload.MatchUser;
import com.spt.bas.report.client.payload.OverdueCompany;
import com.spt.bas.report.client.utils.PageHelper;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * risk 报表
 * @author shengong
 */
public interface IRptRiskReportService {
    /**
     * 业务员业务统计信息
     * @param matchUser
     * @return
     */
    Page<RptMatchUserReport> getMatchUserList(MatchUser matchUser);

    /**
     * 业务员业务统计信息(footer)
     * @param matchUser
     * @return
     */
    RptCountOfMatchUserReport countMatchUserList(MatchUser matchUser);

    /**
     * 获取企业逾期信息列表
     * @param overdueCompany
     * @return
     */
    Page<RptRiskOverdueCompanyReport> getOverdueCompanyList(OverdueCompany overdueCompany);

    /**
     * 获取所有企业逾期信息列表
     * @param overdueCompany
     * @return
     */
    List<RptRiskOverdueCompanyReport> getAllOverdueCompanyList(OverdueCompany overdueCompany);

    /**
     * 获取所有业务员统计信息
     * @param matchUser
     * @return
     */
    List<RptMatchUserReport> getAllMatchUserList(MatchUser matchUser);

    /**
     * 获取企业逾期信息列表(footer)
     * @param overdueCompany
     * @return
     */
    RptCountOfRiskOverdueCompany countOverdueCompanyList(OverdueCompany overdueCompany);

    /**
     * 预算财务统计表
     * @param financeStatics
     * @return
     */
    PageHelper<RptMarginAmountReport> getMarginAmountList(FinanceStatics financeStatics);

    /**
     * 预算财务统计表(单表查询)
     * @param financeStatics
     * @return
     */
    PageHelper<RptMarginAmountReport> getMarginAmountListNew(FinanceStatics financeStatics);
    /**
     * 预算财务统计表
     * @param financeStatics
     * @return
     */
    PageHelper<RptMarginAmountReport> getMarginAmountAllList(FinanceStatics financeStatics);

    /**
     * 预算财务统计表所有行（单表）
     * @param financeStatics
     * @return
     */
     PageHelper<RptMarginAmountReport> getMarginAmountAllNewList(FinanceStatics financeStatics);


    /**
     * 预算财务统计表所有行
     * @param financeStatics
     * @return
     */
    List<RptMarginAmountReport> getAllMarginAmountList(FinanceStatics financeStatics);

    /**
     * 预算决算统计表
     * @param financeStatics
     * @return
     */
    PageHelper<RptFinalAccountReport> getFinalAccountsList(FinanceStatics financeStatics);
    
    /**
     * 预算决算统计表 所有行
     * @param financeStatics
     * @return
     */
    List<RptFinalAccountReport> getAllFinalAccountsList(FinanceStatics financeStatics);

    /**
     * 预算决算统计表 NEW
     * @param financeStatics
     * @return
     */
    PageHelper<RptFinalAccountReportNew> getFinalAccountsNewList(FinanceStatics financeStatics);
    
    /**
     * 预算决算统计表 All
     * @param financeStatics
     * @return
     */
    PageHelper<RptFinalAccountReportNew> getFinalAccountsAllList(FinanceStatics financeStatics);


    /**
     * 获取approveId
     * @param search
     * @return
     */
    List<Long> getApproveIds(FinanceStatics search);
}
