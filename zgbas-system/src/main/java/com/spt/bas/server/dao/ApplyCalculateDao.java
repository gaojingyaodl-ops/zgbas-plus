package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyCalculate;

public interface ApplyCalculateDao extends BaseDao<ApplyCalculate> {

	List<ApplyCalculate> findByCalculateNo(String calculateNo);

	List<ApplyCalculate> findByCalculateNoAndContractId(String calculateNo, Long contractId);

	@Query("from ApplyCalculate where importDetailId =?1 and calculateNo =?2 and status ='N'")
	List<ApplyCalculate> findByImportId(Long importId, String calculateNo);
}

