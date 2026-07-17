/**
 *
 */
package com.spt.bas.web.controller.pm;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.ruoyi.common.utils.IpUtils;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.report.client.entity.RptCtrContractOrverdur;
import com.spt.bas.report.client.entity.RptPrintHistoryReport;
import com.spt.bas.report.client.remote.IRptCtrContractOrverdurClient;
import com.spt.bas.report.client.remote.IRptPrintHistoryReportClient;
import com.spt.bas.web.config.BasicErrorController;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.*;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.*;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.util.RuleUtils;
import com.spt.pm.util.SubjectPmUtil;
import com.spt.pm.vo.*;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.jpa.vo.IdEntity;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 审批信息
 *
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/pm/approve")
public class PmApproveController extends PageController<PmApprove, BaseVo> {

    @Autowired
    private IBsCompanyClient bsCompanyClient;
    @Autowired
    private IPmApproveClient approveClient;
    @Autowired
    private IPmProcessClient processClient;
    @Autowired
    private IPmApproveHistoryClient approveHistoryClient;
    @Autowired
    private IPmApproveStepClient approveStepClient;
    @Autowired
    private IPmProcessStepClient processStepClient;
    @Autowired
    private IBsProductConfigClient bsProductConfigClient;
    @Autowired
    private ICtrContractClient contractClient;
    @Autowired
    private IApplyBuyClient buyClient;
    @Autowired
    private IApplyProductDetailClient productDetailClient;
    @Autowired
    private IApplySellClient sellClient;
    @Autowired
    private IApplyMatchClient applyMatchClient;
    @Autowired
    private IApplyMatchDetailClient applyMatchDetailClient;
    @Autowired
    private IApplyImportClient applyImportClient;
    @Autowired
    private IApplyImportDetailClient applyImportDetailClient;
    @Autowired
    private IBsContractTemplateClient bsContractTemplateClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IRptCtrContractOrverdurClient ctrContractOrverdurClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IApplyCtrDcsxClinent applyCtrDcsxClinent;
    @Autowired
    private IRptPrintHistoryReportClient printHistoryReportClient;

    @Autowired
    private IApplyPayClient applyPayClient;

    @Override
    public BaseClient<PmApprove> getService() {
        return approveClient;
    }

    @Value("${down.wordOutputUrl}")
    private String wordOutputUrl;


    @RequestMapping(value = "")
    public String index(Model model,HttpServletRequest request) {
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findAccess(searchVo);
        model.addAttribute("processJson", JsonUtil.obj2Json(processList));
        model.addAttribute("processList", processList);
        List<PmProcess> processListAll = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processAllJson", JsonUtil.obj2Json(processListAll));
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_START.getPermissionCode())) {
            model.addAttribute("hasApproveStart", true);
        } else {
            model.addAttribute("hasApproveStart", false);
        }
        List<EasyTreeNode> easyTree = EasyTreeUtil2.getProcessTree(processList);
        model.addAttribute("processTree", JsonUtil.obj2Json(easyTree));

        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("approveOpinionJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVEOPINION)));
        //逾期收款通知权限
        model.addAttribute("noteFlg", ShiroUtil.isPermitted(PermissionEnum.PERM_ORVERDUR_NOTICE.getPermissionCode()));

        // 类型权限
        if (ShiroUtil.isPermitted(PermissionEnum.BAS_APPROVE_NOALLTYPE.getPermissionCode())) {
            model.addAttribute("hasAllType", false);
        }else {
            model.addAttribute("hasAllType", true);
        }
        // 首页跳转用参数
        model.addAttribute("statusFromIndex",request.getParameter("status"));
        model.addAttribute("indexParamFlag",request.getParameter("paramFlag"));
        model.addAttribute("batchApproveFlg", ShiroUtil.isPermitted(PermissionEnum.PERAM_BATCH_APPROVE.getPermissionCode()));
        model.addAttribute("exportApproveFlg", ShiroUtil.isPermitted(PermissionEnum.PERAM_EXPORT_APPROVE.getPermissionCode()));
        model.addAttribute("batchSelectDel", ShiroUtil.isPermitted(PermissionEnum.PERAM_BATCH_SELECT_DEL.getPermissionCode()));
        return "pm/approve";
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    @RequestMapping(value = "listApprove")
    public void listApprove(PmApproveSearchVo queryVo, HttpServletRequest request, HttpServletResponse response) {
        buildQueryParam(queryVo, request);
        Page<PmApproveDownVo> page = approveClient.findPageApprove(queryVo);

        Map<String, Object> searchParams = queryVo.getSearchParams();
        Map<String, Object> footer = new HashMap<>();
        footer.put("approveNo", "");
        footer.put("subject", getSubjectAmount(page.getContent(),searchParams.get("LIKES_subject")));
        JsonEasyUI.renderJson(response, page,null, footer);
    }

    /**
     * 计算本页摘要中的数据合计
     * @param list
     * @return
     */
    public String getSubjectAmount(List<PmApproveDownVo> list,Object likesSubject){
        // 定义正则表达式模式，匹配包含逗号和小数点的金额
        String regex = "([0-9,]+\\.[0-9]+)元";
        String regexOld = "(\\d+(?:\\.\\d{1,2})?)元";
        String regexOld2 = "(\\d+(?:\\.\\d{1,2})?)";
        
        BigDecimal computeAmount = BigDecimal.ZERO;
        Boolean computeFlg = false;
        
        try {
            String subjectConfig = BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.DICT_COMPUTE_SUBJECT_CONFIG, BasConstants.DICT_COMPUTE_SUBJECT_CONFIG_CONTAINS_STRING);
            // 根据摘要查询条件判断是否计算摘要金额合计
            if(likesSubject != null && likesSubject instanceof String) {
                String subject = (String) likesSubject;
                if(StringUtils.isNotBlank(subject)) {
                    if(StringUtils.isNotBlank(subjectConfig)) {
                        // 根据数据字典配置判断是否计算摘要金额合计
                        String[] split = subjectConfig.split("|");
                        for (String str : split) {
                            if(subject.contains(str)){
                                computeFlg = true;
                                break;
                            }
                        }
                    }
                }
            }
            if(computeFlg) {
                if(CollectionUtils.isNotEmpty(list)) {
                    for (PmApproveDownVo pmApproveDownVo : list) {
                        String subject = pmApproveDownVo.getSubject();
                        // 编译正则表达式
                        Pattern pattern = Pattern.compile(regex);
                        // 创建 Matcher 对象，用于在输入字符串中执行匹配
                        Matcher matcher = pattern.matcher(subject);
                        String amount = "";
                        // 查找并输出匹配的金额
                        if (matcher.find()) {
                            amount = matcher.group(1);
                        } else {
                            Pattern patternOld = Pattern.compile(regexOld);
                            // 创建 Matcher 对象，用于在输入字符串中执行匹配
                            Matcher matcherOld = patternOld.matcher(subject);
                            if(matcherOld.find()) {
                                amount = matcherOld.group(1);
                            } else {
                                Pattern patternOld2 = Pattern.compile(regexOld2);
                                // 创建 Matcher 对象，用于在输入字符串中执行匹配
                                Matcher matcherOld2 = patternOld2.matcher(subject);
                                if(matcherOld2.find()) {
                                    amount = matcherOld2.group(1);
                                }
                            }
                        }
                        if(StringUtils.isNotBlank(amount)){
                            // 去除千分位分隔符
                            computeAmount = computeAmount.add(new BigDecimal(amount.replace(",", "")));
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error("计算摘要金额合计失败：{}",e);
        }
        
        if(computeFlg && computeAmount.compareTo(BigDecimal.ZERO) != 0) {
            return SubjectPmUtil.formatMoney(computeAmount, RuleUtils.monetaryUnit);
        } else {
            return "";
        }
        
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        PmApproveDownVo approve = approveClient.getEntityVo(id);
        PmProcess process = processClient.getEntity(approve.getProcessId());

        if (StringUtils.equals(process.getEntityName(), BsCompanyAllowed.class.getName()) ||
                StringUtils.equals(process.getEntityName(), BsCompanyQuota.class.getName()) ||
                StringUtils.equals(process.getEntityName(), BsCompanyQuotaV1.class.getName())) {
            PmApproveContents contents = pmApproveContentsClient.findByApproveId(approve.getId());
            logger.info("detail - contents : " + JsonUtil.obj2Json(contents));

            Map<String, Object> contentsMap = JsonUtil.json2Map(contents.getContents());
            model.addAttribute("companyName", (String) contentsMap.get("companyName"));
            model.addAttribute("companyId", Integer.toString((Integer) contentsMap.get("companyId")));
        }

        Boolean backEditFlg = backEditFlg(approve);// 是否可以追回
        boolean hasApprove = AccessControlUtil.hasApprove(approve);// 审批权限
        boolean hasEdit = AccessControlUtil.hasEdit(approve);// 编辑权限
        boolean hasBack = false;// 驳回权限
        boolean hasEditFile = AccessControlUtil.hasEditFile(approve);// 附件修改权限
        boolean hasInvalid = AccessControlUtil.hasInvalid(process.getProcessCode(), approve.getStatus()); //出入库申请作废权限
        model.addAttribute(ShiroUtil.INDUSTRY, ShiroUtil.getIndustry());
        model.addAttribute("hasApprove", hasApprove);
        model.addAttribute("hasEdit", hasEdit);
        model.addAttribute("hasBack", hasBack);
        model.addAttribute("hasEditFile", hasEditFile);
        model.addAttribute("hasWithdraw", hasWithdrawFlg(approve));
        model.addAttribute("hasInvalid", hasInvalid);
        model.addAttribute("backEditFlg", backEditFlg);
        model.addAttribute("lastApproveStepFlg", lastApproveStep(approve.getId()));
        model.addAttribute("approveStatusValue", approve.getStatus());
        String statusName = DictUtil.getValue(BasConstants.DICT_TYPE_APPROVESTATUS, approve.getStatus());
        approve.setStatus(statusName);
        model.addAttribute("approve", approve);
        model.addAttribute("contentUrl", process.getContentUrl());
        model.addAttribute("processCode", process.getProcessCode());
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("approveOpinionJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVEOPINION)));
        //包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        //获取审批的步骤
        PageSearchVo queryVo = new PageSearchVo();
        queryVo.setRows(20);
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EQL_processId", approve.getProcessId());
        if (approve.getConditionId() != null && approve.getConditionId().compareTo(new Long(0)) > 0) {
            searchParams.put("EQL_conditionId", approve.getConditionId());
        }
        searchParams.put("EQB_enableFlg", true);
        queryVo.setSort("dispOrderNo");
        queryVo.setOrder("ASC");
        queryVo.setSearchParams(searchParams);
        PageDown<PmProcessStep> stepPage = processStepClient.findPage(queryVo);
        model.addAttribute("stepListJson", JsonUtil.obj2Json(stepPage.getContent()));

        //如果是采购销售撮合进口代理需判断是否有合同模板
        boolean existTemp = existTemplate(process);
        model.addAttribute("exitsTemp", existTemp);
        boolean isFromContract = false;
        String from = request.getParameter("from");
        if (from != null && from.equals("dcContract")) {
            isFromContract = true;
        }
        model.addAttribute("isFromContract", isFromContract);

        // 是否是背靠背业务合同
        model.addAttribute("isFromBkb", null);
        // 保理业务发起提示信息
        String blMessage = BsDictUtil.getValue(process.getEnterpriseId(), BasConstants.BS_DICT_TYPE_SYSCONFIG, BasConstants.BS_DICT_TYPE_BL_MESSAGE);
        model.addAttribute("blMessage",blMessage);
        model.addAttribute("isMobile", ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")));
        return "pm/approve-detail";
    }

    private boolean existTemplate(PmProcess process) {
        boolean existTemp = false;
        String processCode = process.getProcessCode();
        String[] array = new String[]{BasConstants.APPLY_TYPE_A, BasConstants.APPLY_TYPE_B, BasConstants.APPLY_TYPE_S,
                BasConstants.APPLY_TYPE_L, BasConstants.APPLY_TYPE_M, BasConstants.APPLY_TYPE_R};
        if (Arrays.asList(array).contains(processCode)) {
            BsContractTemplate template = new BsContractTemplate();
            template.setEnterpriseId(process.getEnterpriseId());
            template.setTemplateTag(BasConstants.TEMPLATE_CONTRACT_BUY);
            BsContractTemplate buyTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
            template.setTemplateTag(BasConstants.TEMPLATE_CONTRACT_SALE);
            BsContractTemplate sellTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);
            if (processCode.equals(BasConstants.APPLY_TYPE_A) || processCode.equals(BasConstants.APPLY_TYPE_B)) {
                if (buyTemplate != null) {
                    existTemp = true;
                }
            } else if (processCode.equals(BasConstants.APPLY_TYPE_S) || processCode.equals(BasConstants.APPLY_TYPE_L)) {
                if (sellTemplate != null) {
                    existTemp = true;
                }
            } else if (processCode.equals(BasConstants.APPLY_TYPE_M) || processCode.equals(BasConstants.APPLY_TYPE_R)) {
                if (buyTemplate != null || sellTemplate != null) {
                    existTemp = true;
                }
            }
        }
        return existTemp;
    }

    /**
     * 审批历史
     * @param id
     * @param model
     * @param response
     */
    @RequestMapping(value = "loadHistory/{id}")
    public void loadHistory(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        PageSearchVo queryVo = new PageSearchVo();
        queryVo.setRows(100);
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EQL_approveId", id);
        queryVo.setSearchParams(searchParams);
        Page<PmApproveHistory> page = approveHistoryClient.findPage(queryVo);
        List<PmApproveHistory> list = page.getContent();
        if (list == null) {
            list = new ArrayList<>(0);
        }
        String json = JsonUtil.obj2Json(list);
        RenderUtil.renderJson(json, response);
    }

    @RequestMapping(value = "approveStep/{id}")
    public void approveStep(@PathVariable("id") Long id, Model model, HttpServletResponse response, HttpServletRequest request) {
        PmApprove approve = new PmApprove();
        approve.setId(id);
        approve.setProcessId(Long.valueOf(request.getParameter("processId")));
        approve.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmApproveHistory> list = approveHistoryClient.findByApproveIdOrProcessId(approve);
        RenderUtil.renderJson(JsonUtil.obj2Json(list), response);
    }

    /**
     * 新建审批单
     * @param processId
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "newFlow/{processId}", method = RequestMethod.GET)
    public String newFlow(@PathVariable("processId") Long processId, Model model, HttpServletRequest request) {
        PmProcess process = processClient.getEntity(processId);
        String processCode = process.getProcessCode();
        PmApproveDownVo approve = new PmApproveDownVo();
        approve.setId(0L);
        approve.setBizId(0L);
        approve.setProcessId(processId);
        approve.setProcessName(process.getProcessName());
        String statusName = DictUtil.getValue(BasConstants.DICT_TYPE_APPROVESTATUS, BasConstants.APPROVE_STATUS_N);
        approve.setStatus(statusName);
        String contractId = request.getParameter("contractId");
        model.addAttribute("approve", approve);
        model.addAttribute("contentUrl", process.getContentUrl());
        model.addAttribute("processCode", processCode);
        model.addAttribute("hasEdit", true);
        model.addAttribute("hasBack", false);
        model.addAttribute("hasEditFile", true);
        model.addAttribute("hasApprove", false);
        model.addAttribute("hasWithdraw", hasWithdrawFlg(approve));
        model.addAttribute("hasPayBatchApply", hasPayBatchApply(process, contractId));
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("approveOpinionJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVEOPINION)));
        //包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        //获取审批的步骤
        PageSearchVo queryVo = new PageSearchVo();
        queryVo.setRows(20);
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("EQB_enableFlg", true);
        searchParams.put("EQL_processId", processId);
        queryVo.setSort("dispOrderNo");
        queryVo.setOrder("ASC");
        queryVo.setSearchParams(searchParams);
        PageDown<PmProcessStep> stepPage = processStepClient.findPage(queryVo);
        model.addAttribute("stepListJson", JsonUtil.obj2Json(stepPage.getContent()));
        model.addAttribute("deptAbbr", ShiroUtil.getDeptAbbr());
        //获得通知事项 的合同id
        String waybillCode = request.getParameter("waybillCode");
        model.addAttribute("waybillCode", waybillCode);
        if (contractId != null) {
            String isServiceInvoice = request.getParameter("isServiceInvoice");
            // 判断是否是服务费开票申请
            if (StringUtils.equals("0", isServiceInvoice)) {
                model.addAttribute("isServiceInvoice", true);
                CtrServiceContractChooseVo serviceContract = contractClient.findByServiceContractId(Long.valueOf(contractId));
                model.addAttribute("contract", JsonUtil.obj2Json(serviceContract));
                model.addAttribute("contractId", contractId);
            } else {
                model.addAttribute("isServiceInvoice", false);
                CtrContractChooseVo contract = contractClient.findByContractId(Long.valueOf(contractId));
                model.addAttribute("contract", JsonUtil.obj2Json(contract));
                model.addAttribute("contractId", contractId);
                boolean isFromBkb = false;
                ApplyMatchDetail byContractNo = applyMatchDetailClient.findByContractNo(contract.getContractNo());
                if (byContractNo != null && byContractNo.getApplyMatchId() != null) {
                    ApplyMatch entity = applyMatchClient.getEntity(byContractNo.getApplyMatchId());
                    if (BasConstants.BUSINESS_TYPE_ZY_BB.equals(entity.getBusinessType())) {
                        isFromBkb = true;
                    }
                }
                model.addAttribute("isFromBkb", isFromBkb);
            }
        }

        String companyId = request.getParameter("companyId");
        if (companyId != null) {
            model.addAttribute("companyId", companyId);
        }

        model.addAttribute("tips", process.getSponsorTips() == null ? "" : process.getSponsorTips());
        // 保理业务发起提示信息
        String blMessage = BsDictUtil.getValue(process.getEnterpriseId(), BasConstants.BS_DICT_TYPE_SYSCONFIG, BasConstants.BS_DICT_TYPE_BL_MESSAGE);
        model.addAttribute("blMessage",blMessage);
        model.addAttribute("isMobile", ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")));
        model.addAttribute("logisticsId", request.getParameter("logisticsId"));
        model.addAttribute("logisticsDeliveryId", request.getParameter("logisticsDeliveryId"));
        // 适配诉讼案件费用申请
        String litigationCaseId = request.getParameter("litigationCaseId");
        model.addAttribute("litigationCaseId", litigationCaseId);
        String feeType = request.getParameter("feeType");
        model.addAttribute("feeType", feeType);
        return "pm/approve-detail";
    }

    /**
     * 新建审批单(我方和宁圣)
     * @param processId
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "newFlowMy/{processId}", method = RequestMethod.GET)
    public String newFlowMy(@PathVariable("processId") Long processId, Model model, HttpServletRequest request) {
        PmProcess process = processClient.getEntity(processId);
        String processCode = process.getProcessCode();
        PmApproveDownVo approve = new PmApproveDownVo();
        approve.setId(0L);
        approve.setBizId(0L);
        approve.setProcessId(processId);
        approve.setProcessName(process.getProcessName());
        String statusName = DictUtil.getValue(BasConstants.DICT_TYPE_APPROVESTATUS, BasConstants.APPROVE_STATUS_N);
        approve.setStatus(statusName);
        model.addAttribute("approve", approve);
        model.addAttribute("contentUrl", process.getContentUrl());
        model.addAttribute("processCode", processCode);
        model.addAttribute("hasEdit", true);
        model.addAttribute("hasBack", false);
        model.addAttribute("hasEditFile", true);
        model.addAttribute("hasApprove", false);
        model.addAttribute("hasWithdraw", hasWithdrawFlg(approve));
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("approveOpinionJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVEOPINION)));
        //包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        //获取审批的步骤
        PageSearchVo queryVo = new PageSearchVo();
        queryVo.setRows(20);
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EQB_enableFlg", true);
        searchParams.put("EQL_processId", processId);
        queryVo.setSort("dispOrderNo");
        queryVo.setOrder("ASC");
        queryVo.setSearchParams(searchParams);
        PageDown<PmProcessStep> stepPage = processStepClient.findPage(queryVo);
        model.addAttribute("stepListJson", JsonUtil.obj2Json(stepPage.getContent()));
        model.addAttribute("deptAbbr", ShiroUtil.getDeptAbbr());
        //获得通知事项 的合同id
        String contractId = request.getParameter("contractId");
        if (contractId != null) {
            model.addAttribute("isServiceInvoice", false);
            ApplyDcsxChooseVo contract = applyCtrDcsxClinent.findById(Long.valueOf(contractId));
            model.addAttribute("contract", JsonUtil.obj2Json(contract));
            model.addAttribute("contractId", contractId);
            model.addAttribute("companyId", contract.getCompanyId());
            boolean isFromBkb = true;
            model.addAttribute("isFromBkb", isFromBkb);
        }
        // 保理业务发起提示信息
        String blMessage = BsDictUtil.getValue(process.getEnterpriseId(), BasConstants.BS_DICT_TYPE_SYSCONFIG, BasConstants.BS_DICT_TYPE_BL_MESSAGE);
        model.addAttribute("blMessage",blMessage);
        model.addAttribute("isMobile", ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")));
        return "pm/approve-detail";
    }

    /**
     * 二次审批单申请
     * @param processId
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "newFlow2/{processId}/{id}", method = RequestMethod.GET)
    public String newFlow2(@PathVariable("processId") Long processId, @PathVariable("id") Long id, Model model, HttpServletRequest request) {
        model.addAttribute("pid", id);
        PmProcess process = processClient.getEntity(processId);
        String processCode = process.getProcessCode();
        PmApproveDownVo approve = new PmApproveDownVo();
        approve.setId(0L);
        approve.setBizId(0L);
        approve.setProcessId(processId);
        approve.setProcessName(process.getProcessName());
        String statusName = DictUtil.getValue(BasConstants.DICT_TYPE_APPROVESTATUS, BasConstants.APPROVE_STATUS_N);
        approve.setStatus(statusName);
        model.addAttribute("approve", approve);
        model.addAttribute("contentUrl", process.getContentUrl());
        model.addAttribute("processCode", processCode);
        model.addAttribute("hasEdit", true);
        model.addAttribute("hasBack", false);
        model.addAttribute("hasEditFile", true);
        model.addAttribute("hasApprove", false);
        model.addAttribute("hasWithdraw", hasWithdrawFlg(approve));
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("approveOpinionJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVEOPINION)));
        //包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        //获取审批的步骤
        PageSearchVo queryVo = new PageSearchVo();
        queryVo.setRows(20);
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EQB_enableFlg", true);
        searchParams.put("EQL_processId", processId);
        queryVo.setSort("dispOrderNo");
        queryVo.setOrder("ASC");
        queryVo.setSearchParams(searchParams);
        PageDown<PmProcessStep> stepPage = processStepClient.findPage(queryVo);
        model.addAttribute("stepListJson", JsonUtil.obj2Json(stepPage.getContent()));
        model.addAttribute("deptAbbr", ShiroUtil.getDeptAbbr());
        //获得通知事项 的合同id
        String contractId = request.getParameter("contractId");
        if (contractId != null) {
            String isServiceInvoice = request.getParameter("isServiceInvoice");
            // 判断是否是服务费开票申请
            if (StringUtils.equals("0", isServiceInvoice)) {
                model.addAttribute("isServiceInvoice", true);
                CtrServiceContractChooseVo serviceContract = contractClient.findByServiceContractId(Long.valueOf(contractId));
                model.addAttribute("contract", JsonUtil.obj2Json(serviceContract));
                model.addAttribute("contractId", contractId);
            } else {
                model.addAttribute("isServiceInvoice", false);
                CtrContractChooseVo contract = contractClient.findByContractId(Long.valueOf(contractId));
                model.addAttribute("contract", JsonUtil.obj2Json(contract));
                model.addAttribute("contractId", contractId);
                boolean isFromBkb = false;
                ApplyMatchDetail byContractNo = applyMatchDetailClient.findByContractNo(contract.getContractNo());
                if (byContractNo != null && byContractNo.getApplyMatchId() != null) {
                    ApplyMatch entity = applyMatchClient.getEntity(byContractNo.getApplyMatchId());
                    if (BasConstants.BUSINESS_TYPE_ZY_BB.equals(entity.getBusinessType())) {
                        isFromBkb = true;
                    }
                }
                model.addAttribute("isFromBkb", isFromBkb);
            }
        }

        String companyId = request.getParameter("companyId");
        if (companyId != null) {
            model.addAttribute("companyId", companyId);
        }
        model.addAttribute("tips", process.getSponsorTips() == null ? "" : process.getSponsorTips());
        // 保理业务发起提示信息
        String blMessage = BsDictUtil.getValue(process.getEnterpriseId(), BasConstants.BS_DICT_TYPE_SYSCONFIG, BasConstants.BS_DICT_TYPE_BL_MESSAGE);
        model.addAttribute("blMessage",blMessage);
        model.addAttribute("isMobile", ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")));
        return "pm/approve-detail";
    }


    /**
     * 保存审批信息
     * @param entity
     * @param vo
     * @param request
     * @param response
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(@Valid @ModelAttribute("pmEntity") IdEntity entity, PmApproveSaveVo vo,
                     HttpServletRequest request, HttpServletResponse response) {
        //由合同管理中发起的出入库收开票等申请单，若当前用户没有该权限，则不能发起
        PmProcessSearchVo searchVo = new PmProcessSearchVo();   //！！代采预算申请会走到这
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        try {
            IPmEntity pmEntity = (IPmEntity) entity;
            vo.setUserId(ShiroUtil.getCurrentUserId());
            vo.setUserName(ShiroUtil.getCurrentUserName());
            vo.setDeptId(ShiroUtil.getDeptId());
            vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            if (pmEntity.getSubClass() != null) {
                List<?> lstInsert = null;
                List<?> lstUpdate = null;
                List<?> lstDelete = null;
                if (pmEntity instanceof ApplyMatchVo) {    //撮合申请
                    ApplyMatchVo applyMatch = (ApplyMatchVo) pmEntity;
                    applyMatch.setEnterpriseId(vo.getEnterpriseId());
                    if (Objects.isNull(applyMatch.getApplyUserId())) {
                        applyMatch.setApplyUserId(ShiroUtil.getCurrentUserId());
                    }
                    if (Objects.isNull(applyMatch.getCreatedUserId())) {
                        applyMatch.setCreatedUserId(ShiroUtil.getCurrentUserId());
                    }
                    //获得表单数据
                    String data = applyMatch.getContentStr();

                    //解析撮合信息----将采购和销售以及当前操作人信息全部保存到一个String字段.
                    List<ApplyMatchDetailVo> list = JSON.parseArray(data, ApplyMatchDetailVo.class);
                    for (ApplyMatchDetailVo matchDetailVo : list) {
                        matchDetailVo.setMatchUserName(UserCache.getUserName(matchDetailVo.getMatchUserId()));
                        matchDetailVo.setDeliveryMode(matchDetailVo.getDeliveryMode());
                    }
                    String parameter = request.getParameter(JsonEasyUI.JSON_NAME);
                    if (StringUtils.isNotBlank(parameter) && !StringUtils.equals("{}",parameter)){
                        List<ApplyMatchChain> insertRecords = JsonEasyUI.getInsertRecords(ApplyMatchChain.class, request);
                        List<ApplyMatchChain> updatedRecords = JsonEasyUI.getUpdatedRecords(ApplyMatchChain.class, request);
                        List<ApplyMatchChain> deletedRecords = JsonEasyUI.getDeletedRecords(ApplyMatchChain.class, request);
                        pmEntity.setChainBatchSub(insertRecords, updatedRecords, deletedRecords);
                    }
                    pmEntity.setBatchSub(list, null, null);
                } else if (pmEntity instanceof ApplyImportVo) {        //进口代理
                    ApplyImportVo importVo = (ApplyImportVo) pmEntity;
                    //保存企业id
                    importVo.setEnterpriseId(vo.getEnterpriseId());
                    //配送方式
                    String deliveryType = importVo.getDeliveryType();
                    //获得表单数据
                    String data = importVo.getContentStr();
                    //解析进口代理信息
                    List<ApplyImportDetailVo> list = JSON.parseArray(data, ApplyImportDetailVo.class);

                    for (ApplyImportDetailVo importDeail : list) {
                        importDeail.setDeliveryMode(importDeail.getDeliveryMode());
                        importDeail.setDeliveryType(deliveryType);
                        //获得随码
                        String randomNumber = importDeail.getRandomNumber();
                        //获得进口代理明细的货品信息
                        lstInsert = JsonEasyUI.getInsertRecords(ApplyProductDetail.class, randomNumber, request);
                        lstUpdate = JsonEasyUI.getUpdatedRecords(ApplyProductDetail.class, randomNumber, request);
                        lstDelete = JsonEasyUI.getDeletedRecords(ApplyProductDetail.class, randomNumber, request);
                        importDeail.setBatchSub(lstInsert, lstUpdate, lstDelete);
                    }
                    pmEntity.setBatchSub(list, null, null);
                } else {
                    lstInsert = JsonEasyUI.getInsertRecords(pmEntity.getSubClass(), request);
                    lstUpdate = JsonEasyUI.getUpdatedRecords(pmEntity.getSubClass(), request);
                    lstDelete = JsonEasyUI.getDeletedRecords(pmEntity.getSubClass(), request);
                    pmEntity.setBatchSub(lstInsert, lstUpdate, lstDelete);
                }
            }
            if (pmEntity instanceof ApplyReceive) {
                ApplyReceive applyReceive = (ApplyReceive) pmEntity;
                List<ApplyReceive> list = JSON.parseArray(applyReceive.getReceiveDetailListStr(), ApplyReceive.class);
                applyReceive.setReceiveDetailList(list);
            }
            String entityJson = JsonUtil.obj2Json(pmEntity);
            logger.info("save : " + entityJson);
            vo.setBizEntityJson(entityJson);
            PmApprove approve = null;

            if (StringUtils.equals(vo.getMode(), "A")) {
                // 申请
                vo.setStatus(BasConstants.APPROVE_STATUS_A);

                if (Boolean.TRUE.equals(vo.isBatchPayApply())){
                    approve = applyPayClient.startBatchPayApply(vo);
                    RenderUtil.renderSuccess(JsonUtil.obj2Json(approve), response);
                    return;
                }

                approve = approveClient.startFlow(vo);
            } else if (StringUtils.equals(vo.getMode(), "P")) {
                // 审批中修改
                vo.setStatus(BasConstants.APPROVE_STATUS_A);
                approve = approveClient.startFlow(vo);
            } else if (StringUtils.equals(vo.getMode(), "S")) {
                // 存草稿
                vo.setStatus(BasConstants.APPROVE_STATUS_N);
                approve = approveClient.startFlow(vo);
            }
            RenderUtil.renderSuccess(JsonUtil.obj2Json(approve), response);
        } catch (Exception e) {
            ErrorResp errsp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errsp.getMessage(), response);
        }
    }

    /**
     * 下一步
     * @param vo
     * @param request
     * @param response
     */
    @RequestMapping(value = "doStep")
    public void doStep(PmApproveStepFlowVo vo, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(vo.getApproveOpinion())) {
                vo.setApproveOpinion(BasConstants.APPROVE_OPINION_DENY);
            }
            vo.setApproveUserId(ShiroUtil.getCurrentUserId());
            vo.setApproveUserName(ShiroUtil.getCurrentUserName());
            setLogParam(vo);
            approveClient.doStepFlow(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("error:" + JsonUtil.obj2Json(vo), e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
    }

    private void setLogParam(PmApproveStepFlowVo vo) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        vo.setIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        vo.setBrowser(userAgent.getBrowser().getName());
        vo.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 批量审批-一键同意
     * @param response
     */
    @RequestMapping(value = "doBatchStep/{approveIds}")
    public void doBatchStep(@PathVariable("approveIds") String approveIds, HttpServletResponse response) {
        logger.info("doBatchStep approveIds:{},optionName:{}", approveIds, ShiroUtil.getCurrentUserName());
        if (!ShiroUtil.isPermitted(PermissionEnum.PERAM_BATCH_APPROVE.getPermissionCode())){
            RenderUtil.renderFailure("暂无批量审批权限，请联系管理员!", response);
            return;
        }
        if (StringUtils.isBlank(approveIds)){
            RenderUtil.renderFailure("参数异常!", response);
            return;
        }
        PmApproveStepFlowVo vo = new PmApproveStepFlowVo();
        try {
            List<String> idsList = Splitter.on(BasConstants.COMMA).splitToList(approveIds);
            List<Long> approveIdList = idsList.stream().map(Long::valueOf).collect(Collectors.toList());
            vo.setApproveOpinion(BasConstants.APPROVE_OPINION_AGREE);
            vo.setApproveUserId(ShiroUtil.getCurrentUserId());
            vo.setApproveUserName(ShiroUtil.getCurrentUserName());
            vo.setApproveIds(approveIdList);
            vo.setApproveRemark("一键同意");
            approveClient.doBatchStepFlow(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("error:" + JsonUtil.obj2Json(vo), e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
    }


    /**
     * 审批撤回
     * @param vo
     * @param request
     * @param response
     */
    @RequestMapping(value = "doWithdraw")
    public void doWithdraw(PmApproveWithdrawVo vo, HttpServletRequest request, HttpServletResponse response) {
        //是否有撤回权限
        try {
            //查询订单是否完成
            vo.setUserId(ShiroUtil.getCurrentUserId());
            vo.setUserName(ShiroUtil.getCurrentUserName());
            approveClient.doWithdraw(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("error:" + JsonUtil.obj2Json(vo), e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
    }

    /**
     * 审批追回
     * @param vo
     * @param request
     * @param response
     */
    @RequestMapping(value = "doRetrieve")
    public void doRetrieve(PmApproveRetrieveVo vo, HttpServletRequest request, HttpServletResponse response) {
        try {
            //是否可以追回
            PmApprove approve = approveClient.getEntity(vo.getApproveId());
            Boolean backEditFlg = backEditFlg(approve);
            if (backEditFlg) {
                vo.setUserId(ShiroUtil.getCurrentUserId());
                vo.setUserName(ShiroUtil.getCurrentUserName());
                approveClient.doRetrieve(vo);
                RenderUtil.renderSuccess("success", response);
            }
        } catch (Exception e) {
            logger.error("error:" + JsonUtil.obj2Json(vo), e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
    }

    /**
     * 使用@ModelAttribute, 实现Struts2
     * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
     */
    @ModelAttribute("preload")
    public PmApprove getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                PmApprove entity = new PmApprove();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                return entity;
            }
        }
        return null;
    }

    /**
     * 使用@ModelAttribute, 实现Struts2
     * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
     */
    @ModelAttribute("pmEntity")
    public IPmEntity getBizEntity(@RequestParam(value = "processId", required = false) Long processId, @RequestParam(value = "id", required = false) Long entityId) {
        IPmEntity entity = null;
        if (processId != null) {
            if (processId > 0) {
                PmProcess process = processClient.getEntity(processId);
                String entityName = process.getEntityName();
                if (StringUtils.isNotBlank(entityName)) {
                    try {
                        entity = (IPmEntity) Class.forName(entityName).newInstance();
                        if (entityId != null && entityId > 0) {
                            String entityClient = process.getEntityService().replaceAll("Service", "Client");
                            BaseClient<?> pmClient = SpringContextHolder.getBean(entityClient);
                            IPmEntity pmEntity = (IPmEntity) pmClient.getEntity(entityId);
                            PropertyUtils.copyProperties(entity, pmEntity);
                        }
                    } catch (Exception e) {
                        logger.warn("getBizEntity err, processId:{},entityId:{}", processId, entityId);
                        // logger.error("getBizEntity", e);
                    }
                }

            }
        }
        return entity;
    }

    @Value("${file.show.url}")
    private String fileShowUrl;

    @RequestMapping(value = "downLoadContract")
    public void downLoadContract(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InvocationTargetException {

        String bizId = request.getParameter("bizId");
        String processCode = request.getParameter("processCode");
        CtrContract ctr = new CtrContract();
        if (processCode.equals("APPLY_BUY")) { // 采购申请内容下载
            ApplyBuy entity = buyClient.getEntity(Long.parseLong(bizId));
            PmApproveVo approve = approveClient.getApproveVo(entity.getApproveId());
            try {
                PropertyUtils.copyProperties(ctr, entity);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (StringUtils.isNotBlank(entity.getShippingAddr())) {
                ctr.setDeliveryAddr(entity.getShippingAddr());
            }
            ctr.setContractType(BasConstants.APPLY_TYPE_B);

            ApplyDeliveryApplyIdVo vo = new ApplyDeliveryApplyIdVo();
            vo.setApplyType(entity.getApplyType());
            vo.setApplyId(entity.getId());

            List<ApplyProductDetail> detailList = productDetailClient.findApplyId(vo);
            Long createUserId = null;
            if (approve != null && approve.getApprove() != null) {
                createUserId = approve.getApprove().getCreateUserId();
            }
            File f = this.downLoadWord(processCode, detailList, ctr, createUserId, "");
            WorderToNewWordUtils.download(response, f);
        } else if (processCode.equals("APPLY_SELL")) { // 销售申请内容下载
            ApplySell entity = sellClient.getEntity(Long.parseLong(bizId));
            PmApproveVo approve = approveClient.getApproveVo(entity.getApproveId());
            try {
                PropertyUtils.copyProperties(ctr, entity);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            ctr.setContractType(BasConstants.APPLY_TYPE_S);

            ApplyDeliveryApplyIdVo applyVo = new ApplyDeliveryApplyIdVo();
            applyVo.setApplyId(entity.getId());
            applyVo.setApplyType(BasConstants.APPLY_TYPE_S);

            List<ApplyProductDetail> detailList = productDetailClient.findApplyId(applyVo);

            File f = this.downLoadWord(processCode, detailList, ctr, approve.getApprove().getCreateUserId(), "");
            WorderToNewWordUtils.download(response, f);

        } else if (processCode.equals("APPLY_MATCH")) { // 撮合业务申请内容下载
            ApplyMatch entity = applyMatchClient.getEntity(Long.parseLong(bizId));
            PmApproveVo approve = approveClient.getApproveVo(entity.getApproveId());
            if (entity.getId() != 0) {
                ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
                vo.setApplyMatchId(entity.getId());
                List<ApplyMatchDetail> list = applyMatchDetailClient.findByApplyMatchId(vo);
                for (ApplyMatchDetail matchList : list) {
                    try {
                        PropertyUtils.copyProperties(ctr, matchList);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    ctr.setOurCompanyName(entity.getOurCompanyName());
                    ctr.setDeliveryAddr(entity.getShippingAddr());

                    ApplyDeliveryApplyIdVo applyVo = new ApplyDeliveryApplyIdVo();
                    applyVo.setApplyId(matchList.getId());
                    applyVo.setApplyType(BasConstants.APPLY_TYPE_M);

                    List<ApplyProductDetail> detailList = productDetailClient.findApplyId(applyVo);

                    this.downLoadWord(processCode, detailList, ctr, approve.getApprove().getCreateUserId(),
                            String.valueOf(entity.getId()));

                }
                try {
                    File zipFiletmp = new File(wordOutputUrl + entity.getId() + File.separator);
                    if (!zipFiletmp.exists() && !(zipFiletmp.isDirectory())) {
                        zipFiletmp.mkdirs();
                    }
                    File f = ZipFileUtils.createZip(wordOutputUrl + entity.getId() + File.separator, wordOutputUrl,
                            "撮合业务");
                    WorderToNewWordUtils.download(response, f);
                    ZipFileUtils.delFolder(wordOutputUrl + entity.getId() + File.separator);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else if (processCode.equals("APPLY_IMPORT")) { // 进口代理申请内容下载
            ApplyImport entity = applyImportClient.getEntity(Long.parseLong(bizId));
            if (entity.getId() != 0) {
                PmApproveVo approve = approveClient.getApproveVo(entity.getApproveId());
                ApplyImportQueryVo vo = new ApplyImportQueryVo();
                vo.setApplyImportId(entity.getId());
                List<ApplyImportDetail> importList = applyImportDetailClient.findByApplyImportId(vo);
                for (ApplyImportDetail matchList : importList) {
                    try {
                        PropertyUtils.copyProperties(ctr, matchList);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    ctr.setOurCompanyName(entity.getOurCompanyName());

                    ApplyDeliveryApplyIdVo applyVo = new ApplyDeliveryApplyIdVo();
                    applyVo.setApplyId(matchList.getId());
                    applyVo.setApplyType(BasConstants.APPLY_TYPE_R);
                    List<ApplyProductDetail> detailList = productDetailClient.findApplyId(applyVo);
                    this.downLoadWord(processCode, detailList, ctr, approve.getApprove().getCreateUserId(),
                            String.valueOf(entity.getId()));
                }

                try {
                    File zipFiletmp = new File(wordOutputUrl + entity.getId() + File.separator);
                    if (!zipFiletmp.exists() && !(zipFiletmp.isDirectory())) {
                        zipFiletmp.mkdirs();
                    }
                    File f = ZipFileUtils.createZip(wordOutputUrl + entity.getId() + File.separator, wordOutputUrl,
                            "进口代理");
                    WorderToNewWordUtils.download(response, f);
                    ZipFileUtils.delFolder(wordOutputUrl + entity.getId() + File.separator);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @RequestMapping(value = "orverdurNotice")
    public void orverdurNotice(HttpServletRequest request, HttpServletResponse response) {
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_ORVERDUR_NOTICE.getPermissionCode())) {
            RptCtrContractOrverdur searchVo = new RptCtrContractOrverdur();
            searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            searchVo.setSearchType("receive");
            searchVo.setContractType("S");
            if (!ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
                if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
                    List<Long> deptIdList = webParamUtils.getMyDeptId(webParamUtils.getDeptLeader());
                    searchVo.setDeptIdList(deptIdList);
                } else {
                    searchVo.setDeptId(ShiroUtil.getDeptId());
                    searchVo.setMatchUserId(ShiroUtil.getCurrentUserId());
                }
            }
            PageDown<RptCtrContractOrverdur> page = ctrContractOrverdurClient.findPageOrverdur(searchVo);
            RptCtrContractOrverdur sum = ctrContractOrverdurClient.findPageTotal(searchVo);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("orverdurNum", page.getTotalElements());
            resultMap.put("orverdurAmount", sum.getOrverdurAmount());
            RenderUtil.renderJson(resultMap, response);
        }
    }

    public File downLoadWord(String processCode, List<ApplyProductDetail> detailList, CtrContract ctr, Long matchUserId, String zip){
        BigDecimal totalAmount = BigDecimal.ZERO;
        String wareHouseName = "";
        List<String[]> testList = new ArrayList<>();
        for (ApplyProductDetail ctrProduct : detailList) {
            String wrapSpecs = ctrProduct.getWrapSpecs();
            String wrapSpecsStr = DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, wrapSpecs);
            testList.add(new String[]{ctrProduct.getProductName(), ctrProduct.getBrandNumber(), ctrProduct.getFactoryName(), wrapSpecsStr, String.valueOf(ctrProduct.getDealNumber()), String.valueOf(ctrProduct.getDealPrice()), String.valueOf(ctrProduct.getTotalPrice())});
            totalAmount = totalAmount.add(ctrProduct.getTotalPrice());
            wareHouseName = ctrProduct.getWarehouseName();
        }
        ctr.setTotalAmount(totalAmount);

        //根据合同类型选择对应的word附件ID
        BsContractTemplate template = new BsContractTemplate();
        if (ctr.getContractType().equals(BasConstants.CONTRACT_TYPE_B)) {
            template.setTemplateTag(BasConstants.TEMPLATE_CONTRACT_BUY);
        } else {
            template.setTemplateTag(BasConstants.TEMPLATE_CONTRACT_SALE);
        }
        template.setEnterpriseId(ctr.getEnterpriseId());
        BsContractTemplate bsTemplate = bsContractTemplateClient.findByTemplateTagAndEnterpriseId(template);

        Map<String, Object> param = new HashMap<>();
        param.put(ContractDownloadUtils.PARAM_WORDOUTPUTURL, wordOutputUrl);
        param.put(ContractDownloadUtils.PARAM_PROCESSCODE, processCode);
        param.put(ContractDownloadUtils.PARAM_TABLELIST, testList);
        param.put(ContractDownloadUtils.PARAM_WAREHOUSENAME, wareHouseName);
        param.put(ContractDownloadUtils.PARAM_ZIP, zip);
        param.put(ContractDownloadUtils.PARAM_MATCHUSERID, matchUserId);
        param.put(ContractDownloadUtils.PARAM_BSTEMPLATE, bsTemplate);
        param.put(ContractDownloadUtils.PARAM_FILESHOWURL, fileShowUrl);
        List<BsDictData> bsDictList = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
        return ContractDownloadUtils.downLoadContract(param, ctr, bsDictList);
    }

    @RequestMapping(value = "findByApproveNo")
    public void findByApproveNo(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String rtn = "";
        if (StringUtils.isNotBlank(id)) {
            PmApprove approve = this.approveClient.getEntity(Long.parseLong(id));
            if (approve != null) {
                rtn = approve.getApproveNo();
            }
        }
        RenderUtil.renderSuccess(rtn, response);
    }

    //判断申请单发起人是否可以自行撤回
    private Boolean backEditFlg(PmApproveDownVo approve) {
        return backEditFlg(approve.getCreateUserId(), approve.getCurrApproveStepId(), approve.getStatus(), approve.getId());
    }

    private Boolean backEditFlg(PmApprove approve) {
        return backEditFlg(approve.getCreateUserId(),approve.getCurrApproveStepId(), approve.getStatus(), approve.getId());
    }

    /**
     * 追回按钮逻辑判断
     * 1.发起后第一个审批人还未进行审批 【发起人】可追回
     * 2.当前审批人审批后（需排除系统自动跳过）、下一步骤还未进行审批 【审批人】可追回
     *
     * @param createUserId
     * @param currApproveStepId
     * @param status
     * @param approveId
     * @return
     */
    private Boolean backEditFlg(Long createUserId, String currApproveStepId, String status, Long approveId) {
        Long currentUserId = ShiroUtil.getCurrentUserId();
        List<PmApproveStep> stepLists = approveStepClient.findByApproveId(approveId);
        if (CollectionUtils.isEmpty(stepLists)) {
            return false;
        }
        stepLists = stepLists.stream().filter(s -> !StringUtils.equals("[审批人重合系统自动跳过]", s.getApproveRemark())).collect(Collectors.toList());
        // 1.发起后第一个审批人还未进行审批 【发起人】可追回
        PmApproveStep firstStep = stepLists.stream().findFirst().orElse(null);
        if (firstStep != null) {
            //第一个审批人未审批，发起人可以追回
            String approveOpinion = firstStep.getApproveOpinion();
            if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, status) && currentUserId.equals(createUserId) && StringUtils.isBlank(approveOpinion)) {
                return true;
            }
        }
        // 2.当前审批人审批后（需排除系统自动跳过）、下一步骤还未进行审批 【审批人】可追回
        if (NumberUtil.isNumber(currApproveStepId)){
            Long stepId = Long.valueOf(currApproveStepId);
            PmApproveStep currStep = stepLists.stream().filter(s -> Objects.equals(s.getId(), stepId)).findAny().orElse(null);
            if (Objects.nonNull(currStep) && StringUtils.isBlank(currStep.getApproveOpinion())){
                Optional<PmApproveStep> verifyOptional = stepLists.stream().filter(s -> s.getDispOrderNo() < currStep.getDispOrderNo() &&
                        Objects.equals(currentUserId, s.getApproveUserId()) &&
                        !StringUtils.equals("[审批人重合系统自动跳过]", s.getApproveRemark())).findAny();
                return verifyOptional.isPresent();
            }
        }
        return false;
    }

    private Boolean lastApproveStep(Long approveId) {
        Boolean lastApproveStep = false;
        try {
            List<PmApproveStep> stepList = approveStepClient.findByApproveId(approveId);
            if (stepList != null && stepList.size() > 0) {
                List<PmApproveStep> list = stepList.stream().filter(s -> StringUtils.isBlank(s.getApproveOpinion()))
                        .collect(Collectors.toList());
                if (list.size() == 1) {
                    lastApproveStep = true;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return lastApproveStep;
    }


    @RequestMapping(value = "findMatch")
    public void findmatch(@RequestParam(value = "pid") Long pid, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        ApplyMatch applyMatch = applyMatchClient.findByApproveId(pid);
        map.put("match2", applyMatch);
        Long id = applyMatch.getId();
        ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
        vo.setApplyMatchId(id);
        List<ApplyMatchDetail> applyMatchDetails = applyMatchDetailClient.findByApplyMatchId(vo);
        //供方 校验企业白名单
        BsCompany buyCompany = bsCompanyClient.getEntity(applyMatchDetails.get(0).getCompanyId());
        if (!"W".equals(buyCompany.getSupplierRating()) || buyCompany.getMatchUserId() == null || !buyCompany.getMatchUserId().equals(applyMatchDetails.get(0).getMatchUserId())) {
            applyMatchDetails.get(0).setCompanyName("");
            applyMatchDetails.get(0).setCompanyId(null);
        }
        BsCompany needCompany = bsCompanyClient.getEntity(applyMatchDetails.get(applyMatchDetails.size()-1).getCompanyId());
        if (!"W".equals(needCompany.getCreditRating()) || needCompany.getMatchUserId() == null || !needCompany.getMatchUserId().equals(applyMatchDetails.get(1).getMatchUserId())) {
            applyMatchDetails.get(1).setCompanyName("");
            applyMatchDetails.get(applyMatchDetails.size()-1).setCompanyId(null);
        }
        map.put("matchdetailb", applyMatchDetails.get(0));
        map.put("matchdetails", applyMatchDetails.get(applyMatchDetails.size()-1));
        RenderUtil.renderJson(map, response);
    }

    @RequestMapping(value = "deleteRecord/{id}")
    public String deleteRecord(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            approveClient.deleteRecord(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            logger.error("delete record error!", e);
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }

    /**
     * 撤回按钮权限
     * 撤回权限且审批中状态或完成状态
     * @param approve
     * @return
     */
    private boolean hasWithdrawFlg(PmApprove approve){
        if (Objects.nonNull(approve)){
            String status = approve.getStatus();
            boolean permitted = ShiroUtil.isPermitted(PermissionEnum.APPROVE_WITHDRAW.getPermissionCode());
            return permitted && (StringUtils.equals(BasConstants.APPROVE_STATUS_A, status) || StringUtils.equals(BasConstants.APPROVE_STATUS_D, status));
        }
        return false;
    }

    /**
     * 付款申请批量发起权限判断
     * 1.具备[付款审批批量申请角色];
     * 2.付款申请流程;
     * 3.乙二醇合同;
     * 4.合同数量大于500吨
     *
     * @param pmProcess
     * @param contractId
     * @return
     */
    private boolean hasPayBatchApply(PmProcess pmProcess, String contractId) {
        if (!ShiroUtil.isPermitted(PermissionEnum.ZGBAS_PAY_BATCH_APPLY.getPermissionCode())) {
            return false;
        }
        if (!StringUtils.equals(BasConstants.PROCESS_CODE_PAY, pmProcess.getProcessCode())) {
            return false;
        }
        if (!NumberUtil.isNumber(contractId)) {
            return false;
        }
        return bsProductConfigClient.verifyBatchPayApply(Long.valueOf(contractId));
    }

    /**
     * 添加打印审批历史记录
     * @param approveNo 审批单号
     */
    @GetMapping("/addPrintHistory")
    public void addPrintHistory(String approveNo,HttpServletResponse response){
        RptPrintHistoryReport printHistoryReport = new RptPrintHistoryReport();
        // 审批单号
        printHistoryReport.setApproveNo(approveNo);
        // 审批人id
        printHistoryReport.setPrintId(ShiroUtil.getCurrentUserId());
        // 审批名字
        printHistoryReport.setPrintName(ShiroUtil.getCurrentUserName());
        Integer printCount = printHistoryReportClient.addPrintHistory(printHistoryReport);
        HashMap<String, Object> result = new HashMap<>();
        result.put("code",200);
        result.put("printCount",printCount);
        result.put("printName",ShiroUtil.getCurrentUserName());
        RenderUtil.renderJson(JsonUtil.obj2Json(result), response);
    }

    @ResponseBody
    @RequestMapping(value = "/exportExcel")
    public void exportExcel(PmApproveSearchVo queryVo, HttpServletRequest request, HttpServletResponse response){
        int batchSize = 500;
        buildQueryParam(queryVo, request);
        queryVo.setRows(batchSize);
        Page<PmApproveDownVo> page = approveClient.findPageApprove(queryVo);

        String[] titles = new String[]{"审批单号", "流程名称", "摘要", "当前步骤", "当前审批人", "状态", "最近审批人", "最近审批时间"};
        String[] fields = new String[]{"approveNo", "processName", "subject", "currStepName", "currApproverUserName", "statusName", "lastApproveUserName", "lastApproveDate"};
        int[] widths = new int[]{20, 20, 60, 20, 20, 20, 20, 20};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet("审批列表");
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(20);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 设置可以换行
        cellStyle.setWrapText(true);
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widths);
        int start = 0;
        while (page != null && !page.getContent().isEmpty()) {
            PoiExcelUtil.createRows(sheet, page.getContent(), fields, start, cellStyle, "yyyy/MM/dd");
            if (page.hasNext()) {
                queryVo.setPage(queryVo.getPage() + 1);
                page = approveClient.findPageApprove(queryVo);
                start += batchSize;
            } else {
                page = null;
            }
        }
        try {
            PoiExcelUtil.write(workbook, response, "审批列表");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void buildQueryParam(PmApproveSearchVo queryVo, HttpServletRequest request){
        initSearch(queryVo, request);
        queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        queryVo.setUserId(ShiroUtil.getCurrentUserId());
        queryVo.setLoginName(ShiroUtil.getShiroUser().loginName);
        // 查询条件，若为全部+有所有权限，则查询全部数据
        String mode = queryVo.getMode();

        // 业务助理角色
        if (StringUtils.isBlank(mode) && ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            queryVo.setMode(PmConstants.APPROVE_MODE_Z);
        }

        // 查看所有业务类型审批单
        if (StringUtils.isBlank(mode) && ShiroUtil.isPermitted(PermissionEnum.APPROVE_BUSINESS_VIEW_ALL.getPermissionCode())){
            queryVo.setMode(PmConstants.APPROVE_MODE_F);
        }

        // 查看所有类型审批单
        if (StringUtils.isBlank(mode) && (ShiroUtil.isPermitted(PermissionEnum.PERM_ADMIN_NEW.getPermissionCode()) || ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode()))) {
            queryVo.setMode(PmConstants.APPROVE_MODE_A);
        }

        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            Map<String, Object> searchParams = queryVo.getSearchParams();
            searchParams.put("NEQS_hideOut", "1");
        }
    }
}
