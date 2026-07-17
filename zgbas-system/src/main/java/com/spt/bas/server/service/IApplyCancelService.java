package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCancel;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyCancelService extends IBaseService<ApplyCancel> {

	/**
	 * 更新附件ID
	 * @param id 合同模板ID
	 * @param fileId 附件ID
	 */
	public void updateFileId(Long id, String fileId);
	
}

