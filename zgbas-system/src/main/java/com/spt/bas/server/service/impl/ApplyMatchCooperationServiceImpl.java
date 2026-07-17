package com.spt.bas.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.CreditFlowEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.filter.IBudgetVerifyFilter;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 代采赊销(合作)
 *
 * @Author MoonLight
 * @Date 2023/8/1 14:43
 * @Version 1.0
 */
@Slf4j
@Transactional(readOnly = true)
@Component(value = "applyMatchCooperationService")
public class ApplyMatchCooperationServiceImpl extends BaseService<ApplyMatch> implements IApplyMatchCooperationService, IPmService, IPmApproveListener {
    @Resource
    private ApplyMatchDao applyMatchDao;
    @Resource
    private IBsCompanyService bsCompanyService;
    @Resource
    private IApplyProductDetailService productDetailService;
    @Resource
    private ApplyProductDetailDao applyProductDetailDao;
    @Resource
    private ApplyMatchDetailDao applyMatchDetailDao;
    @Resource
    private ICtrContractSaveService contractSaveService;
    @Resource
    private ICtrContractService contractService;
    @Resource
    private IBsKeySequenceService bsKeySequenceService;
    @Resource
    private IStockContractService stockContractService;
    @Resource
    private ICtrContractService ctrContractService;
    @Resource
    private IPmApproveService pmApproveService;
    @Resource
    private IBsConfigService bsConfigService;
    @Resource
    private ApplyCtrContractFactoDao applyCtrContractFactoDao;
    @Resource
    private BsDictDataDao bsDictDataDao;
    @Resource
    private IBsCompanyCreditFlowService companyCreditFlowService;
    @Resource
    private ICtrContractProfitService ctrContractProfitService;
    @Resource
    private ICtrLogisticsService ctrLogisticsService;
    @Resource
    private CtrContractChainDao ctrContractChainDao;
    @Resource
    private ApplyDcsxDao applyDcsxDao;
    @Resource
    private ApplyChargeSalesService applyChargeSalesService;
    @Resource
    private  ICtrContractDcsxApplyService ctrContractDcsxApplyService;
    @Autowired
    private BsCompanyOurDao companyOurDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private IBusinessRestrictRelieveService businessRestrictRelieveService;
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

    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        // 代采业务不会涉及到授信额度等信息
        // 处理判断是否所有白条业务,并返回相关参数
        MatchCreditParseVo parseVo = dealWithMatchCreditFlg(approve.getBizId());
        ApplyMatch applyMatch = parseVo.getMatch();

        // 业务配置开关判断
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(applyMatch.getId());
        BsConfigReqVo configReqVo = new BsConfigReqVo(applyMatch.getOurCompanyName(), applyMatch.getContractModel(),
                BasConstants.CONFIG_TYPE_FUND_SOURCE_OUR, approve.getEnterpriseId(), approve.getProcessId(), applyMatch.getBuyAmount());
        BsConfigRespVo bsConfigRespVo = bsConfigService.judgmentStart(configReqVo);
        if (bsConfigRespVo != null && Boolean.FALSE.equals(bsConfigRespVo.getStartFlg())) {
            throw new ApplicationException(bsConfigRespVo.getMessage());
        }

        // 代采毛利率不可低于规定否则不可发起
        BsConfigRespVo profitConfig = bsConfigService.judgmentMatchProfit(matchDetailList, approve.getEnterpriseId(), parseVo.getCreditFlg());
        if (Objects.nonNull(profitConfig) && Boolean.FALSE.equals(profitConfig.getStartFlg())) {
            throw new ApplicationException(profitConfig.getMessage());
        }
        if (applyMatch.getId() != null) {
            applyMatch.setBusinessRestrictRelieveFlg(profitConfig.getBusinessRestrictRelieveFlg());
            applyMatchDao.save(applyMatch);
        }

        // 采购合同 使用授信
        dealWithBuyCompany(parseVo);

        // 销售合同 使用授信
        dealWithSellCompany(parseVo, approve);

        // 更新业务配置额度
        updateBsConfig(bsConfigRespVo, approve, applyMatch);
    }


    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            String pairCode = bsKeySequenceService.getNextKey(BasConstants.KEY_PAIR_CODE, approve.getEnterpriseId());
            // 获得撮合信息
            ApplyMatch match = applyMatchDao.findOne(approve.getBizId());
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
//                companyOurList.stream().filter(b->b.getOurCompanyFlag()==true).forEach(s->{
//                    if(s.getCompanyName().equals(match.getBuyOurCompanyName())){
//                        // 如我司企业处于上游代采位置
//                        companyOurDcFlg = true;
//                    }else if(s.getCompanyName().equals(match.getOurCompanyName())){
//                        // 如我司企业处于下游赊销位置
//                        
//                    }
//                });
            }
            String contractAttr = match.getContractAttr();
            String ourCompanyName = match.getOurCompanyName();
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
                contract.setId(null);
                //保存预算运输费和预算仓储费后期不被覆盖
                contract.setApproveTransportAmount(machDetail.getTransportCost());
                contract.setApproveWarehouseAmount(machDetail.getWarehouseCost());
                contract.setApproveStevedorage(machDetail.getStevedorage());
                if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_B)){
                    buyDetail = machDetail;
                }
                if (machDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                    sellDetail = machDetail;
                    contract.setCarrier(machDetail.getCarrier());
                    contract.setBsTemplateContractId(machDetail.getSellTemplateId());
                    contract.setSellContentFileId(machDetail.getSellContentTemplateId());
                    contract.setServiceContentFileId(machDetail.getServiceContentTemplateId());
                    contract.setDeliveryDateTo(machDetail.getDeliveryDate());
                    contract.setDeliveryDateFrom(machDetail.getDeliveryDate());
                    if(Boolean.TRUE.equals(matchCreditFlg)){
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
                    //手续费是 供应链金融服务费+加价(一票制:最低价*0.0003*赊销时长*数量)
                    //最低价:含税采购价+运输单价+仓储单价+加价
                    // 最低价(基础价) = 含税采购价 + 运输单价 + 仓储单价 + 加价
                    BsCompany company = bsCompanyService.findCompany(match.getSellCompanyId());
                    if(company.getCompanyGrade()==null){
                        company.setCompanyGrade(BasConstants.Customer_Rating_Level_N);
                    }
                    if(company.getCompanyGrade() == null){
                        company.setOpenCfcaFlg(false);
                    }
                } else {
                    contract.setCarrier(machDetail.getCarrier());
                    // 采购合同
                    boolean creditFlgBuy = false;
                    if (StringUtils.equals(BasConstants.DELIVERY_MODE_SX,machDetail.getDeliveryMode())|| StringUtils.equals(BasConstants.DELIVERY_MODE_XHHK,machDetail.getDeliveryMode())){
                        creditFlgBuy = true;
                    }
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
                contract.setRemark(machDetail.getPayRemark());
                contract.setPayMode(machDetail.getPayKind());
                contract.setMatchUserId(machDetail.getMatchUserId());
                contract.setMatchUserName(machDetail.getMatchUserName());
                contract.setDcsxFlg(true);
                BsCompany company = bsCompanyDao.findOne(contract.getCompanyId());
                if(Objects.nonNull(company)) {
                    contract.setCompanyPiccFlg(Boolean.TRUE.equals(company.getPiccFlg()));
                }
                // 设置 赊销业务类型
                String contractModel = match.getContractModel();
                if(StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSX,contractModel)) {
                    if(companyOurDcFlg) {
                        // 判断我司企业处于上游    
                        contract.setBusinessKind(BasConstants.BusinessKind.DICT_SXDC);
                    } else if(companyOurSxFlg){
                        // 判断我司企业处于下游
                        contract.setBusinessKind(BasConstants.BusinessKind.DICT_DCSX);
                    } else {
                        contract.setBusinessKind(BasConstants.BusinessKind.DICT_DCSX);
                    }
                } else if(StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXBL,contractModel)) {
                    contract.setBusinessKind(BasConstants.BusinessKind.DICT_DCSXBL);
                } else if(StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXHDFK,contractModel)) {
                    if(companyOurDcFlg) {
                        // 判断我司企业处于上游    
                        contract.setBusinessKind(BasConstants.BusinessKind.DICT_SXDCHDFK);
                    } else if(companyOurSxFlg){
                        // 判断我司企业处于下游
                        contract.setBusinessKind(BasConstants.BusinessKind.DICT_DCSXHDFK);
                    } else {
                        contract.setBusinessKind(BasConstants.BusinessKind.DICT_DCSXHDFK);
                    }
                } else if(StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXCK,contractModel)) {
                    contract.setBusinessKind(BasConstants.BusinessKind.DICT_DCSXCK);
                } else {
                    contract.setBusinessKind(BasConstants.BusinessKind.DICT_DCSX);
                }
                // 获得商品明细
                List<ApplyProductDetail> productList = productDetailService.findApplyDetail(machDetail.getId(),
                        BasConstants.APPLY_TYPE_M);
                contract.setBondAmount(bondAmount);
                contract.setBondRate(bondRate);
                contract.setMatchCreditFlg(matchCreditFlg);
                // 保存撮合排序号
                contract.setPairCode(pairCode);
                contract.setBusinessTypeDcsx(match.getContractModel());
                contract.setContractModel(match.getContractModel());
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

            ApplyCtrDCSX applyCtrDCSX = applyChargeSalesService.parseCtrDcsx(approve, match, buyDetail, sellDetail);
            applyCtrDCSX = applyDcsxDao.save(applyCtrDCSX);
            ctrContractDcsxApplyService.saveCtrContractApply(applyCtrDCSX.getId(),applyCtrDCSX.getEnterpriseId());

            if (CollectionUtils.isNotEmpty(sellProductList)) {
                contractSaveService.saveContract(sellContract, sellProductList, approve);
            }

            if (sellI == 1) {
                Long sellContractId = lstSellId.get(0);
                CtrContract sell = contractService.getEntity(sellContractId);
                sell.setLinkContractId("," + Joiner.on(",").join(lstBuyId) + ",");
                for (int i = 0; i < buyI; i++) {
                    CtrContract buy = contractService.getEntity(lstBuyId.get(i));
                    buy.setLinkContractId("," + sellContractId + ",");
                    buy.setCooperationMatchUserId(sellDetail.getMatchUserId());
                    buy.setCooperationMatchUserName(sellDetail.getMatchUserName());
                }
            } else if (buyI == 1) {
                Long buyContractId = lstBuyId.get(0);
                CtrContract buy = contractService.getEntity(buyContractId);
                buy.setLinkContractId("," + Joiner.on(",").join(lstSellId) + ",");
                for (int i = 0; i < buyI; i++) {
                    CtrContract sell = contractService.getEntity(lstSellId.get(i));
                    sell.setLinkContractId("," + buyContractId + ",");
                    sell.setCooperationMatchUserId(buyDetail.getMatchUserId());
                    sell.setCooperationMatchUserName(buyDetail.getMatchUserName());
                }
            }

            CtrContract bl=null;
            final List<CtrContract> byApproveId = contractService.findByApproveId(match.getApproveId());
            for (CtrContract contract : byApproveId) {
                if(contract.getContractType().equals("S")){
                    bl=contract;
                }
            }
            if((BasConstants.APPLY_MODEL_BL).equals(match.getContractModel()) && Objects.nonNull(bl)){
                ApplyCtrContractFactor applyCtrContractFactor=new ApplyCtrContractFactor();
                applyCtrContractFactor.setApproveId(bl.getApproveId());
                applyCtrContractFactor.setOurCompanyName(bl.getOurCompanyName());
                applyCtrContractFactor.setContractNo(bl.getContractNo());
                applyCtrContractFactor.setFactorStatus("N");
                applyCtrContractFactor.setRepaymentApplyStatus("");
                applyCtrContractFactor.setPlanBackDate(bl.getPayFullTime());
                applyCtrContractFactor.setContractAmount(bl.getTotalAmount());
                applyCtrContractFactor.setBondAmount(bl.getBondAmount());
                applyCtrContractFactor.setContractId(bl.getId());
                //保理公司
                String value = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.FACTOR_COMPANY, "BLGS");
                applyCtrContractFactor.setFactorCompany(value);
                applyCtrContractFactoDao.save(applyCtrContractFactor);
            }
            // 审批完成自动生成盖章申请
            applyChargeSalesService.autoInitiatedSealUsage(applyCtrDCSX, match, matchList, approve);

            // 初始化保存合同利润统计汇总数据
            ctrContractProfitService.initContractProfit(approve, match);

            // 保存物流单据
            saveLogistics(approve);
        }
    }

    /**
     * 保存物流单据
     * @param approve
     */
    private void saveLogistics(PmApprove approve) throws ApplicationException {
        // 查出采购销售合同列表
        List<CtrContract> contractList = contractService.findByApproveId(approve.getId());
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

    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        // 代采业务不会涉及到授信额度等信息
        // 处理判断是否所有白条业务,并返回相关参数
        Boolean completeDismissalFlg = approve.getCompleteDismissalFlg();
        MatchCreditParseVo parseVo = dealWithMatchCreditFlg(approve.getBizId());
        // 是否使用授信额度
        ApplyMatch applyMatch = applyMatchDao.findOne(approve.getBizId());
        if (Objects.isNull(completeDismissalFlg) || Boolean.FALSE.equals(completeDismissalFlg)) {
            ApplyMatch match = parseVo.getMatch();
            if (Boolean.TRUE.equals(parseVo.getCreditFlgBuy())) {
                // 上游企业
                BsCompany buyCompany = bsCompanyService.getEntity(match.getBuyCompanyId());
                buyCompany.setUsedSupplierPurchaseAmount(buyCompany.getUsedSupplierPurchaseAmount().subtract(match.getBuyAmount()));
                buyCompany.setUsedSupplierPrepayAmount(buyCompany.getUsedSupplierPrepayAmount().subtract(match.getBuyAmount()));
                bsCompanyService.save(buyCompany);
            }
        }

        if (Boolean.TRUE.equals(parseVo.getCreditFlg())) {
            BsCompany bsCompany = bsCompanyService.getEntity(parseVo.getCompanyId());
            String approveNo = approve.getApproveNo();
            if (Boolean.TRUE.equals(completeDismissalFlg)) {
                companyCreditFlowService.updateUsedCreditAmount(approveNo, bsCompany, applyMatch.getSellAmount().negate(), CreditFlowEnum.CC);
            } else {
                companyCreditFlowService.updateApproveCreditAmount(approveNo, bsCompany, applyMatch.getSellAmount().negate(), CreditFlowEnum.AC);
            }
        }

        if (Objects.nonNull(applyMatch) && applyMatch.getBusinessRestrictRelieveFlg() != null && applyMatch.getBusinessRestrictRelieveFlg() && applyMatch.getId() != null) {
            List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(applyMatch.getId());
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
        if (Objects.nonNull(applyMatch.getBsConfigId())) {
            log.info("更新业务配置可用额度 approveNo:{},bsConfigId:{},contractAmount:{}", approve.getApproveNo(), applyMatch.getBsConfigId(), applyMatch.getBuyAmount());
            bsConfigService.refreshBalance(approve.getApproveNo(), applyMatch.getBsConfigId(), applyMatch.getBuyAmount());
        }
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

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // 代采业务不会涉及到授信额度等信息
        // 处理判断是否所有白条业务,并返回相关参数
        PmApprove approve = pmApproveService.getEntity(vo.getApproveId());
        MatchCreditParseVo parseVo = dealWithMatchCreditFlg(vo.getBizId());
        ApplyMatch match = parseVo.getMatch();
        if (Boolean.TRUE.equals(parseVo.getCreditFlgBuy())) {
            // 上游企业
            BsCompany buyCompany = bsCompanyService.getEntity(match.getBuyCompanyId());
            buyCompany.setUsedSupplierPurchaseAmount(buyCompany.getUsedSupplierPurchaseAmount().subtract(match.getBuyAmount()));
            buyCompany.setUsedSupplierPrepayAmount(buyCompany.getUsedSupplierPrepayAmount().subtract(match.getBuyAmount()));
            bsCompanyService.save(buyCompany);
        }
        // 使用授信
        if (Boolean.TRUE.equals(parseVo.getCreditFlg())) {
            String approveNo = approve.getApproveNo();
            BsCompany bsCompany = bsCompanyService.getEntity(parseVo.getCompanyId());
            if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, match.getStatus())) {
                companyCreditFlowService.updateApproveCreditAmount(approveNo, bsCompany, match.getSellAmount().negate(), CreditFlowEnum.AC);
            } else if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, match.getStatus())) {
                companyCreditFlowService.updateUsedCreditAmount(approveNo, bsCompany, match.getSellAmount().negate(), CreditFlowEnum.CC);
            }
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
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyMatch entity = new ApplyMatch();
        List<ApplyMatchDetail> matchDetailList = new ArrayList<>();
        List<ApplyProductDetail> sellProductList = new ArrayList<>();
        if (pmEntity instanceof ApplyMatchVo) {
            ApplyMatchVo vo = (ApplyMatchVo) pmEntity;
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
            for (ApplyMatchDetailVo list : vo.getLstInsert()) {
                // 撮合明细
                ApplyMatchDetail matchDetail = new ApplyMatchDetail();
                BeanUtils.copyProperties(list, matchDetail);
                matchDetail.setMatchUserId(list.getMatchUserId());
                matchDetail.setMatchUserName(list.getMatchUserName());
                if (matchDetail.getId() == null) {
                    if (BasConstants.CONTRACT_STATUS_B.equals(matchDetail.getContractType())) {
                        String contractNo = matchDetail.getContractNo();
                        if(StringUtils.isBlank(contractNo)){
                            // 生成合同号
                            contractNo = BasBusinessUtil.composeContractNo(vo.getEnterpriseId(), vo.getDeptAbbr(), matchDetail.getContractType());
                        }
                        matchDetail.setContractNo(contractNo);
                        buyContractNo = contractNo;
                        entity.setBuyOurCompanyName(matchDetail.getOurCompanyName());
                    } else if (BasConstants.CONTRACT_STATUS_S.equals(matchDetail.getContractType())) {
                        logger.info("buyContractNo:{}", buyContractNo);
                        matchDetail.setContractNo(BasBusinessUtil.buildSellContractNo(buyContractNo));
                        entity.setSellOurCompanyName(list.getSellOurCompanyName());
                    }
                }
                if(StringUtils.isNotBlank(list.getCarrier())){
                    matchDetail.setCarrier(list.getCarrier());
                }
                // 获得供货商id
                Long companyId = list.getCompanyId();
                if(!Objects.isNull(companyId)){
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
                }

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
                List<ApplyProductDetail> productArr = productDetailService.findApplyDetail(matchDetail.getId(),
                        BasConstants.APPLY_TYPE_M);
                BigDecimal totalPrice = BigDecimal.ZERO;
                for (ApplyProductDetail detail : productArr) {
                    BigDecimal currTotalPrice = Objects.isNull(detail.getTotalPrice()) ? BigDecimal.ZERO: detail.getTotalPrice();
                    totalPrice = totalPrice.add(currTotalPrice);
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
                matchDetailList.add(matchDetail);
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
        dealWithCooperation(entity);
        return entity;
    }

    /**
     * 标题
     *
     * @param pmEntity
     * @param pmProcess
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        //代采：合同尾号，品名/牌号/数量，上游-我方-下游，采购总价
        ApplyMatch vo = (ApplyMatch) pmEntity;
        Long matchId = vo.getId();
        List<ApplyMatchDetail> list = applyMatchDetailDao.findByApplyMatchId(matchId);
        StringBuilder strBuf = new StringBuilder();
        BigDecimal totalPrice = new BigDecimal(0);
        String contractNo = "";
        ApplyMatchDetail applyMatchDetail = list.get(0);
        ApplyMatchDetail applyMatchDetail1 = list.get(1);
        contractNo = applyMatchDetail.getContractNo().substring(4);
        Long applyId = applyMatchDetail.getId();
        List<ApplyProductDetail> productlist = productDetailService.findApplyDetail(applyId,
                BasConstants.APPLY_TYPE_M);
        for (ApplyProductDetail product : productlist) {
            String contractyType = applyMatchDetail.getContractType();
            totalPrice = StringUtils.equals(BasConstants.APPLY_TYPE_B, contractyType) ? product.getTotalPrice() : totalPrice;
            String productName = product.getProductName();
            String productCd = product.getProductCd();
            String realOutNumber = NumberUtil.formatNumber(product.getDealNumber(), "#.###");
            String brandNumber = product.getBrandNumber();
            if (productCd.indexOf("SL") > 0) {
                strBuf.append(productName).append("/").append(brandNumber).append("/").append(realOutNumber + RuleUtil.weightUnit);
            } else {
                strBuf.append(productName).append("/").append(realOutNumber + RuleUtil.weightUnit);
            }
        }
        String companyName1 = RuleUtil.companyNameSubString(list.get(0).getCompanyName());
        String companyName2 = RuleUtil.companyNameSubString(vo.getOurCompanyName());
        String companyName3 = RuleUtil.companyNameSubString(list.get(1).getCompanyName());
        String contractModel = "";
        if (StringUtils.isNotBlank(vo.getContractModel())) {
            switch (vo.getContractModel()) {
                case "BL":
                    contractModel = "保理模式";
                    break;
                case "PT":
                    contractModel = "普通模式";
                    break;
                case "DCSXBL":
                    contractModel = "保理模式";
                    break;
                case "DCSX":
                    contractModel = "普通模式";
                    break;
                case "DCSXHDFK":
                    contractModel = "货到付款";
                    break; 
                case "DCSXCK":
                    contractModel = "代采出口";
                    break;
                case "HDFK":
                    contractModel = "货到付款";
                    break;
            }
        } else {
            contractModel = "普通模式";
        }
        String company = "";
        if (StringUtils.isNotBlank(companyName1) && StringUtils.isNotBlank(companyName2) && StringUtils.isNotBlank(companyName3)) {
            company = companyName1 + "-" + companyName2 + "-" + companyName3;
        }
        String s = strBuf.toString();
        String subject;
        if (applyMatchDetail1.getSettlementType() != null) {
            subject = SubjectUtil.formatSubject(contractNo, s, contractModel, company, SubjectUtil.formatMoney(list.get(0).getTotalAmount() , RuleUtil.monetaryUnit));
            Integer creditDays = list.get(1).getCreditDays();
            if (Objects.nonNull(creditDays)){
                subject = SubjectUtil.formatSubject(contractNo, s, contractModel, company, SubjectUtil.formatMoney(list.get(0).getTotalAmount() , RuleUtil.monetaryUnit), list.get(1).getCreditDays() + RuleUtil.dateUnit);
            }
        } else {
            subject = SubjectUtil.formatSubject(contractNo, s, company, SubjectUtil.formatMoney(list.get(0).getTotalAmount() , RuleUtil.monetaryUnit));
        }
        return subject;
    }

    /**
     * 获取业务员id
     *
     * @param pmEntity
     */
    @Override
    public Long getMatchUserId(IPmEntity pmEntity) {
        return IPmService.super.getMatchUserId(pmEntity);
    }

    /**
     * 更新业务配置额度
     *
     * @param bsConfigRespVo
     * @param approve
     * @param applyMatch
     */
    private void updateBsConfig(BsConfigRespVo bsConfigRespVo, PmApprove approve, ApplyMatch applyMatch) {
        if (Objects.nonNull(bsConfigRespVo) && Objects.nonNull(bsConfigRespVo.getBsConfig())) {
            BsConfig bsConfig = bsConfigRespVo.getBsConfig();
            log.info("更新业务配置可用额度 approveNo:{},bsConfigId:{},contractAmount:{}", approve.getApproveNo(), bsConfig.getId(), applyMatch.getBuyAmount().negate());
            applyMatch.setBsConfigId(bsConfig.getId());
            applyMatchDao.save(applyMatch);
            bsConfigService.refreshBalance(approve.getApproveNo(), bsConfig.getId(), applyMatch.getBuyAmount().negate());
        }
    }

    /**
     * 处理判断是否是白条业务,并返回相关参数
     *
     * @param bizId
     * @return
     */
    private MatchCreditParseVo dealWithMatchCreditFlg(Long bizId) {
        boolean creditFlg = false;
        boolean creditFlgBuy = false;
        Long companyId = 0L;
        ApplyMatch match = applyMatchDao.findOne(bizId);
        List<ApplyMatchDetail> matchList = applyMatchDetailDao.findByApplyMatchId(match.getId());
        for (ApplyMatchDetail applyMatchDetail : matchList) {
            if (applyMatchDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                // 结算方式 0：赊销(一票制) 1:赊销(两票制)
                String settlementType = applyMatchDetail.getSettlementType();
                // 公司id
                companyId = applyMatchDetail.getCompanyId();
                // 是否使用授信额度
                creditFlg = !StringUtils.isEmpty(settlementType);
            } else {
                if (StringUtils.equals(BasConstants.DELIVERY_MODE_SX, applyMatchDetail.getDeliveryMode())) {
                    creditFlgBuy = true;
                }
            }
        }
        return new MatchCreditParseVo(creditFlg, creditFlgBuy, companyId, match);
    }

    /**
     * 校验采购额度
     *
     * @param company
     * @param buyAmount 本次采购金额
     * @return
     */
    private boolean checkSupplierPurchase(BsCompany company, BigDecimal buyAmount) {
        // 采购总额度
        BigDecimal totalCredit = company.getSupplierPurchaseAmount();

        BigDecimal usedCredit = company.getUsedSupplierPurchaseAmount();
        // 总额度小于使用额度
        if (totalCredit.compareTo(usedCredit.add(buyAmount)) < 0) {
            return false;
        }

        company.setUsedSupplierPurchaseAmount(usedCredit.add(buyAmount));
        return true;
    }

    /**
     * 预付款额度变动
     *
     * @param company
     * @return
     */
    private void changeSupplierPrepaye(BsCompany company, BigDecimal buyAmount) {
        company.setUsedSupplierPrepayAmount(company.getUsedSupplierPrepayAmount().add(buyAmount));
    }


    /**
     * 设置合作业务员
     * @param applyMatch
     */
    private void dealWithCooperation(ApplyMatch applyMatch) throws ApplicationException {
        PmApprove approve = pmApproveService.getEntity(applyMatch.getApproveId());
        if (Objects.isNull(approve)) {
            return;
        }
        Long cooperationUserId = applyMatch.getCooperationUserId();
        if (Objects.nonNull(cooperationUserId)) {
            approve.setCooperationUserId(cooperationUserId);
            pmApproveService.save(approve);
        }
    }

    private void dealWithBuyCompany(MatchCreditParseVo parseVo) throws ApplicationException {
        ApplyMatch match = parseVo.getMatch();
        if (Boolean.TRUE.equals(parseVo.getCreditFlgBuy())) {
            // 上游企业
            BsCompany buyCompany = bsCompanyService.getEntity(match.getBuyCompanyId());
            // 采购额度校验
            if (!checkSupplierPurchase(buyCompany, match.getBuyAmount())) {
                throw new ApplicationException("申请中的采购总金额大于总额度,无法发起");
            }
            // 预付款额度变动
            changeSupplierPrepaye(buyCompany, match.getBuyAmount());
            bsCompanyService.save(buyCompany);
        }
    }

    private void dealWithSellCompany(MatchCreditParseVo parseVo, PmApprove approve) throws ApplicationException {
        ApplyMatch match = parseVo.getMatch();
        if (Boolean.TRUE.equals(parseVo.getCreditFlg())) {
            // 在赊销预算发起的时候做拦截：如果同一个供应商前面的采购合同还没发货，预算不让申请
            budgetVerifyFilter.deliveryWarning(match);

            BsCompany bsCompany = bsCompanyService.getEntity(parseVo.getCompanyId());
            // 判断企业授信额度 更新审批中的授信额度
            // 总额度
            BigDecimal totalCredit = bsCompany.getTotalCreditAmount();

            // 当前在审批中额度
            BigDecimal approveCredit = bsCompany.getApproveCreditAmount();

            // 已使用额度
            BigDecimal usedCredit = bsCompany.getUsedCreditAmount();

            // 本次申请额度
            BigDecimal thisApprove = match.getSellAmount();

            // 当前使用额度
            BigDecimal realApproveCredit = approveCredit.add(thisApprove).add(usedCredit);
            //可使用额度
            BigDecimal subtract = totalCredit.subtract(usedCredit).subtract(approveCredit);
            // 如果审批中额度加本次申请额度加上已使用额度大于总额度 无法发起
            if (realApproveCredit.compareTo(totalCredit) > 0) {
                throw new ApplicationException("申请中的赊销金额" + thisApprove + "大于剩余可使用授信额度" + subtract + "无法发起");
            }

            companyCreditFlowService.updateApproveCreditAmount(approve.getApproveNo(), bsCompany, thisApprove, CreditFlowEnum.AA);
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchCreditParseVo {
        private Boolean creditFlg = Boolean.FALSE;
        private Boolean creditFlgBuy = Boolean.FALSE;
        private Long companyId;
        private ApplyMatch match;
    }
}
