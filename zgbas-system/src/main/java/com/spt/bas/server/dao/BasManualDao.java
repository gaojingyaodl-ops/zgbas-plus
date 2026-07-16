package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BasManual;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

public interface BasManualDao extends BaseDao<BasManual> {
    List<BasManual> findAllByEnableFlgTrue();
}
