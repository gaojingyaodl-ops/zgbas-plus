package com.spt.bas.server.api;

import com.spt.bas.server.service.IApplyServiceContractService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyServiceContract;
import com.spt.bas.client.vo.FileIdUpdateVo;


@RestController
@RequestMapping(value = "apply/service")
public class ApplyServiceContractApi extends BaseApi<ApplyServiceContract> {
	@Autowired
	private IApplyServiceContractService applyServiceContractService;
	
	@Override
	public IBaseService<ApplyServiceContract> getService() {
		return applyServiceContractService;
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		applyServiceContractService.updateFileId(vo.getId(), vo.getFileId());
	}
	
	@PostMapping("findByContractId")
	public ApplyServiceContract findByContractId(@RequestBody Long contractId) {
		return applyServiceContractService.findByContractId(contractId);
	}
	
}

