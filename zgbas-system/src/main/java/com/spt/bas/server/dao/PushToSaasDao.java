package com.spt.bas.server.dao;

import java.util.List;

import com.spt.bas.client.entity.PushToSaas;
import com.spt.tools.jpa.dao.BaseDao;

public interface PushToSaasDao extends BaseDao<PushToSaas> {
	
	List<PushToSaas> findByPushFlg(Boolean pushFlg);
}

