package com.spt.bas.web.controller.ctr;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.LitigationCase;
import com.spt.bas.client.remote.ILitigationCaseClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping(value = "/ctr/litigationCase")
public class CtrLitigationCaseController extends SingleCrudControll<LitigationCase, BaseVo> {
    @Autowired
    private ILitigationCaseClient litigationCaseClient;
    @Autowired
    private IPmProcessClient processClient;

    @Override
    public BaseClient<LitigationCase> getService() {
        return litigationCaseClient;
    }

    @RequestMapping(value = "")
    public String init(Model model, HttpServletRequest request) {
        return "ctr/litigationCase";
    }

    @PostMapping("findLitigationCaseByPage")
    public void findSellContractExecutionPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<LitigationCase> page = litigationCaseClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }

    /**
     * @param type  A 新增 U 修改
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "skipUpdateLitigationCase", method = RequestMethod.GET)
    public String recharge(@RequestParam("type") String type, @RequestParam(value = "id", required = false) Long id, Model model) {
        LitigationCase litigationCase = null;
        if (id == null) {
            litigationCase = new LitigationCase();
        } else {
            litigationCase = litigationCaseClient.getEntity(id);
        }
        model.addAttribute("entity", litigationCase);
        model.addAttribute("type", type);
        return "ctr/litigationCaseUpdate";
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(LitigationCase litigationCase, Model model, HttpServletResponse response) {
        if (litigationCase.getId() == null) {
            litigationCase.setAttorneyApproveStatus(BasConstants.APPROVE_STATUS_N);
            litigationCase.setProcessingApproveStatus(BasConstants.APPROVE_STATUS_N);
            litigationCase.setPreservationApproveStatus(BasConstants.APPROVE_STATUS_N);
            litigationCase.setLiabilityApproveStatus(BasConstants.APPROVE_STATUS_N);
        }
        litigationCaseClient.save(litigationCase);
        RenderUtil.renderSuccess("保存成功", response);
    }

    @RequestMapping(value = "feeApply", method = RequestMethod.GET)
    public String feeApply(@RequestParam(value = "id") Long id, Model model) {
        LitigationCase entity = litigationCaseClient.getEntity(id);
        model.addAttribute("entity", entity);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        return "ctr/litigationFeeApply";
    }

    @RequestMapping(value = "exportExcel")
    public void exportExcel(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        int batchSize = 50;
        searchVo.setRows(batchSize);
        PageDown<LitigationCase> page = litigationCaseClient.findPage(searchVo);
        handleExport(page.getContent());
        String title = "诉讼案件";
        String[] titles = new String[]{"关联合同号", "是否结案", "接洽律师", "判决时间", "案号", "管辖法院",
                "律师费", "案件受理费", "保全费", "诉责费", "诉讼费用合计", "案件进展跟进"};
        String[] attrs = new String[]{"linkContractNos", "liabilityApproveStatus", "lawyer", "judgmentDate", "caseNo", "competentCourt",
                "attorneyFee", "processingFee", "preservationFee", "liabilityFee", "totalFee", "caseFollow"};
        int[] widths = new int[]{30, 15, 15, 15, 15, 15, 15,
                15, 15, 15, 15, 30};
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
                page = litigationCaseClient.findPage(searchVo);
                handleExport(page.getContent());
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
    public void handleExport(List<LitigationCase> content){
        if(CollectionUtils.isNotEmpty(content)){
            content.stream().forEach(it->{
                // 只存放展示，用于导出
                if(it.getSettleFlag()){
                    it.setLiabilityApproveStatus("已结案");
                } else{
                    it.setLiabilityApproveStatus("未结案");
                }
            });
        }
    }
}
