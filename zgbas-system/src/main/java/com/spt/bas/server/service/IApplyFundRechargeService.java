package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyFundRecharge;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @Author MoonLight
 * @Date 2024/7/12 17:48
 * @Version 1.0
 */
public interface IApplyFundRechargeService extends IBaseService<ApplyFundRecharge> {

    void updateFileId(Long id, String fileId);
}
