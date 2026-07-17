package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.vo.BasBrandSearchVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasBrandService extends IBaseService<BasBrand> {

	List<String> findBrandNumberList(BasBrandSearchVo vo);

	List<BasBrand> findsBrand(BasBrandSearchVo vo);

	List<BasBrand> findSafeBrand();

	/**牌号收集*/
	void saveBrand(String productCode, String brandNumber, Long enterpriseId);

	List<BasBrand> findBrand();
}

