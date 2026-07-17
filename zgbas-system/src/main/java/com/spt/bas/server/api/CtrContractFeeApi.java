package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrContractFee;
import com.spt.bas.client.vo.ContractFeeSearchVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.ICtrContractFeeService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "ctr/contractFee")
public class CtrContractFeeApi extends BaseApi<CtrContractFee> {
	@Autowired
	private ICtrContractFeeService ctrContractFeeService;
	
	@Override
	public IBaseService<CtrContractFee> getService() {
		return ctrContractFeeService;
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractFeeService.updateFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("findPageContractFee")
	public Page<CtrContractFee> findPageContractFee(@RequestBody ContractFeeSearchVo queryVo){
		return ctrContractFeeService.findPageContractFee(queryVo);
	}
	
	@PostMapping("findByContractIdAndFeeType")
	public 	List<CtrContractFee> findByContractIdAndFeeType(@RequestBody CtrContractFee fee) {
		return ctrContractFeeService.findByContractIdAndFeeType(fee.getContractId(),fee.getFeeType());
	}
		
}

