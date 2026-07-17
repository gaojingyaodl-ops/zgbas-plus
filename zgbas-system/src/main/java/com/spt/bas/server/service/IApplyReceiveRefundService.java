package com.spt.bas.server.service;

import com.spt.tools.jpa.service.IBaseService;

import com.spt.bas.client.entity.ApplyReceiveRefund;

public interface IApplyReceiveRefundService extends IBaseService<ApplyReceiveRefund> {
	void updateFileId(Long id, String fileId);
}

