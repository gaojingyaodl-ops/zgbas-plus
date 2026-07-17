package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplyMatchQueryVo;
import com.spt.bas.client.vo.BudgetSettlementVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 代采赊销(合作)
 */
@Controller
@RequestMapping(value = "/apply/cooperation")
public class ApplyMatchCooperationController extends PageController<ApplyMatch, BaseVo> {
    @Autowired
    private IApplyMatchDetailClient applyMatchDetailClient;
    @Autowired
    private IApplyMatchCooperationClient applyMatchCooperationClient;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBsProductTypeClient bsProductTypeClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsFactoryClient factoryClient;
    @Autowired
    private IBsContractTemplateClient bsContractTemplateClient;
    @Autowired
    private IBasBrandClient brandClient;
    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IBsDictClient bsDictClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Resource
    private WebParamUtils webParamUtils;
    @Override
    public BaseClient<ApplyMatch> getService() {
        return applyMatchCooperationClient;
    }

    /***
     * 撮合申请单(白条)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content/{id}")
    public String content(Model model, @PathVariable("id") Long id, PmPermissionVo permissionVo,HttpServletRequest request) {
        model.addAttribute("pid",request.getParameter("pid"));
        ApplyMatch entity = getEntity(id);
        boolean buyShowFlg = true;
        boolean sellShowFlg = true;
        boolean hideApplyBtnFlg = true;
        dealWithModelIou(model, permissionVo, entity);
        List<ApplyMatchDetail> sellList = new ArrayList<>();
        List<ApplyMatchDetail> buyList = new ArrayList<>();
        if (Objects.nonNull(entity) && entity.getId() > 0) {
            Long cooperationUserId = entity.getCooperationUserId();
            String cooperationMode = entity.getCooperationMode();
            Long createdUserId = entity.getCreatedUserId();
            sellList = applyMatchDetailClient.findByApplyQueryVo(new ApplyMatchQueryVo(entity.getId(), BasConstants.CONTRACTTYPE_SELL));
            buyList = applyMatchDetailClient.findByApplyQueryVo(new ApplyMatchQueryVo(entity.getId(), BasConstants.CONTRACTTYPE_BUY));
            if (StringUtils.equals(BasConstants.APPLY_TYPE_N, entity.getStatus())){
                if (Objects.equals(createdUserId, ShiroUtil.getCurrentUserId())) {
                    buyShowFlg = StringUtils.equals("B", cooperationMode);
                    sellShowFlg = !buyShowFlg;
                } else if (Objects.equals(cooperationUserId, ShiroUtil.getCurrentUserId())) {
                    buyShowFlg = true;
                    sellShowFlg = true;
                    hideApplyBtnFlg = false;
                }
            }
        } else {
            buyShowFlg = false;
            sellShowFlg = false;
            ApplyMatchDetail matchDetail = new ApplyMatchDetail();
            matchDetail.setPayRate(new BigDecimal("0.0000"));
            matchDetail.setPremium(BigDecimal.ZERO);
            matchDetail.setGrossProfit(BigDecimal.ZERO);
            sellList.add(matchDetail);
            buyList.add(matchDetail);
        }
        model.addAttribute("hideApplyBtnFlg", hideApplyBtnFlg);
        model.addAttribute("buyShowFlg", buyShowFlg);
        model.addAttribute("sellShowFlg", sellShowFlg);
        model.addAttribute("buyProductDetailList", buyList);
        model.addAttribute("sellProductDetailList", sellList);
        return "apply/match-content-cooperation";
    }

    @ModelAttribute("preload")
    public ApplyMatch getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyMatch entity = new ApplyMatch();
        entity.setId(0L);
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB);
        if (Objects.nonNull(id) && id > 0){
            entity = applyMatchCooperationClient.getEntity(id);
        }
        return entity;
    }

    /**
     * 处理返回白条model
     * @param model
     * @param entity
     * @return
     */
    private Model dealWithModelIou(Model model,PmPermissionVo permissionVo,ApplyMatch entity) {
        Model result = dealWithModel(model, entity);
        // 处理审批中部分控件可编辑(白条)
        permissionVo = dealWithPermission(permissionVo, entity);
        result.addAttribute("psv", permissionVo);
        return result;
    }

    /**
     * 处理审批中部分控件可编辑
     * @param permissionVo
     * @param entity
     * @return
     */
    private PmPermissionVo dealWithPermission(PmPermissionVo permissionVo, ApplyMatch entity) {
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        if (!(entity.getStatus().equals(BasConstants.APPROVE_STATUS_D)
                || entity.getStatus().equals(BasConstants.APPROVE_STATUS_A))) {
            permissionVo.setHasEdit(true);
        }
        return permissionVo;
    }


    /**
     * 处理返回model
     * @param model
     * @param entity
     * @return
     */
    private Model dealWithModel(Model model,ApplyMatch entity) {
        model.addAttribute("match", entity);
        // 采购来源
        model.addAttribute("buySourceJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_SOURCE)));
        // 销售来源
        model.addAttribute("sellSourceJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELL_SOURCE)));
        // 货品树
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
        List<BasBrand> lstBrand = brandClient.findAll();
        model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
        // 包装规格
        model.addAttribute("packingSpecificaJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        // 服务费收取方式
        model.addAttribute("serviceTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYP_SERVICE_TYPE)));

        List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
        model.addAttribute("productAllJson", JsonUtil.obj2Json(productTree));
        model.addAttribute("productChildrenJson", JsonUtil.obj2Json(productTypeClient.findAll()));
        // 支付方式
        model.addAttribute("payTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        // 提货方式
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        // 销售方式
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        // 采购结算方式
        model.addAttribute("buyDeliveryModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_DELIVERYMODE)));
        // 销售结算方式
        model.addAttribute("sellDeliveryModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELL_DELIVERYMODE)));
        // 企业抬头
        model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        model.addAttribute("productDeliveryJson", JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
        // 交货时间的补充字段
        model.addAttribute("arrivalTimeExtJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
        // 质量标准
        model.addAttribute("qualityStandardJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        // 厂商
        List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
        // 合同类型
        model.addAttribute("contractTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
        // 合同属性
        model.addAttribute("contractAttr", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
        // 默认交货地址
        model.addAttribute("defaultDeliveryAddr", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULT_DELIVERYADDR)));
        // 业务类型
        model.addAttribute("business", DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, BasConstants.DICT_TYPE_BUSINESS_DC));
        // 合同状态
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("contractBondRateSellJson", JsonUtil.obj2Json(   BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DEPOSIT_PROPORTION)));
        //采购合同模板
        BsContractTemplate template = new BsContractTemplate();
        template.setEnterpriseId(ShiroUtil.getEnterpriseId());
        template.setContractType(BasConstants.CONTRACT_TYPE_B);
        List<BsContractTemplate> buyTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("buyTemplateList", JsonUtil.obj2Json(buyTemplateList));
        //销售合同模板
        template.setContractType(BasConstants.CONTRACT_TYPE_S);
        List<BsContractTemplate> sellTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        sellTemplateList = sellTemplateList.stream().filter(t -> StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT, t.getTemplateTag()) ||
                StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX, t.getTemplateTag()) ||
                StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX, t.getTemplateTag())
        ).collect(Collectors.toList());
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));
        //服务合同模板
        template.setContractType(BasConstants.CONTRACT_TYPE_F);
        List<BsContractTemplate> serviceTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("serviceTemplateList", JsonUtil.obj2Json(serviceTemplateList));
        // 定金比例
        model.addAttribute("contractBondRateJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));

        // 审批状态
        model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));

        // 代采赊销-采购-采购需方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("dcsxOurCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));

        // 代采赊销-最低毛利率
        List<BsDictData> profitDictList = bsDictClient.loadDatasByTypeCd(BasConstants.CONFIG_FLG_SWITCH, ShiroUtil.getEnterpriseId());
        BsDictData profitRateCredit = profitDictList.stream().filter(p -> StringUtils.equals(BasConstants.PROFIT_RATE_CREDIT, p.getDictCd())).findFirst().orElse(null);
        model.addAttribute("minProfitRate", Objects.nonNull(profitRateCredit) ? profitRateCredit.getDictName() : BasConstants.DEFAULT_CREDIT_PROFIT_RATE);
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DCSX_HDFK_PAY_RATE);
        model.addAttribute("defaultRate", listByCategory.get(0).getDictCd());
        model.addAttribute("cooperationModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COOPERATION_MODE)));
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        model.addAttribute("defaultDate", new Date());
        model.addAttribute("cooperationMatchUserName", "");
        return model;
    }
}
