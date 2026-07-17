package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyAllowedOphis;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyAllowedOphis",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsCompanyAllowedOphisClient  extends BaseClient<BsCompanyAllowedOphis> {
}
