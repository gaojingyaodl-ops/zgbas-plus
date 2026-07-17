package com.spt.bas.web.controller.ctr;


import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.DcsxShowVo;
import com.spt.bas.client.vo.UpdateDcsxContractVo;
import com.spt.bas.report.client.remote.IRptCtrContractReportClient;
import com.spt.bas.report.client.vo.RptExportChainVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/ctr/dcsx")
public class CtrDxscController extends PageController<ApplyCtrDCSX, BaseVo> {
    @Autowired
    private IApplyCtrDcsxClinent applyCtrDcsxClinent;
    @Autowired
    public ICtrContractClient ctrContractClient;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsFunderClient bsFunderClient;
    @Autowired
    private IBsCompanyOurClient companyOurClient;
    @Autowired
    private IApplyConfirmReceiptClient applyConfirmReceiptClient;
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;
    @Autowired
    private  IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Autowired
    private IRptCtrContractReportClient ctrContractReportClient;

    @Override
    public BaseClient<ApplyCtrDCSX> getService() {
        return applyCtrDcsxClinent;
    }
    //代采赊销预算
    @RequestMapping(value = "dcsxysList")
    public void dcsxysList(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
        List<Long> hgMatchUserIdList = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listByCategory)) {
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

        Boolean piccRemainCredit = searchVo.getPiccRemainCredit();
        Map<String, Object> searchParams = searchVo.getSearchParams();
        if (piccRemainCredit != null) {
            if (piccRemainCredit) {
                searchParams.put("GTEM_piccRemainCredit", BigDecimal.ZERO);
            } else {
                searchParams.put("LTM_piccRemainCredit", BigDecimal.ZERO);
            }
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            searchParams.put("NEQS_hideOut", "1");
        }
        //中心负责人ID
        Long deptLeader = webParamUtils.getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }

        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
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

        //1.可以查看本中心所有预售合同权限
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        //2.可以查看本中心所有合同权限
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        Boolean saasContractFlg = searchVo.getSaasContractFlg();
        if (saasContractFlg != null && saasContractFlg) {
            searchVo.setAdmin(true);
            searchParams.remove("EQL_enterpriseId");
        }
        setFunderCompany(searchVo);
        logger.info("searchVo : " + JsonUtil.obj2Json(searchVo));

        Map<String, Object> footer = new HashMap<>();
        Map<String,String>companyMap=bsCompanyOurClient.findAll().stream().collect(Collectors.toMap(BsCompanyOur::getCompanyName,BsCompanyOur::getCompanyAbbr,(key1,key2)->key2));
        Map<String,String>companyDcsxMap=bsCompanyDcsxClient.findAll().stream().collect(Collectors.toMap(BsCompanyDcsx::getCompanyName,BsCompanyDcsx::getCompanyAbbr,(key1, key2)->key2));
        Page<DcsxShowVo> page = applyCtrDcsxClinent.findPageContract(searchVo);
        List<BsProductType> productTypeList = productTypeClient.findAll();
        Map<String, BsProductType> productMap = productTypeList.stream().collect(Collectors.toMap(BsProductType::getTypeCode, m -> m, (a, b) -> b));
        List<Long> approveIds = new ArrayList<>();
        page.getContent().stream().forEach(t->{
            approveIds.add(t.getApproveId());
        });
        List<BsCompanyOur> all = companyOurClient.findAll();
        Map<String, BsCompanyOur> companyOurMap = all.stream().collect(Collectors.toMap(BsCompanyOur::getCompanyName, m -> m, (a, b) -> b));
        List<CtrContract> contractList = ctrContractClient.findByApproveIds(approveIds);
        Map<Long, List<CtrContract>> contractMap = contractList.stream().collect(Collectors.groupingBy(CtrContract::getApproveId));
        
        
        page.getContent().stream().forEach(t->{
            List<CtrContract> ctrContractList = contractMap.get(t.getApproveId());
            for (CtrContract ctr : ctrContractList) {
                if(StringUtils.equals(BasConstants.CONTRACTTYPE_BUY,ctr.getContractType())) {
                    break;
                }
            }
            all.stream().filter(b->b.getOurCompanyFlag()==true).forEach(s->{
                if(s.getCompanyName().equals(t.getOurCompanyName())){
                    // 如果我司企业是中游的我方企业（中游的我方是中游的下游企业）
                    // 显示收票按钮，不显示开票
                    t.setSpFlag(true);
                    t.setKpFlag(false);
                }else if(s.getCompanyName().equals(t.getCompanyName())){
                    // 如果我司企业是中游的代采方企业（中游的代采方是中游的上游企业）
                    // 显示开票按钮，不显示收票
                    t.setSpFlag(false);
                    t.setKpFlag(true);
                }
            });
            //供/需方企业替换简称
            String s1=companyDcsxMap.get(t.getCompanyName());
            if(s1!=null){
                t.setCompanyName(s1);
            }
            String s2=companyMap.get(t.getOurCompanyName());
            if(s2!=null){
                t.setOurCompanyName(s2);
            }
            t.setShowConfirmReceiptBotton(getConfirmReceiptFlg(t,companyOurMap,contractMap));
            if(productMap != null) {
                BsProductType productType = productMap.get(t.getProductBrand());
                if(Objects.nonNull(productType)) {
                    t.setProductBrand(productType.getTypeName());
                }
            }
        });
        ApplyCtrDCSX sum = applyCtrDcsxClinent.sumPageContract(searchVo);
        BigDecimal totalAmount = Objects.isNull(sum.getTotalAmount()) ? BigDecimal.ZERO : sum.getTotalAmount();
        BigDecimal dealedAmount = Objects.isNull(sum.getDealedAmount()) ? BigDecimal.ZERO : sum.getDealedAmount();
        footer.put("companyName", "合计");
        footer.put("totalNumber", sum.getTotalNumber());
        footer.put("totalAmount", sum.getTotalAmount());
        footer.put("bondAmount", totalAmount.subtract(dealedAmount));
        footer.put("billedAmount", sum.getBilledAmount());
        footer.put("dealedAmount", sum.getDealedAmount());
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    public Boolean getConfirmReceiptFlg(ApplyCtrDCSX applyCtrDCSX,Map<String, BsCompanyOur> companyOurMap,Map<Long, List<CtrContract>> contractMap) {
        BigDecimal confirmReceiveNumber = null;
        if(Objects.nonNull(applyCtrDCSX)) {
            // 中游已确认收货数量
            confirmReceiveNumber = applyCtrDCSX.getConfirmReceiveNumber();
            BigDecimal totalNumber = applyCtrDCSX.getTotalNumber();
            //  已确认收货数量小于合同数量
            if(confirmReceiveNumber != null && totalNumber != null) {
                if(confirmReceiveNumber.compareTo(totalNumber) >= 0){
                    return false;
                }
            }
            // 判断我方是否处于中游
            if(companyOurMap != null) {
                BsCompanyOur companyOur = companyOurMap.get(applyCtrDCSX.getCompanyName());
                if(Objects.nonNull(companyOur) && !companyOur.getOurCompanyFlag()) {
                    return false;
                }
            }

            if(contractMap != null) {
                List<CtrContract> contractList = contractMap.get(applyCtrDCSX.getApproveId());
                // 下游已出库数量大于中游已确认收货数量
                if(!CollectionUtil.isNullOrEmpty(contractList)) {
                    for (CtrContract contract : contractList) {
                        if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S,contract.getContractType())) {
                            BigDecimal warehouseNumber = contract.getWarehouseNumber();
                            if(warehouseNumber != null && confirmReceiveNumber != null) {
                                if(warehouseNumber.compareTo(confirmReceiveNumber) > 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private void setFunderCompany(ContractSearchVo searchVo){
        if (!ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())){
            return;
        }
        searchVo.setFunderFlg(true);
        String buyCompanyName = "";
        List<BsFunder> bsFunderList = bsFunderClient.findAllByUserId(ShiroUtil.getCurrentUserId());
        if(!CollectionUtils.isEmpty(bsFunderList)){
            buyCompanyName = bsFunderList.get(0).getCompanyNames();
        }
        if (StringUtils.isNotBlank(buyCompanyName)) {
            List<String> companyNameList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(buyCompanyName);
            searchVo.setCompanyNameList(companyNameList);
            Map<String, Object> searchParams = searchVo.getSearchParams();
            searchParams.put("INS_companyName_OR_INS_ourCompanyName", companyNameList);
        }
    }

    @RequestMapping(value = "/exportExcelGrossMargin")
    @ResponseBody
    public void exportExcelGrossMargin(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = webParamUtils.getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
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
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        setFunderCompany(searchVo);
        Page<DcsxShowVo> page = applyCtrDcsxClinent.findPageContract(searchVo);
        List<DcsxShowVo> content = page.getContent();
        BigDecimal number=new BigDecimal(0);
        for (DcsxShowVo dcsxShowVo : content) {
            number=number.add(new BigDecimal(1));
            dcsxShowVo.setNumber(number);
            dcsxShowVo.setCreatedDateml(dcsxShowVo.getCreatedDate());
            dcsxShowVo.setTotalAmountz(dcsxShowVo.getTotalAmount());
            dcsxShowVo.setDealPricez(dcsxShowVo.getDealPrice());
            dcsxShowVo.setPayFullTimez(dcsxShowVo.getPayFullTime());
             List<CtrContract> byApprove = ctrContractClient.findByApproveId(dcsxShowVo.getApproveId());
            BigDecimal  buyprice=new BigDecimal(0.00);
            BigDecimal  sellprice=new BigDecimal(0.00);
             for (CtrContract contract : byApprove) {

                dcsxShowVo.setProductName(contract.getProductsName());
                if (contract.getContractType().equals("B")){
                    if(contract.getDealPrice()==null){
                        sellprice=new BigDecimal(0.00);
                    }
                    buyprice=contract.getDealPrice();
                    dcsxShowVo.setPayFullTime(contract.getPayFullTime());
                    dcsxShowVo.setTotalAmounts(contract.getTotalAmount());
                    dcsxShowVo.setCompanyNames(contract.getCompanyName());
                    dcsxShowVo.setDealPrices(contract.getDealPrice());
                    dcsxShowVo.setTotalNumbers(contract.getTotalNumber());
                }
                if(contract.getContractType().equals("S")){
                    dcsxShowVo.setPayFullTimex(contract.getPayFullTime());
                    if(contract.getDealPrice()==null){
                        sellprice=new BigDecimal(0.00);
                    }
                    sellprice=contract.getDealPrice();
                    //确认收货日期
                    List<ApplyConfirmReceipt> byContract = applyConfirmReceiptClient.findByContractId(contract.getId());
                    if(byContract.size()!=0){
                        dcsxShowVo.setConfirmDate(byContract.get(0).getConfirmReceiptDate());
                    }
                    dcsxShowVo.setTotalAmountx(contract.getTotalAmount());
                    dcsxShowVo.setOurCompanyName(contract.getCompanyName());
                    dcsxShowVo.setDealPricex(contract.getDealPrice());
                }
            }
             BigDecimal sxnumber ;
             if(dcsxShowVo.getTotalNumbers()==null){
                 sxnumber =new BigDecimal(0.00);
             }else{
                 sxnumber=dcsxShowVo.getTotalNumbers();
             }
            BigDecimal  grossProfit=sxnumber.multiply(sellprice.subtract(buyprice));
            dcsxShowVo.setGrossProfit(grossProfit);
        }
        number=new BigDecimal(0);
        Page<DcsxShowVo> pageVo = preContractData(page);
        String title = "代采赊销导出毛利率台账";
        String[] titles = new String[]{"序号", "申请日期", "系统合同编号", "商品信息", "数量", "上游单位名称", "上游单价", "上游总金额(元)",
                "上游合同规定还款日期", "范太克合同单价", "范太克合同总金额", "范太克合同规定还款日期", "下游单位名称", "下游单价", "下游总金额", "下游账款到期日（合同回款日期）", "确认收货日期",
                "毛利","备注"};

       String[] attrs = new String[]{"number", "createdDateml", "contractNo", "productName", "totalNumbers", "companyNames", "dealPrices", "totalAmounts",
                "payFullTime", "dealPricez", "totalAmountz", "payFullTimez", "ourCompanyName", "dealPricex", "totalAmountx", "payFullTimex", "confirmDate",
                "grossProfit","remark"};
        int[] widths = new int[]{5, 15, 20, 20, 10, 35, 10, 20, 25, 20, 25, 25, 35, 10, 25, 25, 25, 25,25};
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
        createHeadsForstartRow(workbook, sheet, titles,  widthes,  1);
        int start = 1;
        while (pageVo != null && pageVo.getContent().size() > 0) {
            //读取的数据
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page =applyCtrDcsxClinent.findPageContract(searchVo);
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
    @RequestMapping(value = "/exportExcelGrossMarginTp")
    @ResponseBody
    public void exportExcelGrossMarginTp(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = webParamUtils.getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
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
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        setFunderCompany(searchVo);
        Page<DcsxShowVo> page = applyCtrDcsxClinent.findPageContract(searchVo);
        List<DcsxShowVo> content = page.getContent();
        BigDecimal number=new BigDecimal(0);
        for (DcsxShowVo dcsxShowVo : content) {
            number=number.add(new BigDecimal(1));
            dcsxShowVo.setNumber(number);
            dcsxShowVo.setCreatedDateml(dcsxShowVo.getCreatedDate());
            dcsxShowVo.setTotalAmountz(dcsxShowVo.getTotalAmount());
            dcsxShowVo.setDealPricez(dcsxShowVo.getDealPrice());
            dcsxShowVo.setPayFullTimez(dcsxShowVo.getPayFullTime());
            List<CtrContract> byApprove = ctrContractClient.findByApproveId(dcsxShowVo.getApproveId());
            BigDecimal  buyprice=new BigDecimal(0.00);
            BigDecimal  sellprice=new BigDecimal(0.00);
            for (CtrContract contract : byApprove) {

                dcsxShowVo.setProductName(contract.getProductsName());
                if (contract.getContractType().equals("B")){
                    if(contract.getDealPrice()==null){
                        sellprice=new BigDecimal(0.00);
                    }
                    buyprice=contract.getDealPrice();
                    dcsxShowVo.setPayFullTime(contract.getPayFullTime());
                    dcsxShowVo.setTotalAmounts(contract.getTotalAmount());
                    dcsxShowVo.setCompanyNames(contract.getCompanyName());
                    dcsxShowVo.setDealPrices(contract.getDealPrice());
                    dcsxShowVo.setTotalNumbers(contract.getTotalNumber());
                }
                if(contract.getContractType().equals("S")){
                    dcsxShowVo.setPayFullTimex(contract.getPayFullTime());
                    if(contract.getDealPrice()==null){
                        sellprice=new BigDecimal(0.00);
                    }
                    sellprice=contract.getDealPrice();
                    //确认收货日期
                    List<ApplyConfirmReceipt> byContract = applyConfirmReceiptClient.findByContractId(contract.getId());
                    if(byContract.size()!=0){
                        dcsxShowVo.setConfirmDate(byContract.get(0).getConfirmReceiptDate());
                    }
                    dcsxShowVo.setTotalAmountx(contract.getTotalAmount());
                    dcsxShowVo.setOurCompanyName(contract.getCompanyName());
                    dcsxShowVo.setDealPricex(contract.getDealPrice());
                }
            }
            BigDecimal sxnumber ;
            if(dcsxShowVo.getTotalNumbers()==null){
                sxnumber =new BigDecimal(0.00);
            }else{
                sxnumber=dcsxShowVo.getTotalNumbers();
            }
            BigDecimal  grossProfit=sxnumber.multiply(sellprice.subtract(buyprice));
            dcsxShowVo.setGrossProfit(grossProfit);
        }
        number=new BigDecimal(0);
        Page<DcsxShowVo> pageVo = preContractData(page);
        String title = "代采赊销导出毛利率台账";
        String[] titles = new String[]{"序号", "申请日期", "系统合同编号", "商品信息", "数量", "上游单位名称", "上游单价", "上游总金额(元)",
                "上游合同规定还款日期", "范太克合同单价", "范太克合同总金额", "范太克合同规定还款日期", "下游单位名称", "下游单价", "下游总金额", "下游账款到期日（合同回款日期）", "确认收货日期",
                "毛利","备注"};

        String[] attrs = new String[]{"number", "createdDateml", "contractNo", "productName", "totalNumbers", "companyNames", "dealPrices", "totalAmounts",
                "payFullTime", "dealPricez", "totalAmountz", "payFullTimez", "ourCompanyName", "dealPricex", "totalAmountx", "payFullTimex", "confirmDate",
                "grossProfit","remark"};
        int[] widths = new int[]{5, 15, 20, 20, 10, 35, 10, 20, 25, 20, 25, 25, 35, 10, 25, 25, 25, 25,25};
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
        createHeadsForstartRow(workbook, sheet, titles,  widthes,  1);
        int start = 1;
        while (pageVo != null && pageVo.getContent().size() > 0) {
            //读取的数据
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page =applyCtrDcsxClinent.findPageContract(searchVo);
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
    public static void createHeadsForstartRow(Workbook wb, Sheet sheet, String[] titles, int[] widthes, int startRow) {
        Row head = sheet.createRow(startRow);
        CellStyle titleStyle = getTitleStyle(wb);
        head.setHeightInPoints(25.0F);

        for(int i = 0; i < titles.length; ++i) {
            Cell cell = head.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(titleStyle);
            sheet.setColumnWidth((short)i, 256 * widthes[i]);
        }

    }
    public static CellStyle getTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
        style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    @ResponseBody
    @RequestMapping(value = "/downLoadZipContract")
    public void downLoadZipContract(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        String domain = url.substring(0, url.indexOf(uri));
        logger.info("url:{}", url);
        logger.info("uri:{}", uri);
        logger.info("domain:{}", domain);
        searchVo.setRequestUrl(domain);
        searchVo.setDcsxSearchFlg(true);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        Long deptLeader = webParamUtils.getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            Map<String, Object> searchParams = searchVo.getSearchParams();
            searchParams.put("NEQS_hideOut", "1");
        }
        setFunderCompany(searchVo);
        ctrContractClient.downloadContractFileZip(searchVo);
    }

    //导出
    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportExcel(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException, ParseException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = webParamUtils.getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
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
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            Map<String, Object> searchParams = searchVo.getSearchParams();
            searchParams.put("NEQS_hideOut", "1");
        }
        setFunderCompany(searchVo);
        Page<DcsxShowVo> page = applyCtrDcsxClinent.findPageContract(searchVo);
        Page<DcsxShowVo> pageVo = preContractData(page);
        String title = "代采赊销合同";

        String[] titles = new String[]{"合同编号", "预算编号", "签订日", "对方企业名称", "我方抬头", "品种", "牌号", "数量(吨)", "采购单价(元)", "采购总价(元)", "已付金额(元)", "已收金额(元)", "回款周期", "付款日期", "对方开户行", "对方开户账号", "我方开户行", "我方开户账号", "合同状态", "业务员", "确认收货时间"};
        //应该是excel列属性
        String[] attrs = new String[]{"contractNo", "budgetNo", "contractTime", "companyName", "ourCompanyName",
                "productBrand", "productNum", "totalNumber", "dealPrice", "totalAmount", "dealedAmount", "receiveAmount", "creditDays", "lastPayDate",
                "bankName", "bankAccount", "ourBankName", "ourBankAccount", "contractStatus", "matchUserName", "confirmDate"};
        int[] widths = new int[]{15, 15, 15, 30, 15, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 30, 15, 15, 30, 15};
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
            //应该是读取的数据
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page =applyCtrDcsxClinent.findPageContract(searchVo);
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

    }//end exportExcel

    @RequestMapping(value = "/exportExcelTp")
    @ResponseBody
    public void exportExcelTp(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException, ParseException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = webParamUtils.getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
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
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode())) {
            Map<String, Object> searchParams = searchVo.getSearchParams();
            searchParams.put("NEQS_hideOut", "1");
        }
        setFunderCompany(searchVo);
        Page<DcsxShowVo> page = applyCtrDcsxClinent.findPageContract(searchVo);
        Page<DcsxShowVo> pageVo = preContractData(page);
        String title = "代采托盘合同";

        //指定格式 2021/2/25形式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        String[] titles = new String[]{"合同编号", "预算编号", "签订日", "对方企业名称", "我方抬头", "品种", "牌号", "数量(吨)", "采购单价(元)", "采购总价(元)", "已付金额(元)", "已收金额(元)", "回款周期", "付款日期", "对方开户行", "对方开户账号", "我方开户行", "我方开户账号", "合同状态", "业务员", "确认收货时间", "资方利息"};
        //应该是excel列属性
        String[] attrs = new String[]{"contractNo", "budgetNo", "contractTime", "companyName", "ourCompanyName",
                "productBrand", "productNum", "totalNumber", "dealPrice", "totalAmount", "dealedAmount", "receiveAmount", "creditDays", "lastPayDate",
                "bankName", "bankAccount", "ourBankName", "ourBankAccount", "contractStatus", "matchUserName", "confirmDate","zfInterest"};
        int[] widths = new int[]{15, 15, 15, 30, 15, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 30, 15, 15, 30, 15, 15};
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
            //应该是读取的数据
            PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
                    "yyyy/MM/dd");
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                page =applyCtrDcsxClinent.findPageContract(searchVo);
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

    }//end exportExcel



    private Page<DcsxShowVo> preContractData(Page<DcsxShowVo> pageVo) {
        if (pageVo != null && pageVo.getContent().size() > 0) {
            List<BsProductType> productTypeList = productTypeClient.findAll();
            Map<String, BsProductType> productMap = productTypeList.stream().collect(Collectors.toMap(BsProductType::getTypeCode, m -> m, (a, b) -> b));
            for (ApplyCtrDCSX contractShowVo : pageVo.getContent()) {
                String contractType = contractShowVo.getContractType();
                String deliveryMode = contractShowVo.getDeliveryMode();
                //contractShowVo.setSource(DictUtil.getValue(BasConstants.APPLY_TYPE, contractShowVo.getSource()));
                contractShowVo.setContractStatus(
                        DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTSTATUS, contractShowVo.getContractStatus()));
                contractShowVo.setContractAttr(
                        DictUtil.getValue(BasConstants.STOCK__CONTRACT_ATTR, contractShowVo.getContractAttr()));
                if (BasConstants.CONTRACT_TYPE_B.equals(contractType)) {
                    contractShowVo.setDeliveryMode(DictUtil.getValue(BasConstants.TEMPLATE_CONTENT_DELIVERYMODE,
                            contractShowVo.getDeliveryMode()));
                } else {
                    String value = BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE, deliveryMode);
                    contractShowVo.setDeliveryMode(value);
                }
                BsProductType bsProductType = productMap.get(contractShowVo.getProductBrand());
                if (Objects.nonNull(bsProductType)) {
                    contractShowVo.setProductBrand(bsProductType.getTypeName());
                }
            }
        }
        return pageVo;
    }

    @RequestMapping(value = "updateContractFile", method = RequestMethod.POST)
    public void updateContractFile(UpdateDcsxContractVo vo, HttpServletResponse response) {
        try {
            vo.setMatchUserId(ShiroUtil.getCurrentUserId());
            vo.setMatchUserName(ShiroUtil.getCurrentUserName());
            applyCtrDcsxClinent.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }


    @RequestMapping(value = "getUpdateDcsxEntity/{id}", method = RequestMethod.GET)
    public String getUpdateDcsxEntity(@PathVariable("id") Long id, Model model) {
        ApplyCtrDCSX entity = applyCtrDcsxClinent.getEntity(id);
        model.addAttribute("entity",entity);
        return "ctr/dcsxUpdateContractFile";
    }

    @RequestMapping(value = "/exportChainExcel")
    @ResponseBody
    public void exportChainExcel(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException, ParseException {
        initSearch(searchVo, request);
        int batchSize = 500;
        searchVo.setRows(batchSize);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        Long deptLeader = webParamUtils.getDeptLeader();
        searchVo.setDeptLeaderId(deptLeader);
        // 业务助理 查看本业务部所有预算
        if (ShiroUtil.isPermitted(PermissionEnum.USER_ZL.getPermissionCode())) {
            searchVo.setSearchType("D");
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
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
            searchVo.setAdmin(true);
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
            searchVo.setSearchType("P");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
            searchVo.setSearchType("A");
        }
        setFunderCompany(searchVo);
        Page<DcsxShowVo> pageVo = applyCtrDcsxClinent.findPageContract(searchVo);
        List<RptExportChainVo> exportChainVos = ctrContractReportClient.mergeChainExport(pageVo.getContent());

        // 按照 approveId 分组
        Map<Long, List<RptExportChainVo>> groupedByApproveId = exportChainVos.stream()
                .collect(Collectors.groupingBy(RptExportChainVo::getApproveId));

        // 创建两个新的列表，分别存储数量为 4 和 3 的数据
        List<RptExportChainVo> fourList = new ArrayList<>();
        List<RptExportChainVo> threeList = new ArrayList<>();

        // 遍历分组
        for (List<RptExportChainVo> group : groupedByApproveId.values()) {
            if (group.size() == 4) {
                fourList.addAll(group); // 添加到四条相同的列表中
            } else if (group.size() == 3) {
                threeList.addAll(group); // 添加到三条相同的列表中
            }
        }
        
        String title3 = "代采赊销上下游合同(3)";
        String title4 = "代采赊销上下游合同(4)";

        String[] titles3 = new String[]{"合同编号", "我方名称", "对方企业名称", "货名", "合同数量(吨)", "签订日期", "单价(元/吨)",
                "合同总价(元)", "回款周期","约定收/付款日期", "实际收/付全款日期", "已收/付金额(元)", "应收/付金额(元)", "已开/收票金额", "收/开票日期","出/入库日期","确认收货日期","人保已用额度","人保可用额度"};
        String[] titles4 = new String[]{"合同编号", "我方名称", "对方企业名称", "货名", "合同数量(吨)", "签订日期", "单价(元/吨)",
                "合同总价(元)", "回款周期","约定收/付款日期", "实际收/付全款日期", "已收/付金额(元)", "应收/付金额(元)", "已开/收票金额", "收/开票日期","出/入库日期","确认收货日期","人保已用额度","人保可用额度"};
        //应该是excel列属性
        String[] attrs3 = new String[]{"contractNo", "ourCompanyName", "companyName", "productNames", "totalNumber",
                "contractTime", "dealPrice", "totalAmount", "creditDays", "appointPayFullTime", "receiveDate", "receiveAmount",
                "needReceiveAmount", "billAmount", "billDate", "deliveryDate", "confirmDate","usedCreditAmount","availableCreditAmount",};
        //应该是excel列属性
        String[] attrs4 = new String[]{"contractNo", "ourCompanyName", "companyName", "productNames", "totalNumber",
                "contractTime", "dealPrice", "totalAmount", "creditDays", "appointPayFullTime", "receiveDate", "receiveAmount",
                "needReceiveAmount", "billAmount", "billDate", "deliveryDate", "confirmDate","usedCreditAmount","availableCreditAmount",};
        int[] widths3 = new int[]{15, 25, 25, 25, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,15,15};
        int[] widths4 = new int[]{15, 25, 25, 25, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,15,15};
        Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
        // 生成一个表格
        Sheet sheet3 = workbook.createSheet(title3);
        Sheet sheet4 = workbook.createSheet(title4);
        
        // 设置表格默认列宽度为 15 个字节
        sheet3.setDefaultColumnWidth(15);
        sheet4.setDefaultColumnWidth(15);
        // 产生表格标题行
        // 生成一个样式
        CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
        int[] widthes3 = new int[titles3.length];
        for (int i = 0; i < titles3.length; i++) {
            widthes3[i] = widths3[i];
        }
        int[] widthes4 = new int[titles4.length];
        for (int i = 0; i < titles4.length; i++) {
            widthes4[i] = widths4[i];
        }
        PoiExcelUtil.creatHeads(workbook, sheet3, titles3, widthes3);
        PoiExcelUtil.creatHeads(workbook, sheet4, titles4, widthes4);
        int start3 = 0;
        int start4 = 0;
        while (pageVo != null && pageVo.getContent().size() > 0) {
            //应该是读取的数据
            if (!CollectionUtils.isEmpty(threeList)) {
                PoiExcelUtil.createRows(sheet3, threeList, attrs3, start3, cellStyle, "yyyy/MM/dd");
            }
            if (!CollectionUtils.isEmpty(fourList)) {
                PoiExcelUtil.createRows(sheet4, fourList, attrs4, start4, cellStyle, "yyyy/MM/dd");
            }
            start3 += threeList.size();
            start4 += fourList.size();
            if (pageVo.hasNext()) {
                searchVo.setPage(searchVo.getPage() + 1);
                pageVo = applyCtrDcsxClinent.findPageContract(searchVo);
                exportChainVos = ctrContractReportClient.mergeChainExport(pageVo.getContent());
                // 按照 approveId 分组
                groupedByApproveId = exportChainVos.stream()
                        .collect(Collectors.groupingBy(RptExportChainVo::getApproveId));

                // 创建两个新的列表，分别存储数量为 4 和 3 的数据
                fourList = new ArrayList<>();
                threeList = new ArrayList<>();

                // 遍历分组
                for (List<RptExportChainVo> group : groupedByApproveId.values()) {
                    if (group.size() == 4) {
                        fourList.addAll(group); // 添加到四条相同的列表中
                    } else if (group.size() == 3) {
                        threeList.addAll(group); // 添加到三条相同的列表中
                    }
                }
            } else {
                pageVo = null;
            }
        }

        try {
            PoiExcelUtil.write(workbook, response, title3);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
