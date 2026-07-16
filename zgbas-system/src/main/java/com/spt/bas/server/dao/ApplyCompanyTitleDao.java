package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyCompanyTitle;

public interface ApplyCompanyTitleDao extends BaseDao<ApplyCompanyTitle> {
	
	@Modifying
	@Query("update ApplyCompanyTitle a set a.fileId =?2 where a.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	@Query("from ApplyCompanyTitle where contractId = ?1 and status != 'N'")
	List<ApplyCompanyTitle> findByContractId(Long contractId);
}

