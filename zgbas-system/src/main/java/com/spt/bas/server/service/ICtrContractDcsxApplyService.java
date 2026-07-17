package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContractDcsxApply;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface ICtrContractDcsxApplyService extends IBaseService<CtrContractDcsxApply> {

	void saveCtrContractApply(Long contractId, Long enterpriseId);

	void updateCtrContractApply(CtrContractApplyVo vo) throws ApplicationException;

	CtrContractDcsxApply findByContractId(Long contractId);
}
