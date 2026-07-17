package com.spt.bas.web.controller.report;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.remote.IRptSummaryRoiClient;
import com.spt.bas.report.client.vo.RptSummaryResultVo;
import com.spt.bas.report.client.vo.RptSummaryRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.util.JsonEasyUI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ROI 汇总表
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 10:22
 */
@Controller
@RequestMapping("/rpt/summaryRoi")
@Slf4j
public class RptSummaryRoiController {

    @Autowired
    private IRptSummaryRoiClient summaryRoiClient;

    /**
     * 页面地址
     *
     * @param model model
     * @return 页面
     */
    @RequestMapping()
    public String page(Model model){
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        now = now.minusMonths(1);
        model.addAttribute("nowTargetMonth", now.format(date));
        model.addAttribute("branchList", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.BRANCH_CD)));
        model.addAttribute("permReportUserRoiSumExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_USER_ROI_SUM_EXPORT.getPermissionCode()));
        return "report/summary-roi";
    }

    @RequestMapping("/findSummaryRoiPage")
    public void findUserRoiPage(RptUserRoiVo searchVo, HttpServletResponse response){
        List<String> permitteds = new ArrayList<>();
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HD.getPermissionCode())) {
            permitteds.add(BasConstants.HD);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HB.getPermissionCode())) {
            permitteds.add(BasConstants.HB);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HN.getPermissionCode())) {
            permitteds.add(BasConstants.HN);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HDO.getPermissionCode())) {
            permitteds.add(BasConstants.HDO);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HZ.getPermissionCode())) {
            permitteds.add(BasConstants.HZ);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_OT.getPermissionCode())) {
            List<String> otherBranchCd = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), "branchCd")
                    .stream().filter(b-> StringUtils.equalsAnyIgnoreCase(PermissionEnum.ZGBAS_BASECOST_OT.getPermissionCode(), b.getRemark()))
                    .map(BsDictData::getDictCd).collect(Collectors.toList());
            permitteds.addAll(otherBranchCd);
        }
        searchVo.setBranchCdList(permitteds);
        RptSummaryResultVo data = summaryRoiClient.findPage(searchVo);
        if (Objects.isNull(data.getPage())) {
            JsonEasyUI.renderJson(response, new PageImpl<RptSummaryResultVo>(new ArrayList<>()), null, new HashMap<>());
            return;
        }
        JsonEasyUI.renderJson(response, data.getPage(), null, data.getFooter());
    }


    /**
     * 导出excle
     *
     * @param searchVo 查询参数
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping(value = "/exportUserRoi")
    public void exportUserRoi(RptUserRoiVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        RptSummaryResultVo data = summaryRoiClient.findPage(searchVo);
        PageDown<RptSummaryRoiResultVo> page = data.getPage();
        List<RptSummaryRoiResultVo> content = new ArrayList<>();
        if (Objects.nonNull(page)) {
            content = page.getContent();
        }
        String title = "ROI汇总表";
        String[] titles = new String[] { "业务类型","所属区域", "年月", "业务人数", "订单数", "吨数", "销售额(万元)","销售额人效","毛利(万元)","毛利人效","毛利率均值"};
        String[] attrs = new String[] { "businessTypeName","branchName", "baseDate", "businessUserCount", "orderCount", "tonnes", "sellMoney","sellLabor","gross","grossLabor","grossAvg"};
        int[] widths = new int[]{15, 15, 15, 20, 20, 20, 20, 20, 20, 20, 20};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widths);
        int start = 0;
        PoiExcelUtil.createRows(sheet, content, attrs, start, cellStyle,
                DateOperator.FORMAT_STR_WITH_TIME);
        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
