package com.spt.pm.service;

import com.spt.pm.entity.PmApproveContents;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IPmApproveContentsService extends IBaseService<PmApproveContents> {

	public void updateFileId(Long id, String fileId);

	PmApproveContents findByApproveId(Long approveId);

	List<PmApproveContents> findByRealApproveId(Long approveId);
	
}

