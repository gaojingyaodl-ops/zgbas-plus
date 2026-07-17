package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.vo.BasBrandSearchVo;
import com.spt.bas.server.service.IBasBrandService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bas/brand")
public class BasBrandApi extends BaseApi<BasBrand> {
	@Autowired
	private IBasBrandService basBrandService;
	
	@Override
	public IBaseService<BasBrand> getService() {
		return basBrandService;
	}
	
	@PostMapping("findBrandNumberList")
	List<String> findBrandNumberList(@RequestBody BasBrandSearchVo vo){
		return basBrandService.findBrandNumberList(vo);
	}
	
	@PostMapping("findsBrand")
	public List<BasBrand> findsBrand(@RequestBody BasBrandSearchVo vo){
		return basBrandService.findsBrand(vo);
	}

	@PostMapping("findSafeBrand")
	public List<BasBrand> findSafeBrand(){
		return basBrandService.findSafeBrand();
	}
	@PostMapping("findBrand")
	public List<BasBrand> findBrand(){
		return basBrandService.findBrand();
	}

}

