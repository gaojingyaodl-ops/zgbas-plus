package com.spt.bas.web.controller.ctr;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrOutInLedger;
import com.spt.bas.client.remote.ICtrOutInLedgerClient;
import com.spt.bas.client.vo.CtrOutInLedgerVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
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
import java.text.ParseException;
import java.util.*;


@Controller
@RequestMapping("/ctr/ledger")
public class CtrLedgerController extends PageController<CtrOutInLedger, BaseVo> {

    @Autowired
    private ICtrOutInLedgerClient ctrOutInLedgerClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<CtrOutInLedger> getService() {
        return ctrOutInLedgerClient;
    }

    @RequestMapping(value = "index")
    public String index(Model model) {
        DeptSearchVo deptSearchVo = new DeptSearchVo();
        deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));

        model.addAttribute("deliveryModeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.TEMPLATE_CONTENT_DELIVERYMODE)));//交货类型
        model.addAttribute("outInLedgerTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_OUT_IN_LEDGER_TYPE)));//出入库台账操作类型
        return "ctr/ctrLedger";
    }

    @RequestMapping(value = "selectByPage")
    public void selectByPage(CtrOutInLedgerVo ctrOutInLedgerVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(ctrOutInLedgerVo, request);
        logger.info("searchVo : " + JsonUtil.obj2Json(ctrOutInLedgerVo));
        Map<String, Object> footer = new HashMap<>();
        ctrOutInLedgerVo.setSort("approveId");
        ctrOutInLedgerVo.setOrder("DESC");
        Page<CtrOutInLedger> page = ctrOutInLedgerClient.findPage(ctrOutInLedgerVo);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(CtrOutInLedgerVo ctrOutInLedgerVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException, ParseException {
        initSearch(ctrOutInLedgerVo, request);
        int batchSize = 500;
        ctrOutInLedgerVo.setRows(batchSize);
        ctrOutInLedgerVo.setSort("approveId");
        ctrOutInLedgerVo.setOrder("DESC");
        Page<CtrOutInLedger> pageVo = ctrOutInLedgerClient.findPage(ctrOutInLedgerVo);
        pageVo = preCtrOutInLedgerData(pageVo);
        String title = "出入库台账报表";
        String[] titles = new String[]{"时间", "操作类型", "品名", "单价", "交货方式", "系统合同号", "实际合同号",
                "我司名称", "团队", "业务员", "对方单位", "对方传真", "合同数量", "可提数量","出入库数量", "结余数量",
                "承运商名称", "仓库地址/送达地址", "仓库电话","其他费用", "运费", "出库费", "车号", "司机", "身份证号", "电话"};
        //应该是excel列属性
        String[] attrs = new String[]{"operTime", "operation", "productsName", "price", "deliveryMode", "contractNo", "realContractNo",
                "ourCompanyName", "deptName", "matchUserName", "companyName", "companyFax", "totalNumber", "extractNumber", "warehouseNumber", "surplusNumber",
                "carrier", "deliveryAddr", "deliveryPhone", "otherAmount", "transportAmount", "deliveryOutFee","plateNumber", "driverName", "driverCardNo", "driverPhone"};
        Integer[] widths = new Integer[]{15, 15, 30, 15, 15, 15, 15, 30, 20, 15, 30, 20, 15, 15, 15, 15, 30, 30, 15, 15, 15, 15, 30, 30, 30, 30};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        // 设置可以换行
        cellStyle.setWrapText(true);

        // 创建表头
        int[] widthes = new int[titles.length];
        for (int i = 0; i < titles.length; i++) {
            widthes[i] = widths[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
        int start = 0;
        while (pageVo != null && pageVo.getContent().size() > 0) {
            //应该是读取的数据
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                ctrOutInLedgerVo.setPage(ctrOutInLedgerVo.getPage() + 1);
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

    private Page<CtrOutInLedger> preCtrOutInLedgerData(Page<CtrOutInLedger> pageVo) {
        if (pageVo != null && pageVo.getContent().size() > 0) {
            DeptSearchVo deptSearchVo = new DeptSearchVo();
            deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
            List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
            for (CtrOutInLedger ledger : pageVo.getContent()) {
                ledger.setDeliveryMode(
                        BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.TEMPLATE_CONTENT_DELIVERYMODE, ledger.getDeliveryMode()));
                ledger.setOperation(
                        BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.DICT_OUT_IN_LEDGER_TYPE, ledger.getOperation()));
                if (Objects.nonNull(ledger.getDeptId())) {
                    SysDeptSdk sysDeptSdk = deptList.stream().filter(dept -> Objects.equals(ledger.getDeptId(), dept.getDeptId())).findFirst().orElse(null);
                    if (Objects.nonNull(sysDeptSdk)) {
                        ledger.setDeptName(sysDeptSdk.getDeptName());
                    }
                }
            }
        }
        return pageVo;
    }
}
