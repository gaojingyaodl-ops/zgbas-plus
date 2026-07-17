package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancel;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(qualifier="applyCancelClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/cancel",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyCancelClient extends BaseClient<ApplyCancel> {
	@PostMapping("queryDetailPage")
	PageDown<ApplyCancelDetail> queryDetailPage(@RequestBody PageSearchVo searchVo);
	
	@PostMapping("deleteDetail")
	void deleteDetail(@RequestBody Long id);
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);
	
}

