package com.spt.bas.server.dao;

import com.spt.bas.client.entity.RptBaseCost;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;
public interface RptBaseCostMapper extends BaseDao<RptBaseCost> {


    List<RptBaseCost> findByBaseDate(String baseDate);
}
