package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyBuy;
import com.spt.bas.client.vo.ApplyBuyVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(qualifier = "applyBuyClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/buy", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyBuyClient extends BaseClient<ApplyBuy> {

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findByContractId")
    public ApplyBuy findByContractId(@RequestBody Long contractId);

    @PostMapping("printApplyBuy")
    public ApproveFormPrintVo printApplyBuy(@RequestBody Long applyId);

    @PostMapping(value = "/applyBuy")
    public void applyBuy(@RequestBody ApplyBuyVo applyBuyVo) throws WebApplicationException;

}

