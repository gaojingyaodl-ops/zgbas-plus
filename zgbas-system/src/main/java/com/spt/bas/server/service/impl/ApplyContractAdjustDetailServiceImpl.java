package com.spt.bas.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyContractAdjustDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyContractAdjustVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyContractAdjustDetailDao;
import com.spt.bas.server.service.IApplyContractAdjustDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class ApplyContractAdjustDetailServiceImpl extends BaseService<ApplyContractAdjustDetail> implements IApplyContractAdjustDetailService {
	@Autowired
	private ApplyContractAdjustDetailDao applyContractAdjustDetailDao;

	@Override
	public BaseDao<ApplyContractAdjustDetail> getBaseDao() {
		return applyContractAdjustDetailDao;
	}

	@Override
	public Class<ApplyContractAdjustDetail> getEntityClazz() {
		return ApplyContractAdjustDetail.class;
	}

	@Override
	public List<ApplyContractAdjustDetail> findByContractAdjustId(Long contractAdjustId) {
		// TODO Auto-generated method stub
		return applyContractAdjustDetailDao.findByContractAdjustId(contractAdjustId);
	}

}

