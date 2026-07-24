package com.spt.bas.purchase.wx.client.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 *  入金测试和支付的金额和账户信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-23 16:03
 */
@Builder
@Data
public class PriceAndAccountVo {
    /**
     * 入金金额
     */
    private BigDecimal price;
    /**
     * 账户名
     */
    private String accountName;
    /**
     * 账号
     */
    private String accountNumber;
    /**
     * 银行
     */
    private String bank;
    /**
     * 备注
     */
    private String remark;
}
