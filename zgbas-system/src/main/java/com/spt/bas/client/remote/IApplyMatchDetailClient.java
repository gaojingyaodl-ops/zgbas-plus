package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.vo.ApplyMatchQueryVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/matchDetail",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyMatchDetailClient extends BaseClient<ApplyMatchDetail> {

	@PostMapping("findByApplyMatchId")
	List<ApplyMatchDetail> findByApplyMatchId(@RequestBody ApplyMatchQueryVo vo);

	@PostMapping("findByApplyQueryVo")
	List<ApplyMatchDetail> findByApplyQueryVo(@RequestBody ApplyMatchQueryVo vo);

	@PostMapping("findByApproveId")
	List<ApplyMatchDetail> findByApproveId(@RequestBody Long approveId);

	@PostMapping("findByContractNo")
	ApplyMatchDetail findByContractNo(@RequestBody String contractNo);

}

