package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyInvoiceReceived;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(qualifier = "applyInvoiceReceivedClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/invoiceReceived", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyInvoiceReceivedClient extends BaseClient<ApplyInvoiceReceived> {
    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findPageDetail")
    public PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);
}

