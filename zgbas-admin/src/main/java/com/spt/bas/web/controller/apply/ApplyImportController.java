package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyImport;
import com.spt.bas.client.entity.ApplyImportDetail;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.bas.client.vo.ApplyImportQueryVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("apply/import")
public class ApplyImportController extends PageController<ApplyImport, BaseVo> {

    @Autowired
    private IApplyImportClient applyImportClient;
    @Autowired
    private IApplyImportDetailClient applyImportDetailClient;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBsFactoryClient factoryClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private IBsProductTypeClient bsProductTypeClient;
    @Autowired
    private IPmApproveStepClient pmApproveStepClient;
    @Autowired
    private IPmApplySetClient pmApplySetClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IApplyProductDetailClient productDetailClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content/{id}")
    public String content(Model model, @PathVariable("id") Long id, PmPermissionVo permissionVo) {
        ApplyImport entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        model.addAttribute("import", entity);
        //货品树
        List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
        model.addAttribute("productAllJson", JsonUtil.obj2Json(productTree));
        model.addAttribute("productChildrenJson",
                JsonUtil.obj2Json(productTypeClient.findAll()));
        //支付方式
        model.addAttribute("payTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        //提货方式
        model.addAttribute("deliveryTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        //销售方式
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
                BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));

        //企业抬头
        model.addAttribute("ourCompanyNameJson",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        //厂商
        List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
        //合同类型
        model.addAttribute("contractTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
        //合同属性
        model.addAttribute("contractAttr",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
        //业务类型
        String businessType = entity.getBusinessType();
        model.addAttribute("business", getBusiness(businessType));
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);

        //详情
        if (entity.getId() != 0) {
            if (!entity.getStatus().equals(BasConstants.APPROVE_STATUS_N) || entity.getId() != 0) {
                ApplyImportQueryVo vo = new ApplyImportQueryVo();
                vo.setApplyImportId(entity.getId());
                List<ApplyImportDetail> list = applyImportDetailClient.findByApplyImportId(vo);
                String deliveryMode = "";
                String deliveryType = "";
                if (!list.isEmpty()) {
                    deliveryMode = list.get(0).getDeliveryMode();
                    deliveryType = list.get(0).getDeliveryType();
                }
                model.addAttribute("deliveryMode", deliveryMode);
                model.addAttribute("deliveryType", deliveryType);
                model.addAttribute("productDetailList", list);
                return "apply/import-detail";

            }

        }
        //生成随机数
        model.addAttribute("randomNumber1", 0);
        model.addAttribute("randomNumber2", 1);
        model.addAttribute("entity", new ApplyImportDetail());
        model.addAttribute("currentUserId", ShiroUtil.getCurrentUserId());
        model.addAttribute("defaultDate", new Date());
        return "apply/import-content";
    }


    @RequestMapping(value = "addProductDetail")
    public String addProductDetail(Model model, String contractType, Integer curNumber) {
        model.addAttribute("randomNumber", curNumber);
        String url;
        if (contractType.equals("S")) {
            url = "apply/import_sell";
        } else {
            url = "apply/import_buy";
        }
        model.addAttribute("tag", BasConstants.APPLY_TYPE_R);
        model.addAttribute("contractType", contractType);
        model.addAttribute("defaultDate", new Date());
        return url;
    }
    @RequestMapping(value = "addProtocolDocCkhDetail")
    public String addProtocolDocCkhDetail(Model model, String contractType, Integer curNumber) {
        model.addAttribute("randomNumber", curNumber);
//        String url;
//        url = "apply/import_ckh";
//        if (contractType.equals("S")) {
//        } else {
//            url = "apply/import_buy";
//        }
        model.addAttribute("tag", BasConstants.APPLY_TYPE_R);
        model.addAttribute("contractType", contractType);
        model.addAttribute("defaultDate", new Date());
        return "apply/import-ckh";
    }


    @ModelAttribute("preload")
    public ApplyImport getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                ApplyImport entity = new ApplyImport();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_DL_KZ);
                return entity;
            }
        }
        return null;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo,
                             HttpServletResponse response) {
        try {
            applyImportClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @Override
    public BaseClient<ApplyImport> getService() {
        return applyImportClient;
    }

    /**
     * 进口代理申请单打印
     */
    @RequestMapping(value = "print/{id}", method = RequestMethod.GET)
    public String print(@PathVariable(value = "id") Long id, Model model) {
        ApplyImport entity = getEntity(id);
        model.addAttribute("entity", entity);
        PmApprove approve = pmApproveClient.getEntity(entity.getApproveId());
        model.addAttribute("approveNo", approve.getApproveNo());
        model.addAttribute("createUserName", approve.getCreateUserName());
        ApplyImportQueryVo vo = new ApplyImportQueryVo();
        vo.setApplyImportId(entity.getId());
        List<ApplyImportDetail> list = applyImportDetailClient.findByApplyImportId(vo);
        List<ApplyProductDetail> sellList = new ArrayList<ApplyProductDetail>();
        List<ApplyProductDetail> buyList = new ArrayList<ApplyProductDetail>();
        boolean buyEntityFlg = false;
        BigDecimal selltotalAmount = BigDecimal.ZERO;//销售合同总价
        BigDecimal buytotalAmount = BigDecimal.ZERO;//采购合同总价
        for (ApplyImportDetail applyImport : list) {
            //查询明细
            ApplyDeliveryApplyIdVo applyVo = new ApplyDeliveryApplyIdVo();
            applyVo.setApplyId(applyImport.getId());
            applyVo.setApplyType(BasConstants.APPLY_TYPE_R);
            List<ApplyProductDetail> detailList = productDetailClient.findApplyId(applyVo);
            for (ApplyProductDetail detail : detailList) {
                if (applyImport.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                    sellList.add(detail);
                    selltotalAmount = selltotalAmount.add(detail.getTotalPrice());
                } else {
                    buyList.add(detail);
                    buytotalAmount = buytotalAmount.add(detail.getTotalPrice());
                }
            }
            if (applyImport.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                model.addAttribute("sell", applyImport);
                //model.addAttribute("sellContractId",match.getContractNo());
            } else {
                if (!buyEntityFlg) {
                    model.addAttribute("buy", applyImport);
                    //model.addAttribute("buyContractId",match.getContractNo());
                    buyEntityFlg = true;
                }
            }

        }
        model.addAttribute("buytotalAmount", buytotalAmount);
        model.addAttribute("selltotalAmount", selltotalAmount);
        model.addAttribute("sellList", JsonUtil.obj2Json(sellList));
        model.addAttribute("buyList", JsonUtil.obj2Json(buyList));
        model.addAttribute("deliveryTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("contractAttr",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
        return "apply/applyImportPrint";
    }

    private String getBusiness(String businessType) {
        String business = "";
        if (businessType != null) {
            String value = businessType.split("-")[0];
            business = DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, value);
        }
        return business;
    }
}
