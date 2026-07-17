package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyPresell;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyPresellService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/presell")
public class ApplyPresellApi extends BaseApi<ApplyPresell> {
	@Autowired
	private IApplyPresellService applyPresellService;
	
	@Override
	public IBaseService<ApplyPresell> getService() {
		return applyPresellService;
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		applyPresellService.updateFileId(vo.getId(), vo.getFileId());
	}
	
}

