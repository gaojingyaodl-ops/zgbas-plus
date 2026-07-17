package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyOnline;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyCompanyOnlineDao;
import com.spt.bas.server.service.IApplyCompanyOnlineService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveRetrieveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 线上化
 */
@Component("applyCompanyOnlineService")
public class ApplyCompanyOnlineServiceImpl extends BaseService<ApplyCompanyOnline> implements IApplyCompanyOnlineService, IPmApproveListener, IPmService {
    //@Autowired
    //private IAdminOpenFacade adminOpenFacade;

    @Autowired
    private ApplyCompanyOnlineDao applyCompanyOnlineDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IWxUserDetailClient userDetailClient;


    @Autowired
    private IApplyCompanyOnlineService applyCompanyOnlineService;

    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        IPmApproveListener.super.doStepIn(approve);
    }

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {

            ApplyCompanyOnline applyCompanyOnline = applyCompanyOnlineService.getEntity(approve.getBizId());
            BsCompany bsCompany = bsCompanyService.getEntity(applyCompanyOnline.getCompanyId());
            bsCompany.setOnLineFlg(true);


            applyCompanyOnline.setStatus(BasConstants.APPROVE_STATUS_D);
            this.save(applyCompanyOnline);

            List<UserDetail> userDetail = userDetailClient.findByCompanyIdAndEnableFlgTrue(bsCompany.getId());
            if(userDetail.size()>0){
                UserDetail userDetail1 = userDetail.get(0);
                userDetail1.setCompanyApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
                userDetail1.setEntrustApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
                userDetail1.setCfcaApprovedStatus(BasConstants.APPLY_STATUS_COMPLETE);
                userDetailClient.save(userDetail1);
            }


        }

    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        IPmApproveListener.super.doStepBack(approve, nextStep);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        IPmApproveListener.super.doRetrieve(vo);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyCompanyOnline entity = (ApplyCompanyOnline) pmEntity;
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyCompanyOnline companyOnline = (ApplyCompanyOnline) pmEntity;
            String companyName = companyOnline.getCompanyName();
            String subject = String.format("%s",
                    "[" + companyName + " " + " 线上化申请]");
            return subject;
        }
        return null;
    }

    @Override
    public Long getMatchUserId(IPmEntity pmEntity) {
        return IPmService.super.getMatchUserId(pmEntity);
    }

    @Override
    public BaseDao<ApplyCompanyOnline> getBaseDao() {
        return applyCompanyOnlineDao;
    }
}
