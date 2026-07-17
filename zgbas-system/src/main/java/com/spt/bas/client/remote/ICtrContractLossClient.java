package com.spt.bas.client.remote;

import com.spt.bas.client.vo.CtrContractLossVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractLoss;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/contractLoss",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractLossClient extends BaseClient<CtrContractLoss> {
	
	
	@PostMapping("updateLossFileId")
	public void updateLossFileId(FileIdUpdateVo vo);
	
	@PostMapping("updateEnableFlg")
	public void updateEnableFlg(@RequestBody CtrContractLossVo vo);

	@PostMapping("updateContractLoss")
	public boolean updateContractLoss(CtrContractLoss ctrContractLoss);

	@PostMapping("findByContractId")
	public CtrContractLoss findByContractId(Long id);
	
}

