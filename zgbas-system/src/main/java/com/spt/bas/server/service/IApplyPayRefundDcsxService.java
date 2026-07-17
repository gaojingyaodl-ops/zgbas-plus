package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyPayRefundDcsx;
import com.spt.bas.client.vo.protocol.SupplementaryAgreement;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyPayRefundDcsxService extends IBaseService<ApplyPayRefundDcsx> {
	void updateFileId(Long id, String fileId);

	void autoStartRefundWithProtocolDocument(SupplementaryAgreement agreement, PmApprove approve);
}

