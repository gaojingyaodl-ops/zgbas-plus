package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/apply/match")
public class ApplyMatchController extends PageController<ApplyMatch, BaseVo> {

    @Autowired
    private IApplyMatchClient applyMatchClient;
    @Autowired
    private IApplyMatchDetailClient applyMatchDetailClient;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBsFactoryClient factoryClient;
    @Autowired
    private IBsProductTypeClient bsProductTypeClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBasBrandClient brandClient;
    @Autowired
    private IBsContractTemplateClient bsContractTemplateClient;
    @Autowired
    private ICtrServiceContractClient ctrServiceContractClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IBudgetSettlementClient budgetSettlementClient;
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IStockVirtualClient stockVirtualClient;
    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IApplyMatchChainClient applyMatchChainClient;
    @Autowired
    private IBsDictClient bsDictClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Resource
    private WebParamUtils webParamUtils;

    @Override
    public BaseClient<ApplyMatch> getService() {
        return applyMatchClient;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyMatchClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "updateLiabilityFileId", method = RequestMethod.POST)
    public void updateLiabilityFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyMatchClient.updateLiabilityFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 根据供应商等级，确定配送方式（结算方式）
     * A、B 类供应商配送方式需要做判断，根据风控系统里面的供应商配送为是的才支持上家配送，否则过滤
     *
     * @param companyId 公司 id
     * @return
     */
    @PostMapping("/getDeliveryType")
    public void getDeliveryType(Long companyId, HttpServletResponse response) {
        List<Map<String, String>> result = new ArrayList<>(5);
        if (companyId == null) {
            RenderUtil.renderJson(result, response);
        }
        BsCompany company = companyClient.getEntity(companyId);
        if (company != null) {
            // 查询供应商等级对应的付款方式
            Map<String, String> deliveryMap = getDeliveryMap();
            Map<String, String> gradeAndDeliveryMap = getGradeAndDeliveryMap();
            // 封装数据
            pakageDeliveryType(company, deliveryMap, gradeAndDeliveryMap, result);
        }
        RenderUtil.renderJson(result, response);
    }

    /**
     * 封装数据
     *
     * @param keys        键
     * @param deliveryMap 提货方式 map
     * @param result      封装到 list
     */
    private void pakageGradeMap(String keys, Map<String, String> deliveryMap, List<Map<String, String>> result, Integer flag) {
        if (StringUtils.isBlank(keys)) {
            for (Map.Entry<String, String> entry : deliveryMap.entrySet()) {
                Map<String, String> map = new LinkedHashMap<>();
                if (flag != null && BasConstants.PAYMODE_XHHK.equals(entry.getKey())) {
                    continue;
                }
                map.put("dicd", entry.getKey());
                map.put("dicdName", entry.getValue());
                result.add(map);
            }
            return;
        }
        String[] split = keys.split("&");
        for (String s : split) {
            Map<String, String> map = new LinkedHashMap<>();
            if (flag != null && BasConstants.PAYMODE_XHHK.equals(s)) {
                continue;
            }
            map.put("dicd", s);
            map.put("dicdName", deliveryMap.get(s));
            result.add(map);
        }
    }

    /**
     * 封装数据
     *
     * @param company     公司相关信息
     * @param deliveryMap 提货方式 map
     * @param result      封装到 list
     */
    private void pakageDeliveryType(BsCompany company, Map<String, String> deliveryMap, Map<String, String> gradeAndDeliveryMap, List<Map<String, String>> result) {
        // 供应商等级
        String supplierGrade = company.getSupplierGrade();
        // 供应商等级为空的,返回全部付款方式
        if (StringUtils.isBlank(supplierGrade)) {
            for (Map.Entry<String, String> entry : deliveryMap.entrySet()) {
                // 查看是否支持上家配送，如果不支持上家配送，则跳过
                if ("0".equals(company.getSupplierDelivery()) && BasConstants.DICT_TYPE_DELIVERY_P1.equals(entry.getKey())) {
                    continue;
                }
                Map<String, String> map = new LinkedHashMap<>();
                map.put("dicd", entry.getKey());
                map.put("dicdName", entry.getValue());
                result.add(map);
            }
            return;
        }
        // 根据供应商等级，找到支持的配送方式
        String keys = gradeAndDeliveryMap.get(supplierGrade);
        String[] split = keys.split("&");
        for (String s : split) {
            Map<String, String> map = new LinkedHashMap<>();
            // 查看是否支持上家配送，如果不支持上家配送，则跳过
            if ("0".equals(company.getSupplierDelivery()) && BasConstants.DICT_TYPE_DELIVERY_P1.equals(s)) {
                continue;
            }
            map.put("dicd", s);
            map.put("dicdName", deliveryMap.get(s));
            result.add(map);
        }
    }

    /**
     * 供应商等级对应提货方式 map
     *
     * @return
     */
    private Map<String, String> getGradeAndDeliveryMap() {
        List<BsDictData> list = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.GRADE_ANDDELIVERY_TYPE);
        return list.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName, (a, b) -> b, LinkedHashMap::new));
    }

    /**
     * 获取提货方式 map
     *
     * @return 获取提货方式 map
     */
    private Map<String, String> getDeliveryMap() {
        List<SysDictDataSdk> list = DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY);
        return list.stream().filter(e -> !BasConstants.DICT_TYPE_BUYDELIVERY_P.equals(e.getDictCd())).collect(Collectors.toMap(SysDictDataSdk::getDictCd, SysDictDataSdk::getDictName, (a, b) -> b));
    }


    /**
     * 根据供应商等级，确定付款方式（结算方式）
     *
     * @param companyId 公司 id
     * @param flag      为 null不做操作，不为 null 时去除款到发货
     * @return
     */
    @PostMapping("/getCompanyGrade")
    public void getCompanyGrade(Long companyId, Integer flag, Integer skip, HttpServletResponse response) {

        List<Map<String, String>> result = new ArrayList<>(5);
        if (companyId == null) {
            RenderUtil.renderJson(result, response);
        }
        BsCompany company = companyClient.getEntity(companyId);
        if (company != null) {
            // 查询供应商等级对应的付款方式
            Map<String, String> deliveryModeMap = getDeliveryModeMap(skip);
            Map<String, String> payAndDeliveryMap = getPayAndDeliveryMap();
            // 供应商等级
            String supplierGrade = company.getSupplierGrade();
            // 封装数据
            pakageGradeMap(payAndDeliveryMap.getOrDefault(supplierGrade, null), deliveryModeMap, result, flag);
        }
        RenderUtil.renderJson(result, response);
    }

    /**
     * 查询付款方式数据字典
     * 例如：
     * key = XHHK
     * value = 货到付款
     *
     * @return 付款方式数据字典map
     */
    private Map<String, String> getDeliveryModeMap(Integer skip) {
        List<BsDictData> deliverymode = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.TEMPLATE_CONTENT_DELIVERYMODE);
        return deliverymode.stream()
                .filter(e -> skip == null || BasConstants.DELIVERY_MODE_XHHK.equals(e.getDictCd()) || BasConstants.DELIVERY_MODE_XKHH.equals(e.getDictCd()))
                .collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName, (a, b) -> b, LinkedHashMap::new));
    }

    /**
     * 查询供应商等级对应付款方式
     * 例如：
     * key=A
     * value=款到发货
     *
     * @return 付款方式数据字典map
     */
    private Map<String, String> getPayAndDeliveryMap() {
        List<BsDictData> payWay = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.PAYMENT_METHOD_AND_GRADE);
        return payWay.stream()
                .collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName, (a, b) -> b));
    }

    /***
     * 撮合申请单(代采)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content/{id}")
    public String content(Model model, @PathVariable("id") Long id, PmPermissionVo permissionVo, HttpServletRequest request) {
        String pid = request.getParameter("pid");
        model.addAttribute("pid", pid);
        ApplyMatch entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        // 处理返回代采model
        model = dealWithModelDc(model, permissionVo, entity);
        //采购合同模板
        BsContractTemplate template = new BsContractTemplate();
        template.setEnterpriseId(ShiroUtil.getEnterpriseId());
        template.setContractType(BasConstants.CONTRACT_TYPE_B);
        List<BsContractTemplate> buyTemplateList = bsContractTemplateClient.findByContractsell(template);
        model.addAttribute("buyTemplateList", JsonUtil.obj2Json(buyTemplateList));

        model.addAttribute("companyNameFlk", BasConstants.COMPANY_NAME_FLK);
        Long flkTemplateId = null;
        if (CollectionUtils.isNotEmpty(buyTemplateList)) {
            for (BsContractTemplate bsContractTemplate : buyTemplateList) {
                if (StringUtils.equals(BasConstants.TEMPLATETAG_BUY_FLK_DC_CONTRACT, bsContractTemplate.getTemplateTag())) {
                    flkTemplateId = bsContractTemplate.getId();
                    break;
                }
            }
        }
        model.addAttribute("flkTemplateId", flkTemplateId);

        //销售合同模板
        BsContractTemplate templates = new BsContractTemplate();
        templates.setEnterpriseId(ShiroUtil.getEnterpriseId());
        templates.setContractType(BasConstants.CONTRACT_TYPE_S);
        List<BsContractTemplate> sellTemplateList = bsContractTemplateClient.findByContractsell(templates);
        sellTemplateList = sellTemplateList.stream()
                .filter(t -> StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_CONTRACT, t.getTemplateTag())
                        || StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_CONTRACT_TEMPLATE_GXHL, t.getTemplateTag())).collect(Collectors.toList());
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));
        if (entity.getId() != 0) {
            ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
            vo.setApplyMatchId(entity.getId());
            vo.setContractType(BasConstants.CONTRACTTYPE_SELL);
            List<ApplyMatchDetail> sellList = applyMatchDetailClient.findByApplyQueryVo(vo);
            ApplyMatchDetail applyMatchDetail = sellList.get(sellList.size() - 1);
            sellList.removeAll(sellList);
            sellList.add(applyMatchDetail);
            vo.setContractType(BasConstants.CONTRACTTYPE_BUY);
            List<ApplyMatchDetail> buyList = applyMatchDetailClient.findByApplyQueryVo(vo);
            model.addAttribute("buyProductDetailList", buyList);
            model.addAttribute("sellProductDetailList", sellList);
            List<ApplyMatchChain> matchChains = applyMatchChainClient.findMatchChains(entity.getId());
            model.addAttribute("existChainsFlg", CollectionUtils.isNotEmpty(matchChains));
            BudgetSettlement settlement = null;
            if (!sellList.isEmpty() && sellList.get(0).getContractId() != null) {
                settlement = budgetSettlementClient.findBySellContractId(sellList.get(0).getContractId());
            }
            if (Objects.nonNull(entity.getStockVirtualId())){
                StockVirtual stockVirtual = stockVirtualClient.getEntity(entity.getStockVirtualId());
                if (Objects.nonNull(stockVirtual)){
                    model.addAttribute("virtualApproveId", stockVirtual.getApproveId());
                    model.addAttribute("stockVirtualNo", stockVirtual.getStockVirtualNo());
                    model.addAttribute("stockVirtualType", stockVirtual.getVirtualBuyType());
                }
            }
            model.addAttribute("settlement", settlement);
            model.addAttribute("entity", entity);
        } else {
            // 生成随机数
            model.addAttribute("randomNumber1", 0);
            model.addAttribute("randomNumber2", 1);
            model.addAttribute("entity", new ApplyMatchDetail());
            model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
            model.addAttribute("defaultDate", new Date());
            List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
            model.addAttribute("dcsxOurCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
            // 针对化工部门发起业务
            Long currentUserId = ShiroUtil.getCurrentUserId();
            List<BsDictData> hgMatchList = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
            if(CollectionUtils.isNotEmpty(hgMatchList)){
                for (BsDictData bsDictData : hgMatchList) {
                    if(bsDictData.getDictCd().equals(currentUserId.toString())){
                        model.addAttribute("hgMatchUserFlg", true);
                        List<BsDictData> hgProductSelect = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_PRODUCT_SELECT);
                        model.addAttribute("hgProductSelect", JsonUtil.obj2Json(hgProductSelect));
                        break;
                    }
                }
            }
            // 新建
            return "apply/match-content-new";
        }
        // 详情
        if (!entity.getStatus().equals(BasConstants.APPROVE_STATUS_N)) {
            boolean isFromContract = permissionVo.getIsFromContract();
            if (isFromContract) {
                return "apply/match-contract-detail";
            }
            return "apply/match-detail-new";
        }
        // 新建
        return "apply/match-detail-new";
    }

    /***
     * 撮合申请单(白条)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content2/{id}")
    public String content2(Model model, @PathVariable("id") Long id, PmPermissionVo permissionVo, HttpServletRequest request) {
        String pid = request.getParameter("pid");
        model.addAttribute("pid", pid);
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
        sellTemplateList = sellTemplateList.stream().filter(t->StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT, t.getTemplateTag()) ||
                StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX, t.getTemplateTag()) ||
                StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX, t.getTemplateTag()) ||
                StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_GXHL, t.getTemplateTag())
        ).collect(Collectors.toList());
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));


        // 详情
        if (entity.getId() != 0) {
            ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
            vo.setApplyMatchId(entity.getId());
            vo.setContractType(BasConstants.CONTRACTTYPE_SELL);
            List<ApplyMatchDetail> sellList = applyMatchDetailClient.findByApplyQueryVo(vo);
            vo.setContractType(BasConstants.CONTRACTTYPE_BUY);
            List<ApplyMatchDetail> buyList = applyMatchDetailClient.findByApplyQueryVo(vo);
            model.addAttribute("buyProductDetailList", buyList);
            model.addAttribute("sellProductDetailList", sellList);
            model.addAttribute("contractBondRateSellJson",
                    JsonUtil.obj2Json(   BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DEPOSIT_PROPORTION)));
            BudgetSettlementVo settlement = null;
            if (!sellList.isEmpty() && sellList.get(0).getContractId() != null) {
                settlement = budgetSettlementClient.findBySellContractId(sellList.get(0).getContractId());
            }
            model.addAttribute("settlement", settlement);
            boolean isFromContract = permissionVo.getIsFromContract();
            if (isFromContract) {
                CtrServiceContract serviceContract = new CtrServiceContract();
                CtrContract contract = new CtrContract();
                if (CollectionUtils.isNotEmpty(sellList)) {
                    serviceContract = ctrServiceContractClient.findByCtrContract(sellList.get(0).getContractId());
                    CtrContract sellContract = ctrContractClient.findByContractNoV2(sellList.get(0).getContractNo());
                    String sellContractFileId = StringUtils.isEmpty(sellContract.getSellContentFileId()) ? "" : fileShowUrl + "/view/show/" + sellContract.getSellContentFileId().split(",")[0];
                    String serviceContractFileId = StringUtils.isEmpty(sellContract.getServiceContentFileId()) ? "" : fileShowUrl + "/view/show/" + sellContract.getServiceContentFileId().split(",")[0];
                    model.addAttribute("sellContractFileId", sellContractFileId);
                    model.addAttribute("serviceContractFileId", serviceContractFileId);
                }
                if (CollectionUtils.isNotEmpty(buyList)) {
                    CtrContract buyContract = ctrContractClient.findByContractNoV2(buyList.get(0).getContractNo());
                    String buyContractFileId = StringUtils.isEmpty(buyContract.getBuyContentFileId()) ? "" : fileShowUrl + "/view/show/" + buyContract.getBuyContentFileId().split(",")[0];
                    model.addAttribute("buyContractFileId", buyContractFileId);
                }
                Boolean serviceFlg = false;
                if (serviceContract != null) {
                    serviceFlg = true;
                    contract = ctrContractClient.getEntity(serviceContract.getCtrContractId());
                }
                model.addAttribute("serviceContract", serviceContract);
                model.addAttribute("serviceFlg", serviceFlg);
                model.addAttribute("ctrContract", contract);
                return "apply/match-contract-detail-sx";
            }
            return "apply/match-detail-new-sx";
        }

        // 判断是否存在可选择的报价数据
        model.addAttribute("existVirtualFlg", StringUtils.isBlank(pid) &&  stockVirtualClient.existEnableVirtual());

        // 生成随机数
        model.addAttribute("randomNumber1", 0);
        model.addAttribute("randomNumber2", 1);
        model.addAttribute("entity", new ApplyMatchDetail());
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        model.addAttribute("defaultDate", new Date());
        // 新建
        return "apply/match-content-new-sx";
    }

    /***
     * 撮合申请单(托盘)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content3/{id}")
    public String content3(Model model, @PathVariable("id") Long id, PmPermissionVo permissionVo, HttpServletRequest request) {
        String pid = request.getParameter("pid");
        model.addAttribute("pid", pid);
        ApplyMatch entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        // 处理返回代采model
        model = dealWithModelTp(model, permissionVo, entity);

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
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));

        // 详情
        if (entity.getId() != 0 && !entity.getStatus().equals(BasConstants.APPROVE_STATUS_N)) {
            ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
            vo.setApplyMatchId(entity.getId());
            vo.setContractType(BasConstants.CONTRACTTYPE_SELL);
            List<ApplyMatchDetail> sellList = applyMatchDetailClient.findByApplyQueryVo(vo);
            vo.setContractType(BasConstants.CONTRACTTYPE_BUY);
            List<ApplyMatchDetail> buyList = applyMatchDetailClient.findByApplyQueryVo(vo);
            model.addAttribute("buyProductDetailList", buyList);
            model.addAttribute("sellProductDetailList", sellList);
            BudgetSettlement settlement = null;
            if (!sellList.isEmpty() && sellList.get(0).getContractId() != null) {
                settlement = budgetSettlementClient.findBySellContractId(sellList.get(0).getContractId());
            }
            model.addAttribute("settlement", settlement);
            boolean isFromContract = permissionVo.getIsFromContract();
            if (isFromContract) {
                CtrServiceContract serviceContract = new CtrServiceContract();
                CtrContract contract = new CtrContract();
                if (CollectionUtils.isNotEmpty(sellList)) {
                    serviceContract = ctrServiceContractClient.findByCtrContract(sellList.get(0).getContractId());
                }
                Boolean serviceFlg = false;
                if (serviceContract != null) {
                    serviceFlg = true;
                    contract = ctrContractClient.getEntity(serviceContract.getCtrContractId());
                }
                model.addAttribute("serviceContract", serviceContract);
                model.addAttribute("serviceFlg", serviceFlg);
                model.addAttribute("ctrContract", contract);
                return "apply/match-contract-detail-tp";
            }
            return "apply/match-detail-new-tp";
        }
        // 生成随机数
        model.addAttribute("randomNumber1", 0);
        model.addAttribute("randomNumber2", 1);
        model.addAttribute("entity", new ApplyMatchDetail());
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        model.addAttribute("defaultDate", new Date());

        return "apply/match-content-new-tp";
    }

    /***
     * 撮合申请单(托盘 新版本)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content4/{id}")
    public String content4(Model model, @PathVariable("id") Long id, PmPermissionVo permissionVo, HttpServletRequest request) {
        String pid = request.getParameter("pid");
        model.addAttribute("pid", pid);
        ApplyMatch entity = getEntity1(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        // 处理返回代采model
        model = dealWithModelDc(model, permissionVo, entity);
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
        sellTemplateList = sellTemplateList.stream().filter(t->StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX, t.getTemplateTag())).collect(Collectors.toList());
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));
        if (entity.getId() != 0) {
            ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
            vo.setApplyMatchId(entity.getId());
            vo.setContractType(BasConstants.CONTRACTTYPE_SELL);
            List<ApplyMatchDetail> sellList = applyMatchDetailClient.findByApplyQueryVo(vo);
            ApplyMatchDetail applyMatchDetail = sellList.get(sellList.size() - 1);
            sellList.removeAll(sellList);
            sellList.add(applyMatchDetail);
            vo.setContractType(BasConstants.CONTRACTTYPE_BUY);
            List<ApplyMatchDetail> buyList = applyMatchDetailClient.findByApplyQueryVo(vo);
            model.addAttribute("buyProductDetailList", buyList);
            model.addAttribute("sellProductDetailList", sellList);
            List<ApplyMatchChain> matchChains = applyMatchChainClient.findMatchChains(entity.getId());
            model.addAttribute("existChainsFlg", CollectionUtils.isNotEmpty(matchChains));
            BudgetSettlement settlement = null;
            if (!sellList.isEmpty() && sellList.get(0).getContractId() != null) {
                settlement = budgetSettlementClient.findBySellContractId(sellList.get(0).getContractId());
            }
            model.addAttribute("settlement", settlement);
            boolean isFromContract = permissionVo.getIsFromContract();
            if (isFromContract) {
                CtrServiceContract serviceContract = new CtrServiceContract();
                CtrContract contract = new CtrContract();
                if (CollectionUtils.isNotEmpty(sellList)) {
                    serviceContract = ctrServiceContractClient.findByCtrContract(sellList.get(0).getContractId());
                }
                Boolean serviceFlg = false;
                if (serviceContract != null) {
                    serviceFlg = true;
                    contract = ctrContractClient.getEntity(serviceContract.getCtrContractId());
                }
                model.addAttribute("serviceContract", serviceContract);
                model.addAttribute("serviceFlg", serviceFlg);
                model.addAttribute("ctrContract", contract);
            }

            model.addAttribute("entity", entity);
        } else {
            // 生成随机数
            model.addAttribute("randomNumber1", 0);
            model.addAttribute("randomNumber2", 1);
            model.addAttribute("entity", new ApplyMatchDetail());
            model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
            model.addAttribute("defaultDate", new Date());
            List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
            model.addAttribute("dcsxOurCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
            // 新建
            return "apply/match-content-tp-new";
        }
//        // 详情
//        if (!entity.getStatus().equals(BasConstants.APPROVE_STATUS_N)) {
//            boolean isFromContract = permissionVo.getIsFromContract();
//            if (isFromContract) {
//                return "apply/match-contract-tp-detail";
//            }
//            return "apply/match-detail-tp-new";
//        }
        // 新建
        return "apply/match-detail-tp-new";
    }

    /**
     * 处理返回代采model
     *
     * @param model
     * @param entity
     * @return
     */
    private Model dealWithModelDc(Model model, PmPermissionVo permissionVo, ApplyMatch entity) {
        Model result = dealWithModel(model, entity);
        // 处理审批中部分控件可编辑(代采)
        permissionVo = dealWithPermissionDc(permissionVo, entity);
        result.addAttribute("psv", permissionVo);
        // 代采-最低毛利率
        List<BsDictData> profitDictList = bsDictClient.loadDatasByTypeCd(BasConstants.CONFIG_FLG_SWITCH, ShiroUtil.getEnterpriseId());
        BsDictData profitRateCredit = profitDictList.stream().filter(p -> StringUtils.equals(BasConstants.PROFIT_RATE, p.getDictCd())).findFirst().orElse(null);
        result.addAttribute("minProfitRate", Objects.nonNull(profitRateCredit) ? profitRateCredit.getDictName() : BasConstants.DEFAULT_PROFIT_RATE);
        return result;
    }

    /**
     * 处理返回代采model(托盘业务)
     *
     * @param model
     * @param entity
     * @return
     */
    private Model dealWithModelTp(Model model, PmPermissionVo permissionVo, ApplyMatch entity) {
        Model result = dealWithModel(model, entity);

        List<BasBrand> lstBrand = brandClient.findSafeBrand();
        model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
        // 安全仓库
        model.addAttribute("safeWarehouse", JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_SAFEWAREHOUSE)));

        // 处理审批中部分控件可编辑(托盘)
        permissionVo = dealWithPermission(permissionVo, entity);
        result.addAttribute("psv", permissionVo);
        return result;
    }

    /**
     * 处理返回白条model
     *
     * @param model
     * @param entity
     * @return
     */
    private Model dealWithModelIou(Model model, PmPermissionVo permissionVo, ApplyMatch entity) {
        Model result = dealWithModel(model, entity);
        // 处理审批中部分控件可编辑(白条)
        permissionVo = dealWithPermissionIou(permissionVo, entity);
        result.addAttribute("psv", permissionVo);
        // 赊销-最低毛利率
        List<BsDictData> profitDictList = bsDictClient.loadDatasByTypeCd(BasConstants.CONFIG_FLG_SWITCH, ShiroUtil.getEnterpriseId());
        BsDictData profitRateCredit = profitDictList.stream().filter(p -> StringUtils.equals(BasConstants.PROFIT_RATE_CREDIT, p.getDictCd())).findFirst().orElse(null);
        result.addAttribute("minProfitRate", Objects.nonNull(profitRateCredit) ? profitRateCredit.getDictName() : BasConstants.DEFAULT_CREDIT_PROFIT_RATE);
        return result;
    }

    /**
     * 处理审批中部分控件可编辑(代采)
     *
     * @param permissionVo
     * @param entity
     * @return
     */
    private PmPermissionVo dealWithPermissionDc(PmPermissionVo permissionVo, ApplyMatch entity) {
        PmPermissionVo result = dealWithPermission(permissionVo, entity);
        return result;
    }


    /**
     * 处理审批中部分控件可编辑(白条)
     *
     * @param permissionVo
     * @param entity
     * @return
     */
    private PmPermissionVo dealWithPermissionIou(PmPermissionVo permissionVo, ApplyMatch entity) {
        PmPermissionVo result = dealWithPermission(permissionVo, entity);
        return result;
    }

    /**
     * 处理审批中部分控件可编辑
     *
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
     *
     * @param model
     * @param entity
     * @return
     */
    private Model dealWithModel(Model model, ApplyMatch entity) {
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
        String businessType = entity.getBusinessType();
        model.addAttribute("business", getBusiness(businessType));
        // 合同状态
        model.addAttribute("contractStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        // 采购付款方式
        List<ApplySellPayModeVo> buyModeList = getPayModeB();
        if (!buyModeList.isEmpty()) {
            model.addAttribute("buyPayModeJson", JsonUtil.obj2Json(buyModeList));
        }
        // 销售付款方式
        List<ApplySellPayModeVo> sellModeList = getPayModeS();
        if (!sellModeList.isEmpty()) {
            model.addAttribute("sellPayModeJson", JsonUtil.obj2Json(sellModeList));
        }
        //采购合同模板
        BsContractTemplate template = new BsContractTemplate();
        template.setEnterpriseId(ShiroUtil.getEnterpriseId());
        template.setContractType(BasConstants.CONTRACT_TYPE_B);
        List<BsContractTemplate> buyTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("buyTemplateList", JsonUtil.obj2Json(buyTemplateList));
        //销售合同模板
        template.setContractType(BasConstants.CONTRACT_TYPE_S);
        List<BsContractTemplate> sellTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));
        //服务合同模板
        template.setContractType(BasConstants.CONTRACT_TYPE_F);
        List<BsContractTemplate> serviceTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("serviceTemplateList", JsonUtil.obj2Json(serviceTemplateList));
        // 定金比例
        model.addAttribute("contractBondRateJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
        model.addAttribute("contractBondRateSellJson",
                JsonUtil.obj2Json(   BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DEPOSIT_PROPORTION)));
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.HDFK_PAY_RATE);
        if(listByCategory.size()>0){
            model.addAttribute("defaultRate", listByCategory.get(0).getDictCd());
        }
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));

        // 审批状态
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        return model;
    }

    /***
     * 增加采购 销售 输入域
     *
     * @return
     */
    @RequestMapping(value = "addProductDetail")
    public String addProductDetail(Model model, String contractType, Integer curNumber) {
        // 生成一个随机数
        model.addAttribute("randomNumber", curNumber);
        String url;
        if (BasConstants.CONTRACTTYPE_SELL.equals(contractType)) {
            url = "apply/match_sell";
        } else {
            url = "apply/match_buy";
        }
        model.addAttribute("tag", BasConstants.APPLY_TYPE_M);
        model.addAttribute("contractType", contractType);
        model.addAttribute("defaultDate", new Date());
        return url;
    }

    @RequestMapping(value = "print/{id}", method = RequestMethod.GET)
    public String print(@PathVariable(value = "id") Long id, Model model) {
        ApproveMatchFormPrintVo vo = applyMatchClient.printApplyMatch(id);
        model.addAttribute("printContext", vo.getContent());
        return "apply/applyMatchPrint";
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
            } else if(id == 2L) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_TP);
            } else {
                entity = getService().getEntity(id);
            }
        }
        return entity;
    }

    @ModelAttribute("preload")
    public ApplyMatch getEntity1(@RequestParam(value = "id", required = false) Long id) {
        ApplyMatch entity = new ApplyMatch();
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_TP);
        if (id != null) {
            if (id == 0L) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_TP);
            } else {
                entity = getService().getEntity(id);
            }
        }
        return entity;
    }

    private String getBusiness(String businessType) {
        String business = DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, BasConstants.DICT_TYPE_BUSINESS_DC);
        return business;
    }

    private List<ApplySellPayModeVo> getPayModeB() {
        List<ApplySellPayModeVo> modeList = new ArrayList<>();
        List<BsDictData> bsDictData = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),
                BasConstants.BSDICT_BUY_PAYMODE);
        if (!bsDictData.isEmpty()) {
            String[] serialNumber = {BasConstants.PAYMODE_XHHK, BasConstants.PAYMODE_QKZF, BasConstants.PAYMODE_DJZF};
            for (int i = 0; i < bsDictData.size(); i++) {
                String mode = "";
                String dictName = bsDictData.get(i).getDictName();
                if (i == 0) {
                    mode = MessageFormat.format(dictName, new Object[]{"()"});
                } else if (i == 1) {
                    mode = MessageFormat.format(dictName, new Object[]{dataFormat(), "()"});
                } else if (i == 2) {
                    mode = MessageFormat.format(dictName, new Object[]{dataFormat(), "****", "()"});
                }
                ApplySellPayModeVo modeVo = new ApplySellPayModeVo(serialNumber[i], mode);
                modeList.add(modeVo);
            }
        }
        return modeList;
    }

    private List<ApplySellPayModeVo> getPayModeS() {
        List<ApplySellPayModeVo> modeList = new ArrayList<>();
        List<BsDictData> bsDictData = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),
                BasConstants.BSDICT_MATCH_PAYMODE);
        if (!bsDictData.isEmpty()) {
            String[] serialNumber = {BasConstants.PAYMODE_QKZF, BasConstants.PAYMODE_HDFK};
            for (int i = 0; i < bsDictData.size(); i++) {
                String mode = "";
                String dictName = bsDictData.get(i).getDictName();
                if (i == 0) {
                    mode = MessageFormat.format(dictName, new Object[]{dataFormat(), "()"});
                } else {
                    mode = MessageFormat.format(dictName, new Object[]{dataFormat(), "****", "()"});
                }
                ApplySellPayModeVo modeVo = new ApplySellPayModeVo(serialNumber[i], mode);
                modeList.add(modeVo);
            }
        }
        return modeList;
    }

    private static String dataFormat() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }
}
