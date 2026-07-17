package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.entity.LogisticsCompanyDetail;
import com.spt.bas.server.dao.LogisticsCompanyDetailDao;
import com.spt.bas.server.service.LogisticsCompanyDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component("LogisticsCompanyDetailService")
@Transactional(readOnly = false)
public class LogisticsCompanyDetailServiceimpl extends BaseService<LogisticsCompanyDetail> implements LogisticsCompanyDetailService {

	@Autowired
	LogisticsCompanyDetailDao logisticsCompanyDetailDao;

	@Override
	public BaseDao<LogisticsCompanyDetail> getBaseDao() {
		return logisticsCompanyDetailDao;
	}

	@Override
	public BigDecimal findByCarrierScoreAVG(Long id) {
		return logisticsCompanyDetailDao.findByCarrierScoreAVG(id);
	}

	@Override
	public List<LogisticsCompanyDetail> findByLogisticsCompanyId(Long id) {
		return logisticsCompanyDetailDao.findByLogisticsCompanyId(id);
	}
}

