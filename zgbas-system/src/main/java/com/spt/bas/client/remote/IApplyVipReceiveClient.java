package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.ApplyVip;
import com.spt.bas.client.entity.ApplyVipReceive;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/vipReceive", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyVipReceiveClient extends BaseClient<ApplyVipReceive> {

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findPageSum")
    public ApplyReceive findPageSum(@RequestBody PageSearchVo searchVo);

    @PostMapping("findPageDetail")
    public PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);

}

