package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInternalTransferMoney;
import com.spt.bas.server.dao.ApplyInternalTransferMoneyDao;
import com.spt.bas.server.service.IApplyInternalTransferMoneyService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component("ApplyInternalTransferMoneyService")
@Transactional(readOnly = true)
public class ApplyInternalTransferMoneyServiceImpl extends BaseService<ApplyInternalTransferMoney>
        implements IApplyInternalTransferMoneyService, IPmApproveListener {

    @Autowired
    private ApplyInternalTransferMoneyDao applyInternalTransferMoneyDao;


    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyInternalTransferMoney entity = JsonUtil.json2Object(ApplyInternalTransferMoney.class, contents);
            entity.setApproveId(approve.getId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setId(0L);
            String lender = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY, entity.getLender());
            String borrower = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY, entity.getBorrower());
            entity.setLender(lender);
            entity.setBorrower(borrower);
            entity.setStatus("D");
            entity.setFileId(pmApproveContents.getFileId());
            save(entity);
        }


    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<ApplyInternalTransferMoney> getBaseDao() {
        return applyInternalTransferMoneyDao;
    }


}
