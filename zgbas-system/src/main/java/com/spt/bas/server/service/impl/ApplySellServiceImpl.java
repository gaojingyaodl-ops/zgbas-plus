package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplySellDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.IApplySellService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.TemplateContentUtility;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Transient;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component("applySellService")
@Transactional(readOnly = true)
public class ApplySellServiceImpl extends BaseService<ApplySell> implements IApplySellService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService SELL_SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private ApplySellDao applySellDao;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Autowired
    private ICtrContractSaveService contractSaveService;
    @Autowired
    private ICtrContractService contractService;
    @Autowired
    private StockDetailFacade stockDetailFacade;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IStockContractService stockContractService;
    @Autowired
    private CtrProductDao productDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmProcessService pmProcessService;

    @Value("${credit.contract.switch}")
    private Boolean creditSwitch;

    @Override
    public BaseDao<ApplySell> getBaseDao() {
        return applySellDao;
    }

    @Override
    public Class<ApplySell> getEntityClazz() {
        return ApplySell.class;
    }

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplySell sell = applySellDao.findOne(approve.getBizId());
            String applyType = sell.getApplyType();
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(sell.getId(), applyType);
            // 保存合同主表
            CtrContract entity = new CtrContract();
            BeanUtils.copyProperties(sell, entity);
            entity.setCarrier(sell.getCarrier());
            entity.setTransportAmount(sell.getTransportCost());
            entity.setWarehouseAmount(sell.getWarehouseCost());
            entity.setSource(applyType);
            entity.setContractType(BasConstants.CONTRACT_TYPE_S);
            entity.setBondRate(sell.getReceiveRate());
            entity.setDeliveryDateFrom(sell.getDeliveryTime());
            entity.setDeliveryDateTo(sell.getDeliveryTime());
            entity.setPayBondTime(sell.getReceiveBondTime());
            entity.setPayFullTime(sell.getReceiveFullTime());
            entity.setPayType(sell.getReceiveType());

            entity.setBreachAmount(BigDecimal.ZERO);
            entity.setReceiveBreachAmount(BigDecimal.ZERO);
            entity.setServiceBilledAmount(BigDecimal.ZERO);
            entity.setServiceAmount(BigDecimal.ZERO);
            entity.setReceiveServiceAmount(BigDecimal.ZERO);
            //添加赊销合同标识
            String deliveryMode = sell.getDeliveryMode();
            if (StringUtils.equals(deliveryMode, BasConstants.DELIVERY_MODE_SX)) {
                entity.setSettlementType("0");
                entity.setCreditFlg(true);
            }
            entity.setDeliveryAddr(sell.getShippingAddr());
            entity.setBusinessKind(BasConstants.BusinessKind.DICT_ZYXS);
            entity = contractSaveService.saveContract(entity, list, approve);

            // 保存采购申请表中的合同Id
            sell.setContractId(entity.getId());
            sell = applySellDao.save(sell);

            //审批完成后自动发起盖章申请
            this.autoInitiatedSealUsage(sell, approve);
        }
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplySell sell = applySellDao.findOne(approve.getBizId());
        logger.info("creditSwitch:{}", JsonUtil.obj2Json(creditSwitch));
        String applyType = sell.getApplyType();
        if (StringUtils.equals(BasConstants.APPLY_TYPE_S, applyType)) {
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(sell.getId(), BasConstants.APPLY_TYPE_S);
            for (ApplyProductDetail product : list) {
                // 修改库存表冻结数量
                CtrProduct ctr = new CtrProduct();
                BeanUtils.copyProperties(product, ctr);
                StockDetailLinkVo linkVo = new StockDetailLinkVo();
                linkVo.setStockDetailId(product.getStockDetailId());
                linkVo.setStockContractId(product.getStockContractId());

                StockDetailRequest request = StockDetailRequest.build(ctr);
                request.setApplyId(sell.getId());
                request.setApproveId(approve.getId());
                request.setLinkContractId(linkVo.getLinkContractId());
                request.setLinkDetailId(linkVo.getStockDetailId());
                request.setStockContractId(linkVo.getStockContractId());
                request.setBack(false);
                request.setApplyType(BasConstants.APPLY_TYPE_S);
                stockDetailFacade.saveSell(request);
            }
        }

    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        // 驳回
        ApplySell sell = applySellDao.findOne(approve.getBizId());
        String deliveryMode = sell.getDeliveryMode();
        String applyType = sell.getApplyType();
        if (StringUtils.equals(BasConstants.APPLY_TYPE_S, applyType)) {
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(sell.getId(), applyType);

            // 驳回 更新企业审批中的授信额度
            if (StringUtils.equals(deliveryMode, BasConstants.DELIVERY_MODE_SX) && creditSwitch) {
                BsCompany bsCompany = bsCompanyService.getEntity(sell.getCompanyId());
                BigDecimal approveCredit = bsCompany.getApproveCreditAmount() == null ? BigDecimal.ZERO : bsCompany.getApproveCreditAmount();
                BigDecimal realApproveCredit = approveCredit.subtract(sell.getTotalAmount());
                bsCompany.setApproveCreditAmount(realApproveCredit);
                bsCompanyService.save(bsCompany);
            }
            for (ApplyProductDetail product : list) {
                // 修改库存表冻结数量
                CtrProduct ctr = new CtrProduct();
                BeanUtils.copyProperties(product, ctr);
                StockDetailLinkVo linkVo = new StockDetailLinkVo();
                linkVo.setStockDetailId(product.getStockDetailId());

                StockDetailRequest request = StockDetailRequest.build(ctr);
                request.setApplyId(sell.getId());
                request.setApproveId(approve.getId());
                request.setLinkContractId(linkVo.getLinkContractId());
                request.setLinkDetailId(linkVo.getStockDetailId());
                request.setStockContractId(linkVo.getStockContractId());
                request.setBack(true);
                request.setApplyType(BasConstants.APPLY_TYPE_S);
                request.setStockContractId(product.getStockContractId());
                stockDetailFacade.saveSell(request);
            }
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) {
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplySell sell = null;
        ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
        if (pmEntity instanceof ApplySellVo) {
            ApplySellVo sellVo = (ApplySellVo) pmEntity;
            sell = new ApplySell();
            String applyType = sellVo.getApplyType();
            vo.setApplyType(applyType);
            BeanUtils.copyProperties(sellVo, sell);
            if (sell.getId() == null || sell.getId() == 0L) {
                // 生成合同号
                String contractNo = BasBusinessUtil.composeContractNoZy(sellVo.getEnterpriseId(), vo.getApplyType());
                sell.setContractNo(contractNo);
            }
            sell = applySellDao.save(sell);
            sellVo.setApplyId(sell.getId());
            // 新增商品明细
            vo.setApplyId(sell.getId());
            productDetailService.saveDetailBatch(sellVo.getLstInsert(), sellVo.getLstUpdate(), sellVo.getLstDelete(), vo);
            // 计算合同总价,合同毛利,成本毛利,差价
            List<ApplyProductDetail> productList = productDetailService.findApplyDetail(sell.getId(), applyType);
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal grossProfit = BigDecimal.ZERO;//合同毛利
            BigDecimal differPrice = BigDecimal.ZERO;//差价
            for (ApplyProductDetail product : productList) {
                Long stockContractId = product.getStockContractId();
                totalAmount = totalAmount.add(product.getTotalPrice());
                BigDecimal dealPrice = product.getDealPrice();
                BigDecimal dealNumber = product.getDealNumber();
                if (stockContractId != null) {
                    StockContract stockContract = stockContractService.getEntity(product.getStockContractId());
                    if (stockContract != null) {
                        BigDecimal buyDealPrice = stockContract.getDealPrice();
                        differPrice = differPrice.add(dealPrice.subtract(buyDealPrice));
                        grossProfit = grossProfit.add(dealNumber.multiply(dealPrice.subtract(buyDealPrice)));
                    }
                }
            }
            sell.setGrossProfit(grossProfit);
            sell.setDifferPrice(differPrice);
            BigDecimal receiveRate = Objects.isNull(sell.getReceiveRate()) ? BigDecimal.ZERO : sell.getReceiveRate();
            BigDecimal bondAmount = totalAmount.multiply(receiveRate);
            sell.setBondAmount(bondAmount);
            sell.setReceiveRate(receiveRate);
            sell.setTotalAmount(totalAmount);
            if (StringUtils.equals(BasConstants.ATTACH_DELIVERY_TIME_K, sell.getAttachDeliveryTime())) {
                sell.setDeliveryTime(null);
            }
            BsDictData data = BsCompanyOurUtil.getCompanyOurToBsDictData(sell.getEnterpriseId(), sell.getOurCompanyName());
            sell.setOurCompanyName(Objects.nonNull(data) ? data.getDictName() : sell.getOurCompanyName());
            applySellDao.save(sell);
        } else {
            ApplySell entity = (ApplySell) pmEntity;
            sell = applySellDao.save(entity);
            // 保存商品明细中企业id
            vo.setApplyId(sell.getId());
            vo.setEnterpriseId(sell.getEnterpriseId());
            productDetailService.saveBatchEnterpriseId(vo);
        }
        return sell;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplySell vo = (ApplySell) pmEntity;
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(), vo.getApplyType());
            StringBuffer productNameAndBrand = new StringBuffer("");
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (ApplyProductDetail product : list) {
                String dealNumber = NumberUtil.formatNumber(product.getDealNumber(), "#.###");
                String[] title = product.getProductCd().split("_");
                if (title[0].equals("SL")) {
                    productNameAndBrand.append(product.getProductName() + "/" + product.getBrandNumber() + "/"
                             + dealNumber + RuleUtil.weightUnit);
                } else {
                    productNameAndBrand.append(product.getProductName()  + "/" + dealNumber + RuleUtil.weightUnit);
                }
                totalPrice = totalPrice.add(product.getTotalPrice());
            }
            String productNameAndBrandStr = productNameAndBrand.toString();
            String companyName = RuleUtil.companyNameSubString(vo.getCompanyName());
            String subject = SubjectUtil.formatSubject(vo.getContractNo(),productNameAndBrandStr,companyName,SubjectUtil.formatMoney(totalPrice,RuleUtil.monetaryUnit));
            return subject;
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applySellDao.updateFileId(id, fileId);
    }

    @Override
    public void updateApplyStatus(Long contractId) {
        applySellDao.updateApplyStatus(contractId);
    }

    @Override
    public ApplySell findByContractId(Long contractId) {
        return applySellDao.findByContractId(contractId);
    }

    @Override
    public ApproveFormPrintVo printApplySell(Long applyId) {
        ApproveFormPrintVo vo = new ApproveFormPrintVo();
        Set<CtrProduct> buySet = new HashSet<>();
        Set<CtrContract> contractSet = new HashSet<>();
        Optional<ApplySell> applySell = applySellDao.findById(applyId);
        List<ApplyProductDetailVo> newList = new ArrayList<>();
        ApplySell sell = null;
        if (applySell.isPresent()) {
            sell = applySell.get();
        }
        List<ApplyProductDetail> list = productDetailService.findApplyDetail(applyId, BasConstants.APPLY_TYPE_S);
        // 处理模板合并的参数
        for (ApplyProductDetail detail : list) {
            String productName = detail.getProductName() + "/" + detail.getBrandNumber() + "/"
                    + detail.getFactoryName();
            detail.setProductName(productName);
            // 获取货物采购的信息
            StockContract stockContract = stockContractService.getEntity(detail.getStockContractId());
            CtrProduct product = productDao.findOne(stockContract.getBuyProductId());
            CtrContract contract = contractService.getEntity(product.getCtrContractId());
            contractSet.add(contract);
            buySet.add(product);
            vo.setBuyMatchUserName(contract.getMatchUserName());

            ApplyProductDetailVo apvo = new ApplyProductDetailVo();
            BeanUtils.copyProperties(detail, apvo);
            apvo.setBuyDealNumber(product.getDealNumber());
            apvo.setBuyDealPrice(product.getDealPrice());
            apvo.setBuyTotalPrice(product.getTotalPrice());
            apvo.setBuyCtrContractId(product.getCtrContractId());
            newList.add(apvo);
        }
        vo.setDetailList(list);
        vo.setBuyDetailList(getMyArticles(buySet));
        vo.setContractList(getMyArticles(contractSet));
        vo.setNewList(newList);
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        // 期货显示交货时间
        if (StringUtils.equals(sell.getContactAddr(), BasConstants.DICT_TYPE_CONTRACTATTR_F)) {
            vo.setDeliveryTimeStr(sdf.format(sell.getDeliveryTime()));
        } else {
            vo.setDeliveryTimeStr("");
        }
        // 判断是否有运费
        if (sell.getTransportCost() != null && sell.getTransportCost().compareTo(BigDecimal.ZERO) > 0) {
            vo.setTransportAmount(sell.getTransportCost());
        } else {
            vo.setTransportAmount(BigDecimal.ZERO);
        }
        // 获取审批单的创建人
        PmApprove approve = pmApproveService.getEntity(sell.getApproveId());
        vo.setMatchUserName(approve.getCreateUserName());

        vo.setContractAttr(DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTATTR, sell.getContractAttr()));
        vo.setPayTimeStr(sdf.format(sell.getReceiveFullTime()));
        vo.setBondAmount(sell.getBondAmount());
        vo.setDeliveryType(DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, sell.getDeliveryType()));
        vo.setWarehouseName(list.get(0).getWarehouseName());
        vo.setRemark(sell.getRemark());
        vo.setCompanyName(sell.getCompanyName());
        vo.setContractNo(sell.getContractNo());

        BsTemplateConfig template = TemplateContentUtility.getTemplate("matchApplyPrint", "FMC_APPLY_SELL", "CH",
                sell.getEnterpriseId());
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_SX_HK, sell.getBusinessType())) {
            template = TemplateContentUtility.getTemplate("matchApplyPrint", "FMC_APPLY_HK_SELL", "CH", sell.getEnterpriseId());
        }
        try {
            String content = contentMerge(template.getContent(), vo);
            vo.setContent(content);
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return vo;
    }

    // 将审批内容填充至模板
    @SuppressWarnings("deprecation")
    private String contentMerge(String content, ApproveFormPrintVo entity) throws ApplicationException {
        Configuration cfg = new Configuration();
        StringWriter sw = new StringWriter();
        try {
            Template t = new Template("", new StringReader(content), cfg);
            t.process(entity, sw);
            content = sw.toString();
        } catch (Exception e) {
            throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
        }
        return content;
    }

    @Transient
    public <T> List<T> getMyArticles(Set<T> hs) {
        List<T> list = new ArrayList<>(hs);
        return list;
    }


    @Override
    @ServerTransactional
    public void applySell(ApplySellVo applySellVo) throws ApplicationException {
        try {
            for (ApplyProductDetailVo applyProductDetailVo : applySellVo.getProductJSON()) {
                if (applyProductDetailVo.getDealNumber() == null) {
                    throw new ApplicationException("数量不能为NULL");
                }
                if (applyProductDetailVo.getDealPrice() == null) {
                    throw new ApplicationException("单价不能为NULL");
                }
                if (applyProductDetailVo.getTaxPrice() == null) {
                    throw new ApplicationException("不含税单价不能为NULL");
                }
                if (applyProductDetailVo.getTotalPrice() == null) {
                    throw new ApplicationException("总价不能为NULL");
                }
            }

            if (StringUtils.isEmpty(applySellVo.getCompanyName())) {
                throw new ApplicationException("需方不能为NULL");
            }
            if (StringUtils.isEmpty(applySellVo.getQualityStandard())) {
                throw new ApplicationException("质量标准不能为NULL");
            }

            if (StringUtils.isEmpty(applySellVo.getReceiveType())) {
                throw new ApplicationException("支付方式不能为NULL");
            }
            if (StringUtils.isEmpty(applySellVo.getShippingAddr())) {
                throw new ApplicationException("交货地不能为NULL");
            }
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);//中光亿云企业id
            applySellVo.setEnterpriseId(startVo.getEnterpriseId());//获取企业id

            // 接收货品的信息 //ApplyProductDetailVo
            List<ApplyProductDetailVo> applyBuyVoList = applySellVo.getProductJSON();//获取货品信息
            for (ApplyProductDetail detail : applyBuyVoList) {
                detail.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            }
            //原生态,不能为null get值
            applySellVo.setBatchSub(applyBuyVoList, applySellVo.getLstUpdate(), applySellVo.getLstDelete());

            PmProcessSearchVo searchVo = new PmProcessSearchVo();
            searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            searchVo.setProcessCode(BasConstants.PROCESS_APPLY_SELL); //自营销售预算流程code

            //根据对象参数获取流程主表
            PmProcess process = pmProcessService.findByProcessCode(searchVo);
            if (process == null) {
                throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
            }
            //通过当前登录人Id 获取申请人信息
            SysUserSdk userById = authOpenFacade.findUserById(applySellVo.getApplyUserId());
            if (userById != null) {
                startVo.setUserId(userById.getUserId());
                startVo.setUserName(userById.getNickName());
                startVo.setProcessId(process.getId());
                startVo.setApproveId(0L);
                applySellVo.setApproveId(0L);//代表新增
            }
            startVo.setBizEntityJson(JsonUtil.obj2Json(applySellVo));
            pmApproveService.startFlow(startVo);
            logger.info("----自营销售预算测试----");
        } catch (ApplicationException e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * 自动发起盖章申请-自营销售预算
     * @param approve
     */
    @Override
    @ServerTransactional
    public void autoInitiatedSealUsage(ApplySell applySell, PmApprove approve) {
        SELL_SCHEDULED_POOL.schedule(() -> {
            try {
                PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, approve.getEnterpriseId());
                String dictCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), applySell.getOurCompanyName());

                PmApproveSaveVo startVo = new PmApproveSaveVo();
                SealUsage usage = new SealUsage();
                usage.setEnterpriseId(applySell.getEnterpriseId());
                usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                usage.setSealDate(new Date());
                usage.setCompanyName(dictCd);
                usage.setContractNo(applySell.getContractNo());
                usage.setCustomerName(applySell.getCompanyName());
                usage.setTotalAmount(applySell.getTotalAmount());
                usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_BS);
                usage.setApplyUserId(approve.getCreateUserId());
                usage.setApplyUserName(approve.getCreateUserName());
                usage.setContractId(applySell.getContractId());
                usage.setBusinessType(applySell.getBusinessType());
                usage.setRealApproveId(approve.getId());
                usage.setBusinessFlg(true);
                usage.setFileId(applySell.getFileId());
                String entityJson = JsonUtil.obj2Json(usage);
                startVo.setBizEntityJson(entityJson);
                startVo.setProcessId(sealUsageProcess.getId());
                startVo.setDeptId(approve.getDeptId());
                startVo.setMode("A");
                startVo.setStatus(BasConstants.APPROVE_STATUS_A);
                startVo.setApproveId(0L);
                startVo.setUserId(approve.getCreateUserId());
                startVo.setUserName(approve.getCreateUserName());
                startVo.setEnterpriseId(approve.getEnterpriseId());
                pmApproveService.startFlow(startVo);
            }catch (Exception e){
                logger.error("applySell autoInitiatedSealUsage error:{}", e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);
    }
}
