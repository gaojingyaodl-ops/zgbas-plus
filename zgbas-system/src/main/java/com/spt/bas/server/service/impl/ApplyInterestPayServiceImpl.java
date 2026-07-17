package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyInterestPay;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyDcsxDao;
import com.spt.bas.server.dao.ApplyInterestPayDao;
import com.spt.bas.server.dao.BsCompanyDcsxDao;
import com.spt.bas.server.service.IApplyInterestPayService;
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
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 中游付息申请
 */
@Component("applyInterestPayService")
@Transactional(readOnly = true)
public class ApplyInterestPayServiceImpl extends BaseService<ApplyInterestPay> implements IApplyInterestPayService, IPmService, IPmApproveListener {
    @Resource
    private ApplyInterestPayDao applyInterestPayDao;
    @Resource
    private IPmApproveService pmApproveService;
    @Resource
    private PmProcessDao processDao;
    @Resource
    private ApplyDcsxDao applyDcsxDao;
    @Resource
    private BsCompanyDcsxDao bsCompanyDcsxDao;

    @Override
    public BaseDao<ApplyInterestPay> getBaseDao() {
        return applyInterestPayDao;
    }

    @Override
    public Class<ApplyInterestPay> getEntityClazz() {
        return ApplyInterestPay.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyInterestPayDao.updateFileId(id, fileId);
    }

    @Override
    @ServerTransactional
    public RespVo<String> batchPayInterest(ApplyInterestPay entity) {
        RespVo<String> respVo = new RespVo<>();
        String contractIds = entity.getContractIds();
        if (StringUtils.isBlank(contractIds)) {
            respVo.setFail("参数不可为空!");
            return respVo;
        }
        List<String> paramStr = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(contractIds);
        List<Long> contractIdList = paramStr.stream().map(Long::valueOf).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contractIdList)) {
            respVo.setFail("参数不合规!");
            return respVo;
        }
        List<ApplyCtrDCSX> targetList = applyDcsxDao.findByIds(contractIdList);
        if (CollectionUtils.isEmpty(targetList)) {
            respVo.setFail("未查询到符合条件中游合同!");
            return respVo;
        }
        Map<String, List<ApplyCtrDCSX>> companyMap = targetList.stream().collect(Collectors.groupingBy(ApplyCtrDCSX::getCompanyName));
        Map<String, List<ApplyCtrDCSX>> ourCompanyMap = targetList.stream().collect(Collectors.groupingBy(ApplyCtrDCSX::getOurCompanyName));
        if (companyMap.keySet().size() > 1 || ourCompanyMap.keySet().size() > 1) {
            respVo.setFail("付息列表，需保证相同资方公司和相同我方抬头!");
            return respVo;
        }
        ApplyCtrDCSX targetEntity = targetList.get(0);
        // 针对中游合同，判断那个公司名称是我司
        BsCompanyDcsx company = null;
        boolean ourCompanyFlg = false;
        BsCompanyDcsx byCompanyName = bsCompanyDcsxDao.findByCompanyName(targetEntity.getCompanyName());
        if(byCompanyName!=null){
            ourCompanyFlg= byCompanyName.getOurCompanyFlag();
        }
        if(ourCompanyFlg){
            company= bsCompanyDcsxDao.findByCompanyName(targetEntity.getOurCompanyName());
        } else {
            company = byCompanyName;
        }

        if (Objects.isNull(company)) {
            respVo.setFail("未查询出资方信息!");
            return respVo;
        }
        targetList = targetList.stream()
                .filter(t -> t.getOverdueInterest().compareTo(BigDecimal.ZERO) != 0)
                .filter(t -> t.getOverdueInterest().subtract(t.getReceiveOverdueInterest()).compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
        try {
            startInterestPayApply(targetEntity, targetList, entity, company,ourCompanyFlg);

            applyDcsxDao.updateSettlementStatus(contractIdList, "1");
        } catch (Exception e) {
            logger.error("startInterestPayApply error", e);
            respVo.setFail("中游付息申请发起失败!");
            return respVo;
        }
        return respVo;
    }

    /**
     * 发起中游付息申请
     *
     * @param targetEntity
     * @param targetList
     * @param entity
     * @param company
     * @throws ApplicationException
     */
    private void startInterestPayApply(ApplyCtrDCSX targetEntity, List<ApplyCtrDCSX> targetList, ApplyInterestPay entity, BsCompanyDcsx company,boolean ourCompanyFlg) throws ApplicationException {
        BigDecimal payTotalAmount = targetList.stream().map(t -> t.getOverdueInterest().add(t.getAcceptDiscountCost()).add(t.getExtraCost()).subtract(t.getReceiveOverdueInterest())).reduce(BigDecimal.ZERO, BigDecimal::add);
        String contractIds = targetList.stream().map(ApplyCtrDCSX::getId).map(String::valueOf).collect(Collectors.joining(BasConstants.COMMA));
        ApplyInterestPay startEntity = new ApplyInterestPay();
        startEntity.setContractIds(contractIds);
        startEntity.setApplyUserId(entity.getApplyUserId());
        startEntity.setApplyUserName(entity.getApplyUserName());
        startEntity.setReceiveCompanyName(company.getCompanyName());
        startEntity.setReceiveBankName(company.getCompanyBankName());
        startEntity.setReceiveBankAccount(StringUtils.equals("青岛奥顺宇供应链管理有限公司",startEntity.getReceiveCompanyName()) ? "636651034" : company.getCompanyCardId());
        startEntity.setOurCompanyName(ourCompanyFlg?targetEntity.getCompanyName():targetEntity.getOurCompanyName());
        startEntity.setPayAmount(payTotalAmount);
        startEntity.setPayDate(new Date());
        startEntity.setCostMode("服务费");
        startEntity.setEnterpriseId(targetEntity.getEnterpriseId());
        startEntity.setRemark("逾期罚息费用");

        PmProcess interestProcess = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_CODE_INTEREST_PAY, entity.getEnterpriseId());
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        String entityJson = JsonUtil.obj2Json(startEntity);
        startVo.setProcessId(interestProcess.getId());
        startVo.setUserId(entity.getApplyUserId());
        startVo.setUserName(entity.getApplyUserName());
        startVo.setBizEntityJson(entityJson);
        startVo.setMode("A");
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setApproveId(0L);
        startVo.setEnterpriseId(entity.getEnterpriseId());
        startVo.setAutoStartMessage("从风控系统发起中游付息申请!");
        pmApproveService.startFlow(startVo);
    }


    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyInterestPay entity = applyInterestPayDao.findOne(approve.getBizId());
            List<String> paramStr = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(entity.getContractIds());
            List<Long> contractIdList = paramStr.stream().map(Long::valueOf).collect(Collectors.toList());
            List<ApplyCtrDCSX> targetList = applyDcsxDao.findByIds(contractIdList);
            targetList.forEach(t -> {
                t.setReceiveOverdueInterest(t.getOverdueInterest().add(t.getExtraCost()).add(t.getAcceptDiscountCost()));
                t.setSettlementStatus("2");
            });
            applyDcsxDao.saveAll(targetList);
        }
    }


    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyInterestPay entity = applyInterestPayDao.findOne(approve.getBizId());
        List<String> paramStr = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(entity.getContractIds());
        List<Long> contractIdList = paramStr.stream().map(Long::valueOf).collect(Collectors.toList());
        List<ApplyCtrDCSX> targetList = applyDcsxDao.findByIds(contractIdList);
        targetList.forEach(t -> {
            t.setReceiveOverdueInterest(BigDecimal.ZERO);
            t.setSettlementStatus("0");
        });
        applyDcsxDao.saveAll(targetList);
    }

    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyInterestPay entity = applyInterestPayDao.findOne(vo.getBizId());
        List<String> paramStr = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(entity.getContractIds());
        List<Long> contractIdList = paramStr.stream().map(Long::valueOf).collect(Collectors.toList());
        List<ApplyCtrDCSX> targetList = applyDcsxDao.findByIds(contractIdList);
        targetList.forEach(t -> {
            t.setReceiveOverdueInterest(BigDecimal.ZERO);
            t.setSettlementStatus("0");
        });
        applyDcsxDao.saveAll(targetList);
    }


    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity instanceof ApplyInterestPay) {
            ApplyInterestPay entity = (ApplyInterestPay) pmEntity;
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity instanceof ApplyInterestPay) {
            ApplyInterestPay entity = (ApplyInterestPay) pmEntity;
            String receiveCompanyName = entity.getReceiveCompanyName();
            String ourCompanyName = entity.getOurCompanyName();
            BigDecimal payAmount = entity.getPayAmount();
            return SubjectUtil.formatSubject("收款方：" + receiveCompanyName, "我方：" + ourCompanyName, payAmount + RuleUtil.monetaryUnit);
        }
        return null;
    }
}

