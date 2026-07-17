package com.spt.bas.web.controller;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IPmProcessAccessClient;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.remote.IRptBusinessManagerWorkbenchClient;
import com.spt.bas.report.client.vo.RptBusinessManagerWorkbenchSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmProcessAccess;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务经理工作台 Controller
 *
 * @Author: lsj
 * @create 2024/12/11
 */
@Controller
@RequestMapping(value = "/business/manager/workbench")
public class BusinessManagerWorkbenchController {
    @Autowired
    private IRptBusinessManagerWorkbenchClient businessManagerWorkbenchClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmProcessAccessClient processAccessClient;

    @RequestMapping(value = "")
    public String index(Model model) {
        Long currentUserId = ShiroUtil.getCurrentUserId();
        SysUserSdk user = authOpenFacade.findUserById(currentUserId);
        SysDeptSdk dept = authOpenFacade.findDeptById(ShiroUtil.getDeptId());
        model.addAttribute("userInfo",user);
        model.addAttribute("deptName",dept.getDeptName());
        // 判断是否具有采购权限
        List<PmProcessAccess> accessList = processAccessClient.findByUserId(ShiroUtil.getCurrentUserId());
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_PROTOCOL_PURCHASE);
        BsDictData bsDictData = listByCategory.get(0);
        if(CollectionUtils.isNotEmpty(accessList)){
            boolean protocolPurchaseFlg = accessList.stream().anyMatch(it -> it.getProcessId().toString().equals(bsDictData.getDictCd()));
            model.addAttribute("protocolPurchaseFlg",protocolPurchaseFlg);
        }
        List<SysDeptSdk> deptAll = webParamUtils.getDeptAll();
        Set<Long> leaderSets = deptAll.stream().map(SysDeptSdk::getLeaderId).collect(Collectors.toSet());
        boolean showAllPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_BUSINESS_MANAGER_WORKBENCH_SHOW_ALL.getPermissionCode());
        model.addAttribute("showRanking",true);
        if (showAllPerm) {
            model.addAttribute("showRanking",false);
        } else if (leaderSets.contains(user.getUserId())) {
            model.addAttribute("showRanking",false);
        }
        model.addAttribute("month","本月");
        model.addAttribute("month_1",getMonthByNum(1));
        model.addAttribute("month_2",getMonthByNum(2));
        model.addAttribute("managerLeader", ShiroUtil.hasRole("bas_area_manager"));
        return "index/businessManagerIndex";
    }

    /**
     * 个人成就
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findPersonalAchievement")
    @ResponseBody
    public RptPersonalAchievement findPersonalAchievement(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        initSearch(searchVo, true);
        RptPersonalAchievement personalAchievement = businessManagerWorkbenchClient.findPersonalAchievement(searchVo);

        return personalAchievement;
    }

    /**
     * 过去5个月毛利润
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findFiveMonthGrossProfitAmount")
    @ResponseBody
    public List<RptPersonalAchievement> findFiveMonthGrossProfitAmount(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        initSearch(searchVo, true);
        List<RptPersonalAchievement> personalAchievementList = businessManagerWorkbenchClient.findFiveMonthGrossProfitAmount(searchVo);

        return personalAchievementList;
    }

    /**
     * 订单-执行（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractExecutionStatistList")
    @ResponseBody
    public List<RptWorkbenchContractStatist> findContractExecutionStatistList( RptBusinessManagerWorkbenchSearchVo searchVo) {
        initSearch(searchVo, false);
        List<RptWorkbenchContractStatist> list = businessManagerWorkbenchClient.findContractExecutionStatistList(searchVo);

        return list;
    }

    /**
     * 订单-执行详情 （销售）待出库，待收款，待开票
     */
    @RequestMapping("findSellContractExecutionPage")
    public void findSellContractExecutionPage(RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
        PageDown<RptWorkbenchContract> page = businessManagerWorkbenchClient.findSellContractExecutionPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 订单-执行详情 （采购）待收票
     */
    @RequestMapping("findBuyContractExecutionPage")
    public void findBuyContractExecutionPage(RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
        PageDown<RptWorkbenchContract> page = businessManagerWorkbenchClient.findBuyContractExecutionPage(searchVo);

        JsonEasyUI.renderJson(response, page);
    }


    /**
     * 订单-应收（统计）
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findContractReceivableStatistList")
    @ResponseBody
    public List<RptWorkbenchContractStatist> findContractReceivableStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        initSearch(searchVo, false);
        List<RptWorkbenchContractStatist> list = businessManagerWorkbenchClient.findContractReceivableStatistList(searchVo);
        return list;
    }

    /**
     * 订单-应收详情
     */
    @RequestMapping("findSellContractReceivablePage")
    public void findSellContractReceivablePage(RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
        PageDown<RptWorkbenchContract> page = businessManagerWorkbenchClient.findSellContractReceivablePage(searchVo);

        JsonEasyUI.renderJson(response, page, null,getFooter(searchVo));
    }

    /**
     * 合计
     *
     * @return 合计
     */
    private Map<String, Object> getFooter(RptBusinessManagerWorkbenchSearchVo searchVo) {
        searchVo.setPage(1);
        RptWorkbenchContract sum = businessManagerWorkbenchClient.findSellContractReceivableSum(searchVo);
        Map<String, Object> result = new HashMap<>();
        result.put("contractNo", "合计");
        result.put("totalAmount", Objects.nonNull(sum) ? sum.getTotalAmount() : BigDecimal.ZERO);
        result.put("noReceiveTotalAmount", Objects.nonNull(sum) ? sum.getNoReceiveTotalAmount() : BigDecimal.ZERO);
        result.put("breachAmount", Objects.nonNull(sum) ? sum.getBreachAmount() : BigDecimal.ZERO);
        result.put("noReceiveBreachAmount", Objects.nonNull(sum) ? sum.getNoReceiveBreachAmount() : BigDecimal.ZERO);
        return result;
    }

    /**
     * 订单-审批（统计）
     *
     * @param searchVo
     * @return
     */
    @RequestMapping("findContractApproveStatistList")
    public void findContractApproveStatistList(RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
        String beginDate = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_WORKBENCH_CONFIG, BasConstants.DICT_BUSINESS_WORKBENCH_CONFIG_begin_date);
        if (StringUtils.isNotEmpty(beginDate)) {
            searchVo.setBeginDate(beginDate);
        }
        PageDown<RptWorkbenchApproveStatist> page = businessManagerWorkbenchClient.findContractApproveStatistList(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 查询订单-审批数据详情
     */
    @PostMapping("findContractApprovePage")
    @ResponseBody
    public Page<RptWorkbenchApprove> findContractApprovePage(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
        String beginDate = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_WORKBENCH_CONFIG, BasConstants.DICT_BUSINESS_WORKBENCH_CONFIG_begin_date);
        if (StringUtils.isNotEmpty(beginDate)) {
            searchVo.setBeginDate(beginDate);
        }
        PageDown<RptWorkbenchApprove> page = businessManagerWorkbenchClient.findContractApprovePage(searchVo);

        return page;
    }

    /**
     * 查询客户 统计
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findCompanyStatistList")
    @ResponseBody
    List<RptWorkbenchContractStatist> findCompanyStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        initSearch(searchVo, false);
        searchVo.setCompanyType(BasConstants.DICT_TYPE_COMPANYTYPE_I);
        searchVo.setContractType(BasConstants.CONTRACT_TYPE_S);
        searchVo.setMonthFlag(true);
        List<RptWorkbenchContractStatist> list = businessManagerWorkbenchClient.findCompanyStatistList(searchVo);
        return list;
    }

    /**
     * 查询供应商 统计
     *
     * @param searchVo
     * @return
     */
    @PostMapping("findSupplierStatistList")
    @ResponseBody
    List<RptWorkbenchContractStatist> findSupplierStatistList(@RequestBody RptBusinessManagerWorkbenchSearchVo searchVo) {
        initSearch(searchVo, false);
        searchVo.setCompanyType(BasConstants.DICT_TYPE_COMPANYTYPE_T);
        searchVo.setContractType(BasConstants.CONTRACT_TYPE_B);
        searchVo.setMonthFlag(true);
        List<RptWorkbenchContractStatist> list = businessManagerWorkbenchClient.findSupplierStatistList(searchVo);
        return list;
    }

    /**
     * 查询企业本月新增数据详情
     *
     * @param searchVo companyType I-工业客户  T-贸易商; monthFlag=true - 月，yearFlag=true - 年 二选一
     * @return
     */
    @RequestMapping("findNewCompanyPage")
    void findNewCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
//         类型 I-工业客户  T-贸易商
//        searchVo.setCompanyType(BasConstants.DICT_TYPE_COMPANYTYPE_T);
//         monthFlag=true - 月，yearFlag=true - 年 二选一
//        searchVo.setMonthFlag(true);
        PageDown<RptWorkbenchCompany> page = businessManagerWorkbenchClient.findNewCompanyPage(searchVo);

        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 查询人保待批复 数据详情
     *
     * @param searchVo
     * @return
     */
    @RequestMapping("findPiccCompanyPage")
    void findPiccCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
        PageDown<RptWorkbenchCompany> page = businessManagerWorkbenchClient.findPiccCompanyPage(searchVo);

        JsonEasyUI.renderJson(response, page);
    }

    /**
     * 查询活跃企业信息 数据详情
     *
     * @param searchVo
     * @return
     */
    @RequestMapping("findHyCompanyPage")
    void findHyCompanyPage(RptBusinessManagerWorkbenchSearchVo searchVo, HttpServletResponse response) {
        initSearch(searchVo, false);
        PageDown<RptWorkbenchCompany> page = businessManagerWorkbenchClient.findHyCompanyPage(searchVo);

        JsonEasyUI.renderJson(response, page);
    }


    public void initSearch(RptBusinessManagerWorkbenchSearchVo searchVo, Boolean notHgFlag) {
        searchVo.setMatchUserId(ShiroUtil.getCurrentUserId());
        boolean showAllPerm = ShiroUtil.isPermitted(PermissionEnum.PERM_BUSINESS_MANAGER_WORKBENCH_SHOW_ALL.getPermissionCode());
        if (!showAllPerm) {
            List<Long> myDeptToUsers = webParamUtils.getMyDeptToUsers(ShiroUtil.getCurrentUserId());
            if (CollectionUtils.isEmpty(myDeptToUsers)) {
                myDeptToUsers = new ArrayList<>();
            }
            myDeptToUsers.add(ShiroUtil.getCurrentUserId());
            searchVo.setMatchUserIdList(myDeptToUsers);
        }
        
        if (notHgFlag) {
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
            searchVo.setHgMatchUserIdList(hgMatchUserIdList);
        }
    }

    protected String getMonthByNum(Integer num) {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(num);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MM");
        return formatters.format(today)+"月";
    }
}
