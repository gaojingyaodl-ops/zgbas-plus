package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCfca;
import com.spt.bas.client.entity.ApplyTerminalPick;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.dao.ApplyTerminalPickDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplyTerminalPickService;
import com.spt.bas.server.service.IBsCompanyService;
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

/**
 * <p>
 *    终端工厂自提
 * </p>
 *
 */
@Component("applyTerminalPickService")
@Transactional
public class ApplyTerminalPickServiceImpl extends BaseService<ApplyTerminalPick> implements IApplyTerminalPickService, IPmApproveListener {


    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

    @Autowired
    private ApplyTerminalPickDao applyTerminalPickDao;

    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private IBsCompanyService bsCompanyService;

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyTerminalPick entity = JsonUtil.json2Object(ApplyTerminalPick.class, contents);
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            entity.setCreditDeliveryStatus(BasConstants.TERMINAL_PICK_DONE);
            BsCompany bsCompany = bsCompanyService.getEntity(entity.getCompanyId());
            bsCompany.setCreditDelivery(entity.getCreditDelivery());
            bsCompany.setCreditDeliveryStatus(BasConstants.TERMINAL_PICK_DONE);
            bsCompanyDao.save(bsCompany);
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);
        }
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyTerminalPick entity = JsonUtil.json2Object(ApplyTerminalPick.class, contents);
        entity.setApproveId(vo.getApproveId());
        entity.setId(0L);
        entity.setFileId(pmApproveContents.getFileId());
        entity.setCreditDeliveryStatus(BasConstants.TERMINAL_PICK_REFUSE);
        BsCompany bsCompany = bsCompanyService.getEntity(entity.getCompanyId());
        bsCompany.setCreditDeliveryStatus(BasConstants.TERMINAL_PICK_REFUSE);
        bsCompanyDao.save(bsCompany);
        save(entity);
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
        ApplyTerminalPick entity = JsonUtil.json2Object(ApplyTerminalPick.class, contents);
        entity.setApproveId(approve.getId());
        entity.setId(0L);
        entity.setFileId(pmApproveContents.getFileId());
        entity.setCreditDeliveryStatus(BasConstants.TERMINAL_PICK_REFUSE);
        BsCompany bsCompany = bsCompanyService.getEntity(entity.getCompanyId());
        bsCompany.setCreditDeliveryStatus(BasConstants.TERMINAL_PICK_REFUSE);
        bsCompanyDao.save(bsCompany);
        save(entity);
    }

    @Override
    public BaseDao<ApplyTerminalPick> getBaseDao() {
        return applyTerminalPickDao;
    }
}
