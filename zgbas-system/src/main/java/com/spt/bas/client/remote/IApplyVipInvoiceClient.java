package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyInvoice;
import com.spt.bas.client.entity.ApplyVipInvoice;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(qualifier="applyVipInvoiceClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/vipInvoice",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyVipInvoiceClient extends BaseClient<ApplyVipInvoice> {

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findPageDetail")
    public PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);
}

