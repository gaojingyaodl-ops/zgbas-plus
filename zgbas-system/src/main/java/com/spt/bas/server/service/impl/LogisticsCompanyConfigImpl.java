package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.*;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.LogisticsCompanyConfigService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = false)
public class LogisticsCompanyConfigImpl extends BaseService<LogisticsCompanyConfig> implements LogisticsCompanyConfigService {

	@Autowired
	LogisticsCompanyConfigDao logisticsCompanyConfigDao;

	@Override
	public BaseDao<LogisticsCompanyConfig> getBaseDao() {
		return logisticsCompanyConfigDao;
	}

	@Override
	public LogisticsCompanyConfig getByCarrier(LogisticsCompanyConfig logisticsCompanyConfig) {
		Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
		LogisticsCompanyConfig entity = new LogisticsCompanyConfig();
		String carrier = logisticsCompanyConfig.getCarrier();
		if (carrier != null && carrier != ""){
			Specification<LogisticsCompanyConfig> specification = WebUtil.buildSpecification("EQS_carrier",logisticsCompanyConfig.getCarrier());
			List<LogisticsCompanyConfig> all = logisticsCompanyConfigDao.findAll(specification, sort);
			if (all.size()>0){
				entity = all.get(0);
			}
			return entity;
		}else{
			return entity;
		}

	}

	@Override
	public List<LogisticsCompanyConfig> findByOurCompanyNames(String ourCompanyName) {
		return logisticsCompanyConfigDao.findByOurCompanyNames(ourCompanyName);
	}

	@Override
	public LogisticsCompanyConfig findByCarrier(String carrier) {
		return logisticsCompanyConfigDao.findByCarrier(carrier);
	}
}

