package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserRegisterVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyPartner;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.dao.ApplyPartnerDao;
import com.spt.bas.server.service.IApplyPartnerService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-15 15:21
 */
@Component("applyPartnerService")
@Slf4j
public class ApplyPartnerServiceImpl extends BaseService<ApplyPartner> implements IApplyPartnerService, IPmApproveListener {
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

    @Autowired
    private ApplyPartnerDao applyPartnerDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IWxUserDetailClient userDetailDao;

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            // 保存
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyPartner entity = JsonUtil.json2Object(ApplyPartner.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setStatus(BasConstants.APPROVE_STATUS_D);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);

            // 修改userDetail的状态
            UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
            userDetail.setIsBind(true);
            userDetail.setPartnerApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
            userDetailDao.save(userDetail);
            // 创建admin账号
            log.info("开始创建账号...");
            registerPartner(entity);

        }
    }

    /**
     * 注册合伙人账号
     * @param applyPartner
     */
    private void registerPartner(ApplyPartner applyPartner) {
        UserRegisterVo userRegisterVo = new UserRegisterVo();
        userRegisterVo.setMobile(applyPartner.getPhone());
        userRegisterVo.setPassword("123456");
        authOpenFacade.registerPartner(userRegisterVo);
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
    public BaseDao<ApplyPartner> getBaseDao() {
        return applyPartnerDao;
    }
}
