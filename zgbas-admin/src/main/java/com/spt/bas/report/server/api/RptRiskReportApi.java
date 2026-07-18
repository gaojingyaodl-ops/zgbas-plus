package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.payload.FinanceStatics;
import com.spt.bas.report.client.payload.MatchUser;
import com.spt.bas.report.client.payload.OverdueCompany;
import com.spt.bas.report.client.utils.PageHelper;
import com.spt.bas.report.server.service.IRptRiskReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  风控系统报表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-01 14:37
 */
@RestController
@RequestMapping(value = "/risk/report")
public class RptRiskReportApi {

    @Autowired
    private IRptRiskReportService riskReportService;

    /**
     * 业务员的业务统计信息
     *
     * @param matchUser
     * @return
     */
    @PostMapping("getMatchUserList")
    public PageHelper<RptMatchUserReport> getMatchUserList(@RequestBody MatchUser matchUser) {
        matchUser.setPage(1);
        matchUser.setRows(999);
        Page<RptMatchUserReport> page = riskReportService.getMatchUserList(matchUser);
        PageHelper<RptMatchUserReport> r = new PageHelper<>();
        BeanUtils.copyProperties(page, r);
        matchUser.setPage(1);
        matchUser.setRows(1);
        RptCountOfMatchUserReport countOfMatchUserReport = riskReportService.countMatchUserList(matchUser);
        r.setFooter(countOfMatchUserReport);
        return r;
    }

    /**
     * 获取企业逾期信息列表
     * @param overdueCompany
     * @return
     */
    @PostMapping("getOverdueCompanyList")
    public PageHelper<RptRiskOverdueCompanyReport> getOverdueCompanyList(@RequestBody OverdueCompany overdueCompany) {
        Page<RptRiskOverdueCompanyReport> page = riskReportService.getOverdueCompanyList(overdueCompany);
        PageHelper<RptRiskOverdueCompanyReport> r = new PageHelper<>();
        BeanUtils.copyProperties(page, r);
        overdueCompany.setPage(1);
        overdueCompany.setRows(1);
        RptCountOfRiskOverdueCompany countOfRiskOverdueCompany = riskReportService.countOverdueCompanyList(overdueCompany);
        r.setFooter(countOfRiskOverdueCompany);
        return r;
    }

    /**
     * 获取所有逾期信息列表
     * @param overdueCompany
     * @return
     */
    @PostMapping("getAllOverdueCompanyList")
    public List<RptRiskOverdueCompanyReport> getAllOverdueCompanyList(@RequestBody OverdueCompany overdueCompany){
        return riskReportService.getAllOverdueCompanyList(overdueCompany);
    }

    /**
     * 获取所有业务员统计信息
     * @param matchUser
     * @return
     */
    @PostMapping("getAllMatchUserList")
    public List<RptMatchUserReport> getAllMatchUserList(@RequestBody MatchUser matchUser){
        return riskReportService.getAllMatchUserList(matchUser);
    }


    /**
     * 预算财务统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountList")
    public PageHelper<RptMarginAmountReport> getMarginAmountList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getMarginAmountList(financeStatics);
    }
    /**
     * 预算财务统计表(单表)
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountListNew")
    public PageHelper<RptMarginAmountReport> getMarginAmountListNew(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getMarginAmountListNew(financeStatics);
    }

    /**
     * 预算财务统计表 所有记录(新 单表查询)
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountAllNewList")
    public PageHelper<RptMarginAmountReport> getMarginAmountAllNewList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getMarginAmountAllNewList(financeStatics);
    }
    /**
     * 预算财务统计表 所有记录
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountAllList")
    public PageHelper<RptMarginAmountReport> getMarginAmountAllList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getMarginAmountAllList(financeStatics);
    }

    /**
     * 所有预算财务统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getAllMarginAmountList")
    public List<RptMarginAmountReport> getAllMarginAmountList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getAllMarginAmountList(financeStatics);
    }


    /**
     * 预算决算统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getFinalAccountsList")
    public PageHelper<RptFinalAccountReport> getFinalAccountsList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getFinalAccountsList(financeStatics);
    }

    /**
     * 预算决算统计表 New
     * @param financeStatics
     * @return
     */
    @PostMapping("getFinalAccountsNewList")
    public PageHelper<RptFinalAccountReportNew> getFinalAccountsNewList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getFinalAccountsNewList(financeStatics);
    }
    
    /**
     * 预算决算统计表 All
     * @param financeStatics
     * @return
     */
    @PostMapping("getFinalAccountsAllList")
    public PageHelper<RptFinalAccountReportNew> getFinalAccountsAllList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getFinalAccountsAllList(financeStatics);
    }

    /**
     * 所有预算决算统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getAllFinalAccountsList")
    public List<RptFinalAccountReport> getAllFinalAccountsList(@RequestBody FinanceStatics financeStatics){
        return riskReportService.getAllFinalAccountsList(financeStatics);
    }


}
