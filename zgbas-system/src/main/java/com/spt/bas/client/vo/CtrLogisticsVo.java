package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrLogistics;

import java.util.List;

public class CtrLogisticsVo extends CtrLogistics {

    private static final long serialVersionUID = 2415842401950801971L;

    private List<CtrLogisticsDeliveryVo> logisticsDeliveryList;

    public List<CtrLogisticsDeliveryVo> getLogisticsDeliveryList() {
        return logisticsDeliveryList;
    }

    public void setLogisticsDeliveryList(List<CtrLogisticsDeliveryVo> logisticsDeliveryList) {
        this.logisticsDeliveryList = logisticsDeliveryList;
    }
}
