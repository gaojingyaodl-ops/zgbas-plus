package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInventoryVirtual;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 库存采购申请
 * @Author MoonLight
 * @Date 2024/8/20 11:42
 * @Version 1.0
 */
@FeignClient(qualifier = "applyInventoryVirtualClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/inventoryVirtual", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyInventoryVirtualClient extends BaseClient<ApplyInventoryVirtual> {
    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);
}
