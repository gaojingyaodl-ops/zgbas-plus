package com.spt.bas.purchase.wx.server.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeliveryOutNoRequest {

    @NotBlank(message = "contractNo不能为空")
    private String contractNo;

//    @NotBlank(message = "deliveryOutNo不能为空")
//    private String deliveryOutNo;

    @NotBlank(message = "deliveryId不能为空")
    private String deliveryId;
    /**业务员手机号*/
    private String matchUserPhone;

    /**
     * 实际到货日期
     */
    private String confirmReceiptDate;

    //获取司机和车辆信息
    /**
     * 车号
     */
    private String plateNumber;

    /**
     * 司机名称
     */
    private String driverName;

    /**
     * 司机身份证
     */
    private String driverCardNo;
}
