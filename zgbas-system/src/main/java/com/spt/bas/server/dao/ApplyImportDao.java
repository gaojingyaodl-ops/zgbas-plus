package com.spt.bas.server.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyImport;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyImportDao extends BaseDao<ApplyImport> {
	@Modifying
	@Query("update ApplyImport c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	ApplyImport findByApproveId(Long approveId);
}

