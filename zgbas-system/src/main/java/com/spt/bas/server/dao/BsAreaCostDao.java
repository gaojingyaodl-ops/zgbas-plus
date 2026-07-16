package com.spt.bas.server.dao;

import java.util.List;

import com.spt.bas.client.entity.BsAreaCost;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsAreaCostDao extends BaseDao<BsAreaCost> {

	List<BsAreaCost> findByAreaCode(String areaCode);
}

