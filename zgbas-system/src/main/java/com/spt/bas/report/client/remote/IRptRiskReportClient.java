package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.payload.FinanceStatics;
import com.spt.bas.report.client.payload.MatchUser;
import com.spt.bas.report.client.payload.OverdueCompany;
import com.spt.bas.report.client.utils.PageHelper;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author shengong
 */
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/risk/report",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptRiskReportClient {

    /**
     * 业务员的业务统计信息
     *
     * @param matchUser
     * @return
     */
    @PostMapping("getMatchUserList")
    PageHelper<RptMatchUserReport> getMatchUserList(@RequestBody MatchUser matchUser);

    /**
     * 获取企业逾期信息列表
     * @param overdueCompany
     * @return
     */
    @PostMapping("getOverdueCompanyList")
    PageHelper<RptRiskOverdueCompanyReport> getOverdueCompanyList(@RequestBody OverdueCompany overdueCompany);

    /**
     * 获取所有逾期信息列表
     * @param overdueCompany
     * @return
     */
    @PostMapping("getAllOverdueCompanyList")
    List<RptRiskOverdueCompanyReport> getAllOverdueCompanyList(@RequestBody OverdueCompany overdueCompany);

    /**
     * 获取所有业务员统计信息
     * @param matchUser
     * @return
     */
    @PostMapping("getAllMatchUserList")
    List<RptMatchUserReport> getAllMatchUserList(@RequestBody MatchUser matchUser);

    /**
     * 预算财务统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountList")
    PageHelper<RptMarginAmountReport> getMarginAmountList(@RequestBody FinanceStatics financeStatics);
    /**
     * 预算财务统计表(单表)
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountListNew")
    PageHelper<RptMarginAmountReport> getMarginAmountListNew(@RequestBody FinanceStatics financeStatics);

    /**
     * 预算财务统计表 所有记录（新  单表查询）
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountAllNewList")
    PageHelper<RptMarginAmountReport> getMarginAmountAllNewList(@RequestBody FinanceStatics financeStatics);

    /**
     * 预算财务统计表 所有记录
     * @param financeStatics
     * @return
     */
    @PostMapping("getMarginAmountAllList")
    PageHelper<RptMarginAmountReport> getMarginAmountAllList(@RequestBody FinanceStatics financeStatics);

    /**
     * 所有预算财务统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getAllMarginAmountList")
    List<RptMarginAmountReport> getAllMarginAmountList(@RequestBody FinanceStatics financeStatics);


    /**
     * 预算决算统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getFinalAccountsList")
    PageHelper<RptFinalAccountReport> getFinalAccountsList(@RequestBody FinanceStatics financeStatics);
    
    /**
     * 预算决算统计表 NEW
     * @param financeStatics
     * @return
     */
    @PostMapping("getFinalAccountsNewList")
    PageHelper<RptFinalAccountReportNew> getFinalAccountsNewList(@RequestBody FinanceStatics financeStatics);
    
    /**
     * 预算决算统计表 All
     * @param financeStatics
     * @return
     */
    @PostMapping("getFinalAccountsAllList")
    PageHelper<RptFinalAccountReportNew> getFinalAccountsAllList(@RequestBody FinanceStatics financeStatics);

    /**
     * 所有预算决算统计表
     * @param financeStatics
     * @return
     */
    @PostMapping("getAllFinalAccountsList")
    List<RptFinalAccountReport> getAllFinalAccountsList(@RequestBody FinanceStatics financeStatics);

}

