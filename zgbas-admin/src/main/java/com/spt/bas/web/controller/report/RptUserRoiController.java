package com.spt.bas.web.controller.report;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.RptBaseCost;
import com.spt.bas.client.remote.IRptBaseCostClient;
import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.bas.report.client.remote.IRptUserRoiClient;
import com.spt.bas.report.client.vo.RptUserRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.util.JsonEasyUI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 人员ROI报表
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 09:38
 */
@Controller
@RequestMapping("/rpt/userRoi")
@Slf4j
public class RptUserRoiController {

    @Autowired
    private IRptBaseCostClient rptBaseCostClient;
    @Autowired
    private IRptUserRoiClient userRoiClient;

    /**
     * 页面地址
     *
     * @param model model
     * @return 页面
     */
    @RequestMapping()
    public String page(Model model) {
        DateTimeFormatter partten = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        now = now.minusMonths(1);
        model.addAttribute("nowTargetMonth", now.format(partten));
        model.addAttribute("branchList", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.BRANCH_CD)));
        model.addAttribute("permReportUserRoiExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_USER_ROI_EXPORT.getPermissionCode()));
        return "report/user-roi";
    }

    @RequestMapping("/findUserRoiPage")
    public void findUserRoiPage(RptBaseCostVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("searchVo{}", JsonUtil.obj2Json(searchVo));
        initParam(searchVo);
        PageDown<RptBaseCost> userPage = rptBaseCostClient.findPage(searchVo);
        List<RptBaseCost> content = userPage.getContent();
        // 查询当前页的
        List<Long> userIdList = getUserIdList(content);
        RptUserRoiVo userRoiVo = new RptUserRoiVo();
        BeanUtils.copyProperties(searchVo, userRoiVo);
        userRoiVo.setUserList(userIdList);
        List<RptUserRoiResultVo> roiList = userRoiClient.findPage(userRoiVo);
        PageDown<RptUserRoiResultVo> page = new PageDown<>();
        BeanUtils.copyProperties(userPage,page);
        page.setContent(roiList);
        Map<String, Object> footer = getFooter(searchVo);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    private void initParam(RptBaseCostVo searchVo){
        Map<String,Object> param = new HashMap<>();
//        param.put("EQS_baseDate",searchVo.getBaseDate());
        param.put("GTES_baseDate",searchVo.getBaseStartDate());
        param.put("LTES_baseDate",searchVo.getBaseEndDate());
        param.put("LIKES_matchUserName",searchVo.getMatchUserName());
        param.put("EQS_branchCd",searchVo.getBranchCd());
        List<String> branchCds = new ArrayList<>();
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HD.getPermissionCode())) {
            branchCds.add(BasConstants.HD);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HB.getPermissionCode())) {
            branchCds.add(BasConstants.HB);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HN.getPermissionCode())) {
            branchCds.add(BasConstants.HN);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HDO.getPermissionCode())) {
            branchCds.add(BasConstants.HDO);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HZ.getPermissionCode())) {
            branchCds.add(BasConstants.HZ);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_OT.getPermissionCode())) {
            List<String> otherBranchCd = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), "branchCd")
                    .stream().filter(b-> StringUtils.equalsAnyIgnoreCase(PermissionEnum.ZGBAS_BASECOST_OT.getPermissionCode(), b.getRemark()))
                    .map(BsDictData::getDictCd).collect(Collectors.toList());
            branchCds.addAll(otherBranchCd);
        }
        if (CollectionUtils.isNotEmpty(branchCds)) {
            param.put("INS_branchCd", branchCds.toArray());
        } else {
            throw new IllegalArgumentException("请联系管理员分配业务成本统计查看权限！");
        }
        searchVo.setSearchParams(param);
    }

    private List<Long> getUserIdList(List<RptBaseCost> costList) {
        return costList.stream().map(RptBaseCost::getMatchUserId).distinct().collect(Collectors.toList());
    }

    /**
     * 合计
     *
     * @return 合计
     */
    private Map<String, Object> getFooter(RptBaseCostVo searchVo) {
        searchVo.setRows(9999);
        searchVo.setPage(1);
        PageDown<RptBaseCost> userListAll = rptBaseCostClient.findPage(searchVo);
        RptUserRoiVo userRoiVo = new RptUserRoiVo();
        BeanUtils.copyProperties(searchVo, userRoiVo);
        List<Long> userIdAllList = getUserIdList(userListAll.getContent());
        userRoiVo.setUserList(userIdAllList);
        return userRoiClient.getTotal(userRoiVo);
    }

    /**
     * 导出excle
     *
     * @param searchVo 查询参数
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping(value = "/exportUserRoi")
    public void exportUserRoi(RptBaseCostVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initParam(searchVo);
        searchVo.setRows(2000);
        PageDown<RptBaseCost> rptContent = rptBaseCostClient.findPage(searchVo);
        List<Long> userIdList = rptContent.getContent().stream().map(RptBaseCost::getMatchUserId).distinct().collect(Collectors.toList());

        RptUserRoiVo userRoiVo = new RptUserRoiVo();
        userRoiVo.setUserList(userIdList);
        BeanUtils.copyProperties(searchVo, userRoiVo);

        List<RptUserRoiResultVo> content = userRoiClient.findPage(userRoiVo);
        String title = "人员ROI报表";
        String[] titles = new String[] { "姓名","所属区域", "年月", "订单数", "吨数", "销售额(万元)", "毛利(万元)","毛利率均值","总投入(万元)","提成(万元)","出差(万元)","ROI"};
        String[] attrs = new String[] { "matchUserName","branchName", "baseDate", "orderCount", "tunnage", "sellMoney", "gross","grossAvg","totalFinancing","commission","evectionCost","roi" };
        int[] widths = new int[]{15, 15, 15, 20, 20, 20, 20, 20, 20, 20, 20, 20};
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
        PoiExcelUtil.createRows(sheet, content, attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);

        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
