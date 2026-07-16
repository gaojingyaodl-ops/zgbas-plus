package com.spt.bas.client.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class ZyCallBackArriveVehicleVo {
    private String code; //String	是	220101000001	则一订单号
    private String waybillCode; //	String	是	220101000001	则一运单号
    private String customerOrderCode; //String	是    	220101000001	客户订单号，订单唯一标识
    private Long arriveTime; //Number	是	1643251533306	到车时间戳
    private Long operateTime; //Number	是	1643251533306	操作时间戳
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date arriveTimeStr;//则一送达日期

    private BigDecimal cost;//

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }

    public Long getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Long arriveTime) {
        this.arriveTime = arriveTime;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Date getArriveTimeStr() {
        return arriveTimeStr;
    }

    public void setArriveTimeStr(Date arriveTimeStr) {
        this.arriveTimeStr = arriveTimeStr;
    }
}
