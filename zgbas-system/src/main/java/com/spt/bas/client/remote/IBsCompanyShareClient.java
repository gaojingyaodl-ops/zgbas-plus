package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyShare",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsCompanyShareClient extends BaseClient<BsCompanyShare> {
	
	@PostMapping("findByCompanyIdAndSharedUserId")
	BsCompanyShare findByCompanyIdAndSharedUserId(@RequestBody BsCompanyShare share);
	
	@PostMapping("findByCompanyIdAndCreateUserId")
	List<BsCompanyShare> findByCompanyIdAndCreateUserId(@RequestBody BsCompanyShare share);

	@PostMapping("findBySharedUserId")
	List<BsCompanyShare> findBySharedUserId(@RequestBody BsCompanyShare share);
	
	@PostMapping("findByCompanyId")
	List<BsCompanyShare> findByCompanyId(@RequestBody BsCompanyShare share);
}

