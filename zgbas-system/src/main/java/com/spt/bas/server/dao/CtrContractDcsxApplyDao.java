package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractDcsxApply;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CtrContractDcsxApplyDao extends BaseDao<CtrContractDcsxApply> {

	CtrContractDcsxApply findByCtrContractId(Long contractId);

}


