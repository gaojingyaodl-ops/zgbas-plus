package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.vo.ApplyReceiveAmountSumVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/receive", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyReceiveClient extends BaseClient<ApplyReceive> {
    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findPageSum")
    ApplyReceive findPageSum(@RequestBody PageSearchVo searchVo);

    @PostMapping("findPageDetail")
    PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);
    
    @PostMapping("findReceiveAmountSum")
    ApplyReceiveAmountSumVo findReceiveAmountSum(@RequestParam("contractId") Long contractId);
    
    @PostMapping("findReceiveAmountSumByContractNo")
    ApplyReceiveAmountSumVo findReceiveAmountSumByContractNo(@RequestParam("contractNo") String contractNo);

    @PostMapping("findListByContractIdAndStatus")
    List<ApplyReceive> findListByContractIdAndStatus(@RequestParam("contractId") Long contractId,@RequestParam("status") String status );
}

