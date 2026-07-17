package com.spt.bas.server.service.impl;

import org.apache.commons.lang3.StringUtils;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyLoss;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractLoss;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyLossDao;
import com.spt.bas.server.service.IApplyLossService;
import com.spt.bas.server.service.ICtrContractLossService;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveHistory;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveHistoryService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component("applyLossService")
@Transactional(readOnly = true)
public class ApplyLossServiceImpl extends BaseService<ApplyLoss> implements IApplyLossService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyLossDao applyLossDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IPmApproveHistoryService pmApprovevHistoryService;
    @Autowired
    private ICtrContractOphisService ctrContractOphisService;

    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private ICtrContractLossService contractLossService;

    @Override
    public BaseDao<ApplyLoss> getBaseDao() {
        return applyLossDao;
    }

    @Override
    public Class<ApplyLoss> getEntityClazz() {
        return ApplyLoss.class;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyLoss entity = applyLossDao.findOne(approve.getBizId());
            Long sellContractId = entity.getContractId();
            CtrContract contract = ctrContractService.getEntity(sellContractId);
            if (contract != null) {
                Long approveId = contract.getApproveId();
                List<PmApproveHistory> historys = pmApprovevHistoryService.findByApproveId(approve.getId());
                if (!historys.isEmpty()) {
                    PmApproveHistory pmApproveHistory = historys.get(historys.size() - 1);
                    pmApproveHistory.setApproveRemark("损耗");
                    pmApproveHistory.setApproveId(approveId);
                    pmApprovevHistoryService.save(pmApproveHistory);
                }
                BigDecimal lossAmount = entity.getLossAmount();
                if(lossAmount == null){
                    lossAmount = BigDecimal.ZERO;
                }
                BigDecimal lossNumber = entity.getLossNum();
                if(lossNumber == null){
                    lossNumber = BigDecimal.ZERO;
                }

                String lossTypeFromName = "";
                String lossTypeFrom = entity.getLossTypeFrom();
                if(StringUtils.isNotBlank(lossTypeFrom)){
                    BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.DICT_TYPE_LOSS_TYPE, lossTypeFrom);
                    if(bsDictData != null) {
                        lossTypeFromName = bsDictData.getDictName();
                    }

                }

                BigDecimal totalNumber = contract.getTotalNumber();
                BigDecimal totalAmount = contract.getTotalAmount();
                contract.setTotalAmount(totalAmount.subtract(lossAmount));
                contract.setTotalNumber(totalNumber.subtract(lossNumber));

                BigDecimal contractLossAmount = contract.getLossAmount();
                BigDecimal contractLossNumber = contract.getLossNumber();
                if(contractLossAmount != null) {
                    contractLossAmount = contractLossAmount.add(lossAmount);
                } else {
                    contractLossAmount = lossAmount;
                }
                if(contractLossNumber != null) {
                    contractLossNumber = contractLossNumber.add(lossNumber);
                } else {
                    contractLossNumber = lossNumber;
                }
                contract.setLossAmount(contractLossAmount);
                contract.setLossNumber(contractLossNumber);
                ctrContractService.save(contract);

                // 保存货损登记表
                CtrContractLoss ctrContractLoss = new CtrContractLoss();
                BeanUtils.copyProperties(entity,ctrContractLoss);
                ctrContractLoss.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                contractLossService.save(ctrContractLoss);

                CtrContractOphisRequest request = new CtrContractOphisRequest();
                request.setApplyType(null);
                request.setCtrContractId(contract.getId());
                request.setRemark(String.format("损耗 [损耗数量:%s吨  损耗金额: %s元  责任方: %s]", lossNumber,
                        lossAmount, lossTypeFromName));
                request.setCreateUserId(approve.getCreateUserId());
                request.setCreateUserName(approve.getCreateUserName());
                ctrContractOphisService.addHis(request);


            }

        }

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyLoss entity = (ApplyLoss) pmEntity;
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyLoss entity = (ApplyLoss) pmEntity;
            String sellContractNo = entity.getContractNo();
            String lossNumber = NumberUtil.formatNumber(entity.getLossNum(), "#.##");
            String lossAmount = NumberUtil.formatNumber(entity.getLossAmount(), "#.##");
//            String dutyCompanyName = NumberUtil.formatNumber(entity.getLossTypeFrom(), "#.##");
            String lossTypeFromName = "";
            String lossTypeFrom = entity.getLossTypeFrom();
            if(StringUtils.isNotBlank(lossTypeFrom)){
                BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.DICT_TYPE_LOSS_TYPE, lossTypeFrom);
                if(bsDictData != null) {
                    lossTypeFromName = bsDictData.getDictName();
                }

            }
            String lossTypeToName = "";
            if(StringUtils.isNotBlank(entity.getLossTypeTo())){
                BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.DICT_TYPE_LOSS_TYPE, entity.getLossTypeTo());
                if(bsDictData != null) {
                    lossTypeToName = bsDictData.getDictName();
                }
            }
            String companyName = RuleUtil.companyNameSubString(lossTypeFromName);
            String companyName2 = RuleUtil.companyNameSubString(lossTypeToName);
            final String subject = SubjectUtil.formatSubject(sellContractNo,lossNumber+ RuleUtil.weightUnit,SubjectUtil.formatMoney(entity.getLossAmount() , RuleUtil.monetaryUnit),companyName,companyName2);
            return subject;
        }
        return null;
    }

    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyLossDao.updateFileId(id, fileId);

    }
}

