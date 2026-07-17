package com.spt.bas.server.api;


import com.spt.bas.client.entity.PenaltyInterest;
import com.spt.bas.server.service.IPenaltyInterestService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/bs/penaltyInterest")
public class PenaltyInterestApi extends BaseApi<PenaltyInterest> {
	@Autowired
	private IPenaltyInterestService penaltyInterestService;


	@Override
	public IDataService<PenaltyInterest> getService() {
		return penaltyInterestService;
	}

	@RequestMapping(value = "updateInterStatus")
	public void updateInterStatus(@RequestBody PenaltyInterest vo) {
		penaltyInterestService.updateInterStatus(vo.getInterestStatus(),vo.getBizId());
	}


	@RequestMapping(value = "findContractNoByCompanyId")
	public List<String> findContractNoByCompanyId(@RequestBody String companyId) {
		return penaltyInterestService.findContractNoByCompanyId(companyId);
	}
}
