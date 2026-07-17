package com.spt.bas.server.util;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.spt.bas.client.vo.ParamVo;
import com.spt.bas.server.util.HttpClientUtil.ApplicationType;
import com.spt.tools.core.json.JsonUtil;

/**
 * 调用 接口 工具类
 *
 */
public class SptApiCallUtility {

	public static String sendData(String OfferContName, String methodStr, String jsonStr){

		HttpClientUtil httpClientUtil = new HttpClientUtil();
		//String url = PropertiesUtil.getProperty(OfferContName)+methodStr;
		String url = "http://192.168.2.41:8088/open/bs/new/offer/advanceClinchDealRequest";
		String str =null;
		try {
			ParamVo vo =new ParamVo();
			vo.setErrorId("0");
			vo.setObjJson(jsonStr);
			StringEntity se = new StringEntity(JsonUtil.obj2Json(vo));
			HttpPost post = httpClientUtil.createPost(url, se, null);
			post.addHeader("Content-Type", ApplicationType.JSON.val());
			str = httpClientUtil.fetchData(post);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;

	}
}
