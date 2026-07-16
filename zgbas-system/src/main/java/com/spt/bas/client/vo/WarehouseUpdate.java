package com.spt.bas.client.vo;


/**
 * 修改仓库
 */
public class WarehouseUpdate {

    private Long id;

    private  String warehouseFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseFlag() {
        return warehouseFlag;
    }

    public void setWarehouseFlag(String warehouseFlag) {
        this.warehouseFlag = warehouseFlag;
    }
}
