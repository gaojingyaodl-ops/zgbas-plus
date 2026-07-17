package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface ICtrContractApplyService extends IBaseService<CtrContractApply> {

	void saveCtrContractApply(Long contractId, Long enterpriseId);

	void updateCtrContractApply(CtrContractApplyVo vo) throws ApplicationException;

	CtrContractApply findByContractId(Long contractId);
}
