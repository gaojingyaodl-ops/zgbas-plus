package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyAgreementVirtual;
import com.spt.tools.jpa.service.IBaseService;

/**
 * 协议采购申请
 * @Author MoonLight
 * @Date 2024/8/19 15:40
 * @Version 1.0
 */
public interface IApplyAgreementVirtualService extends IBaseService<ApplyAgreementVirtual> {
    void updateFileId(Long id, String fileId);
}
