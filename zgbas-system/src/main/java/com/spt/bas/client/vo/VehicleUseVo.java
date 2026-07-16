package com.spt.bas.client.vo;


import com.spt.tools.core.bean.PageSearchVo;



public class VehicleUseVo extends PageSearchVo {
    private Long id;//编号
    private Long applyUserId;			//申请人ID
    private String plateNumber;			//车牌号
    private String status;			//车辆使用状态

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
