package com.spt.bas.server.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.bas.client.vo.DictDataVo;
import com.spt.bas.client.vo.TemplateQueryVo;
import com.spt.bas.server.service.IBsTemplateConfigService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/templateConfig")
public class BsTemplateConfigApi extends BaseApi<BsTemplateConfig> {
	@Autowired
	private IBsTemplateConfigService bsTemplateConfigService;
	
	@Override
	public IBaseService<BsTemplateConfig> getService() {
		return bsTemplateConfigService;
	}
	
	@PostMapping("templateMap")
	public Map<String, List<DictDataVo>> getTemplateMap(@RequestBody TemplateQueryVo queryVo){
		return bsTemplateConfigService.getTemplateMap(queryVo);
	}
	
	@PostMapping("templateByDictCd")
	public List<DictDataVo> getTemplateByDictCd(@RequestBody TemplateQueryVo queryVo){
		return bsTemplateConfigService.getTemplateByDictCd(queryVo);
	}
	
	@PostMapping("findTemplateValue")
	public  Map<String, String> findTemplateValue(@RequestBody TemplateQueryVo queryVo){
		return bsTemplateConfigService.findTemplateValue(queryVo);
	}
	
	
}

