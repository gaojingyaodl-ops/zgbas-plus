package com.spt.bas.server.api;

import com.spt.tools.core.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyCancel;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyCancelDetailService;
import com.spt.bas.server.service.IApplyCancelService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/cancel")
public class ApplyCancelApi extends BaseApi<ApplyCancel> {
	@Autowired
	private IApplyCancelService applyCancelService;
	@Autowired
	private IApplyCancelDetailService applyCancelDetailService;
	
	@Override
	public IBaseService<ApplyCancel> getService() {
		return applyCancelService;
	}
	
	@PostMapping("queryDetailPage")
	Page<ApplyCancelDetail> queryDetailPage(@RequestBody PageSearchVo searchVo){
		return applyCancelDetailService.findPage(searchVo);
	}
	
	@PostMapping("deleteDetail")
	void deleteDetail(@RequestBody Long id) throws ApplicationException {
		applyCancelDetailService.delete(id);
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		applyCancelService.updateFileId(vo.getId(), vo.getFileId());
	}
	
}

