package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.*;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME
		+ "/pm/approve", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IPmApproveClient extends BaseClient<PmApprove> {

	@PostMapping("getApproveVo")
	public PmApproveVo getApproveVo(@RequestBody Long approveId);

	@PostMapping("startFlow")
	public PmApprove startFlow(@RequestBody PmApproveSaveVo startVo) throws WebApplicationException;

	@PostMapping("doStepFlow")
	public PmApprove doStepFlow(@RequestBody PmApproveStepFlowVo vo);

	@PostMapping("doBatchStepFlow")
	void doBatchStepFlow(@RequestBody PmApproveStepFlowVo vo);

	@PostMapping("findPageApprove")
	public PageDown<PmApproveDownVo> findPageApprove(@RequestBody PmApproveSearchVo queryVo);

	@PostMapping("getEntityVo")
	public PmApproveDownVo getEntityVo(@RequestBody Long id);

	@PostMapping("doWithdraw")
	public void doWithdraw(@RequestBody PmApproveWithdrawVo vo);

	@PostMapping("doRetrieve")
	public void doRetrieve(@RequestBody PmApproveRetrieveVo vo);

	@PostMapping("findByApproveNo")
	PmApprove findByApproveNo(@RequestBody String approveNo);

	@PostMapping("findApproveNoByApproveId")
	PmApprove findApproveNoByApproveId(@RequestBody Long approveId);

	@PostMapping("deleteRecord")
	void deleteRecord(@RequestBody Long approveId);

	@GetMapping("resServerPmApprove")
	RespVo<?> resServerPmApprove(@RequestParam(value = "param", required = false) String param);
}
