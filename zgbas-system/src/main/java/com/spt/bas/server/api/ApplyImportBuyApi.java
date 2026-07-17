package com.spt.bas.server.api;

import com.spt.bas.server.service.IApplyImportBuyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyImportBuy;
import com.spt.bas.client.vo.FileIdUpdateVo;


@RestController
@RequestMapping(value = "apply/importBuy")
public class ApplyImportBuyApi extends BaseApi<ApplyImportBuy> {
	@Autowired
	private IApplyImportBuyService applyImportBuyService;
	
	@Override
	public IBaseService<ApplyImportBuy> getService() {
		return applyImportBuyService;
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		applyImportBuyService.updateFileId(vo.getId(), vo.getFileId());
	}
	
	@PostMapping("findByContractId")
	public ApplyImportBuy findByContractId(@RequestBody Long contractId) {
		return applyImportBuyService.findByContractId(contractId);
	}
	
}

