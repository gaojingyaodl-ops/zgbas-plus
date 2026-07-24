package com.spt.bas.purchase.wx.server.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-12-04 15:24
 */
@Data
public class PayBankInfoRequest {
    /**
     * 0表示销售合同，1表示服务合同
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 销售合同号
     */
    @NotBlank(message = "合同号不能为空")
    private String contractNo;
}
