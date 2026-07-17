package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.ApplyMatch;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.vo.ApplyMatchQueryVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyMatchDetailService extends IBaseService<ApplyMatchDetail> {
	public List<ApplyMatchDetail>findByApplyMatchId(@RequestBody ApplyMatchQueryVo vo);

	public List<ApplyMatchDetail>findByQueryVo(@RequestBody ApplyMatchQueryVo vo);

	ApplyMatchDetail findByContractId(Long contractId);

	void updateApplyStatus(Long contractId);

	public List<ApplyMatchDetail>findByApproveId(@RequestBody Long approveId);

	ApplyMatchDetail findByContractNo(@RequestBody String contractNo);


}

