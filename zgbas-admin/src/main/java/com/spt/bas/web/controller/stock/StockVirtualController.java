package com.spt.bas.web.controller.stock;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.entity.StockVirtual;
import com.spt.bas.client.remote.*;
import com.spt.bas.report.client.remote.IRptStockVirtualReportClient;
import com.spt.bas.report.client.vo.RptStockVirtualSearchVo;
import com.spt.bas.report.client.vo.RptStockVirtualVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmProcessAccessVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 虚拟报价库存
 *
 * @Author: gaojy
 * @create 2022/5/9 10:46
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/stock/virtual")
public class StockVirtualController extends SingleCrudControll<StockVirtual, BaseVo> {
    @Resource
    private IStockVirtualClient stockVirtualClient;
    @Resource
    private IRptStockVirtualReportClient stockVirtualReportClient;
    @Resource
    private IBsProductTypeClient productTypeClient;
    @Resource
    private IBsFactoryClient factoryClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Resource
    private IPmProcessAccessClient pmProcessAccessClient;

    @Override
    public BaseClient<StockVirtual> getService() {
        return stockVirtualClient;
    }

    /**
     * 跳转至-模式选择页面
     *
     * @return
     */
    @RequestMapping(value = "/virtualPrompt")
    public String virtualPrompt(Model model, HttpServletRequest request) {
        PmProcessAccessVo accessVo = new PmProcessAccessVo(ShiroUtil.getCurrentUserId(), BasConstants.PROCESS_APPLY_STOCK_VIRTUAL_BUY);
        Boolean virtualBuyFlg = pmProcessAccessClient.verifyUserProcessPermission(accessVo);
        model.addAttribute("virtualType", request.getParameter("virtualType"));
        model.addAttribute("virtualBuyFlg", virtualBuyFlg);
        return "stock/virtual-prompt";
    }

    /**
     * 跳转至-报价数据选择
     *
     * @return
     */
    @RequestMapping(value = "/choose")
    public String findVirtualData(Model model, HttpServletRequest request) {
        model.addAttribute("virtualBuyTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_VIRTUAL_BUY_TYPE)));
        // 代采赊销-采购-采购需方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("dcsxOurCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
        // 企业抬头
        model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        return "stock/stockVirtual-choose";
    }

    /**
     * 报价数据查询
     *
     * @param searchVo
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "findDataList")
    public void findDataList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        parseChooseParam(searchVo, request);
        PageDown<StockVirtual> page = stockVirtualClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    private void parseChooseParam(PageSearchVo searchVo, HttpServletRequest request) {
        // 查看所有采购申请权限
        Boolean viewAllVirtualFlg = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIEWALL_VIRTUAL.getPermissionCode());
        logger.info("{} :查看所有采购申请权限：{}", ShiroUtil.getCurrentUserName(), viewAllVirtualFlg);
        Map<String, Object> searchParams = searchVo.getSearchParams();
        if (Boolean.FALSE.equals(viewAllVirtualFlg)) {
            searchParams.put("EQL_matchUserId_OR_EQL_sellMatchUserId_OR_ISNULLL_sellMatchUserId", ShiroUtil.getCurrentUserId());
        }
    }

    @ModelAttribute("preload")
    public StockVirtual getEntity(@RequestParam(value = "id", required = false) Long id) {
        StockVirtual entity = new StockVirtual();
        entity.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_N);
        entity.setId(0L);
        if (id != null) {
            if (id == 0L) {
                entity.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_N);
            } else {
                entity = getService().getEntity(id);
            }
        }
        return entity;
    }


    /**
     * 报价管理列表
     *
     * @param model model
     * @return 页面数据
     */
    @GetMapping("")
    public String stockVirtualPage(Model model) {
        buildPageListModel(model);
        return "stock/stockVirtual-purchaseVirtual";
    }

    /**
     * 展示合同模板信息分页
     *
     * @param searchVo 查询参数
     * @param request  请求
     * @param response 返回
     */
    @PostMapping(value = "/findStockVirtualList")
    public void findStockVirtualList(RptStockVirtualSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        PageDown<RptStockVirtualVo> stockVirtualList = stockVirtualReportClient.getStockVirtualList(searchVo);
        JsonEasyUI.renderJson(response, stockVirtualList);
    }

    /**
     * 获取报价单详情信息
     *
     * @param id id
     */
    @GetMapping("/getStockVirtualDetail")
    public void getStockVirtualDetail(Long id, HttpServletResponse response) {
        StockVirtual entity = stockVirtualClient.getEntity(id);
        RenderUtil.renderJson(entity, response);
    }

    /**
     * 作废报价单
     * @param id
     */
    @RequestMapping(value = "invalidStockVirtual/{id}", method = RequestMethod.POST)
    public void invalidStockVirtual(@PathVariable("id") Long id, HttpServletResponse response){
        stockVirtualClient.invalidStockVirtual(id);
        RenderUtil.renderSuccess("success", response);
    }

    @RequestMapping(value = "updateStockVirtual", method = RequestMethod.POST)
    public void updateStockVirtual(@RequestBody StockVirtual stockVirtual, HttpServletResponse response) {
        Long virtualId = stockVirtual.getId();
        BigDecimal newMinSellPrice = stockVirtual.getMinSellPrice();
        Long newSellMatchUserId = stockVirtual.getSellMatchUserId();
        String newSellMatchUserName = stockVirtual.getSellMatchUserName();
        logger.info("更新人:{},更新库存采购ID:{},new销售指导价:{}, 指定销售业务员ID:{},指定销售业务员：{}",
                ShiroUtil.getCurrentUserName(), virtualId, newMinSellPrice, newSellMatchUserId, newSellMatchUserName);
        try {
            StockVirtual entity = getEntity(virtualId);
            if (Objects.isNull(entity)) {
                RenderUtil.renderFailure("未查找到该库存采购单", response);
                return;
            } else if (StringUtils.isNotBlank(entity.getLinkApproveNo())) {
                RenderUtil.renderFailure("更新失败，该库存采购单已被关联使用", response);
                return;
            } else if (Objects.nonNull(newMinSellPrice) && entity.getDealPrice().compareTo(newMinSellPrice) == 0) {
                RenderUtil.renderFailure("与原销售指导价一致", response);
                return;
            }
            stockVirtualClient.updateStockVirtual(stockVirtual);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("updateMinSellPrice:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    private void buildPageListModel(Model model) {
        // 采购类型
        model.addAttribute("virtualBuyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_VIRTUAL_BUY_TYPE)));
        model.addAttribute("productTypeJson",
                JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
        // 厂商
        List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
        // 包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        // 质量标准
        model.addAttribute("qualityStandardJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        model.addAttribute("stockVirtualStatusJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.STOCK_VIRTUAL_STATUS)));
        model.addAttribute("contractTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.CONTRACT_TYPE)));
        // 提货方式
        model.addAttribute("deliveryTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        // 代采赊销-采购-采购需方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("dcsxOurCompanyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
        // 企业抬头
        model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        // 管理员权限
        model.addAttribute("zgbasAdmin", ShiroUtil.isPermitted(PermissionEnum.ZGBASADMIN.getPermissionCode()));
        EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
    }
}
