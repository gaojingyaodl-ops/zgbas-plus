package com.spt.bas.server.service;

import java.util.List;
import java.util.Map;

import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.bas.client.vo.DictDataVo;
import com.spt.bas.client.vo.TemplateQueryVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsTemplateConfigService extends IBaseService<BsTemplateConfig> {

	List<BsTemplateConfig> findByTemplateCat(String templateCat);

	Map<String, List<DictDataVo>> getTemplateMap(TemplateQueryVo queryVo);

	List<DictDataVo> getTemplateByDictCd(TemplateQueryVo queryVo);

	 Map<String, String> findTemplateValue(TemplateQueryVo queryVo);
	
}

