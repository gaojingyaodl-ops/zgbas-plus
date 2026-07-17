package com.spt.bas.web.controller;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsNotice;
import com.spt.bas.client.remote.IBsNoticeClient;
import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.remote.IRptIndexReportClient;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.web.cache.WorkBenchCache;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.constant.PmConstants;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.HtmlUtil;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: gaojy
 * @create 2022/6/16 17:40
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/my/index")
public class MyIndexController {
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsNoticeClient bsNoticeClient;
    @Autowired
    private IRptIndexReportClient indexReportClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "")
    public String index(Model model) {
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
        List<Long> hgMatchUserIdList = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listByCategory)) {
            for (BsDictData bsDictData : listByCategory) {
                try {
                    String dictCd = bsDictData.getDictCd();
                    Long matchUserId = Long.valueOf(dictCd);
                    hgMatchUserIdList.add(matchUserId);
                } catch (Exception e) {
                }
            }
        }
        // 首页-查看风控统计权限
        model.addAttribute("riskStatisticsViewPerm", ShiroUtil.isPermitted(PermissionEnum.BAS_INDEX_RISKSTATISTICS.getPermissionCode()));
        // 首页-查看财务统计权限
        model.addAttribute("financeStatisticsViewPerm", ShiroUtil.isPermitted(PermissionEnum.BAS_INDEX_FINANCESTATISTICS.getPermissionCode()));
        model.addAttribute("businessTypeJson", BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_TYPE));
        // 首页-查看未收款统计权限
        model.addAttribute("unPayStatisticsViewPerm", ShiroUtil.isPermitted(PermissionEnum.BAS_INDEX_UNPAYSTATISTICS.getPermissionCode()));
        // 首页-毛利率查看权限
        model.addAttribute("grossProfitMarginViewPerm", ShiroUtil.isPermitted(PermissionEnum.BAS_INDEX_GROSSPROFITMARGIN.getPermissionCode()));
        List<BsDictData> companyOurFalagList = BsCompanyOurUtil.getCompanyOurFlagToBsDictDataList();
        model.addAttribute("ourCompany", companyOurFalagList);
        model.addAttribute("month","本月");
        model.addAttribute("month_1",getMonthByNum(1));
        model.addAttribute("month_2",getMonthByNum(2));
        model.addAttribute("month_3",getMonthByNum(3));
        // 风控统计
        RptIndexStatisticsReqVo riskStatisticsVo = new RptIndexStatisticsReqVo();
        riskStatisticsVo.setHgMatchUserIdList(hgMatchUserIdList);
        List<RptIndexStatisticsVo> riskStatisticsList = WorkBenchCache.getRiskStatistics(riskStatisticsVo);
        model.addAttribute("riskStatisticsList", riskStatisticsList);
        RptIndexStatisticsReqVo vo = new RptIndexStatisticsReqVo();
        RptIndexReportQuery query = new RptIndexReportQuery();
        query.setCurrentUserId(ShiroUtil.getCurrentUserId());
        // 财务统计
        List<String> ourCompanyNames = companyOurFalagList.stream()
                .map(data -> data.getDictName().trim()) // 去除每个 dictName 两端的空格
                .collect(Collectors.toList());
        vo.setOurCompanyNameList(ourCompanyNames);
        vo.setHgMatchUserIdList(hgMatchUserIdList);
        List<RptIndexStatisticsVo> financeStatisticsList = WorkBenchCache.getFinanceStatistics(vo);
        model.addAttribute("financeStatisticsList", financeStatisticsList);
        // 到期应收
        List<RptIndexStatisticsVo> dueReceivableList = WorkBenchCache.getUnPayFinance();
        model.addAttribute("dueReceivableList", dueReceivableList);
        // 未收款统计
        List<RptIndexStatisticsVo> unPayStatisticsList = WorkBenchCache.getUnPayStatistics();
        model.addAttribute("unPayStatisticsList", unPayStatisticsList);
        // 待办事项
        RptToDoStatisticsVo statisticsVo = getToDoStatisticsVo();
        model.addAttribute("statisticsVo", statisticsVo);
        // 待审批事项
        model.addAttribute("approvalCount", indexReportClient.getApprovalCount(query));
        //公告
//        model.addAttribute("announcement", announCement());

        // 业务统计
        RptIndexReportQuery businessQuery = new RptIndexReportQuery();
        
        businessQuery.setHgMatchUserIdList(hgMatchUserIdList);

        businessQuery.setCurrentUserId(ShiroUtil.getCurrentUserId());

        businessQuery.setStatisticsType("QY");
        List<RptIndexCommonVo> businessQyList = getIndexCommonVos(businessQuery);
        model.addAttribute("businessQyList", businessQyList);
        businessQuery.setStatisticsType("JH");
        List<RptIndexCommonVo> businessJhList = getIndexCommonVos(businessQuery);
        model.addAttribute("businessJhList", businessJhList);

        businessQuery.setQueryDate(getDayByNum(1));
        businessQuery.setLastMonthNum(1);
        businessQuery.setStatisticsType("QY");
        List<RptIndexCommonVo> businessMonth1QyList = getIndexCommonVosByMonth(businessQuery);
        model.addAttribute("businessMonth1QyList", businessMonth1QyList);
        businessQuery.setStatisticsType("JH");
        List<RptIndexCommonVo> businessMonth1JhList = getIndexCommonVosByMonth(businessQuery);
        model.addAttribute("businessMonth1JhList", businessMonth1JhList);

        businessQuery.setQueryDate(getDayByNum(2));
        businessQuery.setLastMonthNum(2);
        businessQuery.setStatisticsType("QY");
        List<RptIndexCommonVo> businessMonth2QyList = getIndexCommonVosByMonth(businessQuery);
        model.addAttribute("businessMonth2QyList", businessMonth2QyList);
        businessQuery.setStatisticsType("JH");
        List<RptIndexCommonVo> businessMonth2JhList = getIndexCommonVosByMonth(businessQuery);
        model.addAttribute("businessMonth2JhList", businessMonth2JhList);

        businessQuery.setQueryDate(getDayByNum(3));
        businessQuery.setLastMonthNum(3);
        businessQuery.setStatisticsType("QY");
        List<RptIndexCommonVo> businessMonth3QyList = getIndexCommonVosByMonth(businessQuery);
        model.addAttribute("businessMonth3QyList", businessMonth3QyList);
        businessQuery.setStatisticsType("JH");
        List<RptIndexCommonVo> businessMonth3JhList = getIndexCommonVosByMonth(businessQuery);
        model.addAttribute("businessMonth3JhList", businessMonth3JhList);


        // 业绩提成
        RptPerformanceVo performanceCommission = getPerformanceVo();
        model.addAttribute("performanceCommission", performanceCommission);
        // 赊销业绩排名
        query.setMatchCreditFlg(true);
        query.setStatisticsType("JH");

        query.setHgMatchUserIdList(hgMatchUserIdList);

        List<RptIndexCommonVo> sellJhList = performanceRanking(query);
        model.addAttribute("sellJhList", sellJhList);
        query.setMatchCreditFlg(true);
        query.setStatisticsType("QY");
        List<RptIndexCommonVo> sellQyList = performanceRanking(query);
        model.addAttribute("sellQyList", sellQyList);
        // 采购业绩排名
        query.setMatchCreditFlg(false);
        query.setStatisticsType("JH");
        List<RptIndexCommonVo> buyJhList = performanceRanking(query);
        model.addAttribute("buyJhList", buyJhList);
        query.setMatchCreditFlg(false);
        query.setStatisticsType("QY");
        List<RptIndexCommonVo> buyQyList = performanceRanking(query);
        model.addAttribute("buyQyList", buyQyList);

        return "index/myIndex";
    }

    protected String getMonthByNum(Integer num) {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(num);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MM");
        return formatters.format(today)+"月";
    }
    protected String getDayByNum(Integer num) {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(num);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatters.format(today);
    }


    /**
     * 待办统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/backlogStatistics")
    public void backlogStatistics(@RequestBody RptIndexReportQuery query, HttpServletResponse response) {
        RenderUtil.renderJson(JsonUtil.obj2Json(getToDoStatisticsVo()), response);
    }

    /**
     * 待办统计
     *
     * @return 待办统计结果
     */
    private RptToDoStatisticsVo getToDoStatisticsVo() {
        RptIndexReportQuery query = new RptIndexReportQuery();
        query.setCurrentUserId(ShiroUtil.getCurrentUserId());
        initQuery(query);
        return WorkBenchCache.getToDoStatistics(query);
    }

    private void initQuery(RptIndexReportQuery query) {
        query.setEnterpriseId(ShiroUtil.getEnterpriseId());
        // 如果没有查看全部的权限
        if (!ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            // D.业务助理 查看本业务部所有预算
            if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
                // 业务助理查询本业务部所有合同
                //可以查看所属于自己的合同
                Long deptId = ShiroUtil.getDeptId();
                query.setDeptIds(Collections.singletonList(deptId));
                query.setUserIdOrDept(true);
            } else if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
                //P.可以查看本中心所有预售合同权限
                List<Long> myDeptId = authOpenFacade.findMyDeptId(query.getCurrentUserId());
                Long deptLeaderId = webParamUtils.getDeptLeader();
                List<Long> allDeptId = authOpenFacade.findMyDeptId(deptLeaderId);
                query.setUserIdOrDept(true);
                query.setDeptIds(myDeptId);
                query.setSource(BasConstants.APPLY_TYPE_L);
                query.setAllDeptIds(allDeptId);
            } else if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
                // A.可以查看本中心所有合同权限
                DeptSearchVo deptSearchVo = new DeptSearchVo(query.getCurrentUserId(), PmConstants.NODE_TYPE_CENTER, ShiroUtil.getEnterpriseId());
                SysDeptSdk dept = authOpenFacade.findDept(deptSearchVo);
                List<Long> allDeptId = authOpenFacade.findMyDeptId(webParamUtils.getDeptLeader());
                if ("ws".equals(dept.getDeptAbbr())) {
                    DeptSearchVo searchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
                    List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(searchVo);
                    allDeptId = deptAll.stream().filter(a -> !StringUtils.isEmpty(a.getDeptAbbr()) && a.getDeptAbbr().contains(dept.getDeptAbbr())).map(a -> a.getDeptId()).collect(Collectors.toList());
                }
                query.setDeptIds(allDeptId);
                query.setUserIdOrDept(true);
            } else if (ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWBL.getPermissionCode())) {
                //B.可以查看本中心所有的保理合同
                query.setUserIdOrDept(true);
                query.setDeptIds(Collections.singletonList(ShiroUtil.getDeptId()));
                query.setBusinessTypeDcsx(Arrays.asList(BasConstants.BUSINESS_TYPE_BL, BasConstants.BUSINESS_TYPE_DCSXBL));
            } else {
                //可以查看所属于自己的合同
                List<Long> myDeptId = authOpenFacade.findMyDeptId(query.getCurrentUserId());
                query.setUserIdOrDept(true);
                query.setDeptIds(myDeptId);
            }
        } else {
            query.setCurrentUserId(null);
        }
    }

    /**
     * 业务统计
     *
     * @return 结果
     */
    @PostMapping("/businessStatistics")
    public void businessStatistics(HttpServletResponse response) {
        RptIndexReportQuery query = new RptIndexReportQuery();
        query.setCurrentUserId(ShiroUtil.getCurrentUserId());
        List<RptIndexCommonVo> result = getIndexCommonVos(query);
        RenderUtil.renderJson(JsonUtil.obj2Json(result), response);
    }

    private List<RptIndexCommonVo> getIndexCommonVos(RptIndexReportQuery query) {
        initQuery(query);
        return WorkBenchCache.getBusinessStatistics(query);
    }

    private List<RptIndexCommonVo> getIndexCommonVosByMonth(RptIndexReportQuery query) {
        initQuery(query);
        return WorkBenchCache.getBusinessStatisticsByMonth(query);
    }

    /**
     * 业绩提成
     *
     * @return 业绩提成统计
     */
    @PostMapping("/getPerformanceCommission")
    public void getPerformanceCommission(HttpServletResponse response) {
        RptPerformanceVo performanceCommission = getPerformanceVo();
        RenderUtil.renderJson(JsonUtil.obj2Json(performanceCommission), response);
    }

    private RptPerformanceVo getPerformanceVo() {
        RptIndexReportQuery query = new RptIndexReportQuery();
        // 是人事查看全部的权限
        if (!ShiroUtil.isPermitted(PermissionEnum.ZGBAS_PERSONNER_ADMIN.getPermissionCode())) {
            query.setCurrentUserId(ShiroUtil.getCurrentUserId());
        }
        // 拥有查看全部的权限
        if (!ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCECOMMISSION_ALL.getPermissionCode())) {
            query.setCurrentUserId(ShiroUtil.getCurrentUserId());
        }
        return WorkBenchCache.getPerformanceCommissionCache(query);
    }

    /**
     * 业绩排行
     */
    @PostMapping("/performanceRanking")
    public void performanceRanking(@RequestBody RptIndexReportQuery query, HttpServletResponse response) {
        query.setCurrentUserId(ShiroUtil.getCurrentUserId());
        List<RptIndexCommonVo> result = performanceRanking(query);
        RenderUtil.renderJson(JsonUtil.obj2Json(result), response);
    }

    private List<RptIndexCommonVo> performanceRanking(RptIndexReportQuery query) {
        // 如果没有查看全部的权限就只查询本部门的数据
        if (!ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCE_ALL.getPermissionCode())) {
            List<SysUserSdk> deptUsers = authOpenFacade.findByDeptIds(Collections.singletonList(ShiroUtil.getDeptId()));
            List<Long> userIds = deptUsers.stream().map(SysUserSdk::getUserId).distinct().collect(Collectors.toList());
            // 查询部门内所有人的业绩
            query.setUserIds(userIds);
        }
        return WorkBenchCache.getPerformanceRanking(query);
    }

    /**
     * 首页-风控统计
     *
     * @return 结果
     */
    @PostMapping("/findIndexRiskStatistics")
    public void findIndexRiskStatistics(@RequestBody RptIndexStatisticsReqVo vo, HttpServletResponse response) {
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
        List<Long> hgMatchUserIdList = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listByCategory)) {
            for (BsDictData bsDictData : listByCategory) {
                try {
                    String dictCd = bsDictData.getDictCd();
                    Long matchUserId = Long.valueOf(dictCd);
                    hgMatchUserIdList.add(matchUserId);
                } catch (Exception e) {
                }
            }
        }
        vo.setHgMatchUserIdList(hgMatchUserIdList);
        List<RptIndexStatisticsVo> riskStatistics = WorkBenchCache.getRiskStatistics(vo);
        RenderUtil.renderJson(JsonUtil.obj2Json(riskStatistics), response);
    }

    /**
     * 首页-财务统计
     *
     * @return 结果
     */
    @PostMapping("/findIndexFinanceStatistics")
    public void findIndexFinanceStatistics(@RequestBody RptIndexStatisticsReqVo vo, Model model, HttpServletResponse response) {
        List<RptIndexStatisticsVo> financeStatistics = WorkBenchCache.getFinanceStatistics(vo);
        model.addAttribute("financeStatisticsList", financeStatistics);
        RenderUtil.renderJson(JsonUtil.obj2Json(financeStatistics), response);
    }

    /**
     * 首页-到期应收      未来7天应收未收的销售货款 已出库，且还未付款
     *
     * @return 结果
     */
    @PostMapping("/findSellUnPayFinance")
    public void findSellUnPayFinance(HttpServletResponse response) {
        List<RptIndexStatisticsVo> indexStatisticsVoList = WorkBenchCache.getUnPayFinance();
        RenderUtil.renderJson(JsonUtil.obj2Json(indexStatisticsVoList), response);
    }

    /**
     * 首页-未收款统计
     *
     * @return 结果
     */
    @PostMapping("/findUnPayStatistics")
    public void findUnPayStatistics(HttpServletResponse response) {
        List<RptIndexStatisticsVo> indexStatisticsVoList = WorkBenchCache.getUnPayStatistics();
        RenderUtil.renderJson(JsonUtil.obj2Json(indexStatisticsVoList), response);
    }

    /**
     * 首页-公告
     *
     * @return
     */
    public List<BsNotice> announCement() {
        String deptId = ShiroUtil.getDeptId().toString();
        List<BsNotice> limit5;
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_ADMIN_NEW.getPermissionCode())) {
            limit5 = bsNoticeClient.findLimit();
        } else {
            limit5 = bsNoticeClient.findLimit5(deptId);
        }
        for (BsNotice bsNotice : limit5) {
            String textFromHtml = HtmlUtil.getTextFromHtml(bsNotice.getContent());
            bsNotice.setYear(DateOperator.formatDate(bsNotice.getCreatedDate()));
            bsNotice.setContent(textFromHtml);
        }
        return limit5;
    }

    /**
     * 获取过去10个月的毛利率
     */
    @PostMapping("/grossProfitMargin")
    public void grossProfitMargin(HttpServletResponse response) {
        List<RptGrossProfitMarginVo> grossProfitMargin = WorkBenchCache.getGrossProfitMargin();
        RenderUtil.renderJson(JsonUtil.obj2Json(grossProfitMargin), response);
    }

    /**
     * 销售（周）	过去10周赊销业务金额统计
     */
    @PostMapping("/findSxSalesTenWeekData")
    public void findSxSalesTenWeekData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> sxSalesTenWeekData = WorkBenchCache.getSalesList(ReportConstant.SALES_WEEK_TYPE_1);
        RenderUtil.renderJson(JsonUtil.obj2Json(sxSalesTenWeekData), response);
    }

    /**
     * 销售（周）	过去10周代采业务金额统计
     */
    @PostMapping("/findDcSalesTenWeekData")
    public void findDcSalesTenWeekData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> dcSalesTenWeekData = WorkBenchCache.getSalesList(ReportConstant.SALES_WEEK_TYPE_2);
        RenderUtil.renderJson(JsonUtil.obj2Json(dcSalesTenWeekData), response);
    }

    /**
     * 销售（周）	过去10周自营业务金额统计
     */
    @PostMapping("/findZySalesTenWeekData")
    public void findZySalesTenWeekData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> zySalesTenWeekData = WorkBenchCache.getSalesList(ReportConstant.SALES_WEEK_TYPE_3);
        RenderUtil.renderJson(JsonUtil.obj2Json(zySalesTenWeekData), response);
    }

    /**
     * 过去10周业务金额统计(赊销、代采、自营)合计
     */
    @PostMapping("/findTotalSalesWeekData")
    public void findTotalSalesWeekData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> totalSalesWeekData = WorkBenchCache.getSalesList(ReportConstant.SALES_WEEK_TYPE_4);
        RenderUtil.renderJson(JsonUtil.obj2Json(totalSalesWeekData), response);
    }

    /**
     * 销售（月）	过去10月赊销业务金额统计
     */
    @PostMapping("/findSxSalesTenMonthData")
    public void findSxSalesTenMonthData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> sxSalesTenMonthData = WorkBenchCache.getSalesList(ReportConstant.SALES_MONTH_TYPE_5);
        RenderUtil.renderJson(JsonUtil.obj2Json(sxSalesTenMonthData), response);
    }

    /**
     * 销售（月）	过去10月代采业务金额统计
     */
    @PostMapping("/findDcSalesTenMonthData")
    public void findDcSalesTenMonthData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> dcSalesTenMonthData = WorkBenchCache.getSalesList(ReportConstant.SALES_MONTH_TYPE_6);
        RenderUtil.renderJson(JsonUtil.obj2Json(dcSalesTenMonthData), response);
    }

    /**
     * 销售（月）	过去10月自营业务金额统计
     */
    @PostMapping("/findZySalesTenMonthData")
    public void findZySalesTenMonthData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> zySalesTenMonthData = WorkBenchCache.getSalesList(ReportConstant.SALES_MONTH_TYPE_7);
        RenderUtil.renderJson(JsonUtil.obj2Json(zySalesTenMonthData), response);
    }

    /**
     * 过去10月业务金额统计(赊销、代采、自营)合计
     */
    @PostMapping("/findTotalSalesMonthData")
    public void findTotalSalesMonthData(HttpServletResponse response) {
        List<RptIndexStatisticsVo> totalSalesMonthData = WorkBenchCache.getSalesList(ReportConstant.SALES_MONTH_TYPE_8);
        RenderUtil.renderJson(JsonUtil.obj2Json(totalSalesMonthData), response);
    }

}
