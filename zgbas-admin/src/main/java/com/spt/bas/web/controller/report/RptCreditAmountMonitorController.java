package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.report.client.entity.RptCreditAmountMonitor;
import com.spt.bas.report.client.remote.IRptCreditAmountMonitorClient;
import com.spt.bas.report.client.vo.RptCreditAmountMonitorSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.*;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.util.JsonEasyUI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = "/rpt/creditAmountMonitor")
public class RptCreditAmountMonitorController {
    
    @Autowired
    private IRptCreditAmountMonitorClient creditAmountMonitorClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content")
    public String content(Model model, HttpServletRequest request) {
        model.addAttribute("companyStatus",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));
        model.addAttribute("creditRating",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级
        model.addAttribute("companyType",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_COMPANYTYPE)));// 客户分类
        model.addAttribute("companyGrade",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));// 客户分类
        model.addAttribute("onLineFlg",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ONLiNEFLG)));// 线上化查询

        model.addAttribute("matchUserId", request.getParameter("matchUserId"));
        model.addAttribute("deptId", request.getParameter("deptId"));
        //获取业务员树
        List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("permReportCreditMonitorExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_CREDIT_MONITOR_EXPORT.getPermissionCode()));
        return "report/creditAmountMonitor";
    }

    /**
     * 授信额度监控列表查询
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findCreditAmountMonitorPage")
    public void findCreditAmountMonitorPage(RptCreditAmountMonitorSearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
        PageDown<RptCreditAmountMonitor> page = creditAmountMonitorClient.findCreditAmountMonitorPage(searchVo);
        JsonEasyUI.renderJson(response, page,null,getFooter(searchVo));
    }
    /**
     * 合计
     *
     * @return 合计
     */
    private Map<String, Object> getFooter(RptCreditAmountMonitorSearchVo searchVo) {
        searchVo.setRows(9999);
        searchVo.setPage(1);
        PageDown<RptCreditAmountMonitor> page = creditAmountMonitorClient.findCreditAmountMonitorPage(searchVo);
        List<RptCreditAmountMonitor> content = page.getContent();
        BigDecimal receiveAmountSum = BigDecimal.ZERO;
        BigDecimal overdueAmountSum = BigDecimal.ZERO;
        BigDecimal excessAmountSum = BigDecimal.ZERO;
        BigDecimal excessGrossProfitSum = BigDecimal.ZERO;
        BigDecimal piccCreditAmountSum = BigDecimal.ZERO;
        if(CollectionUtils.isNotEmpty(content)) {
            for (RptCreditAmountMonitor creditAmountMonitor : content) {
                receiveAmountSum = receiveAmountSum.add(creditAmountMonitor.getReceiveAmount());
                overdueAmountSum = overdueAmountSum.add(creditAmountMonitor.getOverdueAmount());
                excessAmountSum = excessAmountSum.add(creditAmountMonitor.getExcessAmount());
                excessGrossProfitSum = excessGrossProfitSum.add(creditAmountMonitor.getExcessGrossProfit());
                piccCreditAmountSum = piccCreditAmountSum.add(creditAmountMonitor.getPiccCreditAmount());
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("companyName", "合计");
        result.put("receiveAmount", receiveAmountSum);
        result.put("overdueAmount", overdueAmountSum);
        result.put("excessAmount", excessAmountSum);
        result.put("excessGrossProfit", excessGrossProfitSum);
        result.put("piccCreditAmount", piccCreditAmountSum);
        return result;
    }

    /**
     * 授信额度监控EXCEL导出
     * @param searchVo
     * @param request
     * @param response
     * @throws ApplicationException
     */
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptCreditAmountMonitorSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        int batchSize = 500;
        searchVo.setRows(batchSize);
        
        List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
        Map<Long, SysDeptSdk> deptMap = deptList.stream().collect(Collectors.toMap(SysDeptSdk::getDeptId, sysDeptSdk -> sysDeptSdk));


        PageDown<RptCreditAmountMonitor> page = creditAmountMonitorClient.findCreditAmountMonitorPage(searchVo);
        String title = "授信额度监控";

        List<RptCreditAmountMonitor> content = page.getContent();
        if(CollectionUtils.isNotEmpty(content)) {
            for (RptCreditAmountMonitor creditAmountMonitor : content) {
                Boolean piccFlg = creditAmountMonitor.getPiccFlg();
                if(piccFlg !=null && piccFlg) {
                    creditAmountMonitor.setPiccFlgStr("人保授信");
                } else {
                    creditAmountMonitor.setPiccFlgStr("自主授信");
                }
                SysDeptSdk deptSdk = deptMap.get(creditAmountMonitor.getDeptId());
                if(Objects.nonNull(deptSdk)) {
                    creditAmountMonitor.setDeptName(deptSdk.getDeptName());
                }
                Date contractStartDate = creditAmountMonitor.getContractStartDate();
                if(contractStartDate != null) {
                    DateUtils.parseDateToStr("yyyy.MM",contractStartDate);
                }
                BigDecimal excessRate = creditAmountMonitor.getExcessRate();
                if(excessRate != null) {
                    if(excessRate.compareTo(BigDecimal.ZERO) > 0) {
                        creditAmountMonitor.setExcessRateStr(excessRate + "%");
                    } else {
                        creditAmountMonitor.setExcessRateStr("0%");
                    }
                } else {
                    creditAmountMonitor.setExcessRateStr("0%");
                }
                String creditRating = creditAmountMonitor.getCreditRating();
                if(StringUtils.isNotEmpty(creditRating)) {
                    if(StringUtils.equals("W",creditRating)) {
                        creditAmountMonitor.setCreditRating("白名单");
                    } else if(StringUtils.equals("G",creditRating)) {
                        creditAmountMonitor.setCreditRating("灰名单");
                    } else if(StringUtils.equals("B",creditRating)) {
                        creditAmountMonitor.setCreditRating("黑名单");
                    }
                }

            }
        }

        String[] titles = new String[] { "客户名称","客户分类", "授信类别", "人保额度", "大地额度", "已使用额度", "超保毛利", "应收本金", "超额金额", "超额占比", "赊销单数", "应收逾期本金", "逾期[7，15)",
                "逾期[15，30)", "逾期[30，)", "开始合作时间", "是否签连带", "是否访厂", "业务员", "区域ID" };
        String[] attrs = new String[] { "companyName","creditRating", "piccFlgStr", "piccCreditAmount", "daDiCreditAmount", "usedCreditAmount", "excessGrossProfit", "receiveAmount", "excessAmount",
                "excessRateStr", "creditOrdersNum", "overdueAmount", "overdueDays7", "overdueDays15", "overdueDays30", "contractStartDateStr",
                "liabilityFlg", "accessReportFlg", "matchUserName", "deptName" };
        int[] widths = new int[] { 20, 15, 15, 15, 15, 15,15, 15, 20, 15, 15, 15, 15, 15, 15, 20, 15, 15, 20, 15};
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
            PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
            if (page.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = creditAmountMonitorClient.findCreditAmountMonitorPage(searchVo);
                List<RptCreditAmountMonitor> contentNext = page.getContent();
                if(CollectionUtils.isNotEmpty(contentNext)) {
                    for (RptCreditAmountMonitor creditAmountMonitor : contentNext) {
                        Boolean piccFlg = creditAmountMonitor.getPiccFlg();
                        if(piccFlg !=null && piccFlg) {
                            creditAmountMonitor.setPiccFlgStr("人保授信");
                        } else {
                            creditAmountMonitor.setPiccFlgStr("自主授信");
                        }
                        SysDeptSdk deptSdk = deptMap.get(creditAmountMonitor.getDeptId());
                        if(Objects.nonNull(deptSdk)) {
                            creditAmountMonitor.setDeptName(deptSdk.getDeptName());
                        }
                        Date contractStartDate = creditAmountMonitor.getContractStartDate();
                        if(contractStartDate != null) {
                            DateUtils.parseDateToStr("yyyy.MM",contractStartDate);
                        }
                        BigDecimal excessRate = creditAmountMonitor.getExcessRate();
                        if(excessRate != null) {
                            if(excessRate.compareTo(BigDecimal.ZERO) > 0) {
                                creditAmountMonitor.setExcessRateStr(excessRate + "%");
                            } else {
                                creditAmountMonitor.setExcessRateStr("0%");
                            }
                        } else {
                            creditAmountMonitor.setExcessRateStr("0%");
                        }
                        String creditRating = creditAmountMonitor.getCreditRating();
                        if(StringUtils.isNotEmpty(creditRating)) {
                            if(StringUtils.equals("W",creditRating)) {
                                creditAmountMonitor.setCreditRating("白名单");
                            } else if(StringUtils.equals("G",creditRating)) {
                                creditAmountMonitor.setCreditRating("灰名单");
                            } else if(StringUtils.equals("B",creditRating)) {
                                creditAmountMonitor.setCreditRating("黑名单");
                            }
                        }

                    }
                }
                start += batchSize;
            } else {
                page = null;
            }
        }
        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    
}
