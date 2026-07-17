package com.spt.bas.web.controller.apply;

import cn.hutool.core.collection.CollUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyFundRecharge;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.FundAmountFlow;
import com.spt.bas.client.remote.IApplyFundRechargeClient;
import com.spt.bas.client.remote.IFundAmountFlowClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 资金方充值申请
 * @Author MoonLight
 * @Date 2024/7/15 9:36
 * @Version 1.0
 */
@Controller
@RequestMapping(value = "/apply/fundRecharge")
public class ApplyFundRechargeController extends PageController<ApplyFundRecharge, BaseVo> {
    
    @Resource
    private IApplyFundRechargeClient applyFundRechargeClient;
    @Resource
    private IFundAmountFlowClient fundAmountFlowClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Resource
    private IPmProcessClient pmProcessClient;

    @Override
    public BaseClient<ApplyFundRecharge> getService() {
        return applyFundRechargeClient;
    }
    
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyFundRecharge entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        List<BsCompanyDcsx> fundCompanyList = webParamUtils.queryFundCompanyListWithUser();
        model.addAttribute("fundCompanyListJson", JsonUtil.obj2Json(fundCompanyList));
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        model.addAttribute("funderOurCompanyNameJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_FUNDER_OUR_COMPANY_NAME)));//资金方余额我方抬头
        return "apply/fundRecharge";
    }

    
    @ModelAttribute("preload")
    public ApplyFundRecharge getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyFundRecharge entity = new ApplyFundRecharge();
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        if (id != null && id != 0L) {
            entity = getService().getEntity(id);
        }
        return entity;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyFundRechargeClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "/fundAmountFlow", method = RequestMethod.GET)
    public String fundAmountFlow(Model model, HttpServletRequest request) {
        List<BsCompanyDcsx> fundCompanyList = webParamUtils.queryFundCompanyListWithUser();
        PmProcess fundRechargeProcess = pmProcessClient.findByProcessCode(new PmProcessSearchVo(BasConstants.PROCESS_APPLY_FUND_RECHARGE, ShiroUtil.getEnterpriseId()));
        fundRechargeProcess = Objects.isNull(fundRechargeProcess) ? new PmProcess() : fundRechargeProcess;
        model.addAttribute("fundRechargeProcess", fundRechargeProcess);
        model.addAttribute("fundCompanyListJson", JsonUtil.obj2Json(fundCompanyList));
        model.addAttribute("fundTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.FUND_FLOW_TYPE)));
        model.addAttribute("funderOurCompanyNameJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_FUNDER_OUR_COMPANY_NAME)));//资金方余额我方抬头

        String type = request.getParameter("type");
        if (StringUtils.equals("qg",type)) {
            model.addAttribute("ourCompanyNameStr",BasConstants.COMPANY_NAME_QDZG);
        } else if (StringUtils.equals("ws",type)) {
            model.addAttribute("ourCompanyNameStr",BasConstants.COMPANY_NAME_WSNB);
        } else {
            model.addAttribute("ourCompanyNameStr",null);
        }
        return "fund/fundAmountFlow";
    }

    @RequestMapping(value = "/findFundFlowPage")
    public void findFundFlowPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<FundAmountFlow> page = fundAmountFlowClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        PageDown<FundAmountFlow> page = fundAmountFlowClient.findPage(searchVo);
        Map<String, String> fundFlowTypeMap = buildFundFlowType();
        String title = "余额流水";
        String[] titles = new String[]{"流水类型", "流水金额", "初期金额", "期末金额", "流水摘要", "更新日期"};
        String[] attrs = new String[]{"flowType", "flowAmount", "initialAmount", "ultimateAmount", "subject", "updatedDate"};
        int[] widths = new int[]{15, 15, 15, 15, 60, 17};
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
            transFlowType(page.getContent(), fundFlowTypeMap);
            PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
            if (page.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = fundAmountFlowClient.findPage(searchVo);
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

    private Map<String, String> buildFundFlowType() {
        List<BsDictData> fundFlowTypeData = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.FUND_FLOW_TYPE);
        if (CollUtil.isEmpty(fundFlowTypeData)) {
            return new HashMap<>();
        }
        return fundFlowTypeData.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName));
    }

    private void transFlowType(List<FundAmountFlow> targetList, Map<String, String> fundTypeMap){
        if (CollUtil.isEmpty(fundTypeMap)){
            return;
        }
        targetList.forEach(flow->{
            flow.setFlowType(fundTypeMap.get(flow.getFlowType()));
        });
    }
}
