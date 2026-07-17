package com.spt.bas.web.controller.ctr;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Maps;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.bas.client.remote.ICtrContractSettlementClient;
import com.spt.bas.client.vo.BudgetSettlementOphisSearchVo;
import com.spt.bas.client.vo.BudgetSettlementOphisVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务结算单
 * @Author: gaojy
 * @create 2022/4/5 23:29
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/ctr/settlement")
public class CtrContractSettlementController extends PageController<CtrContractSettlement, BaseVo> {
    @Autowired
    private ICtrContractSettlementClient settlementClient;

    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<CtrContractSettlement> getService() {
        return settlementClient;
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String,Object> map = Maps.newHashMap();
        map.put("EQB_enableFlg",true);
        return map;
    }

    @RequestMapping(value = "")
    public String index(Model model,HttpServletRequest request){
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
        // 人事管理员
        model.addAttribute("isAdmin", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_PERSONNER_ADMIN.getPermissionCode()));
        // 事业部负责人(高管)
        model.addAttribute("executiveFlg", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_EXECUTIVES_USER.getPermissionCode()));
        // 结算权限
        model.addAttribute("permissionsSettlement", ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCE_SETTLEMENT.getPermissionCode()));
        // 决算权限
        model.addAttribute("finalAccountFlg", ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCE_SETTLEMENT.getPermissionCode()));
        // 当前日期
        LocalDate now = LocalDate.now();
        DateTimeFormatter partten = DateTimeFormatter.ofPattern("yyyy-MM");
        // 首页跳转标记：1-累计未结算提成，2-预计提成，3-上个月结算提成
        String flag = request.getParameter("indexFlag");
        model.addAttribute("indexFlag",flag);
        model.addAttribute("settlementStatus",StringUtils.isNotBlank(flag) ? getSettleStatus(flag) : null);
        // 如果是累计未结算和预计提成，则显示本月
        if("1".equals(flag) || "2".equals(flag)){
            // 上个月结算提成
            model.addAttribute("createDate",now.format(partten));
        }else{
            model.addAttribute("createDate",now.plusMonths(-1).format(partten));
        }
        return "ctr/settlement";
    }

    //合同详情 上下家信息展示
    @RequestMapping(value = "findSettlementDetail/{settlementId}", method = RequestMethod.GET)
    public String findSettlementDetail(@PathVariable("settlementId") Long settlementId, Model model) {
        CtrContractSettlement entity = settlementClient.getEntity(settlementId);
        model.addAttribute("contract_id", entity.getSellContractId());
        model.addAttribute("settlement_id", settlementId);
        return "ctr/settlement-detail";
    }

    //合同详情 上下家信息展示
    @RequestMapping(value = "findSettlementParticulars/{settlementId}", method = RequestMethod.GET)
    public String findSettlementParticulars(@PathVariable("settlementId") Long settlementId, Model model) {
        CtrContractSettlement entity = settlementClient.getEntity(settlementId);
        model.addAttribute("contract_id", entity.getSellContractId());
        model.addAttribute("entity", entity);
        model.addAttribute("settlement_id", settlementId);
        return "ctr/settlement-particulars";
    }

    @RequestMapping(value = "detailPage/{settlementId}", method = RequestMethod.POST)
    public void detailHisPage(@PathVariable("settlementId") Long settlementId, HttpServletResponse response) {
        List<CtrContractSettlementCommission> resultList = settlementClient.findSettlementDetail(settlementId);
        JsonEasyUI.renderListJson(response, resultList);
    }

    /**
     * 获取结算状态
     * @param flag 首页跳转标记
     * @return 结算状态
     */
    private String getSettleStatus(String flag) {
        if("1".equals(flag)){
            // 未结算提成 - 本月未审核
            return "2";
        }else if("2".equals(flag)){
            // 预计提成 - 本月已审核/ 上个月审核
            return "";
        }else if("3".equals(flag)){
            return "3";
        }
        return null;
    }

    @RequestMapping(value = "findSettlementPage")
    public void findSettlementPage(BudgetSettlementOphisSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        // 首页跳转过来的
        String indexFlag = request.getParameter("indexFlag");
        initSearch(searchVo, request);
        Long currentUserId = ShiroUtil.getCurrentUserId();

        Map<String, Object> searchParams = searchVo.getSearchParams();
        String createdDate = String.valueOf(searchParams.get("createdDate"));
        if (StringUtils.isNotBlank(createdDate)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date parse = sdf.parse(createdDate);
            Date lastDayOfMonth = DateOperator.getLastDayOfMonth(parse);
            Date firstDayOfMonth = DateOperator.getFirstDayOfMonth(parse);
            searchParams.put("GTED_summaryDate", firstDayOfMonth);
            searchParams.put("LTED_summaryDate", lastDayOfMonth);
        }
        searchParams.remove("createdDate");
        if (!ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCE_VIEWALLSETTLEMENT.getPermissionCode())){
            searchParams.put("EQL_sellMatchUserId_OR_EQL_buyMatchUserId_OR_EQL_buyHeadUserId_OR_EQL_sellHeadUserId", currentUserId);
        }

        PageDown<CtrContractSettlement> page;
        CtrContractSettlement sum;
        // 如果不为空，说明是首页跳转过来的
        if (StringUtils.isNotBlank(indexFlag)) {
            // 是否拥有查看所有的权限
            if(!ShiroUtil.isPermitted(PermissionEnum.BAS_PERFORMANCECOMMISSION_ALL.getPermissionCode())){
                searchVo.setMatchUserId(currentUserId);
            }
            page = settlementClient.findIndexPage(searchVo);
            sum = settlementClient.sumIndexPage(searchVo);
        } else {
            page = settlementClient.findPage(searchVo);
            sum = settlementClient.sumPageSettlement(searchVo);
        }

        Map<String, Object> footer = new HashMap<>();


        footer.put("sellContractNo", "汇总");
        footer.put("dealNumber", sum.getDealNumber());
        footer.put("sellTotalAmount", sum.getSellTotalAmount());
        footer.put("buyTotalAmount", sum.getBuyTotalAmount());
        footer.put("vatSpreadAmount", sum.getVatSpreadAmount());
        footer.put("vatAmount", sum.getVatAmount());
        footer.put("printAmount", sum.getPrintAmount());
        footer.put("taxesSurchargesAmount", sum.getTaxesSurchargesAmount());
        footer.put("afterTaxSpreadAmount", sum.getAfterTaxSpreadAmount());
        footer.put("sellHeadCommissionAmount", sum.getSellHeadCommissionAmount());
        footer.put("buyHeadCommissionAmount", sum.getBuyHeadCommissionAmount());
        footer.put("sellMatchAmount", sum.getSellMatchAmount());
        footer.put("buyMatchAmount", sum.getBuyMatchAmount());


        JsonEasyUI.renderJson(response, page,null,footer);
    }

    @RequestMapping(value = "saveOtherDeductionsAmount", method = RequestMethod.POST)
    public void saveOtherDeductionsAmount(@RequestBody CtrContractSettlement settlement, HttpServletResponse response) {
        logger.info("更新人:{},更新结算单ID:{},其他扣除项金额:{}", ShiroUtil.getCurrentUserName(), settlement.getId(), settlement.getOtherDeductionsAmount());
        try {
            CtrContractSettlement entity = settlementClient.getEntity(settlement.getId());
            if (Objects.nonNull(entity) && entity.getOtherDeductionsAmount().compareTo(settlement.getOtherDeductionsAmount()) != 0){
                logger.info("原其他扣除项金额:{}", entity.getOtherDeductionsAmount());
                entity.setOtherDeductionsAmount(settlement.getOtherDeductionsAmount());
                settlementClient.save(entity);
                List<Long> settlementIds = new ArrayList<>();
                settlementIds.add(entity.getId());
                settlementClient.refreshSettlement(settlementIds);
            }
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("saveOtherDeductionsAmount:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "markSettlement/{settlementIds}", method = RequestMethod.GET)
    public void markSettlement(@PathVariable("settlementIds") String settlementIds, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(settlementIds)){
                return;
            }
            String[] split = settlementIds.split(",");
            List<Long> settlementIdList = Arrays.stream(split).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
            BudgetSettlementOphisVo vo = new BudgetSettlementOphisVo();
            vo.setMatchUserId(ShiroUtil.getCurrentUserId());
            vo.setMatchUserName(ShiroUtil.getCurrentUserName());
            vo.setStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_D);
            vo.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_1);
            vo.setSettlementIds(settlementIdList);
            settlementClient.updateSettlementOphis(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("markSettlement:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }
    //修改为结算状态已审批
    @RequestMapping(value = "updateSettleStatus/{settlementIds}", method = RequestMethod.GET)
    public void updateSettleStatus(@PathVariable("settlementIds") String settlementIds, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(settlementIds)){
                return;
            }
            String[] split = settlementIds.split(",");
            List<Long> settlementIdList = Arrays.stream(split).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
            BudgetSettlementOphisVo vo = new BudgetSettlementOphisVo();
            vo.setMatchUserId(ShiroUtil.getCurrentUserId());
            vo.setMatchUserName(ShiroUtil.getCurrentUserName());
            vo.setStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_D);
            vo.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_2);
            vo.setSettlementIds(settlementIdList);
            settlementClient.updateSettlementOphis(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("markSettlement:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    /**
     * 修改已审核为已结算状态
     * @param settlementIds 选中的id
     * @param response 返回状态
     */
    @RequestMapping(value = "/updateSettlement/{settlementIds}", method = RequestMethod.GET)
    public void updateSettlement(@PathVariable("settlementIds") String settlementIds, HttpServletResponse response) {
        if(StringUtils.isBlank(settlementIds)){
            return;
        }
        String[] split = settlementIds.split(",");
        List<Long> settlementIdList = Arrays.stream(split).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
        BudgetSettlementOphisVo vo = new BudgetSettlementOphisVo();
        vo.setMatchUserId(ShiroUtil.getCurrentUserId());
        vo.setMatchUserName(ShiroUtil.getCurrentUserName());
        vo.setStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_D);
        vo.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_3);
        vo.setSettlementIds(settlementIdList);
        settlementClient.updateSettlementOphis(vo);
        RenderUtil.renderSuccess("success", response);

    }

    @RequestMapping(value = "/finalAccount", method = RequestMethod.GET)
    public void finalAccount(HttpServletRequest request, HttpServletResponse response) {
        try {
            String summaryDate = request.getParameter("summaryDate");
            String sellMatchUserId = request.getParameter("sellMatchUserId");
            String sellMatchUserName = request.getParameter("sellMatchUserName");
            logger.info("决算: operateName:{},finalAccountDate:{}, userId:{}, userName:{}", ShiroUtil.getCurrentUserName(), summaryDate, sellMatchUserId, sellMatchUserName);
            if (StringUtils.isBlank(summaryDate)) {
                RenderUtil.renderFailure("决算年月不可为空", response);
                return;
            }
            CtrContractSettlement settlement = new CtrContractSettlement();
            settlement.setSummaryDate(DateOperator.parse(summaryDate, "yyyy-MM"));
            settlement.setSellMatchUserId(NumberUtil.isNumber(sellMatchUserId) ? Long.valueOf(sellMatchUserId) : null);
            settlement.setSellMatchUserName(sellMatchUserName);
            settlementClient.finalAccount(settlement);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("finalAccount:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "refreshSettlement/{settlementIds}", method = RequestMethod.GET)
    public void refreshSettlement(@PathVariable("settlementIds") String settlementIds, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(settlementIds)){
                return;
            }
            String[] split = settlementIds.split(",");
            List<Long> settlementIdList = Arrays.stream(split).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
            logger.info("结算单重新计算: userName:{}, settlementIds:{}", ShiroUtil.getCurrentUserName(), settlementIds);
            settlementClient.refreshSettlement(settlementIdList);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("refreshSettlement:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(BudgetSettlementOphisSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        Long currentUserId = ShiroUtil.getCurrentUserId();
        Map<String, Object> searchParams = searchVo.getSearchParams();
        String createdDate = String.valueOf(searchParams.get("createdDate"));
        if (StringUtils.isNotBlank(createdDate)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date parse = sdf.parse(createdDate);
            Date lastDayOfMonth = DateOperator.getLastDayOfMonth(parse);
            Date firstDayOfMonth = DateOperator.getFirstDayOfMonth(parse);
            searchParams.put("GTED_summaryDate", firstDayOfMonth);
            searchParams.put("LTED_summaryDate", lastDayOfMonth);
        }
        searchParams.remove("createdDate");
        if (!ShiroUtil.isPermitted(PermissionEnum.ZGBAS_PERSONNER_ADMIN.getPermissionCode())){
            searchParams.put("EQL_sellMatchUserId_OR_EQL_buyMatchUserId_OR_EQL_buyHeadUserId_OR_EQL_sellHeadUserId", currentUserId);
        }
        PageDown<CtrContractSettlement> page = settlementClient.findPage(searchVo);
        Page<CtrContractSettlement> pageVo = preContractData(page);
        String title = "提成明细表";

        String[] titles = new String[]{"业务类型", "合同号", "合同年月", "是否标记供应商", "供应商", "客户", "销售人员", "采购人员", "结算状态", "结算日期", "合同数量",
                "采购单价", "销售单价", "销售总额", "采购总额", "付款日期", "实际收款日期", "约定付款日期", "实际收货日期", "金融服务账期",
                "金融服务费", "运输费", "仓储费", "装卸费", "逾期天数", "逾期罚息", "业务员逾期罚息", "保险税率", "增值税税后差价", "增值税",
                "印花税", "税金及附加", "贴现费用", "其他扣除项费用", "利润(税后差价收入)", "销售团队负责人分成", "采购中心副总经理分成",
                "供应商资源负责人分成", "销售人员分成", "采购人员分成", "已结算金额", "待结算金额"};
        String[] attrs = new String[]{"businessType", "sellContractNo", "contractTime", "markSupplierFlag", "buyCompanyName", "sellCompanyName",
                "sellMatchUserName", "buyMatchUserName", "settleStatus", "settlementDate", "dealNumber", "buyPrice", "sellPrice",
                "sellTotalAmount", "buyTotalAmount", "payFullTime", "receiveDate", "appointPayDate", "confirmReceiptDate",
                "financialCreditDays", "financialServiceAmount", "transportAmount", "warehouseAmount", "steveDorageAmount", "breachDay",
                "breachAmount", "matchBreachAmount", "insuranceRate", "vatSpreadAmount", "vatAmount", "printAmount", "taxesSurchargesAmount",
                "discountAmount", "otherDeductionsAmount", "afterTaxSpreadAmount", "sellHeadCommissionAmount", "buyHeadCommissionAmount",
                "supplierManagerAmount", "sellMatchAmount", "buyMatchAmount", "hasSettlementAmount", "noneSettlementAmount"};
        int[] widths = new int[]{15, 15, 15, 25, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
                15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};
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
                page = settlementClient.findPage(searchVo);
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

    private Page<CtrContractSettlement> preContractData(Page<CtrContractSettlement> page){
        if(page != null && page.getContent().size() > 0){
            for (CtrContractSettlement settlement : page.getContent()) {
                String businessTypeStr = "其它";
                String businessType = settlement.getBusinessType();
                String status = settlement.getSettleStatus();
                Boolean matchCreditFlg = settlement.getMatchCreditFlg();
                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.TRUE.equals(matchCreditFlg)) {
                    businessTypeStr = "赊销";
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.FALSE.equals(matchCreditFlg)) {
                    businessTypeStr = "代采";
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType) && Boolean.FALSE.equals(matchCreditFlg)) {
                    businessTypeStr = "自营";
                }else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType) && Boolean.FALSE.equals(matchCreditFlg)) {
                    businessTypeStr = "托盘";
                }
                if (StringUtils.equals("1", status)) {
                    settlement.setSettleStatus("已确认");
                } else if (StringUtils.equals("2", status)) {
                    settlement.setSettleStatus("已审核");
                } else if (StringUtils.equals("3", status)) {
                    settlement.setSettleStatus("已结算");
                } else {
                    settlement.setSettleStatus("未确认");
                }
                settlement.setBusinessType(businessTypeStr);

            }
        }
        return page;
    }
}
