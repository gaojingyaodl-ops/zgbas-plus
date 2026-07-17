package com.spt.bas.web.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.tools.core.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FormConfigUtil {

	public static List<SysDictDataSdk> findByTypeCd(Map<String, Map<String, BsTemplateConfig>> map, String templateContentKey) {
		// TODO Auto-generated method stub
		java.util.Iterator it = map.entrySet().iterator();
        java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
        String key =(String)entry.getKey();     //返回对应的键
		BsTemplateConfig tc = map.get(key).get(key);
		String content = tc.getContent();
		TypeReference<Map<String, String>> clazz =new TypeReference<Map<String, String>>() {
		};
		Map<String, String> obj = JsonUtil.json2Object(clazz, content);
		String values = obj.get(templateContentKey);
		List<SysDictDataSdk> dataList = new ArrayList<SysDictDataSdk>();
		for(String value:values.split(",")){
			SysDictDataSdk dict= new SysDictDataSdk();
			dict.setDictCd(value);
			dict.setDictName(DictUtil.getValue(templateContentKey, value));
			dataList.add(dict);
		}
		
		return dataList;
	}
	public static Long[] formateArray(String[] arr){
		Long[] lon = new Long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			if(StringUtils.isNotBlank(arr[i])){
				lon[i]=Long.valueOf(arr[i]);
			}
		}
		return lon;
	}
	public static String[] removeArrayEmptyTextBackNewArray(String[] strArray) {
        List<String> strList= Arrays.asList(strArray);
        List<String> strListNew=new ArrayList<>();
        for (int i = 0; i <strList.size(); i++) {
            if (strList.get(i)!=null&&!strList.get(i).equals("")){
                strListNew.add(strList.get(i));
            }
        }
        String[] strNewArray = strListNew.toArray(new String[strListNew.size()]);
        return   strNewArray;
    }
}
