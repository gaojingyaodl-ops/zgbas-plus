package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyImportDetail;
import com.spt.bas.client.vo.ApplyImportQueryVo;
import com.spt.bas.server.service.IApplyImportDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/importDetai")
public class ApplyImportDetailApi extends BaseApi<ApplyImportDetail> {
	@Autowired
	private IApplyImportDetailService applyImportDetailService;
	
	@Override
	public IBaseService<ApplyImportDetail> getService() {
		return applyImportDetailService;
	}
	
	@PostMapping("/findByApplyImportId")
	public List<ApplyImportDetail> findByApplyImportId(@RequestBody ApplyImportQueryVo vo){
		return applyImportDetailService.findByApplyImportId(vo);
	}
	
	@PostMapping("findByApplyQueryVo")
	public List<ApplyImportDetail> findByApplyQueryVo(@RequestBody ApplyImportQueryVo vo){
		return applyImportDetailService.findByApplyQueryVo(vo);
	}
	
	@PostMapping("findByContractId")
	public ApplyImportDetail findByContractId(@RequestBody Long contractId) {
		return applyImportDetailService.findByContractId(contractId);
	}
	
	@PostMapping("findByApproveId")
	public List<ApplyImportDetail> findByApproveId(@RequestBody Long approveId){
		return applyImportDetailService.findByApproveId(approveId);
	}
}

