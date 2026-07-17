package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCreditCycle;
import com.spt.bas.client.entity.ApplyRate;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.server.dao.ApplyRateDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplyRateService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-12 13:54
 */
@Component("applyRateService")
@ServerTransactional
public class ApplyRateServiceImpl extends BaseService<ApplyRate> implements IApplyRateService, IPmApproveListener {
    @Autowired
    private ApplyRateDao applyRateDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private BsCompanyDao companyDao;

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
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyRate entity = JsonUtil.json2Object(ApplyRate.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);

            BsCompany company = companyDao.findOne(entity.getCompanyId());
            company.setRate(entity.getRate());
            company.setInterestRate(entity.getInterestRate());
            company.setInterestRateStatus(BasConstants.APPLY_STATUS_COMPLETE);
            companyDao.save(company);
        }
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyRate entity = JsonUtil.json2Object(ApplyRate.class, contents);
        logger.info("doStepBack - " + JsonUtil.obj2Json(entity));

        // 完成后，修改企业状态
        BsCompany bsCompany = companyDao.findOne(entity.getCompanyId());
        bsCompany.setInterestRateStatus(BasConstants.APPLY_STATUS_REJECT);
        companyDao.save(bsCompany);
    }

    @Override
    public BaseDao<ApplyRate> getBaseDao() {
        return applyRateDao;
    }
}
