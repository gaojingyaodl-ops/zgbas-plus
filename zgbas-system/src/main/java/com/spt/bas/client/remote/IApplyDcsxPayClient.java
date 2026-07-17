package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(qualifier = "applyDcsxPayClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/Dcsxpay", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyDcsxPayClient extends BaseClient<ApplyPay> {

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findPageSum")
    public ApplyPay findPageSum(@RequestBody PageSearchVo searchVo);

    @PostMapping("findPageDetail")
    public PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);

    @PostMapping("findApplyPay")
    List<ApplyPay> findApplyPay(@RequestBody String contractNo);
}

