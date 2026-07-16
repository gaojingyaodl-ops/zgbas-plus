package com.spt.bas.client.vo;

import java.util.Date;

/**
 * @Author MoonLight
 * @Date 2023/8/10 11:30
 * @Version 1.0
 */
public class CtrLastDateVo {
    private Long contractId;

    private Date lastDate;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public CtrLastDateVo() {
    }

    public CtrLastDateVo(Long contractId, Date lastDate) {
        this.contractId = contractId;
        this.lastDate = lastDate;
    }
}
