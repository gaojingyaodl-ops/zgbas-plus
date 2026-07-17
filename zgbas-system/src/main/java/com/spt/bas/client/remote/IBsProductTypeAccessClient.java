package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsProductTypeAccess;
import com.spt.bas.client.vo.BsProductTypeAccessSaveVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/productTypeAccess",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsProductTypeAccessClient extends BaseClient<BsProductTypeAccess> {
	
	@PostMapping("findByEnterpriseId")
	public List<BsProductTypeAccess> findByEnterpriseId(@RequestBody Long enterpriseId);
	
	@PostMapping("saveAccess")
	public void saveAccess(@RequestBody BsProductTypeAccessSaveVo vo) throws ApplicationException;
	
	@PostMapping("countByProductCdAndEnterpriseId")
	public void countByProductCdAndEnterpriseId(@RequestBody BsProductTypeAccess vo);

	@GetMapping("reFresh")
	public void reFreshCache();
}

