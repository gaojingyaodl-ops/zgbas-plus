package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyBusinessRestrictRelieve;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.entity.BusinessRestrictRelieve;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyBusinessRestrictRelieveDao;
import com.spt.bas.server.service.IApplyBusinessRestrictRelieveService;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.bas.server.service.IBusinessRestrictRelieveService;
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
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;


@Component("applyBusinessRestrictRelieveService")
@Transactional
public class ApplyBusinessRestrictRelieveServiceImpl extends BaseService<ApplyBusinessRestrictRelieve> implements IApplyBusinessRestrictRelieveService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyBusinessRestrictRelieveDao applyBusinessRestrictRelieveDao;
    @Autowired
    private IBusinessRestrictRelieveService businessRestrictRelieveService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IBsCompanyCreditService companyCreditService;

    @Override
    public BaseDao<ApplyBusinessRestrictRelieve> getBaseDao() {
        return applyBusinessRestrictRelieveDao;
    }

    @Override
    public Class<ApplyBusinessRestrictRelieve> getEntityClazz() {
        return ApplyBusinessRestrictRelieve.class;
    }

    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyBusinessRestrictRelieve entity = applyBusinessRestrictRelieveDao.findOne(approve.getBizId());
        String creditType = entity.getCreditType();
        if (StringUtils.isNotBlank(creditType)) {
            BsCompanyCredit companyCredit = companyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(entity.getCompanyId(), creditType, true);
            if (Objects.isNull(companyCredit)) {
                if (StringUtils.equals(BasConstants.CREDIT_TYPE_0, creditType)) {
                    creditType = BasConstants.CREDIT_TYPE_NAME_0;
                } else if (StringUtils.equals(BasConstants.CREDIT_TYPE_1, creditType)) {
                    creditType = BasConstants.CREDIT_TYPE_NAME_1;
                } else if (StringUtils.equals(BasConstants.CREDIT_TYPE_9, creditType)) {
                    creditType = BasConstants.CREDIT_TYPE_NAME_9;
                }
                throw new ApplicationException("【" + entity.getCompanyName() + "】不存在【" + creditType + "授信】");
            }
        }
    }
    
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyBusinessRestrictRelieve entity = applyBusinessRestrictRelieveDao.findOne(approve.getBizId());
            BusinessRestrictRelieve businessRestrictRelieve = businessRestrictRelieveService.findByCompanyIdAndAndUserId(entity.getCompanyId(), approve.getCreateUserId());
            if (Objects.nonNull(businessRestrictRelieve)) {
                businessRestrictRelieve.setUsableCount(businessRestrictRelieve.getUsableCount()+1);
            } else {
                businessRestrictRelieve = new BusinessRestrictRelieve();
                businessRestrictRelieve.setCompanyId(entity.getCompanyId());
                businessRestrictRelieve.setCompanyName(entity.getCompanyName());
                businessRestrictRelieve.setUserId(approve.getCreateUserId());
                businessRestrictRelieve.setUserName(approve.getCreateUserName());
                businessRestrictRelieve.setUsableCount(1);
            }
            businessRestrictRelieveService.save(businessRestrictRelieve);
            // 是否超额
//            Boolean newExcessFlg = entity.getNewExcessFlg();
//            if (newExcessFlg) {
//                // 超额金额
//                BigDecimal newExcessAmount = entity.getNewExcessAmount();
//                String creditType = entity.getCreditType();
//                if (StringUtils.isNotBlank(creditType) && Objects.nonNull(newExcessAmount)) {
//                    BsCompanyCredit companyCredit = companyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(entity.getCompanyId(), creditType, true);
//                    if (Objects.nonNull(companyCredit)) {
//                        companyCredit.setTemporaryAmount(companyCredit.getTemporaryAmount().add(newExcessAmount));
//                        Date temporaryExpiryDate = companyCredit.getTemporaryExpiryDate();
//                        if (temporaryExpiryDate == null) {
//                            companyCredit.setTemporaryExpiryDate(new Date());
//                        }
//                        companyCreditService.save(companyCredit);
//                    }
//                }
//            }
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyBusinessRestrictRelieve entity = applyBusinessRestrictRelieveDao.findOne(vo.getBizId());
        PmApprove approve = pmApproveService.getEntity(entity.getApproveId());
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_D,approve.getStatus())) {
            BusinessRestrictRelieve businessRestrictRelieve = businessRestrictRelieveService.findByCompanyIdAndAndUserId(entity.getCompanyId(), approve.getCreateUserId());
            if (Objects.nonNull(businessRestrictRelieve)) {
                int newCount = businessRestrictRelieve.getUsableCount() - 1;
                if (newCount < 0) {
                    newCount = 0;
                }
                businessRestrictRelieve.setUsableCount(newCount);
                businessRestrictRelieveService.save(businessRestrictRelieve);
            }
        }

    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyBusinessRestrictRelieve entity = (ApplyBusinessRestrictRelieve) pmEntity;
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity != null) {
            ApplyBusinessRestrictRelieve entity = (ApplyBusinessRestrictRelieve) pmEntity;
            String ownRegion = entity.getOwnRegion();
            String ownRegionName = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_OWN_REGION, ownRegion);
            String companyName = entity.getCompanyName();
            BigDecimal newCreditAmount = entity.getNewCreditAmount();
            return SubjectUtil.formatSubject(companyName, ownRegionName, SubjectUtil.formatMoney(newCreditAmount, RuleUtil.monetaryUnit));
        }
        return null;
    }

    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyBusinessRestrictRelieveDao.updateFileId(id, fileId);
    }

}
