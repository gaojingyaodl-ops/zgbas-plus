package com.spt.bas.server.api;

import com.spt.bas.server.service.IApplyCompanyTitleService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyCompanyTitle;
import com.spt.bas.client.vo.FileIdUpdateVo;


@RestController
@RequestMapping(value = "apply/companyTitle")
public class ApplyCompanyTitleApi extends BaseApi<ApplyCompanyTitle> {
	@Autowired
	private IApplyCompanyTitleService applyCompanyTitleService;
	
	@Override
	public IBaseService<ApplyCompanyTitle> getService() {
		return applyCompanyTitleService;
	}
	
	@PostMapping("updateFileId")
	void updateFileId(@RequestBody FileIdUpdateVo vo){
		applyCompanyTitleService.updateFileId(vo.getId(), vo.getFileId());
	}
	
}

