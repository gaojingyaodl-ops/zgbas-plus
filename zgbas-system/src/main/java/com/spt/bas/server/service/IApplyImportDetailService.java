package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.ApplyImportDetail;
import com.spt.bas.client.vo.ApplyImportQueryVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyImportDetailService extends IBaseService<ApplyImportDetail> {
	public List<ApplyImportDetail> findByApplyImportId(ApplyImportQueryVo vo);
	
	ApplyImportDetail findByContractId(Long contractId);
	
	void updateApplyStatus(Long contractId);
	
	public List<ApplyImportDetail> findByApplyQueryVo(ApplyImportQueryVo vo);
	
	public List<ApplyImportDetail> findByApproveId(Long approveId);
}

