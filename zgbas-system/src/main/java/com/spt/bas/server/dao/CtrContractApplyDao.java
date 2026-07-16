package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractApply;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CtrContractApplyDao extends BaseDao<CtrContractApply> {
	
	CtrContractApply findByCtrContractId(Long contractId);

	@Query("from CtrContractApply a where a.ctrContractId in ?1")
	List<CtrContractApply> findByCtrContractIdIn(List<Long> contractIdList);
}


