package com.spt.bas.client.remote;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.bas.client.vo.DictDataVo;
import com.spt.bas.client.vo.TemplateQueryVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/templateConfig",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsTemplateConfigClient extends BaseClient<BsTemplateConfig> {
	@PostMapping("templateMap")
	public Map<String, List<DictDataVo>> getTemplateMap(@RequestBody TemplateQueryVo queryVo);
	@PostMapping("templateByDictCd")
	public List<DictDataVo> getTemplateByDictCd(@RequestBody TemplateQueryVo queryVo);
	@PostMapping("findTemplateValue")
	public  Map<String, String> findTemplateValue(@RequestBody TemplateQueryVo queryVo);
}

