package com.spt.bas.web.controller.ctr;

import com.google.common.collect.Maps;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.report.client.remote.IRptCtrContractUnDeliveryClient;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliverySearchVo;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliveryVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发货预警
 *
 * @Author: gaojy
 * @create 2022/4/21 14:22
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/ctr/unDelivery")
public class CtrContractUnDeliveryController extends PageController<CtrContract, BaseVo> {
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IRptCtrContractUnDeliveryClient ctrContractUnDeliveryClient;
    @Autowired
    private IPmProcessClient ProcessClient;

    @Override
    public BaseClient<CtrContract> getService() {
        return ctrContractClient;
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    @RequestMapping(value = "")
    public String init(Model model,HttpServletRequest request) {
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = ProcessClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        String index = request.getParameter("index");
        if("true".equals(index)){
            model.addAttribute("index","index");
            model.addAttribute("payCondition",request.getParameter("payCondition"));
            model.addAttribute("warehouseOutCondition",request.getParameter("warehouseOutCondition"));
            model.addAttribute("contractType",BasConstants.CONTRACTTYPE_SELL);
            model.addAttribute("matchCreditFlg",null);
        } else {
            LocalDate localDate = LocalDate.now().minusDays(1);
            model.addAttribute("deliveryDateTo",localDate.toString());
            model.addAttribute("warehouseInCondition",BasConstants.APPLY_TYPE_WN);
            model.addAttribute("contractType",BasConstants.CONTRACTTYPE_BUY);
            model.addAttribute("matchCreditFlg","true");
        }
        model.addAttribute("productType", request.getParameter("productType"));
        return "ctr/unDelivery";
    }

    @RequestMapping(value = "findUnDeliveryPage")
    public void findUnDeliveryPage(RptCtrContractUnDeliverySearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
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
        PageDown<RptCtrContractUnDeliveryVo> page = ctrContractUnDeliveryClient.findUnDeliveryPage(searchVo);
//        PageDown<CtrContract> page = ctrContractClient.findUnDeliveryPage(searchVo);
        JsonEasyUI.renderJson(response, page, null, null);
    }

    /**
     * 导出功能
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(RptCtrContractUnDeliverySearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        PageDown<RptCtrContractUnDeliveryVo> page = ctrContractUnDeliveryClient.findUnDeliveryPage(searchVo);
        List<RptCtrContractUnDeliveryVo> content = page.getContent();
        for (int i = 0; i < content.size(); i++) {
            RptCtrContractUnDeliveryVo ctrContract = content.get(i);
            // 业务类型
            String businessTypeDcsx = ctrContract.getBusinessTypeDcsx();
            Boolean matchCreditFlg = ctrContract.getMatchCreditFlg();
            if (Boolean.TRUE.equals(matchCreditFlg)){
                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_DCSXBL, businessTypeDcsx)) {
                    ctrContract.setBusinessTypeDcsx("代采赊销保理");
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_DCSX, businessTypeDcsx)) {
                    ctrContract.setBusinessTypeDcsx("代采赊销预算");
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_BL, businessTypeDcsx)) {
                    ctrContract.setBusinessTypeDcsx("保理赊销预算");
                } else if (StringUtils.equals(BasConstants.CONFIG_TYPE_CONTRACT_MODEL_PT, businessTypeDcsx)) {
                    ctrContract.setBusinessTypeDcsx("普通赊销预算");
                } else {
                    ctrContract.setBusinessTypeDcsx("普通赊销预算");
                }
            }else{
                ctrContract.setBusinessTypeDcsx("代采预算");
            }

        }
        String title = "发货预警";
        String[] titles = new String[]{"合同号", "业务类型", "货名", "对方企业名称", "合同数量(吨)", "合同总价(元)", "发货数量(吨)", "发货日期", "逾期发货天数","业务员"};
        String[] attrs = new String[]{"contractNo", "businessTypeDcsx", "productsName", "companyName", "totalNumber", "totalAmount", "warehouseNumber", "deliveryDateFrom","overdueDay", "matchUserName"};
        int[] widths = new int[]{20, 15, 15, 15, 15, 15, 15, 15, 15, 15};
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
                page = ctrContractUnDeliveryClient.findUnDeliveryPage(searchVo);
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
}
