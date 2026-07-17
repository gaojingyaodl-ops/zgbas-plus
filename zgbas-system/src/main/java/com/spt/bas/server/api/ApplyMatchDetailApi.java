package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.vo.ApplyMatchQueryVo;
import com.spt.bas.server.service.IApplyMatchDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/matchDetail")
public class ApplyMatchDetailApi extends BaseApi<ApplyMatchDetail> {
	@Autowired
	private IApplyMatchDetailService applyMatchDetailService;

	@Override
	public IBaseService<ApplyMatchDetail> getService() {
		return applyMatchDetailService;
	}

	@PostMapping("findByApplyMatchId")
	public List<ApplyMatchDetail>findByApplyMatchId(@RequestBody ApplyMatchQueryVo vo){
		return applyMatchDetailService.findByApplyMatchId(vo);
	}

	@PostMapping("findByApplyQueryVo")
	public List<ApplyMatchDetail> findByApplyQueryVo(@RequestBody ApplyMatchQueryVo vo){
		return applyMatchDetailService.findByQueryVo(vo);
	}

	@PostMapping("findByApproveId")
	public List<ApplyMatchDetail> findByApproveId(@RequestBody Long approveId){
		return applyMatchDetailService.findByApproveId(approveId);
	}

	@PostMapping("findByContractNo")
	public ApplyMatchDetail findByContractNo(@RequestBody String contractNo){
		return applyMatchDetailService.findByContractNo(contractNo);
	}

}

