package com.spt.bas.web.controller.stock;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IBsFactoryClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.report.client.entity.RptStockBook;
import com.spt.bas.report.client.remote.IRptStockInventoryClient;
import com.spt.bas.report.client.vo.RptStockBookVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 库存台账
 */
@Controller
@RequestMapping(value = "/inventory/book")
public class StockInventoryBookController {
    @Resource
    private WebParamUtils webParamUtils;
    @Resource
    private IBsProductTypeClient productTypeClient;
    @Resource
    private IBsFactoryClient factoryClient;
    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IRptStockInventoryClient stockInventoryClient;

    @GetMapping("/index")
    public String index(Model model) {
        buildPageListModel(model);
        return "stock/inventory-stockBook";
    }

    private void buildPageListModel(Model model) {
        // 区域查询选项
        model.addAttribute("regionContrastJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_REGION_CONTRAST)));
    }

    @RequestMapping(value = "findPageStockBook")
    public void findPageStockBook(RptStockBookVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        // 赊销状态单独处理
        if (searchVo.getBusinessType().equals("ZY-BB1")) {
            searchVo.setBusinessType("ZY-BB");
            searchVo.setMatchCreditFlg(true);
        }
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        parseParam(searchVo,request);
        PageDown<RptStockBook> page = stockInventoryClient.findPageStockBook(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "exportExcel")
    public void exportExcel(RptStockBookVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        // 赊销状态单独处理
        if (searchVo.getBusinessType().equals("ZY-BB1")) {
            searchVo.setBusinessType("ZY-BB");
            searchVo.setMatchCreditFlg(true);
        }
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        parseParam(searchVo,request);
        int batchSize = 100000;
        searchVo.setRows(batchSize);
        PageDown<RptStockBook> page = stockInventoryClient.findPageStockBook(searchVo);
        List<RptStockBook> content = page.getContent();
        String title = "库存台账";
        String[] titles = new String[]{"库存编号", "销售合同号", "下游我方", "下游客户", "业务类型", "货名",
                "业务员", "业务大区", "数量(吨)", "销售指导价(元)","上游-采购价(元)", "中游-采购价(元)", "销售价(元)","销售总价（元）", "指导价毛利", "采购价毛利", "在库天数", "下游账期", "资金成本", "仓储费", "运输费",
                "装卸费", "保费", "签约日期", "出库日期", "支付上游日期","支付上游金额","下游回款日期","下游付款金额", "确认收货日期"};
        String[] attrs = new String[]{"stockVirtualNo", "contractNo", "ourCompanyName", "companyName", "businessTypeName", "productsName",
                "matchUserName", "deptName", "totalNumber", "minSellPrice", "kubBuyPrice","buyPrice", "sellPrice","totalAmount",
                "minSellPriceProfit", "buyPriceProfit", "warehouseDay", "creditCycle", "costOfFunds", "warehouseAmount", "transportAmount", "stevedorage"
                , "insuranceAmount", "contractTime", "realWarehoseDate", "payFullTime","sumPayAmount","receiveDate","sumReceiveAmount", "confirmDate"};
        int[] widths = new int[]{20, 20, 15, 15, 15, 15,15,
                15, 15, 15, 15, 20, 15, 15,20, 15, 15, 15, 15, 15, 20, 15, 15, 15, 15, 15,15,15,15, 15};
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
    private void parseParam(RptStockBookVo searchVo, HttpServletRequest request) {
        // 查看所有采购申请权限
        Boolean viewAllVirtualFlg = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIEWALL_VIRTUAL_BOOK.getPermissionCode());
        if(viewAllVirtualFlg){
            searchVo.setViewAllFlg(true);
        }
    }
}
