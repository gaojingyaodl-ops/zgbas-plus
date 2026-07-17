package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/area",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsAreaClient extends BaseClient<BsArea> {


	@PostMapping("findTopLevel")
	List<BsArea> findTopLevel();
	
	@PostMapping("findByParentId")
	List<BsArea> findByParentId(String pid);
	
	@PostMapping("findByCode")
	List<BsArea> findByCode(String pid);
	
	@PostMapping("getAreaVo")
	public CompanyAreaVo getAreaVo(@RequestBody Long id);

	@PostMapping("getAllArea")
	List<BsArea> getAllArea();
	
}

