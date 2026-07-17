package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;

import com.spt.bas.server.dao.ApplyVipMainInvoiceDao;

import com.spt.bas.server.service.IApplyVipMainInvoiceService;

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


/**
 * vip开票
 */
@Component("applyVipMainInvoiceService")
@Transactional(readOnly = true)
public class ApplyVipMainInvoiceServiceImpl extends BaseService<ApplyVipMainInvoice> implements IApplyVipMainInvoiceService,IPmApproveListener{


    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

    @Autowired
    private ApplyVipMainInvoiceDao applyVipMainInvoiceDao;

    @Override
    public BaseDao<ApplyVipMainInvoice> getBaseDao() {
        return applyVipMainInvoiceDao;
    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVipMainInvoice  entity = JsonUtil.json2Object(ApplyVipMainInvoice.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_B);
        entity.setId(0L);
        save(entity);
        IPmApproveListener.super.doStepBack(approve, nextStep);
    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVipMainInvoice  entity = JsonUtil.json2Object(ApplyVipMainInvoice.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_E);
        entity.setId(0L);
        save(entity);
        IPmApproveListener.super.doRetrieve(vo);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyVipMainInvoice entity = JsonUtil.json2Object(ApplyVipMainInvoice.class, contents);
            entity.setStatus(BasConstants.APPROVE_STATUS_D);
            entity.setId(0L);
            save(entity);
        }
    }
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVipMainInvoice  entity = JsonUtil.json2Object(ApplyVipMainInvoice.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_B);
        entity.setId(0L);
        save(entity);

    }
}

