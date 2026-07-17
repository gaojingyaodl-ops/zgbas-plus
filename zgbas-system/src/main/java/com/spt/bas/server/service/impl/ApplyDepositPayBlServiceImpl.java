package com.spt.bas.server.service.impl;


import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyDepositPayBlDao;
import com.spt.bas.server.service.IApplyDepositPayBlService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Component("applyDepositPayBlService")
@Transactional(readOnly = true)
public class ApplyDepositPayBlServiceImpl extends BaseService<ApplyPay> implements IApplyDepositPayBlService, IPmService,IPmApproveListener {

    @Autowired
    private ApplyDepositPayBlDao applyDepositPayBlDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IApplyDepositPayBlService applyDepositPayBlService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyPay applyPay = JsonUtil.json2Object(ApplyPay.class, contents);
            applyPay.setApproveId(approve.getId());//审批id
            applyPay.setId(0L);//编号
            applyPay.setCreatedDate(approve.getCreatedDate());//创建时间
            applyPay.setUpdatedDate(approve.getUpdatedDate());//更新时间
            save(applyPay);
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        return;
    }

    @Override
    public BaseDao<ApplyPay> getBaseDao() {
        return applyDepositPayBlDao;
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyPay entity = (ApplyPay) pmEntity;
            ApplyPay applyPay = applyDepositPayBlService.getEntity(entity.getContractId());
            PmApprove entity1 = pmApproveService.getEntity(applyPay.getApproveId());
            if(entity1 != null){
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            return save(entity);

        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity != null) {
            ApplyPay pay = (ApplyPay) pmEntity;
            String contractNo = pay.getContractNo();
            String companyName = pay.getCompanyName();
            BigDecimal sumNumber = pay.getPayAmount();
            String payAmount = NumberUtil.formatNumber(sumNumber, "#.##");
            String payType = pay.getPayType();
            String title = BsDictUtil.getValue(pay.getEnterpriseId(), BsDictConstants.DICT_TYPE_PAYTYPE, payType);
            String subject = String.format("%s", "[" + contractNo + " " + " " + payAmount + " " + title + "]");
            return subject;
        }
        return null;
    }
}
