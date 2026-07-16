package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyCancel;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyCancelDao extends BaseDao<ApplyCancel> {
	
	@Transactional
	@Modifying
	@Query("update ApplyCancel t set t.fileId =?2 where t.id=?1")
	void updateFileId(Long id, String fileId);
	
	@Query("SELECT a FROM ApplyCancel a,ApplyCancelDetail d WHERE a.id = d.applyCancelId AND d.oldApproveNo =?1 AND a.status = 'A'")
	List<ApplyCancel> findByOldApproveNo(String oldApproveNo);
}

