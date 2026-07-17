package com.spt.bas.server.util;

import com.alibaba.druid.support.json.JSONUtils;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.bas.client.vo.DictDataVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormConfigUtil {

	public static Map<String, List<DictDataVo>> findByTypeCd(String templateCat,String typeCd,String lang, List<String> dictCds) {
		Map<String, List<DictDataVo>> rtn = new HashMap<String, List<DictDataVo>>();
		// TODO Auto-generated method stub
		BsTemplateConfig template =TemplateContentUtility.findTemplate(templateCat, "FMC_"+typeCd, lang);
		if(template!=null){
			String content = template.getContent();
			Map<String, String> obj = (Map<String, String>)JSONUtils.parse(content);
			for(String dictCd:dictCds){
				String values =(String) obj.get(dictCd);
				List<DictDataVo> dataList = new ArrayList<DictDataVo>();
				for(String value:values.split(",")){
					DictDataVo dict= new DictDataVo();
					dict.setDictCd(value);
					dict.setDictName(DictUtil.getValue(dictCd, value));
					dataList.add(dict);
				}
				rtn.put(dictCd, dataList);
			}
		}
		return rtn;
	}

	public static List<DictDataVo> findByTypeCd(String templateCat, String typeCd, String lang, String dictCd) {
		// TODO Auto-generated method stub
		List<DictDataVo> dataList = new ArrayList<DictDataVo>();
		BsTemplateConfig template =TemplateContentUtility.findTemplate(templateCat, "FMC_"+typeCd, lang);
		if(template!=null){
			String content = template.getContent();
			Map<String, String> obj = (Map<String, String>)JSONUtils.parse(content);
			String values =(String) obj.get(dictCd);
			for(String value:values.split(",")){
				DictDataVo dict= new DictDataVo();
				dict.setDictCd(value);
				dict.setDictName(DictUtil.getValue(dictCd, value));
				dataList.add(dict);
			}
		}
		return dataList;
	}

	public static Map<String, String> findTemplateValue(String templateCat, String typeCd) {
		BsTemplateConfig template =TemplateContentUtility.findTemplate(templateCat, "FMC_"+typeCd, "CH");
		if(template!=null){
			String content = template.getContent();
			Map<String, String> obj = (Map<String, String>)JSONUtils.parse(content);
			return obj;
		}
		return null;
	}

}
