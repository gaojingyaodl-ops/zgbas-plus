package com.spt.pm.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.pm.entity.PmApproveContents;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

public interface PmApproveContentsDao extends BaseDao<PmApproveContents> {
	
	@Modifying
	@Query("update PmApproveContents c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	PmApproveContents findByApproveId(Long approveId);

	List<PmApproveContents> findByRealApproveId(Long realApproveId);
}

