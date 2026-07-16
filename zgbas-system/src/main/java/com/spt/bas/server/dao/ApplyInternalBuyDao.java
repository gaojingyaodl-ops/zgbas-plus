package com.spt.bas.server.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyInternalBuy;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyInternalBuyDao extends BaseDao<ApplyInternalBuy> {

	List<ApplyInternalBuy> findByUpdatedDateAfterAndUpdatedDateBeforeAndStatus(Date startDate, Date endDate, String status);

	@Modifying
	@Query("update ApplyInternalBuy c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);

	ApplyInternalBuy findByContractId(Long contractId);
}

