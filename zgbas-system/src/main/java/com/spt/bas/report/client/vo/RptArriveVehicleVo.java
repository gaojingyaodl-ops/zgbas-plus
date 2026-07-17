package com.spt.bas.report.client.vo;

/**
 * 则一到车信息回调
 */
public class RptArriveVehicleVo {
    private String code; //String	是	220101000001	则一订单号
    private String waybillCode; //	String	是	220101000001	则一运单号
    private String customerOrderCode; //String	是    	220101000001	客户订单号，订单唯一标识
    private Long arriveTime; //Number	是	1643251533306	到车时间戳

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

}
