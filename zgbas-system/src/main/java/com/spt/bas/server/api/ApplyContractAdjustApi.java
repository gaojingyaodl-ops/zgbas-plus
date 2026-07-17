package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyContractAdjust;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyContractAdjustService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/contractAdjust")
public class ApplyContractAdjustApi extends BaseApi<ApplyContractAdjust> {
	@Autowired
	private IApplyContractAdjustService applyContractAdjustService;

	@Override
	public IBaseService<ApplyContractAdjust> getService() {
		return applyContractAdjustService;
	}

	@PostMapping("updateSellFileId")
	public void updateSellFileId(@RequestBody FileIdUpdateVo vo){
		applyContractAdjustService.updateSellFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("updateBuyFileId")
	public void updateBuyFileId(@RequestBody FileIdUpdateVo vo){
		applyContractAdjustService.updateBuyFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		applyContractAdjustService.updateFileId(vo.getId(), vo.getFileId());
	}

}

