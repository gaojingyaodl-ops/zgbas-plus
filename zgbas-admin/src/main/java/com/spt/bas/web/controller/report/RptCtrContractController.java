package com.spt.bas.web.controller.report;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysRoleSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.report.client.entity.RptCtrContractReport;
import com.spt.bas.report.client.remote.IRptCtrContractReportClient;
import com.spt.bas.report.client.vo.RptCtrContractReportSearchVo;
import com.spt.bas.report.client.vo.RptCtrProfitSearchVo;
import com.spt.bas.report.client.vo.RptCtrProfitVo;
import com.spt.bas.report.client.vo.RptCtrTypeProfitVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.bean.ShiroUser;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 已付款未入库明细表
 * 未付款已入库明细表
 * 预售未采购明细表
 * 已收款未出库明细
 */
@Controller
@RequestMapping(value = "/rpt/contractReport")
public class RptCtrContractController extends PageController<CtrContract, BaseVo> {
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IRptCtrContractReportClient ctrContractReportClient;
    @Autowired
    private IPmProcessClient ProcessClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<CtrContract> getService() {
        return ctrContractClient;
    }

    private Model initData(Model model) {
        model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        model.addAttribute("contractStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("applyTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
        model.addAttribute("sellAndBuyStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELLSTATUS)));
        model.addAttribute("contractsTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
        //model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //业务小类
        model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
        model.addAttribute("type", "B");
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = ProcessClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        //获取业务员树
        List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        //预售合同发起采购权限
        Boolean preSellFlg = canStartBuy();
        model.addAttribute("preSellFlg", preSellFlg);
        //确认收货权限
        Boolean confirmFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_CONFIRM.getPermissionCode());
        model.addAttribute("confirmFlg", confirmFlg);
        //签约权限
        Boolean signingFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_SIGNING.getPermissionCode());
        model.addAttribute("signingFlg", signingFlg);
        return model;
    }

    //已付款未入库明细
    @RequestMapping(value = "notDeliveryIn")
    public String notDeliveryIn(Model model) {
        model = initData(model);
        model.addAttribute("searchType", "NI");
        model.addAttribute("contractType", "B");
        return "ctr/contractReport";
    }

    //未付款已入库明细
    @RequestMapping(value = "wasDeliveryIn")
    public String wasDeliveryIn(Model model) {
        model = initData(model);
        model.addAttribute("searchType", "WI");
        model.addAttribute("contractType", "B");
        return "ctr/contractReport";
    }

    //已收款未出库明细
    @RequestMapping(value = "receiveAndNotOut")
    public String receiveAndNotOut(Model model) {
        model = initData(model);
        model.addAttribute("searchType", "RO");
        model.addAttribute("contractType", "S");
        return "ctr/contractReport";
    }

    //未付款已入库明细
    @RequestMapping(value = "preSell")
    public String preSell(Model model) {
        model = initData(model);
        return "ctr/contract-preSell";
    }

    //赊销收款明细
    @RequestMapping(value = "sxReceive")
    public String sxReceive(Model model) {
        model = initData(model);
        return "ctr/contract-sxReceive";
    }

    //毛利率
    @RequestMapping(value = "profitPage/{searchType}")
    public String profitPage(Model model, @PathVariable("searchType") String searchType) {
        model = initData(model);
        model.addAttribute("roiShowFlg","true");
        // 获取当前日期
        Date currentDate = new Date();
        // 计算上月的开始日期
        Date lastMonthStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(currentDate, -1));
        model.addAttribute("lastMonthStart",lastMonthStart);
        // 获取月份的最后一天
        DateTime lastMonthEnd = DateUtil.endOfMonth(lastMonthStart);
        model.addAttribute("lastMonthEnd",lastMonthEnd);
        if (searchType.equals("U")) {
            // 导出权限
            model.addAttribute("permReportMatchUserStatisticsExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_MATCH_USER_STATISTICS_EXPORT.getPermissionCode()));
            // 业务员统计
            return "report/contract-profitUser";
        } else {
            // 导出权限
            model.addAttribute("permReportServiceDataSumExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_SERVICE_DATA_SUM_EXPORT.getPermissionCode()));
            // 业务类型统计
            return "report/contract-profit";
        }

    }

    @RequestMapping(value = "contractList")
    public void contractList(RptCtrContractReportSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        PageDown<RptCtrContractReport> page = ctrContractReportClient.findNotDeliveryInPage(searchVo);
        RptCtrContractReport sum = ctrContractReportClient.findNotDeliveryInPageSum(searchVo);
        Map<String, Object> footer = new HashMap<>();
        footer.put("companyName", "合计");
        footer.put("totalNumber", sum.getTotalNumber());
        footer.put("totalAmount", sum.getTotalAmount());
        footer.put("dealedAmount", sum.getDealedAmount());
        footer.put("billedAmount", sum.getBilledAmount());
        footer.put("warehouseNumber", sum.getWarehouseNumber());
        footer.put("expectDeliveryInNum", sum.getExpectDeliveryInNum());
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    /**
     * 毛利率查询
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findProfitPage")
    public void findProfitPage(RptCtrProfitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        ShiroUser shiroUser = ShiroUtil.getShiroUser();
        List<SysRoleSdk> roles = authOpenFacade.findRoleByUserId(shiroUser.id);
        String roleCds = CollectionUtil.getPropList(roles, "roleKey").toString();
        // 我所负责的部门
        List<Long> myDeptIdList = authOpenFacade.findMyDeptId(shiroUser.id);
        Boolean permissionFlg = true;
        if(CollectionUtils.isEmpty(myDeptIdList)) {
            myDeptIdList = new ArrayList<>();
            permissionFlg = false;
        }

        // 超级管理员查看全部 
        if(roleCds.contains(PermissionEnum.ZGBASADMIN.getPermissionCode()) || ShiroUtil.isPermitted(PermissionEnum.ZGBAS_STATENENT.getPermissionCode())){
            permissionFlg = true;
            myDeptIdList = new ArrayList<>();
            List<Long> deptIdList = searchVo.getDeptId();
            if(CollectionUtils.isNotEmpty(deptIdList)) {
                myDeptIdList = new ArrayList<>();
                for (Long deptId : deptIdList) {
                    myDeptIdList.add(deptId);
                }
            }
            searchVo.setMyDeptIdList(myDeptIdList);
        } else if(ShiroUtil.isPermitted(PermissionEnum.ZGBAS_STATENENT_DEPT.getPermissionCode())){
            searchVo.setMyDeptIdList(myDeptIdList);
            List<Long> deptIdList = searchVo.getDeptId();
            if(CollectionUtils.isNotEmpty(deptIdList)) {
                Boolean myDeptFlg = false;
                myDeptIdList = new ArrayList<>();
                for (Long deptId : deptIdList) {
                    if(myDeptIdList.contains(deptId)){
                        myDeptIdList.add(deptId);
                        myDeptFlg = true;
                    }
                }
                if(!myDeptFlg) {
                    permissionFlg = false;
                }
            } 
        } else {
            permissionFlg = false;
        }
        if(!permissionFlg) {
            myDeptIdList = new ArrayList<>();
            myDeptIdList.add(-1L);
            searchVo.setMyDeptIdList(myDeptIdList);
        }

        String searchType = searchVo.getSearchType();
        if(StringUtils.equals("T",searchType)) {
            PageDown<RptCtrTypeProfitVo> page = ctrContractReportClient.findTypeProfitPage(searchVo);
            RptCtrTypeProfitVo profitSum = ctrContractReportClient.findTypeProfitSum(searchVo);
            Map<String, Object> footer = new HashMap<>();
            footer.put("deptId", "合计");
            footer.put("sellTotalAmount", profitSum.getSellTotalAmount());// 销售总额
            footer.put("buyTotalAmount", profitSum.getBuyTotalAmount());// 采购总额
            footer.put("profit", profitSum.getProfit());// 总毛利
            footer.put("cost", profitSum.getCost());// 总净毛利
            footer.put("margin", profitSum.getMargin());// 总净毛利

            footer.put("businessUserCount", profitSum.getBusinessUserCount());
            footer.put("orderCount", profitSum.getOrderCount());
            footer.put("tonnes", profitSum.getTonnes());
            footer.put("sellMoney", profitSum.getSellMoney());
            footer.put("sellLabor", profitSum.getSellLabor());
            footer.put("gross", profitSum.getGross());
            footer.put("grossLabor", profitSum.getGrossLabor());
            footer.put("grossAvg", profitSum.getGrossAvg());
            JsonEasyUI.renderJson(response, page, null, footer);
        } else {
            PageDown<RptCtrProfitVo> page = ctrContractReportClient.findProfitPage(searchVo);
            RptCtrProfitVo profitSum = ctrContractReportClient.findProfitSum(searchVo);
            Map<String, Object> footer = new HashMap<>();
            footer.put("deptId", "合计");
            footer.put("sellTotalAmount", profitSum.getSellTotalAmount());// 销售总额
            footer.put("buyTotalAmount", profitSum.getBuyTotalAmount());// 采购总额
            footer.put("profit", profitSum.getProfit());// 总毛利
            footer.put("cost", profitSum.getCost());// 总净毛利
            footer.put("margin", profitSum.getMargin());// 总净毛利

            footer.put("orderCount", profitSum.getOrderCount());
            footer.put("tunnage", profitSum.getTunnage());
            footer.put("sellMoney", profitSum.getSellMoney());
            footer.put("gross", profitSum.getGross());
            footer.put("grossAvg", profitSum.getGrossAvg());
            footer.put("totalFinancing", profitSum.getTotalFinancing());
            footer.put("commission", profitSum.getCommission());
            footer.put("evectionCost", profitSum.getEvectionCost());
            footer.put("roi", profitSum.getRoi());
            footer.put("totalCost", profitSum.getTotalCost());

            JsonEasyUI.renderJson(response, page, null, footer);
        }
    }

    @RequestMapping(value = "findProfitByDeptId")
    public void findProfitByDeptId(RptCtrProfitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        searchVo.setSearchType(null);
        searchVo.setCount(-1);
        List<Long> profitByDeptId = ctrContractReportClient.findProfitByDeptId(searchVo);
        List<SysDeptSdk> deptRows = new ArrayList<>();
        for (Long deptId : profitByDeptId) {
            SysDeptSdk sysDept = webParamUtils.getDeptById(deptId);
            deptRows.add(sysDept);
        }
        JsonEasyUI.renderListJson(response, deptRows, null, null);
    }

    @RequestMapping(value = "preSellList")
    public void preSellList(RptCtrContractReportSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        PageDown<RptCtrContractReport> page = ctrContractReportClient.findPreSellPage(searchVo);
        JsonEasyUI.renderJson(response, page, null, null);
    }

    @RequestMapping(value = "sxReceiveList")
    public void sxReceiveList(RptCtrContractReportSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        PageDown<RptCtrContractReport> page = ctrContractReportClient.findSXReceivePage(searchVo);
        JsonEasyUI.renderJson(response, page, null, null);
    }

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptCtrContractReportSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        String searchType = searchVo.getSearchType();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        PageDown<RptCtrContractReport> page = ctrContractReportClient.findNotDeliveryInPage(searchVo);
        Page<RptCtrContractReport> pageVo = preContractData(page);
        String title = "已付款未入库明细";
        String deliveryInTypeName = "预计应入库数量(吨)";
        if (StringUtils.equals("WI", searchType)) {
            deliveryInTypeName = "多入库数量(吨)";
        }

        String[] titles = new String[]{"业务类型", "合同属性", "合同编号", "货名", "我方抬头", "对方企业名称", "交货方式", "合同数量(吨)", "合同总价(元)",
                "付定金日期", "付全款日期", "已付金额(元)", "付款时间", "交货时间", "入库数量(吨)", "入库时间", deliveryInTypeName, "收票金额(元)", "收票时间", "合同状态",
                "合同时间", "业务员"};
        String[] attrs = new String[]{"businessType", "contractAttr", "contractNo", "productsName", "ourCompanyName",
                "companyName", "deliveryMode", "totalNumber", "totalAmount", "payBondTime", "payFullTime",
                "dealedAmount", "lastPayDate", "deliveryDateTo", "warehouseNumber", "lastDeliveryDate",
                "expectDeliveryInNum", "billedAmount", "lastBillDate", "contractStatus", "contractTime",
                "matchUserName"};
        int[] widths = new int[]{15, 15, 15, 20, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 20, 15, 15, 15, 15,
                15};
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
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR_WITH_TIME);
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = ctrContractReportClient.findNotDeliveryInPage(searchVo);
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

    @RequestMapping(value = "/preSellExportExcel")
    @ResponseBody
    public void preSellExportExcel(RptCtrContractReportSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        PageDown<RptCtrContractReport> page = ctrContractReportClient.findPreSellPage(searchVo);
        Page<RptCtrContractReport> pageVo = preContractData(page);
        String title = "预售未采购明细表";

        String[] titles = new String[]{"合同编号", "业务类型", "我方抬头", "对方企业名称", "合同总价(元)", "合同时间", "业务员", "部门"};
        String[] attrs = new String[]{"contractNo", "businessType", "ourCompanyName", "companyName", "totalAmount",
                "contractTime", "matchUserName", "deptId"};
        int[] widths = new int[]{15, 15, 20, 20, 15, 15, 15, 15};
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
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR_WITH_TIME);
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = ctrContractReportClient.findPreSellPage(searchVo);
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

    @RequestMapping(value = "/sxReceiveExportExcel")
    @ResponseBody
    public void sxReceiveExportExcel(RptCtrContractReportSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        PageDown<RptCtrContractReport> page = ctrContractReportClient.findSXReceivePage(searchVo);
        Page<RptCtrContractReport> pageVo = preContractData(page);
        String title = "赊销收款明细表";

        String[] titles = new String[]{"业务类型", "合同编号", "我方抬头", "对方企业名称", "收款日期", "已收金额(元)", "收款金额(元)", "未收金额(元)", "合同总价(元)",
                "业务员", "应收全款日期"};
        String[] attrs = new String[]{"businessType", "contractNo", "ourCompanyName", "companyName", "lastPayDate", "dealedAmount",
                "receiveAmount", "unDealedAmount", "totalAmount", "matchUserName", "payFullTime"};
        int[] widths = new int[]{15, 15, 25, 25, 15, 15, 15, 15, 15, 15, 20};
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
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR_WITH_TIME);
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = ctrContractReportClient.findSXReceivePage(searchVo);
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

    private Page<RptCtrContractReport> preContractData(Page<RptCtrContractReport> pageVo) {
        List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
        if (pageVo != null && pageVo.getContent().size() > 0) {
            for (RptCtrContractReport contractShowVo : pageVo.getContent()) {
                String deptId = contractShowVo.getDeptId();
                contractShowVo.setContractStatus(
                        DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTSTATUS, contractShowVo.getContractStatus()));
                contractShowVo.setContractAttr(
                        DictUtil.getValue(BasConstants.STOCK__CONTRACT_ATTR, contractShowVo.getContractAttr()));
                contractShowVo.setDeliveryMode(DictUtil.getValue(BasConstants.TEMPLATE_CONTENT_DELIVERYMODE,
                        contractShowVo.getDeliveryMode()));
                contractShowVo.setBusinessType(
                        DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESSTYPE, contractShowVo.getBusinessType()));
                for (SysDeptSdk sysDept : deptList) {
                    if (deptId != null && sysDept.getDeptId().equals(Long.valueOf(deptId))) {
                        contractShowVo.setDeptId(sysDept.getDeptName());
                    }
                }

            }
        }
        return pageVo;
    }

    /**
     * 获取是否显示Roi
     * @param searchVo
     * @return
     */
    public Boolean getRoiFlg(RptCtrProfitSearchVo searchVo){
        Date contractTimeStart = searchVo.getContractTimeStart();
        Date contractTimeEnd = searchVo.getContractTimeEnd();
        String noBusinessType = searchVo.getNoBusinessType();

        String baseDateStart = "";
        if(contractTimeStart != null ) {
            baseDateStart = DateUtil.format(contractTimeStart, "YYYY-MM");
        }
        String baseDateEnd = "";
        if(contractTimeEnd != null) {
            baseDateEnd = DateUtil.format(contractTimeEnd, "YYYY-MM");
        }

        Boolean roiDataFlg = false;
        Date nowDate = new Date();
        String nowDateStr = DateUtil.format(nowDate, "YYYY-MM");

        if(StringUtils.isNotBlank(baseDateStart) && StringUtils.equals("Y",noBusinessType)) {
            if(StringUtils.equals(baseDateStart,nowDateStr)) {
                roiDataFlg = true;
            } else if (StringUtils.equals(baseDateStart,baseDateEnd)) {
                roiDataFlg = true;
            }
        }
        return roiDataFlg;
    }
    /**
     * 毛利率导出Exccel表格
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "/profitexportExcel")
    @ResponseBody
    public void profitexportExcel(RptCtrProfitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        
        String searchType = searchVo.getSearchType();
        
        PageDown<RptCtrProfitVo> pageVo = ctrContractReportClient.findProfitPage(searchVo);
        String title = "业务报表查询";
        List<RptCtrProfitVo> content = pageVo.getContent();
        for (int i = 0; i < content.size(); i++) {
            RptCtrProfitVo ctrProfitVo = content.get(i);
            SysDeptSdk sysDept = webParamUtils.getDeptById(ctrProfitVo.getDeptId());
            /* 部门名称 */
            if (sysDept != null) {
                ctrProfitVo.setDeptName(sysDept.getDeptName());
            } else {
                ctrProfitVo.setDeptName("");
            }
            BigDecimal margin = ctrProfitVo.getMargin();//净毛利
            BigDecimal sellTotalAmount = ctrProfitVo.getSellTotalAmount();//销售额
            BigDecimal buyTotalAmount = ctrProfitVo.getBuyTotalAmount();
            BigDecimal profit = ctrProfitVo.getProfit();

            if (margin.compareTo(BigDecimal.ZERO) == 0) {//如果净毛利为0的情况下不执行除法
                BigDecimal decimal = new BigDecimal("0");
                ctrProfitVo.setGrossProfit(decimal);//毛利率
            } else {
                BigDecimal grossProfit = margin.divide(buyTotalAmount, 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal num1 = new BigDecimal("100");
                BigDecimal num2 = grossProfit.multiply(num1);//BigDecimal乘法
                ctrProfitVo.setGrossProfit(num2);//毛利率
            }
            
            if (profit.compareTo(BigDecimal.ZERO) == 0) {//如果毛利为0的情况下不执行除法
                BigDecimal decimal = new BigDecimal("0");
                ctrProfitVo.setProfitRate(decimal);//毛利率
            } else {
                BigDecimal profitRate = profit.divide(buyTotalAmount, 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal num1 = new BigDecimal("100");
                BigDecimal num2 = profitRate.multiply(num1);//BigDecimal乘法
                ctrProfitVo.setProfitRate(num2);//毛利率
            }
        }

        Boolean roiFlg = getRoiFlg(searchVo);

        String[] titles = new String[]{"事业部", "业务类型", "采购额(元)", "销售额(元)", "毛利(元)", "毛利率(%)", "费用(元)", "净毛利(元)", "净毛利率(%)"};
        String[] attrs = new String[]{"deptName", "businessName", "buyTotalAmount", "sellTotalAmount", "profit", "profitRate", "cost", "margin", "grossProfit"};
        int[] widths = new int[]{15, 15, 20, 20, 20, 15, 20, 20, 15};
        if (searchType.equals("U")) {
            title = "业务员报表查询";
            if(roiFlg){
                titles = new String[]{"事业部", "业务类型", "业务员", "订单数量", "采购额(元)", "销售额(元)", "毛利(元)", "毛利率(%)", "合计成本(元)", "净毛利(元)", "净毛利率(%)","订单数", "吨数", "销售额(万元)", "毛利(万元)","毛利率均值","总投入(万元)","提成(万元)","出差(万元)","ROI"};
                attrs = new String[]{"deptName", "businessName", "matchUserName", "orderCount", "buyTotalAmount", "sellTotalAmount", "profit", "profitRate", "totalCost", "margin", "grossProfit", "orderCount", "tunnage", "sellMoney", "gross","grossAvg","totalFinancing","commission","evectionCost","roi" };
                widths = new int[]{15, 15, 15, 15, 20, 20, 20, 15, 20, 20, 15, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            } else {
                titles = new String[]{"事业部", "业务类型", "业务员", "订单数量", "采购额(元)", "销售额(元)", "毛利(元)", "毛利率(%)", "净毛利(元)", "净毛利率(%)"};
                attrs = new String[]{"deptName", "businessName", "matchUserName", "orderCount", "buyTotalAmount", "sellTotalAmount", "profit", "profitRate","margin", "grossProfit"};
                widths = new int[]{15, 15, 15, 15, 20, 20, 20, 15, 20, 15};
            }
        }

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
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR_WITH_TIME);
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                pageVo = ctrContractReportClient.findProfitPage(searchVo);
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

    /**
     * 业务数据汇总 毛利率导出Exccel表格
     *
     * @param searchVo
     * @param request
     * @param response
     * @throws ApplicationException
     */
    @RequestMapping(value = "/profitExportTypeExcel")
    @ResponseBody
    public void profitExportTypeExcel(RptCtrProfitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());

        String searchType = searchVo.getSearchType();

        PageDown<RptCtrTypeProfitVo> pageVo = ctrContractReportClient.findTypeProfitPage(searchVo);
        String title = "业务数据汇总报表查询";
        List<RptCtrTypeProfitVo> content = pageVo.getContent();
        for (int i = 0; i < content.size(); i++) {
            RptCtrTypeProfitVo ctrProfitVo = content.get(i);
            SysDeptSdk sysDept = webParamUtils.getDeptById(ctrProfitVo.getDeptId());
            /* 部门名称 */
            if (sysDept != null) {
                ctrProfitVo.setDeptName(sysDept.getDeptName());
            } else {
                ctrProfitVo.setDeptName("");
            }
            BigDecimal margin = ctrProfitVo.getMargin();//净毛利
            BigDecimal sellTotalAmount = ctrProfitVo.getSellTotalAmount();//销售额
            BigDecimal buyTotalAmount = ctrProfitVo.getBuyTotalAmount();
            BigDecimal profit = ctrProfitVo.getProfit();
            

            if (margin.compareTo(BigDecimal.ZERO) == 0) {//如果净毛利为0的情况下不执行除法
                BigDecimal decimal = new BigDecimal("0");
                ctrProfitVo.setGrossProfit(decimal);//毛利率
            } else {
                BigDecimal grossProfit = margin.divide(buyTotalAmount, 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal num1 = new BigDecimal("100");
                BigDecimal num2 = grossProfit.multiply(num1);//BigDecimal乘法
                ctrProfitVo.setGrossProfit(num2);//毛利率
            }

            if (profit.compareTo(BigDecimal.ZERO) == 0) {//如果毛利为0的情况下不执行除法
                BigDecimal decimal = new BigDecimal("0");
                ctrProfitVo.setProfitRate(decimal);//毛利率
            } else {
                BigDecimal profitRate = profit.divide(buyTotalAmount, 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal num1 = new BigDecimal("100");
                BigDecimal num2 = profitRate.multiply(num1);//BigDecimal乘法
                ctrProfitVo.setProfitRate(num2);//毛利率
            }
            
        }

        Boolean roiFlg = getRoiFlg(searchVo);

        
        String[] titles = new String[]{"事业部", "业务类型", "采购额(元)", "销售额(元)", "毛利(元)", "毛利率(%)", "费用(元)", "净毛利(元)", "净毛利率(%)"};
        String[] attrs = new String[]{"deptName", "businessName", "buyTotalAmount", "sellTotalAmount", "profit", "profitRate", "cost", "margin", "grossProfit"};
        int[] widths = new int[]{15, 15, 20, 20, 20,15, 20, 20, 15};

        if(roiFlg) {
            titles = new String[]{"事业部", "业务类型", "业务员", "采购额(元)", "销售额(元)", "毛利(元)", "毛利率(%)", "费用(元)", "净毛利(元)", "净毛利率(%)", "业务人数", "订单数", "吨数", "销售额(万元)","销售额人效","毛利(万元)","毛利人效","毛利率均值"};
            attrs = new String[]{"deptName", "businessName", "matchUserName", "buyTotalAmount", "sellTotalAmount", "profit", "profitRate", "cost", "margin", "grossProfit", "businessUserCount", "orderCount", "tonnes", "sellMoney","sellLabor","gross","grossLabor","grossAvg"};
            widths = new int[]{15, 15, 15, 20, 20, 20,15, 20, 20, 15, 20, 20, 20, 20, 20, 20, 20, 20};
        }

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
        while (pageVo != null && pageVo.getContent().size() > 0) {
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    DateOperator.FORMAT_STR_WITH_TIME);
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                pageVo = ctrContractReportClient.findTypeProfitPage(searchVo);
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

    private Boolean canStartBuy() {
        Boolean preSellFlg = true;
        String deptId = ShiroUtil.getDeptId().toString();
        //查看所有预售合同权限
        boolean viewPreSell = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode());
        //需要限制预售发起部门ID
        String preSellDeptId = BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.PRESELLDEPTID, BasConstants.DEPTID);
        if (preSellDeptId != null && preSellDeptId.indexOf(deptId) >= 0 && !viewPreSell) {
            preSellFlg = false;
        }
        return preSellFlg;
    }
}

