package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyInventoryVirtual;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyInventoryVirtualService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存采购申请
 * @Author MoonLight
 * @Date 2024/8/20 11:34
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "apply/inventoryVirtual")
public class ApplyInventoryVirtualApi extends BaseApi<ApplyInventoryVirtual> {
    @Autowired
    private IApplyInventoryVirtualService applyInventoryVirtualService;

    @Override
    public IBaseService<ApplyInventoryVirtual> getService() {
        return applyInventoryVirtualService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo){
        applyInventoryVirtualService.updateFileId(vo.getId(), vo.getFileId());
    }
}
