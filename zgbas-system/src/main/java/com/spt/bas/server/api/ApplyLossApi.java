package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyLoss;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyLossService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/loss")
public class ApplyLossApi extends BaseApi<ApplyLoss> {
	@Autowired
	private IApplyLossService applyLossService;

	@Override
	public IBaseService<ApplyLoss> getService() {
		return applyLossService;
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		applyLossService.updateFileId(vo.getId(), vo.getFileId());
	}
}

