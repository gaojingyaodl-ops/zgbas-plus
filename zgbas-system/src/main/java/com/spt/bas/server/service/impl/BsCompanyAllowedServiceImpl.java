package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.ISaveTempClient;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.purchase.wx.client.vo.QuotaTestVo;
import com.spt.bas.server.dao.BsCompanyAllowedDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IBsCompanyAllowedService;
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

@Component("bsCompanyAllowedService")
@Transactional(readOnly = true)
public class BsCompanyAllowedServiceImpl extends BaseService<BsCompanyAllowed> implements IBsCompanyAllowedService, IPmApproveListener {
    @Autowired
    private BsCompanyAllowedDao bsCompanyAllowedDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IWxUserDetailClient userDetailDao;
    @Autowired
    private ISaveTempClient saveTempClient;
    @Autowired
    private BsCompanyDao bsCompanyDao;

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
            bsCompany.setCompanyCategory(entity.getCompanyCategory());
            bsCompany.setCreditRating(entity.getCreditRating());
            // 兼容以前的，现在allowed字段已弃用
            bsCompany.setAllowed("Y");
            bsCompany.setCreditRatingStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompanyDao.save(bsCompany);

            // 采购管家小程序发起的审批
            if (ApplySource.PURCHASE.getCode().equals(entity.getApplySource())) {
                // 修改userDetails状态
                UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
                userDetail.setIsBind(true);
                userDetail.setApplyIouStatus(BasConstants.APPLY_STATUS_COMPLETE);
                userDetail.setQuotaTestStatus(BasConstants.APPLY_STATUS_COMPLETE);
                userDetailDao.save(userDetail);
                // 同步更新company表的3个额度字段
                bsCompany.setTotalCreditAmount(userDetail.getTotalCreditAmount());
                bsCompany.setTotalFuturesAmount(userDetail.getTotalFuturesAmount());
                bsCompany.setTotalSpotAmount(userDetail.getTotalSpotAmount());
                bsCompanyDao.save(bsCompany);

                // 审核完成后保存临时信息
                saveTempClient.commitTempInfo(SaveInfoType.QUOTA_TEST.getType(), entity.getWxUserId());

                // 同步保存信息到company表
                syncQuotaInfo(userDetail);
            }

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
        bsCompany.setCreditRating(BasConstants.DICT_TYPE_CREDITRATING_G);
        // allowed字段不再使用 为兼容默认设置为'Y'
//        bsCompany.setAllowed(BasConstants.DICT_TYPE_ALLOWED_N);
        bsCompany.setCreditRatingStatus(BasConstants.APPLY_STATUS_REJECT);
        bsCompanyDao.save(bsCompany);

        // 小程序端发起的审批
        // 修改 userdetail的applyIOUStatus为3 quotatest为4（完成）
        if (ApplySource.PURCHASE.getCode().equals(entity.getApplySource())) {
            UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
            userDetail.setApplyIouStatus(BasConstants.APPLY_STATUS_REJECT);
            userDetail.setQuotaTestStatus(BasConstants.APPLY_STATUS_COMPLETE);
            userDetailDao.save(userDetail);

            // 审核完成后保存临时信息
            saveTempClient.commitTempInfo(SaveInfoType.QUOTA_TEST.getType(), entity.getWxUserId());

            // 同步保存信息到company表
            syncQuotaInfo(userDetail);
        }

    }


    /**
     * 同步保存信息到company表
     * @param userDetail
     */
    private void syncQuotaInfo(UserDetail userDetail) {
        SaveInfo infoByType = saveTempClient.getInfoByType(userDetail.getUserId(), SaveInfoType.QUOTA_TEST.getType());
        if (infoByType.getContent() != null) {
            QuotaTestVo quotaTest = JsonUtil.json2Object(QuotaTestVo.class, infoByType.getContent());
            BsCompany company = bsCompanyDao.findOne(userDetail.getCompanyId());
            // 企业类型
            company.setCompanyType(quotaTest.getCompanyType());
            // 所属行业
            company.setIndustry(quotaTest.getCustomCompanySource());
            // 账期
            company.setCreditDays(convertCreditDays(quotaTest.getCustomRepaymentPeriod()));
            // 营业执照附件id
            company.setFileId(quotaTest.getBusinessLicenseUrl());
            bsCompanyDao.save(company);
        }
    }

    // 转换自定义还款周期
    private Long convertCreditDays(String code) {
        switch (code) {
            case "0":
                return 15L;
            case "1":
                return 30L;
            case "2":
                return 60L;
            default:
                return 0L;
        }
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