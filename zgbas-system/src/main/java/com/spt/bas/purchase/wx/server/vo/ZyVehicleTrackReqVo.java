package com.spt.bas.purchase.wx.server.vo;

import lombok.Data;

@Data
public class ZyVehicleTrackReqVo {

    private String code; // 则一订单号
    private String subCode; // 则一子单号
    private String customerOrderCode; // 客户订单号，订单唯一标识
    private String licenseNumber; //	String	是	沪DP2312	车牌号
    private Long startTime; //	Number	是	1643251533306	开始时间
    private Long endTime; //	Number	是	1643251533308	结束时间
}
