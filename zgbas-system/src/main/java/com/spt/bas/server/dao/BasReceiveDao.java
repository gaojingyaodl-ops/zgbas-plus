package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BasReceive;
import com.spt.tools.jpa.dao.BaseDao;

public interface BasReceiveDao extends BaseDao<BasReceive> {

	@Query("from BasReceive b where b.contractId=?1 ")
	public List<BasReceive> findByContractId(Long contractId);

	@Query("from BasReceive b where b.contractNo=?1 and b.receiveType=?2")
	public BasReceive findByReceiveVo(String contractNo, String receiveType);
}

