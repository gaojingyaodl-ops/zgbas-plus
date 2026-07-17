package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyImportDetail;
import com.spt.bas.client.vo.ApplyImportQueryVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/importDetai",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyImportDetailClient extends BaseClient<ApplyImportDetail> {
	@PostMapping("/findByApplyImportId")
	public List<ApplyImportDetail> findByApplyImportId(@RequestBody ApplyImportQueryVo vo);
	
	@PostMapping("findByApplyQueryVo")
	public List<ApplyImportDetail> findByApplyQueryVo(@RequestBody ApplyImportQueryVo vo);
	
	@PostMapping("findByContractId")
	public ApplyImportDetail findByContractId(@RequestBody Long contractId);
	
	@PostMapping("findByApproveId")
	public List<ApplyImportDetail> findByApproveId(@RequestBody Long approveId);
}

