package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyImportDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyImportDetailDao extends BaseDao<ApplyImportDetail> {

	public List<ApplyImportDetail> findByApplyImportId(Long applyImportId);
	@Transactional
	@Modifying
	public void deleteByApplyImportId(Long id);

	public ApplyImportDetail findByContractId(Long contractId);

	@Modifying
	@Query("update ApplyImportDetail c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);

	@Query("from ApplyImportDetail d where d.applyImportId = ?1 and d.contractType = ?2")
	public List<ApplyImportDetail> findByApplyQueryVo(Long applyImportId, String contractType);
}

