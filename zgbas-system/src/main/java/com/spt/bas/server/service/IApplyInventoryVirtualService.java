package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInventoryVirtual;
import com.spt.tools.jpa.service.IBaseService;

/**
 * 库存采购申请
 * @Author MoonLight
 * @Date 2024/8/20 11:05
 * @Version 1.0
 */
public interface IApplyInventoryVirtualService extends IBaseService<ApplyInventoryVirtual> {
    void updateFileId(Long id, String fileId);
}
