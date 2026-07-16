package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * 发货预警查询 VO
 */
public class UnDeliverySearchVo extends PageSearchVo {
    
    private String payCondition;	//收付款条件
    private String warehouseInCondition;//出入库条件
    private String warehouseOutCondition;//出入库条件

    public String getPayCondition() {
        return payCondition;
    }

    public void setPayCondition(String payCondition) {
        this.payCondition = payCondition;
    }

    public String getWarehouseInCondition() {
        return warehouseInCondition;
    }

    public void setWarehouseInCondition(String warehouseInCondition) {
        this.warehouseInCondition = warehouseInCondition;
    }

    public String getWarehouseOutCondition() {
        return warehouseOutCondition;
    }

    public void setWarehouseOutCondition(String warehouseOutCondition) {
        this.warehouseOutCondition = warehouseOutCondition;
    }
}
