package com.spt.bas.server.dao;

import com.spt.bas.client.entity.RiskScoreDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface RiskScoreDetailDao extends BaseDao<RiskScoreDetail> {
	
	RiskScoreDetail findTopByCompanyIdAndScoreItemAndScoreCompanyType(Long companyId,String scoreItem,String scoreCompanyType);
	
}

