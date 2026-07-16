package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import com.spt.bas.client.entity.BsProductConfig;

public interface BsProductConfigDao extends BaseDao<BsProductConfig> {

	BsProductConfig findByConfigKeyAndEnterpriseId(String configKey, Long enterpriseId);
}

