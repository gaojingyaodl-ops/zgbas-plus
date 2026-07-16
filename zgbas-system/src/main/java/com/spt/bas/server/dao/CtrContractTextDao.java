package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractText;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CtrContractTextDao extends BaseDao<CtrContractText> {

	CtrContractText findByCtrContractIdAndContractType(Long contractId, String contractType);

	CtrContractText findByCtrContractId(Long ctrContractId);

	@Query("from CtrContractText a where a.ctrContractId in ?1")
	List<CtrContractText> findByCtrContractIds(List<Long> contractIds);
}

