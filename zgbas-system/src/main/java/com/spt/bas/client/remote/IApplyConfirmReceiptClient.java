package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyConfirmReceipt;
import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/confirmReceipt",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IApplyConfirmReceiptClient extends BaseClient<ApplyConfirmReceipt> {

    @PostMapping("applyConfirmReceipt")
    void applyConfirmReceipt(@RequestBody ApplyConfirmReceiptVo confirmReceiptVo)throws WebApplicationException;

    @PostMapping("findByContractId")
     List<ApplyConfirmReceipt> findByContractId(@RequestBody Long contractId);

    @PostMapping
    ApplyConfirmReceipt findByContractNo();

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);
}
