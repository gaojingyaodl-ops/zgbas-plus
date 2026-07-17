package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.BsAreaCost;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsAreaCostService extends IBaseService<BsAreaCost> {

	List<BsAreaCost> findByAreaCode(String areaCode);
	
}

