package com.spt.bas.server.service;

import com.spt.tools.jpa.service.IBaseService;

import com.spt.bas.client.entity.ApplyCompanyTitle;

public interface IApplyCompanyTitleService extends IBaseService<ApplyCompanyTitle> {
	void updateFileId(Long id, String fileId);
}

