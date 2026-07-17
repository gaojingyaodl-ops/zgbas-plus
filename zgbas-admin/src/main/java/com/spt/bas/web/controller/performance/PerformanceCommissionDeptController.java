package com.spt.bas.web.controller.performance;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.PerformanceCommissionDept;
import com.spt.bas.client.remote.IPerformanceCommissionDeptClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 业绩提成-事业部分成
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/2/27 15:36
 */
@Controller
@RequestMapping(value = "/performance/dept")
public class PerformanceCommissionDeptController extends PageController<PerformanceCommissionDept, BaseVo> {
    @Resource
    private IPerformanceCommissionDeptClient performanceCommissionDeptClient;
    @Resource
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<PerformanceCommissionDept> getService() {
        return performanceCommissionDeptClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request){
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("branchList", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.BRANCH_CD)));
        // 人事管理员
        model.addAttribute("isHrAdmin", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_PERSONNER_ADMIN.getPermissionCode()));
        return "performance/dept";
    }

    @RequestMapping(value = "findDeptPage")
    public void findUserPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        if (!ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCE_VIEWALLSETTLEMENT.getPermissionCode())){
            Map<String, Object> searchParams = searchVo.getSearchParams();
            Long currentUserId = ShiroUtil.getCurrentUserId();
            searchParams.put("EQL_userId_OR_EQL_leaderUserId", currentUserId);
        }
        PageDown<PerformanceCommissionDept> page = performanceCommissionDeptClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "initPerformanceDept")
    public void initPerformanceUser(PerformanceCommissionDept vo, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(vo.getPerformanceDate())){
                return;
            }
            performanceCommissionDeptClient.initPerformanceCommissionDept(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("initPerformanceDept:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    /**
     * 事业部分成
     *
     * @return
     */
    @PostMapping("/findPerformanceCommissionDept/{queryType}")
    @ResponseBody
    public PerformanceCommissionDept findPerformanceCommissionDept(@PathVariable String queryType) {
        PerformanceCommissionDept queryVo = new PerformanceCommissionDept();
        queryVo.setLeaderUserId(ShiroUtil.getCurrentUserId());
        if (StringUtils.equals("deptMonth", queryType)) {
            queryVo.setPerformanceDate(getYearMonthStr(0));
        } else if (StringUtils.equals("deptMonth1", queryType)) {
            queryVo.setPerformanceDate(getYearMonthStr(1));
        } else if (StringUtils.equals("deptMonth2", queryType)) {
            queryVo.setPerformanceDate(getYearMonthStr(2));
        }
        PerformanceCommissionDept result = performanceCommissionDeptClient.findPerformanceCommissionDept(queryVo);
        if (Objects.nonNull(result)){
            result.setLeaderLaborCost(safe(result.getLeaderLaborCost()).add(safe(result.getAssistantLaborCost())).add(safe(result.getDeptManageCost())).add(safe(result.getNextMonthBalance())));
        }
        return result;
    }

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        if (!ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCE_VIEWALLSETTLEMENT.getPermissionCode())){
            Map<String, Object> searchParams = searchVo.getSearchParams();
            Long currentUserId = ShiroUtil.getCurrentUserId();
            searchParams.put("EQL_userId_OR_EQL_leaderUserId", currentUserId);
        }
        PageDown<PerformanceCommissionDept> page = performanceCommissionDeptClient.findPage(searchVo);
        Page<PerformanceCommissionDept> pageVo = preContractData(page);
        String title = "销售业务员提成";

        String[] titles = new String[]{"姓名", "年月", "事业部", "事业部净利(元)", "销售提成(元)", "负责人人工成本(元)", "事业部助理人工成本(元)",
                "事业部管理成本(元)", "结存下月成本(元)", "剩余净利(元)", "负责人分成(元)", "当月结算(元)", "当年余额(元)"};
        String[] attrs = new String[]{"leaderUserName", "performanceDate", "owningRegion","deptNetProfit", "deptCommission", "leaderLaborCost", "assistantLaborCost",
                "deptManageCost", "nextMonthBalance", "residueNetProfit", "leaderCommission", "leaderMonthCommission", "leaderYearCommission"};
        int[] widths = new int[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR);
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = performanceCommissionDeptClient.findPage(searchVo);
                pageVo = preContractData(page);
                start += batchSize;
            } else {
                pageVo = null;
            }
        }

        try {
            PoiExcelUtil.write(workbook, response, title);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Page<PerformanceCommissionDept> preContractData(Page<PerformanceCommissionDept> page) {
        if (page != null && page.getContent().size() > 0) {
            for (PerformanceCommissionDept user : page.getContent()) {
                OwnRegionEnum ownRegionEnum = OwnRegionEnum.getRegionEnumByCode(user.getOwningRegion());
                user.setOwningRegion(Objects.nonNull(ownRegionEnum) ? ownRegionEnum.getRegionName() : "");
            }
        }
        return page;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String getYearMonthStr(Integer num){
        Date date = DateOperator.addMonthes(new Date(), -num);
        return DateTimeFormatter.ofPattern("yyyy-MM")
                .format(date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());
    }
}
