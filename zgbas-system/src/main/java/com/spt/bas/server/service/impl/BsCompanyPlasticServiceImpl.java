package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyAllowed;
import com.spt.bas.server.dao.BsCompanyAllowedDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IBsCompanyPlasticService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.util.SubjectPmUtil;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Component("bsCompanyPlasticService")
@Transactional(readOnly = true)
public class BsCompanyPlasticServiceImpl extends BaseService<BsCompanyAllowed> implements IBsCompanyPlasticService, IPmApproveListener {
    @Autowired
    private BsCompanyAllowedDao bsCompanyAllowedDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Resource
    private PmApproveDao pmApproveDao;

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        String subject = approve.getSubject();
        List<String> subjectList = Splitter.on("，").omitEmptyStrings().splitToList(subject);
        if (CollectionUtils.isNotEmpty(subjectList)) {
            String companyName = subjectList.get(0);
            approve.setSubject(SubjectPmUtil.formatSubject(companyName, "PS (热塑性塑料)"));
            pmApproveDao.save(approve);
        }
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            BsCompanyAllowed entity = JsonUtil.json2Object(BsCompanyAllowed.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);

            // 完成后，修改企业状态
            BsCompany bsCompany = bsCompanyDao.findOne(entity.getCompanyId());
            bsCompany.setPlasticType("PS");
            bsCompany.setPlasticStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompany.setPlasticRemark(entity.getRemark());
            bsCompanyDao.save(bsCompany);
        }
    }

    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        // 更新公司准入状态
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        BsCompanyAllowed entity = JsonUtil.json2Object(BsCompanyAllowed.class, contents);
        logger.info("doStepBack - " + JsonUtil.obj2Json(entity));

        // 完成后，修改企业状态
        BsCompany bsCompany = bsCompanyDao.findOne(entity.getCompanyId());
        bsCompany.setPlasticStatus(BasConstants.APPLY_STATUS_REJECT);
        bsCompany.setPlasticRemark(null);
        bsCompanyDao.save(bsCompany);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<BsCompanyAllowed> getBaseDao() {
        return bsCompanyAllowedDao;
    }

    @Override
    public Class<BsCompanyAllowed> getEntityClazz() {
        return BsCompanyAllowed.class;
    }


}