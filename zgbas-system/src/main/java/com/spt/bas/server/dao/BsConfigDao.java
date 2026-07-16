package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsConfig;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

public interface BsConfigDao extends BaseDao<BsConfig> {

    List<BsConfig> findByEnterpriseIdAndEnableFlgTrue(Long enterpriseId);
}

