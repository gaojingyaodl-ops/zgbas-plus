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
import com.spt.bas.web.util.DateUtils;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.exception.ApplicationException;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  sr
 * 代采赊销
 */
@Controller
@RequestMapping(value = "/apply/chargeSales")
public class ApplyChargeSalesController extends PageController<ApplyMatch, BaseVo> {
    @Autowired
    private IApplyChargeSalesClient applyChargeSalesClient;
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
    private IApplyMatchDetailClient applyMatchDetailClient;
    @Autowired
    private IBudgetSettlementClient budgetSettlementClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IBasBrandClient brandClient;
    @Autowired
    private IApplyMatchChainClient applyMatchChainClient;
    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IBsDictClient bsDictClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsCompanyClient bsCompanyClient;
    @Resource
    private IStockVirtualClient stockVirtualClient;
    @Autowired
    private IBsCompanyCreditClient bsCompanyCreditClient;

    /***
     * 撮合申请单(白条)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content/{id}")
    public String content2(Model model, @PathVariable("id") Long id, PmPermissionVo permissionVo,HttpServletRequest request) {
        String pid = request.getParameter("pid");
        model.addAttribute("pid",pid);
        ApplyMatch entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        // 处理返回白条model
        model = dealWithModelIou(model, permissionVo, entity);
        //采购合同模板
        BsContractTemplate template = new BsContractTemplate();
        template.setEnterpriseId(ShiroUtil.getEnterpriseId());
        template.setContractType(BasConstants.CONTRACT_TYPE_B);
        List<BsContractTemplate> buyTemplateList = bsContractTemplateClient.findByContractsell(template);
        model.addAttribute("buyTemplateList", JsonUtil.obj2Json(buyTemplateList));

        //销售合同模板
        BsContractTemplate templates = new BsContractTemplate();
        templates.setEnterpriseId(ShiroUtil.getEnterpriseId());
        templates.setContractType(BasConstants.CONTRACT_TYPE_S);
        List<BsContractTemplate> sellTemplateList = bsContractTemplateClient.findByContractsell(templates);
        sellTemplateList = sellTemplateList.stream()
                .filter(t -> StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT, t.getTemplateTag()) ||
                        StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX, t.getTemplateTag()) ||
                        StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX, t.getTemplateTag()) ||
                        StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_GXHL, t.getTemplateTag())
        ).collect(Collectors.toList());
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));
        model.addAttribute("companyNameSDNH", BasConstants.COMPANY_NAME_SDNH);
        model.addAttribute("companyNameSUGX", BasConstants.COMPANY_NAME_SUGX);

        // 详情
        if (entity.getId() != 0) {
            ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
            vo.setApplyMatchId(entity.getId());
            vo.setContractType(BasConstants.CONTRACTTYPE_SELL);
            List<ApplyMatchDetail> sellList = applyMatchDetailClient.findByApplyQueryVo(vo);
            vo.setContractType(BasConstants.CONTRACTTYPE_BUY);
            List<ApplyMatchDetail> buyList = applyMatchDetailClient.findByApplyQueryVo(vo);
            // 资金方特殊链条查看处理标识
            webParamUtils.dealWithSpecialFundView(model, entity, buyList);
            List<ApplyMatchChain> matchChains = applyMatchChainClient.findMatchChains(entity.getId());
            model.addAttribute("existChainsFlg", CollectionUtils.isNotEmpty(matchChains));
            model.addAttribute("buyProductDetailList", buyList);
            model.addAttribute("sellProductDetailList", sellList);
            model.addAttribute("contractBondRateSellJson",
                    JsonUtil.obj2Json(   BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DEPOSIT_PROPORTION)));
            if (Objects.nonNull(entity.getStockVirtualId())){
                StockVirtual stockVirtual = stockVirtualClient.getEntity(entity.getStockVirtualId());
                if (Objects.nonNull(stockVirtual)){
                    model.addAttribute("virtualApproveId", stockVirtual.getApproveId());
                    model.addAttribute("stockVirtualNo", stockVirtual.getStockVirtualNo());
                    model.addAttribute("stockVirtualType", stockVirtual.getVirtualBuyType());
                }
            }
            BudgetSettlementVo settlement = null;
            if (!sellList.isEmpty() && sellList.get(0).getContractId() != null) {
                settlement = budgetSettlementClient.findBySellContractId(sellList.get(0).getContractId());
            }
            model.addAttribute("settlement", settlement);
            boolean isFromContract = permissionVo.getIsFromContract();
            if (isFromContract) {
                CtrContract contract = new CtrContract();
                if (CollectionUtils.isNotEmpty(sellList)){
                    CtrContract sellContract = ctrContractClient.findByContractNoV2(sellList.get(0).getContractNo());
                    String sellContractFileId = StringUtils.isEmpty(sellContract.getSellContentFileId()) ? "" : fileShowUrl + "/view/show/"+sellContract.getSellContentFileId().split(",")[0];
                    String serviceContractFileId = StringUtils.isEmpty(sellContract.getServiceContentFileId()) ? "" : fileShowUrl + "/view/show/"+sellContract.getServiceContentFileId().split(",")[0];
                    model.addAttribute("sellContractFileId", sellContractFileId);
                    model.addAttribute("serviceContractFileId", serviceContractFileId);

                }
                if (CollectionUtils.isNotEmpty(buyList)){
                    CtrContract buyContract = ctrContractClient.findByContractNoV2(buyList.get(0).getContractNo());
                    String buyContractFileId = StringUtils.isEmpty(buyContract.getBuyContentFileId()) ? "" : fileShowUrl + "/view/show/"+buyContract.getBuyContentFileId().split(",")[0];
                    model.addAttribute("buyContractFileId", buyContractFileId);
                }
                model.addAttribute("ctrContract",contract);
                return "apply/match-contract-detail-sx";
            }
            return "apply/match-detail-new-dcsx";
        }
        // 判断是否存在可选择的报价数据
//        model.addAttribute("existVirtualFlg", StringUtils.isBlank(pid) && stockVirtualClient.existEnableVirtual());

        // 生成随机数
        model.addAttribute("randomNumber1", 0);
        model.addAttribute("randomNumber2", 1);
        model.addAttribute("entity", new ApplyMatchDetail());
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        model.addAttribute("defaultDate", new Date());
        return "apply/match-content-new-dcsx";
    }

    @RequestMapping(value = "getDaDiContent/{sellCompanyId}", method = RequestMethod.GET)
    public String getDaDiContent(@PathVariable("sellCompanyId") Long sellCompanyId, Model model) {

        BsCompany company = bsCompanyClient.findCompany(sellCompanyId);
        model.addAttribute("daDiCreditAmount", company.getDaDiCreditAmount());
        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("usedCreditAmount", company.getUsedCreditAmount());
        
        return "bs/dadiContent";
    }

    @RequestMapping(value = "getSugxContent/{sellCompanyId}", method = RequestMethod.GET)
    public String getSugxContent(@PathVariable("sellCompanyId") Long sellCompanyId, Model model) {

        BsCompany company = bsCompanyClient.findCompany(sellCompanyId);
        BigDecimal totalCreditAmount = company.getTotalCreditAmount();
        BigDecimal usedCreditAmount = company.getUsedCreditAmount();
        model.addAttribute("totalCreditAmount", totalCreditAmount);
        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("usedCreditAmount", usedCreditAmount);
        BigDecimal subtract = totalCreditAmount.subtract(usedCreditAmount);
        model.addAttribute("availableCreditAmount", totalCreditAmount.compareTo(BigDecimal.ZERO) > 0?subtract:BigDecimal.ZERO.setScale(3,BigDecimal.ROUND_HALF_UP) );

        return "bs/sugxContent";
    }

    @ModelAttribute("preload")
    public ApplyMatch getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyMatch entity = new ApplyMatch();
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB);
        if (id != null) {
            if (id == 0L) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB);
            } else if (id == 1L) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_DL_DC);
            } else {
                entity = getService().getEntity(id);
            }
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
        permissionVo = dealWithPermissionIou(permissionVo, entity);
        result.addAttribute("psv", permissionVo);
        return result;
    }

    /**
     * 处理审批中部分控件可编辑(白条)
     * @param permissionVo
     * @param entity
     * @return
     */
    private PmPermissionVo dealWithPermissionIou(PmPermissionVo permissionVo,ApplyMatch entity) {
        return dealWithPermission(permissionVo, entity);
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
        model.addAttribute("productTypeJson",
                JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
        List<BasBrand> lstBrand = brandClient.findAll();
        model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
        // 包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        // 服务费收取方式
        model.addAttribute("serviceTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYP_SERVICE_TYPE)));

        List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
        model.addAttribute("productAllJson", JsonUtil.obj2Json(productTree));
        model.addAttribute("productChildrenJson", JsonUtil.obj2Json(productTypeClient.findAll()));
        // 支付方式
        model.addAttribute("payTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        // 提货方式
        model.addAttribute("deliveryTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        // 销售方式
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
                BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        // 采购结算方式
        model.addAttribute("buyDeliveryModeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_DELIVERYMODE)));
        // 销售结算方式
        model.addAttribute("sellDeliveryModeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELL_DELIVERYMODE)));
        // 企业抬头
        model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        model.addAttribute("productDeliveryJson",
                JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
        // 交货时间的补充字段
        model.addAttribute("arrivalTimeExtJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
        // 质量标准
        model.addAttribute("qualityStandardJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        // 厂商
        List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
        // 合同类型
        model.addAttribute("contractTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
        // 合同属性
        model.addAttribute("contractAttr",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
        // 默认交货地址
        model.addAttribute("defaultDeliveryAddr", JsonUtil.obj2Json(BsDictUtil
                .getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULT_DELIVERYADDR)));
        // 业务类型
        model.addAttribute("business", DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, BasConstants.DICT_TYPE_BUSINESS_DC));
        // 合同状态
        model.addAttribute("contractStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("contractBondRateSellJson",
                JsonUtil.obj2Json(   BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DEPOSIT_PROPORTION)));
        //采购合同模板
        BsContractTemplate template = new BsContractTemplate();
        template.setEnterpriseId(ShiroUtil.getEnterpriseId());
        template.setContractType(BasConstants.CONTRACT_TYPE_B);
        List<BsContractTemplate> buyTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("buyTemplateList", JsonUtil.obj2Json(buyTemplateList));
        //销售合同模板
        template.setContractType(BasConstants.CONTRACT_TYPE_S);
        List<BsContractTemplate> sellTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        sellTemplateList = sellTemplateList.stream().filter(t->StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT, t.getTemplateTag()) ||
                StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX, t.getTemplateTag()) ||
                StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX, t.getTemplateTag())
        ).collect(Collectors.toList());
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));
        //服务合同模板
        template.setContractType(BasConstants.CONTRACT_TYPE_F);
        List<BsContractTemplate> serviceTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("serviceTemplateList", JsonUtil.obj2Json(serviceTemplateList));
        // 定金比例
        model.addAttribute("contractBondRateJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));

        // 审批状态
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));

        // 代采赊销-采购-采购需方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("dcsxOurCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));

        // 代采赊销-最低毛利率
        List<BsDictData> profitDictList = bsDictClient.loadDatasByTypeCd(BasConstants.CONFIG_FLG_SWITCH, ShiroUtil.getEnterpriseId());
        BsDictData profitRateCredit = profitDictList.stream().filter(p -> StringUtils.equals(BasConstants.PROFIT_RATE_CREDIT, p.getDictCd())).findFirst().orElse(null);
        model.addAttribute("minProfitRate", Objects.nonNull(profitRateCredit) ? profitRateCredit.getDictName() : BasConstants.DEFAULT_CREDIT_PROFIT_RATE);
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DCSX_HDFK_PAY_RATE);
        model.addAttribute("defaultRate", listByCategory.get(0).getDictCd());
        return model;
    }

    @Override
    public BaseClient<ApplyMatch> getService() {
        return applyChargeSalesClient;
    }

    /**
     * 预算申请，点击客户获取对应使用的授信信息
     * @param companyCreditId
     * @param companyId
     * @param model
     * @return
     */
    @RequestMapping(value = "getCompanyCredit", method = RequestMethod.GET)
    public String getCompanyCredit(@RequestParam(value = "companyCreditId",required = false) String companyCreditId,@RequestParam("companyId") String companyId, Model model) throws ApplicationException {
        // companyCreditId 存在就获取授信表里面的数据，不存在就依次人保、大地、自主的数据，那个先存在返回那个
        BsCompanyCredit bsCompanyCredit = null;
        if(Objects.nonNull(companyCreditId)){
            bsCompanyCredit =bsCompanyCreditClient.getEntity(Long.valueOf(companyCreditId));
        } else {
            List<BsDictData> creditTypeList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_CREDIT_TYPE);
            List<String> creditTypes = creditTypeList.stream().map(BsDictData::getDictCd).sorted().collect(Collectors.toList());
            for (String creditType : creditTypes) {
                BsCompanyCredit entity = bsCompanyCreditClient.findByCompanyIdAndType(Long.valueOf(companyId),creditType);
                if(Objects.nonNull(entity)){
                    bsCompanyCredit = entity;
                    break;
                }
            }
        }
        BsCompany company = bsCompanyClient.findCompany(Long.valueOf(companyId));
        if(Objects.isNull(bsCompanyCredit)){
            throw new ApplicationException("无法获取，"+company.getCompanyName()+"的授信信息");
        }
        BigDecimal riskAmount = bsCompanyCredit.getRiskAmount();
        BigDecimal usedCreditAmount = bsCompanyCredit.getUsedCreditAmount();
        BigDecimal temporaryAmount = bsCompanyCredit.getTemporaryAmount();
        model.addAttribute("creditAmount", bsCompanyCredit.getCreditAmount());
        model.addAttribute("riskAmount", riskAmount);
        model.addAttribute("usedCreditAmount", usedCreditAmount);
        model.addAttribute("temporaryAmount", temporaryAmount);
        if(bsCompanyCredit.getTemporaryExpiryDate()!=null){
            model.addAttribute("temporaryExpiryDate", DateUtils.parseDateToStr("yyyy-MM-dd",bsCompanyCredit.getTemporaryExpiryDate()));
        }
        BigDecimal availableCreditAmount = (riskAmount != null ? riskAmount : BigDecimal.ZERO)
                .add(temporaryAmount != null ? temporaryAmount : BigDecimal.ZERO)
                .subtract(usedCreditAmount != null ? usedCreditAmount : BigDecimal.ZERO);
        availableCreditAmount = availableCreditAmount.setScale(3, RoundingMode.HALF_UP);
        model.addAttribute("availableCreditAmount", availableCreditAmount);
        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("creditType", bsCompanyCredit.getCreditType());
        model.addAttribute("creditTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));
        return "bs/creditContent";
    }
}
