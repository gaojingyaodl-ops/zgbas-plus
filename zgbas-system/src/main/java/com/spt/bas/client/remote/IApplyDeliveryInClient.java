package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyDeliveryIn;
import com.spt.bas.client.vo.ApplyDeliveryInVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(qualifier = "applyDeliveryInClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/deliveryIn", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyDeliveryInClient extends BaseClient<ApplyDeliveryIn> {

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findDeliveryInContractId")
    List<ApplyDeliveryIn> findDeliveryInContractId(@RequestBody Long contractId);

    @PostMapping("findPageDetail")
    PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);

    @PostMapping("generateApplyNo")
    ApplyDeliveryIn generateApplyNo(@RequestBody Long contractId);
}

