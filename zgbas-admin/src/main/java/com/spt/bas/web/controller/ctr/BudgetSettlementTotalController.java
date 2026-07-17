package com.spt.bas.web.controller.ctr;


import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.entity.BudgetSettlementTotal;
import com.spt.bas.client.remote.IBudgetSettlementTotalClient;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Controller
@RequestMapping(value = "/budget/settlementTotal")
public class BudgetSettlementTotalController extends PageController<BudgetSettlementTotal, BaseVo> {
    @Autowired
    private IBudgetSettlementTotalClient budgetSettlementTotalClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<BudgetSettlementTotal> getService() {
        return budgetSettlementTotalClient;
    }

    @RequestMapping(value = "")
    public String index(Model model){
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));

        UserSearchVo userSearchVo = new UserSearchVo(ShiroUtil.getEnterpriseId(), false);
        List<SysUserSdk> userAll = authOpenFacade.findUserAll(userSearchVo);
        model.addAttribute("userJson", JsonUtil.obj2Json(userAll));
        LocalDate now = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        model.addAttribute("createDate", now.plusMonths(-1).format(dateTimeFormatter));
        return "ctr/settlementTotal";
    }
    @RequestMapping(value = "findSettleTotalPage")
    public void findSettleTotalPage(PageSearchVo searchVo,HttpServletRequest request,HttpServletResponse response){
        initSearch(searchVo,request);
        Map<String, Object> searchParams = searchVo.getSearchParams();
        String budgetSettlementId = (String) searchParams.get("budgetSettlementId");
        String replace = budgetSettlementId.replace("-", "");

        searchParams.put("EQS_budgetSettlementId",replace);
        searchParams.remove("budgetSettlementId");
        PageDown<BudgetSettlementTotal> page = budgetSettlementTotalClient.findSettlementPage(searchVo);
        Map<String, Object> footer = new HashMap<>();
        BudgetSettlementTotal sum = budgetSettlementTotalClient.sumPageSettlement(searchVo);
        footer.put("matchUserId", "合计");
        footer.put("totalAmount", sum.getTotalAmount());
        footer.put("sellCommissionAmount1", sum.getSellCommissionAmount1());
        footer.put("buyCommissionAmount1", sum.getBuyCommissionAmount1());
        footer.put("sellDirectorCommissionAmount1", sum.getSellDirectorCommissionAmount1());
        footer.put("buyDirectorCommissionAmount1", sum.getBuyDirectorCommissionAmount1());
        footer.put("sellCommissionAmount2", sum.getSellCommissionAmount2());
        footer.put("buyCommissionAmount2", sum.getBuyCommissionAmount2());
        footer.put("sellDirectorCommissionAmount2", sum.getSellDirectorCommissionAmount2());
        footer.put("buyDirectorCommissionAmount2", sum.getBuyDirectorCommissionAmount2());
        JsonEasyUI.renderJson(response, page,null,footer);
    }

    @RequestMapping(value = "createSettleTotal")
    public void createSettleTotal(HttpServletRequest request, HttpServletResponse response) {
        try {
            String summaryDate = request.getParameter("summaryDate");
            budgetSettlementTotalClient.createSettleTotal(summaryDate);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("createSettleTotal:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }


    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setSort("settleStatus");
        searchVo.setOrder("ASC");
        Map<String, Object> searchParams = searchVo.getSearchParams();
        String budgetSettlementId = (String) searchParams.get("budgetSettlementId");
        String replace = budgetSettlementId.replace("-", "");

        searchParams.put("EQS_budgetSettlementId",replace);
        searchParams.remove("budgetSettlementId");
        PageDown<BudgetSettlementTotal> page = budgetSettlementTotalClient.findPage(searchVo);
        Page<BudgetSettlementTotal> pageVo = preContractData(page);
        String title = "提成汇总表";

        String[] titles = new String[] { "劳务关系", "业务员", "赊销销售提成", "赊销采购提成", "赊销销售团队负责人提成", "赊销采购团队负责人提成",
                "代采销售提成", "代采采购提成", "代采销售团队负责人提成", "代采采购团队负责人提成", "合计"};
        String[] attrs = new String[] {"settleStatus", "budgetSettlementId", "sellCommissionAmount1", "buyCommissionAmount1","sellDirectorCommissionAmount1",
                "buyDirectorCommissionAmount1", "sellCommissionAmount2", "buyCommissionAmount2", "sellDirectorCommissionAmount2", "buyDirectorCommissionAmount2",
                "totalAmount"};
        int[] widths = new int[] { 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};
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
                page =  budgetSettlementTotalClient.findPage(searchVo);
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

    private Page<BudgetSettlementTotal> preContractData(Page<BudgetSettlementTotal> page){
        if(page != null && page.getContent().size() > 0){
            for (BudgetSettlementTotal settlement : page.getContent()) {
                Long matchUserId = settlement.getMatchUserId();
                SysUserSdk SysUserSdk = authOpenFacade.findUserById(matchUserId);
                String name = Objects.nonNull(SysUserSdk) ? SysUserSdk.getNickName() : "";
                settlement.setBudgetSettlementId(name);
            }
        }
        return page;
    }

}
