package com.spt.bas.server.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.CtrContractFee;
import com.spt.tools.jpa.dao.BaseDao;

public interface CtrContractFeeDao extends BaseDao<CtrContractFee> {

	@Transactional
	@Modifying
	@Query("update CtrContractFee c set c.fileId =?2 where c.id=?1")
	void updateFileId(Long id, String fileId);

	List<CtrContractFee> findByContractIdAndFeeType(Long contractId, String feeType);

	@Transactional
	@Modifying
	void deleteByContractIdAndFeeTypeAndFeeAmount(Long contractId, String feeType, BigDecimal feeAmount);
}

