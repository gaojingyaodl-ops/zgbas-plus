package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySell;
import com.spt.bas.client.vo.ApplySellVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(qualifier = "applySellClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/sell", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplySellClient extends BaseClient<ApplySell> {

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findByContractId")
    public ApplySell findByContractId(@RequestBody Long contractId);

    @PostMapping("printApplySell")
    public ApproveFormPrintVo printApplySell(@RequestBody Long appluId);


    @PostMapping(value = "applySell")
    public void applySell(@RequestBody ApplySellVo applySellVo)throws WebApplicationException;
}

