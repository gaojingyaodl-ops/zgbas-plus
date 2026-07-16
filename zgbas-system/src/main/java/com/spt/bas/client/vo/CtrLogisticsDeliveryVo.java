package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrLogisticsDelivery;
import com.spt.bas.client.entity.CtrLogisticsDriver;

import java.util.List;

public class CtrLogisticsDeliveryVo extends CtrLogisticsDelivery {

    private static final long serialVersionUID = -2711326451726896254L;

    private List<CtrLogisticsDriver> ctrLogisticsDriverList;

    public List<CtrLogisticsDriver> getCtrLogisticsDriverList() {
        return ctrLogisticsDriverList;
    }

    public void setCtrLogisticsDriverList(List<CtrLogisticsDriver> ctrLogisticsDriverList) {
        this.ctrLogisticsDriverList = ctrLogisticsDriverList;
    }
}
