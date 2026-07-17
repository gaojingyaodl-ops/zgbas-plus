package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyFundRecharge;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.fund.ApplyFundRechargeDao;
import com.spt.bas.server.enums.FundFlowEnum;
import com.spt.bas.server.service.IApplyFundRechargeService;
import com.spt.bas.server.service.IFundAmountFlowService;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author MoonLight
 * @Date 2024/7/12 17:49
 * @Version 1.0
 */
@Component("applyFundRechargeService")
@Transactional(readOnly = true)
public class ApplyFundRechargeServiceImpl extends BaseService<ApplyFundRecharge> implements IApplyFundRechargeService, IPmService, IPmApproveListener {

    @Resource
    private ApplyFundRechargeDao applyFundRechargeDao;
    @Resource
    private IFundAmountFlowService fundAmountFlowService;
    @Resource
    private IPmApproveService pmApproveService;

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyFundRecharge entity = applyFundRechargeDao.findOne(approve.getBizId());
            fundAmountFlowService.addFundFlow(entity.getFundCompanyName(), entity.getOurCompanyName(), entity.getRechargeAmount(), FundFlowEnum.Recharge, approve);
        }
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyFundRecharge entity = applyFundRechargeDao.findOne(vo.getBizId());
        PmApprove approve = pmApproveService.getEntity(entity.getApproveId());
        fundAmountFlowService.addFundFlow(entity.getFundCompanyName(), entity.getOurCompanyName(), entity.getRechargeAmount().negate(), FundFlowEnum.RechargeCancel, approve);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyFundRecharge entity = (ApplyFundRecharge) pmEntity;
            return save(entity);
        }
        return null;
    }

    /**
     * 标题
     *
     * @param pmEntity
     * @param pmProcess
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity != null) {
            ApplyFundRecharge entity = (ApplyFundRecharge) pmEntity;
            String fundCompanyName = entity.getFundCompanyName();
            BigDecimal rechargeAmount = entity.getRechargeAmount();
            return SubjectUtil.formatSubject(fundCompanyName, SubjectUtil.formatMoney(rechargeAmount , RuleUtil.monetaryUnit));
        }
        return "";
    }

    @Override
    public BaseDao<ApplyFundRecharge> getBaseDao() {
        return applyFundRechargeDao;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        if (Objects.nonNull(id) && id != 0L){
            applyFundRechargeDao.updateFileId(id, fileId);
        }
    }
}
