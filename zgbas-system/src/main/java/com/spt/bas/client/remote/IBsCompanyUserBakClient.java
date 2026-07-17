package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyUserBak;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyUserBak",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsCompanyUserBakClient extends BaseClient<BsCompanyUserBak> {

     @PostMapping("/getBsCompanyUser")
     BsCompanyUserBak getBsCompanyUserBak(@RequestParam("companyId") Long companyId, @RequestParam("matchUserId") Long matchUserId);

     @PostMapping("/getCompanyUserForDate")
     List<BsCompany> getCompanyForDate(@RequestBody Long matchUserId);

     @PostMapping("/findByCompanyId")
     BsCompanyUserBak findByCompanyId(@RequestParam("companyId") Long companyId);


}
