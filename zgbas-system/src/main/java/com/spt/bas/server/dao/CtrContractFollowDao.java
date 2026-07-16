package com.spt.bas.server.dao;

import java.util.List;

import com.spt.bas.client.entity.CtrContractFollow;
import com.spt.tools.jpa.dao.BaseDao;

public interface CtrContractFollowDao extends BaseDao<CtrContractFollow> {
	
	List<CtrContractFollow> findByCtrContractId(Long ctrContractId);
	List<CtrContractFollow> findByCtrContractIdIn(List<Long> lstContractId);
}