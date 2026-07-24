package com.spt.bas.purchase.wx.server.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ContractNoRequest {

    @NotBlank(message = "contractNo不能为空")
    private String contractNo;
    /**业务员手机号*/
    private String matchUserPhone;
    /**子单号*/
    private String  waybillCode;
    private String operatorPhone;
}
