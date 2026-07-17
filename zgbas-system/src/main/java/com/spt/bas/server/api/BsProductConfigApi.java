package com.spt.bas.server.api;

import com.spt.bas.server.service.IBsProductConfigService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.vo.BsProductConfigVo;


@RestController
@RequestMapping(value = "bs/productConfig")
public class BsProductConfigApi extends BaseApi<BsProductConfig> {
	@Autowired
	private IBsProductConfigService bsProductConfigService;
	
	@Override
	public IBaseService<BsProductConfig> getService() {
		return bsProductConfigService;
	}
	
	@RequestMapping("getProductConfig")
	public BsProductConfigVo getProductConfig(@RequestBody BsProductConfig bsProductConfig) {
		BsProductConfigVo configValue = bsProductConfigService.getConfigValue(bsProductConfig.getConfigKey(),
				bsProductConfig.getEnterpriseId());
		return configValue;
	}

	/**
	 * 验证该合同是否可以批量发起付款申请
	 *
	 * @param ctrContractId
	 * @return
	 */
	@RequestMapping("verifyBatchPayApply")
	public boolean verifyBatchPayApply(@RequestBody Long ctrContractId) {
		return bsProductConfigService.verifyBatchPayApply(ctrContractId);
	}
}

