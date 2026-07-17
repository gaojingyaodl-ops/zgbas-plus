package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCalculate;
import com.spt.bas.client.vo.ApplyCalculateDetailVo;
import com.spt.bas.client.vo.ApplyCalculateFlowVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/calculate",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyCalculateClient extends BaseClient<ApplyCalculate> {

	@PostMapping("saveDetail")
	public void saveDetail(@RequestBody List<ApplyCalculateDetailVo> detailList);
	
	@PostMapping("doCalculate")
	public void doCalculate(@RequestBody ApplyCalculateFlowVo flowVo);
	
	@PostMapping("findByImportId")
	public ApplyCalculate findByImportId(@RequestBody ApplyCalculate calculate);
}

