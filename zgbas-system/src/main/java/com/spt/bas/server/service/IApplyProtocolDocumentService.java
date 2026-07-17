package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyProtocolDocument;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @Author MoonLight
 * @Date 2024/5/21 16:17
 * @Version 1.0
 */
public interface IApplyProtocolDocumentService extends IBaseService<ApplyProtocolDocument> {
    void updateFileId(Long id, String fileId);
}
