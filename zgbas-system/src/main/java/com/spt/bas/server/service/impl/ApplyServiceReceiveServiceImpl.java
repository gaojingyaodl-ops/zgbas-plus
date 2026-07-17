package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyServiceReceive;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyServiceReceiveDao;
import com.spt.bas.server.dao.CtrContractApplyDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IApplyServiceReceiveService;
import com.spt.bas.server.service.ICtrContractApplyService;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.service.ICtrContractService;
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
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务费收款
 */
@Component("applyServiceReceiveService")
@Transactional(readOnly = true)
public class ApplyServiceReceiveServiceImpl extends BaseService<ApplyServiceReceive>
        implements IApplyServiceReceiveService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyServiceReceiveDao applyServiceReceiveDao;
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
    private IPmProcessService pmProcessService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseDao<ApplyServiceReceive> getBaseDao() {
        return applyServiceReceiveDao;
    }

    @Override
    public Class<ApplyServiceReceive> getEntityClazz() {
        return ApplyServiceReceive.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyServiceReceiveDao.updateFileId(id, fileId);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyServiceReceive receive = applyServiceReceiveDao.findOne(approve.getBizId());
            Long contractId = receive.getContractId();
            CtrContract contract = ctrContractService.getEntity(contractId);
            if (contract.getApplyCancelFlg()) {
                throw new ApplicationException("请驳回，该合同处于合同作废阶段!");
            }
            ctrContractUpdateService.addServiceAmount(contractId, receive.getReceiveAmount(), approve.getApproveNo());
            contractOphisService.addHis(BasConstants.APPLY_TYPE_H, contractId, approve, receive.getReceiveDate());
        }

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        //作废服务费收款单
        ApplyServiceReceive receive = applyServiceReceiveDao.findOne(vo.getBizId());
        PmApprove approve = pmApproveService.getEntity(receive.getApproveId());
        Long contractId = receive.getContractId();
        ctrContractUpdateService.addServiceAmount(contractId, receive.getReceiveAmount().negate(), approve.getApproveNo());
        CtrContractOphisRequest request = new CtrContractOphisRequest();
        request.setApplyType(BasConstants.APPLY_TYPE_H);
        request.setCancel(true);
        request.setCtrContractId(contractId);
        request.setRemark(approve.getSubject());
        request.setCreateUserId(vo.getUserId());
        request.setCreateUserName(vo.getUserName());
        request.setApproveId(vo.getApproveIdNew());
        contractOphisService.addHis(request);
        rollbackContractApply(receive);
    }

    private void rollbackContractApply(ApplyServiceReceive entity) throws ApplicationException {
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getReceiveAmount().negate());
        vo.setApplyType(BasConstants.APPLY_TYPE_H);
        contractApplyService.updateCtrContractApply(vo);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyServiceReceive entity = (ApplyServiceReceive) pmEntity;
            PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
            if (entity1!= null) {
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            if (entity.getId() == 0) {
                String applyNo = composeContractNo(entity.getServiceContractNo());
                entity.setApplyNo(applyNo);
            }
            return save(entity);
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyServiceReceive entity = applyServiceReceiveDao.findOne(approve.getBizId());
        CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());
        CtrContract ctrContract = ctrContractDao.findOne(entity.getContractId());
        BigDecimal amount = entity.getReceiveAmount().add(contractApply.getApplyServiceAmount());
        BigDecimal subtract = ctrContract.getServiceAmount().add(ctrContract.getBreachAmount()).subtract(amount);
        BigDecimal sperAmount = ctrContract.getServiceAmount().add(ctrContract.getBreachAmount()).subtract(contractApply.getApplyServiceAmount());
        if (entity.getReceiveAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApplicationException("收款金额必须大于0!");
        }
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApplicationException("收款金额有误,剩余可收款金额为：" + sperAmount);
        }
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getReceiveAmount());
        vo.setApplyType(BasConstants.APPLY_TYPE_H);
        contractApplyService.updateCtrContractApply(vo);
    }


    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyServiceReceive receive = (ApplyServiceReceive) pmEntity;
            String contractNo = receive.getServiceContractNo();
            String companyName = receive.getCompanyName();
            BigDecimal sumNumber = BigDecimal.ZERO;
            sumNumber = receive.getReceiveAmount();
            String receiveAmount = SubjectUtil.formatMoney(sumNumber,  RuleUtil.monetaryUnit);
            
            String subject = String.format("%s", "[" + contractNo + " " + companyName + " " + receiveAmount + "]");
            return subject;
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyServiceReceive entity = applyServiceReceiveDao.findOne(approve.getBizId());
        //更新CtrContractApply中数据
        rollbackContractApply(entity);
    }

    @Override
    public List<ApplyServiceReceive> findByContractId(Long contractId) {
        return applyServiceReceiveDao.findByContractId(contractId);
    }

    @Override
    public List<ApplyServiceReceive> findByServiceContractId(Long serviceContractId) {
        return applyServiceReceiveDao.findByServiceContractId(serviceContractId);
    }

    @Override
    public void updateApplyStatus(Long serviceContractId) {
        applyServiceReceiveDao.updateApplyStatus(serviceContractId);
    }

    private String composeContractNo(String contractNo) {
        List<ApplyServiceReceive> deliveryIn = applyServiceReceiveDao.findByServiceContractNo(contractNo);
        String fmt = String.format("%02d", deliveryIn.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_H + fmt;
    }
}
