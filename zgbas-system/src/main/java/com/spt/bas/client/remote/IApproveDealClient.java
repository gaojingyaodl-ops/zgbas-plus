package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApproveDeal;
import com.spt.bas.client.vo.ApproveDealQueryVo;
import com.spt.bas.client.vo.ApproveDealSerachVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/approve/deal",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApproveDealClient extends BaseClient<ApproveDeal> {
	@PostMapping(value = "findPageVo")
	public PageDown<ApproveDealQueryVo> findPageVo(@RequestBody ApproveDealSerachVo queryVo);
	
}

