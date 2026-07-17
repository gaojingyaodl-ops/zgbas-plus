package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContractLoss;
import com.spt.tools.jpa.service.IBaseService;

public interface ICtrContractLossService extends IBaseService<CtrContractLoss> {
	void updateFileId(Long id, String fileId);

	void updateEnableFlg(Long id, Boolean enableFlg,Long contractId);

	int updateContractLoss(CtrContractLoss vo);

	CtrContractLoss findByContractId(Long contractId);
}

