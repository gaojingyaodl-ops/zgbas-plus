package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyContacts;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyCredit",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsCompanyCreditClient extends BaseClient<BsCompanyCredit> {
    @RequestMapping("/findByCompanyIdAndType")
    public BsCompanyCredit findByCompanyIdAndType(@RequestParam("companyId") Long companyId,@RequestParam("creditType") String creditType);

    @RequestMapping("/findByCompanyId")
    public List<BsCompanyCredit> findByCompanyId(@RequestParam("companyId") Long companyId);
}
