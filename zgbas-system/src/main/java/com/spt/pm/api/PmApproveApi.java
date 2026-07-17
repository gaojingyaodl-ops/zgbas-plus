package com.spt.pm.api;

import com.spt.auth.sdk.cache.UserCache;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.*;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "pm/approve")
public class PmApproveApi extends BaseApi<PmApprove> {
	@Autowired
	private IPmApproveService pmApproveService;

	@Override
	public IBaseService<PmApprove> getService() {
		return pmApproveService;
	}

	@PostMapping("getApproveVo")
	public PmApproveVo getApproveVo(@RequestBody Long approveId) {
		return pmApproveService.getApproveVo(approveId);
	}

	@PostMapping("startFlow")
	public PmApprove startFlow(@RequestBody PmApproveSaveVo startVo) throws ApplicationException{
		return pmApproveService.startFlow(startVo);
	}

	@PostMapping("doStepFlow")
	public PmApprove doStepFlow(@RequestBody PmApproveStepFlowVo vo) throws ApplicationException{
		return pmApproveService.doStepFlow(vo);
	}

	@PostMapping("doBatchStepFlow")
	public void doBatchStepFlow(@RequestBody PmApproveStepFlowVo vo) throws ApplicationException{
		List<Long> approveIds = vo.getApproveIds();
		for (Long approveId : approveIds) {
			vo.setApproveId(approveId);
			pmApproveService.doStepFlow(vo);
		}
	}

	@PostMapping("findPageApprove")
	public Page<PmApproveDownVo> findPageApprove(@RequestBody PmApproveSearchVo queryVo){
		return pmApproveService.findPageApprove(queryVo);
	}

	@PostMapping("getEntityVo")
	public PmApproveDownVo getEntityVo(@RequestBody Long id) {
		PmApprove entity = pmApproveService.getEntity(id);
		PmApproveDownVo vo =new PmApproveDownVo();
		try {
			PropertyUtils.copyProperties(vo, entity);
		} catch (Exception e) {
			logger.warn("copyProperties",e);
		}
		vo.setCurrApproverUserName(UserCache.getUserName(vo.getCurrApproverUserId()));
		return vo;
	}

	@PostMapping("doWithdraw")
	public void doWithdraw(@RequestBody PmApproveWithdrawVo vo) throws ApplicationException {
		pmApproveService.doWithdraw(vo);
	}

	@PostMapping("doRetrieve")
	public void doRetrieve(@RequestBody PmApproveRetrieveVo vo) throws ApplicationException {
		pmApproveService.doRetrieve(vo);
	}

	@PostMapping("findByApproveNo")
	public PmApprove findByApproveNo(@RequestBody String approveNo) throws ApplicationException {
		return pmApproveService.findByApproveNo(approveNo);
	}

	@PostMapping("findApproveNoByApproveId")
	public PmApprove findApproveNoByApproveId(@RequestBody Long approveId) throws ApplicationException{
		return pmApproveService.findApproveNoByApproveId(approveId);
	}
	@PostMapping("deleteRecord")
	void deleteRecord(@RequestBody Long approveId){
		pmApproveService.deleteRecord(approveId);
	}

	@GetMapping("resServerPmApprove")
	RespVo<?> resServerPmApprove(@RequestParam(value = "param", required = false) String param) {
		if (StringUtils.isBlank(param)) {
			RespVo<Object> result = new RespVo<>();
			result.setFail("参数为空！", "500");
			return new RespVo<>();
		}
		return pmApproveService.resServerPmApprove(param);
	}
}

