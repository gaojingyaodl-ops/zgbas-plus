package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySupplierDelivery;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.server.dao.ApplySupplierDeliveryDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplySupplierDeliveryService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveRetrieveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("applySupplierDeliveryService")
@Transactional(readOnly = true)
public class ApplySupplierDeliveryServiceImpl extends BaseService<ApplySupplierDelivery> implements IApplySupplierDeliveryService, IPmApproveListener {
    @Autowired
    private ApplySupplierDeliveryDao applySupplierDeliveryDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplySupplierDelivery applySupplierDelivery = JsonUtil.json2Object(ApplySupplierDelivery.class, contents);
        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierDelivery.getCompanyId());
        bsCompany.setSupplierDeliveryStatus(BasConstants.APPLY_STATUS_APPLYING);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplySupplierDelivery applySupplierDelivery = JsonUtil.json2Object(ApplySupplierDelivery.class, contents);
            applySupplierDelivery.setFileId(pmApproveContents.getFileId());
            applySupplierDelivery.setApproveId(approve.getId());
            applySupplierDeliveryDao.save(applySupplierDelivery);

            BsCompany bsCompany = bsCompanyDao.findOne(applySupplierDelivery.getCompanyId());
            bsCompany.setSupplierDelivery(applySupplierDelivery.getSupplierDelivery());
            bsCompany.setSupplierDeliveryStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompany.setSupplierDeliveryRemark(applySupplierDelivery.getRemark());
            bsCompanyDao.save(bsCompany);
        }
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplySupplierDelivery applySupplierDelivery = JsonUtil.json2Object(ApplySupplierDelivery.class, contents);

        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierDelivery.getCompanyId());
        bsCompany.setSupplierDeliveryStatus(BasConstants.APPLY_STATUS_REJECT);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplySupplierDelivery applySupplierDelivery = JsonUtil.json2Object(ApplySupplierDelivery.class, contents);
        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierDelivery.getCompanyId());
        bsCompany.setSupplierDeliveryStatus(BasConstants.APPLY_STATUS_NO_START);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplySupplierDelivery applySupplierDelivery = JsonUtil.json2Object(ApplySupplierDelivery.class, contents);
        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierDelivery.getCompanyId());
        bsCompany.setSupplierDeliveryStatus(BasConstants.APPLY_STATUS_NO_START);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    public BaseDao<ApplySupplierDelivery> getBaseDao() {
        return applySupplierDeliveryDao;
    }

    @Override
    public Class<ApplySupplierDelivery> getEntityClazz() {
        return ApplySupplierDelivery.class;
    }

}
