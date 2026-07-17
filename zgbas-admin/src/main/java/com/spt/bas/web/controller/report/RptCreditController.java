package com.spt.bas.web.controller.report;

import com.spt.bas.report.client.remote.IRptCreditClient;
import com.spt.bas.report.client.vo.RptCreditBusinessCommission;
import com.spt.bas.report.client.vo.RptCreditBusinessCommissionSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 賒賬明細
 * @author liuzhenwei
 */

@Controller
@RequestMapping(value = "/rpt/credit")
public class RptCreditController extends PageController<RptCreditBusinessCommission, BaseVo> {
    @Autowired
    private IRptCreditClient iCreditClient;

    @Override
    public BaseClient<RptCreditBusinessCommission> getService() {
        return iCreditClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        return "report/creditCommission";
    }
    /**
     *
     * 赊销业务提成计算明细
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "/findMatchUserProfit")
    public void findMatchUserProfit(RptCreditBusinessCommissionSearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
        PageDown<RptCreditBusinessCommission> page = iCreditClient.findCreditBusinessCommissionPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptCreditBusinessCommissionSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);

        PageDown<RptCreditBusinessCommission> page = iCreditClient.findCreditBusinessCommissionPage(searchVo);
        String title = "赊销业务提成明细";

        String[] titles = new String[] { "业务类型", "合同号", "合同客户", "销售人员", "采购人员", "合同数量", "采购单价", "销售单价",
                "销售总额", "采购总额", "付款日期", "实际收款日期", "约定付款日期", "实际收货日期", "金融服务账期", "金融服务费", "运输费", "仓储费","出库费",
                "逾期天数", "逾期罚息" ,"保险税率","增值税税后差价","增值税","印花税","税金及附加","税后差价收入","销售团队负责人分成","采购团队负责人分成",
                "销售人员分成","采购人员分成"};
        String[] attrs = new String[] {"businessTypeDesc", "contractNo", "companyName", "sellMatchUserName",
                "buyMatchUserName", "totalNumber", "sellPrice", "buyPrice", "sellTotalAmount", "buyTotalAmount",
                "payDate", "receiveDate", "appointPayDate", "confirmReceiptDate", "creditCycle",
                "financialServiceAmount", "transportAmount", "warehouseAmount","deliveryFee" ,"breachDay", "breachAmount",
                "insuranceRate", "vatSpreadAmount", "vatAmount", "printAmount", "taxesSurchargesAmount",
                "afterTaxSpreadAmount", "sellHeadCommissionAmount", "buyHeadCommissionAmount", "sellMatchAmount",
                "buyMatchAmount"};
        int[] widths = new int[] { 15, 15, 25, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 20, 15, 15,15, 15, 15,
                15 ,15,15,15,15,15,15,15,15,15};
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
        while (page != null && page.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR);
            if (page.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = iCreditClient.findCreditBusinessCommissionPage(searchVo);
                start += batchSize;
            } else {
                page = null;
            }
        }

        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }


}
