package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyPromoteVip;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyPromoteVipService extends IBaseService<ApplyPromoteVip> {

    /**
     * 自动发起收款审批任务
     * @throws ApplicationException
     */
    void doApplyVipReceiveTask(ApplyPromoteVip applyPromoteVip,String p,Long c) throws ApplicationException;
}
