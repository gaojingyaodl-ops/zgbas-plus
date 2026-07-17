package com.spt.bas.server.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.PushToSaas;
import com.spt.bas.client.vo.SaasApplyBuyRequestVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.PushToSaasDao;
import com.spt.bas.server.service.IPushToSaasService;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.http.util.HTTPUtility;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class PushToSaasServiceImpl extends BaseService<PushToSaas> implements IPushToSaasService {
	private Logger logger = LoggerFactory.getLogger(PushToSaasServiceImpl.class);
	@Autowired
	private PushToSaasDao pushToSaasDao;
	
	@Override
	public BaseDao<PushToSaas> getBaseDao() {
		return pushToSaasDao;
	}
	
	@Override
	public Class<PushToSaas> getEntityClazz() {
		return PushToSaas.class;
	}

	@Override
	@ServerTransactional
	public void pushDataToSaas() {
		//查询未推送成功的记录
		List<PushToSaas> pushList = pushToSaasDao.findByPushFlg(false);
		//logger.info(">>>>>>>>定时推送未推送成功的数据<<<<<<<<", "未推送成功的数据量为："+pushList.size());
		if(!pushList.isEmpty()){
			for (PushToSaas pushToSaas : pushList) {
				SaasApplyBuyRequestVo vo = new SaasApplyBuyRequestVo();
				vo.setContractNo(pushToSaas.getSaasContractNo());
				vo.setStatus(pushToSaas.getSaasContractStatus());
				try {
					String statusJson = HTTPUtility.doPostBody(PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL)+"/open/off/contract/advanceClinchDealRequest", vo, null);
					//推送成功后修改推送状态值
					JSONObject jsonObj = JSONObject.parseObject(statusJson);
					String code = jsonObj.getString("code");
					if(code.equals("200")){
						String objJsonString = jsonObj.getString("data");
						vo =JsonUtil.json2Object(SaasApplyBuyRequestVo.class, objJsonString);
						if(StringUtils.equals(vo.getRetCode(), "200")){
							pushToSaas.setPushFlg(true);
						}
						pushToSaasDao.save(pushToSaas);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
}

