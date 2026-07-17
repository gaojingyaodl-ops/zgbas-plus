package com.spt.bas.server.service;

import com.spt.tools.jpa.service.IBaseService;

import com.spt.bas.client.entity.ApplyPayRefund;

public interface IApplyPayRefundService extends IBaseService<ApplyPayRefund> {
	void updateFileId(Long id, String fileId);
}

