package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractOphis;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "ctr/contractOphis")
public class CtrContractOphisApi extends BaseApi<CtrContractOphis> {
	@Autowired
	private ICtrContractOphisService ctrContractOphisService;
	
	@Override
	public IBaseService<CtrContractOphis> getService() {
		return ctrContractOphisService;
	}

	@PostMapping(value = "addHis")
	public void addHis(@RequestBody CtrContractOphisRequest request){
		ctrContractOphisService.addHis(request);
	}

	@PostMapping(value = "getBusinessDelivery")
	public BusinessDeliveryExcelVo getBusinessDelivery(@RequestBody Long approveId){
		return ctrContractOphisService.getBusinessDelivery(approveId);
	}
	// 添加合同盖章发起记录
	@PostMapping(value = "addSealStartHis")
	public void addSealStartHis(@RequestBody PmApprove approve){
		ctrContractOphisService.addSealStartHis(approve);
	}
}

