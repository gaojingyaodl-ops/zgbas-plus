package com.spt.bas.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
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
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 背靠背业务
 */
@Component("applyMatchService")
@Transactional(readOnly = true)
@Slf4j
public class ApplyMatchServiceImpl extends BaseService<ApplyMatch> implements IApplyMatchService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService MATCH_SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    @Autowired
    private IStockVirtualService stockVirtualService;
    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Autowired
    private ApplyProductDetailDao applyProductDetailDao;
    @Autowired
    private ApplyMatchDetailDao applyMatchDetailDao;
    @Autowired
    private ICtrContractSaveService contractSaveService;
    @Autowired
    private ICtrContractService contractService;
    @Autowired
    private IBsKeySequenceService bsKeySequenceService;
    @Autowired
    private IStockContractService stockContractService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IBsConfigService bsConfigService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private ICtrProductService ctrProductService;
    @Autowired
    private ApplyMatchChainDao applyMatchChainDao;
    @Autowired
    private CtrContractChainDao ctrContractChainDao;
    @Autowired
    private ICtrContractChainTextService ctrContractChainTextService;
    @Autowired
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Autowired
    private IBsCompanyOurService bsCompanyOurService;
    @Autowired
    private ICtrContractRelaService ctrContractRelaService;
    @Autowired
    private IBsCompanyCreditFlowService companyCreditFlowService;
    @Autowired
    private ICtrContractProfitService ctrContractProfitService;
    @Autowired
    private ICtrLogisticsService ctrLogisticsService;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private IBusinessRestrictRelieveService businessRestrictRelieveService;
    @Resource
    private IAutoSealPdfSignFilter autoSealPdfSignFilter;
    @Resource
    private IBudgetVerifyFilter budgetVerifyFilter;

    @Override
    public BaseDao<ApplyMatch> getBaseDao() {
        return applyMatchDao;
    }

    @Override
    public Class<ApplyMatch> getEntityClazz() {
        return ApplyMatch.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyMatchDao.updateFileId(id, fileId);
    }

    @Override
    @ServerTransactional
    public void updateLiabilityFileId(Long id, String fileId) {
        applyMatchDao.updateLiabilityFileId(id, fileId);
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        // 业务配置开关判断
        ApplyMatch applyMatch = applyMatchDao.findOne(approve.getBizId());
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(applyMatch.getId());

        // 赊销授信标识
        boolean creditFlg = verifyMatchCreditFlg(matchDetailList);

        // 验证预算单是否满足发起条件(链条代采方配置余额是否充足)
        BsConfigRespVo bsConfigRespVo = budgetVerifyFilter.judgmentMatchStart(applyMatch, matchDetailList, approve.getProcessId());

        // 代采毛利率不可低于规定否则不可发起
        budgetVerifyFilter.judgmentMatchProfit(applyMatch, matchDetailList, creditFlg);

        // 销售合同 使用授信
        if (Boolean.TRUE.equals(creditFlg)) {
            // 验证发货预警
            budgetVerifyFilter.deliveryWarning(applyMatch);

            // 验证剩余授信额度是否可用
            budgetVerifyFilter.verifyCreditAmount(applyMatch, bsConfigRespVo);
        }

        // 更新业务配置额度
        budgetVerifyFilter.refreshBalance(bsConfigRespVo, approve, applyMatch);

        // 绑定报价虚拟库存
        stockVirtualService.bindStockVirtual(approve, applyMatch, matchDetailList);

        applyMatchDao.save(applyMatch);
    }

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            String pairCode = bsKeySequenceService.getNextKey(BasConstants.KEY_PAIR_CODE, approve.getEnterpriseId());
            // 获得撮合信息
            ApplyMatch match = applyMatchDao.findOne(approve.getBizId());
            Long stockVirtualId = match.getStockVirtualId();
            String contractAttr = match.getContractAttr();
            String ourCompanyName = match.getOurCompanyName();
            // 获得撮合明细，先采购，后销售
            List<ApplyMatchDetail> matchList = applyMatchDetailDao.findByApplyMatchId(match.getId());
            // 采购合同Id获取，保存在销售合同里，销售合同可以找到对应的采购合同ID
            List<Long> lstBuyId = new ArrayList<>();
            List<Long> lstSellId = new ArrayList<>();
            int buyI = 0, sellI = 0;
            Boolean matchCreditFlg = verifyMatchCreditFlg(matchList);
            List<ApplyProductDetail> sellProductList = new ArrayList<>();
            CtrContract sellContract = new CtrContract();
            for (ApplyMatchDetail machDetail : matchList) {
                // 预付定金
                BigDecimal bondAmount;
                // 预付定金比例
                BigDecimal bondRate;
                CtrContract contract = new CtrContract();
                BeanUtils.copyProperties(machDetail, contract);
                contract.setId(null);
                if(StringUtils.equals("hq",match.getApplySource())){
                    contract.setApplySource(machDetail.getApplySource());
                }
                //保存预算运输费和预算仓储费后期不被覆盖
                contract.setApproveTransportAmount(machDetail.getTransportCost());
                contract.setApproveWarehouseAmount(machDetail.getWarehouseCost());
                contract.setApproveStevedorage(machDetail.getStevedorage());
                if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                    budgetVerifyFilter.maintainCreditType(contract, match);
                    contract.setCompanyCreditId(match.getCompanyCreditId());
                    contract.setCarrier(machDetail.getCarrier());
                    contract.setBsTemplateContractId(machDetail.getSellTemplateId());
                    contract.setSellContentFileId(machDetail.getSellContentTemplateId());
                    contract.setServiceContentFileId(machDetail.getServiceContentTemplateId());
                    contract.setDeliveryDateTo(machDetail.getDeliveryDate());
                    contract.setDeliveryDateFrom(machDetail.getDeliveryDate());
                    if(matchCreditFlg){
                        bondAmount = machDetail.getPayBondAmount();
                        bondRate = machDetail.getPayRate() == null ? BigDecimal.ZERO : machDetail.getPayRate();
                    } else {
                        bondAmount = machDetail.getReceiveBondAmount();
                        bondRate = machDetail.getReceiveRate() == null ? BigDecimal.ZERO : machDetail.getReceiveRate();
                    }
                    contract.setRemark(machDetail.getReceiveRemark());
                    contract.setPayBondTime(machDetail.getPayBondTime());
                    contract.setPayType(machDetail.getReceiveType());
                    contract.setCustomerOrderCode(match.getCustomerOrderCode());
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
                } else {
                    machDetail.setStockVirtualId(stockVirtualId);
                    contract.setCarrier(machDetail.getCarrier());
                    // 采购合同
                    StockVirtual stockVirtual = stockVirtualService.getEntity(stockVirtualId);
                    contract.setVirtualContractId(Objects.nonNull(stockVirtual) ? stockVirtual.getVirtualContractId() : null);
                    contract.setVirtualContractNo(Objects.nonNull(stockVirtual) ? stockVirtual.getVirtualContractNo() : null);
                    boolean creditFlgBuy = false;
                    if (StringUtils.equals(BasConstants.DELIVERY_MODE_SX,machDetail.getDeliveryMode())|| StringUtils.equals(BasConstants.DELIVERY_MODE_XHHK,machDetail.getDeliveryMode())){
                        creditFlgBuy = true;
                    }
                    contract.setDealAmountNoTax(machDetail.getDealAmountNotax());
                    contract.setCarrier(machDetail.getCarrier());
                    contract.setBsTemplateContractId(machDetail.getBuyTemplateId());
                    contract.setBuyContentFileId(machDetail.getBuyContentTemplateId());
                    contract.setDeliveryDateTo(machDetail.getDeliveryDate());
                    contract.setDeliveryDateFrom(machDetail.getDeliveryDate());
                    bondAmount = machDetail.getPayBondAmount();
                    bondRate = machDetail.getPayRate() == null ? BigDecimal.ZERO : machDetail.getPayRate();
                    contract.setRemark(machDetail.getPayRemark());
                    contract.setSource(BasConstants.APPLY_TYPE_MB); // 授信标志
                    contract.setCreditFlg(creditFlgBuy);
                    buyI++;
                }
                contract.setWarehouseAmount(machDetail.getWarehouseCost());
                contract.setTransportAmount(machDetail.getTransportCost());
                contract.setContractType(machDetail.getContractType());
                contract.setFileId(match.getFileId());
                // 我方企业抬头
                if(StringUtils.isBlank(contract.getOurCompanyName())){
                    contract.setOurCompanyName(ourCompanyName);
                }
                contract.setContractAttr(contractAttr);
                contract.setBusinessType(match.getBusinessType());
                contract.setAttachDeliveryTime(machDetail.getArrivalTimeExt());
                contract.setPayMode(machDetail.getPayKind());
                contract.setMatchUserId(machDetail.getMatchUserId());
                contract.setMatchUserName(machDetail.getMatchUserName());
                // 获得商品明细
                List<ApplyProductDetail> productList = productDetailService.findApplyDetail(machDetail.getId(), BasConstants.APPLY_TYPE_M);
                contract.setBondAmount(bondAmount);
                contract.setBondRate(bondRate);
                contract.setMatchCreditFlg(matchCreditFlg);
                // 保存撮合排序号
                contract.setPairCode(pairCode);
                contract.setBusinessTypeDcsx(match.getContractModel());
                contract.setContractModel(match.getContractModel());
                BsCompany company = bsCompanyDao.findOne(contract.getCompanyId());
                if(Objects.nonNull(company)) {
                    contract.setCompanyPiccFlg(Boolean.TRUE.equals(company.getPiccFlg()));
                }
                // 设置 赊销业务类型

                String businessKind = parseBusinessKind(matchCreditFlg, match.getContractModel());
                // 代采托盘判断
                if(StringUtils.equals(match.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_TP)){
                    businessKind = BasConstants.BusinessKind.DICT_DCTP;
                }
                contract.setBusinessKind(businessKind);
                contract = contractSaveService.saveContract(contract, productList, approve, lstBuyId, machDetail);

                // 记录合同id
                machDetail.setContractId(contract.getId());
                if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_B)) {
                    lstBuyId.add(contract.getId());
                } else {
                    lstSellId.add(contract.getId());
                }
                applyMatchDetailDao.save(machDetail);
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

            if(match.getContractModel()==null){
                match.setContractModel("false");
            }
//            if (StringUtils.equals(match.getContractModel(), BasConstants.BUSINESS_TYPE_HDFK)) {
//                //自动发起收货款
//                this.autoReceive(match, matchList, approve, byApproveId);
//            }
            // 审批完成自动生成盖章申请
            this.autoInitiatedSealUsage(match, matchList, approve);

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
            
        } else {
            ApplyMatch match = applyMatchDao.findOne(approve.getBizId());
            Boolean liabilityFlg = match.getLiabilityFlg();
            if (Boolean.TRUE.equals(liabilityFlg) && StringUtils.isBlank(match.getLiabilityFileId())) {
                throw new ApplicationException("请上传个人连带责任保证书(C类和D类客户，需与客户签订个人连带责任保证书!)");
            }
        }
    }

    private String parseBusinessKind(Boolean matchCreditFlg, String contractModel){
        String businessKind = "";
        if(matchCreditFlg) {
            if(StringUtils.equals(BasConstants.CONTRACT_MODEL_PT,contractModel)) {
                businessKind = BasConstants.BusinessKind.DICT_SX;
            } else if(StringUtils.equals(BasConstants.CONTRACT_MODEL_BL,contractModel)) {
                businessKind = BasConstants.BusinessKind.DICT_SXBL;
            } else if(StringUtils.equals(BasConstants.CONTRACT_MODEL_HDFK,contractModel)) {
                businessKind = BasConstants.BusinessKind.DICT_SXHDFK;
            } else {
                businessKind = BasConstants.BusinessKind.DICT_SX;
            }
        } else {
            businessKind = BasConstants.BusinessKind.DICT_DC;
        }
        return businessKind;
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        Boolean completeDismissalFlg = approve.getCompleteDismissalFlg();
        ApplyMatch applyMatch = applyMatchDao.findOne(approve.getBizId());
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(applyMatch.getId());
        boolean creditFlg = verifyMatchCreditFlg(matchDetailList);
        if (Boolean.TRUE.equals(completeDismissalFlg) && Boolean.TRUE.equals(creditFlg)) {
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
    }

    /**
     * 撮合业务，删除审批
     */
    @Override
    @ServerTransactional
    public void delete(Long id) {
        if (id != null && id > 0L) {
            applyMatchDetailDao.deleteByApplyMatchId(id);
            applyMatchDao.delete(id);
        }
    }

    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException{
        // 代采业务不会涉及到授信额度等信息
        // 处理判断是否所有白条业务,并返回相关参数
        PmApprove approve = pmApproveService.getEntity(vo.getApproveId());
        ApplyMatch match = applyMatchDao.findOne(approve.getBizId());
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(match.getId());
        boolean creditFlg = verifyMatchCreditFlg(matchDetailList);
        // 使用授信
        if (Boolean.TRUE.equals(creditFlg) && StringUtils.equals(BasConstants.APPROVE_STATUS_D, match.getStatus())) {
            companyCreditFlowService.updateUsedCreditAmount(approve, match.getCompanyCreditId(), match.getSellAmount().negate(), CreditFlowEnum.CC);
        }
        if (Objects.nonNull(match) && match.getBusinessRestrictRelieveFlg() != null && match.getBusinessRestrictRelieveFlg() && match.getId() != null) {
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
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyMatch entity = new ApplyMatch();
        List<ApplyProductDetail> sellProductList = new ArrayList<>();
        if (pmEntity instanceof ApplyMatchVo) {
            ApplyMatchVo vo = (ApplyMatchVo) pmEntity;
            Long stockVirtualId = vo.getStockVirtualId();
            //如果是货到付款模式则进入销售合同定金校验
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_HDFK, vo.getContractModel())) {
                ApplyMatchDetailVo detailVo = vo.getLstInsert().get(vo.getLstInsert().size() - 1);
                List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.HDFK_PAY_RATE);
                BigDecimal bigDecimal = NumberUtils.createBigDecimal(listByCategory.get(0).getDictCd());
                //比例定金
                BigDecimal multiply = detailVo.getTotalAmount().multiply(bigDecimal);
                //页面实际定金
                BigDecimal payBondAmount = vo.getLstInsert().get(vo.getLstInsert().size() - 1).getPayRateAmount();
                if (multiply.compareTo(payBondAmount) > 0) {
                    throw new ApplicationException("货到付款模式下定金必须大于等于销售价的" + listByCategory.get(0).getDictName());
                }
            }
            ApplyProductDetailSaveVo pvo = new ApplyProductDetailSaveVo();
            BeanUtils.copyProperties(vo, entity);
            // 1.保存撮合主表信息
            entity = applyMatchDao.save(entity);
            applyMatchDetailDao.deleteByApplyMatchId(entity.getId());
            Long applyMatchId = entity.getId();
            Long enterpriseId = entity.getEnterpriseId();

            BigDecimal buyAmount = BigDecimal.ZERO;
            BigDecimal sellAmount = BigDecimal.ZERO;
            String ourCompanyName = vo.getOurCompanyName();
            String buyContractNo = null;
            // 获得撮合明细表 及商品明细
            for (ApplyMatchDetailVo list : vo.getLstInsert()) {
                // 撮合明细
                ApplyMatchDetail matchDetail = new ApplyMatchDetail();
                BeanUtils.copyProperties(list, matchDetail);
                matchDetail.setMatchUserId(list.getMatchUserId());
                matchDetail.setMatchUserName(list.getMatchUserName());
                if (matchDetail.getId() == null) {
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
                    } else if (BasConstants.CONTRACT_STATUS_S.equals(matchDetail.getContractType())) {
                        logger.info("buyContractNo:{}", buyContractNo);
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
                if(StringUtils.isNotBlank(list.getOurCompanyName())){
                    matchDetail.setCompanyName(list.getCompanyName());
                }else{
                    matchDetail.setCompanyName(company.getCompanyName());
                }
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
                List<ApplyProductDetail> productArr = productDetailService.findApplyDetail(matchDetail.getId(),BasConstants.APPLY_TYPE_M);
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
                        receiveRate = BigDecimal.ZERO;
                    }
                    BigDecimal receiveBondAmount = totalPrice.multiply(receiveRate);
                    matchDetail.setReceiveRate(receiveRate);
                    matchDetail.setReceiveBondAmount(receiveBondAmount);
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
                        matchDetail.setPayRate(payRate);
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
                matchDetail.setReceiveBondAmount(list.getReceiveRateAmount());
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
            BsDictData data = BsCompanyOurUtil.getCompanyOurToBsDictData(entity.getEnterpriseId(), ourCompanyName);
            if (data != null) {
                entity.setOurCompanyName(data.getDictName());
            }
            if (!sellProductList.isEmpty()) {
                String internalContractNo = BasBusinessUtil.composeContractNo(vo.getEnterpriseId(), vo.getDeptAbbr(), BasConstants.APPLY_TYPE_S);
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
        }
        return entity;
    }

    /**
     * 个人连带责任书逻辑判断
     * 1.赊销合同；
     * 2.客户等级C类、D类；
     * 3.销售-交货日期不等于销售-收全款日期；
     * @param applyMatch
     * @param applyMatchDetail
     */
    private void dealWithLiability(ApplyMatch applyMatch, ApplyMatchDetail applyMatchDetail){
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

    /***
     * 标题显示规则
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        //代采：合同尾号，品名/牌号/数量，上游-我方-下游，采购总价
        ApplyMatch vo = (ApplyMatch) pmEntity;
        Long matchId = vo.getId();
        List<ApplyMatchDetail> list = applyMatchDetailDao.findByApplyMatchId(matchId);
        StringBuilder strBuf = new StringBuilder();
        BigDecimal totalPrice = new BigDecimal(0);
        String contractNo = "";
        ApplyMatchDetail applyMatchDetail = list.get(0);
        ApplyMatchDetail applyMatchDetail1 = list.get(1);
        contractNo = applyMatchDetail.getContractNo().replaceAll("\\D", "");
            Long applyId = applyMatchDetail.getId();
            List<ApplyProductDetail> productlist = productDetailService.findApplyDetail(applyId, BasConstants.APPLY_TYPE_M);
            for (ApplyProductDetail product : productlist) {
                String contractyType = applyMatchDetail.getContractType();
                totalPrice=StringUtils.equals(BasConstants.APPLY_TYPE_B,contractyType) ? product.getTotalPrice():totalPrice;
                String productName = product.getProductName();
                String productCd = product.getProductCd();
                String realOutNumber = NumberUtil.formatNumber(product.getDealNumber(), "#.###");
                String brandNumber = product.getBrandNumber();
                        if (productCd.indexOf("SL") > 0) {
                            strBuf.append(productName).append("/").append(brandNumber).append("/").append(realOutNumber+ RuleUtil.weightUnit);
                        } else {
                            strBuf.append(productName).append("/").append(realOutNumber+ RuleUtil.weightUnit);
                        }
            }
        String companyName1 = RuleUtil.companyNameSubString(list.get(0).getCompanyName());
        String companyName2 = RuleUtil.companyNameSubString(vo.getOurCompanyName());
        String companyName3 = RuleUtil.companyNameSubString(list.get(1).getCompanyName());
        String contractModel="";
        if(StringUtils.isNotBlank(vo.getContractModel())){
               switch (vo.getContractModel()){
                   case "BL":
                       contractModel="保理模式";
                       break;
                   case "PT":
                       contractModel="普通模式";
                       break;
                   case "DCSXBL":
                       contractModel="保理模式";
                       break;
                   case "DCSX":
                       contractModel="普通模式";
                       break;
                   case "DCSXHDFK":
                       contractModel="货到付款";
                       break;
                   case "DCSXCK":
                       contractModel="代采出口";
                       break;
                   case "HDFK":
                       contractModel="货到付款";
                       break;
               }
        }else{
            contractModel="普通模式";
        }
        String company="";
        if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)&&StringUtils.isNotBlank(companyName3)){
            company=companyName1+ "-" +companyName2+ "-" + companyName3;
        }
        String s = strBuf.toString();
        String subject ;
        String receiptArrived = applyMatchDetail1.getReceiptArrivedFlg() ? "[货到票到]" : "";
        if(applyMatchDetail1.getSettlementType()!=null){
            subject = SubjectUtil.formatSubject(receiptArrived,contractNo,s,contractModel,company,SubjectUtil.formatMoney(list.get(0).getTotalAmount() , RuleUtil.monetaryUnit),list.get(1).getCreditDays()+ RuleUtil.dateUnit);
        }else{
            subject = SubjectUtil.formatSubject(receiptArrived,contractNo, s, company, SubjectUtil.formatMoney(list.get(0).getTotalAmount() , RuleUtil.monetaryUnit));
        }
        return subject;
    }

    @Override
    public ApproveMatchFormPrintVo printApplyMatch(Long applyId) {
        ApplyMatch entity = getEntity(applyId);
        ApproveMatchFormPrintVo vo = new ApproveMatchFormPrintVo();
        List<ApplyMatchDetail> list = applyMatchDetailDao.findByApplyMatchId(entity.getId());
        List<ApplyProductDetailVo> sellList = new ArrayList<>();
        List<ApplyProductDetailVo> buyList = new ArrayList<>();
        // 销售合同总价
        BigDecimal selltotalAmount = BigDecimal.ZERO;
        // 采购合同总价
        BigDecimal buytotalAmount = BigDecimal.ZERO;
        BigDecimal buyPayBoundAmount = BigDecimal.ZERO;
        BigDecimal sellPayBoundAmount = BigDecimal.ZERO;
        String buyCompanyName = "";
        String sellCompanyName = "";
        for (ApplyMatchDetail match : list) {
            // 查询明细
            ApplyDeliveryApplyIdVo applyVo = new ApplyDeliveryApplyIdVo();
            applyVo.setApplyId(match.getId());
            applyVo.setApplyType(BasConstants.APPLY_TYPE_M);
            if (match.getReceiveBondAmount() != null && match.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                sellPayBoundAmount = sellPayBoundAmount.add(match.getReceiveBondAmount());
                sellCompanyName = match.getCompanyName();
            }
            if (match.getPayBondAmount() != null && match.getContractType().equals(BasConstants.APPLY_TYPE_B)) {
                buyPayBoundAmount = buyPayBoundAmount.add(match.getPayBondAmount());
                buyCompanyName = match.getCompanyName();
            }
            List<ApplyProductDetail> detailList = productDetailService.findApplyId(applyVo);
            List<ApplyProductDetailVo> detailVo = new ArrayList<>();
            for (ApplyProductDetail applyProductDetail : detailList) {
                ApplyProductDetailVo applyDetail = new ApplyProductDetailVo();
                BeanUtils.copyProperties(applyProductDetail, applyDetail);
                detailVo.add(applyDetail);
            }
            DecimalFormat df = new DecimalFormat();
            df.applyPattern("0.00");
            for (ApplyProductDetailVo detail : detailVo) {
                detail.setContractNo(match.getContractNo());
                detail.setCompanyName(match.getCompanyName());
                if (match.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                    detail.setCompanyName(sellCompanyName);
                    detail.setProductName(
                            detail.getProductName() + "/" + detail.getBrandNumber() + "/" + detail.getFactoryName());
                    sellList.add(detail);
                    selltotalAmount = selltotalAmount.add(detail.getTotalPrice());
                } else {
                    detail.setCompanyName(buyCompanyName);
                    buyList.add(detail);
                    buytotalAmount = buytotalAmount.add(detail.getTotalPrice());
                }
            }
            // 格式化时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            if (match.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                vo.setSellBondAmount(sellPayBoundAmount);
                vo.setSellList(sellList);
                vo.setSellTimeStr(sdf.format(match.getReceiveFullTime()));
            } else {
                vo.setBuyBondAmount(buyPayBoundAmount);
                vo.setBuyList(buyList);
                vo.setBuyTimeStr(sdf.format(match.getPayFullTime()));
            }
            // 期货显示到货时间
            if (match.getArrivalTime() != null
                    && StringUtils.equals(match.getContractAttr(), BasConstants.DICT_TYPE_CONTRACTATTR_F)) {
                vo.setArrivalTimeStr(sdf.format(match.getArrivalTime()));
            } else {
                vo.setArrivalTimeStr("");
            }
            if (match.getTransportCost() != null && match.getTransportCost().compareTo(BigDecimal.ZERO) > 0) {
                vo.setTransportAmount(match.getTransportCost());
            } else {
                vo.setTransportAmount(BigDecimal.ZERO);
            }
            vo.setDeliveryType(DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, match.getDeliveryType()));
            vo.setShippingAddr(entity.getShippingAddr() == null ? "" : entity.getShippingAddr());
            vo.setRemark(entity.getRemark() == null ? "" : entity.getRemark());
            vo.setContractAttr(DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTATTR, entity.getContractAttr()));
            // 获取审批单的创建人
            PmApprove approve = pmApproveService.getEntity(entity.getApproveId());
            vo.setMatchUserName(approve.getCreateUserName());
            // 仓库
            StringBuilder warehouseName = new StringBuilder();
            for (int i = 0; i < buyList.size(); i++) {
                if (i != buyList.size() - 1) {
                    warehouseName.append(buyList.get(i).getWarehouseName()).append(",");
                } else {
                    warehouseName.append(buyList.get(i).getWarehouseName());
                }

            }
            vo.setWarehouseName(warehouseName.toString());
        }

        BsTemplateConfig template = TemplateContentUtility.getTemplate("matchApplyPrint", "FMC_APPLY_MATCH", "CH",
                entity.getEnterpriseId());
        try {
            String content = contentMerge(template.getContent(), vo);
            vo.setContent(content);
        } catch (ApplicationException e) {
            logger.error("contentMerge", e);
        }
        return vo;
    }

    /**
     * 将审批内容填充至模板
     *
     * @param content
     * @param entity
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    private String contentMerge(String content, ApproveMatchFormPrintVo entity) throws ApplicationException {
        Configuration cfg = new Configuration();
        StringWriter sw = new StringWriter();
        try {
            Template t = new freemarker.template.Template("", new StringReader(content), cfg);
            t.process(entity, sw);
            content = sw.toString();
        } catch (Exception e) {
            throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
        }
        return content;
    }

    /***
     * @author shaoanwei
     * @date 2021-02-23 16:06
     * @description: 实现代采预算申请
     * @params [matchVo]
     * @return void
     */
    @Override
    @ServerTransactional
    public void applyMatch(ApplyMatchVo matchVo) throws ApplicationException {
        log.info("applyMatch:{}", JsonUtil.obj2Json(matchVo));
        try {
            if (StringUtils.isEmpty(matchVo.getProductName())) {
                throw new ApplicationException("品种不能为NULL!");
            }

            if (StringUtils.isEmpty(matchVo.getBrandNumber())) {
                throw new ApplicationException("牌号不能为NULL!");
            }

            if (matchVo.getDealNumber() == null) {
                throw new ApplicationException("数量不能为NULL!");
            }

            if (StringUtils.isEmpty(matchVo.getFactoryName())) {
                throw new ApplicationException("厂商不能为NULL!");
            }

            if (StringUtils.isEmpty(matchVo.getWrapSpecs())) {
                throw new ApplicationException("包装规格不能为NULL!");
            }
            if (StringUtils.isEmpty(matchVo.getQualityStandard())) {
                throw new ApplicationException("质量标准不能为NULL!");
            }

            if (StringUtils.isEmpty(matchVo.getBuyInfo().getCompanyName())) {
                throw new ApplicationException("供方不能为NULL!");
            }
            if (StringUtils.isEmpty(matchVo.getBuyInfo().getDeliveryAddr())) {
                throw new ApplicationException("交货地点不能为NULL!");
            }

            if (matchVo.getBuyInfo().getDealPrice() == null) {
                throw new ApplicationException("含税单价不能为NULL!");
            }
            matchVo.getBuyInfo().setDealAmountNotax(matchVo.getBuyInfo().getDealPriceNotax());
            if (matchVo.getBuyInfo().getDealAmountNotax() == null) {
                throw new ApplicationException("不含税单价不能为NULL!");
            }

            if (StringUtils.isEmpty(matchVo.getSellInfo().getCompanyName())) {
                throw new ApplicationException("需方不能为NULL!");
            }

            if (StringUtils.isEmpty(matchVo.getSellInfo().getDeliveryAddr())) {
                throw new ApplicationException("交货地点不能为null!");
            }

            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);//中光亿云企业id
            matchVo.setEnterpriseId(startVo.getEnterpriseId());//获取企业id
            //采购
            ApplyMatchDetailVo buyJSON = matchVo.getBuyInfo();
            buyJSON.setMatchUserName(UserCache.getUserName(buyJSON.getMatchUserId()));
            buyJSON.setDeliveryMode(buyJSON.getDeliveryMode());
            //销售
            ApplyMatchDetailVo sellJSON = matchVo.getSellInfo();

            List<ApplyMatchDetailVo> insertJSON = new ArrayList<>();
            insertJSON.add(buyJSON);
            insertJSON.add(sellJSON);

            //原生态 必写
            matchVo.setBatchSub(insertJSON, null, null);

            PmProcessSearchVo searchVo = new PmProcessSearchVo();
            searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            searchVo.setProcessCode(BasConstants.PROCESS_APPLY_MATCH); //代采流程Code

            //根据对象参数获取流程主表
            PmProcess process = pmProcessService.findByProcessCode(searchVo);
            if (process == null) {
                throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
            }
            //通过业务员id 获取申请人信息
            SysUserSdk userById = authOpenFacade.findUserById(matchVo.getApplyUserId());
            if (userById != null) {
                startVo.setUserId(userById.getUserId());
                startVo.setUserName(userById.getNickName());
                startVo.setProcessId(process.getId());
                startVo.setApproveId(0L);
                matchVo.setApproveId(0L);//代表新增
            }
            startVo.setBizEntityJson(JsonUtil.obj2Json(matchVo));
            pmApproveService.startFlow(startVo);
        } catch (ApplicationException e) {
            //抛出异常否者接收不到 会返回200
            throw new ApplicationException(e);
        }
    }

    @Override
    @ServerTransactional
    public void applyMatchIous(ApplyMatchVo matchVo) throws ApplicationException {
        log.info("applyMatch:{}", JsonUtil.obj2Json(matchVo));
        try {
            if (StringUtils.isEmpty(matchVo.getProductName())) {
                throw new ApplicationException("品种不能为null!");
            }
            if (StringUtils.isEmpty(matchVo.getBrandNumber())) {
                throw new ApplicationException("牌号不能为null!");
            }
            if (matchVo.getDealNumber() == null) {
                throw new ApplicationException("数量不能为null!");
            }
            if (StringUtils.isEmpty(matchVo.getFactoryName())) {
                throw new ApplicationException("厂商不能为null!");
            }
            if (StringUtils.isEmpty(matchVo.getWrapSpecs())) {
                throw new ApplicationException("包装规格不能为null!");
            }
            if (StringUtils.isEmpty(matchVo.getQualityStandard())) {
                throw new ApplicationException("质量标准不能为null!");
            }
            if (StringUtils.isEmpty(matchVo.getBuyInfo().getContractType())) {
                throw new ApplicationException("采购合同类型不能为null!");
            }
            if (StringUtils.isEmpty(matchVo.getSellInfo().getContractType())) {
                throw new ApplicationException("销售合同类型不能为null!");
            }
            if (matchVo.getBuyInfo().getCompanyId() == null) {
                throw new ApplicationException("采购方公司Id不能为null!");
            } else {
                String companyName = bsCompanyService.getEntity(matchVo.getBuyInfo().getCompanyId()).getCompanyName();
                matchVo.getBuyInfo().setCompanyName(companyName);
            }
            if (matchVo.getSellInfo().getCompanyId() == null) {
                throw new ApplicationException("销售方公司Id不能为null!");
            } else {
                String companyName = bsCompanyService.getEntity(matchVo.getSellInfo().getCompanyId()).getCompanyName();
                matchVo.getSellInfo().setCompanyName(companyName);
            }
            if (StringUtils.isEmpty(matchVo.getBuyInfo().getDeliveryAddr())) {
                throw new ApplicationException("交货地点不能为null!");
            }
            if (matchVo.getBuyInfo().getDealPrice() == null) {
                throw new ApplicationException("含税单价不能为null!");
            }
            matchVo.getBuyInfo().setDealAmountNotax(matchVo.getBuyInfo().getDealPriceNotax());
            if (matchVo.getBuyInfo().getDealAmountNotax() == null) {
                throw new ApplicationException("不含税单价不能为null!");
            }
            if (StringUtils.isEmpty(matchVo.getSellInfo().getDeliveryAddr())) {
                throw new ApplicationException("交货地点不能为null!");
            }
            if (matchVo.getSellInfo().getDealPrice() == null) {
                throw new ApplicationException("销售价不能为null!");
            }
            if (matchVo.getSellInfo().getPremium() == null) {
                throw new ApplicationException("加价不能为null!");
            }

            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);//中光亿云企业id
            matchVo.setEnterpriseId(startVo.getEnterpriseId());//获取企业id
            //采购
            ApplyMatchDetailVo buyJSON = matchVo.getBuyInfo();
            buyJSON.setMatchUserName(UserCache.getUserName(buyJSON.getMatchUserId()));

            //销售
            ApplyMatchDetailVo sellJSON = matchVo.getSellInfo();
            sellJSON.setMatchUserName(UserCache.getUserName(sellJSON.getMatchUserId()));

            List<ApplyMatchDetailVo> insertJSON = new ArrayList<>();
            insertJSON.add(buyJSON);
            insertJSON.add(sellJSON);

            //原生态 必写
            matchVo.setBatchSub(insertJSON, null, null);

            PmProcessSearchVo searchVo = new PmProcessSearchVo();
            searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            searchVo.setProcessCode(BasConstants.PROCESS_APPLY_MATCH_IOUS); //赊销（白条）

            //根据对象参数获取流程主表
            PmProcess process = pmProcessService.findByProcessCode(searchVo);
            if (process == null) {
                throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
            }
            //通过业务员id 获取申请人信息
            Long matchUserId = matchVo.getApplyUserId();
            if (matchUserId == null) {
                throw new ApplicationException("业务员Id不能为NULL！");
            } else {
                SysUserSdk userById = authOpenFacade.findUserById(matchUserId);
                if (userById != null) {
                    startVo.setUserId(userById.getUserId());
                    startVo.setUserName(userById.getNickName());
                    startVo.setProcessId(process.getId());
                    startVo.setApproveId(0L);
                    matchVo.setApproveId(0L);//代表新增
                }
            }
            startVo.setBizEntityJson(JsonUtil.obj2Json(matchVo));
            pmApproveService.startFlow(startVo);
        } catch (ApplicationException e) {
            //抛出异常否者接收不到 会返回200
            throw new ApplicationException(e);
        }
    }

    /**
     * 自动发起盖章申请
     * @param applyMatch
     * @param approve
     */
    @Override
    @ServerTransactional
    public void autoInitiatedSealUsage(ApplyMatch applyMatch, List<ApplyMatchDetail> matchDetailList, PmApprove approve) {
        MATCH_SCHEDULED_POOL.schedule(() -> {
            List<CtrContract> contractList = contractService.findByApproveId(applyMatch.getApproveId());
            PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, approve.getEnterpriseId());
            String dictCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), applyMatch.getOurCompanyName());
            for (ApplyMatchDetail detail : matchDetailList) {
                try {
                    PmApproveSaveVo startVo = new PmApproveSaveVo();
                    SealUsage usage = new SealUsage();
                    //合同盖章签署通知
                    CtrContract contract = contractList.stream().filter(c -> StringUtils.equals(detail.getContractType(), c.getContractType())).findAny().orElse(new CtrContract());
                    if (detail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                        if(StringUtils.equals(detail.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_BB_C)){
                            usage.setFileId(detail.getSellContentTemplateId());
                        }else{
                            usage.setFileId(detail.getSellContentTemplateId());
                            BsCompany company = bsCompanyService.getEntity(detail.getCompanyId());
                            if (Boolean.TRUE.equals(company.getOpenCfcaFlg())) {
                                SMSUtils.sendContractNo(company.getCompanyPhone(), detail.getContractNo());
                            }
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
                    if(StringUtils.isEmpty(detail.getOurCompanyName())){
                        usage.setCompanyName(dictCd);
                    }else{
                        usage.setCompanyName(detail.getOurCompanyName());
                    }
                    if(StringUtils.isEmpty(detail.getOurCompanyName())){
                        usage.setCompanyName(dictCd);
                    }else{
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
                    Boolean chaindc=StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB_C,detail.getBusinessType())?true:false;
                    usage.setChainDc(chaindc);
                    usage.setRealApproveId(approve.getId());
                    usage.setBusinessFlg(true);
                    usage.setVirtualType(contract.getContractNo().replaceAll("\\d", ""));
                    final List<CtrProduct> byContractId = ctrProductService.findByContractId(contract.getId());
                    Date payFullTime =StringUtils.equals("S",detail.getContractType()) ?detail.getReceiveFullTime():detail.getPayFullTime();
                    usage.setRemark("业务信息：付款时间"+payFullTime+"/"+"合同金额"+detail.getTotalAmount()+"/"+"牌号"+byContractId.get(0).getBrandNumber()+"/"+"品名"+byContractId.get(0).getProductName()+"/"+"数量"+byContractId.get(0).getDealNumber());
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
                    autoSealPdfSignFilter.generateFLKSealPDFSign(pmApprove, contract);
                    autoSealPdfSignFilter.generateFLKPurchaseOrder(pmApprove, contract);
                } catch (Exception e) {
                    logger.error("autoInitiatedSealUsage error", e);
                }
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 自动发起收货款审批
     * @param applyMatch
     * @param approve
     */
    @ServerTransactional
    public void autoReceive(ApplyMatch applyMatch, List<ApplyMatchDetail> matchDetailList, PmApprove approve, List<CtrContract> ctrContracts) {
        MATCH_SCHEDULED_POOL.schedule(() -> {
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
                    startVo.setAutoStartMessage("自动发起收定金审批任务");
                    pmApproveService.startFlow(startVo);
                    logger.info("autoApplyReceive.完成contract:{}", JsonUtil.obj2Json(ctr));
                }
                } catch(ApplicationException e){
                    logger.error("autoApplyReceive error,contract:{}", JsonUtil.obj2Json(ctr), e);
                }
        }, 5, TimeUnit.SECONDS);
    }

    private Long getBuyCompanyIdBySellContractId(Long contractId) {
        Long buyCompanyId = null;
        CtrContractRela rela = ctrContractRelaService.getRelaBySellContractId(contractId);
        if (rela != null) {
            buyCompanyId = rela.getBuyCompanyId();
        }
        return buyCompanyId;
    }


    @ServiceTransactional
    public void saveChainDetails(List<ApplyMatchChain> chainList, Long applyMatchId) throws ApplicationException {
        for (int i = 0; i < chainList.size(); i++) {
            ApplyMatchChain chain = chainList.get(i);
            if (i < chainList.size() - 1) {
                ApplyMatchChain nextChain = chainList.get(i + 1);
                if (chain.getSellDealPrice().compareTo(nextChain.getBuyDealPrice()) != 0) {
                    throw new ApplicationException("中间链条：上家的销售单价必须和下家的采购单价保持一致！");
                }
                if (StringUtils.equals(chain.getChainCompanyName(), nextChain.getChainCompanyName())) {
                    throw new ApplicationException("中间链条：上下游企业不能为相同！");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(chainList)) {
            applyMatchChainDao.saveAll(chainList);
        }
    }

    @ServiceTransactional
    public List<ApplyMatchDetail>  addchain(List<ApplyMatchDetail> applyMatchDetails, ApplyMatch match, PmApprove approve) throws ApplicationException {
        for (ApplyMatchDetail machDetail : applyMatchDetails) {
            CtrContractChain contract = new CtrContractChain();
            BeanUtils.copyProperties(machDetail, contract);
            contract.setId(null);
            if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                contract.setBsTemplateContractId(machDetail.getSellTemplateId());
                contract.setSellContentFileId(machDetail.getSellContentTemplateId());
                contract.setServiceContentFileId(machDetail.getServiceContentTemplateId());
                contract.setRemark(machDetail.getReceiveRemark());
                contract.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB_C);
                // 兼容以前版本 只要有结算方式字段就是赊销
                if (machDetail.getSettlementType() != null) {
                    machDetail.setDeliveryMode(BasConstants.DELIVERY_MODE_SX);
                    contract.setDeliveryMode(BasConstants.DELIVERY_MODE_SX);
                }
            }
            contract.setContractType(machDetail.getContractType());
            contract.setFileId(match.getFileId());
            String ourCompanyName = match.getOurCompanyName();
            // 我方企业抬头
            if (StringUtils.isBlank(contract.getOurCompanyName())) {
                contract.setOurCompanyName(ourCompanyName);
            }
            contract.setMatchUserId(machDetail.getMatchUserId());
            contract.setMatchUserName(machDetail.getMatchUserName());
            contract.setProductBrand(match.getProductName());
            contract.setProductNum(match.getBrandNumber());
            contract.setContractModel(match.getContractModel());
            contract.setApproveNo(approve.getApproveNo());
            contract.setTotalNumber(match.getDealNumber());
            if (contract.getCompanyName() == null) {
                contract.setCompanyName("");
            }
            // 保存合同主表
            contract.setId(0L);
            contract.setBusinessNo(contract.getContractNo()); // 业务编号 同步为合同编号
            contract.setContractTime(new Date());
            contract.setBusinessTypeDcsx("S");
            contract.setContractStatus(BasConstants.CONTRACTSTATUS_B);//已审批
            contract.setProductStatus(BasConstants.PRODUCT_STATUS_P);// 新增合同为在途
            contract.setStatus(BasConstants.APPROVE_STATUS_D);
            contract.setApproveId(approve.getId());
            if (contract.getLongFlg() == null) {
                contract.setLongFlg(false);
            }
            Long userId = approve.getCreateUserId();
            if (StringUtils.isBlank(contract.getMatchUserName()) || Objects.isNull(contract.getMatchUserId())){
                if (!(BasConstants.APPLY_TYPE_M.equals(contract.getSource()) || BasConstants.APPLY_TYPE_R.equals(contract.getSource()))) {
                    contract.setMatchUserId(userId);// 业务员id
                    contract.setMatchUserName(approve.getCreateUserName());// 业务员姓名
                }
            }
            SysDeptSdk dept = authOpenFacade.findDeptByUserId(userId);
            if (dept != null) {
                contract.setDeptId(dept.getDeptId());
            }
            CtrContractChain save = ctrContractChainDao.save(contract);
            machDetail.setContractId(save.getId());
            CtrContract contract2 = new CtrContract();
            BeanUtils.copyProperties(save, contract2);
            List<CtrProduct> lstProduct = new ArrayList<>();
            // 获得商品明细
            List<ApplyProductDetail> productLists = productDetailService.findApplyDetail(machDetail.getId(),
                    BasConstants.APPLY_TYPE_M);
            // 保存商品信息表
            for (ApplyProductDetail appProd : productLists) {
                // 保存合同商品明细
                CtrProduct ctrProd = new CtrProduct();
                BeanUtils.copyProperties(appProd, ctrProd);
                ctrProd.setEnterpriseId(contract.getEnterpriseId());
                ctrProd.setId(0L);
                ctrProd.setDealPrice(save.getDealPrice());
                ctrProd.setCtrContractId(contract2.getId());
                ctrProd.setRemainNumber(appProd.getDealNumber());
                ctrProd.setTotalPrice(appProd.getDealNumber().multiply(save.getDealPrice()));
                lstProduct.add(ctrProd);
            }
            threadPool.execute(() -> {
                // 生成电子合同
                try {
                    ctrContractChainTextService.saveContractText(contract2, lstProduct);
                } catch (ApplicationException e) {
                    logger.error("生成电子合同异常", e);
                }
            });
        }
        return applyMatchDetails;
    }

    @ServiceTransactional
    public List<ApplyMatchDetail> util(List<ApplyMatchDetail> applyMatchDetails, ApplyMatch vo, PmApprove approve, List<ApplyMatchChain> matchChains) throws ApplicationException {
        ApplyMatchDetail applyMatchDetailVo = applyMatchDetails.get(0);
        List<ApplyMatchDetail> matchDetails = new ArrayList<>();
        List<ApplyMatchDetailVo> matchVoList = new ArrayList<>();
        for (ApplyMatchDetail applyMatchDetail : applyMatchDetails) {
            ApplyMatchDetailVo vo1 = new ApplyMatchDetailVo();
            BeanUtils.copyProperties(applyMatchDetail, vo1);
            matchVoList.add(vo1);
        }
        if (StringUtils.isEmpty(applyMatchDetailVo.getSettlementType())) {
            List<ApplyMatchDetailVo> applyMatchDetailVoList = new ArrayList<>();
            //获取上中下游所有企业
            for (ApplyMatchChain applyMatchChain : matchChains) {
                ApplyMatchDetailVo chain = new ApplyMatchDetailVo();
                BeanUtils.copyProperties(applyMatchDetails.get(1), chain);
                chain.setCompanyName(applyMatchChain.getChainCompanyName());
                chain.setSellDealPrice(applyMatchChain.getSellDealPrice());
                chain.setCompanyId(null);
                chain.setDeliveryAddr("");
                chain.setBuyDealPrice(applyMatchChain.getBuyDealPrice());
                applyMatchDetailVoList.add(chain);
            }
//                vo.getLstInsert().get(vo.getLstInsert().size() - 1).setBuyDealPrice(vo.getLstInsert().get(vo.getLstInsert().size() - 1).getDealPrice());
            matchVoList.addAll(1, applyMatchDetailVoList);
            List<ApplyMatchDetailVo> lists = new ArrayList<>();
            List<ApplyMatchDetailVo> lstInsert1 = matchVoList;
            for (int i = 0; i < lstInsert1.size(); i++) {
                if (i < lstInsert1.size() - 1) {
                    ApplyMatchDetailVo applyMatchDetail1 = lstInsert1.get(i);
                    ApplyMatchDetailVo applyMatchDetail2 = lstInsert1.get(i + 1);
                    if (applyMatchDetail2 != null && i >= 1) {
                        //i==1我方卖给第一家中间商
                        if (i == 1) {
                            ApplyMatchDetailVo u = new ApplyMatchDetailVo();
                            BeanUtils.copyProperties(applyMatchDetail1, u);
                            u.setCompanyName(applyMatchDetail1.getCompanyName());
                            u.setOurCompanyName(vo.getOurCompanyName());
                            u.setTotalAmount(vo.getDealNumber().multiply(applyMatchDetail1.getBuyDealPrice()));
                            u.setDealAmountNotax(applyMatchDetail1.getBuyDealPrice());
                            u.setDealPrice(applyMatchDetail1.getBuyDealPrice());
                            u.setContractType("S");
                            u.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB_C);
                            lists.add(u);
                        }
                        ApplyMatchDetailVo detailVo = new ApplyMatchDetailVo();
                        BeanUtils.copyProperties(applyMatchDetail2, detailVo);
                        detailVo.setCompanyName(applyMatchDetail2.getCompanyName());
                        detailVo.setOurCompanyName(applyMatchDetail1.getCompanyName());
                        //不是最终销售合同
                        if (i != lstInsert1.size() - 2) {
                            detailVo.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_BB_C);
                            detailVo.setDealAmountNotax(applyMatchDetail1.getSellDealPrice());
                            detailVo.setDealPrice(applyMatchDetail1.getSellDealPrice());
                            detailVo.setTotalAmount(vo.getDealNumber().multiply(applyMatchDetail1.getSellDealPrice()));
                        }
                        detailVo.setContractType("S");
                        lists.add(detailVo);
                    } else {
                        lists.add(lstInsert1.get(i));
                    }
                }else{
                    lists.add(lstInsert1.get(i));
                }
            }
            int flag = 0;
            lists.remove(lists.size()-1);
            int size = lists.size() - 1;
            for (ApplyMatchDetailVo list : lists) {
                ApplyMatchDetail matchDetail = new ApplyMatchDetail();
                BeanUtils.copyProperties(list, matchDetail);
                matchDetail.setMatchUserId(list.getMatchUserId());
                matchDetail.setMatchUserName(list.getMatchUserName());
                SysDeptSdk dept = authOpenFacade.findDeptByUserId(list.getMatchUserId());
                if (BasConstants.CONTRACT_STATUS_S.equals(matchDetail.getContractType())) {
                        //生成中间链条合同（代采业务）
                        if (flag > 0 && flag != size) {
                            //中间链条合同 必须先生成采购合同号(SPTB)后转为销售合同号（SPTS） 否则会直接生成采购合同对应的销售合同 导致该业务销售合同号与中间合同号吻合
                            String contractNoChain = BasBusinessUtil.composeContractNo(vo.getEnterpriseId(), dept.getDeptName(),
                                    BasConstants.CONTRACT_STATUS_S);
                            matchDetail.setContractNo(contractNoChain);
                        }
                    }
                // 获得供货商id
                Long companyId = list.getCompanyId();
                //如果是代采中间商合同 则从中游合同企业信息表取得中间商信息
                if (StringUtils.equals(list.getBusinessType(), BasConstants.BUSINESS_TYPE_ZY_BB_C)) {
                    BsCompanyDcsx byCompanyName = bsCompanyDcsxService.findByCompanyName(list.getCompanyName());
                    // 账户
                    matchDetail.setCompanyAccount(byCompanyName.getCompanyCardId());
                    // 银行
                    matchDetail.setCompanyBank(byCompanyName.getCompanyBankName());
                    // 企业名称
                    if (StringUtils.isNotBlank(list.getOurCompanyName())) {
                        matchDetail.setCompanyName(list.getCompanyName());
                    } else {
                        matchDetail.setCompanyName(byCompanyName.getCompanyName());
                    }
                    // 联系人
                    matchDetail.setContactName(byCompanyName.getCompanyContact());

                }
                matchDetails.add(matchDetail);
//                applyMatchChainService.updateChainApproveId(vo.getId(), vo.getApproveId());
                flag++;
            }

        }
        return matchDetails;
    }

    private boolean verifyMatchCreditFlg(List<ApplyMatchDetail> matchDetailList){
        return matchDetailList.stream()
                .filter(m -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, m.getContractType()))
                .anyMatch(m -> Objects.nonNull(m.getSettlementType()));
    }
}




