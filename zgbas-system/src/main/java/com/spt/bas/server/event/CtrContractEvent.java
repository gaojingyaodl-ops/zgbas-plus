package com.spt.bas.server.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/2 13:50
 */

public class CtrContractEvent extends ApplicationEvent {

    /**
     * 合同id
     */
    private Long ctrContractId;


    public CtrContractEvent(Long ctrContractId) {
        super(ctrContractId);
        this.ctrContractId = ctrContractId;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }
}
