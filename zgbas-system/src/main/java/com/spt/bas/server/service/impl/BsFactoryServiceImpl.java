package com.spt.bas.server.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.server.dao.BsFactoryDao;
import com.spt.bas.server.service.IBsFactoryService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsFactoryServiceImpl extends BaseService<BsFactory> implements IBsFactoryService {
	@Autowired
	private BsFactoryDao bsFactoryDao;
	
	@Override
	public BaseDao<BsFactory> getBaseDao() {
		return bsFactoryDao;
	}
	
	@Override
	public Class<BsFactory> getEntityClazz() {
		return BsFactory.class;
	}

	@Override
	public List<BsFactory> findByEnterpriseId(Long enterpriseId) {
		return bsFactoryDao.findByEnterpriseId(enterpriseId);
	}

	@Override
	public Long countFactory(BsFactory factory) {
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if (factory.getId()!=null && factory.getId()>0) {
			queryParams.put("NEQL_id", factory.getId());
		}
		queryParams.put("EQS_factoryName", factory.getFactoryName());
		queryParams.put("EQL_enterpriseId", factory.getEnterpriseId());
		Specification<BsFactory> spec = WebUtil.buildSpecification(queryParams);
		return bsFactoryDao.count(spec);
	}

	@Override
	public List<BsFactory> findByFactoryNameAndEnterpriseId(BsFactory factory) {
		// TODO Auto-generated method stub
		return bsFactoryDao.findByFactoryNameAndEnterpriseId(factory.getFactoryName(), factory.getEnterpriseId());
	}
	
}

