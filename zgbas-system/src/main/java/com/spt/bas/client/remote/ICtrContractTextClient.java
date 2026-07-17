package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.CtrContractText;
import com.spt.bas.client.vo.DcContractText;
import com.spt.bas.client.vo.ExtraBankTextVo;
import com.spt.bas.client.vo.MatchContractTextVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/contractText",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractTextClient extends BaseClient<CtrContractText> {
	
	@PostMapping("findContractText")
	CtrContractText findContractText(@RequestBody CtrContractText text);

	@PostMapping("synthesisMathContractText")
	String synthesisMathContractText(@RequestBody MatchContractTextVo textVo);

	@PostMapping("dealWithExtraBank")
	DcContractText dealWithExtraBank(@RequestBody ExtraBankTextVo extraBankTextVo);
}

