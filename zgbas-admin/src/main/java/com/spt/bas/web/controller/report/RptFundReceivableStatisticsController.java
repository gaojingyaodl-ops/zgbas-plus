package com.spt.bas.web.controller.report;

import com.google.common.base.Splitter;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsFunder;
import com.spt.bas.client.remote.IBsFunderClient;
import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.remote.IRptFundReceivableStatisticsClient;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.util.JsonEasyUI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资金方应收统计
 */
@Slf4j
@Controller
@RequestMapping(value = "/rpt/fundReceivableStatistics")
public class RptFundReceivableStatisticsController {
    @Autowired
    private IRptFundReceivableStatisticsClient fundReceivableStatisticsClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsFunderClient bsFunderClient;

    @RequestMapping(value = "content")
    public String content(Model model, HttpServletRequest request) {
        // 区域查询选项
        model.addAttribute("regionContrastJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_REGION_CONTRAST)));
        model.addAttribute("permReportFundReceivableStatisticsExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_FUND_RECEIVABLE_STATISTICS_EXPORT.getPermissionCode()));
        return "report/fundReceivableStatistics";
    }

    @RequestMapping(value = "findPage")
    public void findPage(RptFundReceivableStatisticsVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        // 资金方只能查看自己的,管理员查看全部
        boolean adminFlg = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIEWALL_FUND_RECE_STATISTICS.getPermissionCode());
        if(!adminFlg){
            String buyCompanyName = "";
            List<String> companyNameList = new ArrayList<>();
            List<BsFunder> bsFunderList = bsFunderClient.findAllByUserId(ShiroUtil.getCurrentUserId());
            if (!org.springframework.util.CollectionUtils.isEmpty(bsFunderList)) {
                buyCompanyName = bsFunderList.get(0).getCompanyNames();
                if (StringUtils.isNotBlank(buyCompanyName)) {
                    companyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(buyCompanyName);
                    searchVo.setCompanyNameList(companyNameList);
                } else {
                    // 防止查询全部，防止sql 报错
                    companyNameList.add("-1");
                    searchVo.setCompanyNameList(companyNameList);
                }
            } else {
                // 防止查询全部，防止sql 报错
                companyNameList.add("-1");
                searchVo.setCompanyNameList(companyNameList);
            }
        }
        PageDown<RptFundReceivableStatistics> page = fundReceivableStatisticsClient.findPage(searchVo);
        List<RptFundReceivableStatistics> content = page.getContent();
        Map<String, Object> footer = new HashMap<>();
        if (CollectionUtils.isNotEmpty(content)) {
            int receivableContractNumSum = content
                    .stream()
                    .mapToInt(RptFundReceivableStatistics::getReceivableContractNum)
                    .sum();
            BigDecimal receivableBreachAmountSum = content.stream()
                    .map(RptFundReceivableStatistics::getReceivableBreachAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal receivableBalanceSum = content.stream()
                    .map(RptFundReceivableStatistics::getReceivableBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            footer.put("companyAbbr", "合计");
            footer.put("receivableContractNum", receivableContractNumSum);
            footer.put("receivableBreachAmount", receivableBreachAmountSum);
            footer.put("receivableBalance", receivableBalanceSum);
        }
        JsonEasyUI.renderJson(response, page, footer);
    }

    @RequestMapping(value = "exportExcel")
    public void exportExcel(RptFundReceivableStatisticsVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        int batchSize = 100000;
        searchVo.setRows(batchSize);
        PageDown<RptFundReceivableStatistics> page = fundReceivableStatisticsClient.findPage(searchVo);
        List<RptFundReceivableStatistics> content = page.getContent();
        String title = "资金方应收统计";
        String[] titles = new String[]{"代采方", "应收合同数", "应收逾期罚息（元）", "应收余额（元）"};
        String[] attrs = new String[]{"companyAbbr", "receivableContractNum", "receivableBreachAmount", "receivableBalance"};
        int[] widths = new int[]{15, 15, 20, 15};
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
