package com.spt.bas.server.service;


import com.spt.bas.client.entity.CtrContractFollow;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface ICtrContractFollowService extends IBaseService<CtrContractFollow> {
	void toNotify(CtrContractFollow follow);

	List<CtrContractFollow> findByCtrContractId(Long ctrContractId);
}
