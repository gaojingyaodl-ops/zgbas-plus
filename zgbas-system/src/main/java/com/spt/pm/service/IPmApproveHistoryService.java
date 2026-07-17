package com.spt.pm.service;

import java.util.List;

import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveHistory;
import com.spt.pm.entity.PmApproveStep;
import com.spt.tools.jpa.service.IBaseService;

public interface IPmApproveHistoryService extends IBaseService<PmApproveHistory> {

	void addHistory(PmApprove approve, PmApproveStep step);

	List<PmApproveHistory> findByApproveId(Long approveId);

	List<PmApproveHistory> findByApproveIdOrProcessId(Long approveId, Long processId, Long enterpriseId);

}

