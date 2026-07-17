package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyCtrDcsxClinent;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyReceiveDao;
import com.spt.bas.server.dao.CtrContractDcsxApplyDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
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
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 代采赊销收货款
 *
 * @author old
 */
@Component("applyDcsxReceiveService")
@Transactional(readOnly = true)
public class ApplyDcsxReceiveServiceImpl extends BaseService<ApplyReceive>
        implements IApplyDcsxReceiveService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyReceiveDao applyReceiveDao;
    @Autowired
    private IApplyCtrDcsxClinent ctrDcsxClinent;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private CtrContractDcsxApplyDao ctrContractDcsxApplyDao;
    @Autowired
    private ICtrContractDcsxApplyService ctrContractDcsxApplyService;
    @Autowired
    private ICtrContractRelaService ctrContractRelaService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IApplyDcsxService applyDcsxService;

    @Override
    public BaseDao<ApplyReceive> getBaseDao() {
        return applyReceiveDao;
    }

    @Override
    public Class<ApplyReceive> getEntityClazz() {
        return ApplyReceive.class;
    }


    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyReceive receive = applyReceiveDao.findOne(approve.getBizId());
            Long contractId = receive.getContractId();
            ApplyCtrDCSX contract = applyDcsxService.getEntity(contractId);
            if (contract.getApplyCancelFlg()) {
                throw new ApplicationException("请驳回，该合同处于合同作废阶段!");
            }
            contract.setReceiveAmount(contract.getReceiveAmount().add(receive.getReceiveAmount()));
            if (contract.getReceiveAmount().compareTo( contract.getTotalAmount())>=0){
                contract.setReceiveDate(receive.getReceiveDate());
            }
            applyDcsxService.save(contract);

            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    // 添加合同操作记录
                    if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                        contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_R,BasConstants.APPROVE_STATUS_D, contractId, approve, receive.getReceiveDate());
                    } else {
                        contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_R,BasConstants.APPROVE_STATUS_D, contractId, approve, receive.getReceiveDate());
                    }
                }
            }
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        //作废收款单
        ApplyReceive receive = applyReceiveDao.findOne(vo.getBizId());
        PmApprove approve = pmApproveService.getEntity(receive.getApproveId());
        Long contractId = receive.getContractId();
        BigDecimal receiveAmount = receive.getReceiveAmount();
//        CtrContractOphisRequest request = new CtrContractOphisRequest();
//        request.setApplyType(BasConstants.APPLY_TYPE_E);
//        request.setCancel(true);
//        request.setCtrContractId(contractId);
//        request.setRemark(approve.getSubject());
//        request.setCreateUserId(vo.getUserId());
//        request.setCreateUserName(vo.getUserName());
//        request.setApproveId(vo.getApproveId());
//        request.setContractGroup("DCSX");
//        contractOphisService.addHis(request);

        ApplyCtrDCSX entity = applyDcsxService.getEntity(contractId);
        entity.setReceiveAmount(entity.getReceiveAmount().add(receiveAmount.negate()));
        applyDcsxService.save(entity);

        //更新ctrContractDcsxApplyService中数据
        CtrContractDcsxApply contractApply = ctrContractDcsxApplyDao.findByCtrContractId(receive.getContractId());
        contractApply.setApplyReceiveAmount(contractApply.getApplyReceiveAmount().subtract(receive.getReceiveAmount()));
        ctrContractDcsxApplyDao.save(contractApply);
    }


    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyReceive entity = (ApplyReceive) pmEntity;
            ApplyCtrDCSX ctr= ctrDcsxClinent.getEntity(entity.getContractId());
            entity.setBusinessType(BasConstants.BUSINESS_TYPE_DCSX);
            BigDecimal interestAmount = ctr.getInterestAmount();
            interestAmount = interestAmount == null ? BigDecimal.ZERO : interestAmount;
            BigDecimal totalAmount = ctr.getTotalAmount();
            BigDecimal settlementTotalAmount = ctr.getSettlementTotalAmount();
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctr.getBusinessType()) &&
                    settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
                totalAmount = settlementTotalAmount;
            }
            BigDecimal unPayedAmount = totalAmount.subtract(ctr.getDealedAmount().subtract(ctr.getRefundAmount())).add(interestAmount);
            entity.setUnpayedAmount(unPayedAmount);
            if (entity.getId() == 0) {
                //生成合同号
                String applyNo = composeContractNo(entity.getContractNo());
                entity.setApplyNo(applyNo);
            }
            String receiveMode = entity.getReceiveMode();
            String payType = ctr.getPayType();
            if (StringUtils.equals(receiveMode, payType)) {
                entity.setRiskApproveFlg(false);
            } else {
                entity.setRiskApproveFlg(true);
            }
            return save(entity);
        }
        return null;
    }





    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyReceive entity = applyReceiveDao.findOne(approve.getBizId());
        ApplyCtrDCSX ctrContract = applyDcsxService.findById(entity.getContractId());
        CtrContractDcsxApply contractApply = ctrContractDcsxApplyDao.findByCtrContractId(entity.getContractId());
        if(contractApply!=null){
            BigDecimal breachAmount = ctrContract.getBreachAmount();
            //现在总付
            BigDecimal amount = entity.getReceiveAmount().add(contractApply.getApplyReceiveAmount().subtract(ctrContract.getRefundAmount()));
            BigDecimal totalAmount = ctrContract.getTotalAmount();
            BigDecimal settlementTotalAmount = ctrContract.getSettlementTotalAmount();
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrContract.getBusinessType()) &&
                    settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
                totalAmount = settlementTotalAmount;
            }
            //总金额-现在总付款=剩下
            BigDecimal subtract = totalAmount.add(breachAmount).subtract(amount);
            BigDecimal sperAmount = totalAmount.add(breachAmount).subtract(contractApply.getApplyReceiveAmount().subtract(ctrContract.getRefundAmount()));
            if (entity.getReceiveAmount().compareTo(BigDecimal.ZERO) == 0) {
                throw new ApplicationException("收款金额必须大于0!");
            }
            if (subtract.compareTo(BigDecimal.ZERO)< 0) {
                throw new ApplicationException("收款金额有误,剩余可收款金额为：" + sperAmount);
            }
            CtrContractApplyVo vo = new CtrContractApplyVo();
            vo.setContractId(entity.getContractId());
            vo.setDealAmount(entity.getReceiveAmount());
            vo.setApplyType(BasConstants.APPLY_TYPE_E);
            ctrContractDcsxApplyService.updateCtrContractApply(vo);
        }
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrContract.getBusinessType())) {
            contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_R,BasConstants.APPROVE_STATUS_D, entity.getContractId(), approve, entity.getReceiveDate());
        } else {
            contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_R,BasConstants.APPROVE_STATUS_D, entity.getContractId(), approve, entity.getReceiveDate());
        }
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyReceive pay = (ApplyReceive) pmEntity;
            String contractNo = pay.getContractNo();
            String companyName = pay.getCompanyName();
            BigDecimal sumNumber = BigDecimal.ZERO;
            sumNumber = pay.getReceiveAmount();
            String receiveAmount = NumberUtil.formatNumber(sumNumber, "#.##");
            String receiveType = pay.getReceiveType();
            String companyName1 = RuleUtil.companyNameSubString(companyName);
            String companyName2 = RuleUtil.companyNameSubString(pay.getOurCompanyName());
            String title = BsDictUtil.getValue(pay.getEnterpriseId(), BsDictConstants.DICT_TYPE_RECEIVETYPE, receiveType);
            String company = "";
            if (StringUtils.isNotBlank(companyName1) && StringUtils.isNotBlank(companyName2)) {
                company = companyName1 + "-" + companyName2;
            }
            String subject = SubjectUtil.formatSubject(contractNo, company, SubjectUtil.formatMoney(sumNumber , RuleUtil.monetaryUnit), title);
            return subject;
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyReceive entity = applyReceiveDao.findOne(approve.getBizId());
        //更新ctrContractDcsxApplyService中数据
        CtrContractDcsxApply contractApply = ctrContractDcsxApplyDao.findByCtrContractId(entity.getContractId());
        contractApply.setApplyReceiveAmount(contractApply.getApplyReceiveAmount().subtract(entity.getReceiveAmount()));
        ctrContractDcsxApplyDao.save(contractApply);
    }

    private String composeContractNo(String contractNo) {
        List<ApplyReceive> deliveryIn = applyReceiveDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", deliveryIn.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_E + fmt;
    }

    /**
     * 自动发起收款审批任务
     */
    @Override
    @ServiceTransactional
    public void doApplyDcsxReceiveTask(ApplyCtrDCSX ctrDCSX, ApplyPay pay) {
        try {
            autoApplyReceive(ctrDCSX, pay);
            logger.info("autoApplyReceive 完成contractNo:{} , receiveAmount:{} ", ctrDCSX.getContractNo(), pay.getPayAmount());
        } catch (Exception e) {
            logger.error("autoApplyReceive error,contractNo:{}", ctrDCSX.getContractNo(), e);
        }
    }

    private void autoApplyReceive(ApplyCtrDCSX ctr, ApplyPay pay) throws ApplicationException {
        BigDecimal dealAmount = pay.getPayAmount();
        BigDecimal totalAmount = ctr.getTotalAmount();
        BigDecimal settlementTotalAmount = ctr.getSettlementTotalAmount();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,ctr.getBusinessType()) &&
                settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 若有结算金额
            totalAmount = settlementTotalAmount;
        }

        BigDecimal receiveAmount = ctr.getReceiveAmount();
        if (receiveAmount.add(dealAmount).compareTo(totalAmount) > 0){
            dealAmount = totalAmount.subtract(receiveAmount);
        }
        if (dealAmount.compareTo(BigDecimal.ZERO) <= 0){
            logger.warn("contractNo：{} 已收全款，autoApplyReceive 中止!", ctr.getContractNo());
            return;
        }

        ApplyReceive applyReceive = new ApplyReceive();
        applyReceive.setId(0L);
        applyReceive.setContractId(ctr.getId());
        applyReceive.setContractNo(ctr.getContractNo());
        applyReceive.setBusinessNo(ctr.getContractNo());
        applyReceive.setCompanyName(ctr.getOurCompanyName());
        applyReceive.setOurCompanyName(ctr.getCompanyName());
        applyReceive.setTotalAmount(totalAmount);
        applyReceive.setDeptId(ctr.getDeptId());
        applyReceive.setPayedAmount(BigDecimal.ZERO);
        applyReceive.setReceiveAmount(dealAmount);
        // 未收
        applyReceive.setUnpayedAmount(totalAmount.subtract(ctr.getReceiveAmount()));
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        // 默认全款
        applyReceive.setReceiveType(dealAmount.compareTo(totalAmount) >= 0  ? "A" : "P");
        applyReceive.setReceiveDate(pay.getPayDate());
        startVo.setAutoStartMessage("代采赊销付款完成，自动发起代采赊销收款并完成");
        // 默认 电汇
        applyReceive.setReceiveMode("T");
        applyReceive.setBuyCompanyId(getBuyCompanyIdBySellContractId(ctr.getId()));
        applyReceive.setApproveId(0L);
        applyReceive.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        applyReceive.setStatus(BasConstants.APPROVE_STATUS_D);
        String bizEntityJson = JsonUtil.obj2Json(applyReceive);
        String processCode;
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctr.getBusinessType())) {
            processCode = BasConstants.PROCESS_APPLY_RECEIVE_DCTP;
        } else {
            processCode = BasConstants.PROCESS_APPLY_RECEIVE_DCSX;
        }

        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_D);
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
        startVo.setAutoStartFlgReal(true);
        pmApproveService.startFlow(startVo);
    }

    /**
     * 根据销售合同id获取上游公司id
     *
     * @param contractId
     * @return
     */
    private Long getBuyCompanyIdBySellContractId(Long contractId) {
        Long buyCompanyId = null;
        CtrContractRela rela = ctrContractRelaService.getRelaBySellContractId(contractId);
        if (rela != null) {
            buyCompanyId = rela.getBuyCompanyId();
        }
        return buyCompanyId;
    }

    @Override
    @ServiceTransactional
    public void ApplyDcsxReceiveTask(ApplyCtrDCSX ctr,int i) throws ApplicationException {
        ApplyReceive applyReceive = new ApplyReceive();
        applyReceive.setId(0L);
        applyReceive.setContractId(ctr.getId());
        applyReceive.setContractNo(ctr.getContractNo());
        applyReceive.setBusinessNo(ctr.getContractNo());
//        applyReceive.setCompanyId(ctr.getCompanyId());
        applyReceive.setCompanyName(ctr.getOurCompanyName());
        applyReceive.setOurCompanyName(ctr.getCompanyName());
        BigDecimal totalAmount = ctr.getTotalAmount();
        BigDecimal settlementTotalAmount = ctr.getSettlementTotalAmount();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctr.getBusinessType()) &&
                settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalAmount = settlementTotalAmount;
        }
        applyReceive.setTotalAmount(totalAmount);
        applyReceive.setDeptId(ctr.getDeptId());
        applyReceive.setPayedAmount(BigDecimal.ZERO);
        // 未收
        applyReceive.setUnpayedAmount(applyReceive.getTotalAmount().subtract(applyReceive.getPayedAmount()));
        BigDecimal bondAmount = ctr.getBondAmount();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        String payBondTime=null;
        if(ctr.getPayBondTime()!=null){
            payBondTime= sdf.format(ctr.getPayBondTime());
        }
        if(Objects.nonNull(payBondTime)  && ctr.getBondAmount().compareTo(BigDecimal.ZERO)>0 && i==1){
            // 定金
            applyReceive.setReceiveType("B");
            applyReceive.setReceiveDate(ctr.getPayBondTime());
            applyReceive.setReceiveAmount(bondAmount == null ? BigDecimal.ZERO : bondAmount);
        }else{

            CtrContractDcsxApply contractApply = ctrContractDcsxApplyDao.findByCtrContractId(ctr.getId());
            BigDecimal applyReceiveAmount = contractApply.getApplyReceiveAmount();

            if(applyReceiveAmount.compareTo(totalAmount)==0){
                applyReceive.setReceiveType("A");
            }else{
                applyReceive.setReceiveType("R");
            }
            applyReceive.setReceiveDate(ctr.getPayFullTime());
            BigDecimal subtract = ctr.getTotalAmount().subtract(applyReceiveAmount);
            applyReceive.setReceiveAmount(subtract == null ? BigDecimal.ZERO : subtract);
        }
        // 默认 电汇
        applyReceive.setReceiveMode("T");
        applyReceive.setBuyCompanyId(getBuyCompanyIdBySellContractId(ctr.getId()));
        applyReceive.setApproveId(0L);
        applyReceive.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        applyReceive.setApplySource(ApplySource.WEBSITE.getCode());
        String bizEntityJson = JsonUtil.obj2Json(applyReceive);
        String processCode;
        if (StringUtils.equals(BasConstants.APPLY_SEAL_USAGE_DCTP, ctr.getBusinessType())) {
            processCode = BasConstants.PROCESS_APPLY_RECEIVE_DCTP;
        } else {
            processCode = BasConstants.PROCESS_APPLY_RECEIVE_DCSX;
        }
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
        startVo.setAutoStartMessage("自动发起收款审批任务");
        pmApproveService.startFlow(startVo);
    }

}

