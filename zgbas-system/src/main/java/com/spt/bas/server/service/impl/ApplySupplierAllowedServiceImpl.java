package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.vo.SupplierAllowed;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.IApplySupplierAllowedService;
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

import java.util.Objects;

@Component("applySupplierAllowedService")
@Transactional(readOnly = true)
public class ApplySupplierAllowedServiceImpl extends BaseService<ApplySupplierAllowed> implements IApplySupplierAllowedService, IPmApproveListener {
    @Autowired
    private ApplySupplierAllowedDao applySupplierAllowedDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private IBsCompanyClient bsCompanyClient;
    @Autowired
    private ApplySupplierQuotaDao applySupplierQuotaDao;
    @Autowired
    private ApplySupplierFutureDao applySupplierFutureDao;


    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplySupplierAllowed applySupplierAllowed = JsonUtil.json2Object(ApplySupplierAllowed.class, contents);
            applySupplierAllowed.setFileId(pmApproveContents.getFileId());
            applySupplierAllowedDao.save(applySupplierAllowed);
            //额度申请
            ApplySupplierQuota applySupplierQuota = JsonUtil.json2Object(ApplySupplierQuota.class, contents);
            applySupplierQuota.setFileId(pmApproveContents.getFileId());
            applySupplierQuotaDao.save(applySupplierQuota);
//            //配送
//            ApplySupplierDelivery applySupplierDelivery = JsonUtil.json2Object(ApplySupplierDelivery.class, contents);
//            applySupplierDelivery.setSupplierDelivery(applySupplierAllowed.getSupplierDeliveryOne());
//            applySupplierDelivery.setFileId(pmApproveContents.getFileId());
//            applySupplierDeliveryDao.save(applySupplierDelivery);
            //远期
            ApplySupplierFuture applySupplierFuture = JsonUtil.json2Object(ApplySupplierFuture.class, contents);
            applySupplierFuture.setSupplierFuture(applySupplierAllowed.getSupplierFutureOne());
            applySupplierFuture.setFileId(pmApproveContents.getFileId());
            applySupplierFutureDao.save(applySupplierFuture);
            //准入
            BsCompany bsCompany = bsCompanyDao.findOne(applySupplierAllowed.getCompanyId());
            bsCompany.setSupplierRating(applySupplierAllowed.getSupplierRating());
//            bsCompany.setSupplierDelivery(applySupplierAllowed.getSupplierDeliveryOne());
            bsCompany.setSupplierFuture(applySupplierAllowed.getSupplierFutureOne());
            bsCompany.setSupplierRatingStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompany.setCompanyCategory(applySupplierAllowed.getSupplierCategory());

            //额度
            bsCompany.setSupplierPrepayAmount(applySupplierQuota.getSupplierPrepayAmount());
            bsCompany.setSupplierPurchaseAmount(applySupplierQuota.getSupplierPurchaseAmount());
            bsCompany.setSupplierLevel(applySupplierQuota.getSupplierLevel());
            bsCompany.setSupplierQuotaStatus(BasConstants.APPLY_STATUS_COMPLETE);
//            //配送
//            bsCompany.setSupplierDelivery(applySupplierAllowed.getSupplierDeliveryOne());
//            bsCompany.setSupplierDeliveryStatus(Constant.APPLY_STATUS_COMPLETE);
            //远期
            bsCompany.setSupplierFuture(applySupplierAllowed.getSupplierFutureOne());
            bsCompany.setSupplierFutureStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompanyDao.save(bsCompany);
        }
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        SupplierAllowed applySupplierAllowed = JsonUtil.json2Object(SupplierAllowed.class, pmApproveContents.getContents());
        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierAllowed.getCompanyId());
        bsCompany.setSupplierRatingStatus(BasConstants.APPLY_STATUS_APPLYING);
        bsCompanyDao.save(bsCompany);

        Long count = ctrContractDao.countByCompanyId(bsCompany.getId());
        applySupplierAllowed.setCooperationFlg(Objects.nonNull(count) && count > 0);
        pmApproveContents.setContents(JsonUtil.obj2Json(applySupplierAllowed));
        pmApproveContentsDao.save(pmApproveContents);
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplySupplierAllowed applySupplierAllowed = JsonUtil.json2Object(ApplySupplierAllowed.class, contents);

        BsCompany bsCompany = bsCompanyDao.findOne(applySupplierAllowed.getCompanyId());
        bsCompany.setCreditRatingStatus(BasConstants.APPLY_STATUS_REJECT);
        //供应商准入审批状态
        bsCompany.setSupplierRatingStatus(BasConstants.SUPPLIER_RATING_STATUS_ONE);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        //供应商准入审批状态
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        ApplySupplierAllowed applySupplierAllowed = JsonUtil.json2Object(ApplySupplierAllowed.class, pmApproveContents.getContents());
        String companyName = applySupplierAllowed.getCompanyName();
        BsCompany bsCompany = bsCompanyClient.findByCompanyName(companyName);
        bsCompany.setSupplierRatingStatus(BasConstants.SUPPLIER_RATING_STATUS_ONE);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    public BaseDao<ApplySupplierAllowed> getBaseDao() {
        return applySupplierAllowedDao;
    }

    @Override
    public Class<ApplySupplierAllowed> getEntityClazz() {
        return ApplySupplierAllowed.class;
    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        //供应商准入审批状态
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        ApplySupplierAllowed applySupplierAllowed = JsonUtil.json2Object(ApplySupplierAllowed.class, pmApproveContents.getContents());
        String companyName = applySupplierAllowed.getCompanyName();
        BsCompany bsCompany = bsCompanyClient.findByCompanyName(companyName);
        bsCompany.setSupplierRatingStatus(BasConstants.SUPPLIER_RATING_STATUS_ONE);
        bsCompanyDao.save(bsCompany);
    }
}