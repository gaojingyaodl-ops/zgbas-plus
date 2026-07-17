package com.spt.bas.web.controller.ctr;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCtrContractWarnReport;
import com.spt.bas.report.client.remote.IRptCtrContractReportClient;
import com.spt.bas.report.client.vo.RptCtrContractWarnSearchVo;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

@Controller
@RequestMapping(value = "/order/warn")
public class OrderWarnController extends PageController<CtrContract, BaseVo> {
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private IRptCtrContractReportClient ctrContractReportClient;

    @Override
    public BaseClient<CtrContract> getService() {
        return ctrContractClient;
    }
    @RequestMapping(value = "")
    public String init(Model model,HttpServletRequest request) {
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        model.addAttribute("performanceStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTPE_RFOEMACE_STATUS)));
        model.addAttribute("contractAttrJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
        model.addAttribute("statisticsType", request.getParameter("statisticsType"));
        //获取业务员树
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(new DeptSearchVo( ShiroUtil.getEnterpriseId()));
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("productType", request.getParameter("productType"));
        
        return "ctr/orderWarn";
    }

    @RequestMapping(value = "findByOrderWarn")
    public void findByOrderWarn(RptCtrContractWarnSearchVo searchVo, HttpServletResponse response) {
        initAuthority(searchVo);
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
        List<Long> hgMatchUserIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listByCategory)) {
            for (BsDictData bsDictData : listByCategory) {
                try {
                    String dictCd = bsDictData.getDictCd();
                    Long matchUserId = Long.valueOf(dictCd);
                    hgMatchUserIdList.add(matchUserId);
                } catch (Exception e) {
                }
            }
        }
        searchVo.setHgMatchUserIdList(hgMatchUserIdList);
        PageDown<RptCtrContractWarnReport> page = ctrContractReportClient.findRptContractWarnPage(searchVo);
        RptCtrContractWarnReport pageSum = ctrContractReportClient.findRptContractWarnSum(searchVo);
        Map<String, Object> footer = new HashMap<>();
        footer.put("receivableAmount", Objects.isNull(pageSum) ? BigDecimal.ZERO : pageSum.getReceivableAmount());
        footer.put("breachAmount", Objects.isNull(pageSum) ? BigDecimal.ZERO : pageSum.getBreachAmount());
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptCtrContractWarnSearchVo searchVo, HttpServletResponse response){
        int batchSize = 500;
        searchVo.setRows(batchSize);
        initAuthority(searchVo);
        PageDown<RptCtrContractWarnReport> page = ctrContractReportClient.findRptContractWarnPage(searchVo);
        List<RptCtrContractWarnReport> content = page.getContent();
        List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(new DeptSearchVo( ShiroUtil.getEnterpriseId()));
        Map<Long, SysDeptSdk> deptAllMap = new HashMap<>();
        if(Objects.nonNull(deptAll)) {
            deptAllMap = deptAll.stream().collect(Collectors.toMap(SysDeptSdk::getDeptId, vo -> vo));
        }
        for (int i = 0; i < content.size(); i++) {
            RptCtrContractWarnReport ctrContract = content.get(i);
            // 履约状态
            String performanceStatus = ctrContract.getPerformanceStatus();
            if (StringUtils.isNotBlank(performanceStatus)){
                String value = DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTPE_RFOEMACE_STATUS, performanceStatus);
                ctrContract.setPerformanceStatus(value);
            }
            SysDeptSdk deptSdk = deptAllMap.get(ctrContract.getDeptId());
            if(Objects.nonNull(deptSdk)) {
                ctrContract.setDeptName(deptSdk.getDeptName());
            }
        }

        String title = "订单预警";
        String[] titles = new String[]{"合同编号","货名", "企业名称","我方","资金方", "合同金额(元)", "合同数量(吨)", "已收金额(元)", "待收金额(元)", "确认收货日期", "账期", "实际应回款日期", "逾期天数",
                "逾期罚息(元)","开票状态","最晚开票日期", "业务员", "区域", "人保额度(元)", "履约状态","逾期跟进"};
        String[] attrs = new String[]{"contractNo","productNames", "companyName","ourCompanyName","fundCompanyName", "totalAmount", "totalNumber", "receiveAmount", "receivableAmount", "confirmDate", "creditCycle",
                "appointPayFullTime", "breachDays", "breachAmount","invoiceBillName","latestBillDate", "matchUserName", "deptName", "piccCreditAmount","performanceStatus", "notifyContent"};
        int[] widths = new int[]{20,20,20, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,15, 20};
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
                page = ctrContractReportClient.findRptContractWarnPage(searchVo);
                start += batchSize;
            } else {
                page = null;
            }
        }
        try {
            PoiExcelUtil.write(workbook, response, title);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }
    public void initAuthority(RptCtrContractWarnSearchVo searchVo){
        // 业务助理只能查看各自负责的区域
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            String dictLabel = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_BUSINESS_ASSISTANT_DICT, ShiroUtil.getCurrentUserId() + "");
            List<Long> deptIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(dictLabel)) {
                String[] split = dictLabel.split(",");
                // 使用 Stream 将 String 数组转换为 List<Long>
                deptIdList = java.util.Arrays.stream(split)
                        .map(Long::parseLong)  // 将 String 转换为 Long
                        .collect(Collectors.toList());  // 收集到 List<Long>
            } else {
                deptIdList.add(-1L);
            }
            searchVo.setDeptIdList(deptIdList);
        }
    }
}
