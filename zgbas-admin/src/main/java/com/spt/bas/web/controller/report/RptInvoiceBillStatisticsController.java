package com.spt.bas.web.controller.report;

import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.report.client.entity.RptInvoiceBillStatistics;
import com.spt.bas.report.client.remote.IRptInvoiceBillStatisticsClient;
import com.spt.bas.report.client.vo.RptInvoiceBillStatisticsVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/rpt/invoiceBillStatistics")
public class RptInvoiceBillStatisticsController extends PageController<RptInvoiceBillStatistics, BaseVo> {
    @Autowired
    private IRptInvoiceBillStatisticsClient rptInvoiceBillStatisticsClient;

    @Override
    public BaseClient<RptInvoiceBillStatistics> getService() {
        return rptInvoiceBillStatisticsClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String format = simpleDateFormat.format(new Date());
        model.addAttribute("invoiceDate",format);
        //我方抬头
        model.addAttribute("ourCompanyJson",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        return "report/rptInvoiceBillStatistics";
    }

    /**
     * 获取按月汇总资金方的开票信息
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findInvoiceBillStatistics")
    public void findInvoiceBillStatistics(RptInvoiceBillStatisticsVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        // 权限判断
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIEWALL_INVOICE_BILL_STATISTICS.getPermissionCode())) {
            searchVo.setViewAllFlg(true);
        }
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        List<RptInvoiceBillStatistics> list = rptInvoiceBillStatisticsClient.findInvoiceBillStatistics(searchVo);
        JsonEasyUI.renderListJson(response, list, null, null);
    }


    /**
     * 开票统计EXCEL导出
     *
     * @param searchVo
     * @param request
     * @param response
     * @throws ApplicationException
     */
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptInvoiceBillStatisticsVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        // 权限判断
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIEWALL_INVOICE_BILL_STATISTICS.getPermissionCode())) {
            searchVo.setViewAllFlg(true);
        }
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        List<RptInvoiceBillStatistics> list = rptInvoiceBillStatisticsClient.findInvoiceBillStatistics(searchVo);
        String title = "开票统计";

        String[] titles = new String[]{"我方", "年月", "开票总额", "开票吨数", "收票总额", "收票吨数"};
        String[] attrs = new String[]{"ourCompanyName", "invoiceDate", "invoiceAmount", "invoiceNumber", "invoiceReceiveAmount", "invoiceReceiveNumber"};
        int[] widths = new int[]{20, 15, 15, 15, 15, 15};
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
        PoiExcelUtil.createRows(sheet, list ,attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
