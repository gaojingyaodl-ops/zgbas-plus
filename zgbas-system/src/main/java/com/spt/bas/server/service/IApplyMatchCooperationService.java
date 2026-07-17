package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.tools.jpa.service.IBaseService;

/**
 * 代采赊销(合作)
 * @Author MoonLight
 * @Date 2023/8/1 14:42
 * @Version 1.0
 */
public interface IApplyMatchCooperationService extends IBaseService<ApplyMatch> {

    void updateFileId(Long id, String fileId);

    void updateLiabilityFileId(Long id, String fileId);
}
