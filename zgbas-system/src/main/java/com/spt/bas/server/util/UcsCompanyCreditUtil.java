package com.spt.bas.server.util;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.vo.UcsCreditReceiveVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.http.util.HTTPUtility;

/**
 * 企业授信额度推送 
 *
 */
public class UcsCompanyCreditUtil {
	public static void doRequestUcs(BsCompany bsCompany, String url) {

		UcsCreditReceiveVo reqVo = new UcsCreditReceiveVo();
		reqVo.setAdjustCreditAmount(bsCompany.getTotalCreditAmount());
		reqVo.setHaveusedAmount(bsCompany.getUsedCreditAmount());
		BigDecimal remainingAmount = bsCompany.getTotalCreditAmount().subtract(bsCompany.getUsedCreditAmount());
		reqVo.setRemainingAmount(remainingAmount);
		reqVo.setCompanyName(bsCompany.getCompanyName());
		reqVo.setAppCode("bps");

		try {
			String statusJson = HTTPUtility.doPostBody(PropertiesUtil.getProperty(BasConstants.UCS_PUSH_URL) + url,reqVo, null);
			JSONObject jsonObj = JSONObject.parseObject(statusJson);
			String code = jsonObj.getString("code");
			String message=jsonObj.getString("message");
			if (!code.equals("200")) {
				throw new ApplicationException(message);
			}
			System.out.println("====================>>>>>>"+JsonUtil.obj2Json(reqVo));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
