package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySupplierFuture;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.server.dao.ApplySupplierFutureDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplySupplierFutureService;
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
import org.springframework.transaction.annotation.Transactional;

@Component("applySupplierFutureService")
@Transactional(readOnly = true)
public class ApplySupplierFutureServiceImpl extends BaseService<ApplySupplierFuture> implements IApplySupplierFutureService, IPmApproveListener {
    @Autowired
    private ApplySupplierFutureDao applySupplierFutureDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplySupplierFuture applySupplierFuture = JsonUtil.json2Object(ApplySupplierFuture.class, contents);
            applySupplierFuture.setFileId(pmApproveContents.getFileId());
            applySupplierFutureDao.save(applySupplierFuture);

            BsCompany bsCompany = bsCompanyDao.findOne(applySupplierFuture.getCompanyId());
            bsCompany.setSupplierFuture(applySupplierFuture.getSupplierFuture());
            bsCompany.setSupplierFutureStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompanyDao.save(bsCompany);
        }
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplySupplierFuture applySupplierFuture = JsonUtil.json2Object(ApplySupplierFuture.class, contents);

        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierFuture.getCompanyId());
        bsCompany.setSupplierFutureStatus(BasConstants.APPLY_STATUS_REJECT);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<ApplySupplierFuture> getBaseDao() {
        return applySupplierFutureDao;
    }

    @Override
    public Class<ApplySupplierFuture> getEntityClazz() {
        return ApplySupplierFuture.class;
    }
}
