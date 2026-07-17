package com.spt.bas.server.service.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.CreditFlowEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.filter.IBudgetVerifyFilter;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.IStockVirtualService;
import com.spt.bas.server.util.*;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.util.ResConditionParser;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.http.util.HTTPUtility;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.datameta.Variable;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 代采赊销
 */
@Slf4j
@Transactional
@Component("applyChargeSalesService")
public class ApplyChargeSalesServiceImpl extends BaseService<ApplyMatch> implements ApplyChargeSalesService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(50);
    @Autowired
    private ApplyChargeSalesDao applyChargeSalesDao;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private IStockVirtualService stockVirtualService;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ApplyMatchDetailDao applyMatchDetailDao;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Autowired
    private ApplyProductDetailDao applyProductDetailDao;
    @Autowired
    private IStockContractService stockContractService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private ICtrContractService contractService;
    @Autowired
    private IBsKeySequenceService bsKeySequenceService;
    @Autowired
    private ICtrContractSaveService contractSaveService;
    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsProductTypeService productTypeService;
    @Autowired
    private IBsProductConfigService bsProductConfigService;
    @Autowired
    private BsDictDataDao bsDictDataDao;
    @Autowired
    private IApplyMatchChainService applyMatchChainService;
    @Autowired
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private ICtrContractRelaService ctrContractRelaService;
    @Autowired
    private IBsCompanyCreditFlowService companyCreditFlowService;
    @Resource
    private IAutoSealPdfSignFilter autoSealPdfSignFilter;
    @Autowired
    private ICtrContractProfitService ctrContractProfitService;
    @Autowired
    private  ICtrContractDcsxApplyService ctrContractDcsxApplyService;
    @Autowired
    private ICtrLogisticsService ctrLogisticsService;
    @Autowired
    private CtrContractChainDao ctrContractChainDao;
    @Autowired
    private BsCompanyOurDao companyOurDao;
    @Resource
    private MidstreamUtil midstreamUtil;
    @Autowired
    private IBusinessRestrictRelieveService businessRestrictRelieveService;
    @Resource
    private IBudgetVerifyFilter budgetVerifyFilter;
    @Resource
    private BsContractTemplateDao contractTemplateDao;
    @Resource
    private DeptUtils deptUtils;
    @Value("${basTrade.server.approveCallBackUrl}")
    private String basTradeApproveUrl;

    private static final BigDecimal DEFAULT_CHAIN_RATE = BigDecimal.valueOf(0.0004);
    private static final BigDecimal DEFAULT_PREMIUM_RATE = BigDecimal.valueOf(0.002);
    private static final Long DEFAULT_CHAIN_DAYS = 5L;
    private static final BigDecimal PARAM_1_13 = BigDecimal.valueOf(1.13);
    private static final BigDecimal YEAR_DAYS = BigDecimal.valueOf(365);

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        log.info("applyChargeSalesService doStepIn approveNo:{}", approve.getApproveNo());
        // 业务配置开关判断
        ApplyMatch applyMatch = applyMatchDao.findOne(approve.getBizId());
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(applyMatch.getId());

        // 链条代采方、中游代采方 选择合规判断
        budgetVerifyFilter.verifyMiningAgent(applyMatch);

        // 验证预算单参数（厂商）不可为空
        budgetVerifyFilter.verifyFactoryName(applyMatch);

        // 验证发货预警
        budgetVerifyFilter.deliveryWarning(applyMatch);

        // 验证预算单是否满足发起条件(链条代采方配置余额是否充足)
        BsConfigRespVo bsConfigRespVo = budgetVerifyFilter.judgmentStart(applyMatch, matchDetailList, approve.getProcessId());

        // 1.验证预算单毛利率不可低于规定否则不可发起
        // 2.验证增加有超三天未发货订单，不能建立新单的限制
        // 3.验证如果有逾期，不能建单的限制
        budgetVerifyFilter.judgmentMatchProfit(applyMatch, matchDetailList, true);

        // 验证剩余授信额度是否可用
        budgetVerifyFilter.verifyCreditAmount(applyMatch, bsConfigRespVo);

        // 更新业务配置额度
        budgetVerifyFilter.refreshBalance(bsConfigRespVo, approve, applyMatch);

        // 绑定报价虚拟库存
        stockVirtualService.bindStockVirtual(approve, applyMatch, matchDetailList);

        applyMatchDao.save(applyMatch);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            String pairCode = bsKeySequenceService.getNextKey(BasConstants.KEY_PAIR_CODE, approve.getEnterpriseId());
            // 获得撮合信息
            ApplyMatch match = applyMatchDao.findOne(approve.getBizId());
            Long stockVirtualId = match.getStockVirtualId();
            List<BsCompanyOur> companyOurList = companyOurDao.findAllEnableAndOurCompanyFlag();
            Boolean companyOurDcFlg = false;
            Boolean companyOurSxFlg = false;
            if(CollectionUtils.isNotEmpty(companyOurList)) {
                for (BsCompanyOur companyOur : companyOurList) {
                    if(companyOur.getCompanyName().equals(match.getBuyOurCompanyName())){
                        // 如我司企业处于上游代采位置
                        companyOurDcFlg = true;
                    }else if(companyOur.getCompanyName().equals(match.getOurCompanyName())){
                        // 如我司企业处于下游赊销位置
                        companyOurSxFlg = true;
                    }
                }
            }
            String contractAttr = match.getContractAttr();
            // 获得撮合明细，先采购，后销售
            List<ApplyMatchDetail> matchList = applyMatchDetailDao.findByApplyMatchId(match.getId());
            // 采购合同Id获取，保存在销售合同里，销售合同可以找到对应的采购合同ID
            List<Long> lstBuyId = new ArrayList<>();
            List<Long> lstSellId = new ArrayList<>();
            int buyI = 0, sellI = 0;
            Boolean matchCreditFlg = false;
            for (ApplyMatchDetail detail : matchList) {
                if (!StringUtils.isEmpty(detail.getSettlementType())) {
                    matchCreditFlg = true;
                    break;
                }
            }
            List<ApplyProductDetail> sellProductList = new ArrayList<>();
            ApplyMatchDetail sellDetail = new ApplyMatchDetail();
            ApplyMatchDetail buyDetail = new ApplyMatchDetail();
            CtrContract sellContract = new CtrContract();
            for (ApplyMatchDetail machDetail : matchList) {
                // 预付定金
                BigDecimal bondAmount;
                // 预付定金比例
                BigDecimal bondRate;
                CtrContract contract = new CtrContract();
                BeanUtils.copyProperties(machDetail, contract);
                if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_B)) {
                    machDetail.setStockVirtualId(stockVirtualId);
                    buyDetail = machDetail;
                    BigDecimal dealPrice = machDetail.getDealPrice();
                    contract.setDealPrice(dealPrice);
                }
                contract.setId(null);
                contract.setApproveTransportAmount(machDetail.getTransportCost());
                contract.setApproveWarehouseAmount(machDetail.getWarehouseCost());
                contract.setApproveStevedorage(machDetail.getStevedorage());
                if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                    sellDetail = machDetail;
                    budgetVerifyFilter.maintainCreditType(contract, match);
                    contract.setCompanyCreditId(match.getCompanyCreditId());
                    contract.setFinancialServiceRate(bsCompanyService.getFinancialServiceRate(contract.getCompanyId()));
                    contract.setBsTemplateContractId(machDetail.getSellTemplateId());
                    contract.setSellContentFileId(machDetail.getSellContentTemplateId());
                    contract.setServiceContentFileId(machDetail.getServiceContentTemplateId());
                    contract.setDeliveryDateTo(machDetail.getDeliveryDate());
                    contract.setDeliveryDateFrom(machDetail.getDeliveryDate());
                    bondAmount = machDetail.getPayBondAmount();
                    bondRate = machDetail.getPayRate() == null ? BigDecimal.ZERO : machDetail.getPayRate();
                    contract.setRemark(machDetail.getReceiveRemark());
                    contract.setPayType(machDetail.getReceiveType());
                    if (machDetail.getCreditDays() == null) {
                        contract.setCreditCycle(0L);
                    } else {
                        contract.setCreditCycle(machDetail.getCreditDays().longValue());
                    }
                    sellI++;
                    contract.setSource(BasConstants.APPLY_TYPE_MS);
                    // 如果是销售合同，contract的payTime为收款时间
                    contract.setPayFullTime(machDetail.getReceiveFullTime());
                    // 授信标志
                    contract.setCreditFlg(matchCreditFlg);
                    contract.setSettlementType(machDetail.getSettlementType());
                    // 兼容以前版本 只要有结算方式字段就是赊销
                    if (machDetail.getSettlementType() != null) {
                        machDetail.setDeliveryMode(BasConstants.DELIVERY_MODE_SX);
                        contract.setDeliveryMode(BasConstants.DELIVERY_MODE_SX);
                    }
                    // 是否完成服务费开票
                    contract.setInterestBilledFlg(false);
                } else {
                    // 采购合同
                    StockVirtual stockVirtual = stockVirtualService.getEntity(stockVirtualId);
                    contract.setVirtualContractId(Objects.nonNull(stockVirtual) ? stockVirtual.getVirtualContractId() : null);
                    contract.setVirtualContractNo(Objects.nonNull(stockVirtual) ? stockVirtual.getVirtualContractNo() : null);
                    boolean creditFlgBuy = false;
                    if (StringUtils.equals(BasConstants.DELIVERY_MODE_SX, machDetail.getDeliveryMode())) {
                        creditFlgBuy = true;
                    }
                    contract.setDealAmountNoTax(machDetail.getDealAmountNotax());
                    contract.setBsTemplateContractId(machDetail.getBuyTemplateId());
                    contract.setBuyContentFileId(machDetail.getBuyContentTemplateId());
                    contract.setDeliveryDateTo(machDetail.getDeliveryDate());
                    contract.setDeliveryDateFrom(machDetail.getDeliveryDate());
                    bondAmount = machDetail.getPayBondAmount();
                    bondRate = machDetail.getPayRate() == null ? BigDecimal.ZERO : machDetail.getPayRate();
                    contract.setRemark(machDetail.getPayRemark());
                    contract.setSource(BasConstants.APPLY_TYPE_MB);
                    contract.setCreditFlg(creditFlgBuy);
                    contract.setFundViewFlag(!BasBusinessUtil.verifySpecialChainFLK(match) && !BasBusinessUtil.verifySpecialChainZJKR(match));
                    buyI++;
                }
                contract.setWarehouseAmount(machDetail.getWarehouseCost());
                contract.setTransportAmount(machDetail.getTransportCost());
                contract.setContractType(machDetail.getContractType());
                contract.setFileId(match.getFileId());
                // 我方企业抬头
                contract.setOurCompanyName(machDetail.getOurCompanyName());
                contract.setContractAttr(contractAttr);
                contract.setBusinessType(match.getBusinessType());
                contract.setAttachDeliveryTime(machDetail.getArrivalTimeExt());
                contract.setPayMode(machDetail.getPayKind());
                contract.setMatchUserId(machDetail.getMatchUserId());
                contract.setMatchUserName(machDetail.getMatchUserName());
                // 获得商品明细
                List<ApplyProductDetail> productList = productDetailService.findApplyDetail(machDetail.getId(), BasConstants.APPLY_TYPE_M);
                contract.setPayBondTime(machDetail.getPayBondTime());
                contract.setBondAmount(bondAmount);
                contract.setBondRate(bondRate);
                contract.setMatchCreditFlg(matchCreditFlg);
                // 保存撮合排序号
                contract.setPairCode(pairCode);
                //代采赊销标识
                contract.setBusinessTypeDcsx(match.getContractModel());
                contract.setContractModel(match.getContractModel());
                contract.setCustomerOrderCode(match.getCustomerOrderCode());
                contract.setDcsxFlg(true);
                BsCompany company = bsCompanyDao.findOne(contract.getCompanyId());
                if(Objects.nonNull(company)) {
                    contract.setCompanyPiccFlg(Boolean.TRUE.equals(company.getPiccFlg()));
                }
                // 设置 赊销业务类型
                String businessKind = parseBusinessKind(match.getContractModel(), companyOurDcFlg, companyOurSxFlg);
                // 代采托盘判断
                if(StringUtils.equals(match.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_TP)){
                    businessKind = BasConstants.BusinessKind.DICT_DCTP;
                }
                contract.setBusinessKind(businessKind);
                contract = contractSaveService.saveContract(contract, productList, approve, lstBuyId, machDetail);
                saveSpecialContract(match, contract, productList, approve, machDetail);

                // 记录合同id
                machDetail.setContractId(contract.getId());
                if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_B)) {
                    lstBuyId.add(contract.getId());
                } else {
                    lstSellId.add(contract.getId());
                }
                applyMatchDetailDao.save(machDetail);
            }
            ApplyCtrDCSX applyCtrDCSX = null;
            ApplyCtrDCSX targetDcsx = parseCtrDcsx(approve, match, buyDetail, sellDetail);
            if (!StringUtils.equals(targetDcsx.getOurCompanyName(), targetDcsx.getCompanyName())) {
                applyCtrDCSX = applyDcsxDao.save(targetDcsx);
                ctrContractDcsxApplyService.saveCtrContractApply(applyCtrDCSX.getId(), applyCtrDCSX.getEnterpriseId());
                this.generateChainContract(match, applyCtrDCSX, matchList);
            }
            if (!sellProductList.isEmpty()) {
                contractSaveService.saveContract(sellContract, sellProductList, approve);
            }
            // ---关联合同id
            if (sellI == 1) {
                Long sellContractId = lstSellId.get(0);
                CtrContract sell = contractService.getEntity(sellContractId);
                sell.setLinkContractId("," + Joiner.on(",").join(lstBuyId) + ",");
                for (int i = 0; i < buyI; i++) {
                    CtrContract buy = contractService.getEntity(lstBuyId.get(i));
                    buy.setLinkContractId("," + sellContractId + ",");
                }
            } else if (buyI == 1) {
                Long buyContractId = lstBuyId.get(0);
                CtrContract buy = contractService.getEntity(buyContractId);
                buy.setLinkContractId("," + Joiner.on(",").join(lstSellId) + ",");
                for (int i = 0; i < buyI; i++) {
                    CtrContract sell = contractService.getEntity(lstSellId.get(i));
                    sell.setLinkContractId("," + buyContractId + ",");
                }
            }

            //-------插入保理合同扩展表-----------------------------------------------------------------------------------------------
            if (match.getContractModel() == null) {
                match.setContractModel("false");
            }
            //审批完成自动发起盖章申请
            this.autoInitiatedSealUsage(applyCtrDCSX, match, matchList, approve);
            //自动发起定金收货款
//            if(StringUtils.equals(match.getContractModel(),BasConstants.BUSINESS_TYPE_DCSX_HDFK)){
//                List<CtrContract> byApproveId = contractService.findByApproveId(match.getApproveId());
//                this.autoReceive(match,matchList,approve,byApproveId);
//            }
            // 更新报价虚拟库存状态
            stockVirtualService.updateStockVirtualStatus(match.getStockVirtualId(), BasConstants.STOCK_VIRTUAL_STATUS_A);

            // 初始化保存合同利润统计汇总数据
            ctrContractProfitService.initContractProfit(approve, match);

            /** 物流单据添加 begin */
            Boolean addLogisticsFlg = true;
            // 查出采购销售合同列表
            List<CtrContract> contractList = contractService.findByApproveId(approve.getId());
            if(addLogisticsFlg) {
                // 查询是否存在中间链条
                List<CtrContractChain> contractChainList = ctrContractChainDao.findByApproveId(approve.getId());
                // 代采赊销中间链条
                List<ApplyCtrDCSX> dcsxList = applyDcsxDao.findByApproveId(approve.getId());

                // 设置物流单据保存参数
                CtrLogistics ctrLogistics = ctrLogisticsService.addLogisticsParams(contractList, contractChainList,dcsxList);
                if(Objects.nonNull(ctrLogistics)) {
                    // 添加物流单据
                    ctrLogisticsService.save(ctrLogistics);
                }
            }
            /** 物流单据添加 end */
            
            // 回调采销中心
            if(match.getTradeFlg() != null && match.getTradeFlg() ) {
                approveCallBackBasTrade(approve);
            }
            
        } else {
            ApplyMatch match = applyMatchDao.findOne(approve.getBizId());
            Boolean liabilityFlg = match.getLiabilityFlg();
            if (Boolean.TRUE.equals(liabilityFlg) && StringUtils.isBlank(match.getLiabilityFileId())) {
                throw new ApplicationException("请上传个人连带责任保证书(C类和D类客户，需与客户签订个人连带责任保证书!)");
            }
        }
    }
    
    /**
     * 回调采销中心
     */
    public void approveCallBackBasTrade(PmApprove approve) {
        SCHEDULED_POOL.schedule(() -> {
            String postBody = JsonUtil.obj2Json(approve);
            try {
                String json = HTTPUtility.doPostJson(basTradeApproveUrl, postBody);
                log.info("回调采销中心---{}", json);
            } catch (Exception e) {
                log.error("回调采销中心失败！{}", e.toString());
            }
        }, 4, TimeUnit.SECONDS);
    }

    private String parseBusinessKind(String contractModel, Boolean companyOurDcFlg, Boolean companyOurSxFlg) {
        String businessKind = "";
        if (StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSX, contractModel)) {
            if (companyOurDcFlg) {
                // 判断我司企业处于上游
                businessKind = BasConstants.BusinessKind.DICT_SXDC;
            } else if (companyOurSxFlg) {
                // 判断我司企业处于下游
                businessKind = BasConstants.BusinessKind.DICT_DCSX;
            } else {
                businessKind = BasConstants.BusinessKind.DICT_DCSX;
            }
        } else if (StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXBL, contractModel)) {
            businessKind = BasConstants.BusinessKind.DICT_DCSXBL;
        } else if (StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXHDFK, contractModel)) {
            if (companyOurDcFlg) {
                // 判断我司企业处于上游
                businessKind = BasConstants.BusinessKind.DICT_SXDCHDFK;
            } else if (companyOurSxFlg) {
                // 判断我司企业处于下游
                businessKind = BasConstants.BusinessKind.DICT_DCSXHDFK;
            } else {
                businessKind = BasConstants.BusinessKind.DICT_DCSXHDFK;
            }
        } else if (StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXCK, contractModel)) {
            businessKind = BasConstants.BusinessKind.DICT_DCSXCK;
        }  else {
            businessKind = BasConstants.BusinessKind.DICT_DCSX;
        }
        return businessKind;
    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyMatch applyMatch = applyMatchDao.findOne(approve.getBizId());
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(applyMatch.getId());
        if (Boolean.TRUE.equals(approve.getCompleteDismissalFlg())) {
            companyCreditFlowService.updateUsedCreditAmount(approve, applyMatch.getCompanyCreditId(), applyMatch.getSellAmount().negate(), CreditFlowEnum.CC);
        }
        if (Objects.nonNull(applyMatch) && applyMatch.getBusinessRestrictRelieveFlg() != null && applyMatch.getBusinessRestrictRelieveFlg() && applyMatch.getId() != null) {
            ApplyMatchDetail sellMatch = matchDetailList.stream().filter(s -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, s.getContractType())).findFirst().orElse(new ApplyMatchDetail());
            if (Objects.nonNull(sellMatch)) {
                BusinessRestrictRelieve businessRestrictRelieve = businessRestrictRelieveService.findByCompanyIdAndAndUserId(sellMatch.getCompanyId(), sellMatch.getMatchUserId());
                if (Objects.nonNull(businessRestrictRelieve)) {
                    int usableCount = businessRestrictRelieve.getUsableCount() + 1;
                    businessRestrictRelieveService.updateUsableCount(businessRestrictRelieve.getId(), usableCount);
                }
            }
        }

        // 更新业务配置额度
        budgetVerifyFilter.rollBackBalance(approve, applyMatch);

        // 更新报价虚拟库存状态
        stockVirtualService.updateStockVirtualStatus(applyMatch.getStockVirtualId(), BasConstants.STOCK_VIRTUAL_STATUS_N);

        // 回调采销中心
        // 回调采销中心
        if(applyMatch.getTradeFlg() != null && applyMatch.getTradeFlg() ) {
            approveCallBackBasTrade(approve);
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // 代采业务不会涉及到授信额度等信息
        // 处理判断是否所有白条业务,并返回相关参数
        PmApprove approve = pmApproveService.getEntity(vo.getApproveId());
        ApplyMatch match = applyMatchDao.findOne(approve.getBizId());
        // 使用授信
        String approveNo = approve.getApproveNo();

        if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, match.getStatus())) {
            companyCreditFlowService.updateUsedCreditAmount(approve, match.getCompanyCreditId(), match.getSellAmount().negate(), CreditFlowEnum.CC);
        }
        if (Objects.nonNull(match) && match.getBusinessRestrictRelieveFlg() != null && match.getBusinessRestrictRelieveFlg() && match.getId() != null) {
            List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(match.getId());
            ApplyMatchDetail sellMatch = matchDetailList.stream().filter(s -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, s.getContractType())).findFirst().orElse(new ApplyMatchDetail());
            if (Objects.nonNull(sellMatch)) {
                BusinessRestrictRelieve businessRestrictRelieve = businessRestrictRelieveService.findByCompanyIdAndAndUserId(sellMatch.getCompanyId(), sellMatch.getMatchUserId());
                if (Objects.nonNull(businessRestrictRelieve)) {
                    int usableCount = businessRestrictRelieve.getUsableCount() + 1;
                    businessRestrictRelieveService.updateUsableCount(businessRestrictRelieve.getId(), usableCount);
                }
            }
        }

        // 更新报价虚拟库存状态
        stockVirtualService.updateStockVirtualStatus(match.getStockVirtualId(), BasConstants.STOCK_VIRTUAL_STATUS_N);

        // 回调采销中心
        if(match.getTradeFlg() != null && match.getTradeFlg() ) {
            approveCallBackBasTrade(approve);
        }
    }

    @Override
    public BaseDao<ApplyMatch> getBaseDao() {
        return applyChargeSalesDao;
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        log.info(JsonUtil.obj2Json(pmEntity));
        log.info("--------------------------------");
        ApplyMatch entity = new ApplyMatch();
        List<ApplyProductDetail> sellProductList = new ArrayList<>();
        if (pmEntity instanceof ApplyMatchVo) {
            ApplyMatchVo vo = (ApplyMatchVo) pmEntity;
            Long stockVirtualId = vo.getStockVirtualId();
            //如果是货到付款模式则进入销售合同定金校验
            if(StringUtils.equals(BasConstants.BUSINESS_TYPE_DCSX_HDFK,vo.getContractModel())){
                ApplyMatchDetailVo detailVo = vo.getLstInsert().get(vo.getLstInsert().size() - 1);
                List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DCSX_HDFK_PAY_RATE);
                BigDecimal bigDecimal = NumberUtils.createBigDecimal(listByCategory.get(0).getDictCd());
                //比例定金
                BigDecimal multiply = detailVo.getTotalAmount().multiply(bigDecimal);
                //页面实际定金
                BigDecimal payBondAmount = vo.getLstInsert().get(vo.getLstInsert().size() - 1).getPayRateAmount();
                payBondAmount = Objects.isNull(payBondAmount) ? BigDecimal.ZERO : payBondAmount;
                if(multiply.compareTo(payBondAmount)>0){
                    throw new ApplicationException("货到付款模式下定金必须大于等于销售价的"+listByCategory.get(0).getDictName());
                }
            }
            ApplyProductDetailSaveVo pvo = new ApplyProductDetailSaveVo();
            PmApprove entity1 = pmApproveService.getEntity(vo.getApproveId());
            if (entity1 != null) {
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                vo.setDeptId(deptByUserId.getDeptId());
            }
            // copy 数据
            BeanUtils.copyProperties(vo, entity);
            // 1.保存撮合主表信息
            if(StringUtils.equals(entity.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_TP)){
                for (ApplyMatchDetailVo list : vo.getLstInsert()) {
                    if(StringUtils.equals(list.getContractType(),"B")){
                        entity.setSellOurCompanyName(list.getOurCompanyName());
                        entity.setBuyOurCompanyName(list.getOurCompanyName());
                    } else {
                        entity.setOurCompanyName(list.getOurCompanyName());
                    }
                }
            }
            entity = applyMatchDao.save(entity);
            applyMatchDetailDao.deleteByApplyMatchId(entity.getId());
            Long applyMatchId = entity.getId();
            Long enterpriseId = entity.getEnterpriseId();

            BigDecimal buyAmount = BigDecimal.ZERO;
            BigDecimal sellAmount = BigDecimal.ZERO;
            String ourCompanyName = vo.getOurCompanyName();
            String buyContractNo = null;
            applyMatchChainService.saveChainDetails(vo.getChainLstInsert(), vo.getChainLstUpdate(), vo.getChainLstDelete(), vo.getApproveId(), entity.getId());
            // 获得撮合明细表 及商品明细
            for (ApplyMatchDetailVo list : vo.getLstInsert()) {
                // 撮合明细
                ApplyMatchDetail matchDetail = new ApplyMatchDetail();
                BeanUtils.copyProperties(list, matchDetail);
                if( StringUtils.equals("S",list.getContractType())){
                    //毛利润（每吨）公式为（销售总价-采购总价-运输费-装卸费-仓储费）/吨数。
                    matchDetail.setGrossProfit(list.getGrossProfit());
                }
                matchDetail.setMatchUserId(list.getMatchUserId());
                matchDetail.setMatchUserName(list.getMatchUserName());
                if (matchDetail.getId() == null) {
                    // 生成合同号
                    if (BasConstants.CONTRACT_STATUS_B.equals(matchDetail.getContractType())) {
                        // 生成合同号
                        buyContractNo = BasBusinessUtil.composeContractNo(vo.getEnterpriseId(), vo.getDeptAbbr(), matchDetail.getContractType());
                        if (Objects.nonNull(stockVirtualId)) {
                            StockVirtual stockVirtual = stockVirtualService.getEntity(stockVirtualId);
                            if (Objects.nonNull(stockVirtual)){
                                buyContractNo = BasBusinessUtil.composeVirtualContractNo(buyContractNo, stockVirtual.getVirtualBuyType(), matchDetail.getContractType());
                            }
                        }
                        matchDetail.setContractNo(buyContractNo);
                        logger.info("applyMatch 采购我方:{}", matchDetail.getOurCompanyName());
                        entity.setBuyOurCompanyName(matchDetail.getOurCompanyName());
                    } else if (BasConstants.CONTRACT_STATUS_S.equals(matchDetail.getContractType())) {
                        logger.info("buyContractNo:{}", buyContractNo);
                        if(!StringUtils.equals(entity.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_TP)){
                            entity.setSellOurCompanyName(list.getSellOurCompanyName());
                        }
                        matchDetail.setContractNo(BasBusinessUtil.buildSellContractNo(buyContractNo));
                        dealWithLiability(entity, matchDetail);
                    }
                }

                // 获得供货商id
                Long companyId = list.getCompanyId();
                // 供货商详情
                CompanyAccountVo company = bsCompanyService.findCompanyAccountVo(companyId);
                // 账户
                matchDetail.setCompanyAccount(company.getBankAccount());
                // 银行
                matchDetail.setCompanyBank(company.getBankName());
                // 企业名称
                matchDetail.setCompanyName(company.getCompanyName());
                // 联系人
                matchDetail.setContactName(company.getContactName());
                // 关联主表
                matchDetail.setApplyMatchId(applyMatchId);
                matchDetail.setEnterpriseId(enterpriseId);
                // 质量标准
                matchDetail.setQualityStandard(entity.getQualityStandard());
                // 2.保存撮合明细表
                matchDetail = applyMatchDetailDao.save(matchDetail);
                pvo.setApplyType(BasConstants.APPLY_TYPE_M);
                pvo.setApplyId(matchDetail.getId());
                pvo.setEnterpriseId(enterpriseId);
                // 保存撮合明细表
                productDetailService.saveDetailMatch(list, vo, pvo);
                // 重新计算收款比例 付款比例
                List<ApplyProductDetail> productArr = productDetailService.findApplyDetail(matchDetail.getId(), BasConstants.APPLY_TYPE_M);
                BigDecimal totalPrice = BigDecimal.ZERO;
                for (ApplyProductDetail detail : productArr) {
                    totalPrice = totalPrice.add(detail.getTotalPrice());
                    // 采购来源 B:自营采购 G:供应商
                    if (StringUtils.equals("B", matchDetail.getBuySource())) {
                        sellProductList.add(detail);
                    }
                }
                if (BasConstants.APPLY_TYPE_S.equals(matchDetail.getContractType())) {
                    BigDecimal receiveRate = matchDetail.getReceiveRate();
                    if (receiveRate == null) {
                        BigDecimal receiveBondAmount = totalPrice.multiply(BigDecimal.ZERO);
                        matchDetail.setReceiveBondAmount(receiveBondAmount);
                    }else{
                        BigDecimal receiveBondAmount = totalPrice.multiply(receiveRate);
                        matchDetail.setReceiveRate(receiveRate);
                        matchDetail.setReceiveBondAmount(receiveBondAmount);
                    }
                    sellAmount = sellAmount.add(totalPrice);
                    // 如果是远期（托盘业务）直接取前端值
                    if ("F".equals(vo.getContractAttr())) {
                        sellAmount = matchDetail.getTotalAmount();
                    }
                    entity.setSellCompanyId(companyId);
                    matchDetail.setTotalAmount(sellAmount);

                } else {
                    BigDecimal payRate = matchDetail.getPayRate();
                    if (payRate == null) {
                        BigDecimal payBondAmount = totalPrice.multiply(BigDecimal.ZERO);
                        matchDetail.setPayBondAmount(payBondAmount);
                    }else{
                        BigDecimal payBondAmount = totalPrice.multiply(payRate);
                        matchDetail.setPayRate(payRate);
                        matchDetail.setPayBondAmount(payBondAmount);
                    }
                    buyAmount = buyAmount.add(totalPrice);
                    entity.setBuyCompanyId(companyId);
                    matchDetail.setTotalAmount(buyAmount);
                }
                // 保存设置的比例
                if (StringUtils.equals(BasConstants.ATTACH_DELIVERY_TIME_K, matchDetail.getArrivalTimeExt())) {
                    matchDetail.setDeliveryTime(null);
                }
                matchDetail.setPayBondAmount(list.getPayRateAmount());
                if (Objects.isNull(matchDetail.getPayRate())){
                    matchDetail.setPayRate(BigDecimal.ZERO);
                }
                if (Objects.isNull(matchDetail.getReceiveRate())){
                    matchDetail.setReceiveRate(BigDecimal.ZERO);
                }
                applyMatchDetailDao.save(matchDetail);
            }
            if (vo.getRemoveArrStr() != null) {
                // 解析数组
                List<Long> removeList = JSON.parseArray(vo.getRemoveArrStr(), Long.class);
                for (Long id : removeList) {
                    // 删除供应商信息以及关联的商品信息
                    applyMatchDetailDao.delete(id);
                    applyProductDetailDao.deleteDetail(id, BasConstants.APPLY_TYPE_M);
                }
            }

            entity.setBuyAmount(buyAmount);
            entity.setSellAmount(sellAmount);
            BsDictData data = BsCompanyOurUtil.getCompanyOurToBsDictData(entity.getEnterpriseId(),ourCompanyName);
            if (data != null) {
                entity.setOurCompanyName(data.getDictName());
            }
            if (!sellProductList.isEmpty()) {
                String internalContractNo = BasBusinessUtil.composeContractNo(vo.getEnterpriseId(), vo.getDeptAbbr(),
                        BasConstants.APPLY_TYPE_S);
                entity.setSellContractNo(internalContractNo);
                Long stockContractId = sellProductList.get(0).getStockContractId();
                StockContract stockContract = stockContractService.getEntity(stockContractId);
                if (stockContract != null) {
                    CtrContract contract = contractService.getEntity(stockContract.getBuyContractId());
                    entity.setBuyUserId(contract.getMatchUserId());
                    entity.setBuyUserName(contract.getMatchUserName());
                }
            } else {
                entity.setSellContractNo(null);
                entity.setBuyUserId(null);
                entity.setBuyUserName(null);
            }
            applyMatchDao.save(entity);
        } else {
            entity = (ApplyMatch) pmEntity;

            entity = applyMatchDao.save(entity);

            applyMatchChainService.updateChainApproveId(entity.getId(), entity.getApproveId());
        }
        return entity;
    }

    /**
     * 生成中游合同
     *
     * @param approve    审批单
     * @param match      预算单
     * @param buyDetail  采购明细
     * @param sellDetail 销售明细
     * @return 中游合同
     */
    @Override
    public ApplyCtrDCSX parseCtrDcsx(PmApprove approve, ApplyMatch match, ApplyMatchDetail buyDetail, ApplyMatchDetail sellDetail){
        ApplyCtrDCSX entity = new ApplyCtrDCSX();
        //中游同步上游定金日期/定金
        if(Objects.nonNull(buyDetail.getPayBondTime())&&Objects.nonNull(buyDetail.getPayBondAmount())){
            entity.setPayBondTime(buyDetail.getPayBondTime());
            entity.setBondAmount(buyDetail.getPayBondAmount());
            entity.setBondRate(buyDetail.getPayRate());
        }
        //每吨利润
        entity.setGrossProfit(sellDetail.getGrossProfit());
        //装卸费
        entity.setStevedorage(sellDetail.getStevedorage());
        // 品种
        entity.setProductBrand(match.getProductCd());
        // 牌号
        entity.setProductNum(match.getBrandNumber());
        // 数量(吨)
        entity.setTotalNumber(match.getDealNumber());
        // 厂商
        entity.setFactoryName(match.getFactoryName());
        // 包装规格
        entity.setWrapSpecs(match.getWrapSpecs());
        // 质量标准
        entity.setQualityStandard(match.getQualityStandard());
        // 补充条款
        entity.setExtraTerm(buyDetail.getExtraTerm());
        // 单价
        entity.setBuyPrice(buyDetail.getDealPrice());
        // 交货方式
        entity.setDeliveryType(sellDetail.getDeliveryType());
        // 交货日期
        entity.setDeliveryDateTo(buyDetail.getDeliveryDate());
        // 下游交货日期
        entity.setSellDeliveryDate(sellDetail.getDeliveryDate());
        // 交货地点
        String contactAddr = sellDetail.getContactAddr();
        entity.setDeliveryAddr(sellDetail.getDeliveryAddr() + (StringUtils.isNotBlank(contactAddr) ? contactAddr : ""));
        // 合同状态 N-新增
        entity.setStatus(BasConstants.APPROVE_STATUS_B);
        // 账期
        if(sellDetail.getCreditDays()!=null){
            entity.setCreditDays(Long.valueOf(sellDetail.getCreditDays()));
            entity.setCreditCycle(Long.valueOf(sellDetail.getCreditDays()));
        }
        // 签订日期
        entity.setContractTime(new Date());
        if(StringUtils.equals(match.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_TP)){
            // 业务类型
            entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_TP);
            // 付款日期
            entity.setPayFullTime(buyDetail.getPayFullTime());
        } else {
            // 业务类型
            entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB);
            // 付款日期
            entity.setPayFullTime(sellDetail.getReceiveFullTime());
        }
        // 上游付款日期
        entity.setBuyPayFullTime(buyDetail.getPayFullTime());
        // 业务员ID
        entity.setMatchUserId(sellDetail.getMatchUserId());
        // 业务员姓名
        entity.setMatchUserName(sellDetail.getMatchUserName());
        // 合作业务员ID
        entity.setCooperationMatchUserId(buyDetail.getMatchUserId());
        // 合作业务员姓名
        entity.setCooperationMatchUserName(buyDetail.getMatchUserName());
        SysDeptSdk sysDept = deptUtils.getDeptByUserIdAndDeptType(sellDetail.getMatchUserId(), PmConstants.NODE_TYPE_DEPT);
        logger.info("getDeptByUserId userId:{}", sellDetail.getMatchUserId());
        if (Objects.nonNull(sysDept)) {
            entity.setDeptId(sysDept.getDeptId());
            logger.info("getDeptByUserId userId:{}, deptId:{}, deptName:{}", sellDetail.getMatchUserId(), sysDept.getDeptId(), sysDept.getDeptName());
        }
        // 企业账套ID
        entity.setEnterpriseId(sellDetail.getEnterpriseId());
        // 生成合同号
        if (entity.getId() == null) {
            String replace = StrUtil.replace(sellDetail.getContractNo(), "SPTS", "SPTX");
            replace = StrUtil.replace(replace, "KCS", "KCX");
            replace = StrUtil.replace(replace, "XYS", "XYX");
            entity.setContractNo(replace);
        }
        // 审批ID
        entity.setApproveId(approve.getId());
        // 预算编号
        entity.setBudgetNo(approve.getApproveNo());
        // 企业名称
        entity.setCompanyId(sellDetail.getCompanyId());
        entity.setCompanyName(match.getSellOurCompanyName());
        entity.setOurCompanyName(sellDetail.getOurCompanyName());
        Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
        BsCompanyDcsx companyDcsx = companyConfigMap.get(match.getSellOurCompanyName());
        if (Objects.nonNull(companyDcsx)) {
            if (companyDcsx.getChainDays() < 0L) {
                if(StringUtils.equals(match.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_TP)){
                    long realCreditDays = 1L;
                    entity.setCreditCycle(realCreditDays);
                    entity.setCreditDays(realCreditDays);
                    // 交货日期
                    entity.setDeliveryDateTo(buyDetail.getDeliveryDate());
                    entity.setPayFullTime(buyDetail.getPayFullTime());
                } else {
                    long realCreditDays = DateOperator.compareDays(sellDetail.getDeliveryDate(), sellDetail.getReceiveFullTime()) + 1L;
                    realCreditDays = Math.max(realCreditDays, 1L);
                    entity.setCreditCycle(realCreditDays);
                    entity.setCreditDays(realCreditDays);
                    // 交货日期
                    entity.setDeliveryDateTo(sellDetail.getDeliveryDate());
                    entity.setPayFullTime(sellDetail.getReceiveFullTime());
                }

            } else {
                entity.setCreditCycle(companyDcsx.getChainDays());
                entity.setCreditDays(companyDcsx.getChainDays());
                entity.setPayFullTime(DateOperator.addDays(buyDetail.getDeliveryDate(), companyDcsx.getChainDays().intValue() - 1));
            }

            if (StringUtils.equals(BasConstants.CALCULATE_TYPE_2, companyDcsx.getCalculateType())){
                // 中游交货日期 = 上游付全款日期
                entity.setDeliveryDateTo(buyDetail.getPayFullTime());
                // 中游付全款日期 = 上游付全款日期 + 固定账期5天
                entity.setPayFullTime(DateOperator.addDays(buyDetail.getPayFullTime(), companyDcsx.getChainDays().intValue()));
            }
        }
        boolean specialChainQGYS = BasBusinessUtil.verifySpecialChainQGYS(match);
        if (Boolean.TRUE.equals(specialChainQGYS)){
            entity.setCompanyName(match.getBuyOurCompanyName());
            entity.setOurCompanyName(match.getSellOurCompanyName());
        }
        BigDecimal dealPrice = midstreamUtil.generateMidstream(entity, match, buyDetail, sellDetail);
        if (Objects.isNull(dealPrice) || dealPrice.compareTo(BigDecimal.ZERO) == 0){
            log.info("generateMidstream warn result 0");
            dealPrice = calculatePrice(match, entity.getCreditDays(), companyConfigMap);
        }
        entity.setDealPrice(dealPrice);
        entity.setTotalAmount(match.getDealNumber().multiply(dealPrice).setScale(2, RoundingMode.HALF_UP));
        CtrContract buyContract = ctrContractDao.findByApproveIdAndContractType(approve.getId(), BasConstants.CONTRACT_TYPE_B);
        if (Objects.nonNull(buyContract) && Objects.nonNull(buyContract.getVirtualContractId())) {
            entity.setVirtualFlg(true);
        }
        return entity;
    }
    
    @Override
    public ApplyCtrDCSX parseCtrDcsxByApproveId(Long approveId){
        if(Objects.isNull(approveId)) {
            return null;
        }
        PmApprove approve = pmApproveService.getEntity(approveId);
        ApplyMatch match = applyMatchDao.findByApproveId(approveId);
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(match.getId());
        List<ApplyMatchDetail> s1 = matchDetailList.stream().filter(s -> s.getContractType().equals(BasConstants.CONTRACT_TYPE_B)).collect(Collectors.toList());
        List<ApplyMatchDetail> s2 = matchDetailList.stream().filter(s -> s.getContractType().equals(BasConstants.CONTRACT_TYPE_S)).collect(Collectors.toList());
        ApplyMatchDetail buyDetail = s1.get(0);
        ApplyMatchDetail sellDetail = s2.get(0);
        return parseCtrDcsx(approve, match, buyDetail, sellDetail);
    }

    /**
     * 个人连带责任书逻辑判断
     * 1.赊销合同；
     * 2.客户等级C类、D类；
     * 3.销售-交货日期不等于销售-收全款日期；
     *
     * @param applyMatch
     * @param applyMatchDetail
     */
    private void dealWithLiability(ApplyMatch applyMatch, ApplyMatchDetail applyMatchDetail) {
        if (Objects.isNull(applyMatch) || Objects.isNull(applyMatchDetail)) {
            return;
        }
        String settlementType = applyMatchDetail.getSettlementType();
        // 更新是否需要签署连带责任保证书标识
        BsCompany company = bsCompanyService.getEntity(applyMatchDetail.getCompanyId());
        // 赊销合同
        boolean param1 = StringUtils.isNotBlank(settlementType);

        // C、D 类客户
        boolean param2 = Objects.nonNull(company) && BasConstants.LIABILITY_COMPANY_GRADE.contains(company.getCompanyGrade());

        // 销售-交货日期不等于销售-收全款日期
        boolean param3 = !Objects.equals(applyMatchDetail.getDeliveryDate(), applyMatchDetail.getReceiveFullTime());

        applyMatch.setLiabilityFlg(param1 && param2 && param3);
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        //合同尾号，品名/牌号/数量，赊销模式，上游-中游-下游，采购总价，账期
        ApplyMatch vo = (ApplyMatch) pmEntity;
        Long matchId = vo.getId();
        List<ApplyMatchDetail> list = applyMatchDetailDao.findByApplyMatchId(matchId);
        ApplyMatchDetail buy = list.get(0);
        ApplyMatchDetail sell = list.get(1);
        String contractNo=buy.getContractNo().replaceAll("\\D", "");
        Long applyId = buy.getId();
        List<ApplyProductDetail> productlist = productDetailService.findApplyDetail(applyId, BasConstants.APPLY_TYPE_M);
        String contractModel = vo.getContractModel();
        StringBuffer productNameAndBrand = new StringBuffer("");
        BigDecimal sumNumber = BigDecimal.ZERO;
        for (ApplyProductDetail applyProductDetail : productlist) {
            String realOutNumber = NumberUtil.formatNumber(applyProductDetail.getDealNumber(), "#.###");
            String[] title = applyProductDetail.getProductCd().split("_");
            if (title[0].equals("SL")) {
                productNameAndBrand.append(applyProductDetail.getProductName() + "/" + applyProductDetail.getBrandNumber() + "/" + realOutNumber + "吨");
            } else {
                productNameAndBrand.append(applyProductDetail.getProductName() + "/" + realOutNumber + "吨");
            }
            sumNumber = sumNumber.add(applyProductDetail.getDealNumber());
        }
        String productNameAndBrandStr = productNameAndBrand.toString();
        if(contractModel==null){
            contractModel="普通模式";
        }
        if(vo.getContractModel() == null){
            vo.setContractModel("PT");
        }
        switch (vo.getContractModel()){
            case "BL":
                contractModel="保理模式";
                break;
            case "DCSXBL":
                contractModel="保理模式";
                break;
            case "PT":
                contractModel="普通模式";
                break;
            case "DCSX":
                contractModel="普通模式";
                break;
            case "HDFK":
                contractModel="货到付款模式";
                break;
            case "DCSXHDFK":
                contractModel="货到付款模式";
                break;
            case "DCSXCK":
                contractModel="代采出口模式";
                break;
            case "":
                contractModel="普通模式";
                break;
        }
        String companyName1 = RuleUtil.companyNameSubString(buy.getCompanyName());
        String companyName2 = RuleUtil.companyNameSubString(vo.getSellOurCompanyName());
        String companyName3 = RuleUtil.companyNameSubString(sell.getCompanyName());
        String company="";
        if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)&&StringUtils.isNotBlank(companyName3)){
            company=companyName1+"-"+companyName2+"-"+companyName3;
        }
        String receiptArrived = sell.getReceiptArrivedFlg() ? "[货到票到]" : "";
        Boolean tradeFlg = vo.getTradeFlg();
        String tradeFlgStr = "";
        if(Boolean.TRUE.equals(tradeFlg)){
            tradeFlgStr = "[采销中心]";
        }
        return SubjectUtil.formatSubject(receiptArrived + contractNo, productNameAndBrandStr, contractModel,
                company, SubjectUtil.formatMoney(buy.getTotalAmount(), RuleUtil.monetaryUnit),sell.getCreditDays()==null?"":sell.getCreditDays() + RuleUtil.dateUnit, tradeFlgStr);
    }

    /**
     * 自动发起盖章申请-代采赊销预算
     *
     * @param applyCtrDcsx
     * @param approve
     */
    @Override
    @ServerTransactional
    public void autoInitiatedSealUsage(ApplyCtrDCSX applyCtrDcsx, ApplyMatch applyMatch, List<ApplyMatchDetail> matchDetailList, PmApprove approve) {
        SCHEDULED_POOL.schedule(() -> {
            PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, approve.getEnterpriseId());
            List<CtrContract> contractList = ctrContractDao.findContractIdByApproveId(applyMatch.getApproveId());
            CtrContract specialContract = ctrContractDao.findSpecialChainByApproveId(applyMatch.getApproveId());
            for (ApplyMatchDetail detail : matchDetailList) {
                try {
                    //合同盖章签署通知
                    PmApproveSaveVo startVo = new PmApproveSaveVo();
                    SealUsage usage = new SealUsage();
                    CtrContract contract = contractList.stream().filter(c -> StringUtils.equals(detail.getContractType(), c.getContractType())).findAny().orElse(new CtrContract());
                    if (detail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                        usage.setFileId(detail.getSellContentTemplateId());

                        BsCompany company = bsCompanyService.getEntity(detail.getCompanyId());
                        if (Boolean.TRUE.equals(company.getOpenCfcaFlg())) {
                            SMSUtils.sendContractNo(company.getCompanyPhone(), detail.getContractNo());
                        }
                    } else {
                        usage.setFileId(detail.getBuyContentTemplateId());
                    }
                    if (StringUtils.equals(BasConstants.APPLY_TYPE_B, contract.getContractType()) && StringUtils.isNotBlank(contract.getVirtualContractNo())) {
                        continue;
                    }
                    usage.setOwnRegion(contract.getOwningRegion());
                    usage.setEnterpriseId(detail.getEnterpriseId());
                    usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                    usage.setSealDate(new Date());
                    String companyCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), detail.getOurCompanyName());
                    if (StringUtils.isNotBlank(companyCd)) {
                        usage.setCompanyName(companyCd);
                    } else {
                        usage.setCompanyName(detail.getOurCompanyName());
                    }
                    usage.setContractNo(detail.getContractNo());
                    usage.setCustomerName(detail.getCompanyName());
                    usage.setTotalAmount(detail.getTotalAmount());
                    usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_BS);
                    usage.setApplyUserId(approve.getCreateUserId());
                    usage.setApplyUserName(approve.getCreateUserName());
                    usage.setContractId(detail.getContractId());
                    usage.setBusinessType(detail.getContractType());
                    usage.setRealApproveId(approve.getId());
                    usage.setBusinessFlg(true);
                    if(contract.getPayFullTime()!=null){
                        usage.setRemark("付款时间" + contract.getPayFullTime() + "/" + "合同金额" + contract.getTotalAmount() + "/" + contract.getProductsName());
                    } else {
                        usage.setRemark("合同金额" + contract.getTotalAmount() + "/" + contract.getProductsName());
                    }
                    usage.setVirtualType(contract.getContractNo().replaceAll("\\d", ""));
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
                    startVo.setAutoStartMessage("预算审批完成，自动发起盖章申请");
                    startVo.setAutoStartFlgReal(true);
                    PmApprove pmApprove = pmApproveService.startFlow(startVo);
                    autoSealPdfSignFilter.generateSealPDFSign(pmApprove, contract);
                } catch (Exception e) {
                    logger.error("autoInitiatedSealUsage error", e);
                }
            }

            if (Objects.nonNull(specialContract)) {
                try {
                    //合同盖章签署通知
                    PmApproveSaveVo startVo = new PmApproveSaveVo();
                    SealUsage usage = new SealUsage();
                    usage.setEnterpriseId(specialContract.getEnterpriseId());
                    usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                    usage.setSealDate(new Date());
                    String companyCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), specialContract.getOurCompanyName());
                    if (StringUtils.isNotBlank(companyCd)) {
                        usage.setCompanyName(companyCd);
                    } else {
                        usage.setCompanyName(specialContract.getOurCompanyName());
                    }
                    usage.setContractNo(specialContract.getContractNo());
                    usage.setCustomerName(specialContract.getCompanyName());
                    usage.setTotalAmount(specialContract.getTotalAmount());
                    usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_BS);
                    usage.setApplyUserId(approve.getCreateUserId());
                    usage.setApplyUserName(approve.getCreateUserName());
                    usage.setContractId(specialContract.getId());
                    usage.setBusinessType(specialContract.getContractType());
                    usage.setRealApproveId(approve.getId());
                    usage.setBusinessFlg(true);
                    if (specialContract.getPayFullTime() != null) {
                        usage.setRemark("付款时间" + specialContract.getPayFullTime() + "/" + "合同金额"
                                + specialContract.getTotalAmount() + "/" + specialContract.getProductsName());
                    } else {
                        usage.setRemark("合同金额" + specialContract.getTotalAmount() + "/" + specialContract.getProductsName());
                    }
                    usage.setVirtualType(specialContract.getContractNo().replaceAll("\\d", ""));
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
                    startVo.setAutoStartMessage("预算审批完成，自动发起盖章申请");
                    startVo.setAutoStartFlgReal(true);
                    startVo.setHideOut(StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, specialContract.getCompanyName()) ? "1" : "0");
                    PmApprove pmApprove = pmApproveService.startFlow(startVo);
                    autoSealPdfSignFilter.generateFLKSealPDFSign(pmApprove, specialContract);
                    autoSealPdfSignFilter.generateFLKPurchaseOrder(pmApprove, specialContract);
                } catch (Exception e) {
                    logger.info("autoInitiatedSealUsage specialContract error:{}", e.getMessage());
                }
            }

            if (Objects.nonNull(applyCtrDcsx)) {
                try {
                    PmProcess process;
                    if (StringUtils.equals(BasConstants.APPLY_SEAL_USAGE_DCTP, applyCtrDcsx.getBusinessType())) {
                        process = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.APPLY_SEAL_USAGE_DCTP, approve.getEnterpriseId());
                    } else {
                        process = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.APPLY_SEAL_USAGE_DCSX, approve.getEnterpriseId());
                    }
                    List<ApplyCtrDCSX> chainList = applyDcsxDao.findByApproveId(applyCtrDcsx.getApproveId());
                    for (ApplyCtrDCSX entity : chainList) {
                        PmApproveSaveVo startVo = new PmApproveSaveVo();
                        SealUsageDCSX usage = new SealUsageDCSX();
                        //品种
                        BsProductType productTypeCode = productTypeService.findProductTypeCode(entity.getProductBrand());
                        usage.setApplyUserId(approve.getCreateUserId());
                        usage.setProductBrand(productTypeCode.getTypeName());
                        //牌号
                        usage.setProductNum(entity.getProductNum());
                        //数量
                        usage.setTotalNumber(entity.getTotalNumber());
                        //厂商
                        usage.setFactoryName(entity.getFactoryName());
                        //包装规格
                        String wrapSpecs = DictUtil.getValue(BasConstants.DICT_TYPE_IMPORTBUYPACKING, entity.getWrapSpecs());
                        usage.setWrapSpecs(wrapSpecs);
                        //质量标准
                        String qualityStandard = DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, entity.getQualityStandard());
                        usage.setQualityStandard(qualityStandard);
                        //我方
                        usage.setOurCompanyName(entity.getOurCompanyName());
                        //代采购方
                        usage.setCompanyName(entity.getCompanyName());
                        //采购单价
                        usage.setBondAmount(entity.getDealPrice());
                        //合同总额
                        usage.setTotalAmount(entity.getTotalAmount());
                        //回款周期
                        usage.setCreditDays(entity.getCreditDays());
                        //付款日期
                        usage.setPayBondTime(entity.getPayBondTime());
                        usage.setLastPayDate(entity.getPayFullTime());
                        usage.setPayFullTime(entity.getPayFullTime());
                        usage.setBuyPayFullTime(entity.getBuyPayFullTime());
                        usage.setDealPrice(entity.getDealPrice());

                        // 关联审批id
                        usage.setRealApproveId(approve.getId());

                        usage.setEnterpriseId(entity.getEnterpriseId());
                        usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                        usage.setSealDate(new Date());
                        usage.setApproveId(entity.getApproveId());
                        usage.setContractNo(entity.getContractNo());
                        usage.setTotalAmount(entity.getTotalAmount());
                        usage.setContractId(entity.getId());
                        usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_DCSX);
                        usage.setApplyUserId(approve.getCreateUserId());
                        usage.setApplyUserName(approve.getCreateUserName());
                        usage.setBusinessType(BasConstants.CONTRACT_TYPE_X);
                        usage.setBusinessFlg(true);
                        usage.setBuyPrice(entity.getBuyPrice());
                        usage.setDeliveryType(entity.getDeliveryType());
                        usage.setDeliveryDate(entity.getDeliveryDateTo());
                        usage.setSellDeliveryDate(entity.getSellDeliveryDate());
                        usage.setDeliveryAddr(entity.getDeliveryAddr());
                        if(entity.getPayFullTime()!=null){
                            usage.setRemark("付款时间" + DateOperator.formatDate(entity.getPayFullTime()) + "/" + "合同金额" + entity.getTotalAmount() + "/" + "数量" + entity.getTotalNumber());
                        } else {
                            usage.setRemark("合同金额" + entity.getTotalAmount() + "/" + "数量" + entity.getTotalNumber());
                        }
                        String entityJson = JsonUtil.obj2Json(usage);
                        startVo.setBizEntityJson(entityJson);
                        startVo.setProcessId(process.getId());
                        startVo.setDeptId(approve.getDeptId());
                        startVo.setApproveId(0L);
                        startVo.setMode("A");
                        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
                        startVo.setUserId(approve.getCreateUserId());
                        startVo.setUserName(approve.getCreateUserName());
                        startVo.setEnterpriseId(approve.getEnterpriseId());
                        startVo.setAutoStartMessage("预算审批完成，自动发起盖章申请");
                        startVo.setAutoStartFlgReal(true);
                        PmApprove pmApprove = pmApproveService.startFlow(startVo);
                        // autoSealPdfSignFilter.generateSealPDFSignDCSX(pmApprove, entity);
                        autoSealPdfSignFilter.generateSealPDFSignDCSXV2(pmApprove, entity);
                    }
                } catch (Exception e) {
                    logger.error("chargeSales autoInitiatedSealUsage error:{}", e.getMessage());
                }
            }
        }, 4, TimeUnit.SECONDS);
    }

    /**
     * 生成中间链条合同
     * @param applyMatch
     * @param matchDetailList
     * @return
     */
    @Override
    @ServiceTransactional
    public void generateChainContract(ApplyMatch applyMatch, ApplyCtrDCSX applyCtrDCSX, List<ApplyMatchDetail> matchDetailList) {
        SCHEDULED_POOL.schedule(() -> {
            try {
                List<ApplyCtrDCSX> chainContractList = new ArrayList<>();
                // 中间链数据
                List<ApplyMatchChain> matchChains = applyMatchChainService.findMatchChains(applyMatch.getId());

                if (CollectionUtils.isEmpty(matchChains)){
                    return;
                }

                // 中间链数据配置项
                Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();

                // 我方抬头列表
                List<BsDictData> ourCompanyList = BsCompanyOurUtil.getCompanyOurToBsDictDataList();

                // 生成中间链中游合同
                List<ApplyMatchChain> chainList = formatChain(matchChains, applyMatch, matchDetailList, companyConfigMap);
                int sortNumber = 1;
                for (int i = 0; i < chainList.size(); i++) {
                    ApplyCtrDCSX ctrDCSX;
                    if (i < chainList.size() - 1) {
                        ApplyMatchChain matchChain = chainList.get(i);
                        ApplyMatchChain matchChainPlus = chainList.get(i + 1);

                        // 中间链中游合同-对方企业
                        String chainCompanyName = matchChain.getChainCompanyName();

                        // 中间链中游合同-我方企业
                        String chainCompanyNamePlus = matchChainPlus.getChainCompanyName();

                        // 中间链中游合同-合同单价
                        BigDecimal dealPrice = matchChainPlus.getBuyDealPrice();

                        // 1.合同对方企业与我方企业不可一致
                        // 2.合同企业不包含于我方抬头列表的中游合同不需要生成
                        if (!StringUtils.equals(chainCompanyName, chainCompanyNamePlus) && checkChainOurCompanyName(chainCompanyName, chainCompanyNamePlus, ourCompanyList)) {
                            ctrDCSX = parseChainContract(applyCtrDCSX, companyConfigMap, chainCompanyName, chainCompanyNamePlus, dealPrice, sortNumber);
                            chainContractList.add(ctrDCSX);
                            sortNumber++;
                        }
                    }
                }
                applyDcsxDao.saveAll(chainContractList);
            } catch (Exception e) {
                log.error("generateChainContract error contractNo:{}", applyCtrDCSX.getContractNo());
            }
        }, 4, TimeUnit.SECONDS);
    }

    /**
     * 获取青岛民生银行配置项
     * @param enterpriseId
     * @return
     */
    @Override
    public BsBankVo getSpecialBank(Long enterpriseId) {
        List<BsDictData> bsDictData = bsDictDataDao.loadDatasByTypeCd(BasConstants.DICT_TYPE_BANK_DATA, enterpriseId);
        if (CollectionUtils.isEmpty(bsDictData)){
            return null;
        }
        BsBankVo bankVo = new BsBankVo();
        bsDictData.forEach(dict->{
            String dictCd = dict.getDictCd();
            String dictName = dict.getDictName();
            if (StringUtils.equals(BasConstants.DICT_COMPANY_NAME, dictCd)){
                bankVo.setCompanyName(dictName);
            }
            if (StringUtils.equals(BasConstants.DICT_TAX_NO, dictCd)){
                bankVo.setTaxNo(dictName);
            }
            if (StringUtils.equals(BasConstants.DICT_BANK_NAME, dictCd)){
                bankVo.setBankName(dictName);
            }
            if (StringUtils.equals(BasConstants.DICT_BANK_NUM, dictCd)){
                bankVo.setBankNum(dictName);
            }
        });
        return bankVo;
    }

    /**
     * 中间链中游合同抬头不包含于我方抬头列表的中游合同不需要生成
     *
     * @param companyName    中间链中游合同-对方企业
     * @param ourCompanyName 中间链中游合同-我方企业
     * @param ourCompanyList 我方抬头企业列表
     * @return
     */
    private boolean checkChainOurCompanyName(String companyName,String ourCompanyName, List<BsDictData> ourCompanyList) {
        boolean flg = true;
        if (CollectionUtils.isNotEmpty(ourCompanyList)) {
            flg = ourCompanyList.stream().anyMatch(d -> StringUtils.equals(d.getDictName(), ourCompanyName)) ||
                    ourCompanyList.stream().anyMatch(d -> StringUtils.equals(d.getDictName(), companyName));
        }
        return flg;
    }

    public List<ApplyMatchChain> formatChain(List<ApplyMatchChain> matchChains, ApplyMatch applyMatch, List<ApplyMatchDetail> matchDetailList, Map<String, BsCompanyDcsx> companyConfigMap) {
        List<ApplyMatchChain> chainList = new ArrayList<>();
        ApplyMatchChain firstChain = new ApplyMatchChain(applyMatch.getBuyOurCompanyName());
        chainList.add(firstChain);

        if (CollectionUtils.isEmpty(matchChains)){
            ApplyMatchDetail buyMatchDetail = matchDetailList.stream().filter(m -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, m.getContractType())).findFirst().orElse(null);

            BsCompanyDcsx companyConfig = companyConfigMap.get(applyMatch.getBuyOurCompanyName());
            BigDecimal premiumRate = Objects.nonNull(companyConfig) ? companyConfig.getPremiumRate() : DEFAULT_PREMIUM_RATE;
            BigDecimal middleChainPrice = buyMatchDetail.getDealPrice().multiply(BigDecimal.ONE.add(premiumRate));
            ApplyMatchChain middle1Chain = new ApplyMatchChain(applyMatch.getSellOurCompanyName(), middleChainPrice);
            chainList.add(middle1Chain);
        }else{
            chainList.addAll(matchChains);

            ApplyMatchChain middleChain = new ApplyMatchChain(applyMatch.getSellOurCompanyName(), matchChains.get(matchChains.size() - 1).getSellDealPrice());
            chainList.add(middleChain);
        }
        return chainList;
    }

    /**
     *  中间链中游合同单价生成
     *  1.【塑融云 - 浙江网塑】 ：单价 = 采购单价 * (1 + 0.0005 * 5天)
     *  2.【其它中游企业 - 浙江网塑】 ：单价 = 采购单价 * (1 + 0.0004 * 5天) * 1.13
     * @param applyMatch
     * @param companyConfigMap
     * @return
     */
    public BigDecimal calculatePrice(ApplyMatch applyMatch, Long creditDays, Map<String, BsCompanyDcsx> companyConfigMap) {
        String sellOurCompanyName = applyMatch.getSellOurCompanyName();

        // 中间链合同计算比率默认值0.0004
        BigDecimal chainRate = DEFAULT_CHAIN_RATE;

        // 中间链周期默认值 5天
        BigDecimal chainDays = BigDecimal.valueOf(DEFAULT_CHAIN_DAYS);

        // 中间链单价计算类型默认值 2【固定收益X周期（税后）】
        String calculateType = BasConstants.CALCULATE_TYPE_2;

        // 中间链单价计算年化收益默认值 0.1
        BigDecimal annualizedRevenue = BigDecimal.valueOf(0.1);

        // 中间链固定加价默认值0
        BigDecimal premiumAmount = BigDecimal.ZERO;

        // 账期
        BigDecimal credit_days = (Objects.isNull(creditDays) || creditDays <= 0) ? BigDecimal.ONE : BigDecimal.valueOf(creditDays);

        BsCompanyDcsx companyConfig = companyConfigMap.get(sellOurCompanyName);
        if (Objects.nonNull(companyConfig)) {
            chainRate = companyConfig.getChainRate();
            chainDays = BigDecimal.valueOf(Objects.isNull(companyConfig.getChainDays()) ? 1L : companyConfig.getChainDays());
            calculateType = companyConfig.getCalculateType();
            annualizedRevenue = companyConfig.getAnnualizedRevenue();
            premiumAmount = companyConfig.getPremiumAmount();
        }

        BigDecimal chainPrice;
        // 采购价
        BigDecimal buyDealPrice = applyMatch.getBuyAmount().divide(applyMatch.getDealNumber(), 2, RoundingMode.HALF_UP);
        BigDecimal sellDealPrice = applyMatch.getSellAmount().divide(applyMatch.getDealNumber(), 2, RoundingMode.HALF_UP);
        if (StringUtils.equalsIgnoreCase("苏州高新供应链管理有限公司", applyMatch.getOurCompanyName())){
            // 下游销售单价 - (上游采购单价 * 0.9 *  0.00019 * 中游合同账期)
            chainPrice = sellDealPrice.subtract(buyDealPrice.multiply(new BigDecimal("0.9")).multiply(new BigDecimal("0.00019")).multiply(credit_days));
        }else if (StringUtils.equals(BasConstants.CALCULATE_TYPE_1, calculateType)) {
            // 固定收益 * 周期 【采购单价 * (1 + 0.0005 * 5天)】
            chainPrice = buyDealPrice.multiply(BigDecimal.ONE.add(chainRate.multiply(chainDays)));
        } else if (StringUtils.equals(BasConstants.CALCULATE_TYPE_3, calculateType)) {
            // 固定年化 * 账期 【采购单价 * (1 + 0.1/365 * 账期)】
            chainPrice = buyDealPrice.multiply(BigDecimal.ONE.add(annualizedRevenue.divide(YEAR_DAYS, 6, RoundingMode.HALF_UP).multiply(credit_days)));
        } else if (StringUtils.equals(BasConstants.CALCULATE_TYPE_4, calculateType)) {
            // 固定加单价 【采购单价 + 中间链加价金额(premiumAmount)】
            chainPrice = buyDealPrice.add(premiumAmount);
        } else if (StringUtils.equals(BasConstants.CALCULATE_TYPE_5, calculateType)){
            // 销售单价- 中游企业单价收益
            chainPrice = sellDealPrice.subtract(buyDealPrice.multiply(annualizedRevenue.divide(YEAR_DAYS, 6, RoundingMode.HALF_UP).multiply(credit_days)));
        } else {
            // 固定收益 * 周期（税后）【采购单价 * (1 + 0.0004 * 5天) * 1.13】
            // chainPrice = buyDealPrice.multiply(BigDecimal.ONE.add(chainRate.multiply(chainDays))).multiply(PARAM_1_13);

            // 固定收益 * 周期（税后）上游采购单价 * [(0.0004 * 5天 * 1.13 ) + 1]
            chainPrice = buyDealPrice.multiply((chainRate.multiply(chainDays).multiply(PARAM_1_13)).add(BigDecimal.ONE));
        }
        log.info("calculatePrice 计算类型:{}，计算单价:{}", calculateType, chainPrice);
        return chainPrice.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 中间链中游合同参数赋值
     * @param applyCtrDCSX
     * @param companyConfigMap
     * @param buyCompanyName
     * @param sellCompanyName
     * @param dealPrice
     * @param sortNumber
     * @return
     */
    public ApplyCtrDCSX parseChainContract(ApplyCtrDCSX applyCtrDCSX, Map<String, BsCompanyDcsx> companyConfigMap, String buyCompanyName, String sellCompanyName, BigDecimal dealPrice, Integer sortNumber) {
        ApplyCtrDCSX newChainContract = new ApplyCtrDCSX();
        BeanUtils.copyProperties(applyCtrDCSX, newChainContract);
        newChainContract.setId(0L);
        newChainContract.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB_C);
        newChainContract.setDealPrice(dealPrice);
        newChainContract.setTotalAmount(newChainContract.getTotalNumber().multiply(dealPrice).setScale(2, RoundingMode.HALF_UP));
        newChainContract.setContractNo(applyCtrDCSX.getContractNo().replace("X", "X" + sortNumber));
        newChainContract.setCompanyName(buyCompanyName);
        newChainContract.setOurCompanyName(sellCompanyName);
        Long chainDays = Objects.nonNull(companyConfigMap.get(buyCompanyName)) ? companyConfigMap.get(buyCompanyName).getChainDays() : DEFAULT_CHAIN_DAYS;
        if (chainDays > 0){
            newChainContract.setPayFullTime(DateOperator.addDays(applyCtrDCSX.getDeliveryDateTo(), chainDays.intValue() - 1));
            newChainContract.setCreditDays(chainDays);
            newChainContract.setCreditCycle(chainDays);
        }
        return newChainContract;
    }

    /**
     * 计算中游合同单价、合同总价
     * <p>
     * 中游合同总额=采购合同总额+服务费总额+利润10%
     * 利润=销售总价-采购总价-货物的仓储费-货物的运输费
     * version2.0 服务费=(采购总价-上游合同定金) * 1-履约保证金比例）* 服务费费率
     * version1.0 服务费=采购总价*（1-履约保证金比例）* 服务费费率
     * 履约保证金比例固定 20%
     *
     * @param entity
     * @return
     */
    private ApplyCtrDCSX parseDcsxEntity(ApplyCtrDCSX entity, List<ApplyMatchDetail> matchList) {
        BsCompanyDcsx companyConfig = bsCompanyDcsxService.findByCompanyName(entity.getCompanyName());
        if (Objects.nonNull(companyConfig) && companyConfig.getChainDays() > 0){
            Long chainDays = companyConfig.getChainDays();
            entity.setCreditCycle(chainDays);
            entity.setCreditDays(chainDays);
            entity.setPayFullTime(DateOperator.addDays(entity.getDeliveryDateTo(), chainDays.intValue()));
        }

        log.info("parseDcsxEntity contractNo:{}", entity.getContractNo());
        BigDecimal buyPrice = BigDecimal.ZERO;
        BigDecimal sellPrice = BigDecimal.ZERO;
        BigDecimal transportAmount = BigDecimal.ZERO;
        BigDecimal warehouseAmount = BigDecimal.ZERO;
        BigDecimal number = entity.getTotalNumber();
        BigDecimal bondAmount = BigDecimal.ZERO;
        for (ApplyMatchDetail matchDetail : matchList) {
            if (BasConstants.CONTRACT_STATUS_B.equals(matchDetail.getContractType())) {
                buyPrice = matchDetail.getDealPrice();
                bondAmount = Objects.nonNull(matchDetail.getPayBondAmount()) ? matchDetail.getPayBondAmount() : BigDecimal.ZERO;
            } else {
                sellPrice = matchDetail.getDealPrice();
            }
            transportAmount = transportAmount.add(matchDetail.getTransportCost());
            warehouseAmount = warehouseAmount.add(matchDetail.getWarehouseCost());
        }
        BigDecimal buyTotalAmount = buyPrice.multiply(number);
        BigDecimal sellTotalAmount = sellPrice.multiply(number);
        log.info("buyTotalAmount:{},sellTotalAmount:{}", buyTotalAmount, sellTotalAmount);
        // 利润
        BigDecimal profit = sellTotalAmount.subtract(buyTotalAmount).subtract(transportAmount).subtract(warehouseAmount).setScale(2, RoundingMode.HALF_UP);

        // 服务费费率
        BigDecimal insurance = getInsurance(entity);

        // 服务费
        BigDecimal serviceAmount = (buyTotalAmount.subtract(bondAmount)).multiply(BigDecimal.valueOf(0.8)).multiply(insurance).setScale(2, RoundingMode.HALF_UP);

        // 中游合同总价
        BigDecimal totalAmount = buyTotalAmount.add(serviceAmount).add(profit.multiply(BigDecimal.valueOf(0.1))).setScale(2, RoundingMode.HALF_UP);
        BigDecimal dealPrice = totalAmount.divide(number, 2, RoundingMode.HALF_UP);
        BigDecimal realTotalAmount = dealPrice.multiply(number).setScale(2, RoundingMode.HALF_UP);
        log.info("利润:{},账期:{},服务费费率:{},服务费:{},计算合同总价:{}", profit, entity.getCreditCycle(), insurance, serviceAmount, totalAmount);

        entity.setTotalAmount(realTotalAmount);
        entity.setDealPrice(dealPrice);
        log.info("contractNo:{},中游合同总价:{},中游合同单价:{}", entity.getContractNo(), realTotalAmount, dealPrice);
        return entity;
    }

    /**
     * 根据配置查询中游合同保费费率
     *
     * @param entity
     * @return
     */
    private BigDecimal getInsurance(ApplyCtrDCSX entity) {
        List<CalculateInsuranceRates> insuranceRatesList = bsProductConfigService.getDcsxInsurance(entity.getEnterpriseId());
        if (CollectionUtils.isNotEmpty(insuranceRatesList)) {
            for (CalculateInsuranceRates insuranceRate : insuranceRatesList) {
                String conditionValue = insuranceRate.getCondition();
                List<ExpressionToken> expressionTokenList = ResConditionParser.getVars(conditionValue);
                Map<String, Object> param = new HashMap<>();
                expressionTokenList.forEach(t -> {
                    Variable var = t.getVariable();
                    Object varVal = ResConditionParser.getVarValue(var.getVariableName(), entity, null);
                    param.put(var.getVariableName(), varVal);
                });
                if (ResConditionParser.validCondition(conditionValue, param)) {
                    logger.info("creditCycle:{},insuranceRate:{}", entity.getCreditCycle(), insuranceRate.getInsuranceRate());
                    return insuranceRate.getInsuranceRate();
                }
            }
        }
        return BigDecimal.ZERO;
    }


    /**
     * 自动发起收货款审批
     * @param applyMatch
     * @param approve
     */
    @ServerTransactional
    public void autoReceive(ApplyMatch applyMatch, List<ApplyMatchDetail> matchDetailList, PmApprove approve, List<CtrContract> ctrContracts) {
        SCHEDULED_POOL.schedule(() -> {
        CtrContract ctrContract = ctrContracts.get(matchDetailList.size() - 1);
        CtrContract ctr = ctrContractService.findByContractNo(ctrContract.getContractNo());
        ApplyMatchDetail applyMatchDetail = matchDetailList.stream().filter(s -> StringUtils.equals(s.getContractNo(), ctr.getContractNo())).collect(Collectors.toList()).get(0);
        try {
            ApplyReceive applyReceive = new ApplyReceive();
            applyReceive.setId(0L);
            applyReceive.setContractId(ctr.getId());
            applyReceive.setContractNo(ctr.getContractNo());
            applyReceive.setBusinessNo(ctr.getContractNo());
            applyReceive.setCompanyId(ctr.getCompanyId());
            applyReceive.setCompanyName(ctr.getCompanyName());
            applyReceive.setOurCompanyName(ctr.getOurCompanyName());
            applyReceive.setTotalAmount(ctr.getTotalAmount());
            applyReceive.setDeptId(ctr.getDeptId());
            applyReceive.setPayedAmount(ctr.getDealedAmount() == null ? BigDecimal.ZERO : ctr.getDealedAmount());
            // 未付
            applyReceive.setUnpayedAmount(applyReceive.getTotalAmount().subtract(applyReceive.getPayedAmount()));
            applyReceive.setReceiveDate(new Date());
            // 默认全款
            applyReceive.setReceiveType("B");
            // 默认 电汇
            applyReceive.setReceiveMode("T");
            if (ctr.getDealedAmount() != null) {
                applyReceive.setReceiveAmount(applyMatchDetail.getPayBondAmount());
                applyReceive.setBuyCompanyId(getBuyCompanyIdBySellContractId(ctr.getId()));
                applyReceive.setApproveId(0L);
                applyReceive.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                applyReceive.setApplySource(ApplySource.WEBSITE.getCode());
                String bizEntityJson = JsonUtil.obj2Json(applyReceive);
                String processCode = BasConstants.PROCESS_APPLY_RECEIVE;
                PmApproveSaveVo startVo = new PmApproveSaveVo();
                startVo.setMode(BasConstants.APPROVE_STATUS_A);
                startVo.setStatus(BasConstants.APPROVE_STATUS_A);
                startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);

                PmProcessSearchVo searchVo = new PmProcessSearchVo();
                searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                searchVo.setProcessCode(processCode);
                PmProcess process = pmProcessService.findByProcessCode(searchVo);
                if (process == null) {
                    throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
                }
                startVo.setUserId(ctr.getMatchUserId());
                startVo.setUserName(ctr.getMatchUserName());
                startVo.setProcessId(process.getId());
                startVo.setApproveId(0L);
                startVo.setBizEntityJson(bizEntityJson);
                startVo.setAutoStartMessage("货到付款审批完成，自动发起收定金");
                startVo.setAutoStartFlgReal(true);
                pmApproveService.startFlow(startVo);
                logger.info("autoApplyReceive.完成contract:{}", JsonUtil.obj2Json(ctr));
            }
        } catch(ApplicationException e){
            logger.error("autoApplyReceive error,contract:{}", JsonUtil.obj2Json(ctr), e);
        }

        }, 4, TimeUnit.SECONDS);
    }
    private Long getBuyCompanyIdBySellContractId(Long contractId) {
        Long buyCompanyId = null;
        CtrContractRela rela = ctrContractRelaService.getRelaBySellContractId(contractId);
        if (rela != null) {
            buyCompanyId = rela.getBuyCompanyId();
        }
        return buyCompanyId;
    }

    private void saveSpecialContract(ApplyMatch match, CtrContract contract, List<ApplyProductDetail> productList,
                                PmApprove approve, ApplyMatchDetail matchDetail) throws ApplicationException {
        boolean flkSpecialFlag = BasBusinessUtil.verifySpecialChainFLK(match);
        boolean krSpecialFlag = BasBusinessUtil.verifySpecialChainZJKR(match);
        boolean qgsgxSpecialFlag = BasBusinessUtil.verifySpecialChainQGSGX(match);
        boolean shzgSpecialFlag = BasBusinessUtil.verifySpecialChainSHZG(match);
        boolean zsnbSpecialFlag = BasBusinessUtil.verifySpecialChainZSNB(match);
        if (Boolean.TRUE.equals(flkSpecialFlag) && StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())){
            // 供应商 -> 范伦克 -> 网塑宁波 -> 苏高新 链条
            CtrContract targetContract = new CtrContract();
            BeanUtils.copyProperties(contract, targetContract);
            targetContract.setId(0L);
            targetContract.setVirtualContractId(null);
            targetContract.setVirtualType(null);
            targetContract.setVirtualContractNo(null);
            targetContract.setVirtualId(null);
            targetContract.setSpecialChainFlag(true);
            targetContract.setFundViewFlag(true);
            targetContract.setBuyContentFileId(null);
            targetContract.setSellContentFileId(null);
            BsCompany bsCompany = bsCompanyDao.findByCompanyName(match.getBuyOurCompanyName()).stream().findFirst().orElse(null);
            targetContract.setCompanyId(Objects.nonNull(bsCompany) ? bsCompany.getId() : null);
            targetContract.setCompanyName(match.getBuyOurCompanyName());
            targetContract.setOurCompanyName(match.getSellOurCompanyName());
            targetContract.setContractNo(BasBusinessUtil.buildSpecialFLKBuyContractNo(contract.getContractNo()));
            BsContractTemplate template = contractTemplateDao.findByTemplateTagAndEnterpriseId(BasConstants.TEMPLATETAG_BUY_FLK_DC_CONTRACT, match.getEnterpriseId());
            targetContract.setBsTemplateContractId(Objects.nonNull(template) ? template.getId() : null);

            ApplyProductDetail productDetail = new ApplyProductDetail();
            BeanUtils.copyProperties(productList.get(0), productDetail);

            productDetail.setId(null);
            productDetail.setApplyType("M1");
            productDetail.setDealPrice(productDetail.getDealPrice().multiply(new BigDecimal("1.003")).setScale(2, RoundingMode.HALF_UP));
            productDetail.setTotalPrice(productDetail.getDealPrice().multiply(productDetail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
            targetContract.setDealPrice(productDetail.getDealPrice());
            targetContract.setTotalAmount(productDetail.getTotalPrice());
            productDetail = applyProductDetailDao.save(productDetail);
            List<ApplyProductDetail> detailList = new ArrayList<>();
            detailList.add(productDetail);
            contractSaveService.saveContract(targetContract, detailList, approve, null, matchDetail);
        }else if (Boolean.TRUE.equals(krSpecialFlag) && StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())){
            // 供应商 -> 浙江康瑞 -> 安徽致远 -> 浙江网塑
            CtrContract targetContract = new CtrContract();
            BeanUtils.copyProperties(contract, targetContract);
            targetContract.setId(0L);
            targetContract.setVirtualContractId(null);
            targetContract.setVirtualType(null);
            targetContract.setVirtualContractNo(null);
            targetContract.setVirtualId(null);
            targetContract.setSpecialChainFlag(true);
            targetContract.setFundViewFlag(true);
            targetContract.setBuyContentFileId(null);
            targetContract.setSellContentFileId(null);
            BsCompany bsCompany = bsCompanyDao.findByCompanyName(match.getBuyOurCompanyName()).stream().findFirst().orElse(null);
            targetContract.setCompanyId(Objects.nonNull(bsCompany) ? bsCompany.getId() : null);
            targetContract.setCompanyName(match.getBuyOurCompanyName());
            targetContract.setOurCompanyName(match.getSellOurCompanyName());
            targetContract.setContractNo(BasBusinessUtil.buildSpecialFLKBuyContractNo(contract.getContractNo()));
            targetContract.setHideOut("1");
            BsContractTemplate template = contractTemplateDao.findByTemplateTagAndEnterpriseId(BasConstants.TEMPLATETAG_BUY_ZJKR_AHZY_CONTRACT, match.getEnterpriseId());
            targetContract.setBsTemplateContractId(Objects.nonNull(template) ? template.getId() : null);

            ApplyProductDetail productDetail = new ApplyProductDetail();
            BeanUtils.copyProperties(productList.get(0), productDetail);

            productDetail.setId(null);
            productDetail.setApplyType("M1");
            productDetail.setDealPrice(productDetail.getDealPrice().add(new BigDecimal("5")));
            productDetail.setTotalPrice(productDetail.getDealPrice().multiply(productDetail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
            targetContract.setDealPrice(productDetail.getDealPrice());
            targetContract.setTotalAmount(productDetail.getTotalPrice());
            productDetail = applyProductDetailDao.save(productDetail);
            List<ApplyProductDetail> detailList = new ArrayList<>();
            detailList.add(productDetail);
            contractSaveService.saveContract(targetContract, detailList, approve, null, matchDetail);
        } else if (Boolean.TRUE.equals(qgsgxSpecialFlag) && StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())){
            // 供应商-青岛中光-苏高新-上海中光
            CtrContract targetContract = new CtrContract();
            BeanUtils.copyProperties(contract, targetContract);
            targetContract.setId(0L);
            targetContract.setVirtualContractId(null);
            targetContract.setVirtualType(null);
            targetContract.setVirtualContractNo(null);
            targetContract.setVirtualId(null);
            targetContract.setSpecialChainFlag(true);
            targetContract.setFundViewFlag(true);
            targetContract.setBuyContentFileId(null);
            targetContract.setSellContentFileId(null);
            BsCompany bsCompany = bsCompanyDao.findByCompanyName(match.getBuyOurCompanyName()).stream().findFirst().orElse(null);
            targetContract.setCompanyId(Objects.nonNull(bsCompany) ? bsCompany.getId() : null);
            targetContract.setCompanyName(match.getBuyOurCompanyName());
            targetContract.setOurCompanyName(match.getSellOurCompanyName());
            targetContract.setContractNo(BasBusinessUtil.buildSpecialFLKBuyContractNo(contract.getContractNo()));
            BsContractTemplate template = contractTemplateDao.findByTemplateTagAndEnterpriseId("sgx_buy_contract_template", match.getEnterpriseId());
            targetContract.setBsTemplateContractId(Objects.nonNull(template) ? template.getId() : null);

            ApplyProductDetail productDetail = new ApplyProductDetail();
            BeanUtils.copyProperties(productList.get(0), productDetail);

            productDetail.setId(null);
            productDetail.setApplyType("M1");
            ApplyMatchDetail sellMatchDetail = applyMatchDetailDao.findOtherMatchDetail(matchDetail.getApplyMatchId(), matchDetail.getId());
            Long creditDay = DateOperator.compareDays(matchDetail.getPayFullTime(), sellMatchDetail.getReceiveFullTime()) + 1L;
            BigDecimal sellPrice = sellMatchDetail.getDealPrice();
            BigDecimal realDealPrice = (sellPrice.subtract(new BigDecimal("10"))).divide((BigDecimal.ONE.add((new BigDecimal("0.00019").multiply(new BigDecimal(creditDay))))), 2, RoundingMode.HALF_UP);
            productDetail.setDealPrice(realDealPrice);
            productDetail.setTotalPrice(productDetail.getDealPrice().multiply(productDetail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
            targetContract.setDealPrice(productDetail.getDealPrice());
            targetContract.setTotalAmount(productDetail.getTotalPrice());
            productDetail = applyProductDetailDao.save(productDetail);
            List<ApplyProductDetail> detailList = new ArrayList<>();
            detailList.add(productDetail);
            contractSaveService.saveContract(targetContract, detailList, approve, null, matchDetail);
        } else if (Boolean.TRUE.equals(shzgSpecialFlag) && StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())){
            // 供应商 -> 上海中光 -> 鸿博 -> 浙江网塑
            CtrContract targetContract = new CtrContract();
            BeanUtils.copyProperties(contract, targetContract);
            targetContract.setId(0L);
            targetContract.setVirtualContractId(null);
            targetContract.setVirtualType(null);
            targetContract.setVirtualContractNo(null);
            targetContract.setVirtualId(null);
            targetContract.setSpecialChainFlag(true);
            targetContract.setFundViewFlag(true);
            targetContract.setBuyContentFileId(null);
            targetContract.setSellContentFileId(null);
            BsCompany bsCompany = bsCompanyDao.findByCompanyName(match.getBuyOurCompanyName()).stream().findFirst().orElse(null);
            targetContract.setCompanyId(Objects.nonNull(bsCompany) ? bsCompany.getId() : null);
            targetContract.setCompanyName(match.getBuyOurCompanyName());
            targetContract.setOurCompanyName(match.getSellOurCompanyName());
            targetContract.setContractNo(BasBusinessUtil.buildSpecialFLKBuyContractNo(contract.getContractNo()));
            BsContractTemplate template = contractTemplateDao.findByTemplateTagAndEnterpriseId("sgx_buy_contract_template", match.getEnterpriseId());
            targetContract.setBsTemplateContractId(Objects.nonNull(template) ? template.getId() : null);

            ApplyProductDetail productDetail = new ApplyProductDetail();
            BeanUtils.copyProperties(productList.get(0), productDetail);

            productDetail.setId(null);
            productDetail.setApplyType("M1");
//            ApplyMatchDetail sellMatchDetail = applyMatchDetailDao.findOtherMatchDetail(matchDetail.getApplyMatchId(), matchDetail.getId());
            // (下游单价-10) / (1 + (0.065 / 365 * (下游付全款日期 - 下游交货日期))))
            // 采购单价+10
//            Long creditDay = DateOperator.compareDays(sellMatchDetail.getDeliveryDate(), sellMatchDetail.getReceiveFullTime()) + 1L;
//            BigDecimal sellPrice = sellMatchDetail.getDealPrice();
//            BigDecimal realDealPrice = (sellPrice.subtract(new BigDecimal("10"))).divide((BigDecimal.ONE.add((new BigDecimal("0.065").divide(new BigDecimal(365), 8, RoundingMode.HALF_UP).multiply(new BigDecimal(creditDay))))), 2, RoundingMode.HALF_UP);
            BigDecimal realDealPrice = matchDetail.getDealPrice().add(new BigDecimal("10"));
            productDetail.setDealPrice(realDealPrice);
            productDetail.setTotalPrice(productDetail.getDealPrice().multiply(productDetail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
            targetContract.setDealPrice(productDetail.getDealPrice());
            targetContract.setTotalAmount(productDetail.getTotalPrice());
            productDetail = applyProductDetailDao.save(productDetail);
            List<ApplyProductDetail> detailList = new ArrayList<>();
            detailList.add(productDetail);
            contractSaveService.saveContract(targetContract, detailList, approve, null, matchDetail);
        } else if (Boolean.TRUE.equals(zsnbSpecialFlag) && StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())){
            // 供应商 -> 浙江网塑 -> 网塑（宁波） -> 苏州高新
            CtrContract targetContract = new CtrContract();
            BeanUtils.copyProperties(contract, targetContract);
            targetContract.setId(0L);
            targetContract.setVirtualContractId(null);
            targetContract.setVirtualType(null);
            targetContract.setVirtualContractNo(null);
            targetContract.setVirtualId(null);
            targetContract.setSpecialChainFlag(true);
            targetContract.setFundViewFlag(true);
            targetContract.setBuyContentFileId(null);
            targetContract.setSellContentFileId(null);
            BsCompany bsCompany = bsCompanyDao.findByCompanyName(match.getBuyOurCompanyName()).stream().findFirst().orElse(null);
            targetContract.setCompanyId(Objects.nonNull(bsCompany) ? bsCompany.getId() : null);
            targetContract.setCompanyName(match.getBuyOurCompanyName());
            targetContract.setOurCompanyName(match.getSellOurCompanyName());
            targetContract.setContractNo(BasBusinessUtil.buildSpecialFLKBuyContractNo(contract.getContractNo()));
            BsContractTemplate template = contractTemplateDao.findByTemplateTagAndEnterpriseId("sgx_buy_contract_template", match.getEnterpriseId());
            targetContract.setBsTemplateContractId(Objects.nonNull(template) ? template.getId() : null);

            ApplyProductDetail productDetail = new ApplyProductDetail();
            BeanUtils.copyProperties(productList.get(0), productDetail);

            productDetail.setId(null);
            productDetail.setApplyType("M1");
            ApplyMatchDetail sellMatchDetail = applyMatchDetailDao.findOtherMatchDetail(matchDetail.getApplyMatchId(), matchDetail.getId());
            // 销售单价 - （采购单价 * 0.1 / 365 * 中游账期）
            Long creditDay = DateOperator.compareDays(sellMatchDetail.getDeliveryDate(), sellMatchDetail.getReceiveFullTime()) + 1L;
            BigDecimal sellPrice = sellMatchDetail.getDealPrice();
            BigDecimal buyPrice = matchDetail.getDealPrice();
            BigDecimal realDealPrice = buyPrice.multiply(new BigDecimal("0.1").divide(new BigDecimal(365), 8, RoundingMode.HALF_UP).multiply(new BigDecimal(creditDay))).setScale(2, RoundingMode.HALF_UP);
            productDetail.setDealPrice(sellPrice.subtract(realDealPrice));
            productDetail.setTotalPrice(productDetail.getDealPrice().multiply(productDetail.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
            targetContract.setDealPrice(productDetail.getDealPrice());
            targetContract.setTotalAmount(productDetail.getTotalPrice());
            productDetail = applyProductDetailDao.save(productDetail);
            List<ApplyProductDetail> detailList = new ArrayList<>();
            detailList.add(productDetail);
            contractSaveService.saveContract(targetContract, detailList, approve, null, matchDetail);
        }
    }
}


