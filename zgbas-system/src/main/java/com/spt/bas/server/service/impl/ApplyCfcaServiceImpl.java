package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCfca;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.dao.ApplyCfcaDao;
import com.spt.bas.server.service.IApplyCfcaService;
import com.spt.bas.server.service.IBsCompanyService;
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
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-21 10:24
 */
@Component("applyCfcaService")
@Transactional
public class ApplyCfcaServiceImpl extends BaseService<ApplyCfca> implements IApplyCfcaService, IPmApproveListener {

    @Autowired
    private ApplyCfcaDao applyCfcaDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IWxUserDetailClient userDetailDao;
    @Autowired
    private IBsCompanyService companyService;

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
            ApplyCfca entity = JsonUtil.json2Object(ApplyCfca.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);

            // cfca通过后更新企业信息字段
            BsCompany company = companyService.getEntity(entity.getCompanyId());
            company.setOpenCfcaFlg(true);
            companyService.save(company);

            // 修改userDetails状态
            UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
            userDetail.setIsBind(true);
            userDetail.setCompanyApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
            userDetail.setCfcaApprovedStatus(BasConstants.APPLY_STATUS_COMPLETE);
            userDetailDao.save(userDetail);


        }
    }

    /**
     * 审批追回
     *
     * @param vo
     */
    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyCfca entity = JsonUtil.json2Object(ApplyCfca.class, contents);
        // 修改userDetails状态
        UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
        userDetail.setCompanyApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetail.setCfcaApprovedStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetailDao.save(userDetail);

    };


    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyCfca entity = JsonUtil.json2Object(ApplyCfca.class, contents);
        // 修改userDetails状态
        UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
        userDetail.setCompanyApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetail.setCfcaApprovedStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetailDao.save(userDetail);
    }

    @Override
    public BaseDao<ApplyCfca> getBaseDao() {
        return applyCfcaDao;
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
        ApplyCfca entity = JsonUtil.json2Object(ApplyCfca.class, contents);
        // 修改userDetails状态
        UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
        userDetail.setCompanyApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetail.setCfcaApprovedStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetailDao.save(userDetail);
    }


}
