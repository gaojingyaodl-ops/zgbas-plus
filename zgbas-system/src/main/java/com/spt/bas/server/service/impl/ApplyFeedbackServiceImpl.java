package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApplyCancelVo;
import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.FeedbackDao;
import com.spt.bas.server.service.IApplyFeedbackService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
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
 * @Date: Created in 2020-10-15 11:24
 */
@Component("applyFeedbackService")
public class ApplyFeedbackServiceImpl extends BaseService<Feedback>
    implements IApplyFeedbackService, IPmApproveListener {

    @Autowired
    private FeedbackDao feedbackDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

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
            Feedback entity = JsonUtil.json2Object(Feedback.class, contents);
            entity.setApproveId(approve.getId());
            entity.setFileId(pmApproveContents.getFileId());
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
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

    }

    @Override
    public BaseDao<Feedback> getBaseDao() {
        return feedbackDao;
    }
}
