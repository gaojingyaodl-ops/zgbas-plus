package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractFee;
import com.spt.bas.client.vo.ContractFeeSearchVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/contractFee",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractFeeClient extends BaseClient<CtrContractFee> {

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);
	
	@PostMapping("findPageContractFee")
	public PageDown<CtrContractFee> findPageContractFee(@RequestBody ContractFeeSearchVo queryVo);

	@PostMapping("findByContractIdAndFeeType")
	public 	List<CtrContractFee> findByContractIdAndFeeType(@RequestBody CtrContractFee fee);
	
}

