package com.spt.bas.web.controller.apply;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Splitter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.SealUsageSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 印章使用申请单
 *
 * @author xxx
 */
@Controller
@RequestMapping(value = "/apply/sealUsage")
public class ApplySealUsageController extends PageController<SealUsage, BaseVo> {
    @Autowired
    private ISealUsageClient sealUsageClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IPmApproveStepClient pmApproveStepClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private ICtrServiceContractClient ctrServiceContractClient;
    @Autowired
    private ICtrContractChainClient ctrContractChainClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private ISignFileClient signFileClient;
    @Resource
    private IBizSignClient bizSignClient;

    @Override
    public BaseClient<SealUsage> getService() {
        return sealUsageClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request) {
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        // 印章-印章类型
        model.addAttribute("sealTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_TYPE)));
        // 盖章类型
        model.addAttribute("stampTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_STAMPTYPE)));
        // 印章-公司名称
        model.addAttribute("customerNameJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
        // 印章-文件类型
        model.addAttribute("sealFileTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_FILE_TYPE)));
        return "seal/seal_usage";
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
                          HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        model.addAttribute("processCode", processCode);
        String contractIdres = request.getParameter("contractId");
        SealUsage entity = getEntity(id, processCode);
        entity = dealWithBusinessBuySign(entity, model, processCode);
        // 印章-印章类型
        model.addAttribute("sealTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_TYPE)));
        // 盖章类型
        model.addAttribute("stampTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_STAMPTYPE)));
        // 印章-公司名称
        model.addAttribute("customerNameJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
        // 印章-文件类型
        model.addAttribute("sealFileTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_FILE_TYPE)));
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status", getStatus(id));

        String businessType = entity.getBusinessType();
        Long contractId = null;
        if (!contractIdres.equals("null") && !contractIdres.equals("") && contractIdres != null) {
            contractId = Long.valueOf(contractIdres);
            CtrContract ctrContract = ctrContractClient.getEntity(contractId);
            String contractNo = ctrContract.getContractNo();
            entity.setContractNo(contractNo);//合同编号
            String companyName = ctrContract.getCompanyName();
            entity.setCustomerName(companyName);//客户
            String ourCompanyName = ctrContract.getOurCompanyName();
            entity.setCompanyName(ourCompanyName);//我方企业
            BigDecimal totalAmount = ctrContract.getTotalAmount();
            entity.setTotalAmount(totalAmount);//合同总金额
            entity.setFileType("SS");//文件类型
        } else {
            contractId = entity.getContractId();
        }

        String chain = entity.getChainDc() == true ? "T" : "F";
        model.addAttribute("chain", chain);
        Long ctrContractId = null;
        String url = "";
        if (StringUtils.isNotBlank(businessType)) {
            CtrContract ctrContract = new CtrContract();
            if (entity.getChainDc() == true) {
                CtrContractChain chains = ctrContractChainClient.findByContractNo(entity.getContractNo());
                BeanUtils.copyProperties(chains, ctrContract);
            } else {
                if (StringUtils.equals(BasConstants.CONTRACT_TYPE_F, businessType)) {
                    CtrServiceContract serviceContract = ctrServiceContractClient.getEntity(contractId);
                    ctrContract = ctrContractClient.getEntity(serviceContract.getCtrContractId());
                } else {
                    ctrContract = ctrContractClient.getEntity(contractId);
                }
            }
            Long bsTemplateContractId = ctrContract.getBsTemplateContractId();
            if (bsTemplateContractId == null) {
                if (StringUtils.equals(BasConstants.CONTRACT_TYPE_F, businessType)) {
                    String serviceContentFileId = ctrContract.getServiceContentFileId();
                    if (!StringUtils.isEmpty(serviceContentFileId)) {
                        List<String> serviceContentFileIdList = Splitter.on(",").omitEmptyStrings().splitToList(serviceContentFileId);
                        if (CollectionUtils.isNotEmpty(serviceContentFileIdList)) {
                            url = fileShowUrl + "/view/show/" + serviceContentFileIdList.get(0);
                        }
                    }
                } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, businessType)) {
                    String buyContentFileId = ctrContract.getBuyContentFileId();
                    if (!StringUtils.isEmpty(buyContentFileId)) {
                        List<String> buyContentFileIdList = Splitter.on(",").omitEmptyStrings().splitToList(buyContentFileId);
                        if (CollectionUtils.isNotEmpty(buyContentFileIdList)) {
                            url = fileShowUrl + "/view/show/" + buyContentFileIdList.get(0);
                        }
                    }
                } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, businessType)) {
                    String sellContentFileId = ctrContract.getSellContentFileId();
                    if (!StringUtils.isEmpty(sellContentFileId)) {
                        List<String> sellContentFileIdList = Splitter.on(",").omitEmptyStrings().splitToList(sellContentFileId);
                        if (CollectionUtils.isNotEmpty(sellContentFileIdList)) {
                            url = fileShowUrl + "/view/show/" + sellContentFileIdList.get(0);
                        }
                    }
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_CG, businessType) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType)) {
                    String buyContentFileId = ctrContract.getFileId();
                    if (!StringUtils.isEmpty(buyContentFileId)) {
                        List<String> sellContentFileIdList = Splitter.on(",").omitEmptyStrings().splitToList(buyContentFileId);
                        if (CollectionUtils.isNotEmpty(sellContentFileIdList)) {
                            url = fileShowUrl + "/view/show/" + sellContentFileIdList.get(0);
                        }
                    }
                }
            }
            ctrContractId = ctrContract.getId();
            model.addAttribute("businessName", BsDictUtil.getValue(ctrContract.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND, ctrContract.getBusinessKind()));
        }
        model.addAttribute("ctrContractId", ctrContractId);
        model.addAttribute("contractTextUrl", url);

        if (Objects.nonNull(entity.getApproveId())) {
            List<PmApproveStep> stepList = pmApproveStepClient.findByApproveId(entity.getApproveId());
            PmApproveStep lastStep = stepList.stream().filter(s -> StringUtils.equals(s.getApproveOpinion(), "A"))
                    .max(Comparator.comparing(PmApproveStep::getDispOrderNo)).orElse(null);
            if (Objects.nonNull(lastStep) && lastStep.getApproveUserId().toString().contains(ShiroUtil.getCurrentUserId().toString())) {
                model.addAttribute("cfcaFlag", "true");
            }
        }
        return "apply/seal-usage";
    }

    @RequestMapping(value = "findUsagePage")
    public void findUsagePage(SealUsageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Page<SealUsage> page = sealUsageClient.findUsagePage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            pmApproveContentsClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    @ModelAttribute("preload")
    public SealUsage getEntity(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam(value = "processCode", required = false) String processCode) {
        SealUsage enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (SealUsage) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                SealUsage sealUsage = new SealUsage();
                sealUsage.setSealDate(new Date());
                sealUsage.setId(0L);
                return sealUsage;
            }
        }
        return enObject;
    }

    private SealUsage dealWithBusinessBuySign(SealUsage entity, Model model, String processCode) {
        try {
            if (Objects.isNull(entity) || Objects.isNull(entity.getApproveId())) {
                return entity;
            }
            String companyName = entity.getCompanyName();
            String businessType = entity.getBusinessType();
            if (!StringUtils.equals(BasConstants.CONTRACT_TYPE_B, businessType)) {
                return entity;
            }
            List<BizSign> bizSignList = bizSignClient.getBizSignList(entity.getApproveId());
            if (CollectionUtils.isNotEmpty(bizSignList)) {
                String flkPurchaseFileId = bizSignList.stream()
                        .filter(b -> StringUtils.equals("D", b.getSignStatus()))
                        .map(BizSign::getCfcaFileId)
                        .collect(Collectors.joining(""));
                if (StringUtils.isNotBlank(flkPurchaseFileId)) {
                    entity.setFileId(entity.getFileId() + flkPurchaseFileId);
                } else {
                    String flkPurchaseSignFileId = bizSignList.stream()
                            .map(BizSign::getSignFileId)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(""));
                    entity.setFileId(entity.getFileId() + flkPurchaseSignFileId);
                }
            }
            PmApprove approve = pmApproveClient.getEntity(entity.getApproveId());
            if (Objects.isNull(approve) || !StringUtils.equals(BasConstants.APPROVE_STATUS_A, approve.getStatus())) {
                return entity;
            }
            if (!StringUtils.equals("QDZG", companyName)) {
                return entity;
            }
            PmApproveContents approveContents = pmApproveContentsClient.getEntity(entity.getId());
            if (Objects.isNull(approveContents) || StringUtils.isBlank(approveContents.getCfcaContractNo())) {
                return entity;
            }
            SignFile signFile = refreshSignFile(approveContents.getCfcaContractNo());
            if (Objects.isNull(signFile) && StringUtils.isNotBlank(approveContents.getSignShortUrl())) {
                QrConfig config = new QrConfig(220, 220);
                // 设置边距，既二维码和背景之间的边距
                config.setMargin(1);
                // 高纠错级别
                config.setErrorCorrection(ErrorCorrectionLevel.H);
                String url = approveContents.getSignShortUrl();
                String qcCodePng = QrCodeUtil.generateAsBase64(url, config, "PNG", BsDictConstants.LOGO_IMAGE_BASE_64);
                model.addAttribute("qcCodePng", qcCodePng);
            }
            entity = getEntity(entity.getId(), processCode);
        } catch (Exception e) {
            logger.error("dealWithBusinessBuySign error:", e);
        }
        return entity;
    }

    private SignFile refreshSignFile(String cfcaContractNo) {
        SignFile signFile = null;
        try {
            SignFile targetSignFile = signFileClient.findByCfcaContractNo(cfcaContractNo);
            if (Objects.nonNull(targetSignFile) && StringUtils.equals("D", targetSignFile.getSignStatus())) {
                return targetSignFile;
            }
            signFile = signFileClient.refreshSignFile(cfcaContractNo);
        } catch (Exception e) {
            logger.error("ApplySealUsageController refreshSignFile error");
        }
        return signFile;
    }

    private String getStatus(Long id) {
        if (id != null && id > 0L) {
            PmApproveContents entity = pmApproveContentsClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }
}
