package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SignFileUser;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/signUser/file",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface ISignFileUserClient extends BaseClient<SignFileUser> {

    @PostMapping(value = "findSignFileUserBySignId")
    List<SignFileUser> findSignFileUserBySignId(@RequestBody Long SignId);

    @PostMapping(value = "saveDatas")
    void saveDatas(@RequestBody BatchSaveVo<SignFileUser> batchSaveVo, @RequestParam("signFileId") Long signFileId);
}
