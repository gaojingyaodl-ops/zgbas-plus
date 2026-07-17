package com.spt.bas.web.controller.report;

import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.RptBaseCost;
import com.spt.bas.client.remote.IRptBaseCostClient;
import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.bas.report.client.vo.RptBaseCostReportVo;
import com.spt.bas.web.config.BasicErrorController;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rpt/baseCost")
public class RptBaseCostController extends PageController<RptBaseCost, BaseVo> {

    @Autowired
    private IRptBaseCostClient rptBaseCostClient;
    @Autowired
    private com.spt.bas.report.client.remote.IRptBaseCostClient rptBaseCostClient2;

    @Override
    public BaseClient<RptBaseCost> getService() {
        return rptBaseCostClient;
    }

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        DateTimeFormatter partten = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        now = now.minusMonths(1);
        String profitFlg = request.getParameter("profitFlg");
        if (StringUtils.equals("true", profitFlg)) {
            model.addAttribute("nowTargetMonth", request.getParameter("targetMonth"));
        } else {
            model.addAttribute("nowTargetMonth", now.format(partten));
        }
        model.addAttribute("branchList", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.BRANCH_CD)));
        model.addAttribute("permReportMatchUserCostStatisticsExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_MATCH_USER_COST_STATISTICS_EXPORT.getPermissionCode()));
        return "bas/rptBaseCost";
    }

    @RequestMapping(value = "selectByPage")
    public void selectByPage(RptBaseCostVo rptBaseCostVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        // TODO 添加权限
        buildSearchParams(rptBaseCostVo);
        
        PageDown<RptBaseCostReportVo> page = rptBaseCostClient2.findPage(rptBaseCostVo);
        Map<String, Object> footer = rptBaseCostClient2.getTotal(rptBaseCostVo);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    /**
     * 组装个区域数据查看权限查询参数
     *
     * @param rptBaseCostVo
     * @return
     * @throws ApplicationException
     */
    private RptBaseCostVo buildSearchParams(RptBaseCostVo rptBaseCostVo) throws ApplicationException {
        List<String> branchCdList = new ArrayList<>();
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HD.getPermissionCode())) {
            branchCdList.add(BasConstants.HD);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HB.getPermissionCode())) {
            branchCdList.add(BasConstants.HB);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HN.getPermissionCode())) {
            branchCdList.add(BasConstants.HN);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HDO.getPermissionCode())) {
            branchCdList.add(BasConstants.HDO);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HZ.getPermissionCode())) {
            branchCdList.add(BasConstants.HZ);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_NB.getPermissionCode())) {
            branchCdList.add(BasConstants.NB);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HZ.getPermissionCode())) {
            branchCdList.add(BasConstants.JX);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_OT.getPermissionCode())) {
            List<String> otherBranchCd = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), "branchCd")
                    .stream().filter(b-> StringUtils.equalsAnyIgnoreCase(PermissionEnum.ZGBAS_BASECOST_OT.getPermissionCode(), b.getRemark()))
                    .map(BsDictData::getDictCd).collect(Collectors.toList());
            branchCdList.addAll(otherBranchCd);
        }
        logger.info(JsonUtil.obj2Json(branchCdList));
        if (CollectionUtils.isNotEmpty(branchCdList)) {
            rptBaseCostVo.setBranchCdList(branchCdList);
        } else {
            throw new ApplicationException("请联系管理员分配业务成本统计查看权限！");
        }
        return rptBaseCostVo;
    }

    @RequestMapping(value = "refreshUserEvectionCost")
    public void refreshUserEvectionCost(HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        String baseStartDate = request.getParameter("baseStartDate");
        String baseEndDate = request.getParameter("baseEndDate");
        if (StringUtils.isEmpty(baseStartDate) || StringUtils.isEmpty(baseEndDate)) {
            RenderUtil.renderFailure("年月范围不可为空!", response);
            return;
        }
        if (!StringUtils.equals(baseStartDate, baseEndDate)) {
            RenderUtil.renderFailure("自动计算出差报销费用，数据不可以跨多月！", response);
            return;
        }
        try {
            rptBaseCostClient.refreshUserEvectionCost(baseStartDate);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("refreshUserEvectionCost:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "showImportExcel")
    public String showImportExcel(Model model) {
        return "bas/rptBaseCost-excel";
    }

    /**
     *  根据业务成本统计导入的excel 获取年月，判断该年月是否存在数据
     * @param request
     * @param response
     */
    @RequestMapping(value = "getCostbaseByImportExcel")
    public void getCostbaseByImportExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileIds = request.getParameter("fileId");
            if(StringUtils.isNotBlank(fileIds)){
                String[] split = fileIds.split(",");
                String results = "0";
                for (String fileId : split) {
                    results = rptBaseCostClient.getCostbaseByImportExcel(fileId);
                }
                RenderUtil.renderJson(results,response);
            } else {
                RenderUtil.renderFailure("文件id为空", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
    }

    /**
     * 业务成本统计数据导入
     * @param request
     * @param response
     */
    @RequestMapping(value = "importExcel")
    public void importExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileIds = request.getParameter("fileId");
            if(StringUtils.isNotBlank(fileIds)){
                String[] split = fileIds.split(",");
                List<String> results = new ArrayList<>();
                for (String fileId : split) {
                    results = rptBaseCostClient.importExcel(fileId);
                }
                RenderUtil.renderJson(results,response);
            } else {
                RenderUtil.renderFailure("文件id为空", response);
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
            ErrorResp errorResp = BasicErrorController.getErrorResp(e);
            RenderUtil.renderFailure(errorResp.getMessage(), response);
        }
    }

    /**
     * 业务成本统计导出Excel表格
     * @param rptBaseCostVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportExcel")
    public void exportExcel(RptBaseCostVo rptBaseCostVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        int batchSize = 500;
        rptBaseCostVo.setRows(batchSize);
        initSearch(rptBaseCostVo, request);
        buildSearchParams(rptBaseCostVo);
        Page<RptBaseCostReportVo> page = rptBaseCostClient2.findPage(rptBaseCostVo);
        String tableName = "业务成本统计";
        String[] titles = new String[]{"业务员", "所属区域名称", "业务成本年月", "工资", "提成绩效", "其它费用", "社保", "公积金", "出差报销费用", "合计成本", "备注", "更新时间"};
        String[] attrs = new String[]{"matchUserName", "branchName", "baseDate", "wages", "commission", "otherCost", "socialSecurity", "providentFund", "evectionCost", "totalCost", "remark", "updatedDate"};
        int[] widths = new int[]{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 20};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        Sheet sheet = workbook.createSheet(tableName);
        sheet.setDefaultColumnWidth(15);
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        cellStyle.setWrapText(true);
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (page != null && CollectionUtils.isNotEmpty(page.getContent())) {
            PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, "yyyy/MM/dd");
            if (page.hasNext()) {
                rptBaseCostVo.setPage(rptBaseCostVo.getPage() + 1);
                page = rptBaseCostClient2.findPage(rptBaseCostVo);
                start += batchSize;
            } else {
                page = null;
            }
        }

        try {
            PoiExcelUtil.write(workbook, response, tableName);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response) {
        OutputStream out = null;
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        String fileName = "业务成本统计模版";
        String filePath ="/excel/rptBaseCost.xlsx";
        try {
            // 读取模板
            Resource res = new ClassPathResource(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(res.getInputStream());

            // 转换为字节流
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            in = new ByteArrayInputStream(barray);

            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            out = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("下载模板失败",e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("关闭资源异常",e);
                }
                in = null;
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("关闭资源异常",e);
                }
                out = null;
            }
            if (null != bos) {
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    logger.error("关闭资源异常",e);
                }
                out = null;
            }
        }
    }

}
