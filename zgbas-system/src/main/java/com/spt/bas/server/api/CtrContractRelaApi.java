package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrContractRela;
import com.spt.bas.server.service.ICtrContractRelaService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "ctr/contractRela")
public class CtrContractRelaApi extends BaseApi<CtrContractRela> {
	@Autowired
	private ICtrContractRelaService ctrContractRelaService;

	@Override
	public IBaseService<CtrContractRela> getService() {
		return ctrContractRelaService;
	}

	/**
	 * 通过sellContractId获取合同关联关系
	 * @param contractId
	 * @return
	 */
	@PostMapping("getRelaBySellContractId")
	public CtrContractRela getRelaBySellContractId(@RequestBody Long contractId) {
		return ctrContractRelaService.getRelaBySellContractId(contractId);
	}

}

