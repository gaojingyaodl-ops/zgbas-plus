package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatters;
import com.spt.bas.server.dao.ApplyMattersDao;
import com.spt.bas.server.service.IApplyMattersService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component("applyMattersService")
@Transactional
public class ApplyMattersServiceImpl extends BaseService<ApplyMatters> implements IApplyMattersService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyMattersDao applyMattersDao;

    @Override
    public BaseDao<ApplyMatters> getBaseDao() {
        return applyMattersDao;
    }
    @Override
    public Class<ApplyMatters> getEntityClazz() {
        return ApplyMatters.class;
    }
    
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyMatters entity = applyMattersDao.findOne(approve.getBizId());
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyMatters entity = (ApplyMatters) pmEntity;
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity != null) {
            ApplyMatters entity = (ApplyMatters) pmEntity;
            String mattersType = entity.getMattersType();
            String approveNo = entity.getApproveNo();
            String contractNo = entity.getContractNo();
            return SubjectUtil.formatSubject(mattersType,contractNo,approveNo,entity.getRemark());
        }
        return null;
    }
    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyMattersDao.updateFileId(id, fileId);
    }
    
}
