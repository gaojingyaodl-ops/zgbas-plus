package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyPayRefundDcsx;
import com.spt.bas.client.entity.ApplyReceiveRefundDcsx;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyReceiveRefundDcsxService extends IBaseService<ApplyReceiveRefundDcsx> {
	void updateFileId(Long id, String fileId);

	void autoApplyReceiveRefundDcsx(PmApprove approve, ApplyCtrDCSX ctrDcsx, ApplyPayRefundDcsx payRefund) throws ApplicationException;
}

