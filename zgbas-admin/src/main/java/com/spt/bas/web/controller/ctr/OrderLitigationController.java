package com.spt.bas.web.controller.ctr;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.ApproveWaitDeal;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApproveWaitDealClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.ContractOrderVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.report.client.entity.RptCtrContractWarnReport;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/order/litigation")
public class OrderLitigationController extends PageController<CtrContract, BaseVo> {
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IPmProcessClient ProcessClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IApproveWaitDealClient approveWaitDealClient;
    @Override
    public BaseClient<CtrContract> getService() {
        return ctrContractClient;
    }
    @RequestMapping(value = "")
    public String init(Model model, HttpServletRequest request) {
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = ProcessClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        // 合同履约状态
        List<SysDictDataSdk> listByCategory = DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTPE_RFOEMACE_STATUS);
        // 只获取诉讼和逾期
        List<SysDictDataSdk> collect = listByCategory.stream().filter(it -> it.getDictCd().equals("S") || it.getDictCd().equals("P")).collect(Collectors.toList());
        model.addAttribute("performanceStatusJson", JsonUtil.obj2Json(collect));
        //获取业务员树
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(new DeptSearchVo( ShiroUtil.getEnterpriseId()));
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("performanceStatus", request.getParameter("performanceStatus"));
        model.addAttribute("productType", request.getParameter("productType"));
        
        return "ctr/orderLitigation";
    }

    @RequestMapping(value = "findByLitigation", method = RequestMethod.POST)
    public void findByLitigation(ContractOrderVo searchVo, HttpServletRequest request, HttpServletResponse response) {
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
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        PageDown<CtrContract> page = ctrContractClient.findByLitigation(searchVo);
        CtrContract sum =ctrContractClient.sumByLitigation(searchVo);
        Map<String, Object> footer = new HashMap<>();
        BigDecimal totalAmount = sum.getTotalAmount()==null?BigDecimal.ZERO:sum.getTotalAmount();
        BigDecimal dealedAmount = sum.getDealedAmount()==null?BigDecimal.ZERO:sum.getDealedAmount();
        BigDecimal receivablePrincipal = totalAmount.subtract(dealedAmount);
        BigDecimal confirmReceiveNumber = sum.getConfirmReceiveNumber();
        BigDecimal breachAmount = sum.getBreachAmount();
        footer.put("totalAmount",totalAmount);
        footer.put("dealedAmount",dealedAmount);
        footer.put("receivablePrincipal",receivablePrincipal);
        footer.put("confirmReceiveNumber",confirmReceiveNumber);
        footer.put("breachAmount",breachAmount);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    /**
     * 更新履约状态 -- 违约
     * @param id
     * @param response
     */
    @RequestMapping(value = "updatePerformanceStatus/{id}")
    public void updatePerformanceStatus(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            String status = "P";// 违约
            ctrContractClient.updatePerformanceStatus(id,status);
            ctrContractClient.violateFlgUpdate(id);
            CtrContract entity = ctrContractClient.getEntity(id);
            saveApproveWaitDeal(entity);
            // 生成代办事项通知代恒
            String dh = DictUtil.getValue(BasConstants.DEAL_NOTIFIED_PARTY, "DH");
            entity.setMatchUserId(Long.valueOf(dh));
            saveApproveWaitDeal(entity);
            // 生成代办事项通知张建
            String zj = DictUtil.getValue(BasConstants.DEAL_NOTIFIED_PARTY, "ZJ");
            entity.setMatchUserId(Long.valueOf(zj));
            saveApproveWaitDeal(entity);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 导出功能
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(ContractOrderVo searchVo, HttpServletRequest request, HttpServletResponse response){
        initSearch(searchVo, request);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        int batchSize = 50;
        searchVo.setRows(batchSize);
        PageDown<CtrContract> page = ctrContractClient.findByLitigation(searchVo);
        List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(new DeptSearchVo( ShiroUtil.getEnterpriseId()));
        Map<Long, SysDeptSdk> deptAllMap = new HashMap<>();
        if(Objects.nonNull(deptAll)) {
            deptAllMap = deptAll.stream().collect(Collectors.toMap(SysDeptSdk::getDeptId, vo -> vo));
        }
        List<ContractShowVo> contractShowVoList = preContractData(page, deptAllMap);

        String title = "诉讼管理";
        String[] titles = new String[]{"合同号", "货名", "对方企业名称", "我方", "合同总价(元)", "已收款金额(元)", "待收本金(元)","逾期天数", "逾期罚息", "业务员", "区域", "约定付款日期", "账期", "履约状态", "诉讼费", "保全费", "律师费"};
        String[] attrs = new String[]{"contractNo", "productsName", "companyName", "ourCompanyName", "totalAmount", "dealedAmount", "receivablePrincipal","breachDays", "breachAmount", "matchUserName", "deptName", "appointPayFullTime", "creditCycle", "performanceStatus",
                 "litigationFees", "securityFees", "legalFees"};
        int[] widths = new int[]{20, 15, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,};
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
            PoiExcelUtil.createRows(sheet, contractShowVoList, attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
            if (page.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page = ctrContractClient.findByLitigation(searchVo);
                contractShowVoList = preContractData(page, deptAllMap);
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

    public List<ContractShowVo> preContractData(PageDown<CtrContract> page, Map<Long, SysDeptSdk> deptAllMap){
        List<ContractShowVo> contractShowVoList = new ArrayList<>();
        List<CtrContract> content = page.getContent();
        for (int i = 0; i < content.size(); i++) {
            CtrContract ctrContract = content.get(i);
            ContractShowVo contractShowVo = new ContractShowVo();
            BeanUtils.copyProperties(ctrContract, contractShowVo);
            // 业务类型
            String businessTypeDcsx = contractShowVo.getBusinessTypeDcsx();
            Boolean matchCreditFlg = contractShowVo.getMatchCreditFlg();
            if (Boolean.TRUE.equals(matchCreditFlg)){
                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_DCSXBL, businessTypeDcsx)) {
                    contractShowVo.setBusinessTypeDcsx("代采赊销保理");
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_DCSX, businessTypeDcsx)) {
                    contractShowVo.setBusinessTypeDcsx("代采赊销预算");
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_BL, businessTypeDcsx)) {
                    contractShowVo.setBusinessTypeDcsx("保理赊销预算");
                } else if (StringUtils.equals(BasConstants.CONFIG_TYPE_CONTRACT_MODEL_PT, businessTypeDcsx)) {
                    contractShowVo.setBusinessTypeDcsx("普通赊销预算");
                } else {
                    contractShowVo.setBusinessTypeDcsx("普通赊销预算");
                }
            }else{
                contractShowVo.setBusinessTypeDcsx("代采预算");
            }

            // 履约状态
            String performanceStatus = contractShowVo.getPerformanceStatus();
            if (StringUtils.isNotBlank(performanceStatus)){
                String value = DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTPE_RFOEMACE_STATUS, performanceStatus);
                contractShowVo.setPerformanceStatus(value);
            }
            SysDeptSdk sysDeptSdk = deptAllMap.get(contractShowVo.getDeptId());
            if (Objects.nonNull(sysDeptSdk)) {
                contractShowVo.setDeptName(sysDeptSdk.getDeptName());
            }
            contractShowVo.setReceivablePrincipal(contractShowVo.getTotalAmount().subtract(contractShowVo.getDealedAmount()));

            contractShowVoList.add(contractShowVo);

        }
        return contractShowVoList;
    }

    public Long getDeptLeader() {
        Long deptLeader = 0L;
        try {
            DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getCurrentUserId(), PmConstants.NODE_TYPE_CENTER, ShiroUtil.getEnterpriseId());
            deptLeader = authOpenFacade.findDeptLeader(deptSearchVo);
            logger.info("getDeptLeader : " + JsonUtil.obj2Json(deptLeader));
        } catch (Exception e) {
            logger.error("getDeptLeader error:{}",e);
        }
        return Objects.isNull(deptLeader) ? 0L : deptLeader;
    }

    public void saveApproveWaitDeal(CtrContract searchVo){
        ApproveWaitDeal entity = new ApproveWaitDeal();
        Long matchUserId = searchVo.getMatchUserId();
        entity.setEnterpriseId(ShiroUtil.getEnterpriseId());// 企业账套Id
        entity.setCreatedDate(new Date());//创建时间
        entity.setRelaUserId(String.valueOf(matchUserId));// 负责人
        entity.setDealType(BasConstants.WAIT_DEAL_TYPE_NOTIFY);//事项类型
        entity.setReadFlg(BasConstants.READ_FLG_NOT);//已读状态
        entity.setCompleteFlg("0");//完成状态
        entity.setCreatedUserId(ShiroUtil.getCurrentUserId());
        // 设置待办事项摘要内容
        String str = "合同: "+searchVo.getContractNo()+" 履约状态修改为 : 违约";
        entity.setSubject(str);//摘要
        approveWaitDealClient.save(entity);
    }
}
