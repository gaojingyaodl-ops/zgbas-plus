package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCreditCycle;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyAllowed;
import com.spt.bas.server.dao.ApplyCreditCycleDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplyCreditCycleService;
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
 * @Date: Created in 2021-01-12 13:41
 */
@Component("applyCreditCycleService")
@ServerTransactional
public class ApplyCreditCycleServiceImpl extends BaseService<ApplyCreditCycle> implements IApplyCreditCycleService, IPmApproveListener {

    @Autowired
    private ApplyCreditCycleDao applyCreditCycleDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;

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
            ApplyCreditCycle entity = JsonUtil.json2Object(ApplyCreditCycle.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);
            BsCompany company = bsCompanyDao.findOne(entity.getCompanyId());
            company.setCreditDays(entity.getCreditDays());
            company.setCreditCycleStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompanyDao.save(company);
        }
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
        ApplyCreditCycle entity = JsonUtil.json2Object(ApplyCreditCycle.class, contents);
        logger.info("doStepBack - " + JsonUtil.obj2Json(entity));

        // 完成后，修改企业状态
        BsCompany bsCompany = bsCompanyDao.findOne(entity.getCompanyId());
        bsCompany.setCreditCycleStatus(BasConstants.APPLY_STATUS_REJECT);
        bsCompanyDao.save(bsCompany);

    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<ApplyCreditCycle> getBaseDao() {
        return applyCreditCycleDao;
    }
}
