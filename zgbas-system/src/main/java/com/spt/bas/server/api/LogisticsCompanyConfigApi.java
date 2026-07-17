package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractOphis;
import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.service.LogisticsCompanyConfigService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "ctr/logisticsCompanyConfig")
public class LogisticsCompanyConfigApi extends BaseApi<LogisticsCompanyConfig> {
	@Autowired
	private LogisticsCompanyConfigService logisticsCompanyConfigService;
	
	@Override
	public IBaseService<LogisticsCompanyConfig> getService() {
		return logisticsCompanyConfigService;
	}




	@PostMapping(value = "getByCarrier")
	public LogisticsCompanyConfig getByCarrier(@RequestBody LogisticsCompanyConfig logisticsCompanyConfig){
		return logisticsCompanyConfigService.getByCarrier(logisticsCompanyConfig);
	}

	@PostMapping(value = "findByOurCompanyNames")
	List<LogisticsCompanyConfig> findByOurCompanyNames(@RequestBody String ourCompanyName){
		return logisticsCompanyConfigService.findByOurCompanyNames(ourCompanyName);
	}


}

