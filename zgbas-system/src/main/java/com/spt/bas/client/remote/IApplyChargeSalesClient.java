package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.vo.BsBankVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/chargeSales",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IApplyChargeSalesClient extends BaseClient<ApplyMatch> {

    @PostMapping(value = "getSpecialBank")
    BsBankVo getSpecialBank(@RequestBody Long enterpriseId);
    
    @PostMapping(value = "parseCtrDcsxByApproveId")
    ApplyCtrDCSX parseCtrDcsxByApproveId(@RequestBody Long approveId);
}
