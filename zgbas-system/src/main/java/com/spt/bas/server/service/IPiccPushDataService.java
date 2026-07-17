package com.spt.bas.server.service;

import com.spt.bas.client.entity.PiccPushData;
import com.spt.tools.jpa.service.IBaseService;

public interface IPiccPushDataService extends IBaseService<PiccPushData> {

    /**
     * 赊销记录
     * @param contractNo
     * @return
     */
    PiccPushData findContractNoSx(String contractNo);

    /**
     * 回款记录
     * @param contractNo
     * @return
     */
    PiccPushData findContractNoRecovery(String contractNo);
}
