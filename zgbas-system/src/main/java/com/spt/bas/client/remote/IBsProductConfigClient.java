package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.vo.BsProductConfigVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/productConfig",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsProductConfigClient extends BaseClient<BsProductConfig> {

	@RequestMapping("getProductConfig")
	BsProductConfigVo getProductConfig(@RequestBody BsProductConfig bsProductConfig);

	/**
	 * 验证该合同是否可以批量发起付款申请
	 * @param ctrContractId
	 * @return
	 */
	@RequestMapping("verifyBatchPayApply")
	boolean verifyBatchPayApply(@RequestBody Long ctrContractId);
	
}

