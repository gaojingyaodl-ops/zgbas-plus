package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrServiceContract;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/service",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrServiceContractClient extends BaseClient<CtrServiceContract> {

	@RequestMapping(value = "updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);

	@RequestMapping(value = "findByCtrContract")
	public CtrServiceContract findByCtrContract(@RequestBody Long ctrContractId);
}

