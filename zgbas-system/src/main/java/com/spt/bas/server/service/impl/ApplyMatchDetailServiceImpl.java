package com.spt.bas.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.vo.ApplyMatchQueryVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyMatchDao;
import com.spt.bas.server.dao.ApplyMatchDetailDao;
import com.spt.bas.server.service.IApplyMatchDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class ApplyMatchDetailServiceImpl extends BaseService<ApplyMatchDetail> implements IApplyMatchDetailService {
	@Autowired
	private ApplyMatchDetailDao applyMatchDetailDao;
	@Autowired
	private ApplyMatchDao applyMatchDao;

	@Override
	public BaseDao<ApplyMatchDetail> getBaseDao() {
		return applyMatchDetailDao;
	}

	@Override
	public Class<ApplyMatchDetail> getEntityClazz() {
		return ApplyMatchDetail.class;
	}

	@Override
	public List<ApplyMatchDetail> findByApplyMatchId(ApplyMatchQueryVo vo) {
		Long applyMatchId = vo.getApplyMatchId();
		List<ApplyMatchDetail> list = applyMatchDetailDao.findByApplyMatchId(applyMatchId);
		return list;
	}

	@Override
	public List<ApplyMatchDetail> findByQueryVo(ApplyMatchQueryVo vo) {
		Long applyMatchId = vo.getApplyMatchId();
		String contractType = vo.getContractType();
		List<ApplyMatchDetail> list = applyMatchDetailDao.findByQueryVo(applyMatchId, contractType);
		return list;
	}

	@Override
	public ApplyMatchDetail findByContractId(Long contractId) {
		return applyMatchDetailDao.findByContractId(contractId);
	}

	@Override
	@ServerTransactional
	public void updateApplyStatus(Long contractId) {
		applyMatchDetailDao.updateApplyStatus(contractId);
	}

	@Override
	public List<ApplyMatchDetail> findByApproveId(Long approveId) {
		ApplyMatch match = applyMatchDao.findByApproveId(approveId);
		if (match == null) {
			return null;
		}
		List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(match.getId());
		return matchDetailList;
	}

	@Override
	public ApplyMatchDetail findByContractNo(String contractNo) {
		return applyMatchDetailDao.findByContractNo(contractNo);
	}
}
