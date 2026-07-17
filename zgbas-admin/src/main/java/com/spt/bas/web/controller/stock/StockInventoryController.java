package com.spt.bas.web.controller.stock;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.entity.StockInventory;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IBsFactoryClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockInventoryClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author MoonLight
 * @Date 2024/8/20 13:56
 * @Version 1.0
 */
@Controller
@RequestMapping(value = "/inventory/stockVirtual")
public class StockInventoryController extends SingleCrudControll<StockInventory, BaseVo> {
    @Resource
    private WebParamUtils webParamUtils;
    @Resource
    private IBsProductTypeClient productTypeClient;
    @Resource
    private IBsFactoryClient factoryClient;
    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Resource
    private IStockInventoryClient inventoryStockVirtualClient;

    @Override
    public BaseClient<StockInventory> getService() {
        return inventoryStockVirtualClient;
    }

    @GetMapping("/index")
    public String index(Model model){
        buildPageListModel(model);
        return "stock/inventory-stockVirtual";
    }

    @RequestMapping(value = "findInventoryPage")
    public void findDataList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        parseParam(searchVo, request);
        PageDown<StockInventory> page = inventoryStockVirtualClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    private void parseParam(PageSearchVo searchVo, HttpServletRequest request) {
        // 查看所有采购申请权限
        Boolean viewAllVirtualFlg = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIEWALL_VIRTUAL.getPermissionCode());
        logger.info("{} :查看所有采购申请权限：{}", ShiroUtil.getCurrentUserName(), viewAllVirtualFlg);
        Map<String, Object> searchParams = searchVo.getSearchParams();
        if (Boolean.FALSE.equals(viewAllVirtualFlg)) {
            searchParams.put("EQL_matchUserId_OR_EQL_sellMatchUserId_OR_ISNULLL_sellMatchUserId", ShiroUtil.getCurrentUserId());
        }
    }

    private void buildPageListModel(Model model){
        // 采购类型
        model.addAttribute("virtualBuyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId() , BasConstants.DICT_TYPE_VIRTUAL_BUY_TYPE)));
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

    /**
     * 编辑虚拟库存
     * @param id id
     */
    @GetMapping("/getInventoryStockVirtual")
    public void getInventoryStockVirtual(Long id,HttpServletResponse response){
        StockInventory entity = inventoryStockVirtualClient.getEntity(id);
        RenderUtil.renderJson(entity, response);
    }

    /**
     * 作废采购申请
     * @param id
     */
    @RequestMapping(value = "invalidInventory/{id}", method = RequestMethod.POST)
    public void invalidInventory(@PathVariable("id") Long id, HttpServletResponse response){
        logger.info("invalidInventory id:{} , operation:{}", id, ShiroUtil.getCurrentUserName());
        inventoryStockVirtualClient.invalidInventory(id);
        RenderUtil.renderSuccess("success", response);
    }

    @RequestMapping(value = "updateInventory", method = RequestMethod.POST)
    public void updateInventory(@RequestBody StockInventory stockVirtual, HttpServletResponse response) {
        Long virtualId = stockVirtual.getId();
        BigDecimal newMinSellPrice = stockVirtual.getMinSellPrice();
        BigDecimal newReleaseNumber = stockVirtual.getReleaseNumber();
        Long newSellMatchUserId = stockVirtual.getSellMatchUserId();
        String newSellMatchUserName = stockVirtual.getSellMatchUserName();
        logger.info("更新人:{},更新库存采购ID:{},new销售指导价:{},释放数量:{}, 指定销售业务员ID:{},指定销售业务员：{}",
                ShiroUtil.getCurrentUserName(), virtualId, newMinSellPrice, newReleaseNumber, newSellMatchUserId, newSellMatchUserName);
        try {
            StockInventory entity = inventoryStockVirtualClient.getEntity(virtualId);
            if (Objects.isNull(entity)) {
                RenderUtil.renderFailure("未查找到该库存采购单", response);
                return;
            } else if (Objects.nonNull(newMinSellPrice) && entity.getDealPrice().compareTo(newMinSellPrice) == 0) {
                RenderUtil.renderFailure("与原销售指导价一致", response);
                return;
            } else if (Objects.nonNull(newReleaseNumber) && entity.getDealNumber().subtract(entity.getReleaseNumber()).subtract(newReleaseNumber).compareTo(BigDecimal.ZERO) < 0) {
                RenderUtil.renderFailure("释放数量有误，最多可释放数量:" + entity.getDealNumber().subtract(entity.getReleaseNumber()), response);
                return;
            } else {
                inventoryStockVirtualClient.updateInventory(stockVirtual);
            }
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("updateMinSellPrice:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }
    @RequestMapping(value = "findInventoryVirtualPage")
    public void findInventoryVirtualPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<StockInventory> page = inventoryStockVirtualClient.findInventoryVirtualPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "exportExcel")
    public void exportExcel(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {

        // 代采赊销-采购-采购需方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        Map<String, String> dcsxCompanyNameMap = dcsxCompanyList.stream().collect(Collectors.toMap(BsCompanyDcsx::getCompanyCd, BsCompanyDcsx::getCompanyAbbr));
        // 我方
        List<BsDictData> companyOurToBsDictDataList = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
        Map<String, String> ourCompanyNameMap = companyOurToBsDictDataList.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getRemark));
        // 交货方式
        List<SysDictDataSdk> deliveryTypeList = DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY);
        Map<String, String> deliveryTypeMap = deliveryTypeList.stream().collect(Collectors.toMap(SysDictDataSdk::getDictCd, SysDictDataSdk::getDictName));
        // 状态
        List<BsDictData> stockVirtualStatusList = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.STOCK_VIRTUAL_STATUS);
        Map<String, String> stockVirtualStatusMap = stockVirtualStatusList.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName));

        initSearch(searchVo, request);
        parseParam(searchVo, request);
        int batchSize = 100000;
        searchVo.setRows(batchSize);
        PageDown<StockInventory> page = inventoryStockVirtualClient.findPage(searchVo);
        List<StockInventory> content = page.getContent();
        if(CollectionUtils.isNotEmpty(content)){
            content.forEach(it->{
                it.setBuyOurCompanyName(dcsxCompanyNameMap.get(it.getBuyOurCompanyName()));
                it.setOurCompanyName(ourCompanyNameMap.get(it.getOurCompanyName()));
                it.setDeliveryType(deliveryTypeMap.get(it.getDeliveryType()));
                it.setInventoryStatus(stockVirtualStatusMap.get(it.getInventoryStatus()));
                //设置库存数量dealNumber-releaseNumber(dealNumber 数据库中设置了不为null，不用判断)
                BigDecimal dealNumber = it.getReleaseNumber()==null?BigDecimal.ZERO:it.getReleaseNumber();
                it.setStockNumber(it.getDealNumber().subtract(dealNumber));
            });
        }
        String title = "库存列表";
        String[] titles = new String[]{"库存编号","供应商","代采方","我方","品种","牌号",
                "厂商","采购数量(吨)","已释放数量(吨)","库存数量","采购单价(元/吨)","加价(元/吨)","销售指导价(元/吨)","总价(元)","交货日期","付全款日期","交货方式","状态","交货地点",
                "采购业务员"};
        String[] attrs = new String[]{"stockVirtualNo","companyName","buyOurCompanyName","ourCompanyName","productName","brandNumber",
                "factoryName","dealNumber","releaseNumber","stockNumber","dealPrice","raisePrice",
                "minSellPrice","totalAmount","deliveryDate","payFullTime","deliveryType","inventoryStatus","deliveryAddr", "matchUserName"};
        int[] widths = new int[]{20, 20, 15, 15, 15, 15,
                15, 15, 15, 15, 20, 15, 20, 15, 15, 15, 15, 15, 20, 15};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        /** 创建表头 */
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        PoiExcelUtil.createRows(sheet, content, attrs, start, cellStyle, DateOperator.FORMAT_STR);
        try {
            PoiExcelUtil.write(workbook, response, title);

        } catch (IOException e) {
        }
    }
}
