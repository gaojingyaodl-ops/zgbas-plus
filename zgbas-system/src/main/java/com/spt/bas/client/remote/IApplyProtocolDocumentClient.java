package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyProtocolDocument;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author MoonLight
 * @Date 2024/5/21 16:22
 * @Version 1.0
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/protocolDocument", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyProtocolDocumentClient extends BaseClient<ApplyProtocolDocument> {

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);
}
