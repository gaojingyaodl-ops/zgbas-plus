package com.spt.bas.web.controller.ctr;

import cn.hutool.core.date.DateUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.common.ApiResult;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.dto.CtrContractDto;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.report.client.remote.IRptCtrContractReportClient;
import com.spt.bas.report.client.vo.RptCtrContractRptVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.*;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.bean.ShiroUser;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping(value = "/ctr/contract")
public class CtrContractController extends PageController<CtrContract, BaseVo> {

    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IRptCtrContractReportClient ctrContractReportClient;
    @Autowired
    private ICtrContractLossClient ctrContractLossClient;
    @Autowired
    private IBsWarehouseClient warehouseClient;
    @Autowired
    private ICtrProductClient ctrProductClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmProcessClient ProcessClient;
    @Autowired
    private IBsContractTemplateClient bsContractTemplateClient;
    @Autowired
    private IApplyImportBuyClient applyImportBuyClient;
    @Autowired
    private IApplyImportDetailClient applyImportDetailClient;
    @Autowired
    private IApplyMatchDetailClient applyMatchDetailClient;
    @Autowired
    private ICtrServiceContractClient ctrServiceContractClient;
    @Autowired
    private IApplyCtrDcsxClinent ctrDcsxClinent;
    @Autowired
    private IApplyConfirmReceiptClient applyConfirmReceiptClient;
    @Autowired
    private IBsProductTypeClient bsProductTypeClient;
    @Autowired
    private ICtrContractLoadingClient ctrContractLoadingClient;
    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;
    @Value("${file.show.url}")
    private String fileShowUrl;

    @Autowired
    private IApplyMatchClient applyMatchClient;

    @Autowired
    private IPenaltyInterestClient penaltyInterestClient;

    @Autowired
    private IBsFunderClient bsFunderClient;

    @Autowired
    private IBsCompanyOurClient companyOurClient;

    @Autowired
    private ICtrContractDcsxApplyClient contractDcsxApplyClient;

    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBsCompanyClient bsCompanyClient;

    @Override
    public BaseClient<CtrContract> getService() {
        return ctrContractClient;
    }

    /**
     * 通过企业id查询合同主表
     */
    @RequestMapping("/findListByCompanyId")
    public void findListByCompanyId(@RequestParam("id") Long id, PageSearchVo queryVo, HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("EQL_companyId", id);
        queryVo.setSearchParams(map);
        PageDown<CtrContract> page = ctrContractClient.findPage(queryVo);
        JsonEasyUI.renderJson(response, page);
    }

    //跳转合同选择页面
    @RequestMapping(value = "choose")
    public String choose(Model model) {
        model.addAttribute("contractTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        return "ctr/ctrContract-choose";
    }

    //跳转合同选择页面
    @RequestMapping(value = "choose2")
    public String choose2(Model model) {
        model.addAttribute("contractTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        return "ctr/ctrContract-choose2";
    }

    //选择合同列表
    @RequestMapping(value = "listChoose")
    public void listChoose(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Page<CtrContractChooseVo> page = ctrContractClient.findPageChoose(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    public Model initData(Model model) {
        model.addAttribute("businessKindJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND)));
        model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("applyTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        model.addAttribute("sellAndBuyStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELLSTATUS)));
        model.addAttribute("contractsTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
        //model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
//        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        model.addAttribute("performanceStatusJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_PERFORMANCE_STATUS)));
        model.addAttribute("qualityStandardJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        //业务小类
        model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = ProcessClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));

        DeptSearchVo deptSearchVo = new DeptSearchVo();
        deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));

        // 区域查询选项
        model.addAttribute("regionContrastJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_REGION_CONTRAST)));
        //预售合同发起采购权限
        Boolean preSellFlg = canStartBuy();
        model.addAttribute("preSellFlg", preSellFlg);
        //确认收货权限
        Boolean confirmFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_CONFIRM.getPermissionCode());
        model.addAttribute("confirmFlg", confirmFlg);
        //签约权限
        Boolean signingFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_SIGNING.getPermissionCode());
        model.addAttribute("signingFlg", signingFlg);
        //刷新电子合同权限
        boolean refreshFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_REFRESH_CONTRACT.getPermissionCode());
        model.addAttribute("refreshFlg", refreshFlg);
        //资金方合同查看权限
        model.addAttribute("funderViewFlg", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode()));
        //业务助理权限(可以具备预算操作权限，出入库收付款收开票等按钮)
        model.addAttribute("assistantFlg", !(ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BIZ_OPE.getPermissionCode()) || ShiroUtil.isPermitted(PermissionEnum.ZGBASADMIN.getPermissionCode())));
        //清除罚金权限
        model.addAttribute("clearPenaltyFlg", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_CLEAR_PENALTY.getPermissionCode()));
        model.addAttribute("sealFlgJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULTFLG)));// 是否盖章查询条件
        // 导出合同Excel权限
        model.addAttribute("permCtrExport", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_EXPORT.getPermissionCode()));
        // 导出合同双签权限
        model.addAttribute("permCtrExportSign", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_EXPORT_SIGN.getPermissionCode()));
        // 导出合同上下游权限
        model.addAttribute("permCtrExportBuyAndSell", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_EXPORT_BUYANDSELL.getPermissionCode()));
        // 导出业务表权限
        model.addAttribute("permCtrExportBusiness", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_EXPORT_BUSINESS.getPermissionCode()));
        // 合同预览权限
        model.addAttribute("permCtrPreview", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_PREVIEW.getPermissionCode()));
        
        return model;
    }

    @RequestMapping(value = "findByB")
    public String findByB(Model model) {
        model = initData(model);
        model.addAttribute("type", BasConstants.CONTRACTTYPE_BUY);
        return "ctr/buyAndSellContract";
    }

    @RequestMapping(value = "findByS")
    public String findByS(Model model) {
        model = initData(model);
        model.addAttribute("type", BasConstants.CONTRACTTYPE_SELL);
        return "ctr/buyAndSellContract";
    }

    //自营采购合同列表
    @RequestMapping(value = "findZYCG")
    public String findZYCG(Model model) {
        model = initData(model);
        model = checkGcy(model);
        model.addAttribute("type", BasConstants.CONTRACTTYPE_BUY);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_CG);
        return "ctr/buyAndSellContract";
    }

    //自营销售合同列表
    @RequestMapping(value = "findZYXS")
    public String findZYXS(Model model, HttpServletRequest request) {
        model = initData(model);
        model = checkGcy(model);
        model.addAttribute("type", BasConstants.CONTRACTTYPE_SELL);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_XS);
        // 首页跳转标记,1-当日，2-当月
        String flag = request.getParameter("indexFlag");
        String isQy = request.getParameter("isQy");
        String monthNum = request.getParameter("monthNum");
        Integer num = 0;
        if (StringUtils.isNotBlank(monthNum)) {
            num = Integer.valueOf(monthNum);
        }
        if (StringUtils.equals("true", isQy)) {
            model.addAttribute("sealDateStart", StringUtils.isNotBlank(flag) ? getContractTimeStart(flag, num) : null);
            model.addAttribute("sealDateEnd", StringUtils.isNotBlank(flag) ? getContractTimeEnd(flag, num) : null);
        } else {
            model.addAttribute("deliveryDateStart", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateStart(flag, num) : null);
            model.addAttribute("deliveryDateEnd", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateEnd(flag, num) : null);
        }
        model.addAttribute("productTypeCondition", request.getParameter("productType"));
        return "ctr/buyAndSellContract";
    }

    //自营赊销合同列表
    @RequestMapping(value = "findZYSX")
    public String findZYSX(Model model) {
        model = initData(model);
        model.addAttribute("type", BasConstants.CONTRACTTYPE_SELL);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_SX_SX);
        return "ctr/buyAndSellContract";
    }

    //代采合同列表
    @RequestMapping(value = "findDC")
    public String findDC(Model model, HttpServletRequest request) {
        model = initData(model);
        model = checkGcy(model);
        model = checkFileAuthority(model);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_BB);
        model.addAttribute("matchCreditFlg", false);
        // 合同费用调整权限
        boolean updateCostFlg = false;
        if (ShiroUtil.isPermitted(PermissionEnum.CONTRACT_UPDATECOSTFLG.getPermissionCode())) {
            updateCostFlg = true;
        }
        model.addAttribute("updateCostFlg", updateCostFlg);
        // 首页跳转标记,1-当日，2-当月
        String flag = request.getParameter("indexFlag");
        model.addAttribute("contractType", request.getParameter("contractType"));
        String isQy = request.getParameter("isQy");
        String monthNum = request.getParameter("monthNum");
        Integer num = 0;
        if (StringUtils.isNotBlank(monthNum)) {
            num = Integer.valueOf(monthNum);
        }
        model.addAttribute("defaultSealFlg", request.getParameter("defaultSealFlg"));
        // 利润表跳转标识
        String profitFlg = request.getParameter("profitFlg");
        if (StringUtils.equals("true", profitFlg)) {
            model.addAttribute("contractTimeStart", request.getParameter("contractTimeStart"));
            model.addAttribute("contractTimeEnd",request.getParameter("contractTimeEnd"));
        } else {
            if (StringUtils.equals("true", isQy)) {
                model.addAttribute("sealDateStart", StringUtils.isNotBlank(flag) ? getContractTimeStart(flag, num) : null);
                model.addAttribute("sealDateEnd", StringUtils.isNotBlank(flag) ? getContractTimeEnd(flag, num) : null);
            } else {
                model.addAttribute("deliveryDateStart", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateStart(flag, num) : null);
                model.addAttribute("deliveryDateEnd", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateEnd(flag, num) : null);
            }
        }
        model.addAttribute("productTypeCondition", request.getParameter("productType"));

        model.addAttribute("userId", request.getParameter("userId"));
        model.addAttribute("updateCostFlg", updateCostFlg);
        return "ctr/dcContract";
    }

    /**
     * 获取合同开始时间
     *
     * @param flag 标记
     * @return 合同开始时间
     */
    private String getContractTimeStart(String flag, Integer num) {
        LocalDate today = LocalDate.now();
        LocalDate now = today.minusMonths(num);
        DateTimeFormatter partten = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if ("1".equals(flag)) {
            // 如果是今日的
            return now.format(partten);
        } else if ("2".equals(flag)) {
            // 如果是本月的，直接返回本月第一天
            return now.with(TemporalAdjusters.firstDayOfMonth()).format(partten);
        }
        return null;
    }

    /**
     * 获取合同结束时间
     *
     * @param flag 标记
     * @return 合同结束时间
     */
    private String getContractTimeEnd(String flag, Integer num) {
        LocalDate today = LocalDate.now();
        LocalDate now = today.minusMonths(num);
        DateTimeFormatter partten = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if ("1".equals(flag)) {
            // 如果是今日的
            return now.format(partten);
        } else if ("2".equals(flag)) {
            // 如果是本月的，直接返回本月第一天
            return now.with(TemporalAdjusters.lastDayOfMonth()).format(partten);
        }
        return null;
    }

    /**
     * 获取交货开始时间
     *
     * @param flag 标记
     * @return 合同交货时间
     */
    private String getIndexDeliveryDateStart(String flag, Integer num) {
        LocalDate today = LocalDate.now();
        LocalDate now = today.minusMonths(num);
        DateTimeFormatter partten = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if ("1".equals(flag)) {
            // 如果是今日的
            return now.format(partten);
        } else if ("2".equals(flag)) {
            // 如果是本月的，直接返回本月第一天
            return now.with(TemporalAdjusters.firstDayOfMonth()).format(partten);
        }
        return null;
    }

    /**
     * 获取交货结束时间
     *
     * @param flag 标记
     * @return 合同交货时间
     */
    private String getIndexDeliveryDateEnd(String flag, Integer num) {
        LocalDate today = LocalDate.now();
        LocalDate now = today.minusMonths(num);
        DateTimeFormatter partten = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if ("1".equals(flag)) {
            // 如果是今日的
            return now.format(partten);
        } else if ("2".equals(flag)) {
            // 如果是本月的，直接返回本月第一天
            return now.with(TemporalAdjusters.lastDayOfMonth()).format(partten);
        }
        return null;
    }

    //代采赊销合同列表
    @RequestMapping(value = "findDCSX")
    public String findDCSX(Model model, HttpServletRequest request) {
        model = initData(model);
        model = checkGcy(model);
        model = checkFileAuthority(model);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_BB);
        // 更新违约标识权限
        boolean violateTreatyFlgUpdate = false;
        if (ShiroUtil.isPermitted(PermissionEnum.VIOLATETREATYFLG_UPDATE.getPermissionCode())) {
            violateTreatyFlgUpdate = true;
        }
        model.addAttribute("violateTreatyFlgUpdate", violateTreatyFlgUpdate);
        // 合同费用调整权限
        boolean updateCostFlg = false;
        if (ShiroUtil.isPermitted(PermissionEnum.CONTRACT_UPDATECOSTFLG.getPermissionCode())) {
            updateCostFlg = true;
        }
        model.addAttribute("updateCostFlg", updateCostFlg);
        model.addAttribute("matchCreditFlg", true);
        model.addAttribute("payFullTime", request.getParameter("payFullTime"));
        model.addAttribute("payType", request.getParameter("payType"));
        model.addAttribute("warehouseType", request.getParameter("warehouseType"));
        model.addAttribute("contractType", request.getParameter("contractType"));
        model.addAttribute("billCondition", request.getParameter("billCondition"));
        if (StringUtils.equals("B", request.getParameter("unPayType")) || StringUtils.equals("D", request.getParameter("unPayType"))) {
            model.addAttribute("payFullTimeFrom", DateUtils.getDate());
        } else if (StringUtils.equals("C", request.getParameter("unPayType")) || StringUtils.equals("E", request.getParameter("unPayType"))) {
            model.addAttribute("payFullTimeTo", LocalDate.now().plusDays(-1));
        }
        // 首页跳转标记,1-当日，2-当月
        String flag = request.getParameter("indexFlag");
        String toReportQueryFlg = request.getParameter("toReportQueryFlg");
        model.addAttribute("toReportQueryFlg", StringUtils.isNotBlank(toReportQueryFlg) ? "true" : "false");
        String isQy = request.getParameter("isQy");
        String monthNum = request.getParameter("monthNum");
        Integer num = 0;
        if (StringUtils.isNotBlank(monthNum)) {
            num = Integer.valueOf(monthNum);
        }
        model.addAttribute("defaultSealFlg", request.getParameter("defaultSealFlg"));
        // 利润表跳转标识
        String profitFlg = request.getParameter("profitFlg");
        if (StringUtils.equals("true", profitFlg)) {
            model.addAttribute("contractTimeStart", request.getParameter("contractTimeStart"));
            model.addAttribute("contractTimeEnd",request.getParameter("contractTimeEnd"));
        } else {
            if (StringUtils.equals("true", isQy)) {
                model.addAttribute("sealDateStart", StringUtils.isNotBlank(flag) ? getContractTimeStart(flag, num) : null);
                model.addAttribute("sealDateEnd", StringUtils.isNotBlank(flag) ? getContractTimeEnd(flag, num) : null);
            } else {
                model.addAttribute("deliveryDateStart", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateStart(flag, num) : null);
                model.addAttribute("deliveryDateEnd", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateEnd(flag, num) : null);
            }
        }
        model.addAttribute("productTypeCondition", request.getParameter("productType"));



        model.addAttribute("userId", request.getParameter("userId"));
        model.addAttribute("companyNameJump", request.getParameter("companyName"));
        model.addAttribute("ourCompanyNameJump", request.getParameter("ourCompanyName"));
        return "ctr/dcContract";
    }

    @RequestMapping(value = "findDCTP")
    public String findDCTP(Model model, HttpServletRequest request) {
        model = initData(model);
        model = checkGcy(model);
        model = checkFileAuthority(model);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_TP);
        // 更新违约标识权限
        boolean violateTreatyFlgUpdate = false;
        if (ShiroUtil.isPermitted(PermissionEnum.VIOLATETREATYFLG_UPDATE.getPermissionCode())) {
            violateTreatyFlgUpdate = true;
        }
        model.addAttribute("violateTreatyFlgUpdate", violateTreatyFlgUpdate);
        // 合同费用调整权限
        boolean updateCostFlg = false;
        if (ShiroUtil.isPermitted(PermissionEnum.CONTRACT_UPDATECOSTFLG.getPermissionCode())) {
            updateCostFlg = true;
        }
        model.addAttribute("updateCostFlg", updateCostFlg);
        model.addAttribute("matchCreditFlg", false);
        model.addAttribute("payFullTime", request.getParameter("payFullTime"));
        model.addAttribute("payType", request.getParameter("payType"));
        model.addAttribute("warehouseType", request.getParameter("warehouseType"));
        model.addAttribute("contractType", request.getParameter("contractType"));
        model.addAttribute("billCondition", request.getParameter("billCondition"));
        if (StringUtils.equals("B", request.getParameter("unPayType")) || StringUtils.equals("D", request.getParameter("unPayType"))) {
            model.addAttribute("payFullTimeFrom", DateUtils.getDate());
        } else if (StringUtils.equals("C", request.getParameter("unPayType")) || StringUtils.equals("E", request.getParameter("unPayType"))) {
            model.addAttribute("payFullTimeTo", LocalDate.now().plusDays(-1));
        }
        // 首页跳转标记,1-当日，2-当月
        String flag = request.getParameter("indexFlag");
        String toReportQueryFlg = request.getParameter("toReportQueryFlg");
        model.addAttribute("toReportQueryFlg", StringUtils.isNotBlank(toReportQueryFlg) ? "true" : "false");
        String isQy = request.getParameter("isQy");
        String monthNum = request.getParameter("monthNum");
        Integer num = 0;
        if (StringUtils.isNotBlank(monthNum)) {
            num = Integer.valueOf(monthNum);
        }
        // 利润表跳转标识
        String profitFlg = request.getParameter("profitFlg");
        if (StringUtils.equals("true", profitFlg)) {
            model.addAttribute("contractTimeStart", request.getParameter("contractTimeStart"));
            model.addAttribute("contractTimeEnd",request.getParameter("contractTimeEnd"));
        } else {
            if (StringUtils.equals("true", isQy)) {
                model.addAttribute("contractTimeStart", StringUtils.isNotBlank(flag) ? getContractTimeStart(flag, num) : null);
                model.addAttribute("contractTimeEnd", StringUtils.isNotBlank(flag) ? getContractTimeEnd(flag, num) : null);
            } else {
                model.addAttribute("deliveryDateStart", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateStart(flag, num) : null);
                model.addAttribute("deliveryDateEnd", StringUtils.isNotBlank(flag) ? getIndexDeliveryDateEnd(flag, num) : null);
            }
        }



        model.addAttribute("userId", request.getParameter("userId"));
        model.addAttribute("companyNameJump", request.getParameter("companyName"));
        return "ctr/dcTpContract";
    }

    public void handelModelAttr(){

    }

    // 库存采购合同列表
    @RequestMapping(value = "findVirtual")
    public String findVirtual(Model model, HttpServletRequest request) {
        initData(model);
        checkGcy(model);
        checkFileAuthority(model);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_KC_CG);
        model.addAttribute("matchCreditFlg", false);
        return "ctr/kcCGContract";
    }

    //代采赊销预算合同列表
    @RequestMapping(value = "findDCSXYS")
    public String findDCSXYS(Model model, HttpServletRequest request) {
        model = initData(model);
        model = checkGcy(model);
        model = checkFileAuthority(model);

        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_BB);
        model.addAttribute("matchCreditFlg", true);
        model.addAttribute("sealFlgJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULTFLG)));// 是否盖章查询条件
        model.addAttribute("payType", request.getParameter("payType"));

        model.addAttribute("sealFlg", request.getParameter("sealFlg"));

        model.addAttribute("companyName", request.getParameter("companyName"));
        model.addAttribute("contractDateBegin", request.getParameter("contractDateBegin"));
        model.addAttribute("contractDateEnd", request.getParameter("contractDateEnd"));
        model.addAttribute("contractStatus", request.getParameter("contractStatus"));
        model.addAttribute("billType", request.getParameter("billType"));
        model.addAttribute("receiveType", request.getParameter("receiveType"));
        model.addAttribute("invoiceBillType", request.getParameter("invoiceBillType"));
        model.addAttribute("productType", request.getParameter("productType"));

        String ourCompanyName = request.getParameter("ourCompanyName");
        model.addAttribute("ourCompanyName", ourCompanyName);
        if (StringUtils.isEmpty(ourCompanyName)) {
            String allOurCompanyFlag = request.getParameter("allOurCompanyFlag");
            if (StringUtils.equals("true",allOurCompanyFlag)) {
                List<BsDictData> companyOurFalagList = BsCompanyOurUtil.getCompanyOurFlagToBsDictDataList();
                List<String> ourCompanyNames = companyOurFalagList.stream()
                        .map(data -> data.getDictName().trim()) // 去除每个 dictName 两端的空格
                        .collect(Collectors.toList());
                model.addAttribute("ourCompanyName", ourCompanyNames);
            }
        }

        //查看企业详情权限
        boolean updateFileIdFlg = false;
        if (ShiroUtil.isPermitted(PermissionEnum.CTR_DCSXUPDATEFILEID_FLG.getPermissionCode())) {
            updateFileIdFlg = true;
        }
        model.addAttribute("dcsxUpdateFileIdFlg", updateFileIdFlg);
        final ShiroUser shiroUser = ShiroUtil.getShiroUser();
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.ACCOUNT_ASY);
        long count = listByCategory.stream().filter(s -> StringUtils.equals(s.getDictName(), shiroUser.loginName)).count();
        if (count > 0) {
            model.addAttribute("asy", "true");
        }
        return "ctr/dcsxContract";
    }

    @RequestMapping(value = "findDCSXTP")
    public String findDCSXTP(Model model, HttpServletRequest request) {
        model = initData(model);
        model = checkGcy(model);
        model = checkFileAuthority(model);

        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_TP);
        model.addAttribute("matchCreditFlg", true);
        model.addAttribute("sealFlgJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULTFLG)));// 是否盖章查询条件
        model.addAttribute("payType", request.getParameter("payType"));
        model.addAttribute("ourCompanyName", request.getParameter("ourCompanyName"));
        model.addAttribute("sealFlg", request.getParameter("sealFlg"));

        model.addAttribute("companyName", request.getParameter("companyName"));
        model.addAttribute("contractDateBegin", request.getParameter("contractDateBegin"));
        model.addAttribute("contractDateEnd", request.getParameter("contractDateEnd"));
        model.addAttribute("contractStatus", request.getParameter("contractStatus"));
        model.addAttribute("billType", request.getParameter("billType"));
        model.addAttribute("receiveType", request.getParameter("receiveType"));
        model.addAttribute("invoiceBillType", request.getParameter("invoiceBillType"));


        //查看企业详情权限
        boolean updateFileIdFlg = false;
        if (ShiroUtil.isPermitted(PermissionEnum.CTR_DCSXUPDATEFILEID_FLG.getPermissionCode())) {
            updateFileIdFlg = true;
        }
        model.addAttribute("dcsxUpdateFileIdFlg", updateFileIdFlg);
        final ShiroUser shiroUser = ShiroUtil.getShiroUser();
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.ACCOUNT_ASY);
        long count = listByCategory.stream().filter(s -> StringUtils.equals(s.getDictName(), shiroUser.loginName)).count();
        if (count > 0) {
            model.addAttribute("asy", "true");
        }
        return "ctr/dcsxContract-tp";
    }

    @RequestMapping(value = "getConfirmReceiptFlg", method = RequestMethod.POST)
    @ResponseBody
    public Boolean getConfirmReceiptFlg(@RequestParam("approveId") Long approveId) {
        ApplyCtrDCSX applyCtrDCSX = ctrDcsxClinent.findByDCSXApproveId(approveId);
        BigDecimal confirmReceiveNumber = null;
        BigDecimal applyConfirmReceiptNumber = null;
        if (Objects.nonNull(applyCtrDCSX)) {
            // 中游已确认收货数量
            confirmReceiveNumber = applyCtrDCSX.getConfirmReceiveNumber();
            BigDecimal totalNumber = applyCtrDCSX.getTotalNumber();
            //  已确认收货数量小于合同数量
            if (confirmReceiveNumber != null && totalNumber != null) {
                if (confirmReceiveNumber.compareTo(totalNumber) >= 0) {
                    return false;
                }
            }
            // 判断我方是否处于中游
            BsCompanyOurSearchVo searchVo = new BsCompanyOurSearchVo();
            searchVo.setCompanyName(applyCtrDCSX.getCompanyName());
            BsCompanyOur companyOur = companyOurClient.getCompanyOurDetail(searchVo);
            if (Objects.nonNull(companyOur) && !companyOur.getOurCompanyFlag()) {
                return false;
            }
            CtrContractDcsxApply contractDcsxApply = contractDcsxApplyClient.findByContractId(applyCtrDCSX.getId());
            if (Objects.nonNull(contractDcsxApply)) {
                // 占用数量
                applyConfirmReceiptNumber = contractDcsxApply.getApplyConfirmReceiptNumber();
                if (applyConfirmReceiptNumber == null) {
                    applyConfirmReceiptNumber = BigDecimal.ZERO;
                }
            }

            List<CtrContract> contractList = ctrContractClient.findByApproveId(approveId);
            // 下游已出库数量大于中游已确认收货数量
            if (!CollectionUtil.isNullOrEmpty(contractList)) {
                for (CtrContract contract : contractList) {
                    if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contract.getContractType())) {
                        BigDecimal warehouseNumber = contract.getWarehouseNumber();
                        if (warehouseNumber != null && confirmReceiveNumber != null) {
                            if (warehouseNumber.compareTo(confirmReceiveNumber) > 0) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是观察员
     *
     * @param model
     * @return
     */
    private Model checkGcy(Model model) {
        boolean isGcy = false;
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            isGcy = true;
        }
        model.addAttribute("isGcy", isGcy);
        return model;
    }

    /**
     * 判断是否有附件替换功能
     *
     * @param model
     * @return
     */
    private Model checkFileAuthority(Model model) {
        boolean canEditContractFile = false;
        // 业务助理
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BIZ_OPE.getPermissionCode())) {
            canEditContractFile = true;
        }
        model.addAttribute("canEditContractFile", canEditContractFile);
        return model;
    }

    //白条逾期预算列表
    @RequestMapping(value = "findOverdue")
    public String findOverdue(Model model) {
        model = initData(model);
        model.addAttribute("businessType", BasConstants.BUSINESS_TYPE_ZY_BB);
        model.addAttribute("matchCreditFlg", true);
        return "ctr/overdueContract";
    }

    //Saass采购合同
    @RequestMapping(value = "buySaasContract")
    public String buySaasContract(Model model) {
        model = initData(model);
        model.addAttribute("type", BasConstants.CONTRACTTYPE_BUY);
        model.addAttribute("contractSource", BasConstants.APPLY_SAAS_TYPE_AB);
        return "ctr/saasContract";
    }

    //Saass采购合同
    @RequestMapping(value = "sellSaasContract")
    public String sellSaasContract(Model model) {
        model = initData(model);
        model.addAttribute("type", BasConstants.CONTRACTTYPE_SELL);
        model.addAttribute("contractSource", BasConstants.APPLY_SAAS_TYPE_AS);
        return "ctr/saasContract";
    }

    @RequestMapping(value = "registerFee")
    public String registerFee(Model model) {
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        //预售合同发起采购权限
        Boolean preSellFlg = canStartBuy();
        model.addAttribute("preSellFlg", preSellFlg);
        return "ctr/buyAndSellContract";
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        /** 付款方式 */
        model.addAttribute("payTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        // 质量标准
        model.addAttribute("qualityStandardJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        // 交货方式
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_BUYDELIVERY)));
        // 交货时间的补充字段
        model.addAttribute("attachDeliveryTimeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
        // 销售方式
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("applyTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        //包装规格-全部
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
//        List<BsWarehouse> warehouseList = warehouseClient.findAll();
//        model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
        CtrContract entity = ctrContractClient.getEntity(id);
        List<CtrContract> ctr = ctrContractClient.findByLinkContractIdLink("," + entity.getId() + ",");
        String businessType = entity.getBusinessType();
        if (StringUtils.isNotBlank(businessType)) {
            if (BasConstants.BUSINESS_TYPE_ZY_BB.equals(businessType)) {
                model.addAttribute("businessType", entity.getMatchCreditFlg() ? "白条" : "代采");
            } else {
                model.addAttribute("businessType", DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESSTYPE, businessType));
            }
        }
        boolean hasNext = false;
        if (ctr.size() > 0 || StringUtils.isNotBlank(entity.getLinkContractId())) {
            hasNext = true;
        }
        model.addAttribute("hasNext", hasNext);
        //合同状态 contractStatusJson
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
        boolean cancelFlg = false;
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_CTR_CANCEL.getPermissionCode()) && !entity.getContractStatus().equals(BasConstants.CONTRACTSTATUS_C)) {
            List<CtrContract> contractList = ctrContractClient.findApproveByOrder(entity);
            if (contractList != null && contractList.size() > 0) {
                //采购或销售
                if (contractList.size() == 1) {
                    //预售采购不能作废
                    if (!entity.getSource().equals(BasConstants.APPLY_TYPE_A) && entity.findRealDealedAmount().compareTo(BigDecimal.ZERO) == 0 && entity.getWarehouseNumber().compareTo(BigDecimal.ZERO) == 0) {
                        cancelFlg = true;
                    }
                } else {
                    //撮合业务或进口代理
                    CtrContract sellContract = contractList.get(0);
                    CtrContract buyContract = contractList.get(1);
                    if (buyContract.findRealDealedAmount().compareTo(BigDecimal.ZERO) == 0 && sellContract.findRealDealedAmount().compareTo(BigDecimal.ZERO) == 0
                            && sellContract.getWarehouseNumber().compareTo(BigDecimal.ZERO) == 0) {
                        cancelFlg = true;
                    }
                }
            }
        }
        //如果是从库存明细过来的请求，合同详情界面部分控件是不需要展示的
        boolean isFromContract = true;
        String from = request.getParameter("from");
        if (from != null && from.equals("stock")) {
            isFromContract = false;
        }
        Boolean warehouseFlg = entity.getWarehouseFlg();
        if (warehouseFlg == null) {
            entity.setWarehouseFlg(false);
        }
        model.addAttribute("entity", entity);
        model.addAttribute("isFromContract", isFromContract);
        model.addAttribute("cancelFlg", cancelFlg);
        //合同调整权限
        boolean canAdjust = false;
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_ADJUST.getPermissionCode())) {
            canAdjust = true;
        }
        model.addAttribute("canAdjust", canAdjust);
        if (StringUtils.equals(businessType, BasConstants.BUSINESS_TYPE_ZY_JK)) {
            ApplyImportBuy importBuy = applyImportBuyClient.findByContractId(entity.getId());
            model.addAttribute("importBuy", importBuy);
        } else if (StringUtils.equals(businessType, BasConstants.BUSINESS_TYPE_DL_KZ)) {
            ApplyImportDetail importDetail = applyImportDetailClient.findByContractId(entity.getId());
            model.addAttribute("importBuy", importDetail);
        }
        //罚息输入权限
        model.addAttribute("interestFlg", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_INTEREST.getPermissionCode()));
        //删除合同附件权限
        model.addAttribute("deleteFileFlg", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_DELETE_FILEID.getPermissionCode()));
        // 合同预览权限
        model.addAttribute("permCtrPreview", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_PREVIEW.getPermissionCode()));
        return "ctr/contract-detail";
    }

    @RequestMapping(value = "loss/{id}", method = RequestMethod.GET)
    public String loss(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        CtrContractChooseVo findByContractId = ctrContractClient.findByContractId(id);
        CtrContractLoss lossEntity = ctrContractLossClient.findByContractId(id);
        if (org.springframework.util.StringUtils.isEmpty(lossEntity)) {
            lossEntity = new CtrContractLoss();
        }
        model.addAttribute("contractEntity", findByContractId);
        model.addAttribute("lossEntity", lossEntity);
        return "ctr/contract-loss";
    }

    //合同损耗情况记录
    @RequestMapping(value = "lossLog/{id}", method = RequestMethod.GET)
    public String lossLog(@PathVariable("id") Long id, Model model) {
        if (id != null && id > 0L) {
            model.addAttribute("contractId", id);
            //model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        }
        return "ctr/contract-lossLog";
    }

    @RequestMapping(value = "detailLossLog/{id}", method = RequestMethod.POST)
    public void detailLossLog(@PathVariable("id") Long id, HttpServletResponse response) {
        if (id != null && id > 0l) {
            PageSearchVo searchVo = new PageSearchVo();
            searchVo.setSort("id");
            searchVo.setOrder("DESC");
            searchVo.setRows(50);
            Map<String, Object> searchParams = new HashMap<String, Object>();
            searchParams.put("EQL_contractId", id);
            searchVo.setSearchParams(searchParams);
            PageDown<CtrContractLoss> findPage = ctrContractLossClient.findPage(searchVo);
            JsonEasyUI.renderJson(response, findPage);
        }
    }

    @RequestMapping(value = "getApproveHistory", method = RequestMethod.POST)
    public void detailLossLog(ContractSearchVo searchVo, HttpServletResponse response) {
        List<PmApproveHistoryVo> historyList = ctrContractClient.getApproveHistory(searchVo);
        JsonEasyUI.renderListJson(response, historyList);
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }


    @RequestMapping(value = "contractList")
    public void contractList(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        dealWithPerformanceStatus(searchVo);
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
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
            setFunderCompany(searchVo);
            searchVo.setFundViewFlag(true);
            PageDown<RptCtrContractRptVo> rptContractPage = ctrContractReportClient.findRptContractPage(searchVo);
            searchVo.setCount(-1);
            RptCtrContractRptVo sum = ctrContractReportClient.findRptSumPageContract(searchVo);
            Map<String, Object> footer = getContractSumFooter(sum);
            JsonEasyUI.renderJson(response, rptContractPage, null, footer);
            return;
        }
        String toReportQueryFlg = request.getParameter("toReportQueryFlg");
        if (StringUtils.equals("true",toReportQueryFlg)) {
            PageDown<ContractShowVo> rptContractPage = ctrContractReportClient.findIndexRptContractPage(searchVo);
            searchVo.setCount(-1);
            RptCtrContractRptVo sum = ctrContractReportClient.findIndexRptSumPageContract(searchVo);
            Map<String, Object> footer = getContractSumFooter(sum);
            JsonEasyUI.renderJson(response, rptContractPage, null, footer);
            return;
        }
        Boolean piccRemainCredit = searchVo.getPiccRemainCredit();
        Map<String, Object> searchParams = searchVo.getSearchParams();
        if (piccRemainCredit != null) {
            if (piccRemainCredit) {
                searchParams.put("GTEM_piccRemainCredit", BigDecimal.ZERO);
            } else {
                searchParams.put("LTM_piccRemainCredit", BigDecimal.ZERO);
            }
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            searchParams.put("NEQS_hideOut", "1");
        }
        //中心负责人ID
        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }

        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
            String dictLabel = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_ASSISTANT_DICT, ShiroUtil.getCurrentUserId() + "");
            List<Long> deptIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(dictLabel)) {
                String[] split = dictLabel.split(",");
                // 使用 Stream 将 String 数组转换为 List<Long>
                deptIdList = java.util.Arrays.stream(split)
                        .map(Long::parseLong)  // 将 String 转换为 Long
                        .collect(Collectors.toList());  // 收集到 List<Long>

            } else {
                deptIdList.add(-1L);
            }
            searchVo.setDeptIdList(deptIdList);
            
        }

        //1.可以查看本中心所有预售合同权限
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        //2.可以查看本中心所有合同权限
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        //2.可以查看本中心所有的保理合同
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CUST_VIEWBL.getPermissionCode())) {
            searchVo.setSearchType("B");
        }
        List<Long> deptIdList = searchVo.getDeptIdList();
        if (!ShiroUtil.isPermitted(PermissionEnum.PERM_ZGBAS_CUSTOMER_DEPT_VIEW.getPermissionCode())
                && (deptIdList == null || !deptIdList.contains(67957L))){
            searchParams.put("NEQS_owningRegion", "KH");
        }
        if (searchParams != null && Objects.equals(67957L, ShiroUtil.getDeptId())) {
            searchParams.remove("NEQS_owningRegion");
        }
        logger.info("searchVo : " + JsonUtil.obj2Json(searchVo));

        Map<String, Object> footer = new HashMap<>();
        Page<ContractShowVo> page = ctrContractClient.findPageContract(searchVo);
        List<String> str = productTypeClient.findAllProductAlAndHg().stream().map(s -> s.getTypeName()).collect(Collectors.toList());
        List<ContractShowVo> content = page.getContent();
        String join = StringUtils.join(",", str);
        content.stream().forEach(s -> {
            String productsName = s.getProductsName();
            int length = productsName.length();
            String replace = productsName.replace("/", "");
            int length1 = replace.length();
            int len = length - length1;
            if (len >= 1) {
                String substring = productsName.substring(0, productsName.indexOf("/"));
                if (join.indexOf(substring) > 0) {
                    if (len > 1) {
                        int index = s.getProductsName().indexOf("/");
                        int index2 = s.getProductsName().indexOf("/", index + 1);
                        String sub = s.getProductsName().substring(index, index2);
                        String s1 = s.getProductsName().replaceAll(sub, "");
                        s.setProductsName(s1);
                    } else {
                        int index = s.getProductsName().indexOf("/");
                        String sub = s.getProductsName().substring(index);
                        String s1 = s.getProductsName().replaceAll(sub, "");
                        s.setProductsName(s1);
                    }
                }
            }
        });
        //不带条件查询
        //CtrContract sum= ctrContractClient.sumPage(searchVo.getSearchParams());
        CtrContract sum = ctrContractClient.sumPageContract(searchVo);
        footer.put("companyName", "合计");
        footer.put("totalNumber", sum.getTotalNumber());
        footer.put("warehouseNumber", sum.getWarehouseNumber());
        footer.put("totalAmount", sum.getTotalAmount());
        footer.put("dealedAmount", sum.getDealedAmount());
        footer.put("billedAmount", sum.getBilledAmount());
        footer.put("receivableBalance", sum.getLossAmount());
        footer.put("receivablePrincipal", sum.getReceiveServiceAmount());
        footer.put("warehouseAmount", sum.getWarehouseAmount());
        footer.put("transportAmount", sum.getTransportAmount());
        footer.put("stevedorage", sum.getStevedorage());
        footer.put("breachAmount", sum.getBreachAmount());
        footer.put("receiveBreachAmount", sum.getReceiveBreachAmount());
        footer.put("insuranceAmount", sum.getInterestAmount());
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    private Map<String, Object> getContractSumFooter(RptCtrContractRptVo sum) {
        Map<String, Object> footer = new HashMap<>();
        if (Objects.nonNull(sum)) {
            footer.put("companyName", "合计");
            footer.put("totalNumber", sum.getTotalNumber());
            footer.put("warehouseNumber", sum.getWarehouseNumber());
            footer.put("totalAmount", sum.getTotalAmount());
            footer.put("dealedAmount", sum.getDealedAmount());
            footer.put("billedAmount", sum.getBilledAmount());
            footer.put("receivableBalance", sum.getReceivableBalance());
            footer.put("warehouseAmount", sum.getWarehouseAmount());
            footer.put("transportAmount", sum.getTransportAmount());
            footer.put("stevedorage", sum.getStevedorage());
            footer.put("breachAmount", sum.getBreachAmount());
            footer.put("receiveBreachAmount", sum.getReceiveBreachAmount());
            footer.put("insuranceAmount", sum.getInterestAmount());
        }
        return footer;
    }

    //作废
    @RequestMapping(value = "invalidTheContract")
    public void invalidTheContract(CtrConctractInvalidVo vo, HttpServletResponse response) {
        try {
            vo.setUserId(ShiroUtil.getCurrentUserId());
            vo.setUserName(ShiroUtil.getCurrentUserName());
            ctrContractClient.invalidTheContract(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            String msg = e.getMessage();
            if (e.getCause() != null) {
                msg = e.getCause().getMessage();
            }
            RenderUtil.renderFailure(msg, response);
        }

    }

    /**
     * 修改合同预估运费预估仓储费及罚息
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(CtrContractUpdateVo updateVo, HttpServletRequest request,
                     HttpServletResponse response) {
        try {
            //CtrContract ctrContract = ctrContractClient.getEntity(entity.getId());
            //ctrContract.setTransportAmount(entity.getTransportAmount());
            //ctrContract.setWarehouseAmount(entity.getWarehouseAmount());
            //entity = getService().save(ctrContract);
            //ctrContractClient.saveCtrContract(ctrContract);
            updateVo.setBizUserId(ShiroUtil.getCurrentUserId());
            updateVo.setBizUserName(ShiroUtil.getCurrentUserName());
            ctrContractClient.updateContractAmount(updateVo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "findByCtrContractId")
    public void findByCtrContractId(HttpServletRequest request, HttpServletResponse response, Long contractId) {
        List<CtrProduct> ctrProductList = ctrProductClient.findByOutCtrContractId(contractId);
        RenderUtil.renderJson(ctrProductList, response);
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "updateCtrFileId", method = RequestMethod.POST)
    public void updateCtrFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateCtrFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 更新开票附件
     *
     * @param vo
     * @param response
     */
    @RequestMapping(value = "updateInvoiceFileId", method = RequestMethod.POST)
    public void updateInvoiceFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateInvoiceFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 更新债权凭证附件
     *
     * @param vo
     * @param response
     */
    @RequestMapping(value = "updateDebtCertificateFileId", method = RequestMethod.POST)
    public void updateDebtCertificateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateDebtCertificateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }


    /**
     * 更新确认商品附件
     *
     * @param vo
     * @param response
     */
    @RequestMapping(value = "updateGoodsFileId", method = RequestMethod.POST)
    public void updateGoodsFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateGoodsFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "updateWarehouseFileId", method = RequestMethod.POST)
    public void updateWarehouseFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateWarehouseFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "updateDoubleCheckFileId", method = RequestMethod.POST)
    public void updateDoubleCheckFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateDoubleCheckFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }


    /**
     * 打开签约窗口
     */
    @RequestMapping(value = "openSigning/{id}", method = RequestMethod.GET)
    public String openSigning(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        CtrContract entity = ctrContractClient.getEntity(id);
        model.addAttribute("entity", entity);
        return "ctr/contract-upSigning";
    }

    /**
     * 打开更新附件窗口(代采赊销合同)
     */
    @RequestMapping(value = "openUpdateFile/{id}", method = RequestMethod.GET)
    public String openUpdateFile(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        ApplyCtrDCSX entity = ctrDcsxClinent.getEntity(id);
        model.addAttribute("entity", entity);
        return "ctr/contract-updateContractFile";
    }

    /**
     * 打开更新附件窗口
     */
    @RequestMapping(value = "openCtrUpdateFile/{id}", method = RequestMethod.GET)
    public String openCtrUpdateFile(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        CtrContract entity = ctrContractClient.getEntity(id);
        List<ApplyConfirmReceipt> applyConfirmReceiptList = applyConfirmReceiptClient.findByContractId(entity.getId());
        boolean b = Objects.isNull(applyConfirmReceiptList);
        if (Boolean.FALSE.equals(b)) {
            for (ApplyConfirmReceipt applyConfirmReceipt : applyConfirmReceiptList) {
                boolean spts = applyConfirmReceipt.getContractNo().contains("S");
                if (spts) {
                    String fileId = applyConfirmReceipt.getFileId();
                    entity.setFileId(fileId);
                }
            }
        }
        model.addAttribute("entity", entity);
        return "ctr/contract-updateCtrContractFile";
    }

    /**
     * 更新资料上传状态
     */
    @RequestMapping(value = "uploadComplete/{id}", method = RequestMethod.POST)
    public void uploadComplete(@PathVariable("id") Long id, HttpServletResponse response) {
        CtrContract entity = ctrContractClient.getEntity(id);
        if (judgeFileId(entity)) {
            entity.setFactorStatus(BasConstants.FACTOR_STATUS_Z);
            ctrContractClient.save(entity);
            // applyCtrContractFactoClient.autoLaunchApplyPay(entity.getContractNo());
        }
        RenderUtil.renderJson(entity, response);
    }

    /**
     * 保理模式打开更新附件窗口
     */
    @RequestMapping(value = "openBLCtrUpdateFile/{id}", method = RequestMethod.GET)
    public String openBLCtrUpdateFile(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        boolean editFlg = true;
        String editFlgStr = request.getParameter("editFlg");
        if (StringUtils.isNotBlank(editFlgStr)) {
            editFlg = Boolean.parseBoolean(editFlgStr);
        }
        model.addAttribute("entity", ctrContractClient.refreshFactorStatus(id));
        model.addAttribute("editFlg", editFlg);
        return "ctr/contract-updateBLCtrContractFile";
    }


    /**
     * 执行签约
     */
    @RequestMapping(value = "doSigning", method = RequestMethod.POST)
    public void doSigning(CtrContractSignRequest vo, HttpServletResponse response) {
        try {
            //判断签约权限
            Boolean signingFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_SIGNING.getPermissionCode());
            if (signingFlg) {
                CtrContract entity = ctrContractClient.getEntity(vo.getCtrContractId());
                if (StringUtils.equals(BasConstants.CONTRACTSTATUS_S, entity.getContractStatus())
                        || StringUtils.equals(BasConstants.CONTRACTSTATUS_G1, entity.getContractStatus())) {
                    RenderUtil.renderFailure("提示:" + "该合同已签约，请刷新页面!", response);
                    return;
                }
                vo.setUserId(ShiroUtil.getCurrentUserId());
                vo.setUserName(ShiroUtil.getCurrentUserName());
                ctrContractClient.doSigning(vo);
                RenderUtil.renderSuccess("success", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 刷新电子合同
     */
    @RequestMapping(value = "refreshContractText", method = RequestMethod.POST)
    public void refreshContractText(@RequestParam(value = "contractId") Long contractId, HttpServletResponse response) {
        try {
            if (contractId != null && contractId != 0L) {
                ctrContractClient.refreshContractText(contractId);
                RenderUtil.renderSuccess("success", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "getEntityById")
    public String getEntityById(@RequestParam(value = "id") Long id, Model model) {
        CtrContract entity = ctrContractClient.getEntity(id);
        model.addAttribute("entity", entity);
        return "ctr/contract-confirm";
    }

    @RequestMapping(value = "updateConfirmNumber", method = RequestMethod.POST)
    public void updateConfirmNumber(CtrContract entity, HttpServletRequest request,
                                    HttpServletResponse response) {
        try {
            CtrContractOphisRequest ophisRequest = new CtrContractOphisRequest();
            ophisRequest.setCtrContractId(entity.getId());
            ophisRequest.setApplyType(BasConstants.APPLY_TYPE_G);
            ophisRequest.setDealNumber(entity.getConfirmReceiveNumber());
            ophisRequest.setFileId(entity.getWarehouseFileId());
            ophisRequest.setCreateUserId(ShiroUtil.getCurrentUserId());
            ophisRequest.setCreateUserName(ShiroUtil.getCurrentUserName());
            ophisRequest.setHappenDate(entity.getDeliveryInTime());
            if (null != entity.getDeliveryInTime()) {
                ophisRequest.setDeliveryInTime(entity.getDeliveryInTime());
            }
            ctrContractClient.updateConfirmReceiveNumber(ophisRequest);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "findById", method = RequestMethod.POST)
    @ResponseBody
    public CtrContractChooseVo findById(@RequestParam("contractId") Long contractId) {
        CtrContractChooseVo ctr = ctrContractClient.findByContractId(contractId);
        return ctr;
    }

    //合同详情 上下家信息展示
    @RequestMapping(value = "findOpponent/{contractId}", method = RequestMethod.GET)
    public String findOpponent(@PathVariable("contractId") Long contractId, Model model) {
        CtrContract entity = ctrContractClient.getEntity(contractId);
        model.addAttribute("payTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("applyTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        List<BsWarehouse> warehouseList = warehouseClient.findAll();
        model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
        //业务小类
        model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
        model.addAttribute("contract_id", contractId);
        model.addAttribute("entity", entity);
        return "ctr/contract-oppon";
    }

    //合同详情 上下家信息展示
    @RequestMapping(value = "findMlOpponent/{approveId}", method = RequestMethod.GET)
    public String findMlOpponent(@PathVariable("approveId") Long approveId, Model model) {
        model.addAttribute("payTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("applyTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        List<BsWarehouse> warehouseList = warehouseClient.findAll();
        model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
        //业务小类
        model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
        model.addAttribute("approveId", approveId);
        return "ctr/contract-mlOppon";
    }

    @RequestMapping(value = "detailHisPage/{contractId}", method = RequestMethod.POST)
    public void detailHisPage(@PathVariable("contractId") Long contractId, HttpServletResponse response) {
        PageSearchVo searchVo = new PageSearchVo();
        searchVo.setSort("id");
        searchVo.setOrder("DESC");
        searchVo.setRows(50);
        CtrContract entity = ctrContractClient.getEntity(contractId);
        Map<String, Object> searchParams = new HashMap<String, Object>();
        //销售
        if (StringUtils.isNotBlank(entity.getLinkContractId())) {
            String link = entity.getLinkContractId();
            String[] links = link.split(",");
            Long[] linkContractId = FormConfigUtil.formateArray(links);
            searchParams.put("INL_id", linkContractId);
            // 采购
        } else {
            searchParams.put("LIKES_linkContractId", "," + contractId + ",");
        }
        searchVo.setSearchParams(searchParams);
        PageDown<CtrContract> findPage = ctrContractClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, findPage);
    }

    @RequestMapping(value = "detailMlHisPage/{approveId}", method = RequestMethod.POST)
    public void detailMlHisPage(@PathVariable("approveId") Long approveId, HttpServletResponse response) {
        if (approveId != null && approveId != 0L) {
            PageSearchVo searchVo = new PageSearchVo();
            searchVo.setSort("id");
            searchVo.setOrder("DESC");
            searchVo.setRows(50);
            List<Long> contractList = new ArrayList<>();
            List<ApplyMatchDetail> matchDetailList = applyMatchDetailClient.findByApproveId(approveId);
            List<ApplyImportDetail> importDetailList = applyImportDetailClient.findByApproveId(approveId);
            if (matchDetailList != null && matchDetailList.size() > 0) {
                contractList = matchDetailList.stream().map(m -> m.getContractId()).collect(Collectors.toList());
            } else if (importDetailList != null && importDetailList.size() > 0) {
                contractList = importDetailList.stream().map(i -> i.getContractId()).collect(Collectors.toList());
            }
            if (!contractList.isEmpty()) {
                Map<String, Object> searchParams = new HashMap<String, Object>();
                searchParams.put("INL_id", contractList);
                searchParams.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
                searchVo.setSearchParams(searchParams);
                PageDown<CtrContract> findPage = ctrContractClient.findPage(searchVo);
                JsonEasyUI.renderJson(response, findPage);
            }
        }
    }

    @RequestMapping(value = "detailAmount/{id}", method = RequestMethod.GET)
    public String detailAmount(@PathVariable("id") Long id, Model model) {
        if (id != null && id > 0L) {
            CtrContract ctr = ctrContractClient.getEntity(id);
            model.addAttribute("ctrContractId", id);
            model.addAttribute("contractType", ctr.getContractType());
            model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
            model.addAttribute("payModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        }
        return "apply/payed-detail";
    }

    @RequestMapping(value = "findDetailByContractId", method = RequestMethod.POST)
    @ResponseBody
    public CtrContractDetailVo findDetailByContractId(@RequestParam("contractId") Long contractId) {
        if (contractId != null && contractId != 0L) {
            return ctrContractClient.findDetailByContractId(contractId);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "findDetailByContractNo", method = RequestMethod.POST)
    public Object findDetailByContractNo(@RequestParam("contractNo") String contractNo, @RequestParam("endDate") String endDate) {
        
        if (StringUtils.isNotBlank(contractNo)) {
            ProtocolDocumentContractVo protocolDocumentContractVo = new ProtocolDocumentContractVo();
            CtrContract contract = ctrContractClient.findByContractNoV2(contractNo);
            if (Objects.isNull(contract)) {
                ApplyCtrDCSX ctrDCSX = ctrDcsxClinent.findByContractNo(contractNo);
                ProtocolDocumentDcsxVo protocolDocumentDcsxVo = new ProtocolDocumentDcsxVo();
                if (Objects.nonNull(ctrDCSX)) {
                    BeanUtils.copyProperties(ctrDCSX, protocolDocumentDcsxVo);
                    List<CtrContract> contractList = ctrContractClient.findByApproveId(ctrDCSX.getApproveId());
                    if (!CollectionUtils.isEmpty(contractList)) {
                        CtrContract ctrContract = contractList.get(0);
                        protocolDocumentDcsxVo.setProductsName(ctrContract.getProductsName());
                    }
                    protocolDocumentDcsxVo.setBankName(ctrDCSX.getBankName());
                    protocolDocumentDcsxVo.setBankAccount(ctrDCSX.getBankAccount());
                    // 获取企业社会信用代码
                    BsCompanyOurSearchVo bsCompanyOurSearchVo = new BsCompanyOurSearchVo();
                    bsCompanyOurSearchVo.setCompanyName(protocolDocumentDcsxVo.getOurCompanyName());
                    BsCompanyOur bsCompanyOur = bsCompanyOurClient.getCompanyOurDetail(bsCompanyOurSearchVo);
                    BsCompany bsCompany = bsCompanyClient.findByCompanyName(protocolDocumentDcsxVo.getCompanyName());
                    protocolDocumentDcsxVo.setOurCompanyNo(Objects.nonNull(bsCompanyOur) ? bsCompanyOur.getCompanyTaxNo() : "");
                    protocolDocumentDcsxVo.setCompanyNo(Objects.nonNull(bsCompany) ? bsCompany.getCompanyCreditNo() : "");
                }
                return protocolDocumentDcsxVo;
            } else {
                // 根据截止日期重新计算逾期罚息
                if (StringUtils.isNotBlank(endDate)) {
                    BigDecimal breachRate = contract.getBreachRate();
                    if (Objects.nonNull(breachRate)) {
                        BigDecimal needReceiveAmount = contract.getTotalAmount().subtract(contract.getDealedAmount());
                        BigDecimal breachAmount = contract.getBreachAmount();
                        Date endDateFormat = DateUtil.parseDate(endDate);
                        LocalDate endDateLocalDate = endDateFormat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate newLocalDate = LocalDate.now();
                        long days = ChronoUnit.DAYS.between(newLocalDate, endDateLocalDate);
                        BigDecimal overdueLateFee = needReceiveAmount.multiply(breachRate).multiply(new BigDecimal(days)).setScale(3, BigDecimal.ROUND_HALF_UP);
                        breachAmount = breachAmount.add(overdueLateFee);
                        contract.setBreachAmount(breachAmount);
                    }
                }
                BeanUtils.copyProperties(contract,protocolDocumentContractVo);
                ApplyCtrDCSX dcsxContract = ctrDcsxClinent.findByContractNo(contractNo);
                String ourCompanyName = contract.getOurCompanyName();
                if (Objects.nonNull(dcsxContract)) {
                    if (com.ruoyi.common.utils.StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contract.getContractType())) {
                        protocolDocumentContractVo.setBankName(dcsxContract.getOurBankName());
                        protocolDocumentContractVo.setBankAccount(dcsxContract.getOurBankAccount());
                    } else {
                        protocolDocumentContractVo.setBankName(dcsxContract.getBankName());
                        protocolDocumentContractVo.setBankAccount(dcsxContract.getBankAccount());
                    }
                } else {
                    BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
                    companyOurSearchVo.setCompanyName(ourCompanyName);
                    BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
                    if(Objects.nonNull(companyOur)){
                        protocolDocumentContractVo.setBankName(companyOur.getCompanyBankName());
                        protocolDocumentContractVo.setBankAccount(companyOur.getCompanyCardId());

                    } else {
                        BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(ourCompanyName);
                        if (byCompanyName!=null) {
                            protocolDocumentContractVo.setBankName(byCompanyName.getCompanyBankName());
                            protocolDocumentContractVo.setBankAccount(byCompanyName.getCompanyCardId());
                        }
                    }
                }
                // 获取企业社会信用代码
                BsCompanyOurSearchVo bsCompanyOurSearchVo = new BsCompanyOurSearchVo();
                bsCompanyOurSearchVo.setCompanyName(protocolDocumentContractVo.getOurCompanyName());
                BsCompanyOur bsCompanyOur = bsCompanyOurClient.getCompanyOurDetail(bsCompanyOurSearchVo);
                BsCompany bsCompany = bsCompanyClient.findByCompanyName(protocolDocumentContractVo.getCompanyName());
                protocolDocumentContractVo.setOurCompanyNo(Objects.nonNull(bsCompanyOur) ? bsCompanyOur.getCompanyTaxNo() : "");
                protocolDocumentContractVo.setCompanyNo(Objects.nonNull(bsCompany) ? bsCompany.getCompanyCreditNo() : "");
            }
            return protocolDocumentContractVo;
        }
        return null;
    }

    @RequestMapping(value = "findServiceByContractId", method = RequestMethod.POST)
    @ResponseBody
    public CtrServiceContract findServiceByContractId(@RequestParam("contractId") Long contractId) {
        if (contractId != null && contractId != 0L) {
            CtrServiceContract entity = ctrServiceContractClient.findByCtrContract(contractId);
            return entity;
        }
        return null;
    }

    /**
     * 收货证明
     */
    @RequestMapping(value = "print/{id}", method = RequestMethod.GET)
    public String print(@PathVariable(value = "id") Long id, Model model) {
        ApproveFormPrintVo vo = ctrContractClient.printApplyConfirm(id);
        model.addAttribute("printContext", vo.getContent());
        return "apply/applyConfirmPrint";
    }

    @RequestMapping(value = "downLoadContract")
    public void downLoadContract(HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        String contractId = request.getParameter("contractId");
        CtrContract ctr = ctrContractClient.getEntity(Long.valueOf(contractId));
        List<CtrProduct> ctrProductList = ctrProductClient.findByOutCtrContractId(Long.valueOf(contractId));
        List<String[]> testList = new ArrayList<>();
        String wareHouseName = "";
        for (CtrProduct ctrProduct : ctrProductList) {
            String wrapSpecs = ctrProduct.getWrapSpecs();
            String wrapSpecsStr = DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, wrapSpecs);
            testList.add(new String[]{ctrProduct.getProductName(), ctrProduct.getBrandNumber(), ctrProduct.getFactoryName(), wrapSpecsStr, String.valueOf(ctrProduct.getDealNumber()), String.valueOf(ctrProduct.getDealPrice()), String.valueOf(ctrProduct.getTotalPrice())});
            wareHouseName = ctrProduct.getWarehouseName();
        }
        List<BsDictData> bsDictList = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
        //根据合同类型选择对应的word附件ID
        BsContractTemplate template = new BsContractTemplate();
        if (ctr.getContractType().equals(BasConstants.CONTRACT_TYPE_B)) {
            template.setTemplateTag(BasConstants.TEMPLATE_CONTRACT_BUY);
        } else {
            template.setTemplateTag(BasConstants.TEMPLATE_CONTRACT_SALE);
        }
        template.setEnterpriseId(ctr.getEnterpriseId());
        BsContractTemplate bsTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
        if (bsTemplate.getFileId() == null) {
            throw new ApplicationException("文档模板丢失");
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ContractDownloadUtils.PARAM_TABLELIST, testList);
        param.put(ContractDownloadUtils.PARAM_WAREHOUSENAME, wareHouseName);
        param.put(ContractDownloadUtils.PARAM_MATCHUSERID, ctr.getMatchUserId());
        param.put(ContractDownloadUtils.PARAM_BSTEMPLATE, bsTemplate);
        param.put(ContractDownloadUtils.PARAM_FILESHOWURL, fileShowUrl);
        File file = ContractDownloadUtils.downLoadContract(param, ctr, bsDictList);
        WorderToNewWordUtils.download(response, file);
    }

    @ResponseBody
    @RequestMapping(value = "downLoadZipContract")
    public void downLoadZipContract(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        dealWithPerformanceStatus(searchVo);
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        String domain = url.substring(0, url.indexOf(uri));
        logger.info("url:{}", url);
        logger.info("uri:{}", uri);
        logger.info("domain:{}", domain);
        searchVo.setRequestUrl(domain);
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
            String dictLabel = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_ASSISTANT_DICT, ShiroUtil.getCurrentUserId() + "");
            List<Long> deptIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(dictLabel)) {
                String[] split = dictLabel.split(",");
                // 使用 Stream 将 String 数组转换为 List<Long>
                deptIdList = java.util.Arrays.stream(split)
                        .map(Long::parseLong)  // 将 String 转换为 Long
                        .collect(Collectors.toList());  // 收集到 List<Long>

            } else {
                deptIdList.add(-1L);
            }
            searchVo.setDeptIdList(deptIdList);

        }
        Map<String, Object> searchParams = searchVo.getSearchParams();
        List<Long> deptIdList = searchVo.getDeptIdList();
        if (!ShiroUtil.isPermitted(PermissionEnum.PERM_ZGBAS_CUSTOMER_DEPT_VIEW.getPermissionCode())
                && (deptIdList == null || !deptIdList.contains(67957L))){
            searchParams.put("NEQS_owningRegion", "KH");
        }
        if (searchParams != null && Objects.equals(67957L, ShiroUtil.getDeptId())) {
            searchParams.remove("NEQS_owningRegion");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
            setFunderCompany(searchVo);
            searchVo.setFunderFlg(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            searchParams.put("NEQS_hideOut", "1");
        }
        ctrContractClient.downloadContractFileZip(searchVo);
    }

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException, ParseException {

        initSearch(searchVo, request);
        dealWithPerformanceStatus(searchVo);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
        Map<Long, SysDeptSdk> deptAllMap = new HashMap<>();
        if(Objects.nonNull(deptAll)) {
            deptAllMap = deptAll.stream()
                    .collect(Collectors.toMap(SysDeptSdk::getDeptId, vo -> vo));
        }


        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
            String dictLabel = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_ASSISTANT_DICT, ShiroUtil.getCurrentUserId() + "");
            List<Long> deptIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(dictLabel)) {
                String[] split = dictLabel.split(",");
                // 使用 Stream 将 String 数组转换为 List<Long>
                deptIdList = java.util.Arrays.stream(split)
                        .map(Long::parseLong)  // 将 String 转换为 Long
                        .collect(Collectors.toList());  // 收集到 List<Long>

            } else {
                deptIdList.add(-1L);
            }
            searchVo.setDeptIdList(deptIdList);

        }
        Map<String, Object> searchParams = searchVo.getSearchParams();
        List<Long> deptIdList = searchVo.getDeptIdList();
        if (!ShiroUtil.isPermitted(PermissionEnum.PERM_ZGBAS_CUSTOMER_DEPT_VIEW.getPermissionCode())
                && (deptIdList == null || !deptIdList.contains(67957L))){
            searchParams.put("NEQS_owningRegion", "KH");
        }
        if (searchParams != null && Objects.equals(67957L, ShiroUtil.getDeptId())) {
            searchParams.remove("NEQS_owningRegion");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            searchParams.put("NEQS_hideOut", "1");
        }
        String businessType = searchVo.getBusinessType();
        // Page<ContractShowVo> page = ctrContractClient.findPageContract(searchVo);
        Page<ContractShowVo> page = prePageShowVo(searchVo);
        Page<ContractShowVo> pageVo = preContractData(page, deptAllMap);
        List<ContractShowVo> content = pageVo.getContent();
        if(!CollectionUtils.isEmpty(content)) {

            for (int i = 0; i < content.size(); i++) {
                ContractShowVo contractShowVo = content.get(i);
                SysDeptSdk deptSdk = deptAllMap.get(contractShowVo.getDeptId());
                if(Objects.nonNull(deptSdk)) {
                    contractShowVo.setDeptName(deptSdk.getDeptName());
                }

            }
        }
        String title = "";
        String deliveryNumber = "出/入库数量(吨)";
        String payReceiveAmount = "已收/付金额(元)";
        String billedAmount = "收/开票金额(元)";
        String payBondTime = "收/付定金日期";
        String payFullTime = "收/付全款日期";
        String lastPayDate = "收/付款时间";
        String lastDeliveryDate = "出/入库时间";
        String lastBillDate = "收/开票时间";
        Boolean matchCreditFlg = searchVo.getMatchCreditFlg();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_CG, businessType)) {
            title = "自营采购合同";
            deliveryNumber = "入库数量(吨)";
            payReceiveAmount = "已付金额(元)";
            billedAmount = "收票金额(元)";
            payBondTime = "付定金日期";
            payFullTime = "付全款日期";
            lastPayDate = "付款时间";
            lastDeliveryDate = "入库时间";
            lastBillDate = "收票时间";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType)) {
            title = "自营销售合同";
            deliveryNumber = "出库数量(吨)";
            payReceiveAmount = "已收金额(元)";
            billedAmount = "开票金额(元)";
            payBondTime = "收定金日期";
            payFullTime = "收全款日期";
            lastPayDate = "收款时间";
            lastDeliveryDate = "出库时间";
            lastBillDate = "开票时间";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_SX_SX, businessType)) {
            title = "自营赊销合同";
            deliveryNumber = "出库数量(吨)";
            payReceiveAmount = "已收金额(元)";
            billedAmount = "开票金额(元)";
            payBondTime = "收定金日期";
            payFullTime = "收全款日期";
            lastPayDate = "收款时间";
            lastDeliveryDate = "出库时间";
            lastBillDate = "开票时间";
        } else if(StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG, businessType)){
            title = "库存采购合同";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && !matchCreditFlg) {
            title = "代采合同";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && matchCreditFlg) {
            title = "赊销合同";
        }
        String[] titles = new String[]{"合同编号", "货品", "我方抬头", "对方企业名称","是否签连带","是否访厂", "交货方式", "合同数量(吨)", "合同总价(元)",
                payBondTime, payFullTime, "约定收/付全款日期", "实际收/付全款日期", "应收本金(元)", "应收余额(元)", payReceiveAmount, lastPayDate, "交货时间", deliveryNumber, lastDeliveryDate, "仓储费(元)", "运输费(元)",
                billedAmount, lastBillDate, "逾期天数","履约状态", "合同状态", "签订日期", "业务员","业务部门"};
        //应该是excel列属性
        String[] attrs = new String[]{"contractNo", "productsName", "ourCompanyName",
                "companyName","liabilityFlg","accessReportFlg", "deliveryMode", "totalNumber", "totalAmount", "payBondTime", "payFullTime", "appointPayFullTime", "realPayFullTime", "receivablePrincipal", "receivableBalance",
                "dealedAmount", "lastPayDate", "deliveryDateTo", "deliveryNumStr", "deliveryDateStr", "warehouseAmount", "transportAmount", "billedAmount",
                "lastBillDate", "breachDays","violateFlg", "contractStatus", "contractTime", "matchUserName","deptName"};
        Integer[] widths = new Integer[]{15, 20, 30, 30, 15, 15, 20, 15, 15, 15, 15, 15, 15, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 10,10, 15, 15, 15,15};
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType)) {
//            setExtraData(page);
            List<String> titlesString = new ArrayList<>(Arrays.asList(titles));
            titlesString.add("实际交货日期");
            titlesString.add("业务类型");
            titlesString.add("确认收货数量");
            titlesString.add("确认收货时间");
            titlesString.add("保费费率");
            titlesString.add("保费");
            titlesString.add("装卸费");
            titlesString.add("逾期罚息");
            titlesString.add("已收逾期罚息");
            titlesString.add("逾期天数（天）");
            titlesString.add("回款周期（天）");
            List<String> attrsString = new ArrayList<>(Arrays.asList(attrs));
            attrsString.add("confirmDate");
            attrsString.add("businessKind");
            attrsString.add("confirmReceiveNumber");
            attrsString.add("confirmDate");
            attrsString.add("insuranceRate");
            attrsString.add("insuranceAmount");
            attrsString.add("stevedorage");
            attrsString.add("breachAmount");
            attrsString.add("receiveBreachAmount");
            attrsString.add("breachDays");
            attrsString.add("creditCycle");
            List<Integer> widthsList = new ArrayList<>(Arrays.asList(widths));
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widths = new Integer[widthsList.size()];
            titles = new String[titlesString.size()];
            attrs = new String[attrsString.size()];
            for (int i = 0; i < widthsList.size(); i++) {
                widths[i] = widthsList.get(i);
            }
            for (int i = 0; i < titlesString.size(); i++) {
                titles[i] = titlesString.get(i);
            }
            for (int i = 0; i < attrsString.size(); i++) {
                attrs[i] = attrsString.get(i);
            }
        }
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 设置可以换行
        cellStyle.setWrapText(true);

        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (pageVo != null && pageVo.getContent().size() > 0) {
            //应该是读取的数据
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                // page = ctrContractClient.findPageContract(searchVo);
                page = prePageShowVo(searchVo);
                pageVo = preContractData(page, deptAllMap);
//                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && matchCreditFlg) {
//                    setExtraData(pageVo);
//                }
                start += batchSize;
            } else {
                pageVo = null;
            }
        }

        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @RequestMapping(value = "/exportExcelTp")
    @ResponseBody
    public void exportExcelTp(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException, ParseException {

        initSearch(searchVo, request);
        dealWithPerformanceStatus(searchVo);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
        Map<Long, SysDeptSdk> deptAllMap = new HashMap<>();
        if(Objects.nonNull(deptAll)) {
            deptAllMap = deptAll.stream()
                    .collect(Collectors.toMap(SysDeptSdk::getDeptId, vo -> vo));
        }


        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
            String dictLabel = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_ASSISTANT_DICT, ShiroUtil.getCurrentUserId() + "");
            List<Long> deptIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(dictLabel)) {
                String[] split = dictLabel.split(",");
                // 使用 Stream 将 String 数组转换为 List<Long>
                deptIdList = java.util.Arrays.stream(split)
                        .map(Long::parseLong)  // 将 String 转换为 Long
                        .collect(Collectors.toList());  // 收集到 List<Long>

            } else {
                deptIdList.add(-1L);
            }
            searchVo.setDeptIdList(deptIdList);

        }
        Map<String, Object> searchParams = searchVo.getSearchParams();
        List<Long> deptIdList = searchVo.getDeptIdList();
        if (!ShiroUtil.isPermitted(PermissionEnum.PERM_ZGBAS_CUSTOMER_DEPT_VIEW.getPermissionCode())
                && (deptIdList == null || !deptIdList.contains(67957L))){
            searchParams.put("NEQS_owningRegion", "KH");
        }
        if (searchParams != null && Objects.equals(67957L, ShiroUtil.getDeptId())) {
            searchParams.remove("NEQS_owningRegion");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            searchParams.put("NEQS_hideOut", "1");
        }
        String businessType = searchVo.getBusinessType();
        // Page<ContractShowVo> page = ctrContractClient.findPageContract(searchVo);
        Page<ContractShowVo> page = prePageShowVo(searchVo);
        Page<ContractShowVo> pageVo = preContractData(page, deptAllMap);
        List<ContractShowVo> content = pageVo.getContent();
        if(!CollectionUtils.isEmpty(content)) {

            for (int i = 0; i < content.size(); i++) {
                ContractShowVo contractShowVo = content.get(i);
                SysDeptSdk deptSdk = deptAllMap.get(contractShowVo.getDeptId());
                if(Objects.nonNull(deptSdk)) {
                    contractShowVo.setDeptName(deptSdk.getDeptName());
                }

            }
        }
        String title = "托盘合同";
        String deliveryNumber = "出/入库数量(吨)";
        String payReceiveAmount = "已收/付金额(元)";
        String billedAmount = "收/开票金额(元)";
        String payBondTime = "收/付定金日期";
        String payFullTime = "收/付全款日期";
        String lastPayDate = "收/付款时间";
        String lastDeliveryDate = "出/入库时间";
        String lastBillDate = "收/开票时间";
//        Boolean matchCreditFlg = searchVo.getMatchCreditFlg();

        String[] titles = new String[]{"合同编号", "货品", "我方抬头", "对方企业名称","是否签连带","是否访厂", "交货方式", "合同数量(吨)", "合同总价(元)",
                payBondTime, payFullTime, "约定收/付全款日期", "实际收/付全款日期", "应收本金(元)", "应收余额(元)", payReceiveAmount, lastPayDate, "交货时间", deliveryNumber, lastDeliveryDate, "仓储费(元)", "运输费(元)",
                billedAmount, lastBillDate, "逾期天数","履约状态", "合同状态", "签订日期", "业务员","业务部门"};
        //应该是excel列属性
        String[] attrs = new String[]{"contractNo", "productsName", "ourCompanyName",
                "companyName","liabilityFlg","accessReportFlg", "deliveryMode", "totalNumber", "totalAmount", "payBondTime", "payFullTime", "appointPayFullTime", "realPayFullTime", "receivablePrincipal", "receivableBalance",
                "dealedAmount", "lastPayDate", "deliveryDateTo", "deliveryNumStr", "deliveryDateStr", "warehouseAmount", "transportAmount", "billedAmount",
                "lastBillDate", "breachDays","violateFlg", "contractStatus", "contractTime", "matchUserName","deptName"};
        Integer[] widths = new Integer[]{15, 20, 30, 30, 15, 15, 20, 15, 15, 15, 15, 15, 15, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 10,10, 15, 15, 15,15};
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType)) {
//            setExtraData(page);
            List<String> titlesString = new ArrayList<>(Arrays.asList(titles));
            titlesString.add("实际交货日期");
            titlesString.add("业务类型");
            titlesString.add("确认收货数量");
            titlesString.add("确认收货时间");
            titlesString.add("装卸费");
            titlesString.add("已提货款");
            titlesString.add("剩余货款");
            titlesString.add("托盘利息");
            titlesString.add("已收托盘利息");

            List<String> attrsString = new ArrayList<>(Arrays.asList(attrs));
            attrsString.add("confirmDate");
            attrsString.add("businessKind");
            attrsString.add("confirmReceiveNumber");
            attrsString.add("confirmDate");
            attrsString.add("stevedorage");
            attrsString.add("usedDeliveryAmount");
            attrsString.add("remainingDeliveryAmount");
            attrsString.add("tpInterest");
            attrsString.add("receiveTpInterest");

            List<Integer> widthsList = new ArrayList<>(Arrays.asList(widths));
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);
            widthsList.add(15);

            widths = new Integer[widthsList.size()];
            titles = new String[titlesString.size()];
            attrs = new String[attrsString.size()];
            for (int i = 0; i < widthsList.size(); i++) {
                widths[i] = widthsList.get(i);
            }
            for (int i = 0; i < titlesString.size(); i++) {
                titles[i] = titlesString.get(i);
            }
            for (int i = 0; i < attrsString.size(); i++) {
                attrs[i] = attrsString.get(i);
            }
        }
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 设置可以换行
        cellStyle.setWrapText(true);

        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (pageVo != null && pageVo.getContent().size() > 0) {
            //应该是读取的数据
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                // page = ctrContractClient.findPageContract(searchVo);
                page = prePageShowVo(searchVo);
                pageVo = preContractData(page, deptAllMap);
                start += batchSize;
            } else {
                pageVo = null;
            }
        }

        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    private Page<ContractShowVo> prePageShowVo(ContractSearchVo searchVo) {
        Page<ContractShowVo> page = null;
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
            setFunderCompany(searchVo);
            PageDown<RptCtrContractRptVo> rptContractPage = ctrContractReportClient.findRptContractPage(searchVo);
            if (Objects.nonNull(rptContractPage) && org.apache.commons.collections.CollectionUtils.isNotEmpty(rptContractPage.getContent())) {
                List<ContractShowVo> showVoList = new ArrayList<>();
                ContractShowVo vo;
                for (RptCtrContractRptVo ctrContractRptVo : rptContractPage.getContent()) {
                    vo = new ContractShowVo();
                    BeanUtils.copyProperties(ctrContractRptVo, vo);
                    showVoList.add(vo);
                }
                Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
                page = new PageImpl<>(showVoList, pageable, searchVo.getCount());
            }
        } else {
            page = ctrContractClient.findPageContract(searchVo);
        }
        return page;
    }

    private void parseDeliveryData(ContractShowVo vo, Map<Long, ApplyDeliveryExportVo> deliveryExportMap) {
        ApplyDeliveryExportVo deliveryExportVo = deliveryExportMap.get(vo.getId());
        if (Objects.nonNull(deliveryExportVo) && org.apache.commons.collections.CollectionUtils.isNotEmpty(deliveryExportVo.getExportList())) {
            List<ApplyDeliveryExportVo.ExportVo> exportList = deliveryExportVo.getExportList();
            String deliveryNumStr = exportList.stream()
                    .map(ApplyDeliveryExportVo.ExportVo::getDealNumber)
                    .map(String::valueOf)
                    .collect(Collectors.joining("\n"));
            String deliveryDataStr = exportList.stream()
                    .map(ApplyDeliveryExportVo.ExportVo::getDeliveryDate)
                    .map(e -> DateOperator.formatDate(e, "yyyy/MM/dd")).collect(Collectors.joining("\n"));
            vo.setDeliveryNumStr(deliveryNumStr);
            vo.setDeliveryDateStr(deliveryDataStr);
        }
    }

    private Page<ContractShowVo> preContractData(Page<ContractShowVo> pageVo, Map<Long, SysDeptSdk> deptAllMap) {
        if (pageVo != null && org.apache.commons.collections.CollectionUtils.isNotEmpty(pageVo.getContent())) {
            List<ContractShowVo> content = pageVo.getContent();
            List<Long> contractIdList = content.stream().map(ContractShowVo::getId).collect(Collectors.toList());
            Map<Long, ApplyDeliveryExportVo> deliveryExportMap = ctrContractClient.getDeliveryExportVo(contractIdList);
            for (ContractShowVo contractShowVo : pageVo.getContent()) {
                contractShowVo.setContractStatus(DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTSTATUS, contractShowVo.getContractStatus()));
                contractShowVo.setContractAttr(DictUtil.getValue(BasConstants.STOCK__CONTRACT_ATTR, contractShowVo.getContractAttr()));
                contractShowVo.setDeliveryMode(DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, contractShowVo.getDeliveryType()));
                contractShowVo.setBusinessKind(BsDictUtil.getValue(contractShowVo.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND, contractShowVo.getBusinessKind()));
                contractShowVo.setPerformanceStatus(BsDictUtil.getValue(contractShowVo.getEnterpriseId(), BasConstants.DICT_TYPE_PERFORMANCE_STATUS, contractShowVo.getPerformanceStatus()));
                SysDeptSdk deptSdk = deptAllMap.get(contractShowVo.getDeptId());
                if(Objects.nonNull(deptSdk)) {
                    contractShowVo.setDeptName(deptSdk.getDeptName());
                }
                parseDeliveryData(contractShowVo, deliveryExportMap);
            }
        }
        return pageVo;
    }

    public Long getDeptLeader() {
        Long deptLeader = 0L;
        try {
            DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getCurrentUserId(), PmConstants.NODE_TYPE_CENTER, ShiroUtil.getEnterpriseId());
            deptLeader = authOpenFacade.findDeptLeader(deptSearchVo);
            logger.info("getDeptLeader : " + JsonUtil.obj2Json(deptLeader));
        } catch (Exception e) {
            logger.error("getDeptLeader error:{}", e);
        }
        return Objects.isNull(deptLeader) ? 0L : deptLeader;
    }

    private Boolean canStartBuy() {
        Boolean preSellFlg = true;
        String deptId = Optional.ofNullable(ShiroUtil.getDeptId()).orElse(0L).toString();
        //查看所有预售合同权限
        boolean viewPreSell = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode());
        //需要限制预售发起部门ID
        String preSellDeptId = BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.PRESELLDEPTID, BasConstants.DEPTID);
        if (preSellDeptId != null && preSellDeptId.indexOf(deptId) >= 0 && !viewPreSell) {
            preSellFlg = false;
        }
        return preSellFlg;
    }

    /**
     * 获取合同附件下载地址
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value = "getContractFileUrl", method = RequestMethod.POST)
    @ResponseBody
    public String getContractFileUrl(@RequestParam("contractId") Long contractId) {
        if (contractId != null) {
            CtrContract entity = ctrContractClient.getEntity(contractId);
            if (entity != null && BasConstants.CONTRACT_TYPE_B.equals(entity.getContractType()) && !entity.getBuyContentFileId().isEmpty()) {
                String buyContractFileId = "";
                if (StringUtils.isNotBlank(entity.getBuyContentFileId())) {
                    String[] split = entity.getBuyContentFileId().split(BasConstants.COMMA);
                    if (split.length > 0) {
                        buyContractFileId = fileShowUrl + "/view/show/" + split[split.length - 1];
                    }
                }
                return buyContractFileId;
            }
            if (entity != null && BasConstants.CONTRACT_TYPE_S.equals(entity.getContractType()) && !entity.getSellContentFileId().isEmpty()) {
                String sellContractFileId = "";
                if (StringUtils.isNotBlank(entity.getSellContentFileId())) {
                    String[] split = entity.getSellContentFileId().split(BasConstants.COMMA);
                    if (split.length > 0) {
                        sellContractFileId = fileShowUrl + "/view/show/" + split[split.length - 1];
                    }
                }
                return sellContractFileId;
            }
        }
        return "";
    }

    /**
     * 获取合同附件下载地址
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value = "getDebtCertificateFileUrl", method = RequestMethod.POST)
    @ResponseBody
    public String getDebtCertificateFileUrl(@RequestParam("contractId") Long contractId) {
        if (contractId != null) {
            CtrContract entity = ctrContractClient.getEntity(contractId);
            if (entity != null && !entity.getDebtCertificateFileId().isEmpty()) {
                return fileShowUrl + "/view/show/" + entity.getDebtCertificateFileId().split(",")[0];
            }
        }
        return "";
    }

    private Boolean judgeFileId(CtrContract contract) {
        if (Objects.isNull(contract)) {
            return false;
        }
        boolean invoiceFileId = StringUtils.isNotBlank(contract.getInvoiceFileId());
        boolean debtCertificateFileId = StringUtils.isNotBlank(contract.getDebtCertificateFileId());
        boolean goodsFileId = StringUtils.isNotBlank(contract.getGoodsFileId());
        return invoiceFileId && debtCertificateFileId && goodsFileId;
    }

    /**
     * 更新违约标识
     *
     * @param id
     * @param response
     */
    @RequestMapping(value = "violateFlgUpdate/{id}", method = RequestMethod.POST)
    public void violateFlgUpdate(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            ctrContractClient.violateFlgUpdate(id);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderFailure(e.getMessage(), response);
        }
    }

    /**
     * 清除罚金
     *
     * @param id
     * @param response
     */
    @RequestMapping(value = "clearPenalty/{id}", method = RequestMethod.POST)
    public void clearPenalty(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            logger.info("执行清除罚金操作，userName:{},contractId:{}", ShiroUtil.getCurrentUserName(), id);
            ctrContractClient.clearPenalty(id);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderFailure(e.getMessage(), response);
        }
    }


    /**
     * 打开合同费用（仓储费，运输费，出\入库费用）调整窗口
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "getContractCost/{id}", method = RequestMethod.GET)
    public String getContractCost(@PathVariable("id") Long id, Model model) {
        CtrContract entity = ctrContractClient.getEntity(id);
        model.addAttribute("entity", entity);
        return "ctr/updateContractCost";
    }

    @RequestMapping(value = "updateContractCost", method = RequestMethod.POST)
    public void updateContractCost(CtrContract entity, HttpServletResponse response) {
        try {
            Long id = entity.getId();
            // 仓储费
            BigDecimal warehouseAmount = entity.getWarehouseAmount();
            // 运输费
            BigDecimal transportAmount = entity.getTransportAmount();
            // 出\入库费用
            BigDecimal deliveryFee = entity.getDeliveryFee();
            if (id != null) {
                CtrContract ctr = ctrContractClient.getEntity(id);
                if (!warehouseAmount.equals(BigDecimal.ZERO)) {
                    ctr.setWarehouseAmount(warehouseAmount);
                }
                if (!transportAmount.equals(BigDecimal.ZERO)) {
                    ctr.setTransportAmount(transportAmount);
                }
                if (!deliveryFee.equals(BigDecimal.ZERO)) {
                    ctr.setDeliveryFee(deliveryFee);
                }
                ctrContractClient.save(ctr);
                RenderUtil.renderSuccess("success", response);
            }

        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 生成提货单时，从合同代入数据
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "getContractLoading/{id}/{type}", method = RequestMethod.GET)
    public String getContractLoading(@PathVariable("id") Long id, @PathVariable("type") String type, Model model) {
        String deliveryType = "";
        CtrContractLoading entity = new CtrContractLoading();
        if (StringUtils.equals("edit", type) && id != 0L) {
            // 进入编辑页
            entity = ctrContractLoadingClient.getEntity(id);
        } else if (StringUtils.equals("copy", type) && id != 0L) {
            // 克隆
            entity = ctrContractLoadingClient.getEntity(id);
            entity.setId(0L);
        } else {
            // 处理新增时默认数据带出
            entity.setId(0L);
            entity.setContractId(id);
            entity.setNumberUnit(BasConstants.NUMBER_UNIT_DUN);
            dealWithNewBill(entity, type, deliveryType, id);
        }
        model.addAttribute("entity", entity);
        model.addAttribute("type", type);
        model.addAttribute("copyLoadingId", id);
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
        return "ctr/contract-loading";
    }

    /**
     * 处理新增时默认数据带出
     *
     * @param entity
     * @param type
     * @param deliveryType
     * @param id
     */
    private void dealWithNewBill(CtrContractLoading entity, String type, String deliveryType, Long id) {
        if (id == 0) {
            entity.setBillType(StringUtils.equals(BasConstants.APPLY_TYPE_B, type) ? BasConstants.LOADING_BILL_TYPE_T : BasConstants.LOADING_BILL_TYPE_P);
        } else {
            if (StringUtils.equals(BasConstants.APPLY_TYPE_B, type)) {
                CtrContract ctrContract = ctrContractClient.getEntity(id);
                ApplyMatch applyMatch = applyMatchClient.findByApproveId(ctrContract.getApproveId());
                // 未入库数量 = 合同数量 - 已入库数量
                BigDecimal totalNumber = ctrContract.getTotalNumber();
                BigDecimal warehouseNumber = ctrContract.getWarehouseNumber();
                BigDecimal dealNumber = totalNumber.subtract(warehouseNumber);
                entity.setDealNumber(dealNumber);
                entity.setOurCompanyName(ctrContract.getOurCompanyName());
                entity.setCompanyId(ctrContract.getCompanyId());
                entity.setCompanyName(ctrContract.getCompanyName());
                entity.setContractNo(ctrContract.getContractNo());
                entity.setProductName(applyMatch.getProductName());
                entity.setBrandNumber(applyMatch.getBrandNumber());
                entity.setFactoryName(applyMatch.getFactoryName());
                entity.setContractNo(ctrContract.getContractNo());
                BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(ctrContract.getOurCompanyName());
                String companyPhone = byCompanyName.getCompanyPhone();
                String companyContact = byCompanyName.getCompanyContact();
                entity.setContactPhone(companyPhone);
                entity.setContactName(companyContact);
                entity.setContactAddress(ctrContract.getDeliveryAddr() + "/" + ctrContract.getContactAddr());
                entity.setLoadingDate(ctrContract.getDeliveryDateFrom());
                deliveryType = ctrContract.getDeliveryType();
            } else {
                ApplyCtrDCSX applyCtrDCSX = ctrDcsxClinent.getEntity(id);
                if (Objects.nonNull(applyCtrDCSX)) {
                    entity.setOurCompanyName(applyCtrDCSX.getOurCompanyName());
                    entity.setCompanyId(applyCtrDCSX.getCompanyId());
                    entity.setCompanyName(applyCtrDCSX.getCompanyName());
                    entity.setContractNo(applyCtrDCSX.getContractNo());
                    entity.setDealNumber(applyCtrDCSX.getTotalNumber());
                    BsProductType productType = bsProductTypeClient.findProductTypeCode(applyCtrDCSX.getProductBrand());
                    if (Objects.nonNull(productType)) {
                        entity.setProductName(productType.getTypeName());
                    } else {
                        entity.setProductName(applyCtrDCSX.getProductsName());
                    }
                    entity.setBrandNumber(applyCtrDCSX.getProductNum());
                    entity.setFactoryName(applyCtrDCSX.getFactoryName());
                    entity.setLoadingDate(applyCtrDCSX.getDeliveryDateTo());
                    BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(applyCtrDCSX.getOurCompanyName());
                    String companyPhone = byCompanyName.getCompanyPhone();
                    String companyContact = byCompanyName.getCompanyContact();
                    entity.setContactPhone(companyPhone);
                    entity.setContactName(companyContact);
                    entity.setContactAddress(applyCtrDCSX.getDeliveryAddr() + "/" + applyCtrDCSX.getContactAddr());
                    entity.setLoadingDate(applyCtrDCSX.getDeliveryDateTo());
                    deliveryType = applyCtrDCSX.getDeliveryType();
                }
            }
            entity.setBillType(StringUtils.equals(BasConstants.DICT_TYPE_DELIVERY_P1, deliveryType) ? BasConstants.LOADING_BILL_TYPE_P : BasConstants.LOADING_BILL_TYPE_T);
        }
    }


    @RequestMapping(value = "findByCompanyInterest", method = RequestMethod.POST)
    public void findByCompanyInterest(CtrContractDto ctrContractDto, HttpServletResponse response) {
        List<String> contractNoList = new ArrayList<>();
        List<String> contractNoByCompanyId = penaltyInterestClient.findContractNoByCompanyId(String.valueOf(ctrContractDto.getCompanyId()));
        if (!CollectionUtils.isEmpty(contractNoByCompanyId)) {
            for (int i = 0; i < contractNoByCompanyId.size(); i++) {
                String s = contractNoByCompanyId.get(i);
                String[] strArray = s.split(",");
                for (int j = 0; j < strArray.length; j++) {
                    String contractNo = strArray[j];
                    contractNoList.add(contractNo);
                }
            }
        }
        ctrContractDto.setContractNoList(contractNoList);
        ctrContractDto.setUserId(ShiroUtil.getCurrentUserId());
        Page<CtrContract> page = ctrContractClient.findByCompanyInterest(ctrContractDto);
        JsonEasyUI.renderJson(response, page);
    }

    private void setFunderCompany(ContractSearchVo searchVo) {
        if (!ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
            return;
        }
        String buyCompanyName = "";
        List<BsFunder> bsFunderList = bsFunderClient.findAllByUserId(ShiroUtil.getCurrentUserId());
        if (!CollectionUtils.isEmpty(bsFunderList)) {
            buyCompanyName = bsFunderList.get(0).getCompanyNames();
        }
        if (StringUtils.isNotBlank(buyCompanyName)) {
            List<String> companyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(buyCompanyName);
            searchVo.setCompanyNameList(companyNameList);
        }
    }

    @RequestMapping(value = "findContractNo/{contractNo}", method = RequestMethod.POST)
    public void findContractNo(@PathVariable("contractNo") String contractNo, HttpServletResponse response) {
        CtrContractLoading entity = new CtrContractLoading();
        if (contractNo == null) {
            return;
        } else {
            CtrContract ctr = new CtrContract();
            ctr.setContractNo(contractNo);
            CtrContract ctrContract = ctrContractClient.findByContractNo(ctr);
            if (ctrContract == null) {
                ApplyCtrDCSX byDCSXApproveId = ctrDcsxClinent.findByContractNo(contractNo);
                if (byDCSXApproveId != null) {
                    //是代采赊销预算
                    entity.setOurCompanyName(byDCSXApproveId.getOurCompanyName());
                    entity.setCompanyName(byDCSXApproveId.getCompanyName());
                    entity.setDealNumber(byDCSXApproveId.getTotalNumber());
                    BsProductType productType = bsProductTypeClient.findProductTypeCode(byDCSXApproveId.getProductBrand());
                    if (Objects.nonNull(productType)) {
                        entity.setProductName(productType.getTypeName());
                    } else {
                        entity.setProductName(byDCSXApproveId.getProductsName());
                    }
                    entity.setContactAddress(byDCSXApproveId.getDeliveryAddr());
                    entity.setBrandNumber(byDCSXApproveId.getProductNum());
                    entity.setFactoryName(byDCSXApproveId.getFactoryName());
                    entity.setContactAddress(byDCSXApproveId.getDeliveryAddr() + "/" + byDCSXApproveId.getContactAddr());
                    entity.setLoadingDate(byDCSXApproveId.getDeliveryDateTo());
                    BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(byDCSXApproveId.getOurCompanyName());
                    String companyPhone = byCompanyName.getCompanyPhone();
                    String companyContact = byCompanyName.getCompanyContact();
                    entity.setContactPhone(companyPhone);
                    entity.setContactName(companyContact);
                }
            } else {
                //普通预算
                ApplyMatch applyMatch = applyMatchClient.findByApproveId(ctrContract.getApproveId());
                // 未入库数量 = 合同数量 - 已入库数量
                BigDecimal totalNumber = ctrContract.getTotalNumber();
                BigDecimal warehouseNumber = ctrContract.getWarehouseNumber();
                BigDecimal dealNumber = totalNumber.subtract(warehouseNumber);
                entity.setDealNumber(dealNumber);
                entity.setOurCompanyName(ctrContract.getOurCompanyName());
                entity.setCompanyId(ctrContract.getCompanyId());
                entity.setCompanyName(ctrContract.getCompanyName());
                entity.setContractNo(ctrContract.getContractNo());
                entity.setProductName(applyMatch.getProductName());
                entity.setBrandNumber(applyMatch.getBrandNumber());
                entity.setFactoryName(applyMatch.getFactoryName());
                entity.setContactAddress(ctrContract.getDeliveryAddr() + "/" + ctrContract.getContactAddr());
                entity.setLoadingDate(ctrContract.getDeliveryDateFrom());
                BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(ctrContract.getOurCompanyName());
                String companyPhone = byCompanyName.getCompanyPhone();
                String companyContact = byCompanyName.getCompanyContact();
                entity.setContactPhone(companyPhone);
                entity.setContactName(companyContact);
            }
        }
        RenderUtil.renderJson(entity, response);
    }

    @ResponseBody
    @RequestMapping(value = "/exportBusinessExcel")
    public void exportBusinessExcel(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        int batchSize = 500;
        preconditionParam(searchVo, request, batchSize);
        Map<Long, SysDeptSdk> deptAllMap = preconditionDept();
        Page<ContractShowVo> page = prePageShowVo(searchVo);
        Page<ContractShowVo> pageVo = preContractData(page, deptAllMap);
        String title = "业务表";
        String[] titles = new String[]{"业务部门", "业务员", "业务类型", "合同编号", "货品", "我方抬头", "对方企业名称", "合同状态", "签订日期",
                "交货时间", "合同数量(吨)", "交货方式", "仓储费(元)", "运输费(元)", "装卸费(元)", "出/入库时间", "出/入库数量(吨)", "实际交货日期",
                "确认收货时间", "确认收货数量(吨)", "回款周期（天）", "履约状态", "收/付全款日期", "约定收/付全款日期", "合同总价(元)", "收/付定金日期",
                "实际收/付全款日期", "已收/付金额(元)", "收/付款时间", "应收本金(元)", "逾期罚息(元)", "已收逾期罚息(元)", "应收余额(元)", "逾期天数(天)",
                "收/开票金额(元)", "收/开票时间", "是否签连带", "是否访厂", "逾期天数", "保费费率", "保费"};
        String[] attrs = new String[]{"deptName", "matchUserName", "businessKind", "contractNo", "productsName", "ourCompanyName",
                "companyName", "contractStatus", "contractTime", "deliveryDateTo", "totalNumber", "deliveryMode", "warehouseAmount",
                "transportAmount", "stevedorage", "deliveryDateStr", "deliveryNumStr", "confirmDate", "confirmDate", "confirmReceiveNumber",
                "creditCycle", "performanceStatus", "payFullTime", "appointPayFullTime", "totalAmount", "payBondTime", "realPayFullTime",
                "dealedAmount", "lastPayDate", "receivablePrincipal", "breachAmount", "receiveBreachAmount", "receivableBalance",
                "breachDays", "billedAmount", "lastBillDate", "liabilityFlg", "accessReportFlg", "breachDays", "insuranceRate", "insuranceAmount"};
        Integer[] widths = new Integer[]{15, 15, 15, 15, 20, 30, 30, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
                15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 设置可以换行
        cellStyle.setWrapText(true);
        int start = 1;
        buildPoiExcel(workbook, sheet, titles, widths);
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = prePageShowVo(searchVo);
                pageVo = preContractData(page, deptAllMap);
//                setExtraData(pageVo);
                start += batchSize;
            } else {
                pageVo = null;
            }
        }

        try {
            buildExcelStyle(workbook, sheet, page.getTotalElements());
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/exportBusinessExcelTp")
    public void exportBusinessExcelTp(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        int batchSize = 500;
        preconditionParam(searchVo, request, batchSize);
        Map<Long, SysDeptSdk> deptAllMap = preconditionDept();
        Page<ContractShowVo> page = prePageShowVo(searchVo);
        Page<ContractShowVo> pageVo = preContractData(page, deptAllMap);
        String title = "业务表";
        String[] titles = new String[]{"业务部门", "业务员", "业务类型", "合同编号", "货品", "我方抬头", "对方企业名称",
                "合同状态", "签订日期", "交货时间", "合同数量(吨)", "交货方式", "仓储费(元)", "运输费(元)", "装卸费(元)", "出/入库时间", "出/入库数量(吨)", "实际交货日期", "确认收货时间", "确认收货数量(吨)",
                "收/付全款日期", "约定收/付全款日期", "合同总价(元)", "收/付定金日期", "实际收/付全款日期", "已收/付金额(元)", "收/付款时间", "应收本金(元)", "托盘利息(元)", "已收托盘利息(元)", "应收余额(元)", "已提货款","剩余货款",
                "收/开票金额(元)", "收/开票时间",
                "是否签连带", "是否访厂"};
        String[] attrs = new String[]{"deptName", "matchUserName", "businessKind", "contractNo", "productsName", "ourCompanyName","companyName",
                "contractStatus", "contractTime", "deliveryDateTo", "totalNumber", "deliveryMode", "warehouseAmount", "transportAmount",
                    "stevedorage", "deliveryDateStr", "deliveryNumStr", "confirmDate", "confirmDate", "confirmReceiveNumber",
                "payFullTime", "appointPayFullTime", "totalAmount", "payBondTime", "realPayFullTime", "dealedAmount", "lastPayDate",
                    "receivablePrincipal", "tpInterest", "receiveTpInterest", "receivableBalance", "usedDeliveryAmount","remainingDeliveryAmount",
                "billedAmount", "lastBillDate",
                "liabilityFlg", "accessReportFlg"};
        Integer[] widths = new Integer[]{15, 15, 15, 20, 30, 30, 15,
                15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
                15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
                15, 15,
                15, 15};

        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 设置可以换行
        cellStyle.setWrapText(true);
        int start = 1;
        buildPoiExcelTp(workbook, sheet, titles, widths);
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = prePageShowVo(searchVo);
                pageVo = preContractData(page, deptAllMap);
//                setExtraData(pageVo);
                start += batchSize;
            } else {
                pageVo = null;
            }
        }

        try {
            buildExcelStyleTp(workbook, sheet, page.getTotalElements());
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void preconditionParam(ContractSearchVo searchVo, HttpServletRequest request, int batchSize){
        initSearch(searchVo, request);
        dealWithPerformanceStatus(searchVo);
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
            String dictLabel = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_ASSISTANT_DICT, ShiroUtil.getCurrentUserId() + "");
            List<Long> deptIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(dictLabel)) {
                String[] split = dictLabel.split(",");
                // 使用 Stream 将 String 数组转换为 List<Long>
                deptIdList = java.util.Arrays.stream(split)
                        .map(Long::parseLong)  // 将 String 转换为 Long
                        .collect(Collectors.toList());  // 收集到 List<Long>

            } else {
                deptIdList.add(-1L);
            }
            searchVo.setDeptIdList(deptIdList);

        }
        Map<String, Object> searchParams = searchVo.getSearchParams();
        List<Long> deptIdList = searchVo.getDeptIdList();
        if (!ShiroUtil.isPermitted(PermissionEnum.PERM_ZGBAS_CUSTOMER_DEPT_VIEW.getPermissionCode())
                && (deptIdList == null || !deptIdList.contains(67957L))){
            searchParams.put("NEQS_owningRegion", "KH");
        }
        if (searchParams != null && Objects.equals(67957L, ShiroUtil.getDeptId())) {
            searchParams.remove("NEQS_owningRegion");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            searchParams.put("NEQS_hideOut", "1");
        }
    }

    private Map<Long, SysDeptSdk> preconditionDept(){
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
        Map<Long, SysDeptSdk> deptAllMap = new HashMap<>();
        if(Objects.nonNull(deptAll)) {
            deptAllMap = deptAll.stream().collect(Collectors.toMap(SysDeptSdk::getDeptId, vo -> vo));
        }
        return deptAllMap;
    }

    private void buildPoiExcel(Workbook workbook, Sheet sheet,String[] titles, Integer[] widths){
        String[] firstTitles = new String[]{"订单信息", "", "", "", "", "", "", "物流信息", "", "", "", "", "", "", "", "",
                "", "", "", "", "账期信息", "", "收款信息", "", "", "", "", "", "", "", "", "", "", "", "发票", "", "其他", "", "", "", ""};
        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, firstTitles, widthes);
        CellRangeAddress mergedRegion1 = new CellRangeAddress(0, 0, 0, 6);
        CellRangeAddress mergedRegion2 = new CellRangeAddress(0, 0, 7, 19);
        CellRangeAddress mergedRegion3 = new CellRangeAddress(0, 0, 20, 21);
        CellRangeAddress mergedRegion4 = new CellRangeAddress(0, 0, 22, 33);
        CellRangeAddress mergedRegion5 = new CellRangeAddress(0, 0, 34, 35);
        CellRangeAddress mergedRegion6 = new CellRangeAddress(0, 0, 36, 40);
        sheet.addMergedRegion(mergedRegion6);
        sheet.addMergedRegion(mergedRegion5);
        sheet.addMergedRegion(mergedRegion4);
        sheet.addMergedRegion(mergedRegion3);
        sheet.addMergedRegion(mergedRegion2);
        sheet.addMergedRegion(mergedRegion1);
        PoiExcelUtil.createHeadsForstartRow(workbook, sheet, titles, widthes, 1);
    }

    private void buildPoiExcelTp(Workbook workbook, Sheet sheet,String[] titles, Integer[] widths){
        String[] firstTitles = new String[]{"订单信息", "", "", "", "", "", "",
                "物流信息", "", "", "", "", "", "", "", "","", "", "", "",
                "收款信息", "", "", "", "", "", "", "", "", "", "", "", "",
                "发票", "",
                "其他", ""};
        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, firstTitles, widthes);
        CellRangeAddress mergedRegion1 = new CellRangeAddress(0, 0, 0, 6);
        CellRangeAddress mergedRegion2 = new CellRangeAddress(0, 0, 7, 19);
        CellRangeAddress mergedRegion4 = new CellRangeAddress(0, 0, 20, 32);
        CellRangeAddress mergedRegion5 = new CellRangeAddress(0, 0, 33, 34);
        CellRangeAddress mergedRegion6 = new CellRangeAddress(0, 0, 35, 36);
        sheet.addMergedRegion(mergedRegion6);
        sheet.addMergedRegion(mergedRegion5);
        sheet.addMergedRegion(mergedRegion4);
        sheet.addMergedRegion(mergedRegion2);
        sheet.addMergedRegion(mergedRegion1);
        PoiExcelUtil.createHeadsForstartRow(workbook, sheet, titles, widthes, 1);
    }

    private CellStyle buildCellStyle(Workbook workbook, IndexedColors indexedColors){
        CellStyle style = workbook.createCellStyle();
        // 设置单元格背景颜色
        style.setFillForegroundColor(indexedColors.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        // 设置垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 开启单元格内换行功能
        style.setWrapText(true);
        // 边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }

    private void buildExcelStyle(Workbook workbook, Sheet sheet, Long totalRows){
        CellStyle style = buildCellStyle(workbook, IndexedColors.LIGHT_GREEN);
        CellStyle style2 = buildCellStyle(workbook, IndexedColors.PALE_BLUE);
        CellStyle style3 = buildCellStyle(workbook, IndexedColors.LEMON_CHIFFON);
        for(int r = 0 ; r < totalRows + 2; r++){
            for (int i = 0; i < 7; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style);
            }

            for (int i = 7; i < 20; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style2);
            }

            for (int i = 20; i < 22; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style);
            }

            for (int i = 22; i < 34; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style3);
            }

            for (int i = 34; i < 36; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style);
            }

            for (int i = 36; i < 41; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style2);
            }
        }
    }

    private void buildExcelStyleTp(Workbook workbook, Sheet sheet, Long totalRows){
        CellStyle style = buildCellStyle(workbook, IndexedColors.LIGHT_GREEN);
        CellStyle style2 = buildCellStyle(workbook, IndexedColors.PALE_BLUE);
        CellStyle style3 = buildCellStyle(workbook, IndexedColors.LEMON_CHIFFON);
        for(int r = 0 ; r < totalRows + 2; r++){
            for (int i = 0; i < 7; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style);
            }

            for (int i = 7; i < 20; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style2);
            }

            for (int i = 20; i < 33; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style3);
            }

            for (int i = 33; i < 35; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style);
            }

            for (int i = 35; i < 37; i++) {
                Cell cell = sheet.getRow(r).getCell(i);
                cell.setCellStyle(style2);
            }
        }
    }

    private void dealWithPerformanceStatus(ContractSearchVo searchVo){
        Object eqsPerformanceStatus = searchVo.getSearchParams().get("EQS_performanceStatus");
        if (Objects.nonNull(eqsPerformanceStatus)) {
            searchVo.getSearchParams().put("INS_performanceStatus", eqsPerformanceStatus);
            searchVo.getSearchParams().put("EQS_performanceStatus", null);
        }
    }

    @RequestMapping(value = "findContractByNo", method = RequestMethod.POST)
    @ResponseBody
    public CtrContract findContractByNo(@RequestParam("contractNo") String contractNo) {
        if (StringUtils.isNotEmpty(contractNo)) {
            return ctrContractClient.findByContractNoV2(contractNo);
        }
        return null;
    }

    /**
     * 选择合同界面
     * @param model
     * @return
     */
    @RequestMapping(value = "contractChoose")
    public String choose1(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("businessKindJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND)));
        DeptSearchVo deptSearchVo = new DeptSearchVo();
        deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        model.addAttribute("performanceStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTPE_RFOEMACE_STATUS)));
        return "ctr/contract-choose";
    }

    /**
     * 获取赊销业务销售合同，主要用于弹出合同选择合同，不用涉及权限问题
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findSellContract")
    public void findSellContract(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<CtrContract> page = ctrContractClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page, null,null );
    }

    /**
     * 判断是否是特殊链条，同时SPTX 是否已经收票
     *
     * @param contractId
     * @param request
     * @param response
     */
    @RequestMapping(value = "judgmentSpecialChain")
    @ResponseBody
    public ApiResult findSellContract(@RequestParam("contractId") Long contractId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        // 判断是是否收票完成
        Boolean billedFlg = false;
        // 判断是否是特殊链条
        Boolean specialChainFlg = false;
        CtrContract contractS = ctrContractClient.getEntity(contractId);
        Long approveId = contractS.getApproveId();
        CtrContract specialChainContract = ctrContractClient.findSpecialChainContract(approveId);
        if (specialChainContract != null) {
            if (contractS.getOurCompanyName().equals(BasConstants.COMPANY_NAME_SHZG)) {
                specialChainFlg = true;
                ApplyCtrDCSX applyCtrDCSX = ctrDcsxClinent.findByDCSXApproveId(approveId);
                BigDecimal billedAmount = applyCtrDCSX.getBilledAmount() == null ? BigDecimal.ZERO : applyCtrDCSX.getBilledAmount();
                BigDecimal totalAmount = applyCtrDCSX.getTotalAmount();
                if (billedAmount.compareTo(totalAmount) >= 0) {
                    billedFlg = true;
                }
            }
        }

        result.put("billedFlg", billedFlg);
        result.put("specialChainFlg", specialChainFlg);
        return new ApiResult(200, "查询成功", result);
    }
}
