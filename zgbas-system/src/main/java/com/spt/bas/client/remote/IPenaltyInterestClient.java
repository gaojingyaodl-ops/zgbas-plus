package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyVip;
import com.spt.bas.client.entity.PenaltyInterest;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/penaltyInterest",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IPenaltyInterestClient extends BaseClient<PenaltyInterest> {
    @RequestMapping(value = "updateInterStatus")
    void updateInterStatus(@RequestBody PenaltyInterest penaltyInterest);


    @RequestMapping(value = "findContractNoByCompanyId")
    List<String> findContractNoByCompanyId(@RequestBody String companyId);
}
