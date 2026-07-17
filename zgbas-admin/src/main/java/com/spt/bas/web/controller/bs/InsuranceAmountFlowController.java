package com.spt.bas.web.controller.bs;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IInsuranceAmountFlowClient;
import com.spt.bas.web.shiro.ShiroUtil;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资金方流水
 */
@Controller
@RequestMapping(value = "/bs/companyDcsx/insuranceAmountFlow")
public class InsuranceAmountFlowController extends PageController<InsuranceAmountFlow, BaseVo> {
    @Autowired
    private IInsuranceAmountFlowClient iInsuranceAmountFlowClient;
    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;

    @Override
    public BaseClient<InsuranceAmountFlow> getService() {
        return iInsuranceAmountFlowClient;
    }

    @RequestMapping(value = "")
    public String findFactory(@RequestParam(name = "fundCompanyId",required = false)Long fundCompanyId, Model model) {
        List<BsCompanyDcsx> bsCompanyDcsxList = bsCompanyDcsxClient.findAll();
        // 获取有效的
        if(CollectionUtils.isNotEmpty(bsCompanyDcsxList)){
            List<BsCompanyDcsx> bsCompanyDcsxes = bsCompanyDcsxList.stream().filter(it -> it.getEnableFlg()).collect(Collectors.toList());
            model.addAttribute("bsCompanyDcsxJson",JsonUtil.obj2Json(bsCompanyDcsxes));
        }
        if(fundCompanyId!=null){
            model.addAttribute("fundCompanyId",fundCompanyId);
        }
        // 保费流水类型
        model.addAttribute("insuranceAmountFlowTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.INSURANCE_AMOUNT_FLOW_TYPE)));
        return "bs/insuranceAmountFlow";
    }

    @RequestMapping(value = "findPage")
    public void findInsuranceAmFlowPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        searchVo.setSort("createdDate");
        searchVo.setOrder("DESC");
        PageDown<InsuranceAmountFlow> page = iInsuranceAmountFlowClient.findPage(searchVo);
        // 设置资金方名称
        List<BsCompanyDcsx> bsCompanyDcsxList = bsCompanyDcsxClient.findAll();
        if(CollectionUtils.isNotEmpty(bsCompanyDcsxList)){
            Map<Long, String> bsCompanyDcsxMap = bsCompanyDcsxList.stream().collect(Collectors.toMap(BsCompanyDcsx::getId, BsCompanyDcsx::getCompanyAbbr));
            if(CollectionUtils.isNotEmpty(page.getContent())){
                page.getContent().stream().forEach(it->{
                    it.setFundCompanyName(bsCompanyDcsxMap.get(it.getFundCompanyId()));
                });
            }
        }
        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "save")
    public void save(InsuranceAmountFlow insuranceAmountFlow,  HttpServletRequest request, HttpServletResponse response) {
       try{
           // 修改资金方保费
           BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.getEntity(insuranceAmountFlow.getFundCompanyId());
           bsCompanyDcsx.setInsuranceAmount(insuranceAmountFlow.getUltimateAmount());
           bsCompanyDcsxClient.save(bsCompanyDcsx);
           // 针对扣款，流水金额为负的
           if(insuranceAmountFlow.getFlowType().equals(BasConstants.DICT_TYPE_INSURANCE_AMFL_D)){
                insuranceAmountFlow.setFlowAmount(insuranceAmountFlow.getFlowAmount().negate());
           }
           // 增加保费流水记录(充值)
           iInsuranceAmountFlowClient.save(insuranceAmountFlow);
           RenderUtil.renderSuccess("保存成功", response);
       }catch (Exception e){
           e.printStackTrace();
           RenderUtil.renderFailure("保存失败", response);
       }
    }
    @RequestMapping(value = "exportExcel")
    public void exportExcel(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        int batchSize = 100000;
        searchVo.setRows(batchSize);
        PageDown<InsuranceAmountFlow> page = iInsuranceAmountFlowClient.findPage(searchVo);
        List<InsuranceAmountFlow> content = page.getContent();
        // 保费流水类型
        List<BsDictData> flowType = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.INSURANCE_AMOUNT_FLOW_TYPE);
        Map<String, String> flowTypeMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(flowType)){
            flowTypeMap = flowType.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName));
        }
        if(CollectionUtils.isNotEmpty(content)){
            Map<String, String> finalFlowTypeMap = flowTypeMap;
            content.stream().forEach(it->{
                // 转换为码值名称
                it.setFlowType(finalFlowTypeMap.get(it.getFlowType()));
            });
        }
        String title = "保费流水";
        String[] titles = new String[]{"流水类型", "流水金额","期初金额", "期末金额","摘要", "日期"};
        String[] attrs = new String[]{"flowType", "flowAmount", "initialAmount", "ultimateAmount", "subject", "updatedDate",};
        int[] widths = new int[]{15, 15, 15, 15, 35, 15};
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
        PoiExcelUtil.createRows(sheet, content, attrs, start, cellStyle, DateOperator.FORMAT_STR);
        try {
            PoiExcelUtil.write(workbook, response, title);

        } catch (IOException e) {
        }
    }
}