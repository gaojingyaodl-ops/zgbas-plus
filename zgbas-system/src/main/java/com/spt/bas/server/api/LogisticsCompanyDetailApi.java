package com.spt.bas.server.api;

import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.entity.LogisticsCompanyDetail;
import com.spt.bas.server.service.LogisticsCompanyDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping(value = "ctr/logisticsCompanyDetail")
public class LogisticsCompanyDetailApi extends BaseApi<LogisticsCompanyDetail> {


	@Autowired
	private LogisticsCompanyDetailService logisticsCompanyDetailService;

	@Override
	public IDataService<LogisticsCompanyDetail> getService() {
		return logisticsCompanyDetailService;
	}



	@PostMapping("findByCarrierScoreAVG")
	public BigDecimal findByCarrierScoreAVG(@RequestBody Long id) {
		return logisticsCompanyDetailService.findByCarrierScoreAVG(id);
	}

	@PostMapping("findByLogisticsCompanyId")
	public List<LogisticsCompanyDetail> findByLogisticsCompanyId(@RequestBody Long id) {
		return logisticsCompanyDetailService.findByLogisticsCompanyId(id);
	}
}

