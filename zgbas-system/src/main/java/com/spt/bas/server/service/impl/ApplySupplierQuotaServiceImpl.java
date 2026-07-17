package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySupplierQuota;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.server.dao.ApplySupplierQuotaDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplySupplierQuotaService;
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

@Component("applySupplierQuotaService")
@Transactional(readOnly = true)
public class ApplySupplierQuotaServiceImpl extends BaseService<ApplySupplierQuota> implements IApplySupplierQuotaService, IPmApproveListener{
    @Autowired
    private ApplySupplierQuotaDao applySupplierQuotaDao;
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
            ApplySupplierQuota applySupplierQuota = JsonUtil.json2Object(ApplySupplierQuota.class, contents);
            applySupplierQuota.setFileId(pmApproveContents.getFileId());
            applySupplierQuotaDao.save(applySupplierQuota);

            BsCompany bsCompany = bsCompanyDao.findOne(applySupplierQuota.getCompanyId());
            bsCompany.setSupplierPrepayAmount(applySupplierQuota.getSupplierPrepayAmount());
            bsCompany.setSupplierPurchaseAmount(applySupplierQuota.getSupplierPurchaseAmount());
            bsCompany.setSupplierLevel(applySupplierQuota.getSupplierLevel());
            bsCompany.setSupplierQuotaStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompanyDao.save(bsCompany);
        }
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplySupplierQuota applySupplierQuota = JsonUtil.json2Object(ApplySupplierQuota.class, contents);

        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierQuota.getCompanyId());
        bsCompany.setSupplierQuotaStatus(BasConstants.APPLY_STATUS_REJECT);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<ApplySupplierQuota> getBaseDao() {
        return applySupplierQuotaDao;
    }

    @Override
    public Class<ApplySupplierQuota> getEntityClazz() {
        return ApplySupplierQuota.class;
    }
}
