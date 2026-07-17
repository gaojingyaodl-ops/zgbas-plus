package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.vo.BasBrandSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/brand",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBasBrandClient extends BaseClient<BasBrand> {
	
	
	@PostMapping("findBrandNumberList")
	List<String> findBrandNumberList(@RequestBody BasBrandSearchVo vo);
	
	@PostMapping("findsBrand")
	List<BasBrand> findsBrand(@RequestBody BasBrandSearchVo vo);

	@PostMapping("findSafeBrand")
	List<BasBrand> findSafeBrand();

	@PostMapping("findBrand")
	List<BasBrand> findBrand();
}

