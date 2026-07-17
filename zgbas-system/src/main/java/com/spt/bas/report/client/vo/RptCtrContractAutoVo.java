package com.spt.bas.report.client.vo;

import com.spt.bas.client.entity.CtrContract;

/**
 * 自動發起付款返vo
 */
public class RptCtrContractAutoVo extends CtrContract {

  private   String  autoPayAmount;

    public String getAutoPayAmount() {
        return autoPayAmount;
    }

    public void setAutoPayAmount(String autoPayAmount) {
        this.autoPayAmount = autoPayAmount;
    }
}
