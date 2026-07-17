package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BasContractOphis;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.bas.server.dao.BasContractOphisDao;
import com.spt.bas.server.service.IBasContractOphisService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BasContractOphisServiceImpl extends BaseService<BasContractOphis> implements IBasContractOphisService {
	@Autowired
	private BasContractOphisDao basContractOphisDao;
	
	@Override
	public BaseDao<BasContractOphis> getBaseDao() {
		return basContractOphisDao;
	}
	
	@Override
	public Class<BasContractOphis> getEntityClazz() {
		return BasContractOphis.class;
	}
	
	@Override
	public void addOphis(ContractOpVo opVo) {
		BasContractOphis basContractOphis = new BasContractOphis();
		basContractOphis.setContractId(opVo.getId());
		basContractOphis.setContractStatus(opVo.getContractStatus());
		basContractOphis.setCreateUserName(opVo.getCreateUserName());
		basContractOphis.setCreateUserId(opVo.getCreateUserId());
		
		basContractOphisDao.save(basContractOphis);
		
	}
}

