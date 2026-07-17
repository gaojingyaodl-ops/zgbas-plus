package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.pm.entity.PmApproveContents;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/contents",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPmApproveContentsClient extends BaseClient<PmApproveContents> {
	
	@RequestMapping(value = "updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);

	@RequestMapping(value = "findByApproveId")
	public PmApproveContents findByApproveId(@RequestBody Long approveId);

	@RequestMapping(value = "findByRealApproveId")
	List<PmApproveContents> findByRealApproveId(@RequestBody Long approveId);
}

