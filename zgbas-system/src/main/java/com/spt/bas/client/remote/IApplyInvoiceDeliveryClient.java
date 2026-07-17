package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInvoiceDelivery;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @author shengong
 */
@FeignClient(qualifier="applyInvoiceDeliveryClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/invoiceDelivery",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyInvoiceDeliveryClient extends BaseClient<ApplyInvoiceDelivery> {
    @PostMapping(value = "updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);
}

