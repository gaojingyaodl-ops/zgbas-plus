package com.spt.pm.service;

import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApprovePush;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @Author: gaojy
 * @create 2022/4/26 11:16
 * @version: 1.0
 * @description:
 */
public interface IPmApprovePushService extends IBaseService<PmApprovePush> {

    void addSysPush(PmApprove approve);
}
