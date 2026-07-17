package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealUsageDCSX;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.SealUsageDcsxVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/seal/usageDCSX", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface ISealUsageDCSXClient  extends BaseClient<SealUsageDCSX> {
    @RequestMapping(value = "updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping(value = "addSealUsageUpdateHis")
    void addSealUsageUpdateHis(@RequestBody PmApproveSaveVo startVo);
    
    @PostMapping(value = "updateCfcaContractNo")
    void updateCfcaContractNo(@RequestBody SealUsageDcsxVo entity);
    
}
