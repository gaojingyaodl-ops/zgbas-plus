package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存采购申请
 *
 * @Author MoonLight
 * @Date 2024/8/19 16:53
 * @Version 1.0
 */
@Controller
@RequestMapping(value = "/apply/inventoryVirtual")
public class ApplyInventoryVirtualController extends PageController<ApplyInventoryVirtual, BaseVo> {
    @Resource
    private IApplyInventoryVirtualClient applyInventoryVirtualClient;
    @Resource
    private IBsProductTypeClient productTypeClient;
    @Resource
    private IBsFactoryClient factoryClient;
    @Resource
    private IBasBrandClient brandClient;
    @Resource
    private IBsContractTemplateClient bsContractTemplateClient;
    @Resource
    private IBsProductTypeClient bsProductTypeClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;


    @Override
    public BaseClient<ApplyInventoryVirtual> getService() {
        return applyInventoryVirtualClient;
    }

    @ModelAttribute("preload")
    public ApplyInventoryVirtual getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyInventoryVirtual entity = new ApplyInventoryVirtual();
        entity.setId(0L);
        if (id != null) {
            if (id == 0L) {
                entity.setStatus(BasConstants.STOCK_VIRTUAL_STATUS_N);
            } else {
                entity = getService().getEntity(id);
            }
        }
        return entity;
    }

    /**
     * 库存采购
     *
     * @param model model
     * @return 页面
     */
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String purchaseDetail(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
        ApplyInventoryVirtual entity = getEntity(id);
        dealWithModel(model);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("entity", entity);
        model.addAttribute("psv", permissionVo);
        return "virtual/apply-virtual-kc-buy";
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo,
                             HttpServletResponse response) {
        try {
            applyInventoryVirtualClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 初始化 model
     *
     * @param model model
     * @return
     */
    public Model dealWithModel(Model model) {
        // 采购类型
        model.addAttribute("virtualBuyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_VIRTUAL_BUY_TYPE)));
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
        // 合同状态
        model.addAttribute("contractStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        //采购合同模板
        BsContractTemplate template = new BsContractTemplate();
        template.setEnterpriseId(ShiroUtil.getEnterpriseId());
        template.setContractType(BasConstants.CONTRACT_TYPE_B);
        List<BsContractTemplate> buyTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
        model.addAttribute("buyTemplateList", JsonUtil.obj2Json(buyTemplateList.stream().filter(BsContractTemplate::getEnableFlg).collect(Collectors.toList())));
        // 定金比例
        model.addAttribute("contractBondRateJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
        EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        // 代采赊销-采购-采购需方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("dcsxOurCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
        // 企业抬头
        model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        return model;
    }
}
