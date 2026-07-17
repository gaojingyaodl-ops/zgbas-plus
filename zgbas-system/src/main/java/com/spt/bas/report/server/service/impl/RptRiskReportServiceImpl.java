package com.spt.bas.report.server.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.payload.FinanceStatics;
import com.spt.bas.report.client.payload.MatchUser;
import com.spt.bas.report.client.payload.OverdueCompany;
import com.spt.bas.report.client.utils.PageHelper;
import com.spt.bas.report.server.dao.RptRiskCompanyReportMapper;
import com.spt.bas.report.server.dao.RptRiskFinanceReportMapper;
import com.spt.bas.report.server.dao.RptRiskMatchUserReportMapper;
import com.spt.bas.report.server.service.IRptRiskReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-01 14:43
 */
@Component
public class RptRiskReportServiceImpl implements IRptRiskReportService {

    @Autowired
    private RptRiskMatchUserReportMapper riskMatchUserReportMapper;

    @Autowired
    private RptRiskCompanyReportMapper riskCompanyReportMapper;

    @Autowired
    private RptRiskFinanceReportMapper riskFinanceReportMapper;

    @Resource
    private IAuthOpenFacade authOpenFacade;

    /**
     * 业务员业务统计信息
     *
     * @param matchUser
     * @return
     */
    @Override
    public Page<RptMatchUserReport> getMatchUserList(MatchUser matchUser) {
        // 查询事业部下所有人
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(221L);
        deptIds.add(223L);
        deptIds.add(229L);
        List<SysUserSdk> userList = authOpenFacade.findByDeptIds(deptIds);
        List<Long> userIds = userList.stream().map(SysUserSdk::getUserId).collect(Collectors.toList());
        matchUser.setMatchUserIds(userIds);
        List<RptMatchUserReport> matchUserList = riskMatchUserReportMapper.getMatchUserList(matchUser);
        Pageable pageable = PageRequest.of(matchUser.getPage() - 1, matchUser.getRows());
        Page<RptMatchUserReport> pageVo = new PageImpl<>(matchUserList, pageable, matchUser.getCount());
        return pageVo;
    }

    /**
     * 业务员业务统计信息(footer)
     *
     * @param matchUser
     * @return
     */
    @Override
    public RptCountOfMatchUserReport countMatchUserList(MatchUser matchUser) {
        return riskMatchUserReportMapper.countMatchUserList(matchUser);
    }

    /**
     * 获取企业逾期信息列表
     *
     * @param overdueCompany
     * @return
     */
    @Override
    public Page<RptRiskOverdueCompanyReport> getOverdueCompanyList(OverdueCompany overdueCompany) {
        List<RptRiskOverdueCompanyReport> list = riskCompanyReportMapper.getOverdueCompanyList(overdueCompany);
        Pageable pageable = PageRequest.of(overdueCompany.getPage() - 1, overdueCompany.getRows());
        Page<RptRiskOverdueCompanyReport> pageVo = new PageImpl<>(list, pageable, overdueCompany.getCount());
        return pageVo;
    }

    /**
     * 获取所有企业逾期信息列表
     *
     * @param overdueCompany
     * @return
     */
    @Override
    public List<RptRiskOverdueCompanyReport> getAllOverdueCompanyList(OverdueCompany overdueCompany) {
        overdueCompany.setRows(9999);
        overdueCompany.setPage(1);
        return riskCompanyReportMapper.getOverdueCompanyList(overdueCompany);
    }

    /**
     * 获取所有业务员统计信息
     *
     * @param matchUser
     * @return
     */
    @Override
    public List<RptMatchUserReport> getAllMatchUserList(MatchUser matchUser) {
        matchUser.setRows(9999);
        matchUser.setPage(1);
        // 查询事业部下所有人
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(221L);
        deptIds.add(223L);
        deptIds.add(229L);
        List<SysUserSdk> byDeptIds = authOpenFacade.findByDeptIds(deptIds);
        List<Long> userIds = byDeptIds.stream().map(SysUserSdk::getUserId).collect(Collectors.toList());
        matchUser.setMatchUserIds(userIds);
        return riskMatchUserReportMapper.getMatchUserList(matchUser);
    }

    /**
     * 获取企业逾期信息列表(footer)
     *
     * @param overdueCompany
     * @return
     */
    @Override
    public RptCountOfRiskOverdueCompany countOverdueCompanyList(OverdueCompany overdueCompany) {
        return riskCompanyReportMapper.countOverdueCompanyList(overdueCompany);
    }

    /**
     * 预算财务统计表
     *
     * @param financeStatics
     * @return
     */
    @Override
    public PageHelper<RptMarginAmountReport> getMarginAmountList(FinanceStatics financeStatics) {

        List<Long> approveIds = getApproveIds(financeStatics);
        List<RptMarginAmountReport> list = Collections.emptyList();
        // 代采赊销的数据与其他类型分开查询
        if ("5".equals(financeStatics.getBudgetType())) {
            list = riskFinanceReportMapper.getMarginAmountListWithDCSX(approveIds);
        }else {
            list = riskFinanceReportMapper.getMarginAmountList(approveIds);
        }
        Pageable pageable = PageRequest.of(financeStatics.getPage() - 1, financeStatics.getRows());
        Page<RptMarginAmountReport> pageVo = new PageImpl<>(list, pageable, financeStatics.getCount());
        PageHelper<RptMarginAmountReport> r = new PageHelper<>();
        BeanUtils.copyProperties(pageVo, r);
        financeStatics.setPage(1);
        financeStatics.setRows(9999);
        List<Long> approveIds1 = getApproveIds(financeStatics);
        RptSumMarginAmountReport sumMarginAmountReport;
        // 代采赊销的数据与其他类型分开查询
        if ("5".equals(financeStatics.getBudgetType())) {
            sumMarginAmountReport = riskFinanceReportMapper.sumOfMarginAmountListDCSX(approveIds1);
        }else {
            sumMarginAmountReport = riskFinanceReportMapper.sumOfMarginAmountList(approveIds1);
        }
        r.setFooter(sumMarginAmountReport);
        return r;
    }


    /**
     * 预算财务统计表（单表查询）
     *
     * @param financeStatics
     * @return
     */
    @Override
    public PageHelper<RptMarginAmountReport> getMarginAmountListNew(FinanceStatics financeStatics) {
        List<RptMarginAmountReport> list = Collections.emptyList();
        list= riskFinanceReportMapper.getMarginAmountListNew(financeStatics);
        Pageable pageable = PageRequest.of(financeStatics.getPage() - 1, financeStatics.getRows());
        Page<RptMarginAmountReport> pageVo = new PageImpl<>(list, pageable, financeStatics.getCount());
        PageHelper<RptMarginAmountReport> r = new PageHelper<>();
        BeanUtils.copyProperties(pageVo, r);
        financeStatics.setPage(1);
        financeStatics.setRows(9999);
        RptSumMarginAmountReport sumMarginAmountReport;
        sumMarginAmountReport = riskFinanceReportMapper.sumOfMarginAmountListNew(financeStatics);
        r.setFooter(sumMarginAmountReport);
        return r;
    }

    /**
     * 预算财务统计表 所有记录(单表)
     *
     * @param financeStatics
     * @return
     */
    @Override
    public PageHelper<RptMarginAmountReport> getMarginAmountAllNewList(FinanceStatics financeStatics) {
        financeStatics.setPage(1);
        financeStatics.setRows(100000);
        List<RptMarginAmountReport> list = Collections.emptyList();
        list= riskFinanceReportMapper.getMarginAmountListNew(financeStatics);
        Pageable pageable = PageRequest.of(financeStatics.getPage() - 1, financeStatics.getRows());
        Page<RptMarginAmountReport> pageVo = new PageImpl<>(list, pageable, financeStatics.getCount());
        PageHelper<RptMarginAmountReport> r = new PageHelper<>();
        BeanUtils.copyProperties(pageVo, r);
        return r;
    }

    /**
     * 预算财务统计表 所有记录
     *
     * @param financeStatics
     * @return
     */
    @Override
    public PageHelper<RptMarginAmountReport> getMarginAmountAllList(FinanceStatics financeStatics) {

        financeStatics.setPage(1);
        financeStatics.setRows(100000);
        List<Long> approveIds = getApproveIds(financeStatics);
        List<RptMarginAmountReport> list = Collections.emptyList();
        // 代采赊销的数据与其他类型分开查询
        if ("5".equals(financeStatics.getBudgetType())) {
            list = riskFinanceReportMapper.getMarginAmountListWithDCSX(approveIds);
        }else {
            list = riskFinanceReportMapper.getMarginAmountList(approveIds);
        }
        Pageable pageable = PageRequest.of(financeStatics.getPage() - 1, financeStatics.getRows());
        Page<RptMarginAmountReport> pageVo = new PageImpl<>(list, pageable, financeStatics.getCount());
        PageHelper<RptMarginAmountReport> r = new PageHelper<>();
        BeanUtils.copyProperties(pageVo, r);
        return r;
    }

    /**
     * 预算财务统计表所有行
     *
     * @param financeStatics
     * @return
     */
    @Override
    public List<RptMarginAmountReport> getAllMarginAmountList(FinanceStatics financeStatics) {
        financeStatics.setRows(9999);
        financeStatics.setPage(1);
        List<Long> approveIds = getApproveIds(financeStatics);
        return riskFinanceReportMapper.getMarginAmountList(approveIds);
    }

    /**
     * 预算决算统计表
     *
     * @param financeStatics
     * @return
     */
    @Override
    public PageHelper<RptFinalAccountReport> getFinalAccountsList(FinanceStatics financeStatics) {
        List<Long> approveIds = getApproveIds(financeStatics);
        List<RptFinalAccountReport> list = Collections.emptyList();
        // 代采赊销的数据与其他类型分开查询
        if ("5".equals(financeStatics.getBudgetType())) {
            list = riskFinanceReportMapper.getFinalAccountsListDCSX(approveIds);
        }else{
            list = riskFinanceReportMapper.getFinalAccountsList(approveIds);
        }
        Pageable pageable = PageRequest.of(financeStatics.getPage() - 1, financeStatics.getRows());
        Page<RptFinalAccountReport> pageVo = new PageImpl<>(list, pageable, financeStatics.getCount());
        PageHelper<RptFinalAccountReport> r = new PageHelper<>();
        BeanUtils.copyProperties(pageVo, r);
        financeStatics.setPage(1);
        financeStatics.setRows(9999);
        List<Long> approveIds1 = getApproveIds(financeStatics);
        // 代采赊销的数据与其他类型分开查询
        RptSumFinalAccountReport sumFinalAccountReport;
        if ("5".equals(financeStatics.getBudgetType())) {
            sumFinalAccountReport = riskFinanceReportMapper.sumOfFinalAccountListDCSX(approveIds1);
        }else{
            sumFinalAccountReport = riskFinanceReportMapper.sumOfFinalAccountList(approveIds1);
        }
        r.setFooter(sumFinalAccountReport);
        return r;
    }
    /**
     * 预算决算统计表
     *
     * @param financeStatics
     * @return
     */
    @Override
    public PageHelper<RptFinalAccountReportNew> getFinalAccountsNewList(FinanceStatics financeStatics) {
        // 代采赊销的数据与其他类型分开查询
        String productsName = financeStatics.getProductsName();
        if( !StringUtils.isEmpty(productsName)) {
            String[] split = productsName.split("/");
            if(split.length == 1) {
                financeStatics.setProductsNameOne(split[0]);
                financeStatics.setLevel("1");
            } else if(split.length == 2) {
                financeStatics.setProductsNameOne(split[0]);
                financeStatics.setProductsNameTwo(split[1]);
                financeStatics.setLevel("2");
            } else if(split.length == 3) {
                financeStatics.setProductsNameOne(split[0]);
                financeStatics.setProductsNameTwo(split[1]);
                financeStatics.setProductsNameThree(split[2]);
                financeStatics.setLevel("3");
            }
        }
        List<RptFinalAccountReportNew> list = riskFinanceReportMapper.getFinalAccountsNewList(financeStatics);
        Pageable pageable = PageRequest.of(financeStatics.getPage() - 1, financeStatics.getRows());
        Page<RptFinalAccountReportNew> pageVo = new PageImpl<>(list, pageable, financeStatics.getCount());
        PageHelper<RptFinalAccountReportNew> r = new PageHelper<>();
        BeanUtils.copyProperties(pageVo, r);
        
        // 合计
        financeStatics.setPage(1);
        financeStatics.setRows(100000);
        // 代采赊销的数据与其他类型分开查询
        RptSumFinalAccountReportNew sumFinalAccountReportNew = riskFinanceReportMapper.sumOfFinalAccountNewList(financeStatics);
        
        r.setFooter(sumFinalAccountReportNew);
        return r;
    }

    @Override
    public PageHelper<RptFinalAccountReportNew> getFinalAccountsAllList(FinanceStatics financeStatics) {
        financeStatics.setRows(100000);
        financeStatics.setPage(1);
        String productsName = financeStatics.getProductsName();
        if( !StringUtils.isEmpty(productsName)) {
            String[] split = productsName.split("/");
            if(split.length == 1) {
                financeStatics.setProductsNameOne(split[0]);
                financeStatics.setLevel("1");
            } else if(split.length == 2) {
                financeStatics.setProductsNameOne(split[0]);
                financeStatics.setProductsNameTwo(split[1]);
                financeStatics.setLevel("2");
            } else if(split.length == 3) {
                financeStatics.setProductsNameOne(split[0]);
                financeStatics.setProductsNameTwo(split[1]);
                financeStatics.setProductsNameThree(split[2]);
                financeStatics.setLevel("3");
            }
        }
        List<RptFinalAccountReportNew> list = riskFinanceReportMapper.getFinalAccountsNewList(financeStatics);
        Pageable pageable = PageRequest.of(financeStatics.getPage() - 1, financeStatics.getRows());
        Page<RptFinalAccountReportNew> pageVo = new PageImpl<>(list, pageable, financeStatics.getCount());
        PageHelper<RptFinalAccountReportNew> r = new PageHelper<>();
        BeanUtils.copyProperties(pageVo, r);
        
        return r;
    }

    /**
     * 预算决算统计表 所有行
     *
     * @param financeStatics
     * @return
     */
    @Override
    public List<RptFinalAccountReport> getAllFinalAccountsList(FinanceStatics financeStatics) {
        financeStatics.setRows(9999);
        financeStatics.setPage(1);
        List<Long> approveIds = getApproveIds(financeStatics);
        return riskFinanceReportMapper.getFinalAccountsList(approveIds);
    }

    /**
     * for findPageCreditBudget 获取approveId
     * @param search
     * @return
     */
    @Override
    public List<Long> getApproveIds(FinanceStatics search) {
        // 处理检索条件 buyContractStatus sellContractStatus
        // 无法直接在一个sql中处理 所以根据采购或销售合同分情况处理
        List<Long> approveIds = riskFinanceReportMapper.getApproveIdsWithAll(search);

        if (approveIds.isEmpty()) {
            approveIds.add(0L);
        }
        return approveIds;
    }

}
