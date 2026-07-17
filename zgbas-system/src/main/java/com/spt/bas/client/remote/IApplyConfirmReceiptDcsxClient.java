package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyConfirmReceiptDcsx;
import com.spt.bas.client.vo.ApplyConfirmReceiptDcsxVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/confirmReceiptDcsx",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IApplyConfirmReceiptDcsxClient extends BaseClient<ApplyConfirmReceiptDcsx> {

    @PostMapping("applyConfirmReceiptDcsx")
    void applyConfirmReceiptDcsx(@RequestBody ApplyConfirmReceiptDcsxVo confirmReceiptDcsxVo)throws WebApplicationException;

    @PostMapping("findByContractId")
    List<ApplyConfirmReceiptDcsx> findByContractId(@RequestBody Long contractId);

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

}
