package com.spt.bas.server.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.constant.CreditFlowEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyCtrDcsxClinent;
import com.spt.bas.client.remote.ICtrContractDcsxApplyClient;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.ApplyReceiveAmountSumVo;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyPayDao;
import com.spt.bas.server.dao.ApplyReceiveDao;
import com.spt.bas.server.dao.CtrContractApplyDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 收货款
 *
 * @author old
 */
@Component("applyReceiveService")
@Transactional(readOnly = true)
public class ApplyReceiveServiceImpl extends BaseService<ApplyReceive> implements IApplyReceiveService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private ICtrContractSettlementAmountService settlementAmountService;
    @Autowired
    private ApplyReceiveDao applyReceiveDao;
    @Autowired
    private ICtrContractUpdateService ctrContractUpdateService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private ICtrContractApplyService contractApplyService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrContractApplyDao ctrContractApplyDao;
    @Autowired
    private IBsCompanyCreditFlowService companyCreditFlowService;
    @Autowired
    private IPmProcessClient processClient;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveClient approveClient;
    @Autowired
    private IApplyCtrDcsxClinent applyCtrDcsxClinent;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private ApplyPayDao applyPayDao;
    @Autowired
    private PmProcessDao processDao;


    @Override
    public BaseDao<ApplyReceive> getBaseDao() {
        return applyReceiveDao;
    }

    @Override
    public Class<ApplyReceive> getEntityClazz() {
        return ApplyReceive.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyReceiveDao.updateFileId(id, fileId);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyReceive targetReceive = this.getEntity(approve.getBizId());
            List<ApplyReceive> receiveDetailAllList = targetReceive.getReceiveDetailList();
            for (ApplyReceive receive : receiveDetailAllList) {
                Long contractId = receive.getContractId();
                CtrContract contract = ctrContractService.getEntity(contractId);
                // 更新企业已使用授信额度
                if (Boolean.TRUE.equals(contract.getMatchCreditFlg()) && !StringUtils.equals(BasConstants.PAY_TYPE_W, receive.getReceiveType())
                        && !StringUtils.equals(BasConstants.PAY_TYPE_T, receive.getReceiveType())) {
                    BigDecimal flowAmount;
                    BigDecimal receiveAmount = receive.getReceiveAmount();
                    BigDecimal realNeedReceive = contract.getTotalAmount().subtract(contract.getDealedAmount());
                    flowAmount = (realNeedReceive.compareTo(receiveAmount) >= 0) ? receiveAmount : realNeedReceive;
                    companyCreditFlowService.updateUsedCreditAmount(approve, contract.getCompanyCreditId(), flowAmount.negate(), CreditFlowEnum.UR);
                }

                // 更新合同结算金额表
                settlementAmountService.initSaveBreachSettlementAmount(receive, false);

                // 更新收款金额
                ctrContractUpdateService.addReceiveAmount(receive, approve.getApproveNo(), true);

                //更新合同收款时间
                CtrContractApply contractApply = contractApplyService.findByContractId(contractId);
                Date realPayDate = contractApply.getRealPayDate();
                Date receiveDate = receive.getReceiveDate();
                if ((realPayDate == null || receiveDate.after(realPayDate)) && !StringUtils.equals(BasConstants.PAY_TYPE_T, receive.getReceiveType())) {
                    contractApply.setRealPayDate(receiveDate);
                    contractApplyService.save(contractApply);
                }
                BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.AUTO_APPLY_PAY_SWITCH, BasConstants.SWITCH);
                if (bsDictData != null && StringUtils.equals("1", bsDictData.getDictName())) {
                    //回款完成时， 针对新链条，我方为上光，自动发起SPTX的代采赊销业务付款
                    this.addAutoApplyPay(contractId);
                }
                
                // 鸿博业务 
                CtrContract ctrContract = ctrContractDao.findOne(contractId);
                BigDecimal dealedAmount = ctrContract.getDealedAmount();
                if (Objects.isNull(dealedAmount)) {
                    dealedAmount = BigDecimal.ZERO;
                }
                BigDecimal totalAmount = ctrContract.getTotalAmount();
                if (dealedAmount.compareTo(totalAmount) >= 0) {
                    // 合同已收齐款 自动发起中游的付款
                    autoApplyDcsxPay(ctrContract);
                }
                
                
            }
        }
    }


    /**
     * 自动发起中游的付款
     */
    public void autoApplyDcsxPay(CtrContract contract) {
        logger.info("autoInitiatedCompleteDcsxPay contract:{}", contract.getContractNo());
        SCHEDULED_POOL.schedule(() -> {
            ApplyCtrDCSX applyCtrDCSX = applyDcsxService.findByDCSXApproveId(contract.getApproveId());
            autoApplyDcsxPay(applyCtrDCSX);
        }, 5, TimeUnit.SECONDS);
    }
    
    /**
     * 定时任务自动发起中游的付款
     */
    @Override
    @ServerTransactional
    public void autoApplyDcsxPayScheduled() {
        List<ApplyCtrDCSX> applyCtrDCSXList = applyDcsxService.findHb60DayNotApplyList(BasConstants.COMPANY_NAME_YYHB);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(applyCtrDCSXList)) {
            for (ApplyCtrDCSX applyCtrDCSX : applyCtrDCSXList) {
                this.autoApplyDcsxPay(applyCtrDCSX);
            }
        }
    }
    
    @Override
    public void autoApplyDcsxPay(ApplyCtrDCSX applyCtrDCSX) {
        try {
            if (Objects.isNull(applyCtrDCSX) || ( !StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, applyCtrDCSX.getCompanyName())
                    && !StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, applyCtrDCSX.getOurCompanyName()) )) {
                return;
            }

            // 判断是否可以发起中游付款申请
            BigDecimal totalAmount = applyCtrDCSX.getTotalAmount();
            List<ApplyPay> dcsxPayList = applyPayDao.findByContractNo(applyCtrDCSX.getContractNo());
            BigDecimal applyPayAmount = dcsxPayList.stream()
                    .filter(d -> (StringUtils.equals(BasConstants.APPROVE_STATUS_A, d.getStatus())
                            || StringUtils.equals(BasConstants.APPROVE_STATUS_D, d.getStatus())))
                    .map(ApplyPay::getPayAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            String payType = "A";
            if (applyPayAmount.compareTo(BigDecimal.ZERO)  != 0) {
                payType = "R";
            }

            BigDecimal targetPayAmount = totalAmount.subtract(applyPayAmount);


            // 已发起
            if (targetPayAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            PmApproveSaveVo saveVo = buildFLKDcsxPayVo(applyCtrDCSX, targetPayAmount, payType);
            pmApproveService.startFlow(saveVo);
        } catch (Exception e) {
            logger.error("autoInitiatedCompleteDcsxPay error!", e);
        }
    }

    private PmApproveSaveVo buildFLKDcsxPayVo(ApplyCtrDCSX entity, BigDecimal payAmount, String payType){
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_CODE_DCSX_PAY, entity.getEnterpriseId());

        ApplyPay pay = new ApplyPay();
        pay.setId(0L);
        pay.setContractId(entity.getId());
        pay.setContractNo(entity.getContractNo());
        pay.setPayDate(new Date());
        pay.setTotalAmount(entity.getTotalAmount());
        pay.setPayedAmount(entity.getDealedAmount());
        pay.setUnpayedAmount(entity.getTotalAmount().subtract(entity.getDealedAmount()));
        pay.setPayType(payType);
        pay.setPayAmount(payAmount);
        pay.setCompanyId(entity.getCompanyId());
        pay.setCompanyName(entity.getCompanyName());
        BsCompanyDcsx companyConfig = bsCompanyDcsxService.findByCompanyName(entity.getCompanyName());
        if (Objects.nonNull(companyConfig)) {
            pay.setBankName(companyConfig.getCompanyBankName());
            pay.setBankAccount(companyConfig.getCompanyCardId());
        }
        pay.setPayMode("T");
        pay.setStatus("A");
        pay.setEnterpriseId(entity.getEnterpriseId());
        pay.setOurCompanyName(entity.getOurCompanyName());
        pay.setDeptId(entity.getDeptId());
        pay.setAllSealFlg(true);

        // 设置自动发起并完成审批单
        String entityJson = JsonUtil.obj2Json(pay);
        startVo.setAutoStartMessage("下游收全款完成，自动发起代采赊销付款申请");
        startVo.setBizEntityJson(entityJson);
        startVo.setProcessId(process.getId());
        startVo.setDeptId(entity.getDeptId());
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setApproveId(0L);
        startVo.setUserId(entity.getMatchUserId());
        startVo.setUserName(entity.getMatchUserName());
        startVo.setEnterpriseId(entity.getEnterpriseId());
        startVo.setAutoStartFlgReal(true);
        return startVo;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        //作废收款单
        ApplyReceive targetReceive = this.getEntity(vo.getBizId());
        List<ApplyReceive> receiveDetailAllList = targetReceive.getReceiveDetailList();
        for (ApplyReceive receive : receiveDetailAllList) {
            PmApprove approve = pmApproveService.getEntity(receive.getApproveId());
            Long contractId = receive.getContractId();
            CtrContract contract = ctrContractService.getEntity(contractId);
            if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, receive.getStatus())) {
                // 更新企业已使用授信额度
                if (Boolean.TRUE.equals(contract.getMatchCreditFlg()) && !StringUtils.equals(BasConstants.PAY_TYPE_W, receive.getReceiveType())
                        && !StringUtils.equals(BasConstants.PAY_TYPE_T, receive.getReceiveType())) {
                    BigDecimal receiveAmount = receive.getReceiveAmount();
                    BigDecimal realNeedReceive = contract.getDealedAmount();
                    BigDecimal flowAmount = (realNeedReceive.compareTo(receiveAmount) >= 0) ? receiveAmount : realNeedReceive;
                    companyCreditFlowService.updateUsedCreditAmount(approve, contract.getCompanyCreditId(), flowAmount, CreditFlowEnum.UC);
                }
                ctrContractUpdateService.addReceiveAmount(receive, approve.getApproveNo(), false);
            }
        }
        rollbackContractApply(targetReceive);
    }

    private void rollbackContractApply(ApplyReceive targetEntity) throws ApplicationException {
        List<ApplyReceive> receiveDetailList = targetEntity.getReceiveDetailList();
        for (ApplyReceive entity : receiveDetailList) {
            //更新CtrContractApply中数据
            CtrContractApplyVo vo = new CtrContractApplyVo();
            vo.setContractId(entity.getContractId());
            vo.setDealAmount(entity.getReceiveAmount().negate());
            vo.setApplyType(BasConstants.APPLY_TYPE_E);
            String status = entity.getStatus();
            if (StringUtils.equals(BasConstants.APPROVE_STATUS_C, status)) {
                Date findLastPay = applyReceiveDao.findLastPay(entity.getContractId());
                vo.setRealDate(findLastPay);
            }
            if (!StringUtils.equals(BasConstants.PAY_TYPE_T, entity.getReceiveType())){
                contractApplyService.updateCtrContractApply(vo);
            }

            // 更新合同结算金额表
            settlementAmountService.initSaveBreachSettlementAmount(entity, true);
        }
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyReceive entity = (ApplyReceive) pmEntity;
            List<ApplyReceive> receiveDetailList = parseReceiveDetail(entity);
            if (entity.getId() != 0) {
                parseUpdateEntity(entity);
            }
            entity = this.save(entity);
            entity.setReceiveDetailList(receiveDetailList);
            saveReceiveDetailList(entity, receiveDetailList);
            return entity;
        }
        return null;
    }

    @Override
    public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
        try {
            ApplyReceive entity = (ApplyReceive) pmEntity;
            CtrContract contract = ctrContractDao.findOne(entity.getReceiveDetailList().get(0).getContractId());
            return BasBusinessUtil.buildConditionDefaultMap(contract);
        } catch (Exception e) {
            logger.error("buildConditionDefaultMap error", e);
        }
        return new HashMap<>();
    }

    /**
     * 处理更改审批中的付款金额，ctrContractApply中的申请中金额未更新问题
     * @param targetEntity
     */
    private void parseUpdateEntity(ApplyReceive targetEntity) {
        try {
            ApplyReceive receive = this.getEntity(targetEntity.getId());
            List<ApplyReceive> receiveDetailAllList = receive.getReceiveDetailList();
            for (ApplyReceive entity : receiveDetailAllList) {
                if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, receive.getStatus())) {
                    BigDecimal payedAmountHis = receive.getReceiveAmount();
                    BigDecimal payedAmount = entity.getReceiveAmount();
                    if (payedAmountHis.compareTo(payedAmount) != 0) {
                        CtrContractApply contractApply = contractApplyService.findByContractId(receive.getContractId());
                        if (Objects.nonNull(contractApply)) {
                            BigDecimal applyPayAmount = Objects.isNull(contractApply.getApplyPayAmount()) ? BigDecimal.ZERO : contractApply.getApplyPayAmount();
                            BigDecimal realApplyPayAmount = applyPayAmount.add(payedAmount.subtract(payedAmountHis));
                            contractApply.setApplyPayAmount(realApplyPayAmount);
                            logger.info("parseUpdateEntity contractNo:{} applyPayAmount:{}, realApprovePayAmount:{}", entity.getContractNo(), applyPayAmount, realApplyPayAmount);
                            contractApplyService.save(contractApply);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parseUpdateEntity error:contractNo:{}", targetEntity.getContractNo());
        }
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        List<ApplyReceive> receiveList = applyReceiveDao.findApplyReceiveDetail(approve.getBizId());
        for (ApplyReceive entity : receiveList) {
            CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());
            CtrContract ctrContract = ctrContractDao.findOne(entity.getContractId());
            BigDecimal breachAmount = ctrContract.getBreachAmount();
            BigDecimal discountChargeAmount = ctrContract.getDiscountChargeAmount();
            String contractNo = ctrContract.getContractNo();
            BigDecimal amount = entity.getReceiveAmount().add(contractApply.getApplyPayAmount().subtract(ctrContract.getRefundAmount()));
            BigDecimal subtract = ctrContract.getTotalAmount().add(discountChargeAmount).add(breachAmount).subtract(amount);
            BigDecimal sperAmount = ctrContract.getTotalAmount().add(breachAmount).subtract(contractApply.getApplyPayAmount().subtract(ctrContract.getRefundAmount()));
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,ctrContract.getBusinessType())) {
                BigDecimal tpInterest = ctrContract.getTpInterest();
                subtract = ctrContract.getTotalAmount().add(tpInterest).subtract(amount);
                sperAmount = ctrContract.getTotalAmount().add(tpInterest).subtract(contractApply.getApplyPayAmount().subtract(ctrContract.getRefundAmount()));
            }
            if (!StringUtils.equals("H", entity.getReceiveMode()) && entity.getReceiveAmount().add(entity.getDiscountAmount()).compareTo(BigDecimal.ZERO) == 0) {
                throw new ApplicationException(contractNo + "金额必须大于0!");
            }
            if (!StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,ctrContract.getBusinessType())){
                if (!StringUtils.equals("M", entity.getReceiveType()) && subtract.compareTo(BigDecimal.ZERO) < 0) {
                    throw new ApplicationException(contractNo + "收款金额有误,剩余可收款金额为：" + sperAmount);
                }
            }
            if (Objects.isNull(approve.getContractId())){
                approve.setContractId(entity.getContractId());
                pmApproveService.save(approve);
            }
            if (!StringUtils.equals(BasConstants.PAY_TYPE_T, entity.getReceiveType())){
                CtrContractApplyVo vo = new CtrContractApplyVo();
                vo.setContractId(entity.getContractId());
                vo.setDealAmount(entity.getReceiveAmount());
                vo.setApplyType(BasConstants.APPLY_TYPE_E);
                contractApplyService.updateCtrContractApply(vo);
            }
            // 更新合同历史状态表
            contractOphisService.addHis(BasConstants.APPLY_TYPE_E, entity.getContractId(), approve, entity.getReceiveDate());
        }
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyReceive pay = (ApplyReceive) pmEntity;
            StringBuilder subjectStr = new StringBuilder();
            List<ApplyReceive> applyReceiveDetail = applyReceiveDao.findApplyReceiveDetail(pmEntity.getId());
            for (ApplyReceive entity : applyReceiveDetail) {
                String contractNo = entity.getContractNo();
                String companyName = entity.getCompanyName();
                String receiveType = entity.getReceiveType();
                String companyName1 = RuleUtil.companyNameSubString(companyName);
                String companyName2 = RuleUtil.companyNameSubString(entity.getOurCompanyName());
                String title = BsDictUtil.getValue(entity.getEnterpriseId(), BsDictConstants.DICT_TYPE_RECEIVETYPE, receiveType);
                String company="";
                if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)){
                    company=companyName1+"-"+companyName2;
                }

                String businessTypeStr = "代采赊销";
                CtrContract contract = ctrContractService.getEntity(entity.getContractId());
                String businessType = contract.getBusinessType();
                String businessTypeDcsx = contract.getBusinessTypeDcsx();
                Boolean matchCreditFlg = contract.getMatchCreditFlg();
                List<String> list = new ArrayList<>();
                list.add("PT");
                list.add("HDFK");
                list.add("BL");
                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType)) {
                    businessTypeStr = "代采托盘";
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.TRUE.equals(matchCreditFlg)) {
                    if (StringUtils.isNotBlank(businessTypeDcsx) && list.contains(businessTypeDcsx)) {
                        businessTypeStr = "赊销";
                    }
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.FALSE.equals(matchCreditFlg)) {
                    businessTypeStr = "代采";
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_CG, businessType)) {
                    businessTypeStr = "自营";
                }
                // 收款方式
                String receiveMode = entity.getReceiveMode();
                String receiveModeName = BsDictUtil.getValue(entity.getEnterpriseId(), BasConstants.DICT_MODE_APPLYRECEIVE, receiveMode);
                subjectStr.append(SubjectUtil.formatSubject(contractNo, businessTypeStr, company, SubjectUtil.formatMoney(pay.getReceiveAmount(), RuleUtil.monetaryUnit), title, receiveModeName));
            }
            return subjectStr.toString();
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyReceive entity = this.getEntity(approve.getBizId());
        //更新CtrContractApply中数据
        rollbackContractApply(entity);
    }

    @Override
    public ApplyReceive getEntity(Long id){
        ApplyReceive entity = applyReceiveDao.findOne(id);
        if (Objects.nonNull(entity)){
            List<ApplyReceive> applyReceiveDetail = (StringUtils.equals(BasConstants.APPROVE_STATUS_C, entity.getStatus())
                    || StringUtils.equals(BasConstants.APPROVE_STATUS_E, entity.getStatus())
                    || StringUtils.equals(BasConstants.APPROVE_STATUS_B, entity.getStatus()))
                    ? applyReceiveDao.findApplyReceiveDetailAll(id)
                    : applyReceiveDao.findApplyReceiveDetail(id);
            if (CollectionUtils.isEmpty(applyReceiveDetail)){
                applyReceiveDetail = new ArrayList<>();
                ApplyReceive childrenReceive = new ApplyReceive();
                BeanUtils.copyProperties(entity, childrenReceive);
                applyReceiveDetail.add(childrenReceive);
            }
            entity.setReceiveDetailList(applyReceiveDetail);
        }
        return entity;
    }

    public void saveReceiveDetailList(ApplyReceive parentReceive, List<ApplyReceive> childrenList){
        if (CollectionUtils.isNotEmpty(childrenList) && parentReceive.getApproveId() > 0){
            childrenList.forEach(d-> d.setParentReceiveId(parentReceive.getId()));
            applyReceiveDao.saveAll(childrenList);
        }
    }

    public List<ApplyReceive> parseReceiveDetail(ApplyReceive parentReceive){
        List<ApplyReceive> childrenReceive = parentReceive.getReceiveDetailList();
        if (CollectionUtils.isEmpty(childrenReceive)){
            return null;
        }
        List<ApplyReceive> resultList = new ArrayList<>();
        for (ApplyReceive detail : childrenReceive) {
            ApplyReceive entity = new ApplyReceive();
            BeanUtils.copyProperties(parentReceive, entity);
            CtrContract ctr = ctrContractService.getEntity(detail.getContractId());
            if (StringUtils.equals(ctr.getBusinessTypeDcsx(), BasConstants.BUSINESS_TYPE_DCSX)) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_DCSX);
            }
            entity.setId(detail.getId());
            entity.setContractId(detail.getContractId());
            entity.setContractNo(ctr.getContractNo());
            entity.setBusinessNo(ctr.getContractNo());
            entity.setTotalAmount(ctr.getTotalAmount());
            entity.setPayedAmount(ctr.getDealedAmount());
            entity.setUnDiscountAmount(ctr.getDiscountChargeAmount().subtract(ctr.getDiscountReceiveAmount()));
            entity.setUnpayedAmount(ctr.getTotalAmount().add(ctr.getTpInterest()).subtract(ctr.getApproveTpInterest()).subtract(ctr.getDealedAmount()));
            entity.setReceiveAmount(detail.getReceiveAmount());
            entity.setBreachAmount(ctr.getBreachAmount());
            entity.setReceiveBreachAmount(ctr.getReceiveBreachAmount());
            entity.setTpInterest(ctr.getTpInterest());
            entity.setApproveTpInterest(ctr.getApproveTpInterest());
            entity.setApplyNo(composeContractNo(detail.getContractNo()));
            entity.setParentReceiveId(parentReceive.getId());
            entity.setApproveId(parentReceive.getApproveId());
            entity.setFileId(parentReceive.getFileId());
            entity.setReceiveAmount(detail.getReceiveAmount());
            entity.setDiscountAmount(detail.getDiscountAmount());
            entity.setDiscountTarget(parentReceive.getDiscountTarget());
            entity.setDiscountRate(parentReceive.getDiscountRate());
            entity.setBillDueTime(parentReceive.getBillDueTime());
            entity.setDueTime(parentReceive.getDueTime());
            resultList.add(entity);
        }
        return resultList;
    }

    @Override
    public List<ApplyReceive> findByContractId(Long contractId) {
        return applyReceiveDao.findByContractId(contractId);
    }

    @Override
    public List<ApplyReceive> findListByContractIdAndStatus(Long contractId, String status) {
        return applyReceiveDao.findListByContractIdAndStatus(contractId, status);
    }

    @Override
    public ApplyReceiveAmountSumVo findReceiveAmountSum(Long contractId) {
        ApplyReceiveAmountSumVo receiveAmountSumVo = new ApplyReceiveAmountSumVo();
        Map<String, Object> receiveAmountMap = applyReceiveDao.findReceiveAmountSum(contractId);
        if(receiveAmountMap != null){
            if(receiveAmountMap.get("receiveAmount") != null){
                BigDecimal receiveAmount = (BigDecimal) receiveAmountMap.get("receiveAmount");
                receiveAmountSumVo.setReceiveAmount(receiveAmount);
            }
            if(receiveAmountMap.get("receiveDate") != null){
                Date receiveDate = (Date) receiveAmountMap.get("receiveDate");
                receiveAmountSumVo.setReceiveDate(receiveDate);
            }
            if(receiveAmountMap.get("num") != null && receiveAmountMap.get("num") instanceof BigInteger){
                BigInteger countBigInteger = (BigInteger) receiveAmountMap.get("num");
                Integer count = countBigInteger.intValue();
                receiveAmountSumVo.setCount(count);
            }
        }
        return receiveAmountSumVo;
    } 
    
    @Override
    public ApplyReceiveAmountSumVo findReceiveAmountSumByContractNo(String contractNo) {
        ApplyReceiveAmountSumVo receiveAmountSumVo = new ApplyReceiveAmountSumVo();
        Map<String, Object> receiveAmountMap = applyReceiveDao.findReceiveAmountSumByContractNo(contractNo);
        if(receiveAmountMap != null){
            if(receiveAmountMap.get("receiveAmount") != null){
                BigDecimal receiveAmount = (BigDecimal) receiveAmountMap.get("receiveAmount");
                receiveAmountSumVo.setReceiveAmount(receiveAmount);
            }
            if(receiveAmountMap.get("receiveDate") != null){
                Date receiveDate = (Date) receiveAmountMap.get("receiveDate");
                receiveAmountSumVo.setReceiveDate(receiveDate);
            }
            if(receiveAmountMap.get("num") != null && receiveAmountMap.get("num") instanceof BigInteger){
                BigInteger countBigInteger = (BigInteger) receiveAmountMap.get("num");
                Integer count = countBigInteger.intValue();
                receiveAmountSumVo.setCount(count);
            }
        }
        return receiveAmountSumVo;
    }

    @Override
    public void updateApplyStatus(Long contractId) {
        applyReceiveDao.updateApplyStatus(contractId);
    }

    @Override
    public ApplyReceive findPageSum(PageSearchVo searchVo) {
        Specification<ApplyReceive> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery();
        Root<ApplyReceive> root = query.from(ApplyReceive.class);
        CriteriaQuery<?> cq = query.where(spe.toPredicate(root, query, cb)).multiselect(cb.sum(root.get("receiveAmount")));
        TypedQuery<?> tq = em.createQuery(cq);
        Object result = tq.getSingleResult();
        ApplyReceive sum = new ApplyReceive();
        BigDecimal receiveAmount = (BigDecimal) result;
        sum.setReceiveAmount(receiveAmount);
        return sum;
    }

    private String composeContractNo(String contractNo) {
        List<ApplyReceive> deliveryIn = applyReceiveDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", deliveryIn.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_E + fmt;
    }

    private void addAutoApplyPay(Long contractId) {
        try {
            CtrContract contract = ctrContractDao.findOne(contractId);
            // 判断我方是上光
            List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.AUTO_APPLY_PAY_OUR_COMPANY);
            List<String> ourCompanyName = listByCategory.stream().map(BsDictData::getDictName).collect(Collectors.toList());
            if (ourCompanyName.contains(contract.getOurCompanyName())) {
                // 判断合同是否已经收款，已收金额>=合同总价
                BigDecimal totalAmount = contract.getTotalAmount();
                BigDecimal dealedAmount = contract.getDealedAmount() == null ? BigDecimal.ZERO : contract.getDealedAmount();
                if(dealedAmount.compareTo(totalAmount)>=0){
                    /**
                     * 根据采购合同的定金，来判断是发起全款，还是定金及尾款
                     * 如果有定金，要发起定金、及尾款两个自动付款流程
                     */
                    List<CtrContract> ctrContractList = ctrContractDao.findByApproveId(contract.getApproveId());
                    Optional<CtrContract> findContractB = ctrContractList.stream().filter(it -> it.getContractType().equals("B") && !it.getSpecialChainFlag()).findFirst();
                    // 判断采购合同是不有定金
                    CtrContract ctrContractB = findContractB.get();
                    boolean bondFlg=false;
                    BigDecimal bondRate= BigDecimal.ZERO;
                    if(ctrContractB.getBondAmount().compareTo(BigDecimal.ZERO)>0){
                        bondFlg = true;
                        bondRate=ctrContractB.getBondAmount().divide(ctrContractB.getTotalAmount(),4, RoundingMode.HALF_UP);
                    }
                    ApplyCtrDCSX ctrDcsx = applyCtrDcsxClinent.findByDCSXApproveId(contract.getApproveId());
                    if(bondFlg){
                        ApplyPay sptxApplyPayB = this.buildApplyDcsxPay(ctrDcsx,"B",bondRate);
                        ApplyPay sptxApplyPayR = this.buildApplyDcsxPay(ctrDcsx,"R",bondRate);
                        startFlow(JsonUtil.obj2Json(sptxApplyPayB), BasConstants.PROCESS_CODE_DCSX_PAY, ctrDcsx.getMatchUserId());
                        startFlow(JsonUtil.obj2Json(sptxApplyPayR), BasConstants.PROCESS_CODE_DCSX_PAY, ctrDcsx.getMatchUserId());
                    } else {
                        ApplyPay sptxApplyPay = this.buildApplyDcsxPay(ctrDcsx,"A",BigDecimal.ZERO);
                        startFlow(JsonUtil.obj2Json(sptxApplyPay), BasConstants.PROCESS_CODE_DCSX_PAY, ctrDcsx.getMatchUserId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("sptx 自动化发起业务付款申请异常 error");
        }
    }

    private ApplyPay buildApplyDcsxPay(ApplyCtrDCSX contract,String payType,BigDecimal bondRate) {
        ApplyPay applyPay = new ApplyPay();
        applyPay.setContractId(contract.getId());
        applyPay.setContractNo(contract.getContractNo());
        applyPay.setTotalAmount(contract.getTotalAmount());
        BigDecimal dealedAmount = contract.getDealedAmount() == null ? contract.getDealedAmount() : BigDecimal.ZERO;
        applyPay.setPayedAmount(dealedAmount);
        applyPay.setOurCompanyName(contract.getOurCompanyName());
        applyPay.setCompanyName(contract.getCompanyName());
        applyPay.setBankName(contract.getBankName());
        applyPay.setBankAccount(contract.getBankAccount());
        switch (payType){
            case "A":
                // 全款
                applyPay.setPayAmount(contract.getTotalAmount());
                break;
            case "B":
                // 定金
                BigDecimal bondAmount = contract.getTotalAmount().multiply(bondRate).setScale(2, RoundingMode.HALF_UP);
                applyPay.setPayAmount(bondAmount);
                break;
            case "R":
                // 尾款
                BigDecimal bondAmount1 = contract.getTotalAmount().multiply(bondRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal finalAmount  = contract.getTotalAmount().subtract(bondAmount1);
                applyPay.setPayAmount(finalAmount);
                break;
        }
        applyPay.setPayType(payType);
        // 判断我方是苏高新，收款方是青光、网速，付款类型为账户余额
        if(contract.getOurCompanyName().equals(BasConstants.COMPANY_NAME_SUGX)){
            if(contract.getCompanyName().equals(BasConstants.COMPANY_NAME_QDZG)||contract.getCompanyName().equals(BasConstants.COMPANY_NAME_WSNB)){
                applyPay.setPayMode("F");
            } else {
                applyPay.setPayMode("T");
            }
        } else {
            applyPay.setPayMode("T");
        }
        applyPay.setPayDate(new Date());
        applyPay.setDeptId(contract.getDeptId());
        applyPay.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        return applyPay;
    }

    /**
     * 发起审批
     */
    private void startFlow(String bizEntityJson, String processCode, Long applyUserId) throws ApplicationException, WebApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(processCode);
        PmProcess process = processClient.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
        SysUserSdk userById = authOpenFacade.findUserById(applyUserId);
        startVo.setUserId(userById.getUserId());
        startVo.setDeptId(userById.getDeptId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        startVo.setAutoStartFlg(true);
        approveClient.startFlow(startVo);
    }

}
