package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.sign.SignFileSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/sign/file",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface ISignFileClient extends BaseClient<SignFile> {
    @PostMapping(value = "/findPageSignFile")
    PageDown<SignFile> findPageSignFile(@RequestBody SignFileSearchVo searchVo);

    @PostMapping(value = "/generateSignature")
    SignFile generateSignature(@RequestBody Long signId) throws ApplicationException;

    @PostMapping(value = "/findByCfcaContractNo")
    SignFile findByCfcaContractNo(@RequestBody String cfcaContractNo);

    @PostMapping(value = "/refreshSignFile")
    SignFile refreshSignFile(@RequestBody String cfcaContractNo) throws ApplicationException;

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findByAllLimit")
     SignFile findByAllLimit();


}
