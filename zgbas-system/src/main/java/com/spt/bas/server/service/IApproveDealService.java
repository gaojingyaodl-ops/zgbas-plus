package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.ApproveDeal;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApproveDealRequest;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;

public interface IApproveDealService extends IBaseService<ApproveDeal> {

	void addApproveDeal(PmApprove approve);
	void removeApproveDeal(Long processId, String contractId);
	void updateSubject(ApproveDealRequest request);

	void dueToRemind(List<CtrContract> repaymentList);
	void removeApproveDeal(Long contractId);

}

