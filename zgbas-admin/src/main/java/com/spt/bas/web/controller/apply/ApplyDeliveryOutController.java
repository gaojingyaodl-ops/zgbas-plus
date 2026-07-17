package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.DateUtils;
import com.spt.bas.web.util.FreemarkUtils;
import com.spt.bas.web.util.GenerationUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcessStep;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/apply/deliveryOut")
public class ApplyDeliveryOutController extends PageController<ApplyDeliveryOut, BaseVo> {

    @Autowired
    private IApplyDeliveryOutClient applyDeliveryOutClient;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBsFactoryClient factoryClient;
    @Autowired
    private IBsWarehouseClient warehouseClient;
    @Autowired
    private IStockDetailClient stockDetailClient;
    @Autowired
    private IApplyDeliveryInClient applyDeliveryInClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private IPmProcessStepClient pmProcessStepClient;
    @Autowired
    private IFileProcessRelClient fileProcessRelClient;
    @Autowired
    private LogisticsCompanyConfigClient logisticsCompanyConfigClient;
    @Resource
    private ICtrLogisticsDeliveryClient logisticsDeliveryClient;
    @Resource
    private ICtrLogisticsDriverClient logisticsDriverClient;
    @Resource
    private ICtrLogisticsFileClient logisticsFileClient;
    @Override
    public BaseClient<ApplyDeliveryOut> getService() {
        return applyDeliveryOutClient;
    }

    /**
     * 出库
     *
     * @param id
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String contractId = request.getParameter("contractId");
        String logisticsId = request.getParameter("logisticsId");
        String logisticsDeliveryId = request.getParameter("logisticsDeliveryId");
        ApplyDeliveryOut entity = getEntity(id, contractId, logisticsId, logisticsDeliveryId);
        model.addAttribute("productTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
        model.addAttribute("payTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        model.addAttribute("deliveryTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        //产品类型
        List<SysDictDataSdk> list = DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT);
        model.addAttribute("productType", JsonUtil.obj2Json(list));
        model.addAttribute("entity", entity);
        model.addAttribute("productJson",
                JsonUtil.obj2Json(productTypeClient.findAll()));
        List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
        model.addAttribute("entity", entity);

        List<BsWarehouse> warehouseList = warehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
        //交货方式
        model.addAttribute("deliveryMode",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
        //出库方式
        model.addAttribute("deliveryOutTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYOUT_TYPE)));
        //包装规格-全部
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
        String from = request.getParameter("from");
        if (from != null) {
            return "changeApply/oldDeliveryOut-content";
        }
        model.addAttribute("industry", ShiroUtil.getIndustry());

        String processCode = request.getParameter("processCode");
        // 附件类型
        List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
        model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        model.addAttribute("nowDate", LocalDate.now().format(formatter));

        //出库申请需要另外展示对应销售申请审批流程
        if (entity != null && entity.getContractId() != null) {
            CtrContract contract = ctrContractClient.getEntity(entity.getContractId());
            if (contract != null) {
                Long approveId = contract.getApproveId();
                model.addAttribute("contractApproveId", approveId);
                PmApprove approve = pmApproveClient.getEntity(approveId);
                //获取审批的步骤
                PageSearchVo queryVo = new PageSearchVo();
                queryVo.setRows(20);
                Map<String, Object> searchParams = new HashMap<>(4);
                searchParams.put("EQL_processId", approve.getProcessId());
                if (approve.getConditionId() != null && approve.getConditionId().compareTo(new Long(0)) > 0) {
                    searchParams.put("EQL_conditionId", approve.getConditionId());
                }
                searchParams.put("EQB_enableFlg", true);
                queryVo.setSort("dispOrderNo");
                queryVo.setOrder("ASC");
                queryVo.setSearchParams(searchParams);
                PageDown<PmProcessStep> stepPage = pmProcessStepClient.findPage(queryVo);
                model.addAttribute("sellStepListJson", JsonUtil.obj2Json(stepPage.getContent()));
            }
        }
        String contractApproveId = request.getParameter("contractApproveId");
        if (StringUtils.isNotEmpty(contractApproveId)) {
            model.addAttribute("contractApproveId", Long.parseLong(request.getParameter("contractApproveId")));
        }

        boolean isFromTP = dealWithTp(model, contractId, entity);
        // 代采 白条进入新的出库页面
        if (Boolean.parseBoolean(request.getParameter("isFromBkb")) || (entity.getContractNo()!=null && ctrContractClient.checkIsBkb(entity.getContractNo()))) {
            return "apply/deliveryOut-contentBkb";
        } else if (isFromTP){
            return "apply/deliveryOut-contentTP";
        }
        return "apply/deliveryOut-content";
    }

    private boolean dealWithTp(Model model, String contractId, ApplyDeliveryOut entity){
        long realContractId = StringUtils.isNotBlank(contractId) && NumberUtil.isNumber(contractId) ? Long.parseLong(contractId) : entity.getContractId();
        CtrContract contract = ctrContractClient.getEntity(realContractId);
        if (Objects.isNull(contract)) {
            return false;
        }
        if (!ctrContractClient.checkIsTP(contract.getContractNo())){
            return false;
        }

        List<CtrContract> contractList = ctrContractClient.findByApproveId(contract.getApproveId());
        CtrContract buyContract = contractList.stream()
                .filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType()))
                .findFirst().orElse(new CtrContract());
        // 经确认取约定付款日期
        String buyPayFullTime = DateUtils.parseDateToStr("yyyy-MM-dd", buyContract.getAppointPayFullTime());
        String contractTime = DateUtils.parseDateToStr("yyyy-MM-dd", contract.getContractTime());
        entity.setBuyDealPrice(buyContract.getDealPrice());
        entity.setBuyPayFullTime(buyContract.getPayFullTime());
        entity.setTpRate(contract.getTpRate());
        model.addAttribute("buyPayFullTime", buyPayFullTime);
        model.addAttribute("buyDealPrice", buyContract.getDealPrice());
        model.addAttribute("contractTime", contractTime);
        model.addAttribute("sellFundRate", contract.getTpRate());
        return true;
    }

    @ModelAttribute("preload")
    public ApplyDeliveryOut getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "contractId", required = false) String contractId,
                                      @RequestParam(value = "logisticsId", required = false) String logisticsId, @RequestParam(value = "logisticsDeliveryId", required = false) String logisticsDeliveryId) {
        logger.info("getEntity contractId:{}, logisticsId:{}, logisticsDeliveryId:{}", contractId, logisticsId, logisticsDeliveryId);
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                ApplyDeliveryOut entity = new ApplyDeliveryOut();
                if (StringUtils.isNotBlank(contractId) && NumberUtil.isNumber(contractId)){
                    entity = applyDeliveryOutClient.generateApplyNo(Long.valueOf(contractId));
                }
                if (StringUtils.isNotBlank(logisticsDeliveryId) && NumberUtil.isNumber(logisticsDeliveryId)
                        && StringUtils.isNotBlank(logisticsId) && NumberUtil.isNumber(logisticsId)){
                    CtrLogisticsDelivery logisticsDelivery = logisticsDeliveryClient.getEntity(Long.valueOf(logisticsDeliveryId));
                    if (Objects.nonNull(logisticsDelivery)){
                        entity.setWarehouseOutDate(logisticsDelivery.getLogisticsDate());
                        entity.setDeliveryType(logisticsDelivery.getDeliveryType());
                        entity.setDeliveryOutFee(logisticsDelivery.getDeliveryOutFee());
                        entity.setTransportAmount(logisticsDelivery.getTransportAmount());
                        entity.setStevedorage(logisticsDelivery.getStevedorage());
                        entity.setOtherFee(logisticsDelivery.getOtherFee());
                        entity.setCarrier(logisticsDelivery.getCarrier());
                    }

                    CtrLogisticsDriver driver = new CtrLogisticsDriver(Long.valueOf(logisticsId), Long.valueOf(logisticsDeliveryId));
                    List<CtrLogisticsDriver> driverList = logisticsDriverClient.findByLogisticsIdAndLogisticsDeliveryId(driver);
                    if (CollectionUtils.isNotEmpty(driverList)){
                        entity.setDriverName(driverList.stream().map(CtrLogisticsDriver::getDriverName).filter(StringUtils::isNotBlank).collect(Collectors.joining(BasConstants.OBL)));
                        entity.setDriverCardNo(driverList.stream().map(CtrLogisticsDriver::getDriverCardNo).filter(StringUtils::isNotBlank).collect(Collectors.joining(BasConstants.OBL)));
                        entity.setPlateNumber(driverList.stream().map(CtrLogisticsDriver::getPlateNumber).filter(StringUtils::isNotBlank).collect(Collectors.joining(BasConstants.OBL)));
                        entity.setDriverPhone(driverList.stream().map(CtrLogisticsDriver::getContactPhone).filter(StringUtils::isNotBlank).collect(Collectors.joining(BasConstants.OBL)));
                    }

                    CtrLogisticsFile logisticsFile = new CtrLogisticsFile(Long.valueOf(logisticsId), Long.valueOf(logisticsDeliveryId));
                    CtrLogisticsFileRespVo respVo = logisticsFileClient.findByLogisticsIdAndLogisticsDeliveryId(logisticsFile);
                    if (Objects.nonNull(respVo)){
                        entity.setFileId(respVo.getAllFileId());
                        entity.setLogisticsId(Long.valueOf(logisticsId));
                        entity.setLogisticsDeliveryId(Long.valueOf(logisticsDeliveryId));
                    }
                }
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                return entity;
            }
        }
        return null;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyDeliveryOutClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "findWarehouseName", method = RequestMethod.POST)
    public void findWarehouse(String warehouseName, HttpServletResponse response) {
        StockDetail stockDetail = stockDetailClient.findWarehouseName(warehouseName);
        RenderUtil.renderJson(stockDetail, response);
    }

    @RequestMapping(value = "findDeliveryInContractId", method = RequestMethod.POST)
    public void findDeliveryInContractId(Long buyContractId, HttpServletResponse response) {
        List<ApplyDeliveryIn> applyDeliveryInList = applyDeliveryInClient.findDeliveryInContractId(buyContractId);
        if (applyDeliveryInList != null && applyDeliveryInList.size() > 0) {
            RenderUtil.renderJson(applyDeliveryInList.get(0), response);
        } else {
            RenderUtil.renderFailure("fail", response);
        }

    }

    @RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
    public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Map<String, Object> map = searchVo.getSearchParams();
        map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        Page<ApplyCancelDetail> page = applyDeliveryOutClient.findPageDetail(searchVo);

        JsonEasyUI.renderJson(response, page);
    }

    @RequestMapping(value = "getSellProcessSteJson", method = RequestMethod.POST)
    public void getSellProcessSteJson(Long ctrContractId, HttpServletResponse response) {
        if (ctrContractId != null && ctrContractId != 0L) {
            CtrContract contract = ctrContractClient.getEntity(ctrContractId);
            Long approveId = contract.getApproveId();
            PmApprove approve = pmApproveClient.getEntity(approveId);
            //获取审批的步骤
            PageSearchVo queryVo = new PageSearchVo();
            queryVo.setRows(20);
            Map<String, Object> searchParams = new HashMap<>(4);
            searchParams.put("EQL_processId", approve.getProcessId());
            if (approve.getConditionId() != null && approve.getConditionId().compareTo(new Long(0)) > 0) {
                searchParams.put("EQL_conditionId", approve.getConditionId());
            }
            searchParams.put("EQB_enableFlg", true);
            queryVo.setSort("dispOrderNo");
            queryVo.setOrder("ASC");
            queryVo.setSearchParams(searchParams);
            PageDown<PmProcessStep> stepPage = pmProcessStepClient.findPage(queryVo);
            if (stepPage != null && stepPage.getContent().size() > 0) {
                RenderUtil.renderJson(stepPage.getContent(), response);
            } else {
                RenderUtil.renderFailure("fail", response);
            }
        }
    }

    /**
     * 查询已出库未确认批次信息
     *
     * @param ctrContractId
     * @param response
     */
    @RequestMapping(value = "getUnConfirmDeliveryOut")
    public void getUnConfirmDeliveryOut( Long ctrContractId, String waybillCode, HttpServletResponse response,HttpServletRequest request ) {

        List<ApplyProductDetailVo> result=new ArrayList<>();
        if (ctrContractId != null) {
            if(StringUtils.isNotBlank(waybillCode)&&!"null".equals(waybillCode)){
                 List<ApplyProductDetailVo> results = applyDeliveryOutClient.getUnConfirmDeliveryOut(ctrContractId);
                 List<ApplyDeliveryOut> byContractId = applyDeliveryOutClient.findByContractId(ctrContractId);
                for (ApplyDeliveryOut applyDeliveryOut : byContractId) {
                    if(StringUtils.isNotBlank(applyDeliveryOut.getWaybillCode())&&applyDeliveryOut.getWaybillCode().equals(waybillCode)){
                        for (ApplyProductDetailVo resultt : results) {
                            if(resultt.getApplyDeliveryOutId().toString().equals(applyDeliveryOut.getId().toString())){
                                result.add(resultt);
                            }
                        }
                    }
                }
            }else{
              result= applyDeliveryOutClient.getUnConfirmDeliveryOut(ctrContractId);
            }
            RenderUtil.renderJson(result, response);
        }
    }
    
    /**
     * 查询中游已出库未确认批次信息
     *
     * @param ctrContractId
     * @param response
     */
    @RequestMapping(value = "getUnConfirmDeliveryOutDcsx")
    public void getUnConfirmDeliveryOutDcsx( Long ctrContractId, String waybillCode, HttpServletResponse response,HttpServletRequest request ) {

        List<ApplyProductDetailVo> result=new ArrayList<>();
        if (ctrContractId != null) {
            if(StringUtils.isNotBlank(waybillCode)&&!"null".equals(waybillCode)){
                List<ApplyProductDetailVo> results = applyDeliveryOutClient.getUnConfirmDeliveryOutDcsx(ctrContractId);
                List<ApplyDeliveryOut> byContractId = applyDeliveryOutClient.findByContractId(ctrContractId);
                for (ApplyDeliveryOut applyDeliveryOut : byContractId) {
                    if(StringUtils.isNotBlank(applyDeliveryOut.getWaybillCode())&&applyDeliveryOut.getWaybillCode().equals(waybillCode)){
                        for (ApplyProductDetailVo resultt : results) {
                            if(resultt.getApplyDeliveryOutId().toString().equals(applyDeliveryOut.getId().toString())){
                                result.add(resultt);
                            }
                        }
                    }
                }
            }else{
                result= applyDeliveryOutClient.getUnConfirmDeliveryOutDcsx(ctrContractId);
            }
            RenderUtil.renderJson(result, response);
        }
    }

    /**
     * 物流调整 选择出库批次页面
     *
     * @return
     */
    @RequestMapping(value = "logisticsAdjustDeliveryOutChoose", method = RequestMethod.GET)
    public String logisticsAdjustDeliveryOutChoose(Model model, HttpServletRequest request) {
        model.addAttribute("contractId", request.getParameter("contractId"));
        model.addAttribute("waybillCode", request.getParameter("waybillCode"));
        return "apply/logisticsAdjustDeliveryOut-choose";
    }
    /**
     * 查询中游已出库批次信息
     *
     * @param ctrContractId
     * @param response
     */
    @RequestMapping(value = "getAllDeliveryOut")
    public void getAllDeliveryOut( Long ctrContractId, String waybillCode, HttpServletResponse response,HttpServletRequest request ) {
        if (ctrContractId != null) {
            List<ApplyProductDetailVo> result = applyDeliveryOutClient.getAllDeliveryOut(ctrContractId);
            RenderUtil.renderJson(result, response);
        }
    }
    
    /**
     * 确认收货 选择出库批次页面
     *
     * @return
     */
    @RequestMapping(value = "choose", method = RequestMethod.GET)
    public String unConfirmDeliveryOutChoose(Model model, HttpServletRequest request) {
        model.addAttribute("contractId", request.getParameter("contractId"));
        model.addAttribute("waybillCode", request.getParameter("waybillCode"));
        return "apply/unConfirmDeliveryOut-choose";
    }

    /**
     * 生成货转单
     *
     * @param entity
     * @param response
     */
    @RequestMapping(value = "generateTranshipment", method = RequestMethod.POST)
    public void generateTranshipment(ApplyDeliveryOutVo entity, HttpServletResponse response,HttpServletRequest request) throws ApplicationException {
        logger.info("ApplyDeliveryOutVo:{}", JsonUtil.obj2Json(entity));
        List<?> list = JsonEasyUI.getInsertRecords(entity.getSubClass(), request);
        ApplyProductDetail detail = null;
        if (!list.isEmpty()) {
            detail = (ApplyProductDetail) list.get(0);
        }
        try {
            Map map = dealWithMap(detail);
            String html = FreemarkUtils.getTemplate("deliveryOutFile.html", map);
            // 生成并下载
            GenerationUtil.generateTranshipment(response, html,"货转单.png");
        } catch (Exception e) {
            logger.error("获取提货单模板异常", e);
        }
    }

    /**
     * 组装返回参数
     * @return
     */
    private Map dealWithMap(ApplyProductDetail detail) {
        Map map = new HashMap();
        // 委托日期 (当天)
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        map.put("date", df.format(LocalDateTime.now()));

        // 送货单号
        map.put("deliveryNo", "");

        if (detail != null) {
            // 品名
            map.put("productName", detail.getProductName());
            // 牌号
            map.put("brandNumber", detail.getBrandNumber());
            // 产地/厂家
            map.put("factoryName", detail.getFactoryName());
            // 规格
            map.put("wrapStr", "");
            // 数量
            map.put("dealNumber", detail.getDealNumber());
            // 单位
            map.put("numberUnit", detail.getNumberUnit());
            // 单价
            map.put("dealPrice", detail.getDealPrice());
            // 金额
            BigDecimal totalAmount = detail.getDealPrice().multiply(detail.getDealNumber());
            map.put("deliveryOutTotalAmount", totalAmount);
            String number2CNMontray = RmbUtil.number2Chinese(totalAmount);
            map.put("deliveryOutTotalAmountZh", number2CNMontray);

            // 总重量(吨)
            map.put("dealNumber", detail.getDealNumber());



        }

        // 提货日期
        map.put("receiveDate", "提货日期：");
        // 要求到货日期
        map.put("requireDate", "要求到货日期：    年    月     日");
        // 发货单位
        map.put("sendCompany", "发货单位:");
        // 联系人
        map.put("sendCompanyContact", "发货联系人:");
        // 联系电话
        map.put("sendCompanyContactPhone", "联系电话:");
        // 提货地址
        map.put("productAddress", "提货地址:");
        // 提货仓联系人
        map.put("thcContact", "提货仓联系人:");
        // 联系人
        map.put("thcContactPhone", "联系电话:");

        // 送货单位
        map.put("sender", "送货单位:");
        // 收货单位
        map.put("receiver", "收货单位:");
        // 联系人
        map.put("senderContact", "联系人:");
        // 联系人
        map.put("receiverContact", "联系人:");
        // 电话
        map.put("senderPhone", "电话:");
        map.put("receiverPhone", "联系电话:");

        // 地址
        map.put("senderAddress", "地址:");
        map.put("receiverAddress", "收货地址:");
        map.put("remark", "备注:");

        return map;
    }

    /**同步則一推送的司机信息*/
    @RequestMapping(value = "findDriverInformation", method = RequestMethod.POST)
    @ResponseBody
    public CtrContractDeliveryVo findDriverInformation(@RequestParam("ctrContractId") Long ctrContractId,@RequestParam("waybillCode") String waybillCode ,HttpServletRequest request, HttpServletResponse response) {
        return  null;
    }

        /**
         * 获取承运商信息
         */
        @RequestMapping("getCarrierInfo")
        public void getCarrierInfo(@RequestParam("ourCompanyName") String ourCompanyName,HttpServletRequest request, HttpServletResponse response){
            int flag=1;
        if(StringUtils.isNotBlank(ourCompanyName)){
            List<BsDictData> listByCategory = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
            for (BsDictData bsDictData : listByCategory) {
                if(StringUtils.equals(ourCompanyName,bsDictData.getDictName())){
                    ourCompanyName=bsDictData.getDictCd();
                }
            }
            List<LogisticsCompanyConfig> byOurCompanyNames = logisticsCompanyConfigClient.findByOurCompanyNames(ourCompanyName);
             Iterator<LogisticsCompanyConfig> iterator = byOurCompanyNames.iterator();
            while (iterator .hasNext()) {
                 LogisticsCompanyConfig next = iterator.next();
                String ourCompanyNames = next.getOurCompanyNames();
                String[] split = ourCompanyNames.split(",");
                for (String s : split) {
                    if(StringUtils.equals(s,ourCompanyName)){
                        flag=2;
                    }
                }
                if(flag!=2){
                    iterator.remove();
                }
                flag=1;
            }
            String json= JsonUtil.obj2Json(byOurCompanyNames);
            RenderUtil.renderJson(json,response);
        }
    }

    /**
     * 生成出库单据
     * @param deliveryOut
     * @param request
     * @param response
     */
    @RequestMapping(value = "generateOutFile", method = RequestMethod.POST)
    public void generateOutFile(ApplyDeliveryOut deliveryOut, HttpServletRequest request, HttpServletResponse response) {
        try {
            CtrLogisticsReqVo reqVo = new CtrLogisticsReqVo();
            ApplyDeliveryOut entity = applyDeliveryOutClient.getEntity(deliveryOut.getId());
            reqVo.setApplyDeliveryOut(entity);
            String currNumber = request.getParameter("currNumber");
            if (StringUtils.isNotBlank(currNumber) && NumberUtil.isNumber(currNumber)) {
                reqVo.setCurrNumber(new BigDecimal(currNumber));
            }
            reqVo.setContractId(entity.getContractId());
            reqVo.setContractNo(entity.getContractNo());
            reqVo.setLogisticsEnum(LogisticsEnum.DELIVERY_OUT);
            reqVo.setBizUserName(ShiroUtil.getCurrentUserName());
            CtrLogisticsFile logisticsFile = logisticsDeliveryClient.generateDeliveryFile(reqVo);
            RenderUtil.renderJson(JsonUtil.obj2Json(logisticsFile), response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            String msg = e.getMessage();
            if (e.getCause() != null) {
                msg = e.getCause().getMessage();
            }
            RenderUtil.renderFailure(msg, response);
        }
    }
}
