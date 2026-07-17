package com.spt.bas.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.ApplyImport;
import com.spt.bas.client.entity.ApplyImportDetail;
import com.spt.bas.client.vo.ApplyImportQueryVo;
import com.spt.bas.server.dao.ApplyImportDao;
import com.spt.bas.server.dao.ApplyImportDetailDao;
import com.spt.bas.server.service.IApplyImportDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class ApplyImportDetailServiceImpl extends BaseService<ApplyImportDetail> implements IApplyImportDetailService {
	@Autowired
	private ApplyImportDetailDao applyImportDetailDao;
	@Autowired
	private ApplyImportDao applyImportDao;
	
	@Override
	public BaseDao<ApplyImportDetail> getBaseDao() {
		return applyImportDetailDao;
	}
	
	@Override
	public Class<ApplyImportDetail> getEntityClazz() {
		return ApplyImportDetail.class;
	}

	@Override
	public List<ApplyImportDetail> findByApplyImportId(ApplyImportQueryVo vo) {
		Long applyImportId=vo.getApplyImportId();
		return applyImportDetailDao.findByApplyImportId(applyImportId);
	}

	@Override
	public ApplyImportDetail findByContractId(Long contractId) {
		return applyImportDetailDao.findByContractId(contractId);
	}

	@Override
	public void updateApplyStatus(Long contractId) {
		applyImportDetailDao.updateApplyStatus(contractId);
	}

	@Override
	public List<ApplyImportDetail> findByApplyQueryVo(ApplyImportQueryVo vo) {
		Long applyImportId = vo.getApplyImportId();
		String contractType = vo.getContractType();
		return applyImportDetailDao.findByApplyQueryVo(applyImportId, contractType);
	}

	@Override
	public List<ApplyImportDetail> findByApproveId(Long approveId) {
		ApplyImport impt = applyImportDao.findByApproveId(approveId);
		if (impt == null) {
			return null;
		}
		List<ApplyImportDetail> importDetailList = applyImportDetailDao.findByApplyImportId(impt.getId());
		return importDetailList;
	}
	
}

