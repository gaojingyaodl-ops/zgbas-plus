package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SignFile;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"sign/file",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface ISignFileApiClient extends BaseClient<SignFile> {

    @PostMapping("findByCfcaContractNo")
      SignFile findByCfcaContractNo(String cfcaContractNo);
}
