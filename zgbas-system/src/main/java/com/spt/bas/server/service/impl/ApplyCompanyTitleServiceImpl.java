package com.spt.bas.server.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyTitle;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyCompanyTitleDao;
import com.spt.bas.server.service.IApplyCompanyTitleService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("applyCompanyTitleService")
@Transactional(readOnly = true)
public class ApplyCompanyTitleServiceImpl extends BaseService<ApplyCompanyTitle> implements IApplyCompanyTitleService,IPmService, IPmApproveListener {
	@Autowired
	private ApplyCompanyTitleDao applyCompanyTitleDao;
	@Autowired
	private ICtrContractService ctrContractService;
	
	@Override
	public BaseDao<ApplyCompanyTitle> getBaseDao() {
		return applyCompanyTitleDao;
	}
	
	@Override
	public Class<ApplyCompanyTitle> getEntityClazz() {
		return ApplyCompanyTitle.class;
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyCompanyTitle companyTitle = applyCompanyTitleDao.findOne(approve.getBizId());
			Long contractId = companyTitle.getContractId();
			CtrContract contract = ctrContractService.getEntity(contractId);
			if (contract != null){
				contract.setOurCompanyName(companyTitle.getNewOurCompanyName());
				ctrContractService.save(contract);
			}
			
		}
		
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			ApplyCompanyTitle entity = (ApplyCompanyTitle) pmEntity;
			return save(entity);
		}
		return null;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyCompanyTitle entity = (ApplyCompanyTitle) pmEntity;
			
			String contractNo = entity.getContractNo();
			String oldOurCompanyName = entity.getOldOurCompanyName();
			String newOurCompanyName = entity.getNewOurCompanyName();
			String companyName1 = RuleUtil.companyNameSubString(oldOurCompanyName);
			String companyName2 = RuleUtil.companyNameSubString(newOurCompanyName);
			String company="";
			if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)){
				company=companyName1+"-"+companyName2;
			}
			String subject = SubjectUtil.formatSubject(contractNo,company);
			return subject;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyCompanyTitleDao.updateFileId(id, fileId);
	}
	
}

