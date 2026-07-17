package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInvalid;
import com.spt.bas.client.vo.ApplyInvalidApproveVo;
import com.spt.bas.client.vo.ApplyInvalidDetailVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 作废申请
 * @Author MoonLight
 * @Date 2023/9/11 14:37
 * @Version 1.0
 */
@FeignClient(qualifier = "applyInvalidClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/invalid", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyInvalidClient extends BaseClient<ApplyInvalid> {

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("queryInvalidDetail")
    ApplyInvalidDetailVo queryInvalidDetail(@RequestBody ApplyInvalid applyInvalid);

    @PostMapping("queryInvalidApproveList")
    List<ApplyInvalidApproveVo> queryInvalidApproveList(@RequestBody ApplyInvalid applyInvalid);
}
