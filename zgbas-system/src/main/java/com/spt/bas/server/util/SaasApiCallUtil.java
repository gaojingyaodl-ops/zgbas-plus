package com.spt.bas.server.util;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.PushToSaas;
import com.spt.bas.client.vo.SaasApplyBuyRequestVo;
import com.spt.bas.client.vo.SaasApproveStatusVo;
import com.spt.bas.server.dao.PushToSaasDao;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.http.util.HTTPUtility;

public class SaasApiCallUtil {
	
	/**
	 * 审批完成或者驳回调用saas
	 * */
	public static void doRequestSaas(SaasApproveStatusVo vo,String url){
		
			//添加PushToSaas记录
			PushToSaas push = new PushToSaas();
			push.setPushType(vo.getType());
			push.setSaasContractNo(vo.getContractNo());
			push.setSaasContractStatus(vo.getStatus());
			
			try {
				String statusJson = HTTPUtility.doPostBody(PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL)+url, vo, null);
				//推送成功后修改推送状态值
				JSONObject jsonObj = JSONObject.parseObject(statusJson);
				String code = jsonObj.getString("code");
				if(code.equals("200")){
					String objJsonString = jsonObj.getString("data");
					SaasApplyBuyRequestVo responseVo =JsonUtil.json2Object(SaasApplyBuyRequestVo.class, objJsonString);
					if(StringUtils.equals(responseVo.getRetCode(), "200")){
						push.setPushFlg(true);
					}
				}
				PushToSaasDao pushToSaasDao = SpringContextHolder.getBean(PushToSaasDao.class);
				pushToSaasDao.save(push);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
