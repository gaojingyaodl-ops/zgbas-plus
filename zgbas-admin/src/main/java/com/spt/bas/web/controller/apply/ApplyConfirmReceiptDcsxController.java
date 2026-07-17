package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.FreemarkUtils;
import com.spt.bas.web.util.GenerationUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 */
@Controller
@RequestMapping(value = "/apply/confirmReceiptDcsx")
public class ApplyConfirmReceiptDcsxController extends PageController<ApplyConfirmReceiptDcsx, BaseVo> {
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IApplyConfirmReceiptDcsxClient applyConfirmReceiptDcsxClient;
    @Autowired
    private IApplyDeliveryOutClient applyDeliveryOutClient;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBsFactoryClient factoryClient;
    @Autowired
    private IBsWarehouseClient warehouseClient;
    @Autowired
    private IStockDetailClient stockDetailClient;
    @Autowired
    private IApplyDeliveryInClient applyDeliveryInClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private IPmProcessStepClient pmProcessStepClient;
    @Autowired
    private IFileProcessRelClient fileProcessRelClient;
    @Override
    public BaseClient<ApplyConfirmReceiptDcsx> getService() {
        return applyConfirmReceiptDcsxClient;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, Model model, HttpServletRequest request,PmPermissionVo permissionVo) {
        ApplyConfirmReceiptDcsx entity = getEntity(id);
        model.addAttribute("productTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        model.addAttribute("payTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        model.addAttribute("deliveryTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        //产品类型
        List<SysDictDataSdk> list = DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT);
        model.addAttribute("productType", JsonUtil.obj2Json(list));
        model.addAttribute("entity", entity);
        model.addAttribute("productJson",
                JsonUtil.obj2Json(productTypeClient.findAll()));
        List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
        List<BsWarehouse> warehouseList = warehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
        //交货方式
        model.addAttribute("deliveryMode",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        //出库方式
        model.addAttribute("deliveryOutTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYOUT_TYPE)));
        //包装规格-全部
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));

        String processCode = request.getParameter("processCode");
        // 附件类型
        List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
        model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        //出库申请需要另外展示对应销售申请审批流程
        if (entity != null && entity.getContractId() != null) {
            CtrContract contract = ctrContractClient.getEntity(entity.getContractId());
            if (contract != null) {
                Long approveId = contract.getApproveId();
                model.addAttribute("contractApproveId", approveId);
                PmApprove approve = pmApproveClient.getEntity(approveId);
                //获取审批的步骤
                PageSearchVo queryVo = new PageSearchVo();
                queryVo.setRows(20);
                Map<String, Object> searchParams = new HashMap<>(4);
                searchParams.put("EQL_processId", approve.getProcessId());
                if (approve.getConditionId() != null && approve.getConditionId().compareTo(0L) > 0) {
                    searchParams.put("EQL_conditionId", approve.getConditionId());
                }
                searchParams.put("EQB_enableFlg", true);
                queryVo.setSort("dispOrderNo");
                queryVo.setOrder("ASC");
                queryVo.setSearchParams(searchParams);
                PageDown<PmProcessStep> stepPage = pmProcessStepClient.findPage(queryVo);
                model.addAttribute("sellStepListJson", JsonUtil.obj2Json(stepPage.getContent()));
            }
        }
        String contractApproveId = request.getParameter("contractApproveId");
        if (StringUtils.isNotEmpty(contractApproveId)) {
            model.addAttribute("contractApproveId", Long.parseLong(request.getParameter("contractApproveId")));
        }
        return "apply/confirmReceiptDcsx-content";
    }

    @ModelAttribute("preload")
    public ApplyConfirmReceiptDcsx getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                ApplyConfirmReceiptDcsx entity = new ApplyConfirmReceiptDcsx();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                return entity;
            }
        }
        return null;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyConfirmReceiptDcsxClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "findWarehouseName", method = RequestMethod.POST)
    public void findWarehouse(String warehouseName, HttpServletResponse response) {
        StockDetail stockDetail = stockDetailClient.findWarehouseName(warehouseName);
        RenderUtil.renderJson(stockDetail, response);
    }

    @RequestMapping(value = "findDeliveryInContractId", method = RequestMethod.POST)
    public void findDeliveryInContractId(Long buyContractId, HttpServletResponse response) {
        List<ApplyDeliveryIn> applyDeliveryInList = applyDeliveryInClient.findDeliveryInContractId(buyContractId);
        if (applyDeliveryInList != null && applyDeliveryInList.size() > 0) {
            RenderUtil.renderJson(applyDeliveryInList.get(0), response);
        } else {
            RenderUtil.renderFailure("fail", response);
        }

    }

    @RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
    public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Map<String, Object> map = searchVo.getSearchParams();
        map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        Page<ApplyCancelDetail> page = applyDeliveryOutClient.findPageDetail(searchVo);

        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "getSellProcessSteJson", method = RequestMethod.POST)
    public void getSellProcessSteJson(Long ctrContractId, HttpServletResponse response) {
        if (ctrContractId != null && ctrContractId != 0L) {
            CtrContract contract = ctrContractClient.getEntity(ctrContractId);
            Long approveId = contract.getApproveId();
            PmApprove approve = pmApproveClient.getEntity(approveId);
            //获取审批的步骤
            PageSearchVo queryVo = new PageSearchVo();
            queryVo.setRows(20);
            Map<String, Object> searchParams = new HashMap<>(4);
            searchParams.put("EQL_processId", approve.getProcessId());
            if (approve.getConditionId() != null && approve.getConditionId().compareTo(new Long(0)) > 0) {
                searchParams.put("EQL_conditionId", approve.getConditionId());
            }
            searchParams.put("EQB_enableFlg", true);
            queryVo.setSort("dispOrderNo");
            queryVo.setOrder("ASC");
            queryVo.setSearchParams(searchParams);
            PageDown<PmProcessStep> stepPage = pmProcessStepClient.findPage(queryVo);
            if (stepPage != null && stepPage.getContent().size() > 0) {
                RenderUtil.renderJson(stepPage.getContent(), response);
            } else {
                RenderUtil.renderFailure("fail", response);
            }
        }
    }

    /**
     * 生成收货确认单
     *
     * @param entity
     * @param response
     */
    @RequestMapping(value = "generateGoodsReceipt", method = RequestMethod.POST)
    public void generateGoodsReceipt(ApplyConfirmReceiptVo entity, HttpServletResponse response, HttpServletRequest request) throws ApplicationException {
        logger.info("ApplyConfirmReceiptVo:{}", JsonUtil.obj2Json(entity));
        List<?> list = JsonEasyUI.getInsertRecords(entity.getSubClass(), request);
        ApplyProductDetail detail = null;
        if (!list.isEmpty()) {
            detail = (ApplyProductDetail) list.get(0);
        }
        try {
            Map map = dealWithMap(detail, entity);
            String html = FreemarkUtils.getTemplate("goodsReceipt.html", map);
            // 生成并下载
            GenerationUtil.generateTranshipment(response, html,"收货确认单.png");
        } catch (Exception e) {
            logger.error("获取收货确认单模板异常", e);
        }
    }

    /**
     * 组装返回参数
     * @return
     */
    private Map dealWithMap(ApplyProductDetail detail,ApplyConfirmReceiptVo entity) {
        Map map = new HashMap();
        if (detail != null) {
            map.put("productName", detail.getProductName());
            map.put("brandNumber", detail.getBrandNumber());
            // 规格
            map.put("wrapStr", "");
            map.put("numberUnit", detail.getNumberUnit());
            map.put("dealNumber", detail.getDealNumber());
        }

        if (entity != null) {
            map.put("contractNo", entity.getContractNo());
        }
        map.put("deliveryNo", "送货单号：");
        map.put("receiverCompany", "收货单位：");
        map.put("deliveryCompany", "运输单位：");
        map.put("contacter", "联系人：");
        map.put("carNo", "车       号：");
        map.put("contactPhone", "联系电话：");
        map.put("driverName", "司机名称：");
        map.put("address", "收货地址：");
        map.put("driverPhone", "司机电话：");
        map.put("remark", "备注：");

        return map;
    }

    /** 下游销售合同 信息*/
    @RequestMapping(value = "findSellContract", method = RequestMethod.POST)
    @ResponseBody
    public CtrContract findSellContract(@RequestParam("approveId") Long approveId, HttpServletRequest request, HttpServletResponse response) {
        List<CtrContract> contractList = ctrContractClient.findByApproveId(approveId);
        if(!CollectionUtil.isNullOrEmpty(contractList)) {
            for (CtrContract contract : contractList) {
                if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S,contract.getContractType())) {
                    return contract;
                }
            }
        }
        return new CtrContract();
    }
    /**
     * 确认收货 选择出库批次页面
     *
     * @return
     */
    @RequestMapping(value = "choose", method = RequestMethod.GET)
    public String unConfirmDeliveryOutDcsxChoose(Model model, HttpServletRequest request) {
        model.addAttribute("contractId", request.getParameter("contractId"));
        model.addAttribute("waybillCode", request.getParameter("waybillCode"));
        return "apply/unConfirmDeliveryOutDcsx-choose";
    }
}
