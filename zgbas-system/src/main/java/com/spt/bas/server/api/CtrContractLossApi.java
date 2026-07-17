package com.spt.bas.server.api;


import com.spt.bas.client.vo.CtrContractLossVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrContractLoss;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.ICtrContractLossService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "ctr/contractLoss")
public class CtrContractLossApi extends BaseApi<CtrContractLoss> {
	@Autowired
	private ICtrContractLossService ctrContractLossService;
	@Override
	public IBaseService<CtrContractLoss> getService() {
		return ctrContractLossService;
	}
	
	@PostMapping("updateLossFileId")
	public void updateLossFileId(@RequestBody FileIdUpdateVo vo) {
		ctrContractLossService.updateFileId(vo.getId(), vo.getFileId());
	}
	@PostMapping("updateEnableFlg")
	public void updateEnableFlg(@RequestBody CtrContractLossVo vo) {
		ctrContractLossService.updateEnableFlg(vo.getId(), vo.getEnableFlg(),vo.getContractId());
	}
	
	@PostMapping("updateContractLoss")
	public boolean updateContractLoss(@RequestBody CtrContractLoss vo) {
		int updateContractLoss = ctrContractLossService.updateContractLoss(vo);
		return updateContractLoss > 0;
	}
	@PostMapping("findByContractId")
	public CtrContractLoss findByContractId(@RequestBody Long ContractId){
		
		return ctrContractLossService.findByContractId(ContractId);
	}
}

