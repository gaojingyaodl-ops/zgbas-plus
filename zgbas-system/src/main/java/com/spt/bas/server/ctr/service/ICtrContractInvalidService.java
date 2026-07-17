package com.spt.bas.server.ctr.service;

import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.tools.core.exception.ApplicationException;

public interface ICtrContractInvalidService {

	void invalidTheContract(CtrConctractInvalidVo vo) throws ApplicationException;

}
